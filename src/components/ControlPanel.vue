<template>
  <div class="control-panel">
    <h1>控制面板</h1>
    <main>
      <label for="room-id">房间号：</label>
      <button :class="{ active: state }" @click="toggleAirConditioning">
        {{ state ? '关闭空调' : '开启空调' }}
      </button>
      <div v-if="state">
        <TemperatureDisplay />
        <TemperatureControl />
        <CostDisplay />
      </div>
    </main>
  </div>
</template>

<script>
// 导入组件
import TemperatureDisplay from './TemperatureDisplay.vue'
import TemperatureControl from './TemperatureControl.vue'
import CostDisplay from './CostDisplay.vue'

export default {
  name: 'ControlPanel',
  components: {
    TemperatureDisplay,
    TemperatureControl,
    CostDisplay
  },
  data() {
    return {
      state: false,
    };
  },
  methods: {
    toggleAirConditioning() {
      this.state = !this.state; // 切换空调开启/关闭状态
    }
  }
}
</script>

<style scoped>
.active {
  background-color: red;
  color: white;
}

button {
  padding: 10px 15px;
  border: none;
  cursor: pointer;
  transition: background-color 0.3s;
}

button:hover {
  background-color: #0056b3;
  color: white;
}
</style>
