import { createRouter, createWebHistory } from 'vue-router' // 引入 Vue Router 创建函数
import { useUserStore } from '@/store/modules/user' // 引入用户状态管理

const router = createRouter({ // 创建 Vue Router 实例
  history: createWebHistory(import.meta.env.BASE_URL), // 历史模式, 基于URL的路由
  routes: [ // 路由配置
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/home/HomeView.vue'), // 懒加载首页组件
      meta: { title: '失物招领 - 首页' }
    },
    {
      path: '/detail/:id',
      name: 'detail',
      component: () => import('@/views/detail/DetailView.vue'), // 懒加载详情组件
      meta: { title: '信息详情' }
    },
    {
      path: '/publish',
      name: 'publish',
      component: () => import('@/views/publish/PublishView.vue'), // 懒加载发布组件
      meta: { title: '发布信息', requiresAuth: true }
    },
    {
      path: '/publish/:id',
      name: 'publish-edit',
      component: () => import('@/views/publish/PublishView.vue'), // 懒加载发布编辑组件
      meta: { title: '编辑信息', requiresAuth: true }
    },
    {
      path: '/login', // 登录路由
      name: 'login', // 登录路由
      component: () => import('@/views/user/LoginView.vue'), // 懒加载登录组件
      meta: { title: '登录' }
    },
    {
      path: '/register',
      name: 'register', // 注册路由
      component: () => import('@/views/user/RegisterView.vue'), // 懒加载注册组件
      meta: { title: '注册' }
    },
    {
      path: '/user/profile',
      name: 'profile', // 个人中心路由
      component: () => import('@/views/user/ProfileView.vue'),
      meta: { title: '个人中心', requiresAuth: true }
    },
    {
      path: '/user/posts',
      name: 'my-posts',
      component: () => import('@/views/user/MyPostsView.vue'),
      meta: { title: '我的发布', requiresAuth: true }
    },
    {
      path: '/user/claims',
      name: 'my-claims',
      component: () => import('@/views/claim/MyClaimsView.vue'),
      meta: { title: '我的认领', requiresAuth: true }
    }
  ]
})

// 全局前置守卫 - 认证检查
router.beforeEach((to, _from, next) => { // 全局前置守卫
  document.title = (to.meta.title as string) || '失物招领系统'

  if (to.meta.requiresAuth) {
    const userStore = useUserStore() // 获取用户状态管理实例
    if (!userStore.token) {
      // 未登录，跳转到登录页
      next({ name: 'login', query: { redirect: to.fullPath } }) // 跳转到登录页, 并将当前路由路径作为查询参数
      return
    }
  }
  next() // 继续导航
})

export default router // 导出 Vue Router 实例
