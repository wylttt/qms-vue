<template>
  <div style="border: 1px solid #ccc;border-radius: 6px;">
    <!-- 工具栏 -->
    <Toolbar
      style="border-bottom: 1px solid #fafbfc"
      :editor="editorRef"
      :defaultConfig="toolbarConfig"
      :mode="mode"
    />
    <!-- 编辑器 -->
    <Editor
      :style="{ height: editorHeight + 'px', 'overflow-y': 'hidden' }"
      v-model="valueHtml"
      :defaultConfig="editorConfig"
      :mode="mode"
      @onCreated="handleCreated"
      @onChange="handleChange"
      @onDestroyed="handleDestroyed"
      @onFocus="handleFocus"
      @onBlur="handleBlur"
      @customAlert="handleAlert"
    />
  </div>
</template>

<script setup>
import '@wangeditor/editor/dist/css/style.css';
import { onBeforeUnmount, ref, shallowRef, onMounted, watch } from 'vue';
import { Editor, Toolbar } from '@wangeditor/editor-for-vue';
import { message } from 'ant-design-vue';
import { getToken } from '@/utils/auth.js';

// 定义 Props
const props = defineProps({
  value: {
    type: String,
    default: '',
  },
  height: {
    type: Number,
    default: 400,
  },
  readOnly: {
    type: Boolean,
    default: false,
  },
  placeholder: {
    type: String,
    default: '请输入内容...',
  },
  imageSize: {
    type: Number,
    default: 5, // MB
  },
  videoSize: {
    type: Number,
    default: 50, // MB
  },
});

// 定义 Emits
const emit = defineEmits(['update:value', 'change']);

// 编辑器实例，必须用 shallowRef
const editorRef = shallowRef();

// 内容 HTML
const valueHtml = ref(props.value || '');

// 编辑器高度
const editorHeight = ref(props.height);

// 模式
const mode = ref('default'); // 或 'simple'

// 工具栏配置
const toolbarConfig = {
  excludeKeys: props.readOnly ? [] : undefined, // 只读模式下排除所有工具
};

// 编辑器配置
const editorConfig = {
  placeholder: props.placeholder,
  readOnly: props.readOnly,
  MENU_CONF: {
    // 配置上传图片
    uploadImage: {
      server: `${import.meta.env.VITE_APP_BASE_API}/common/upload`,
      fieldName: 'file',
      maxFileSize: props.imageSize * 1024 * 1024, // 5M
      maxNumberOfFiles: 10,
      allowedFileTypes: ['image/*'],
      headers: {
        Authorization: 'Bearer ' + getToken(),
      },
      customInsert(res, insertFn) {
        // res 即服务端的返回结果
        if (res.code === 200) {
          // 从 res 中找到 url alt href ，然后插入图片
          insertFn(res.url, res.fileName, res.url);
        } else {
          message.error(res.msg || '图片上传失败');
        }
      },
    },
    // 配置上传视频
    uploadVideo: {
      server: `${import.meta.env.VITE_APP_BASE_API}/common/upload`,
      fieldName: 'file',
      maxFileSize: props.videoSize * 1024 * 1024, // 50M
      maxNumberOfFiles: 3,
      allowedFileTypes: ['video/*'],
      headers: {
        Authorization: 'Bearer ' + getToken(),
      },
      customInsert(res, insertFn) {
        // res 即服务端的返回结果
        if (res.code === 200) {
          // 从 res 中找到 url ，然后插入视频
          insertFn(res.url, res.fileName);
        } else {
          message.error(res.msg || '视频上传失败');
        }
      },
    },
  },
};

// 监听 props.value 变化
watch(
  () => props.value,
  (newVal) => {
    if (newVal !== valueHtml.value) {
      valueHtml.value = newVal || '';
    }
  },
  { immediate: true }
);

// 编辑器回调函数
const handleCreated = (editor) => {
  editorRef.value = editor; // 记录 editor 实例，重要！
};

const handleChange = (editor) => {
  const html = editor.getHtml();
  emit('update:value', html);
  emit('change', html);
};

const handleDestroyed = (editor) => {
  console.log('destroyed', editor);
};

const handleFocus = (editor) => {
  console.log('focus', editor);
};

const handleBlur = (editor) => {
  console.log('blur', editor);
};

const handleAlert = (info, type) => {
  if (type === 'error') {
    message.error(info);
  } else if (type === 'warning') {
    message.warning(info);
  } else {
    message.info(info);
  }
};

// 组件销毁时，也及时销毁编辑器
onBeforeUnmount(() => {
  const editor = editorRef.value;
  if (editor == null) return;
  editor.destroy();
});
</script>

<style lang="less" scoped>
// 编辑器整体样式
:deep(.w-e-toolbar) {
  border-bottom: 1px solid #e8e8e8 !important;
  background: #fafafa;

  .w-e-toolbar-item {
    &:hover {
      background: #e6f7ff;
    }

    &.w-e-active {
      background: #1890ff;
      color: white;
    }
  }
}

:deep(.w-e-text-container) {
  .w-e-text {
    font-size: 14px;
    line-height: 1.6;

    p {
      margin: 8px 0;
    }

    h1, h2, h3, h4, h5, h6 {
      margin: 16px 0 8px 0;
      font-weight: 600;
    }

    ul, ol {
      margin: 8px 0;
      padding-left: 20px;
    }

    blockquote {
      margin: 16px 0;
      padding: 12px 16px;
      background: #f6f8fa;
      border-left: 4px solid #1890ff;
    }

    code {
      padding: 2px 4px;
      background: #f5f5f5;
      border-radius: 3px;
      color: #e74c3c;
    }

    pre {
      margin: 16px 0;
      padding: 12px;
      background: #f8f8f8;
      border-radius: 6px;
      overflow-x: auto;
    }

    img {
      max-width: 100%;
      height: auto;
      border-radius: 4px;
      margin: 8px 0;
    }

    video {
      max-width: 100%;
      height: auto;
      border-radius: 4px;
      margin: 8px 0;
    }

    table {
      width: 100%;
      border-collapse: collapse;
      margin: 16px 0;

      th, td {
        padding: 8px 12px;
        border: 1px solid #e8e8e8;
        text-align: left;
      }

      th {
        background: #fafafa;
        font-weight: 600;
      }
    }
  }
}
</style>
