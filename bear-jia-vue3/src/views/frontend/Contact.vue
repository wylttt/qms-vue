<template>
  <div class="contact-page">
    <!-- Hero Section -->
    <section class="hero-section">
      <div class="container">
        <div class="hero-content">
          <h1>联系我们</h1>
          <p>我们随时为您提供专业的服务和支持</p>
        </div>
      </div>
    </section>

    <!-- Contact Section -->
    <section class="contact-section">
      <div class="container">
        <div class="contact-grid">
          <!-- 联系信息 -->
          <div class="contact-info">
            <h2>联系方式</h2>
            <p>欢迎通过以下方式与我们取得联系</p>
            
            <div class="contact-items">
              <div class="contact-item">
                <div class="contact-icon">
                  <phone-outlined />
                </div>
                <div class="contact-details">
                  <h4>电话咨询</h4>
                  <p>400-123-4567</p>
                  <span>工作日 9:00-18:00</span>
                </div>
              </div>

              <div class="contact-item">
                <div class="contact-icon">
                  <mail-outlined />
                </div>
                <div class="contact-details">
                  <h4>邮箱联系</h4>
                  <p>contact@bearjia.com</p>
                  <span>24小时内回复</span>
                </div>
              </div>

              <div class="contact-item">
                <div class="contact-icon">
                  <environment-outlined />
                </div>
                <div class="contact-details">
                  <h4>公司地址</h4>
                  <p>北京市朝阳区xxx大厦</p>
                  <span>欢迎预约参观</span>
                </div>
              </div>

              <div class="contact-item">
                <div class="contact-icon">
                  <wechat-outlined />
                </div>
                <div class="contact-details">
                  <h4>微信客服</h4>
                  <p>BearJia_Service</p>
                  <span>扫码添加客服</span>
                </div>
              </div>
            </div>

            <!-- 社交媒体 -->
            <div class="social-section">
              <h4>关注我们</h4>
              <div class="social-links">
                <a href="#" class="social-link">
                  <wechat-outlined />
                  <span>微信公众号</span>
                </a>
                <a href="#" class="social-link">
                  <weibo-outlined />
                  <span>官方微博</span>
                </a>
                <a href="#" class="social-link">
                  <github-outlined />
                  <span>GitHub</span>
                </a>
              </div>
            </div>
          </div>

          <!-- 联系表单 -->
          <div class="contact-form">
            <h2>在线咨询</h2>
            <p>填写表单，我们将尽快与您联系</p>
            
            <a-form
              ref="formRef"
              :model="formData"
              :rules="formRules"
              layout="vertical"
              @finish="handleSubmit"
            >
              <a-row :gutter="16">
                <a-col :span="12">
                  <a-form-item label="姓名" name="name">
                    <a-input v-model:value="formData.name" placeholder="请输入您的姓名" />
                  </a-form-item>
                </a-col>
                <a-col :span="12">
                  <a-form-item label="公司" name="company">
                    <a-input v-model:value="formData.company" placeholder="请输入公司名称" />
                  </a-form-item>
                </a-col>
              </a-row>

              <a-row :gutter="16">
                <a-col :span="12">
                  <a-form-item label="手机号" name="phone">
                    <a-input v-model:value="formData.phone" placeholder="请输入手机号" />
                  </a-form-item>
                </a-col>
                <a-col :span="12">
                  <a-form-item label="邮箱" name="email">
                    <a-input v-model:value="formData.email" placeholder="请输入邮箱地址" />
                  </a-form-item>
                </a-col>
              </a-row>

              <a-form-item label="咨询类型" name="type">
                <a-select v-model:value="formData.type" placeholder="请选择咨询类型">
                  <a-select-option value="product">产品咨询</a-select-option>
                  <a-select-option value="price">价格咨询</a-select-option>
                  <a-select-option value="demo">演示申请</a-select-option>
                  <a-select-option value="support">技术支持</a-select-option>
                  <a-select-option value="cooperation">商务合作</a-select-option>
                  <a-select-option value="other">其他</a-select-option>
                </a-select>
              </a-form-item>

              <a-form-item label="详细需求" name="message">
                <a-textarea 
                  v-model:value="formData.message" 
                  placeholder="请详细描述您的需求或问题"
                  :rows="4"
                />
              </a-form-item>

              <a-form-item>
                <a-button 
                  type="primary" 
                  html-type="submit" 
                  size="large"
                  :loading="submitting"
                  block
                >
                  <template #icon>
                    <send-outlined />
                  </template>
                  提交咨询
                </a-button>
              </a-form-item>
            </a-form>
          </div>
        </div>
      </div>
    </section>

    <!-- Map Section -->
    <section class="map-section">
      <div class="container">
        <h2>公司位置</h2>
        <div class="map-container">
          <div class="map-placeholder">
            <environment-outlined />
            <p>地图加载中...</p>
            <small>北京市朝阳区xxx大厦</small>
          </div>
        </div>
      </div>
    </section>

    <!-- FAQ Section -->
    <section class="faq-section">
      <div class="container">
        <div class="section-header">
          <h2>常见问题</h2>
          <p>以下是客户经常咨询的问题</p>
        </div>
        <div class="faq-content">
          <a-collapse v-model:activeKey="activeKeys" ghost>
            <a-collapse-panel v-for="faq in faqs" :key="faq.id" :header="faq.question">
              <p>{{ faq.answer }}</p>
            </a-collapse-panel>
          </a-collapse>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { message } from 'ant-design-vue';
