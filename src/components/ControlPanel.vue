<template>
  <div class="control-panel">
    <h1>控制面板</h1>
    <main>
      <div class="room-info">
        <label for="room-id" class="room-label">房间号：</label>
        <span class="room-number">{{ roomNumber }}</span>
      </div>
      <div class="cost-info">
        <label for="room-id" class="room-label">当前费用: </label>
        <span class="room-number">{{ fee }}¥</span>
      </div>
      <div class="energy-info">
        <label for="room-id" class="room-label">能耗：</label>
        <span class="room-number">{{ energy }}KW/h</span>
      </div>
      <button :class="{ active: state }" @click="toggleAirConditioning">
        {{ state ? '关闭空调' : '开启空调' }}
      </button>
      <div v-if="state" class="controls">
        <div class="left-panel">
          <TemperatureDisplay/>
        </div>
        <div class="right-panel">
          <TemperatureControl/>
        </div>
      </div>
      <div v-else>
        <button @click="logout" class="logout-button">退出</button>
      </div>
    </main>
  </div>
</template>

<script>
// 导入组件
import TemperatureDisplay from './TemperatureDisplay.vue'
import TemperatureControl from './TemperatureControl.vue'

export default {
  name: 'ControlPanel',
  components: {
    TemperatureDisplay,
    TemperatureControl,
  },
  data() {
    return {};
  },
  methods: {
    toggleAirConditioning() {
      if (!this.$store.state.Host) {
        alert('host is not true');
        return false;
      }
      this.$store.dispatch('toggleAirConditioning');
      this.intervalId = setInterval(() => {
        this.$store.dispatch('getFeeAndEnergy');
      }, 1000);

    },
    logout() {
      this.$store.dispatch('logout');
      clearInterval(this.intervalId);
      this.$router.push('/');
    }
  },
  computed: {
    roomNumber() {
      return this.$store.state.roomNumber;
    },
    state() {
      return this.$store.state.airConditioning;
    },
    Host() {
      return this.$store.state.Host;
    },
    energy() {
      return this.$store.state.energy;
    },
    fee() {
      return this.$store.state.cost;
    }
  }
}
</script>

<style scoped>
.cost-info {
  width: 100%;
  display: flex;
  justify-content: center;
  margin-top: 10px;
}

.control-panel {
  background: linear-gradient(-45deg, #83a4d4, #b6fbff, #83a4d4, #b6fbff);
  background-size: 400% 400%;
  animation: BackgroundGradient 15s ease infinite;
  border: 1px solid #dee2e6;
  padding: 20px;
  margin: 20px auto;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  max-width: 1200px;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

h1 {
  color: #007bff;
  margin-bottom: 20px;
  font-size: 2em;
}

.room-info {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
}

.room-number {
  font-size: 1.5em;
  color: #343a40;
  white-space: normal;
}

.room-label {
  font-weight: bold;
  color: #343a40;
  font-size: 1.5em;
  margin-right: 10px;
}

button {
  padding: 10px 20px;
  border: none;
  background-color: #007bff;
  color: white;
  cursor: pointer;
  border-radius: 5px;
  transition: background-color 0.3s, transform 0.2s;
  margin-top: 20px;
  font-size: 1.2em;
}

button:hover {
  background-color: #0056b3;
  transform: translateY(-2px);
}

button.active {
  background-color: #dc3545;
}

.controls {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  width: 100%;
  margin-top: 20px;
}

.left-panel, .right-panel {
  width: 48%;
}

.left-panel {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  padding-right: 20px;
}

.right-panel {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.temperature-setting, .fan-speed-buttons {
  width: 100%;
  text-align: center;
  margin-bottom: 20px;
}

.fan-speed-buttons {
  display: flex;
  justify-content: center;
}

.fan-speed-buttons button {
  margin: 0 5px;
}

.current-temp {
  font-weight: bold;
  color: #343a40;
  margin-left: 10px;
  font-size: 1.5em;
}

@keyframes BackgroundGradient {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}
</style>
