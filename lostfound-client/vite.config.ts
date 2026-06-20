import { fileURLToPath, URL } from 'node:url' // 引入 URL 模块, 用于处理文件路径
import { defineConfig } from 'vite' // 引入 Vite 配置函数
import vue from '@vitejs/plugin-vue' // 引入 Vue 插件

// Vite 配置
export default defineConfig({ // 导出 Vite 配置
  plugins: [vue()], // 插件数组, 包含 Vue 插件
  resolve: { // 解析选项
    alias: { // 别名选项
      '@': fileURLToPath(new URL('./src', import.meta.url)) // 别名 @ 到 ./src 目录
    }
  },
  server: { // 开发服务器配置
    port: 5173, // 服务器端口
    proxy: { // 代理选项
      '/api': { // 代理 /api 路径
        target: 'http://localhost:8080', // 代理目标地址
        changeOrigin: true // 改变源地址, 用于跨域请求时的重定向
      },
      '/uploads': { // 代理 /uploads 路径
        target: 'http://localhost:8080', // 代理目标地址
        changeOrigin: true // 改变源地址, 用于跨域请求时的重定向
      }
    }
  }
})
