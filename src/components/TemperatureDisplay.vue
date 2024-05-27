<template>
  <div>
    <h3 v-if="currentTemperature === null">加载中...</h3>
    <h3 v-else class="temperature">当前温度: {{ currentTemperature }}°C</h3>
    <h3 class="host-temperature">主机温度: {{ currentHostTemperature }}°C</h3>
    <h3 class="host-mode">主机工作模式： {{ currentHostMode }} </h3>
    <h3 class="fan-mode">送风状态：{{ fanMode }}</h3>
  </div>
</template>


<script>


export default {
  data() {
    return {
    };
  },
  mounted() {
    console.log('Component mounted, fetching temperature...');
    this.$store.dispatch('getCurrentTemperature'); // 在组件挂载时获取当前温度

    // 设置每隔一秒获取一次当前温度
    this.intervalId = setInterval(() => {
      this.$store.dispatch('getCurrentTemperature');
    }, 10000);
  },
  methods: {
      
  },
  computed: {
    currentHostTemperature() {
      return this.$store.state.currentHostTemperature;
    },
    currentHostMode() {
      return this.$store.state.currentHostMode; 
    },
    fanMode() {
      return this.$store.state.fanMode;   
    },
    currentTemperature() {
      return this.$store.state.currentTemperature;
    }
}
}
</script>

<style scoped>
.temperature-display {
  text-align: center;
  padding: 20px;
}

.temperature,
.host-temperature,
.host-mode,
.loading,
.fan-mode{
  font-size: 2em;
  font-weight: bold;
  margin: 10px 0;
  padding: 10px;
  border-radius: 8px;
}
.fan-mode {
  background-color: #e0f7fa;
  color: #00796b;
}
.temperature {
  background-color: #e0f7fa;
  color: #00796b;
}

.host-temperature {
  background-color: #fff3e0;
  color: #e65100;
}

.host-mode {
  background-color: #e1bee7;
  color: #6a1b9a;
}

.loading {
  background-color: #ffeb3b;
  color: #f57f17;
}
</style>

