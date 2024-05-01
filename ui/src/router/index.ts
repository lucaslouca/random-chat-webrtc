import { createRouter, createWebHistory, createWebHashHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const router = createRouter({
  // history: createWebHistory(import.meta.env.BASE_URL),
  history: createWebHashHistory(),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/chat',
      name: 'chat',
      // route level code-splitting
      // this generates a separate chunk (Graph.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/ChatView.vue')
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue')
    },
    {
      path: '/error',
      name: 'error',
      component: () => import('../views/ErrorView.vue')
    },
    {
      path: '/signup',
      name: 'signup',
      component: () => import('../views/SignupView.vue'),
      meta: {
        title: 'Signup'
      }
    },
    {
      path: '/resetpassword',
      name: 'resetpassword',
      component: () => import('../views/ResetPasswordView.vue'),
      meta: {
        title: 'Reset your password'
      }
    },
    {
      path: '/updatepassword',
      name: 'updatepassword',
      component: () => import('../views/UpdatePasswordView.vue'),
      meta: {
        title: 'Set a new password'
      }
    },
    {
      path: '/members',
      name: 'members',
      component: () => import('../views/MembersView.vue'),
      children: [
        {
          path: 'dashboard',
          name: 'dashboard',
          component: () => import('../views/DashboardView.vue')
        },
        {
          path: 'settings',
          name: 'settings',
          component: () => import('../views/SettingsView.vue')
        }
      ]
    }
  ]
})

export default router
