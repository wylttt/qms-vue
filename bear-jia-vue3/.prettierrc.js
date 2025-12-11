module.exports = {
  // 基础配置
  printWidth: 100, // 每行最大字符数
  tabWidth: 2, // 缩进空格数
  useTabs: false, // 使用空格缩进
  semi: true, // 语句末尾使用分号
  singleQuote: true, // 使用单引号
  quoteProps: 'as-needed', // 对象属性按需加引号
  jsxSingleQuote: false, // JSX中使用双引号
  
  // 尾随逗号配置
  trailingComma: 'only-multiline', // 多行时才添加尾随逗号
  
  // 括号配置
  bracketSpacing: true, // 对象字面量的括号之间添加空格
  bracketSameLine: false, // 将多行HTML元素的>放在最后一行的末尾
  
  // 箭头函数配置
  arrowParens: 'always', // 箭头函数参数总是使用括号
  
  // 格式化范围
  rangeStart: 0,
  rangeEnd: Infinity,
  
  // Vue文件配置
  vueIndentScriptAndStyle: false, // 不缩进Vue文件中的script和style标签
  
  // HTML配置
  htmlWhitespaceSensitivity: 'css', // HTML空格敏感度
  
  // 换行符配置
  endOfLine: 'auto', // 自动检测换行符
  
  // 嵌入式语言格式化
  embeddedLanguageFormatting: 'auto', // 自动格式化嵌入的代码
  
  // 特定文件覆盖配置
  overrides: [
    {
      files: '*.vue',
      options: {
        parser: 'vue'
      }
    },
    {
      files: ['*.json', '.eslintrc', '.prettierrc'],
      options: {
        parser: 'json',
        tabWidth: 2
      }
    },
    {
      files: '*.md',
      options: {
        parser: 'markdown',
        proseWrap: 'preserve'
      }
    },
    {
      files: '*.yaml',
      options: {
        parser: 'yaml',
        tabWidth: 2
      }
    }
  ],
  
  // 忽略的文件和目录（从 .prettierignore 合并而来）
  // 注意：Prettier 使用 .prettierignore 文件或 package.json 中的配置
  // 这里仅作为文档记录，实际忽略需要通过其他方式配置
  /*
  ignorePatterns: [
    'node_modules',
    'dist',
    'public',
    'docs',
    '*.min.js',
    '*.min.css',
    'package-lock.json',
    '.vscode',
    '.idea'
  ]
  */
};

// 导出忽略模式供工具使用
module.exports.prettierIgnore = [
  'node_modules',
  'dist',
  'public',
  'docs',
  '*.min.js',
  '*.min.css',
  'package-lock.json',
  '.vscode',
  '.idea'
];