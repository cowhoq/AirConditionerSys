import Vue from 'vue'
import Vuex from 'vuex'
Vue.use(Vuex)
import App from './App.vue'
import router from './router'
import store from './store'
Vue.config.productionTip = false


new Vue({
  router,
  store,
  render: h => h(App),
}).$mount('#app')

// src/main.js
if (process.env.NODE_ENV === 'development') {
  require('./mock'); // 仅在开发环境中引入 Mock 数据
}
