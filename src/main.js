// src/main.js
import { createApp } from 'vue';
import App from './App.vue';
import axios from './axios';  // Ensure this path is correct
import router from './router';
import ElementPlus from 'element-plus';
// import ElementUI from 'element-ui'
import 'element-plus/dist/index.css';

const app = createApp(App);

app.config.globalProperties.$axios = axios;

app.use(router);
app.use(ElementPlus);
app.mount('#app');