import {
  PhoneOutlined,
  MailOutlined,
  EnvironmentOutlined,
  WechatOutlined,
  WeiboOutlined,
  GithubOutlined,
  SendOutlined
} from '@ant-design/icons-vue';

// 表单数据
const formRef = ref();
const submitting = ref(false);
const formData = reactive({
  name: '',
  company: '',
  phone: '',
  email: '',
  type: '',
  message: ''
});

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入您的姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择咨询类型', trigger: 'change' }
  ],
  message: [
    { required: true, message: '请描述您的需求', trigger: 'blur' }
  ]
};

// FAQ数据
const activeKeys = ref(['1']);
const faqs = [
  {
    id: '1',
    question: '系统支持哪些浏览器？',
    answer: '我们的系统支持Chrome、Firefox、Safari、Edge等主流浏览器的最新版本。建议使用Chrome浏览器以获得最佳体验。'
  },
  {
    id: '2',
    question: '是否提供免费试用？',
    answer: '是的，我们提供30天的免费试用期。您可以在试用期内体验所有功能，无需支付任何费用。'
  },
  {
    id: '3',
    question: '数据安全如何保障？',
    answer: '我们采用银行级别的安全措施，包括数据加密传输、定期备份、权限控制等，确保您的数据安全。'
  },
  {
    id: '4',
    question: '是否支持定制开发？',
    answer: '是的，我们提供定制开发服务。可以根据您的具体需求进行功能定制和界面调整。'
  },
  {
    id: '5',
    question: '技术支持服务如何？',
    answer: '我们提供7×24小时技术支持服务，包括在线客服、电话支持、远程协助等多种方式。'
  }
];

// 提交表单
const handleSubmit = async () => {
  try {
    submitting.value = true;
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 2000));
    
    message.success('咨询提交成功！我们将尽快与您联系。');
    
    // 重置表单
    formRef.value.resetFields();
  } catch (error) {
    message.error('提交失败，请稍后重试');
  } finally {
    submitting.value = false;
  }
};
</script>

