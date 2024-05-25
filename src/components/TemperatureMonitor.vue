<template>
  <div>
    <h2>各屋温度和从机状态</h2>
    <table>
      <thead>
        <tr>
          <th>房间号</th>
          <th>温度</th>
          <th>从机状态</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="room in rooms" :key="room.id">
          <td>{{ room.id }}</td>
          <td>{{ room.temperature }}</td>
          <td>{{ room.status }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import apiClient from '../api';

export default {
  name: 'TemperatureMonitor',
  setup() {
    const rooms = ref([]);

    const fetchRooms = () => {
      apiClient.get('/rooms') // 假设后端有此接口
        .then(response => {
          rooms.value = response.data;
        })
        .catch(() => {
          alert('获取数据失败');
        });
    };

    onMounted(fetchRooms);

    return {
      rooms
    };
  }
};
</script>
