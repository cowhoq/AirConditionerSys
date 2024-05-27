<template>
  <div class="control-panel">
    <div class="temperature-setting">
      <label class="label">目标温度:</label>
      <div class="temp-control">
        <button @click="changeTemperature(-1)">降低一度</button>
        <span class="current-temp">{{ targetTemperature }}°C</span>
        <button @click="changeTemperature(1)">升高一度</button>
      </div>
    </div>
    <div class="fan-speed-buttons">
      <span class="label">风速调节</span>
      <button @click="setFanSpeed('low')" :class="{'active': fanSpeed === 'low'}">低速</button>
      <button @click="setFanSpeed('medium')" :class="{'active': fanSpeed === 'medium'}">中速</button>
      <button @click="setFanSpeed('high')" :class="{'active': fanSpeed === 'high'}">高速</button>
    </div>
    <button @click="submitSettings" class="submit-button">提交设置</button>
  </div>
</template>

<script>
import axios from 'axios'; // 引入 axios 库

export default {
  data() {
    return {
      mode: 'cool',
      fanSpeed: 'medium',
      timer: null,  // 用于存储 setTimeout 的 ID
    };
  },
  methods: {
    setFanSpeed(fanSpeed) {
      this.fanSpeed = fanSpeed;
    },
    changeTemperature(index) {
      if(index === 1) {
        this.$store.dispatch('increaseTemperature', this.targetTemperature + 1);
      } else if(index === -1) {
        this.$store.dispatch('decreaseTemperature', this.targetTemperature - 1);
      }
    },
    submitSettings() {
      if (this.mode === 'cool' && this.targetTemperature >= 25) {
        alert('制冷模式下温度区间为18-25度');        
        return false;
      } else if (this.mode === 'warm' && this.targetTemperature < 25) {
        alert('制热模式下温度区间为25-30度');
        return false;
      }
      if (this.mode != this.$store.state.currentHostMode) {
        alert('当前主机不支持该模式！');
        return false;
      }
      if (this.timer) {
        clearTimeout(this.timer);  // 清除之前的计时器
      }
      this.timer = setTimeout(() => {
        console.log('Submitting settings:', this.targetTemperature, this.fanSpeed, this.roomNumber);
        
        axios.post('/api/settings', {  // 假定你的 API 端点为 /api/settings
          temperature: this.targetTemperature,
          fanSpeed: this.fanSpeed,
          roomNumber: this.roomNumber,
          targetMode: this.mode
        })
        .then(response => {
          console.log('Settings updated successfully', response.data);
          // 可以添加更多的响应处理逻辑
        })
        .catch(error => {
          console.error('Failed to update settings', error);
          // 处理错误的逻辑
        });

        this.timer = null;  // 重置计时器
      }, 1000);  // 如果在一秒内多次点击，只处理最后一次
    }
  },
  computed: {
    roomNumber() {
      // 假定房间号从 store 中获取
      return this.$store.state.roomNumber;  // 默认房间号为 101    
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
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
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
