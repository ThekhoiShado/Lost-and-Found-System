import { createApp } from 'vue' // 引入 Vue 创建应用函数
import { createPinia } from 'pinia'   // 引入 Pinia 创建应用函数
import ElementPlus from 'element-plus' // 引入 Element Plus UI 库
import 'element-plus/dist/index.css'  // 引入 Element Plus 样式
import zhCn from 'element-plus/es/locale/lang/zh-cn' // 引入 Element Plus 中文语言包
import * as ElementPlusIconsVue from '@element-plus/icons-vue' // 引入 Element Plus 图标组件库

import App from './App.vue' // 引入 App 组件
import router from './router' // 引入 Vue Router 实例
import './assets/styles/global.css' // 引入全局样式

// 创建 Vue 应用实例
const app = createApp(App)

// 注册 Element Plus 图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(createPinia()) // 使用 Pinia 状态管理
app.use(router) // 使用 Vue Router 实例
app.use(ElementPlus, { locale: zhCn }) // 使用 Element Plus 库

app.mount('#app') // 挂载 Vue 应用到 #app 元素
