<template>
    <div id="app">
      <header>
        <h1>分布式温控系统</h1>
        <router-view></router-view>
      </header>
      <!-- <main>
        <TemperatureDisplay />
        <TemperatureControl />
        <CostDisplay />
      </main> -->
    </div>
  </template>
  
  <script>
  // 导入组件
  /* import TemperatureDisplay from './components/TemperatureDisplay.vue'
  import TemperatureControl from './components/TemperatureControl.vue'
  import CostDisplay from './components/CostDisplay.vue'
   */
  export default {
    name: 'App',
    /* components: {
      TemperatureDisplay,
      TemperatureControl,
      CostDisplay
    } */
  }
  </script>
  
  <style>
  /* 全局样式 */
  #app {
    text-align: center;
    font-family: 'Avenir', Helvetica, Arial, sans-serif;
  }
  
  header {
    background-color: #f9f9f9;
    padding: 10px 20px;
    border-bottom: 1px solid #ccc;
  }
  
  main {
    margin: 20px;
    padding: 20px;
    display: flex;
    flex-direction: column;
    align-items: center;
  }
  
  button {
    margin: 10px;
    padding: 8px 20px;
    border: none;
    background-color: #007BFF;
    color: white;
    border-radius: 4px;
    cursor: pointer;
  }
  
  button:hover {
    background-color: #0056b3;
  }
  
  input[type="number"] {
    padding: 8px;
    width: 80px;
    margin: 0 10px;
    border: 1px solid #ccc;
    border-radius: 4px;
  }
  </style>
  