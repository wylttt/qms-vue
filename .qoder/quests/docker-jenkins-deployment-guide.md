# 濂溪区胃癌筛查系统 - Docker + Jenkins 容器化部署方案

**部署方式**: Docker容器化 + Jenkins自动化部署  
**目标环境**: 测试环境  
**编写时间**: 2025-12-16  

---

## 📋 目录

1. [部署架构](#一部署架构)
2. [前置准备](#二前置准备)
3. [Java服务Docker化](#三java服务docker化)
4. [前端静态资源部署](#四前端静态资源部署)
5. [Jenkins自动化部署](#五jenkins自动化部署)
6. [环境配置](#六环境配置)
7. [部署验证](#七部署验证)
8. [常见问题](#八常见问题)

---

## 一、部署架构

### 1.1 整体架构图

```
┌────────────────────────────────────────────────────────┐
│                     测试服务器                          │
│  ┌──────────────────────────────────────────────────┐ │
│  │                  Docker Network                   │ │
│  │                                                    │ │
│  │  ┌─────────────┐  ┌─────────────┐  ┌──────────┐ │ │
│  │  │   MySQL     │  │   Redis     │  │  Nginx   │ │ │
│  │  │  (已有)     │  │  (已有)     │  │  (已有)  │ │ │
│  │  └──────┬──────┘  └──────┬──────┘  └─────┬────┘ │ │
│  │         │                 │                │      │ │
│  │  ┌──────▼─────────────────▼────────────────▼───┐ │ │
│  │  │      BearJia Backend Container             │ │ │
│  │  │      (Spring Boot Application)             │ │ │
│  │  │      端口: 8080                             │ │ │
│  │  └────────────────────────────────────────────┘ │ │
│  │                                                    │ │
│  └──────────────────────────────────────────────────┘ │
│                                                         │
│  ┌──────────────────────────────────────────────────┐ │
│  │   Nginx静态资源目录                               │ │
│  │   /data/nginx/html/bearjia-admin                 │ │
│  │   (Vue3前端构建产物)                              │ │
│  └──────────────────────────────────────────────────┘ │
│                                                         │
│  ┌──────────────────────────────────────────────────┐ │
│  │                  Jenkins                          │ │
│  │   - 拉取代码 (Git)                                │ │
│  │   - 构建镜像 (Maven + Docker)                     │ │
│  │   - 部署容器 (Docker Compose)                     │ │
│  └──────────────────────────────────────────────────┘ │
└────────────────────────────────────────────────────────┘

外部访问:
  → http://test-server:80 (前端页面)
  → http://test-server:80/api (后端API，Nginx反向代理)
```

### 1.2 容器规划

| 容器名称 | 基础镜像 | 端口映射 | 用途 | 状态 |
|---------|---------|---------|------|------|
| mysql | mysql:8.0 | 3306:3306 | 数据库 | 已有 |
| redis | redis:7.0 | 6379:6379 | 缓存 | 已有 |
| nginx | nginx:1.24 | 80:80, 443:443 | Web服务器 | 已有 |
| bearjia-backend | openjdk:17-slim | 8080:8080 | Java应用 | **新建** |

### 1.3 网络配置

```bash
# 确保所有容器在同一网络中
docker network create bearjia-network

# 将现有容器连接到网络
docker network connect bearjia-network mysql
docker network connect bearjia-network redis
docker network connect bearjia-network nginx
```

---

## 二、前置准备

### 2.1 检查现有Docker环境

```bash
# 检查Docker版本
docker --version
# 预期: Docker version 20.10.x 或更高

# 检查Docker Compose版本
docker-compose --version
# 预期: Docker Compose version v2.x.x

# 检查现有容器
docker ps -a

# 检查现有网络
docker network ls
```

### 2.2 检查现有MySQL容器

```bash
# 查看MySQL容器信息
docker inspect mysql | grep -A 10 "NetworkSettings"

# 测试MySQL连接
docker exec -it mysql mysql -uroot -p -e "SELECT VERSION();"

# 查看MySQL配置
docker exec -it mysql cat /etc/mysql/my.cnf
```

### 2.3 检查现有Nginx容器

```bash
# 查看Nginx容器信息
docker inspect nginx | grep -A 10 "Mounts"

# 查看Nginx配置目录
docker exec -it nginx ls -la /etc/nginx/conf.d/

# 查看静态资源目录（通常是/usr/share/nginx/html或挂载目录）
docker exec -it nginx ls -la /usr/share/nginx/html/
```

### 2.4 创建必要目录

```bash
# 在宿主机创建目录
sudo mkdir -p /data/bearjia/backend
sudo mkdir -p /data/bearjia/logs
sudo mkdir -p /data/nginx/html/bearjia-admin
sudo mkdir -p /data/nginx/conf.d

# 设置权限
sudo chmod -R 755 /data/bearjia
sudo chmod -R 755 /data/nginx
```

---

## 三、Java服务Docker化

### 3.1 创建Dockerfile

在项目根目录创建 `bearjia-admin-backend/Dockerfile`:

```dockerfile
# 多阶段构建 - 构建阶段
FROM maven:3.8.6-openjdk-17-slim AS builder

# 设置工作目录
WORKDIR /build

# 复制pom.xml和源代码
COPY pom.xml .
COPY src ./src

# 下载依赖（利用Docker缓存）
RUN mvn dependency:go-offline -B

# 打包应用（跳过测试以加快构建速度）
RUN mvn clean package -DskipTests -B

# 运行阶段 - 使用更小的基础镜像
FROM openjdk:17-slim

# 设置时区为中国
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 创建应用目录
WORKDIR /app

# 从构建阶段复制JAR文件
COPY --from=builder /build/target/*.jar app.jar

# 创建日志目录
RUN mkdir -p /app/logs

# 暴露应用端口
EXPOSE 8080

# 设置JVM参数
ENV JAVA_OPTS="-Xms512m -Xmx1g -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/app/logs/heapdump.hprof"

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/api/actuator/health || exit 1

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app/app.jar"]
```

### 3.2 创建应用配置文件

在 `bearjia-admin-backend/src/main/resources/` 创建 `application-docker.yml`:

```yaml
# Docker环境配置
server:
  port: 8080
  servlet:
    context-path: /api

spring:
  application:
    name: bearjia-admin-backend
  
  # 数据源配置（连接Docker MySQL容器）
  datasource:
    url: jdbc:mysql://mysql:3306/bear_jia?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
    username: ${DB_USERNAME:bearjia}
    password: ${DB_PASSWORD:bearjia123}
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      minimum-idle: 5
      maximum-pool-size: 20
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
  
  # Redis配置（连接Docker Redis容器）
  redis:
    host: redis
    port: 6379
    password: ${REDIS_PASSWORD:}
    database: 0
    timeout: 3000ms
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0
        max-wait: -1ms
  
  # JPA配置
  jpa:
    hibernate:
      ddl-auto: none
    show-sql: false
    properties:
      hibernate:
        format_sql: false
  
  # 文件上传配置
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 50MB

# 日志配置
logging:
  level:
    root: INFO
    com.javaxiaobear: INFO
  file:
    name: /app/logs/application.log
    max-size: 100MB
    max-history: 30
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{50} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{50} - %msg%n"

# 应用配置
app:
  upload-path: /app/uploads
  ocr:
    enabled: false

# Actuator配置（用于健康检查）
management:
  endpoints:
    web:
      exposure:
        include: health,info
  endpoint:
    health:
      show-details: always
```

### 3.3 创建Docker Compose文件

在项目根目录创建 `docker-compose.yml`:

```yaml
version: '3.8'

services:
  bearjia-backend:
    container_name: bearjia-backend
    build:
      context: ./bearjia-admin-backend
      dockerfile: Dockerfile
    image: bearjia-backend:latest
    restart: unless-stopped
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - DB_USERNAME=bearjia
      - DB_PASSWORD=bearjia123
      - REDIS_PASSWORD=
      - TZ=Asia/Shanghai
    volumes:
      - /data/bearjia/logs:/app/logs
      - /data/bearjia/uploads:/app/uploads
    networks:
      - bearjia-network
    depends_on:
      - mysql
      - redis
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/api/actuator/health"]
      interval: 30s
      timeout: 3s
      retries: 3
      start_period: 60s

networks:
  bearjia-network:
    external: true

```

### 3.4 创建 .dockerignore

在 `bearjia-admin-backend/` 创建 `.dockerignore`:

```
target/
logs/
.mvn/
mvnw
mvnw.cmd
.git/
.gitignore
*.md
```

### 3.5 手动构建和运行（测试）

```bash
# 进入后端项目目录
cd /data/workspace/qms-vue

# 创建Docker网络（如果不存在）
docker network create bearjia-network || true

# 连接现有容器到网络
docker network connect bearjia-network mysql || true
docker network connect bearjia-network redis || true
docker network connect bearjia-network nginx || true

# 构建镜像
docker-compose build bearjia-backend

# 启动容器
docker-compose up -d bearjia-backend

# 查看日志
docker-compose logs -f bearjia-backend

# 查看容器状态
docker-compose ps

# 测试健康检查
curl http://localhost:8080/api/actuator/health
```

---

## 四、前端静态资源部署

### 4.1 前端构建脚本

创建 `bear-jia-vue3/build-docker.sh`:

```bash
#!/bin/bash
# 前端Docker环境构建脚本

set -e

echo "========================================="
echo "开始构建前端应用..."
echo "========================================="

# 设置环境变量
export NODE_ENV=production

# 清理旧的构建产物
rm -rf dist/

# 安装依赖（如果需要）
if [ ! -d "node_modules" ]; then
  echo "安装依赖..."
  npm install
fi

# 构建生产版本
echo "执行构建..."
npm run build:prod

# 检查构建是否成功
if [ ! -d "dist" ]; then
  echo "❌ 构建失败: dist目录不存在"
  exit 1
fi

echo "========================================="
echo "✅ 构建完成！"
echo "========================================="

# 复制到Nginx静态资源目录
echo "复制文件到Nginx目录..."
sudo rm -rf /data/nginx/html/bearjia-admin/*
sudo cp -r dist/* /data/nginx/html/bearjia-admin/

# 设置权限
sudo chmod -R 755 /data/nginx/html/bearjia-admin

echo "========================================="
echo "✅ 部署完成！"
echo "静态资源位置: /data/nginx/html/bearjia-admin"
echo "========================================="
```

设置执行权限:
```bash
chmod +x bear-jia-vue3/build-docker.sh
```

### 4.2 Nginx配置

创建 `nginx/bearjia.conf` 并复制到Nginx容器:

```nginx
# 濂溪区胃癌筛查系统 - Nginx配置

server {
    listen 80;
    server_name _;  # 测试环境可使用通配符
    
    # 访问日志
    access_log /var/log/nginx/bearjia_access.log;
    error_log /var/log/nginx/bearjia_error.log;
    
    # 根目录重定向到管理后台
    location = / {
        return 301 /admin;
    }
    
    # 前端管理后台
    location /admin {
        alias /usr/share/nginx/html/bearjia-admin;
        index index.html;
        try_files $uri $uri/ /admin/index.html;
        
        # 禁用缓存（测试环境）
        add_header Cache-Control "no-cache, no-store, must-revalidate";
        add_header Pragma "no-cache";
        add_header Expires "0";
    }
    
    # 后端API代理
    location /api/ {
        proxy_pass http://bearjia-backend:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # 超时设置
        proxy_connect_timeout 30s;
        proxy_send_timeout 300s;
        proxy_read_timeout 300s;
        
        # 文件上传大小限制
        client_max_body_size 50M;
        
        # WebSocket支持（如需要）
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }
    
    # gzip压缩
    gzip on;
    gzip_vary on;
    gzip_proxied any;
    gzip_comp_level 6;
    gzip_types text/plain text/css text/xml text/javascript application/json application/javascript application/xml+rss application/rss+xml font/truetype font/opentype application/vnd.ms-fontobject image/svg+xml;
    gzip_disable "MSIE [1-6]\.";
}
```

### 4.3 部署Nginx配置

```bash
# 方式1: 将配置复制到Nginx容器内
docker cp nginx/bearjia.conf nginx:/etc/nginx/conf.d/

# 方式2: 如果Nginx配置目录已挂载到宿主机
sudo cp nginx/bearjia.conf /data/nginx/conf.d/

# 重新加载Nginx配置
docker exec nginx nginx -t  # 测试配置
docker exec nginx nginx -s reload  # 重载配置
```

### 4.4 验证静态资源部署

```bash
# 检查静态文件是否存在
ls -la /data/nginx/html/bearjia-admin/

# 检查Nginx配置
docker exec nginx nginx -t

# 访问测试
curl -I http://localhost/admin
# 预期: HTTP/1.1 200 OK
```

---

## 五、Jenkins自动化部署

### 5.1 Jenkins Pipeline脚本

创建 `Jenkinsfile` 在项目根目录:

```groovy
pipeline {
    agent any
    
    environment {
        // Docker镜像信息
        BACKEND_IMAGE = 'bearjia-backend'
        BACKEND_TAG = "${BUILD_NUMBER}"
        
        // Docker网络
        DOCKER_NETWORK = 'bearjia-network'
        
        // 部署目录
        DEPLOY_DIR = '/data/bearjia'
        NGINX_HTML_DIR = '/data/nginx/html/bearjia-admin'
        
        // Git仓库信息
        GIT_REPO = 'http://your-git-server/bearjia/qms-vue.git'
        GIT_BRANCH = 'master'
        GIT_CREDENTIALS = 'git-credentials-id'
    }
    
    options {
        // 保留最近10次构建
        buildDiscarder(logRotator(numToKeepStr: '10'))
        // 禁止并发构建
        disableConcurrentBuilds()
        // 超时时间30分钟
        timeout(time: 30, unit: 'MINUTES')
    }
    
    stages {
        stage('拉取代码') {
            steps {
                echo '========== 拉取代码 =========='
                git branch: "${GIT_BRANCH}",
                    credentialsId: "${GIT_CREDENTIALS}",
                    url: "${GIT_REPO}"
                
                sh 'git log -1'
            }
        }
        
        stage('构建后端') {
            steps {
                echo '========== 构建后端Docker镜像 =========='
                dir('bearjia-admin-backend') {
                    script {
                        // 构建Docker镜像
                        sh """
                            docker build -t ${BACKEND_IMAGE}:${BACKEND_TAG} .
                            docker tag ${BACKEND_IMAGE}:${BACKEND_TAG} ${BACKEND_IMAGE}:latest
                        """
                    }
                }
            }
        }
        
        stage('构建前端') {
            steps {
                echo '========== 构建前端静态资源 =========='
                dir('bear-jia-vue3') {
                    sh '''
                        # 安装依赖
                        npm install --registry=https://registry.npmmirror.com
                        
                        # 构建生产版本
                        npm run build:prod
                        
                        # 验证构建产物
                        ls -la dist/
                    '''
                }
            }
        }
        
        stage('停止旧容器') {
            steps {
                echo '========== 停止旧容器 =========='
                script {
                    sh """
                        # 停止并删除旧容器（如果存在）
                        docker stop bearjia-backend || true
                        docker rm bearjia-backend || true
                    """
                }
            }
        }
        
        stage('部署后端') {
            steps {
                echo '========== 部署后端容器 =========='
                script {
                    sh """
                        # 创建网络（如果不存在）
                        docker network create ${DOCKER_NETWORK} || true
                        
                        # 连接现有容器到网络
                        docker network connect ${DOCKER_NETWORK} mysql || true
                        docker network connect ${DOCKER_NETWORK} redis || true
                        docker network connect ${DOCKER_NETWORK} nginx || true
                        
                        # 启动新容器
                        docker run -d \
                          --name bearjia-backend \
                          --network ${DOCKER_NETWORK} \
                          --restart unless-stopped \
                          -p 8080:8080 \
                          -e SPRING_PROFILES_ACTIVE=docker \
                          -e DB_USERNAME=bearjia \
                          -e DB_PASSWORD=bearjia123 \
                          -e REDIS_PASSWORD= \
                          -e TZ=Asia/Shanghai \
                          -v ${DEPLOY_DIR}/logs:/app/logs \
                          -v ${DEPLOY_DIR}/uploads:/app/uploads \
                          ${BACKEND_IMAGE}:${BACKEND_TAG}
                        
                        # 等待容器启动
                        sleep 10
                        
                        # 检查容器状态
                        docker ps | grep bearjia-backend
                    """
                }
            }
        }
        
        stage('部署前端') {
            steps {
                echo '========== 部署前端静态资源 =========='
                dir('bear-jia-vue3') {
                    sh """
                        # 备份旧版本
                        if [ -d ${NGINX_HTML_DIR} ]; then
                            sudo mv ${NGINX_HTML_DIR} ${NGINX_HTML_DIR}.bak.\$(date +%Y%m%d_%H%M%S)
                        fi
                        
                        # 创建目录
                        sudo mkdir -p ${NGINX_HTML_DIR}
                        
                        # 复制新版本
                        sudo cp -r dist/* ${NGINX_HTML_DIR}/
                        
                        # 设置权限
                        sudo chmod -R 755 ${NGINX_HTML_DIR}
                        
                        # 重载Nginx
                        docker exec nginx nginx -s reload
                    """
                }
            }
        }
        
        stage('健康检查') {
            steps {
                echo '========== 健康检查 =========='
                script {
                    retry(5) {
                        sleep 5
                        sh """
                            # 检查后端健康状态
                            curl -f http://localhost:8080/api/actuator/health
                            
                            # 检查前端页面
                            curl -I http://localhost/admin | grep "200 OK"
                        """
                    }
                }
            }
        }
        
        stage('清理旧镜像') {
            steps {
                echo '========== 清理旧镜像 =========='
                sh """
                    # 删除未使用的镜像（保留最新的3个版本）
                    docker images ${BACKEND_IMAGE} --format "{{.Tag}}" | tail -n +4 | xargs -r -I {} docker rmi ${BACKEND_IMAGE}:{}
                    
                    # 清理悬空镜像
                    docker image prune -f
                """
            }
        }
    }
    
    post {
        success {
            echo '========================================='
            echo '✅ 部署成功！'
            echo "后端镜像: ${BACKEND_IMAGE}:${BACKEND_TAG}"
            echo "访问地址: http://test-server/admin"
            echo '========================================='
            
            // 发送成功通知（可选）
            // emailext (
            //     subject: "部署成功 - Build #${BUILD_NUMBER}",
            //     body: "部署成功，访问地址: http://test-server/admin",
            //     to: "dev-team@example.com"
            // )
        }
        
        failure {
            echo '========================================='
            echo '❌ 部署失败！'
            echo '========================================='
            
            // 回滚（可选）
            sh """
                docker stop bearjia-backend || true
                docker rm bearjia-backend || true
                
                # 恢复上一个版本
                LAST_TAG=\$((\${BUILD_NUMBER} - 1))
                if docker images | grep ${BACKEND_IMAGE} | grep \${LAST_TAG}; then
                    docker run -d \
                      --name bearjia-backend \
                      --network ${DOCKER_NETWORK} \
                      --restart unless-stopped \
                      -p 8080:8080 \
                      -e SPRING_PROFILES_ACTIVE=docker \
                      -v ${DEPLOY_DIR}/logs:/app/logs \
                      -v ${DEPLOY_DIR}/uploads:/app/uploads \
                      ${BACKEND_IMAGE}:\${LAST_TAG}
                fi
            """ || true
            
            // 发送失败通知
            // emailext (
            //     subject: "部署失败 - Build #${BUILD_NUMBER}",
            //     body: "部署失败，请查看Jenkins日志",
            //     to: "dev-team@example.com"
            // )
        }
        
        always {
            echo '========== 清理工作空间 =========='
            // 清理构建产物（可选）
            // cleanWs()
        }
    }
}
```

### 5.2 Jenkins Job配置

#### 5.2.1 创建Pipeline Job

1. 登录Jenkins: `http://jenkins-server:8080`
2. 点击"新建任务"
3. 输入任务名称: `bearjia-deploy`
4. 选择"Pipeline"
5. 点击"确定"

#### 5.2.2 配置Pipeline

**General**:
- 描述: `濂溪区胃癌筛查系统 - 自动化部署`
- 勾选"丢弃旧的构建" → 保留构建的天数: 30，保持构建的最大个数: 10

**构建触发器**:
- 勾选"Poll SCM": `H/5 * * * *` (每5分钟检查一次代码变化)
- 或勾选"GitHub hook trigger" (如果使用GitHub/GitLab Webhook)

**Pipeline**:
- Definition: `Pipeline script from SCM`
- SCM: `Git`
- Repository URL: `http://your-git-server/bearjia/qms-vue.git`
- Credentials: 选择已配置的Git凭证
- Branch Specifier: `*/master`
- Script Path: `Jenkinsfile`

点击"保存"。

#### 5.2.3 配置Git凭证

如果还没有配置Git凭证:

1. 进入Jenkins → 系统管理 → Manage Credentials
2. 选择"(global)" → Add Credentials
3. Kind: `Username with password`
4. Username: Git用户名
5. Password: Git密码或Token
6. ID: `git-credentials-id`
7. 点击"Create"

### 5.3 手动触发构建

1. 进入Jenkins Job页面
2. 点击"立即构建"
3. 查看"Console Output"监控构建过程
4. 构建成功后访问测试环境验证

---

## 六、环境配置

### 6.1 数据库初始化

如果是全新环境，需要初始化数据库:

```bash
# 复制SQL文件到MySQL容器
docker cp bearjia-admin-backend/src/main/resources/sql/gc_gastric_cancer_screening.sql mysql:/tmp/
docker cp bearjia-admin-backend/src/main/resources/sql/gc_region_data_sample.sql mysql:/tmp/
docker cp bearjia-admin-backend/src/main/resources/sql/gc_questionnaire_template_sample.sql mysql:/tmp/

# 执行SQL
docker exec -it mysql bash -c "mysql -uroot -p bear_jia < /tmp/gc_gastric_cancer_screening.sql"
docker exec -it mysql bash -c "mysql -uroot -p bear_jia < /tmp/gc_region_data_sample.sql"
docker exec -it mysql bash -c "mysql -uroot -p bear_jia < /tmp/gc_questionnaire_template_sample.sql"
```

### 6.2 环境变量配置

创建 `.env` 文件（用于本地测试）:

```bash
# 数据库配置
DB_HOST=mysql
DB_PORT=3306
DB_NAME=bear_jia
DB_USERNAME=bearjia
DB_PASSWORD=bearjia123

# Redis配置
REDIS_HOST=redis
REDIS_PORT=6379
REDIS_PASSWORD=

# 应用配置
SPRING_PROFILES_ACTIVE=docker
TZ=Asia/Shanghai
```

### 6.3 日志配置

```bash
# 创建日志目录
sudo mkdir -p /data/bearjia/logs

# 配置日志轮转
sudo tee /etc/logrotate.d/bearjia <<EOF
/data/bearjia/logs/*.log {
    daily
    rotate 30
    compress
    delaycompress
    missingok
    notifempty
    create 0644 root root
}
EOF
```

---

## 七、部署验证

### 7.1 验证后端服务

```bash
# 检查容器状态
docker ps | grep bearjia-backend

# 查看容器日志
docker logs -f bearjia-backend

# 检查健康状态
curl http://localhost:8080/api/actuator/health

# 预期输出:
# {
#   "status": "UP",
#   "components": {
#     "db": {"status": "UP"},
#     "redis": {"status": "UP"}
#   }
# }

# 测试API接口
curl http://localhost:8080/api/system/user/getInfo
```

### 7.2 验证前端页面

```bash
# 检查静态文件
ls -la /data/nginx/html/bearjia-admin/

# 访问前端页面
curl -I http://localhost/admin

# 在浏览器中访问
# http://test-server/admin
```

### 7.3 验证数据库连接

```bash
# 进入后端容器
docker exec -it bearjia-backend sh

# 测试MySQL连接
curl mysql:3306
# 预期: 返回MySQL版本信息

# 测试Redis连接
curl redis:6379
# 预期: 返回PONG

# 退出容器
exit
```

### 7.4 完整功能测试

1. **登录测试**
   - 访问: `http://test-server/admin`
   - 使用管理员账号登录
   - 验证: 登录成功，跳转到首页

2. **API测试**
   - 打开浏览器开发者工具
   - 查看Network标签
   - 验证: API请求正常，返回200状态码

3. **数据库测试**
   - 创建测试数据（居民、问卷等）
   - 验证: 数据正确保存到MySQL

4. **文件上传测试**
   - 上传测试文件
   - 验证: 文件保存到 `/data/bearjia/uploads`

---

## 八、常见问题

### 8.1 容器无法启动

**问题**: `docker ps` 看不到 bearjia-backend 容器

**排查步骤**:
```bash
# 查看所有容器（包括停止的）
docker ps -a | grep bearjia

# 查看容器日志
docker logs bearjia-backend

# 检查镜像是否存在
docker images | grep bearjia-backend
```

**常见原因**:
- 端口冲突: 检查8080端口是否被占用
- 配置错误: 检查 application-docker.yml
- 依赖容器未启动: 检查MySQL、Redis是否运行

**解决方案**:
```bash
# 删除容器重新创建
docker rm -f bearjia-backend
docker-compose up -d bearjia-backend
```

### 8.2 无法连接MySQL

**问题**: 应用日志显示 `Could not connect to MySQL`

**排查步骤**:
```bash
# 检查MySQL容器状态
docker ps | grep mysql

# 测试网络连通性
docker exec bearjia-backend ping -c 3 mysql

# 检查MySQL用户权限
docker exec -it mysql mysql -uroot -p -e "SELECT user, host FROM mysql.user WHERE user='bearjia';"
```

**解决方案**:
```bash
# 创建数据库用户
docker exec -it mysql mysql -uroot -p <<EOF
CREATE USER 'bearjia'@'%' IDENTIFIED BY 'bearjia123';
GRANT ALL PRIVILEGES ON bear_jia.* TO 'bearjia'@'%';
FLUSH PRIVILEGES;
EOF
```

### 8.3 Nginx 404错误

**问题**: 访问 `http://test-server/admin` 返回404

**排查步骤**:
```bash
# 检查静态文件是否存在
docker exec nginx ls -la /usr/share/nginx/html/bearjia-admin/

# 检查Nginx配置
docker exec nginx nginx -t

# 查看Nginx日志
docker logs nginx
```

**解决方案**:
```bash
# 重新部署前端
cd bear-jia-vue3
npm run build:prod
sudo cp -r dist/* /data/nginx/html/bearjia-admin/

# 重载Nginx
docker exec nginx nginx -s reload
```

### 8.4 Jenkins构建失败

**问题**: Jenkins Pipeline执行失败

**排查步骤**:
1. 查看Jenkins Console Output
2. 定位失败的Stage
3. 检查错误信息

**常见错误**:
- Maven构建失败: 检查pom.xml依赖
- Docker构建失败: 检查Dockerfile语法
- 权限不足: 给Jenkins用户添加Docker权限

**解决方案**:
```bash
# 添加Jenkins用户到docker组
sudo usermod -aG docker jenkins
sudo systemctl restart jenkins
```

### 8.5 前端API请求跨域

**问题**: 浏览器控制台显示CORS错误

**排查步骤**:
- 检查Nginx代理配置
- 查看请求URL是否正确

**解决方案**:
在Nginx配置中添加CORS头:
```nginx
location /api/ {
    proxy_pass http://bearjia-backend:8080/api/;
    
    # 添加CORS支持
    add_header Access-Control-Allow-Origin * always;
    add_header Access-Control-Allow-Methods 'GET, POST, PUT, DELETE, OPTIONS' always;
    add_header Access-Control-Allow-Headers 'DNT,X-Mx-ReqToken,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Authorization' always;
    
    # 其他配置...
}
```

### 8.6 容器内存不足

**问题**: 容器频繁重启，日志显示OOM

**排查步骤**:
```bash
# 查看容器资源使用
docker stats bearjia-backend

# 查看JVM堆内存
docker exec bearjia-backend jmap -heap 1
```

**解决方案**:
调整JVM参数（在Dockerfile或docker-compose.yml中）:
```yaml
environment:
  - JAVA_OPTS=-Xms512m -Xmx1g
```

---

## 📊 附录

### 附录A: 快速命令参考

```bash
# ========== Docker命令 ==========

# 构建镜像
docker-compose build bearjia-backend

# 启动容器
docker-compose up -d bearjia-backend

# 停止容器
docker-compose stop bearjia-backend

# 重启容器
docker-compose restart bearjia-backend

# 查看日志
docker-compose logs -f bearjia-backend

# 进入容器
docker exec -it bearjia-backend sh

# 删除容器
docker-compose down

# ========== 部署命令 ==========

# 一键部署（后端）
cd /data/workspace/qms-vue && \
  docker-compose build bearjia-backend && \
  docker-compose up -d bearjia-backend && \
  docker-compose logs -f bearjia-backend

# 一键部署（前端）
cd /data/workspace/qms-vue/bear-jia-vue3 && \
  npm run build:prod && \
  sudo cp -r dist/* /data/nginx/html/bearjia-admin/ && \
  docker exec nginx nginx -s reload

# ========== 验证命令 ==========

# 健康检查
curl http://localhost:8080/api/actuator/health

# 前端检查
curl -I http://localhost/admin

# 查看容器状态
docker ps | grep bearjia

# 查看网络
docker network inspect bearjia-network
```

### 附录B: 目录结构

```
/data/workspace/qms-vue/
├── bearjia-admin-backend/
│   ├── Dockerfile                          # 后端Dockerfile
│   ├── .dockerignore                       # Docker忽略文件
│   ├── pom.xml
│   └── src/
│       └── main/
│           └── resources/
│               ├── application-docker.yml  # Docker环境配置
│               └── sql/                    # 数据库脚本
├── bear-jia-vue3/
│   ├── build-docker.sh                     # 前端构建脚本
│   └── dist/                               # 构建产物
├── nginx/
│   └── bearjia.conf                        # Nginx配置
├── docker-compose.yml                       # Docker Compose配置
├── Jenkinsfile                              # Jenkins Pipeline
└── .env                                     # 环境变量

/data/bearjia/                               # 应用数据目录
├── logs/                                    # 日志目录
└── uploads/                                 # 上传文件目录

/data/nginx/                                 # Nginx数据目录
├── html/
│   └── bearjia-admin/                      # 前端静态文件
└── conf.d/
    └── bearjia.conf                        # Nginx配置
```

### 附录C: 版本信息

| 组件 | 版本 | 备注 |
|-----|------|------|
| Java | 17 | OpenJDK 17-slim |
| Spring Boot | 2.7.x | 根据项目实际版本 |
| MySQL | 8.0 | 已有容器 |
| Redis | 7.0 | 已有容器 |
| Nginx | 1.24 | 已有容器 |
| Node.js | 18.x | 构建前端使用 |
| Docker | 20.10+ | 容器运行时 |
| Docker Compose | 2.x | 容器编排 |
| Jenkins | 2.x | CI/CD工具 |

---

## 📝 总结

本部署方案提供了基于 **Docker + Jenkins** 的完整容器化部署流程，包括:

✅ **Java服务Docker化**: Dockerfile、多阶段构建、健康检查  
✅ **前端静态托管**: Nginx配置、自动化构建脚本  
✅ **Jenkins自动化**: Pipeline脚本、自动构建、自动部署  
✅ **环境配置**: Docker Compose、网络配置、数据库初始化  
✅ **部署验证**: 完整的验证步骤和测试方法  
✅ **故障排查**: 常见问题和解决方案

**关键优势**:
- 🚀 **快速部署**: Jenkins一键构建部署
- 🔄 **版本管理**: Docker镜像版本控制
- 📦 **环境隔离**: 容器化保证环境一致性
- 🔧 **易于维护**: 标准化部署流程
- 🔙 **快速回滚**: 支持版本回退

**下一步**: 
1. 按照文档配置Docker环境
2. 创建Jenkinsfile并配置Jenkins Job
3. 执行首次部署并验证

---

**文档版本**: v1.0  
**编写日期**: 2025-12-16  
**适用环境**: 测试环境  
**编写人**: Qoder AI Assistant
