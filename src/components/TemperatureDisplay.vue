<template>
  <div>
    <h3 v-if="currentTemp === null">加载中...</h3>
    <h3 v-else>当前温度: {{ currentTemp }}°C</h3>
  </div>
</template>


<script>
import axios from 'axios';

export default {
  data() {
    return {
      currentTemp: null // 初始化为 null 表示温度尚未加载
    };
  },
  mounted() {
    this.fetchCurrentTemperature();
  },
  methods: {
    fetchCurrentTemperature() {
      axios.get('/api/current-temperature')
        .then(response => {
          this.currentTemp = response.data.temperature;
        })
        .catch(error => {
          console.error('Error fetching temperature', error);
          this.currentTemp = 'Error'; // 出错时显示错误信息
        });
    }
  }
}
</script>

<style>
/* 这里可以添加一些样式来美化你的温度显示 */
</style>
