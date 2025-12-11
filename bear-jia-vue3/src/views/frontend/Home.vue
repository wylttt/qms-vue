<template>
  <div class="home-page">
    <!-- Hero Section -->
    <section class="hero-section">
      <div class="hero-container">
        <div class="hero-content">
          <h1 class="hero-title">
            <span class="gradient-text">BearJia</span>
            <br />
            企业级解决方案
          </h1>
          <p class="hero-description">
            专业的后台管理系统，助力企业数字化转型。
            提供完整的用户管理、权限控制、数据分析等功能。
          </p>
          <div class="hero-actions">
            <a-button type="primary" size="large" class="cta-button">
              <template #icon>
                <rocket-outlined />
              </template>
              立即体验
            </a-button>
            <a-button size="large" class="demo-button" @click="goToDemo">
              <template #icon>
                <play-circle-outlined />
              </template>
              查看演示
            </a-button>
          </div>
        </div>
        <div class="hero-visual">
          <div class="floating-cards">
            <div class="card card-1">
              <dashboard-outlined />
              <span>数据看板</span>
            </div>
            <div class="card card-2">
              <team-outlined />
              <span>用户管理</span>
            </div>
            <div class="card card-3">
              <safety-outlined />
              <span>权限控制</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Features Section -->
    <section class="features-section">
      <div class="container">
        <div class="section-header">
          <h2>核心功能</h2>
          <p>为企业提供全方位的管理解决方案</p>
        </div>
        <div class="features-grid">
          <div 
            v-for="feature in features" 
            :key="feature.id"
            class="feature-card"
            @click="handleFeatureClick(feature)"
          >
            <div class="feature-icon">
              <component :is="feature.icon" />
            </div>
            <h3>{{ feature.title }}</h3>
            <p>{{ feature.description }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Statistics Section -->
    <section class="stats-section">
      <div class="container">
        <div class="stats-grid">
          <div v-for="stat in statistics" :key="stat.label" class="stat-item">
            <div class="stat-number">{{ animatedStats[stat.key] }}</div>
            <div class="stat-label">{{ stat.label }}</div>
          </div>
        </div>
      </div>
    </section>

    <!-- Testimonials Section -->
    <section class="testimonials-section">
      <div class="container">
        <div class="section-header">
          <h2>客户评价</h2>
          <p>听听我们的客户怎么说</p>
        </div>
        <div class="testimonials-carousel">
          <a-carousel autoplay :dots="false" ref="testimonialCarousel">
            <div v-for="testimonial in testimonials" :key="testimonial.id" class="testimonial-slide">
              <div class="testimonial-card">
                <div class="testimonial-content">
                  <quote-left-outlined class="quote-icon" />
                  <p>{{ testimonial.content }}</p>
                </div>
                <div class="testimonial-author">
                  <img :src="testimonial.avatar" :alt="testimonial.name" />
                  <div>
                    <h4>{{ testimonial.name }}</h4>
                    <span>{{ testimonial.position }}</span>
                  </div>
                </div>
              </div>
            </div>
          </a-carousel>
        </div>
      </div>
    </section>

    <!-- CTA Section -->
    <section class="cta-section">
      <div class="container">
        <div class="cta-content">
          <h2>准备开始了吗？</h2>
          <p>立即体验 BearJia 管理系统，提升您的工作效率</p>
          <div class="cta-actions">
            <a-button type="primary" size="large" @click="goToAdmin">
              <template #icon>
                <login-outlined />
              </template>
              进入管理后台
            </a-button>
            <a-button size="large" @click="goToContact">
              <template #icon>
                <phone-outlined />
              </template>
              联系我们
            </a-button>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import {
  RocketOutlined,
  PlayCircleOutlined,
  DashboardOutlined,
  TeamOutlined,
  SafetyOutlined,
  SettingOutlined,
  BarChartOutlined,
  FileTextOutlined,
  BellOutlined,
  CloudOutlined,
  MobileOutlined,
  LoginOutlined,
  PhoneOutlined
} from '@ant-design/icons-vue';

const router = useRouter();

// 功能特性数据
const features = [
  {
    id: 1,
    icon: DashboardOutlined,
    title: '数据看板',
    description: '实时数据展示，直观的图表分析，帮助您快速了解业务状况'
  },
  {
    id: 2,
    icon: TeamOutlined,
    title: '用户管理',
    description: '完善的用户体系，支持多角色权限管理，灵活的组织架构'
  },
  {
    id: 3,
    icon: SafetyOutlined,
    title: '安全防护',
    description: '多层安全防护机制，数据加密传输，保障系统安全稳定'
  },
  {
    id: 4,
    icon: SettingOutlined,
    title: '系统配置',
    description: '灵活的系统配置选项，支持个性化定制，满足不同需求'
  },
  {
    id: 5,
    icon: BarChartOutlined,
    title: '数据分析',
    description: '强大的数据分析功能，多维度报表，助力业务决策'
  },
  {
    id: 6,
    icon: FileTextOutlined,
    title: '内容管理',
    description: '便捷的内容管理系统，支持富文本编辑，多媒体文件管理'
  },
  {
    id: 7,
    icon: BellOutlined,
    title: '消息通知',
    description: '实时消息推送，多渠道通知方式，确保重要信息及时传达'
  },
  {
    id: 8,
    icon: CloudOutlined,
    title: '云端部署',
    description: '支持云端部署，弹性扩容，高可用架构保障服务稳定'
  },
  {
    id: 9,
    icon: MobileOutlined,
    title: '移动适配',
    description: '响应式设计，完美适配各种设备，随时随地管理您的业务'
  }
];

// 统计数据
const statistics = [
  { key: 'users', value: 10000, label: '活跃用户' },
  { key: 'projects', value: 500, label: '成功项目' },
  { key: 'companies', value: 200, label: '合作企业' },
  { key: 'satisfaction', value: 99, label: '客户满意度' }
];

// 客户评价
const testimonials = [
  {
    id: 1,
    content: 'BearJia 管理系统极大地提升了我们的工作效率，界面简洁美观，功能强大实用。',
    name: '张总',
    position: 'ABC科技 CEO',
    avatar: 'https://randomuser.me/api/portraits/men/1.jpg'
  },
  {
    id: 2,
    content: '系统稳定可靠，客服响应及时，是我们数字化转型的得力助手。',
    name: '李经理',
    position: 'XYZ公司 运营总监',
    avatar: 'https://randomuser.me/api/portraits/women/2.jpg'
  },
  {
    id: 3,
    content: '权限管理功能非常完善，安全性高，完全满足我们企业的需求。',
    name: '王主管',
    position: '123集团 IT主管',
    avatar: 'https://randomuser.me/api/portraits/men/3.jpg'
  }
];

// 动画统计数据
const animatedStats = reactive({
  users: 0,
  projects: 0,
  companies: 0,
  satisfaction: 0
});

// 数字动画
const animateNumber = (key, target, duration = 2000) => {
  const start = 0;
  const increment = target / (duration / 16);
  let current = start;
  
  const timer = setInterval(() => {
    current += increment;
    if (current >= target) {
      current = target;
      clearInterval(timer);
    }
    animatedStats[key] = Math.floor(current);
  }, 16);
};

// 处理功能点击
const handleFeatureClick = (feature) => {
  console.log('Feature clicked:', feature.title);
};

// 跳转到演示
const goToDemo = () => {
  router.push('/');
};

// 跳转到管理后台
const goToAdmin = () => {
  router.push('/');
};

// 跳转到联系页面
const goToContact = () => {
  router.push('/frontend/contact');
};

// 监听滚动，触发动画
const handleScroll = () => {
  const statsSection = document.querySelector('.stats-section');
  if (statsSection) {
    const rect = statsSection.getBoundingClientRect();
    if (rect.top < window.innerHeight && rect.bottom > 0) {
      // 开始数字动画
      statistics.forEach(stat => {
        animateNumber(stat.key, stat.value);
      });
      // 移除监听器，避免重复触发
      window.removeEventListener('scroll', handleScroll);
    }
  }
};

onMounted(() => {
  window.addEventListener('scroll', handleScroll);
});

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
});
</script>

