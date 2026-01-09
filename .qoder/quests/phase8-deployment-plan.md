# 第八阶段：部署上线 - 实施方案

**阶段名称**: 部署上线  
**计划时间**: 1周（7个工作日）  
**开始时间**: 2025-12-16  
**预计完成**: 2025-12-23  

---

## 📋 目录

1. [部署架构设计](#一部署架构设计)
2. [服务器环境配置](#二服务器环境配置)
3. [数据库部署](#三数据库部署)
4. [后端应用部署](#四后端应用部署)
5. [前端应用部署](#五前端应用部署)
6. [小程序发布](#六小程序发布)
7. [系统初始化](#七系统初始化)
8. [监控与运维](#八监控与运维)
9. [用户培训](#九用户培训)
10. [上线计划](#十上线计划)

---

## 一、部署架构设计

### 1.1 整体架构

```
┌─────────────────────────────────────────────────────────┐
│                        外网访问                          │
└─────────────────────────────────────────────────────────┘
                            │
              ┌─────────────┼─────────────┐
              │             │             │
         【小程序】    【管理后台】   【第三方系统】
              │             │             │
              └─────────────┼─────────────┘
                            │
                ┌───────────▼───────────┐
                │    Nginx反向代理      │
                │   (端口80/443)        │
                └───────────┬───────────┘
                            │
              ┌─────────────┼─────────────┐
              │                           │
    ┌─────────▼─────────┐      ┌─────────▼─────────┐
    │  前端静态资源服务  │      │   后端API服务      │
    │  (Vue3应用)        │      │  (Spring Boot)     │
    │  端口: 5173        │      │  端口: 8080        │
    └───────────────────┘      └─────────┬───────────┘
                                          │
                                ┌─────────▼─────────┐
                                │   MySQL数据库      │
                                │   端口: 3306       │
                                └───────────────────┘
```

### 1.2 服务器规划

| 服务器 | 角色 | 配置建议 | 操作系统 |
|-------|------|---------|----------|
| Server-1 | 应用服务器（前后端+Nginx） | 4核8G 100G | CentOS 7.9 / Ubuntu 22.04 |
| Server-2 | 数据库服务器 | 4核8G 200G SSD | CentOS 7.9 / Ubuntu 22.04 |
| Server-3 | 备份服务器（可选） | 2核4G 500G | CentOS 7.9 |

**注**: 初期可合并为单服务器部署（8核16G），生产环境建议分离部署。

### 1.3 端口规划

| 服务 | 端口 | 访问范围 | 说明 |
|-----|------|---------|------|
| Nginx HTTP | 80 | 外网 | HTTP服务 |
| Nginx HTTPS | 443 | 外网 | HTTPS服务 |
| Spring Boot | 8080 | 内网 | 后端API |
| Vue前端开发 | 5173 | 内网 | 开发模式（生产环境使用Nginx代理） |
| MySQL | 3306 | 内网 | 数据库 |
| Redis（可选） | 6379 | 内网 | 缓存 |

---

## 二、服务器环境配置

### 2.1 基础环境准备

#### 2.1.1 系统更新
```bash
# CentOS 7
sudo yum update -y

# Ubuntu 22.04
sudo apt update && sudo apt upgrade -y
```

#### 2.1.2 安装基础工具
```bash
# CentOS 7
sudo yum install -y vim wget curl git unzip net-tools

# Ubuntu 22.04
sudo apt install -y vim wget curl git unzip net-tools
```

#### 2.1.3 配置防火墙
```bash
# CentOS 7 - 使用firewalld
sudo systemctl start firewalld
sudo systemctl enable firewalld
sudo firewall-cmd --permanent --add-port=80/tcp
sudo firewall-cmd --permanent --add-port=443/tcp
sudo firewall-cmd --permanent --add-port=8080/tcp
sudo firewall-cmd --reload

# Ubuntu 22.04 - 使用ufw
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw allow 8080/tcp
sudo ufw enable
```

### 2.2 Java环境安装

#### 2.2.1 安装OpenJDK 17
```bash
# CentOS 7
sudo yum install -y java-17-openjdk java-17-openjdk-devel

# Ubuntu 22.04
sudo apt install -y openjdk-17-jdk
```

#### 2.2.2 验证Java版本
```bash
java -version
# 预期输出: openjdk version "17.x.x"
```

#### 2.2.3 配置JAVA_HOME
```bash
# 编辑环境变量
sudo vim /etc/profile.d/java.sh

# 添加以下内容
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
export PATH=$JAVA_HOME/bin:$PATH

# 使配置生效
source /etc/profile.d/java.sh
```

### 2.3 Node.js环境安装

#### 2.3.1 安装Node.js 18.x
```bash
# 使用NodeSource仓库
curl -fsSL https://rpm.nodesource.com/setup_18.x | sudo bash -
sudo yum install -y nodejs  # CentOS

# 或使用nvm（推荐）
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.0/install.sh | bash
source ~/.bashrc
nvm install 18
nvm use 18
```

#### 2.3.2 验证Node.js版本
```bash
node -v  # v18.x.x
npm -v   # 9.x.x
```

#### 2.3.3 配置npm国内镜像
```bash
npm config set registry https://registry.npmmirror.com
```

### 2.4 Nginx安装

#### 2.4.1 安装Nginx
```bash
# CentOS 7
sudo yum install -y epel-release
sudo yum install -y nginx

# Ubuntu 22.04
sudo apt install -y nginx
```

#### 2.4.2 启动Nginx
```bash
sudo systemctl start nginx
sudo systemctl enable nginx
sudo systemctl status nginx
```

#### 2.4.3 验证Nginx
```bash
curl http://localhost
# 预期: 显示Nginx欢迎页面
```

---

## 三、数据库部署

### 3.1 MySQL安装

#### 3.1.1 安装MySQL 8.0
```bash
# CentOS 7
sudo wget https://dev.mysql.com/get/mysql80-community-release-el7-3.noarch.rpm
sudo rpm -ivh mysql80-community-release-el7-3.noarch.rpm
sudo yum install -y mysql-server

# Ubuntu 22.04
sudo apt install -y mysql-server
```

#### 3.1.2 启动MySQL
```bash
sudo systemctl start mysqld
sudo systemctl enable mysqld
sudo systemctl status mysqld
```

#### 3.1.3 获取初始密码（CentOS）
```bash
sudo grep 'temporary password' /var/log/mysqld.log
# 输出: A temporary password is generated for root@localhost: xxxxx
```

#### 3.1.4 安全初始化
```bash
sudo mysql_secure_installation

# 按提示完成以下操作:
# 1. 输入临时密码
# 2. 设置新密码（复杂度要求: 8位+大小写+数字+特殊字符）
# 3. 移除匿名用户: Yes
# 4. 禁止root远程登录: Yes (如需远程,选No并单独配置)
# 5. 删除test数据库: Yes
# 6. 重新加载权限表: Yes
```

### 3.2 数据库配置优化

#### 3.2.1 编辑MySQL配置
```bash
sudo vim /etc/my.cnf  # CentOS
# 或
sudo vim /etc/mysql/mysql.conf.d/mysqld.cnf  # Ubuntu

# 添加/修改以下配置
[mysqld]
character-set-server=utf8mb4
collation-server=utf8mb4_unicode_ci
default-time-zone='+08:00'
max_connections=500
innodb_buffer_pool_size=2G
innodb_log_file_size=256M
```

#### 3.2.2 重启MySQL
```bash
sudo systemctl restart mysqld
```

### 3.3 创建数据库和用户

#### 3.3.1 登录MySQL
```bash
mysql -uroot -p
```

#### 3.3.2 创建数据库
```sql
-- 创建数据库
CREATE DATABASE bear_jia DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 查看数据库
SHOW DATABASES;
```

#### 3.3.3 创建应用用户
```sql
-- 创建用户（替换your_password为实际密码）
CREATE USER 'bearjia'@'localhost' IDENTIFIED BY 'your_password';

-- 授权
GRANT ALL PRIVILEGES ON bear_jia.* TO 'bearjia'@'localhost';

-- 如果需要远程访问（谨慎使用）
CREATE USER 'bearjia'@'%' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON bear_jia.* TO 'bearjia'@'%';

-- 刷新权限
FLUSH PRIVILEGES;

-- 退出
EXIT;
```

### 3.4 导入数据库结构

#### 3.4.1 上传SQL文件到服务器
```bash
# 在本地执行（替换server_ip为实际IP）
scp bearjia-admin-backend/src/main/resources/sql/*.sql root@server_ip:/tmp/
```

#### 3.4.2 执行SQL脚本
```bash
# 在服务器上执行
cd /tmp

# 导入核心表结构和数据字典
mysql -ubearjia -p bear_jia < gc_gastric_cancer_screening.sql

# 导入行政区划示例数据
mysql -ubearjia -p bear_jia < gc_region_data_sample.sql

# 导入问卷模板示例数据
mysql -ubearjia -p bear_jia < gc_questionnaire_template_sample.sql

# 验证导入
mysql -ubearjia -p bear_jia -e "SHOW TABLES;"
```

#### 3.4.3 验证数据
```bash
# 检查表数量
mysql -ubearjia -p bear_jia -e "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='bear_jia';"

# 检查行政区划数据
mysql -ubearjia -p bear_jia -e "SELECT COUNT(*) FROM gc_region;"

# 检查问卷模板数据
mysql -ubearjia -p bear_jia -e "SELECT template_name, version FROM gc_questionnaire_template;"
```

### 3.5 数据库备份策略

#### 3.5.1 创建备份脚本
```bash
sudo mkdir -p /data/backup/mysql
sudo vim /data/backup/mysql/backup.sh
```

#### 3.5.2 备份脚本内容
```bash
#!/bin/bash
# MySQL数据库备份脚本

BACKUP_DIR="/data/backup/mysql"
DB_NAME="bear_jia"
DB_USER="bearjia"
DB_PASS="your_password"
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="${BACKUP_DIR}/${DB_NAME}_${DATE}.sql"

# 执行备份
mysqldump -u${DB_USER} -p${DB_PASS} ${DB_NAME} > ${BACKUP_FILE}

# 压缩备份文件
gzip ${BACKUP_FILE}

# 删除7天前的备份
find ${BACKUP_DIR} -name "*.sql.gz" -mtime +7 -delete

echo "备份完成: ${BACKUP_FILE}.gz"
```

#### 3.5.3 设置定时任务
```bash
# 设置可执行权限
sudo chmod +x /data/backup/mysql/backup.sh

# 添加定时任务（每天凌晨2点执行）
crontab -e

# 添加以下内容
0 2 * * * /data/backup/mysql/backup.sh >> /var/log/mysql_backup.log 2>&1
```

---

## 四、后端应用部署

### 4.1 打包后端应用

#### 4.1.1 本地打包
```bash
# 在项目根目录执行
cd /data/workspace/qms-vue/bearjia-admin-backend

# 使用Maven打包
mvn clean package -DskipTests

# 打包成功后，JAR文件位于
# target/bearjia-admin-backend-1.0.0.jar
```

#### 4.1.2 上传到服务器
```bash
# 在本地执行
scp target/bearjia-admin-backend-1.0.0.jar root@server_ip:/opt/bearjia/
```

### 4.2 配置应用

#### 4.2.1 创建部署目录
```bash
# 在服务器上执行
sudo mkdir -p /opt/bearjia
sudo mkdir -p /opt/bearjia/logs
sudo mkdir -p /opt/bearjia/config
```

#### 4.2.2 创建生产环境配置
```bash
sudo vim /opt/bearjia/config/application-prod.yml
```

#### 4.2.3 配置文件内容
```yaml
# 生产环境配置
server:
  port: 8080
  servlet:
    context-path: /api

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/bear_jia?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false
    username: bearjia
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
    
  # JPA配置
  jpa:
    hibernate:
      ddl-auto: none
    show-sql: false
    
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
    name: /opt/bearjia/logs/application.log
    max-size: 100MB
    max-history: 30

# 业务配置
app:
  upload-path: /opt/bearjia/uploads
  ocr:
    enabled: false  # 初期禁用OCR
```

### 4.3 创建启动脚本

#### 4.3.1 启动脚本
```bash
sudo vim /opt/bearjia/start.sh
```

```bash
#!/bin/bash
# Spring Boot应用启动脚本

APP_NAME="bearjia-admin-backend"
APP_JAR="/opt/bearjia/${APP_NAME}-1.0.0.jar"
CONFIG_DIR="/opt/bearjia/config"
LOG_FILE="/opt/bearjia/logs/startup.log"

# JVM参数
JVM_OPTS="-Xms512m -Xmx2g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# 检查是否已启动
PID=$(pgrep -f ${APP_JAR})
if [ -n "$PID" ]; then
    echo "应用已启动，PID: $PID"
    exit 1
fi

# 启动应用
nohup java ${JVM_OPTS} \
  -Dspring.profiles.active=prod \
  -Dspring.config.additional-location=${CONFIG_DIR}/ \
  -jar ${APP_JAR} \
  > ${LOG_FILE} 2>&1 &

echo "应用启动中，请查看日志: ${LOG_FILE}"
```

#### 4.3.2 停止脚本
```bash
sudo vim /opt/bearjia/stop.sh
```

```bash
#!/bin/bash
# Spring Boot应用停止脚本

APP_NAME="bearjia-admin-backend"
APP_JAR="/opt/bearjia/${APP_NAME}-1.0.0.jar"

# 查找进程
PID=$(pgrep -f ${APP_JAR})

if [ -z "$PID" ]; then
    echo "应用未启动"
    exit 1
fi

# 停止进程
kill -15 $PID

# 等待进程结束
for i in {1..30}; do
    if ! ps -p $PID > /dev/null; then
        echo "应用已停止"
        exit 0
    fi
    sleep 1
done

# 强制停止
kill -9 $PID
echo "应用已强制停止"
```

#### 4.3.3 重启脚本
```bash
sudo vim /opt/bearjia/restart.sh
```

```bash
#!/bin/bash
# 重启应用

/opt/bearjia/stop.sh
sleep 3
/opt/bearjia/start.sh
```

#### 4.3.4 设置权限
```bash
sudo chmod +x /opt/bearjia/*.sh
```

### 4.4 配置系统服务

#### 4.4.1 创建systemd服务
```bash
sudo vim /etc/systemd/system/bearjia.service
```

```ini
[Unit]
Description=BearJia Gastric Cancer Screening System
After=network.target mysql.service

[Service]
Type=forking
User=root
WorkingDirectory=/opt/bearjia
ExecStart=/opt/bearjia/start.sh
ExecStop=/opt/bearjia/stop.sh
ExecReload=/opt/bearjia/restart.sh
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
```

#### 4.4.2 启用服务
```bash
# 重新加载systemd配置
sudo systemctl daemon-reload

# 启动服务
sudo systemctl start bearjia

# 设置开机自启
sudo systemctl enable bearjia

# 查看服务状态
sudo systemctl status bearjia
```

### 4.5 验证后端部署

#### 4.5.1 检查进程
```bash
ps aux | grep bearjia
```

#### 4.5.2 检查日志
```bash
tail -f /opt/bearjia/logs/application.log
```

#### 4.5.3 测试接口
```bash
# 健康检查
curl http://localhost:8080/api/actuator/health

# 预期输出: {"status":"UP"}
```

---

## 五、前端应用部署

### 5.1 打包前端应用

#### 5.1.1 修改生产环境配置
```bash
# 编辑生产环境配置
vim /data/workspace/qms-vue/bear-jia-vue3/.env.prod
```

```properties
# 生产环境配置
NODE_ENV=production

# API地址（替换为实际服务器地址）
VITE_APP_BASE_API=https://your-domain.com/api

# 应用标题
VITE_APP_TITLE=濂溪区胃癌筛查信息系统
```

#### 5.1.2 打包构建
```bash
cd /data/workspace/qms-vue/bear-jia-vue3

# 安装依赖
npm install

# 生产环境打包
npm run build:prod

# 打包成功后，dist目录包含所有静态文件
```

#### 5.1.3 上传到服务器
```bash
# 压缩dist目录
tar -czf dist.tar.gz dist/

# 上传到服务器
scp dist.tar.gz root@server_ip:/tmp/
```

### 5.2 部署前端静态文件

#### 5.2.1 创建部署目录
```bash
# 在服务器上执行
sudo mkdir -p /var/www/bearjia-frontend
```

#### 5.2.2 解压文件
```bash
cd /var/www/bearjia-frontend
sudo tar -xzf /tmp/dist.tar.gz
sudo mv dist/* .
sudo rm -rf dist
```

### 5.3 配置Nginx

#### 5.3.1 创建Nginx配置文件
```bash
sudo vim /etc/nginx/conf.d/bearjia.conf
```

#### 5.3.2 配置内容
```nginx
# 前端应用
server {
    listen 80;
    server_name your-domain.com;  # 替换为实际域名
    
    # 访问日志
    access_log /var/log/nginx/bearjia_access.log;
    error_log /var/log/nginx/bearjia_error.log;
    
    # 前端静态文件
    location / {
        root /var/www/bearjia-frontend;
        index index.html;
        try_files $uri $uri/ /index.html;
    }
    
    # 后端API代理
    location /api/ {
        proxy_pass http://localhost:8080/api/;
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
    }
    
    # gzip压缩
    gzip on;
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml;
    gzip_min_length 1000;
}
```

#### 5.3.3 配置HTTPS（可选但推荐）
```bash
# 安装certbot（Let's Encrypt免费证书）
sudo yum install -y certbot python3-certbot-nginx  # CentOS
# 或
sudo apt install -y certbot python3-certbot-nginx  # Ubuntu

# 获取证书（替换your-domain.com为实际域名）
sudo certbot --nginx -d your-domain.com
```

HTTPS配置会自动添加到Nginx配置文件：
```nginx
server {
    listen 443 ssl http2;
    server_name your-domain.com;
    
    ssl_certificate /etc/letsencrypt/live/your-domain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/your-domain.com/privkey.pem;
    ssl_protocols TLSv1.2 TLSv1.3;
    
    # 其他配置同上...
}

# HTTP自动跳转HTTPS
server {
    listen 80;
    server_name your-domain.com;
    return 301 https://$server_name$request_uri;
}
```

#### 5.3.4 测试并重启Nginx
```bash
# 测试配置
sudo nginx -t

# 重启Nginx
sudo systemctl restart nginx
```

### 5.4 验证前端部署

#### 5.4.1 访问前端
```bash
# 在浏览器中访问
http://your-domain.com
# 或
https://your-domain.com
```

#### 5.4.2 检查网络请求
- 打开浏览器开发者工具
- 查看Network标签
- 验证API请求是否正常

---

## 六、小程序发布

### 6.1 小程序配置

#### 6.1.1 修改配置文件
```javascript
// h5/src/config/config.js
export default {
  // API地址（生产环境）
  baseURL: 'https://your-domain.com/api',
  
  // 微信小程序AppID
  appId: 'your_appid',
  
  // 版本号
  version: '1.0.0'
}
```

#### 6.1.2 配置服务器域名
在微信小程序管理后台配置：
1. 登录 https://mp.weixin.qq.com
2. 开发 → 开发管理 → 开发设置 → 服务器域名
3. 添加以下域名：
   - request合法域名: `https://your-domain.com`
   - uploadFile合法域名: `https://your-domain.com`
   - downloadFile合法域名: `https://your-domain.com`

### 6.2 打包小程序

#### 6.2.1 构建微信小程序
```bash
cd /data/workspace/qms-vue/h5

# 安装依赖
npm install

# 构建微信小程序
npm run build:mp-weixin
```

#### 6.2.2 上传到微信开发者工具
1. 打开微信开发者工具
2. 导入项目: `h5/dist/dev/mp-weixin`
3. 填写AppID
4. 点击"上传"按钮
5. 填写版本号和项目备注
6. 上传代码

### 6.3 提交审核

#### 6.3.1 设置体验版
1. 登录微信小程序管理后台
2. 管理 → 版本管理 → 开发版本
3. 设置为体验版
4. 添加体验者（扫码或手机号）

#### 6.3.2 提交审核
1. 版本管理 → 审核版本 → 提交审核
2. 填写审核信息:
   - 功能页面: 选择核心功能页面截图
   - 服务类目: 医疗 → 互联网医院
   - 功能简介: 濂溪区胃癌早期筛查问卷调查系统
3. 提交等待审核（通常1-7个工作日）

#### 6.3.3 发布上线
1. 审核通过后，点击"发布"按钮
2. 确认发布
3. 小程序正式上线

---

## 七、系统初始化

### 7.1 导入全国行政区划数据

#### 7.1.1 获取全国行政区划数据
```bash
# 从国家统计局下载最新行政区划代码
# http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/

# 或使用现成的数据源
wget https://github.com/modood/Administrative-divisions-of-China/raw/master/dist/pca-code.json
```

#### 7.1.2 转换为SQL格式
可使用Python脚本转换（示例）:
```python
import json

with open('pca-code.json', 'r', encoding='utf-8') as f:
    data = json.load(f)

with open('import_regions.sql', 'w', encoding='utf-8') as f:
    for item in data:
        sql = f"INSERT INTO gc_region (region_code, region_name, region_level, parent_id, sort_order) VALUES ('{item['code']}', '{item['name']}', {item['level']}, {item['parent_id']}, 0);\n"
        f.write(sql)
```

#### 7.1.3 导入数据库
```bash
mysql -ubearjia -p bear_jia < import_regions.sql
```

### 7.2 创建管理员账号

#### 7.2.1 登录数据库
```bash
mysql -ubearjia -p bear_jia
```

#### 7.2.2 创建超级管理员
```sql
-- 插入用户（密码: admin123，使用BCrypt加密）
INSERT INTO sys_user (user_id, dept_id, user_name, nick_name, email, phonenumber, sex, password, status, create_time)
VALUES (1, 103, 'admin', '系统管理员', 'admin@bearjia.com', '13800138000', '0', 
        '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE/sXHbLxnqE.', '0', NOW());

-- 分配角色（超级管理员）
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);

-- 验证
SELECT user_name, nick_name, status FROM sys_user WHERE user_id = 1;
```

#### 7.2.3 创建区级管理员
```sql
-- 插入濂溪区管理员
INSERT INTO sys_user (user_id, dept_id, user_name, nick_name, email, phonenumber, sex, password, status, create_time)
VALUES (2, 104, 'lianxi_admin', '濂溪区管理员', 'lianxi@bearjia.com', '13900139000', '0',
        '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE/sXHbLxnqE.', '0', NOW());

-- 分配角色
INSERT INTO sys_user_role (user_id, role_id) VALUES (2, 2);  -- 区级管理员角色
```

### 7.3 初始化系统配置

#### 7.3.1 系统参数配置
```sql
-- 系统名称
INSERT INTO sys_config (config_name, config_key, config_value, config_type, create_time) 
VALUES ('系统名称', 'sys.system.name', '濂溪区胃癌筛查信息系统', 'Y', NOW());

-- 文件上传路径
INSERT INTO sys_config (config_name, config_key, config_value, config_type, create_time)
VALUES ('文件上传路径', 'sys.upload.path', '/opt/bearjia/uploads', 'Y', NOW());

-- OCR功能开关
INSERT INTO sys_config (config_name, config_key, config_value, config_type, create_time)
VALUES ('OCR功能', 'sys.ocr.enabled', 'false', 'Y', NOW());
```

#### 7.3.2 业务参数配置
```sql
-- 重点人群评分阈值
INSERT INTO sys_config (config_name, config_key, config_value, config_type, create_time)
VALUES ('重点人群阈值', 'gc.focus.threshold', '60', 'Y', NOW());

-- 采血点默认容量
INSERT INTO sys_config (config_name, config_key, config_value, config_type, create_time)
VALUES ('采血点默认容量', 'gc.sampling.capacity', '100', 'Y', NOW());

-- 随访计划次数
INSERT INTO sys_config (config_name, config_key, config_value, config_type, create_time)
VALUES ('随访计划次数', 'gc.followup.count', '3', 'Y', NOW());
```

### 7.4 初始化示例数据

#### 7.4.1 创建示例采血点
```sql
-- 插入十里街道采血点
INSERT INTO gc_sampling_site (site_name, region_id, address, daily_capacity, status, create_time)
VALUES ('十里街道社区卫生服务中心', 
        (SELECT region_id FROM gc_region WHERE region_name='十里街道'),
        '濂溪区十里大道100号', 100, '0', NOW());
```

#### 7.4.2 创建示例调查员
```sql
-- 插入调查员（关联用户ID）
INSERT INTO gc_surveyor (user_id, real_name, gender, region_id, phone_number, status, create_time)
VALUES (3, '张调查', '0', 
        (SELECT region_id FROM gc_region WHERE region_name='十里街道'),
        '13700137000', '0', NOW());
```

---

## 八、监控与运维

### 8.1 系统监控

#### 8.1.1 安装监控工具
```bash
# 安装htop（进程监控）
sudo yum install -y htop  # CentOS
sudo apt install -y htop  # Ubuntu

# 安装iftop（网络监控）
sudo yum install -y iftop  # CentOS
sudo apt install -y iftop  # Ubuntu
```

#### 8.1.2 配置Prometheus + Grafana（可选）
```bash
# 下载Prometheus
wget https://github.com/prometheus/prometheus/releases/download/v2.40.0/prometheus-2.40.0.linux-amd64.tar.gz
tar -xzf prometheus-2.40.0.linux-amd64.tar.gz
sudo mv prometheus-2.40.0.linux-amd64 /opt/prometheus

# 配置systemd服务
sudo vim /etc/systemd/system/prometheus.service
```

#### 8.1.3 应用健康检查脚本
```bash
sudo vim /opt/bearjia/health_check.sh
```

```bash
#!/bin/bash
# 健康检查脚本

API_URL="http://localhost:8080/api/actuator/health"
ALERT_EMAIL="admin@example.com"

# 检查API健康状态
response=$(curl -s -o /dev/null -w "%{http_code}" $API_URL)

if [ $response -ne 200 ]; then
    echo "$(date): 应用健康检查失败, HTTP状态码: $response" >> /var/log/bearjia_health.log
    
    # 发送告警邮件（需配置邮件服务）
    echo "应用健康检查失败" | mail -s "BearJia系统告警" $ALERT_EMAIL
    
    # 尝试重启应用
    sudo systemctl restart bearjia
else
    echo "$(date): 应用健康正常" >> /var/log/bearjia_health.log
fi
```

#### 8.1.4 定时健康检查
```bash
# 添加定时任务（每5分钟检查一次）
crontab -e

# 添加内容
*/5 * * * * /opt/bearjia/health_check.sh
```

### 8.2 日志管理

#### 8.2.1 配置日志轮转
```bash
sudo vim /etc/logrotate.d/bearjia
```

```
/opt/bearjia/logs/*.log {
    daily
    rotate 30
    compress
    delaycompress
    missingok
    notifempty
    create 0644 root root
    postrotate
        /bin/kill -HUP $(cat /var/run/syslogd.pid 2>/dev/null) 2>/dev/null || true
    endscript
}
```

#### 8.2.2 Nginx日志轮转
```bash
sudo vim /etc/logrotate.d/nginx
```

```
/var/log/nginx/*.log {
    daily
    rotate 30
    compress
    delaycompress
    missingok
    notifempty
    create 0644 nginx nginx
    sharedscripts
    postrotate
        /bin/kill -USR1 $(cat /var/run/nginx.pid 2>/dev/null) 2>/dev/null || true
    endscript
}
```

### 8.3 性能优化

#### 8.3.1 JVM调优
根据实际情况调整JVM参数（在start.sh中）:
```bash
JVM_OPTS="-Xms1g -Xmx2g \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:HeapDumpPath=/opt/bearjia/logs/heapdump.hprof"
```

#### 8.3.2 数据库连接池优化
在application-prod.yml中调整:
```yaml
spring:
  datasource:
    hikari:
      minimum-idle: 10
      maximum-pool-size: 50
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
```

---

## 九、用户培训

### 9.1 编写用户手册

#### 9.1.1 管理员手册目录
```
1. 系统登录
2. 居民管理
   2.1 居民信息录入
   2.2 批量导入
   2.3 数据导出
3. 问卷管理
   3.1 问卷模板配置
   3.2 问卷记录查看
4. 任务管理
   4.1 创建任务
   4.2 查看进度
5. 筛查结果管理
   5.1 结果录入
   5.2 结果审核
6. 统计分析
   6.1 数据看板
   6.2 报表导出
```

#### 9.1.2 居民端小程序手册目录
```
1. 注册与登录
2. 个人信息录入
3. 问卷填写
4. 采血预约
5. 查看结果
```

#### 9.1.3 调查员端小程序手册目录
```
1. 登录系统
2. 协助居民录入
3. 离线填写
4. 查看任务进度
```

### 9.2 培训计划

#### 9.2.1 培训对象
| 角色 | 人数 | 培训时长 | 培训内容 |
|-----|------|---------|---------|
| 系统管理员 | 2-3人 | 4小时 | 系统配置、数据管理、运维监控 |
| 区级管理员 | 5-10人 | 3小时 | 居民管理、任务分配、统计分析 |
| 街道管理员 | 20-30人 | 2小时 | 居民录入、问卷管理 |
| 调查员 | 50-100人 | 1.5小时 | 小程序使用、协助录入 |

#### 9.2.2 培训方式
1. **集中培训**: 线下会议室，投影演示
2. **视频教程**: 录制操作视频，可反复观看
3. **在线答疑**: 微信群、钉钉群实时解答
4. **实操演练**: 提供测试环境，实际操作

#### 9.2.3 培训资料
- PPT演示文稿
- 操作手册（PDF）
- 视频教程（MP4）
- 常见问题FAQ文档

---

## 十、上线计划

### 10.1 上线时间表

| 时间 | 任务 | 负责人 | 备注 |
|-----|------|-------|------|
| Day 1 AM | 服务器环境配置 | 运维 | Java、Node.js、Nginx、MySQL |
| Day 1 PM | 数据库部署 | 运维+DBA | 创建数据库、导入结构 |
| Day 2 AM | 后端应用部署 | 后端开发 | 上传JAR、配置启动 |
| Day 2 PM | 前端应用部署 | 前端开发 | 打包、上传、Nginx配置 |
| Day 3 AM | 小程序发布 | 前端开发 | 提交审核（提前进行） |
| Day 3 PM | 系统初始化 | 全员 | 导入数据、创建账号 |
| Day 4 | 系统测试 | 测试 | 全流程测试 |
| Day 5 | 用户培训 | 产品 | 管理员培训 |
| Day 6 | 试运行 | 全员 | 小范围试用 |
| Day 7 | 正式上线 | 全员 | 全面开放使用 |

### 10.2 上线检查清单

#### 10.2.1 环境检查
- [ ] 服务器配置完成（CPU、内存、磁盘）
- [ ] Java 17环境安装
- [ ] Node.js 18环境安装
- [ ] Nginx安装并启动
- [ ] MySQL 8.0安装并启动
- [ ] 防火墙端口开放
- [ ] 域名解析配置

#### 10.2.2 数据库检查
- [ ] 数据库创建成功
- [ ] 用户权限配置正确
- [ ] 表结构导入完成（12个表）
- [ ] 数据字典导入完成（6组）
- [ ] 行政区划数据导入
- [ ] 问卷模板导入
- [ ] 备份策略配置

#### 10.2.3 后端检查
- [ ] JAR包上传成功
- [ ] 配置文件正确（数据库连接、日志路径）
- [ ] 应用启动成功
- [ ] 健康检查接口正常
- [ ] systemd服务配置
- [ ] 日志输出正常

#### 10.2.4 前端检查
- [ ] 静态文件上传成功
- [ ] Nginx配置正确
- [ ] 页面可正常访问
- [ ] API请求正常
- [ ] HTTPS证书配置（可选）
- [ ] gzip压缩开启

#### 10.2.5 小程序检查
- [ ] 代码上传成功
- [ ] 服务器域名配置
- [ ] 体验版测试通过
- [ ] 提交审核
- [ ] 审核通过
- [ ] 正式发布

#### 10.2.6 功能检查
- [ ] 用户登录功能
- [ ] 居民管理功能
- [ ] 问卷填写功能
- [ ] 采血预约功能
- [ ] 筛查结果管理
- [ ] 统计分析功能
- [ ] 权限控制正确

#### 10.2.7 监控检查
- [ ] 应用日志正常
- [ ] Nginx访问日志正常
- [ ] 健康检查脚本运行
- [ ] 日志轮转配置
- [ ] 数据库备份任务

### 10.3 应急预案

#### 10.3.1 数据库故障
**症状**: 数据库连接失败  
**排查步骤**:
1. 检查MySQL服务状态: `systemctl status mysqld`
2. 检查端口监听: `netstat -tuln | grep 3306`
3. 查看MySQL错误日志: `/var/log/mysqld.log`

**应对措施**:
1. 重启MySQL: `systemctl restart mysqld`
2. 如数据损坏，从备份恢复
3. 联系DBA支持

#### 10.3.2 应用服务故障
**症状**: API请求失败  
**排查步骤**:
1. 检查应用进程: `ps aux | grep bearjia`
2. 查看应用日志: `tail -f /opt/bearjia/logs/application.log`
3. 检查JVM内存: `jstat -gc <pid>`

**应对措施**:
1. 重启应用: `systemctl restart bearjia`
2. 如OOM，调整JVM参数
3. 检查数据库连接数

#### 10.3.3 Nginx故障
**症状**: 页面无法访问  
**排查步骤**:
1. 检查Nginx状态: `systemctl status nginx`
2. 测试配置: `nginx -t`
3. 查看错误日志: `/var/log/nginx/error.log`

**应对措施**:
1. 重启Nginx: `systemctl restart nginx`
2. 修复配置错误
3. 检查磁盘空间

#### 10.3.4 小程序故障
**症状**: 小程序无法打开  
**排查步骤**:
1. 检查服务器API是否正常
2. 检查小程序版本
3. 查看微信小程序后台错误日志

**应对措施**:
1. 回滚到上一个稳定版本
2. 修复Bug后重新发布
3. 联系微信技术支持

### 10.4 回滚方案

#### 10.4.1 后端回滚
```bash
# 停止当前版本
sudo systemctl stop bearjia

# 备份当前版本
sudo mv /opt/bearjia/bearjia-admin-backend-1.0.0.jar /opt/bearjia/bearjia-admin-backend-1.0.0.jar.bak

# 恢复上一版本
sudo cp /opt/bearjia/backup/bearjia-admin-backend-0.9.0.jar /opt/bearjia/bearjia-admin-backend-1.0.0.jar

# 启动应用
sudo systemctl start bearjia
```

#### 10.4.2 前端回滚
```bash
# 备份当前版本
sudo mv /var/www/bearjia-frontend /var/www/bearjia-frontend.bak

# 恢复上一版本
sudo cp -r /var/www/backup/bearjia-frontend-v0.9.0 /var/www/bearjia-frontend

# 重启Nginx
sudo systemctl reload nginx
```

#### 10.4.3 数据库回滚
```bash
# 从备份恢复
mysql -ubearjia -p bear_jia < /data/backup/mysql/bear_jia_20251215.sql
```

---

## 📊 附录

### 附录A: 常用命令速查

#### 系统管理
```bash
# 查看系统信息
uname -a
cat /etc/os-release

# 查看磁盘使用
df -h

# 查看内存使用
free -h

# 查看进程
ps aux | grep bearjia

# 查看端口占用
netstat -tuln | grep 8080
```

#### 应用管理
```bash
# 启动应用
sudo systemctl start bearjia

# 停止应用
sudo systemctl stop bearjia

# 重启应用
sudo systemctl restart bearjia

# 查看状态
sudo systemctl status bearjia

# 查看日志
tail -f /opt/bearjia/logs/application.log
```

#### 数据库管理
```bash
# 登录数据库
mysql -ubearjia -p bear_jia

# 备份数据库
mysqldump -ubearjia -p bear_jia > backup.sql

# 恢复数据库
mysql -ubearjia -p bear_jia < backup.sql
```

### 附录B: 配置文件模板

已在文档各部分提供。

### 附录C: 故障排查流程图

```
故障发生
    ↓
确认故障现象
    ↓
    ├─→ 页面无法访问 → 检查Nginx → 检查网络 → 检查DNS
    ├─→ API请求失败 → 检查后端应用 → 检查数据库 → 查看日志
    ├─→ 性能缓慢 → 检查CPU/内存 → 检查数据库慢查询 → 优化索引
    └─→ 数据错误 → 检查业务逻辑 → 查看数据库 → 恢复备份
```

---

## 📝 总结

本部署方案涵盖了从服务器环境配置到系统上线的完整流程，包括：

✅ **服务器环境**: Java、Node.js、Nginx、MySQL完整安装配置  
✅ **数据库部署**: 创建、导入、备份策略完整  
✅ **后端部署**: JAR包部署、systemd服务、监控告警  
✅ **前端部署**: 打包构建、Nginx配置、HTTPS配置  
✅ **小程序发布**: 配置、上传、审核、发布完整流程  
✅ **系统初始化**: 数据导入、账号创建、参数配置  
✅ **监控运维**: 健康检查、日志管理、性能优化  
✅ **用户培训**: 手册编写、培训计划、实操演练  
✅ **上线计划**: 时间表、检查清单、应急预案

**下一步工作**: 按照本方案逐步执行部署，确保系统稳定上线！

---

**文档版本**: v1.0  
**编写日期**: 2025-12-16  
**编写人**: Qoder AI Assistant
