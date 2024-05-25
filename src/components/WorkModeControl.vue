<template>
  <div>
    <select v-model="workMode">
      <option v-for="mode in workModes" :key="mode" :value="mode">{{ mode }}</option>
    </select>
    <input v-model="firstValue" type="number" placeholder="First Value">
    <input v-model="secondValue" type="number" placeholder="Second Value">
    <button @click="setWorkMode">设置工作模式</button>
  </div>
</template>

<script>
import apiClient from '../api';
import { ref } from 'vue';

export default {
  name: 'WorkModeControl',
  setup() {
    const workModes = ref(['MODE1', 'MODE2', 'MODE3']); // 替换为实际的工作模式
    const workMode = ref('');
    const firstValue = ref(0);
    const secondValue = ref(0);

    const setWorkMode = () => {
      apiClient.post('/setWorkMode', {
        workMode: workMode.value,
        firstValue: firstValue.value,
        secondValue: secondValue.value
      })
      .then(() => {
        alert('设置成功');
      })
      .catch(() => {
        alert('设置失败');
      });
    };

    return {
      workModes,
      workMode,
      firstValue,
      secondValue,
      setWorkMode
    };
  }
};
</script>