<style lang="less" scoped>
.contact-page {
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

  .contact-section {
    padding: 100px 0;
    background: white;

    .container {
      max-width: 1200px;
      margin: 0 auto;
      padding: 0 24px;
    }

    .contact-grid {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 80px;

      @media (max-width: 768px) {
        grid-template-columns: 1fr;
        gap: 60px;
      }
    }

    .contact-info {
      h2 {
        font-size: 2.2rem;
        font-weight: 700;
        color: #2d3748;
        margin-bottom: 16px;
      }

      > p {
        font-size: 1.1rem;
        color: #718096;
        margin-bottom: 40px;
      }

      .contact-items {
        margin-bottom: 50px;

        .contact-item {
          display: flex;
          align-items: flex-start;
          gap: 20px;
          margin-bottom: 30px;

          .contact-icon {
            width: 60px;
            height: 60px;
            background: linear-gradient(135deg, var(--primary-color), #667eea);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 24px;
            flex-shrink: 0;
          }

          .contact-details {
            h4 {
              font-size: 1.2rem;
              font-weight: 600;
              color: #2d3748;
              margin-bottom: 8px;
            }

            p {
              font-size: 1.1rem;
              color: #4a5568;
              margin-bottom: 4px;
            }

            span {
              color: #718096;
              font-size: 0.9rem;
            }
          }
        }
      }

      .social-section {
        h4 {
          font-size: 1.2rem;
          font-weight: 600;
          color: #2d3748;
          margin-bottom: 20px;
        }

        .social-links {
          display: flex;
          gap: 16px;

          .social-link {
            display: flex;
            align-items: center;
            gap: 8px;
            padding: 12px 20px;
            background: #f8fafc;
            border-radius: 25px;
            text-decoration: none;
            color: #4a5568;
            transition: all 0.3s ease;

            &:hover {
              background: var(--primary-color);
              color: white;
              transform: translateY(-2px);
            }

            .anticon {
              font-size: 18px;
            }

            span {
              font-size: 0.9rem;
              font-weight: 500;
            }
          }
        }
      }
    }

    .contact-form {
      background: #f8fafc;
      padding: 40px;
      border-radius: 16px;

      h2 {
        font-size: 2.2rem;
        font-weight: 700;
        color: #2d3748;
        margin-bottom: 16px;
      }

      > p {
        font-size: 1.1rem;
        color: #718096;
        margin-bottom: 30px;
      }

      .ant-form-item-label > label {
        font-weight: 600;
        color: #2d3748;
      }

      .ant-btn-primary {
        background: linear-gradient(45deg, var(--primary-color), #667eea);
        border: none;
        height: 50px;
        font-size: 16px;
        font-weight: 600;
        border-radius: 25px;

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 6px 25px rgba(102, 126, 234, 0.4);
        }
      }
    }
  }

  .map-section {
    padding: 100px 0;
    background: #f8fafc;

    .container {
      max-width: 1200px;
      margin: 0 auto;
      padding: 0 24px;
    }

    h2 {
      font-size: 2.2rem;
      font-weight: 700;
      color: #2d3748;
      text-align: center;
      margin-bottom: 40px;
    }

    .map-container {
      border-radius: 16px;
      overflow: hidden;
      box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);

      .map-placeholder {
        height: 400px;
        background: #e2e8f0;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        color: #718096;

        .anticon {
          font-size: 48px;
          margin-bottom: 16px;
        }

        p {
          font-size: 1.1rem;
          margin-bottom: 8px;
        }

        small {
          font-size: 0.9rem;
        }
      }
    }
  }

  .faq-section {
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

    .faq-content {
      max-width: 800px;
      margin: 0 auto;

      :deep(.ant-collapse-header) {
        font-size: 1.1rem;
        font-weight: 600;
        color: #2d3748;
      }

      :deep(.ant-collapse-content-box) {
        p {
          color: #718096;
          line-height: 1.6;
          margin: 0;
        }
      }
    }
  }
}

// 暗色主题适配
:global(.dark-theme) {
  .contact-page {
    .contact-section {
      background: #1a1a1a;

      .contact-info {
        h2 {
          color: #fff;
        }

        > p {
          color: #ccc;
        }

        .contact-items .contact-item .contact-details {
          h4 {
            color: #fff;
          }

          p {
            color: #ccc;
          }

          span {
            color: #999;
          }
        }

        .social-section h4 {
          color: #fff;
        }
      }

      .contact-form {
        background: #2a2a2a;

        h2 {
          color: #fff;
        }

        > p {
          color: #ccc;
        }

        :deep(.ant-form-item-label > label) {
          color: #fff;
        }
      }
    }

    .map-section {
      background: #141414;

      h2 {
        color: #fff;
      }
    }

    .faq-section {
      background: #1a1a1a;

      .section-header {
        h2 {
          color: #fff;
        }

        p {
          color: #ccc;
        }
      }

      .faq-content {
        :deep(.ant-collapse-header) {
          color: #fff;
        }

        :deep(.ant-collapse-content-box) {
          p {
            color: #ccc;
          }
        }
      }
    }
  }
}
</style>
