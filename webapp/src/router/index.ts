import HealthStatus from '@/views/HealthStatus.vue'
import UserManagement from '@/views/UserManagement.vue'
import { createRouter, createWebHistory } from 'vue-router'

const routes = [
    {
        path: '/',
        name: 'UserManagement',
        component: UserManagement,
        meta: {
            title: 'User Management'
        }
    },
    {
        path: '/health',
        name: 'HealthStatus',
        component: HealthStatus,
        meta: {
            title: 'Health Status'
        }
    },
    {
        path: '/:pathMatch(.*)*',
        name: 'NotFound',
        redirect: '/'
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// Update page title on route change
router.beforeEach((to, from, next) => {
    if (to.meta.title) {
        document.title = `${to.meta.title} - Digg App`
    }
    next()
})

export default router