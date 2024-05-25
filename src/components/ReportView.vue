<template>
  <div>
    <h2>报表</h2>
    <div>
      <button @click="fetchReport('daily')">日报表</button>
      <button @click="fetchReport('weekly')">周报表</button>
      <button @click="fetchReport('monthly')">月报表</button>
    </div>
    <table v-if="reportData.length">
      <thead>
        <tr>
          <th>房间号</th>
          <th>开关机次数</th>
          <th>温控请求起止时间</th>
          <th>温控请求起止温度及风量消耗大小</th>
          <th>温控请求所需费用</th>
          <th>总费用</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="record in reportData" :key="record.roomId">
          <td>{{ record.roomId }}</td>
          <td>{{ record.powerCycleCount }}</td>
          <td>
            <ul>
              <li v-for="(time, index) in record.requestTimes" :key="index">{{ time }}</li>
            </ul>
          </td>
          <td>
            <ul>
              <li v-for="(temperature, index) in record.temperatureRanges" :key="index">{{ temperature }}</li>
            </ul>
          </td>
          <td>
            <ul>
              <li v-for="(cost, index) in record.requestCosts" :key="index">{{ cost }}</li>
            </ul>
          </td>
          <td>{{ record.totalCost }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
import { ref } from 'vue';
import apiClient from '../api';

export default {
  name: 'ReportView',
  setup() {
    const reportData = ref([]);

    const fetchReport = (type) => {
      apiClient.get(`/report?type=${type}`) // 假设后端有此接口
        .then(response => {
          reportData.value = response.data;
        })
        .catch(() => {
          alert('获取报表失败');
        });
    };

    return {
      reportData,
      fetchReport
    };
  }
};
</script>