<style lang="less" scoped>
.home-page {
  overflow-x: hidden;
}

// Hero Section
.hero-section {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="50" cy="50" r="1" fill="rgba(255,255,255,0.1)"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>');
    opacity: 0.3;
  }

  .hero-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 24px;
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 60px;
    align-items: center;
    position: relative;
    z-index: 1;

    @media (max-width: 768px) {
      grid-template-columns: 1fr;
      gap: 40px;
      text-align: center;
    }
  }

  .hero-content {
    .hero-title {
      font-size: 3.5rem;
      font-weight: 700;
      color: white;
      margin-bottom: 24px;
      line-height: 1.2;

      @media (max-width: 768px) {
        font-size: 2.5rem;
      }

      .gradient-text {
        background: linear-gradient(45deg, #ffd700, #ffed4e);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
      }
    }

    .hero-description {
      font-size: 1.2rem;
      color: rgba(255, 255, 255, 0.9);
      margin-bottom: 40px;
      line-height: 1.6;
    }

    .hero-actions {
      display: flex;
      gap: 16px;

      @media (max-width: 768px) {
        flex-direction: column;
        align-items: center;
      }

      .cta-button {
        background: linear-gradient(45deg, #ff6b6b, #ee5a24);
        border: none;
        border-radius: 25px;
        height: 50px;
        padding: 0 32px;
        font-size: 16px;
        font-weight: 600;
        box-shadow: 0 4px 20px rgba(255, 107, 107, 0.4);
        transition: all 0.3s ease;

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 6px 25px rgba(255, 107, 107, 0.6);
        }
      }

      .demo-button {
        background: rgba(255, 255, 255, 0.1);
        border: 2px solid rgba(255, 255, 255, 0.3);
        color: white;
        border-radius: 25px;
        height: 50px;
        padding: 0 32px;
        font-size: 16px;
        font-weight: 600;
        backdrop-filter: blur(10px);
        transition: all 0.3s ease;

        &:hover {
          background: rgba(255, 255, 255, 0.2);
          border-color: rgba(255, 255, 255, 0.5);
          transform: translateY(-2px);
        }
      }
    }
  }

  .hero-visual {
    position: relative;
    height: 400px;

    .floating-cards {
      position: relative;
      width: 100%;
      height: 100%;

      .card {
        position: absolute;
        background: rgba(255, 255, 255, 0.1);
        backdrop-filter: blur(20px);
        border: 1px solid rgba(255, 255, 255, 0.2);
        border-radius: 16px;
        padding: 20px;
        color: white;
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 12px;
        font-size: 24px;
        font-weight: 600;
        animation: float 6s ease-in-out infinite;

        &.card-1 {
          top: 20%;
          left: 10%;
          animation-delay: 0s;
        }

        &.card-2 {
          top: 50%;
          right: 20%;
          animation-delay: 2s;
        }

        &.card-3 {
          bottom: 20%;
          left: 30%;
          animation-delay: 4s;
        }

        .anticon {
          font-size: 32px;
        }

        span {
          font-size: 14px;
        }
      }
    }
  }
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-20px); }
}

