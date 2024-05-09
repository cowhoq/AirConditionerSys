<template>
  <div class="control-panel">
    <div class="temperature-setting">
      <label for="tempInput">目标温度:</label>
      <input type="number" id="tempInput" v-model="targetTemperature">
      <span class="current-temp">{{ targetTemperature }}°C</span>
    </div>
    <div class="fan-speed-buttons">
      <button @click="setFanSpeed('low')" :class="{'active': fanSpeed === 'low'}">低速</button>
      <button @click="setFanSpeed('medium')" :class="{'active': fanSpeed === 'medium'}">中速</button>
      <button @click="setFanSpeed('high')" :class="{'active': fanSpeed === 'high'}">高速</button>
    </div>
    <button @click="submitSettings" class="submit-button">提交设置</button>
  </div>
</template>

<script>
export default {
  data() {
    return {
      targetTemperature: 22,
      fanSpeed: 'medium',
    };
  },
  methods: {
    updateTemperature() {
      console.log('Setting temperature to:', this.targetTemperature);
      // 这里添加发送到服务器的代码
    },
    setFanSpeed(speed) {
      this.fanSpeed = speed;
      console.log('Setting fan speed to:', this.fanSpeed);
      // 在这里添加发送到服务器的代码
    },
    submitSettings() {
      console.log('Submitting settings:', this.targetTemperature, this.fanSpeed);
      // 这里添加提交所有设置到服务器的代码
    }
  }
}
</script>

<style scoped>
.control-panel {
  border: 1px solid #ccc;
  padding: 20px;
  margin: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}

.temperature-setting {
  margin-bottom: 20px;
}

.current-temp {
  margin-left: 10px;
  font-weight: bold;
  color: #555;
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
