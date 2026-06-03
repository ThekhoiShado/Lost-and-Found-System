/// <reference types="vite/client" />

declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  const component: DefineComponent<{}, {}, any>
  export default component
}

// @wangeditor/editor-for-vue 的类型声明（包的 exports 字段与类型路径不兼容）
declare module '@wangeditor/editor-for-vue' {
  import type { Component } from 'vue'
  import type { IDomEditor, IToolbarConfig, IEditorConfig } from '@wangeditor/editor'

  export const Editor: Component
  export const Toolbar: Component
}

interface ImportMetaEnv {
  readonly VITE_API_BASE_URL: string
  readonly VITE_UPLOAD_URL: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}