// Features Section
.features-section {
  padding: 100px 0;
  background: #f8fafc;

  .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 24px;
  }

  .section-header {
    text-align: center;
    margin-bottom: 60px;

    h2 {
      font-size: 2.5rem;
      font-weight: 700;
      color: #2d3748;
      margin-bottom: 16px;
    }

    p {
      font-size: 1.2rem;
      color: #718096;
    }
  }

  .features-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 30px;
  }

  .feature-card {
    background: white;
    padding: 40px 30px;
    border-radius: 16px;
    text-align: center;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    transition: all 0.3s ease;
    cursor: pointer;

    &:hover {
      transform: translateY(-8px);
      box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
    }

    .feature-icon {
      width: 80px;
      height: 80px;
      background: linear-gradient(135deg, var(--primary-color), #667eea);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      margin: 0 auto 24px;
      color: white;
      font-size: 32px;
    }

    h3 {
      font-size: 1.5rem;
      font-weight: 600;
      color: #2d3748;
      margin-bottom: 16px;
    }

    p {
      color: #718096;
      line-height: 1.6;
    }
  }
}

// Statistics Section
.stats-section {
  padding: 80px 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);

  .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 24px;
  }

  .stats-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 40px;
  }

  .stat-item {
    text-align: center;
    color: white;

    .stat-number {
      font-size: 3rem;
      font-weight: 700;
      margin-bottom: 8px;
    }

    .stat-label {
      font-size: 1.1rem;
      opacity: 0.9;
    }
  }
}

