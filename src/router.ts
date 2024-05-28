// 第一步: 引入 createRouter
import {createRouter, createWebHistory} from 'vue-router'

// 第二步: 创建路由器
const router = createRouter({
    history: createWebHistory(), // 告诉路由器, 使用 history 模式
    routes: [ // 告诉路由器, 要呈现的组件
        {
            path: '/login',
            component: () => import('./views/Login.vue') // 动态导入 (懒加载), 提高性能
        },
        {
            path: '/',
            component: () => import('./views/DashBoard.vue')
        }

    ]
})

export default router // 将 router 暴露出去
