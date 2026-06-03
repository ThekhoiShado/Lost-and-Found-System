import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/modules/user'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/home/HomeView.vue'),
      meta: { title: '失物招领 - 首页' }
    },
    {
      path: '/detail/:id',
      name: 'detail',
      component: () => import('@/views/detail/DetailView.vue'),
      meta: { title: '信息详情' }
    },
    {
      path: '/publish',
      name: 'publish',
      component: () => import('@/views/publish/PublishView.vue'),
      meta: { title: '发布信息', requiresAuth: true }
    },
    {
      path: '/publish/:id',
      name: 'publish-edit',
      component: () => import('@/views/publish/PublishView.vue'),
      meta: { title: '编辑信息', requiresAuth: true }
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/user/LoginView.vue'),
      meta: { title: '登录' }
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/user/RegisterView.vue'),
      meta: { title: '注册' }
    },
    {
      path: '/user/profile',
      name: 'profile',
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
router.beforeEach((to, _from, next) => {
  document.title = (to.meta.title as string) || '失物招领系统'

  if (to.meta.requiresAuth) {
    const userStore = useUserStore()
    if (!userStore.token) {
      // 未登录，跳转到登录页
      next({ name: 'login', query: { redirect: to.fullPath } })
      return
    }
  }
  next()
})

export default router