// Testimonials Section
.testimonials-section {
  padding: 100px 0;
  background: white;

  .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 24px;
  }

  .section-header {
    text-align: center;
    margin-bottom: 60px;

    h2 {
      font-size: 2.5rem;
      font-weight: 700;
      color: #2d3748;
      margin-bottom: 16px;
    }

    p {
      font-size: 1.2rem;
      color: #718096;
    }
  }

  .testimonials-carousel {
    max-width: 800px;
    margin: 0 auto;
  }

  .testimonial-slide {
    padding: 0 20px;
  }

  .testimonial-card {
    background: #f8fafc;
    padding: 40px;
    border-radius: 16px;
    text-align: center;

    .testimonial-content {
      margin-bottom: 30px;

      .quote-icon {
        font-size: 32px;
        color: var(--primary-color);
        margin-bottom: 20px;
      }

      p {
        font-size: 1.2rem;
        color: #4a5568;
        line-height: 1.6;
        font-style: italic;
      }
    }

    .testimonial-author {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 16px;

      img {
        width: 60px;
        height: 60px;
        border-radius: 50%;
        object-fit: cover;
      }

      h4 {
        font-size: 1.1rem;
        font-weight: 600;
        color: #2d3748;
        margin-bottom: 4px;
      }

      span {
        color: #718096;
        font-size: 0.9rem;
      }
    }
  }
}

// CTA Section
.cta-section {
  padding: 100px 0;
  background: linear-gradient(135deg, #2d3748 0%, #4a5568 100%);

  .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 24px;
  }

  .cta-content {
    text-align: center;
    color: white;

    h2 {
      font-size: 2.5rem;
      font-weight: 700;
      margin-bottom: 16px;
    }

    p {
      font-size: 1.2rem;
      opacity: 0.9;
      margin-bottom: 40px;
    }

    .cta-actions {
      display: flex;
      gap: 16px;
      justify-content: center;

      @media (max-width: 768px) {
        flex-direction: column;
        align-items: center;
      }

      .ant-btn {
        height: 50px;
        padding: 0 32px;
        font-size: 16px;
        font-weight: 600;
        border-radius: 25px;
      }

      .ant-btn-primary {
        background: linear-gradient(45deg, #ff6b6b, #ee5a24);
        border: none;
        box-shadow: 0 4px 20px rgba(255, 107, 107, 0.4);

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 6px 25px rgba(255, 107, 107, 0.6);
        }
      }
    }
  }
}

// 暗色主题适配
:global(.dark-theme) {
  .features-section {
    background: #1a1a1a;

    .section-header h2 {
      color: #fff;
    }

    .section-header p {
      color: #ccc;
    }

    .feature-card {
      background: #2a2a2a;

      h3 {
        color: #fff;
      }

      p {
        color: #ccc;
      }
    }
  }

  .testimonials-section {
    background: #141414;

    .section-header h2 {
      color: #fff;
    }

    .section-header p {
      color: #ccc;
    }

    .testimonial-card {
      background: #2a2a2a;

      .testimonial-content p {
        color: #ccc;
      }

      .testimonial-author h4 {
        color: #fff;
      }

      .testimonial-author span {
        color: #999;
      }
    }
  }
}
</style>
