import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import {fileURLToPath} from 'url';
import ElementPlus from 'unplugin-element-plus/vite'
import {ElementPlusResolver} from "unplugin-vue-components/resolvers";
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    AutoImport({
      imports: ['vue'],
      resolvers: [ElementPlusResolver()],
    }),
    Components({
      resolvers: [ElementPlusResolver({importStyle: "sass"})],
    }),
    ElementPlus({
      useSource: true,
      defaultLocale: 'zh-cn',
    }),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // 目标请求地址
        changeOrigin: true, //允许跨域
        rewrite: (path) => path.replace(/^\/api/, '') // 去除请求中的 `/api`
      }
    }
  },
})
