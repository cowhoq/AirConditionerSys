<template>
  <div class="control-panel">
    <div class="temperature-setting">
      <label class="label">目标温度:</label>
      <div class="temp-control">
        <button @click="changeTemperature(-1)">降低一度</button>
        <span class="current-temp">{{ targetTemperature / 100 }}°C</span>
        <button @click="changeTemperature(1)">升高一度</button>
      </div>
    </div>
    <div class="fan-speed-buttons">
      <span class="label">风速调节</span>
      <button @click="setFanSpeed('SLOW')" :class="{'active': fanSpeed === 'SLOW'}">低速</button>
      <button @click="setFanSpeed('MEDDLE')" :class="{'active': fanSpeed === 'MEDDLE'}">中速</button>
      <button @click="setFanSpeed('FAST')" :class="{'active': fanSpeed === 'FAST'}">高速</button>
    </div>
    <button @click="submitSettings" class="submit-button">提交设置</button>
  </div>
</template>

<script>
export default {
  data() {
    return {
      mode: 'cool',
      fanSpeed: 'FAST',
      timer: null, // 用于存储 setTimeout 的 ID
    };
  },
  methods: {
    setFanSpeed(fanSpeed) {
      this.fanSpeed = fanSpeed;
    },

    changeTemperature(index) {
      const minTemp = this.$store.state.hostTemperatureLow;
      const maxTemp = this.$store.state.hostTemperatureHigh;
      const newTemp = this.$store.state.targetTemperature / 100 + index;
      if (newTemp < minTemp || newTemp > maxTemp) {

        window.alert(`当前温度已经达到上下限，无法再次调节。`);
        return;
      }
      if (index === 1) {
        this.$store.dispatch('increaseTemperature', this.targetTemperature + 100);
      } else if (index === -1) {
        this.$store.dispatch('decreaseTemperature', this.targetTemperature - 100);
      }
    },

    submitSettings() {
      let speed = this.fanSpeed.toString();
      console.log('this.fanSpeed', speed);
      this.$store.state.fanSpeed = this.fanSpeed;
      this.$store.dispatch('changeFanSpeed');
    }
  },
  computed: {
    roomNumber() {
      // 假定房间号从 store 中获取
      return this.$store.state.roomNumber; // 默认房间号为 101
    },
    targetTemperature() {
      this.$store.dispatch('getTargetTemperature');
      return this.$store.state.targetTemperature;
    },
    targetMode() {
      return this.$store.state.targetMode;
    },
  }
}
</script>

<style scoped>
.label {
  font-weight: bold;
  font-size: 1.2em;
  color: #333; /* 深灰色 */
  margin-bottom: 10px; /* 标签与内容的间距 */
}

.control-panel {
  border: 1px solid #ccc;
  padding: 30px;
  margin: 30px;
  display: flex;
  flex-direction: column;
  align-items: center;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.temperature-setting {
  margin-bottom: 20px;
}

.temp-control {
  display: flex;
  align-items: center;
}

.temp-control button {
  width: 120px;
  height: 40px;
  font-size: 1em;
  margin: 0 10px;
  background-color: #007BFF;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.temp-control button:hover {
  background-color: #0056b3;
}

.current-temp {
  font-weight: bold;
  color: #333;
}

.fan-speed-buttons {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.fan-speed-buttons button {
  margin: 5px;
  padding: 10px 20px;
  border: none;
  background-color: #ccc;
  color: white;
  cursor: pointer;
  transition: background-color 0.3s;
}

.fan-speed-buttons button.active {
  background-color: #007BFF;
}

.fan-speed-buttons button:hover {
  background-color: #0056b3;
}

.submit-button {
  margin-top: 20px;
  padding: 10px 20px;
  background-color: green;
  color: white;
  border: none;
  cursor: pointer;
}

.submit-button:hover {
  background-color: darkgreen;
}
</style>
