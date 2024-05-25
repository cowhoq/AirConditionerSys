<template>
  <div>
    <button @click="powerOn">开机</button>
    <button @click="powerOff">关机</button>
  </div>
</template>

<script>
import apiClient from '../api';

export default {
  name: 'PowerControl',
  methods: {
    powerOn() {
      apiClient.get('/powerOn')
        .then(() => {
          alert('开机成功');
        })
        .catch(() => {
          alert('开机失败');
        });
    },
    powerOff() {
      apiClient.get('/powerOff')
        .then(() => {
          alert('关机成功');
        })
        .catch(() => {
          alert('关机失败');
        });
    }
  }
};
</script>
