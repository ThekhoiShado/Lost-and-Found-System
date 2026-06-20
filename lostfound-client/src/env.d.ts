/// <reference types="vite/client" />

declare module '*.vue' { // 定义 Vue 组件模块类型
  import type { DefineComponent } from 'vue'
  const component: DefineComponent<{}, {}, any>
  export default component
}

// @wangeditor/editor-for-vue 的类型声明（包的 exports 字段与类型路径不兼容）
declare module '@wangeditor/editor-for-vue' { // 定义 WangedEditor 组件模块类型
  import type { Component } from 'vue' // 引入 Vue 组件类型
  import type { IDomEditor, IToolbarConfig, IEditorConfig } from '@wangeditor/editor'

  export const Editor: Component // WangedEditor 组件类型
  export const Toolbar: Component // WangedEditor 工具栏组件类型
}

interface ImportMetaEnv { // 定义环境变量类型
  readonly VITE_API_BASE_URL: string // API 基础 URL
  readonly VITE_UPLOAD_URL: string // 上传 URL
}

interface ImportMeta { // 定义导入元数据类型
  readonly env: ImportMetaEnv // 环境变量实例
}
