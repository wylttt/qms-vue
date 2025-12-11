<template>
  <div class="about-page">
    <!-- Hero Section -->
    <section class="hero-section">
      <div class="container">
        <div class="hero-content">
          <h1>关于 BearJia</h1>
          <p>专业的企业级管理系统解决方案提供商</p>
        </div>
      </div>
    </section>

    <!-- Company Info Section -->
    <section class="company-section">
      <div class="container">
        <div class="company-grid">
          <div class="company-content">
            <h2>我们的使命</h2>
            <p>
              BearJia 致力于为企业提供最优质的管理系统解决方案，
              通过先进的技术和专业的服务，帮助企业实现数字化转型，
              提升管理效率，降低运营成本。
            </p>
            <div class="values">
              <div class="value-item">
                <innovation-outlined />
                <div>
                  <h4>创新驱动</h4>
                  <p>持续技术创新，引领行业发展</p>
                </div>
              </div>
              <div class="value-item">
                <customer-service-outlined />
                <div>
                  <h4>客户至上</h4>
                  <p>以客户需求为导向，提供优质服务</p>
                </div>
              </div>
              <div class="value-item">
                <team-outlined />
                <div>
                  <h4>团队协作</h4>
                  <p>专业团队，协作共赢</p>
                </div>
              </div>
            </div>
          </div>
          <div class="company-image">
            <img src="https://images.unsplash.com/photo-1560472354-b33ff0c44a43?w=600&h=400&fit=crop" alt="公司团队" />
          </div>
        </div>
      </div>
    </section>

    <!-- Team Section -->
    <section class="team-section">
      <div class="container">
        <div class="section-header">
          <h2>核心团队</h2>
          <p>专业的技术团队，为您提供可靠的服务保障</p>
        </div>
        <div class="team-grid">
          <div v-for="member in teamMembers" :key="member.id" class="team-card">
            <div class="member-avatar">
              <img :src="member.avatar" :alt="member.name" />
            </div>
            <h3>{{ member.name }}</h3>
            <p class="member-position">{{ member.position }}</p>
            <p class="member-description">{{ member.description }}</p>
            <div class="member-social">
              <a href="#" class="social-link">
                <linkedin-outlined />
              </a>
              <a href="#" class="social-link">
                <github-outlined />
              </a>
              <a href="#" class="social-link">
                <twitter-outlined />
              </a>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Timeline Section -->
    <section class="timeline-section">
      <div class="container">
        <div class="section-header">
          <h2>发展历程</h2>
          <p>见证我们的成长足迹</p>
        </div>
        <div class="timeline">
          <div v-for="(event, index) in timeline" :key="event.id" class="timeline-item" :class="{ 'timeline-item-right': index % 2 === 1 }">
            <div class="timeline-content">
              <div class="timeline-year">{{ event.year }}</div>
              <h3>{{ event.title }}</h3>
              <p>{{ event.description }}</p>
            </div>
            <div class="timeline-dot"></div>
          </div>
        </div>
      </div>
    </section>

    <!-- Contact CTA Section -->
    <section class="contact-cta-section">
      <div class="container">
        <div class="cta-content">
          <h2>想了解更多？</h2>
          <p>联系我们，获取专业的解决方案</p>
          <a-button type="primary" size="large" @click="goToContact">
            <template #icon>
              <phone-outlined />
            </template>
            联系我们
          </a-button>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';
import {
  CustomerServiceOutlined,
  TeamOutlined,
  LinkedinOutlined,
  GithubOutlined,
  TwitterOutlined,
  PhoneOutlined
} from '@ant-design/icons-vue';

const router = useRouter();

// 团队成员数据
const teamMembers = [
  {
    id: 1,
    name: '张三',
    position: '技术总监',
    description: '10年以上企业级系统开发经验，专注于系统架构设计',
    avatar: 'https://randomuser.me/api/portraits/men/1.jpg'
  },
  {
    id: 2,
    name: '李四',
    position: '产品经理',
    description: '资深产品经理，深度理解企业管理需求',
    avatar: 'https://randomuser.me/api/portraits/women/2.jpg'
  },
  {
    id: 3,
    name: '王五',
    position: '前端架构师',
    description: '前端技术专家，致力于用户体验优化',
    avatar: 'https://randomuser.me/api/portraits/men/3.jpg'
  },
  {
    id: 4,
    name: '赵六',
    position: '后端工程师',
    description: '后端开发专家，专注于系统性能优化',
    avatar: 'https://randomuser.me/api/portraits/women/4.jpg'
  }
];

// 发展历程数据
const timeline = [
  {
    id: 1,
    year: '2020',
    title: '公司成立',
    description: 'BearJia 正式成立，开始专注于企业管理系统开发'
  },
  {
    id: 2,
    year: '2021',
    title: '产品发布',
    description: '第一版管理系统正式发布，获得市场认可'
  },
  {
    id: 3,
    year: '2022',
    title: '快速发展',
    description: '客户数量突破100家，团队规模扩大到50人'
  },
  {
    id: 4,
    year: '2023',
    title: '技术升级',
    description: '全面升级技术架构，推出云原生解决方案'
  },
  {
    id: 5,
    year: '2024',
    title: '持续创新',
    description: '引入AI技术，打造智能化管理平台'
  }
];

// 跳转到联系页面
const goToContact = () => {
  router.push('/frontend/contact');
};
</script>

