<template>
  <div>
    <h3 v-if="currentTemperature === null">加载中...</h3>
    <h3 v-else class="temperature">当前温度: {{ currentTemperature / 100 }}°C</h3>
    <h3 class="host-temperature">主机温度: {{ hostTemperatureLow + '~' + hostTemperatureHigh }}°C</h3>
    <h3 class="host-mode">主机工作模式： {{ hostMode === 'REFRIGERATION' ? '制冷' : '制热' }} </h3>
    <h3 class="fan-mode">送风状态：{{ fanStatus == true ? '开启' : '关闭' }}</h3>
  </div>
</template>

<script>
export default {
  data() {
    return {
      intervals: []
    };
  },
  mounted() {
    console.log('Component mounted, fetching temperature...');
    
    // 初始化获取数据
    this.$store.dispatch('getCurrentTemperature'); 
    this.$store.dispatch('getMasterStatus');
    this.$store.dispatch('getFanStatus');

    // 每隔一秒获取一次主机状态
    this.intervals.push(setInterval(() => {
      this.$store.dispatch('getMasterStatus');
    }, 1000));
    
    // 每隔一秒获取一次送风状态
    this.intervals.push(setInterval(() => {
      this.$store.dispatch('getFanStatus');
    }, 1000));
    
    // 每隔一秒获取一次当前温度
    this.intervals.push(setInterval(() => {
      this.$store.dispatch('getCurrentTemperature');
    }, 1000));

    
  },
  beforeDestroy() {
    // 清除所有定时器
    this.intervals.forEach(intervalId => clearInterval(intervalId));
  },
  computed: {
    hostTemperatureLow() {
      return this.$store.state.hostTemperatureLow;
    },
    hostTemperatureHigh() {
      return this.$store.state.hostTemperatureHigh;
    },
    hostMode() {
      return this.$store.state.hostMode; 
    },
    fanStatus() {
      console.log('fanStatus', this.$store.state.fanStatus);
      return this.$store.state.fanStatus;   
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
.fan-mode {
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
