// router/index.js
import Vue from 'vue';
import Router from 'vue-router';
import LoginPage from '@/components/LoginPage.vue';
import ControlPanel from '@/components/ControlPanel.vue';

Vue.use(Router);

export default new Router({
  mode: 'history',
  routes: [
    {
      path: '/',
      name: 'LoginPage',
      component: LoginPage
    },
    {
      path: '/control-panel',
      name: 'ControlPanel',
      component: ControlPanel,
      props: true
    }
  ]
});