<style lang="less" scoped>
.about-page {
  .hero-section {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    padding: 120px 0 80px;
    text-align: center;
    color: white;

    .container {
      max-width: 1200px;
      margin: 0 auto;
      padding: 0 24px;
    }

    h1 {
      font-size: 3rem;
      font-weight: 700;
      margin-bottom: 20px;
    }

    p {
      font-size: 1.3rem;
      opacity: 0.9;
    }
  }

  .company-section {
    padding: 100px 0;
    background: white;

    .container {
      max-width: 1200px;
      margin: 0 auto;
      padding: 0 24px;
    }

    .company-grid {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 60px;
      align-items: center;

      @media (max-width: 768px) {
        grid-template-columns: 1fr;
        gap: 40px;
      }
    }

    .company-content {
      h2 {
        font-size: 2.5rem;
        font-weight: 700;
        color: #2d3748;
        margin-bottom: 24px;
      }

      p {
        font-size: 1.1rem;
        color: #718096;
        line-height: 1.8;
        margin-bottom: 40px;
      }

      .values {
        display: flex;
        flex-direction: column;
        gap: 24px;

        .value-item {
          display: flex;
          align-items: flex-start;
          gap: 16px;

          .anticon {
            font-size: 24px;
            color: var(--primary-color);
            margin-top: 4px;
          }

          h4 {
            font-size: 1.2rem;
            font-weight: 600;
            color: #2d3748;
            margin-bottom: 8px;
          }

          p {
            color: #718096;
            margin: 0;
          }
        }
      }
    }

    .company-image {
      img {
        width: 100%;
        height: 400px;
        object-fit: cover;
        border-radius: 16px;
        box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
      }
    }
  }

  .team-section {
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

    .team-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
      gap: 30px;
    }

    .team-card {
      background: white;
      padding: 40px 30px;
      border-radius: 16px;
      text-align: center;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
      transition: all 0.3s ease;

      &:hover {
        transform: translateY(-8px);
        box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
      }

      .member-avatar {
        margin-bottom: 24px;

        img {
          width: 100px;
          height: 100px;
          border-radius: 50%;
          object-fit: cover;
          border: 4px solid var(--primary-color);
        }
      }

      h3 {
        font-size: 1.5rem;
        font-weight: 600;
        color: #2d3748;
        margin-bottom: 8px;
      }

      .member-position {
        color: var(--primary-color);
        font-weight: 600;
        margin-bottom: 16px;
      }

      .member-description {
        color: #718096;
        line-height: 1.6;
        margin-bottom: 24px;
      }

      .member-social {
        display: flex;
        justify-content: center;
        gap: 12px;

        .social-link {
          width: 40px;
          height: 40px;
          background: #f8fafc;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          color: #718096;
          text-decoration: none;
          transition: all 0.3s ease;

          &:hover {
            background: var(--primary-color);
            color: white;
            transform: translateY(-2px);
          }
        }
      }
    }
  }

  .timeline-section {
    padding: 100px 0;
    background: white;

    .container {
      max-width: 1200px;
      margin: 0 auto;
      padding: 0 24px;
    }

    .section-header {
      text-align: center;
      margin-bottom: 80px;

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

    .timeline {
      position: relative;
      max-width: 800px;
      margin: 0 auto;

      &::before {
        content: '';
        position: absolute;
        left: 50%;
        top: 0;
        bottom: 0;
        width: 2px;
        background: var(--primary-color);
        transform: translateX(-50%);
      }

      .timeline-item {
        position: relative;
        margin-bottom: 60px;
        display: flex;
        align-items: center;

        &:last-child {
          margin-bottom: 0;
        }

        .timeline-content {
          background: white;
          padding: 30px;
          border-radius: 16px;
          box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
          width: 45%;
          position: relative;

          .timeline-year {
            background: var(--primary-color);
            color: white;
            padding: 8px 16px;
            border-radius: 20px;
            font-weight: 600;
            display: inline-block;
            margin-bottom: 16px;
          }

          h3 {
            font-size: 1.3rem;
            font-weight: 600;
            color: #2d3748;
            margin-bottom: 12px;
          }

          p {
            color: #718096;
            line-height: 1.6;
          }
        }

        .timeline-dot {
          position: absolute;
          left: 50%;
          width: 16px;
          height: 16px;
          background: var(--primary-color);
          border-radius: 50%;
          transform: translateX(-50%);
          z-index: 1;
        }

        &.timeline-item-right {
          flex-direction: row-reverse;

          .timeline-content {
            margin-left: auto;
          }
        }

        @media (max-width: 768px) {
          flex-direction: column !important;

          .timeline-content {
            width: 100% !important;
            margin: 0 !important;
          }

          .timeline-dot {
            position: relative;
            left: auto;
            transform: none;
            margin: 20px 0;
          }
        }
      }

      @media (max-width: 768px) {
        &::before {
          display: none;
        }
      }
    }
  }

  .contact-cta-section {
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

      .ant-btn {
        height: 50px;
        padding: 0 32px;
        font-size: 16px;
        font-weight: 600;
        border-radius: 25px;
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
  .about-page {
    .company-section {
      background: #1a1a1a;

      .company-content {
        h2 {
          color: #fff;
        }

        p {
          color: #ccc;
        }

        .values .value-item {
          h4 {
            color: #fff;
          }

          p {
            color: #ccc;
          }
        }
      }
    }

    .team-section {
      background: #141414;

      .section-header {
        h2 {
          color: #fff;
        }

        p {
          color: #ccc;
        }
      }

      .team-card {
        background: #2a2a2a;

        h3 {
          color: #fff;
        }

        .member-description {
          color: #ccc;
        }
      }
    }

    .timeline-section {
      background: #1a1a1a;

      .section-header {
        h2 {
          color: #fff;
        }

        p {
          color: #ccc;
        }
      }

      .timeline-item .timeline-content {
        background: #2a2a2a;

        h3 {
          color: #fff;
        }

        p {
          color: #ccc;
        }
      }
    }
  }
}
</style>
