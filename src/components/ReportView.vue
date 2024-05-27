<template>
  <div>
    <el-form @submit.prevent="fetchReports">
      <el-form-item label="Period">
        <el-date-picker v-model="period" type="date" placeholder="Select date"></el-date-picker>
      </el-form-item>
      <el-button type="primary" native-type="submit">Get Reports</el-button>
    </el-form>
    <el-table :data="reports" style="width: 100%">
      <el-table-column prop="roomId" label="Room ID"></el-table-column>
      <el-table-column prop="powerOnCount" label="Power On Count"></el-table-column>
      <el-table-column prop="requests" label="Requests">
        <template v-slot:scope="{ row }">
          <ul>
            <li v-for="request in row.requests" :key="request.id">
              Start: {{ request.startTime }}, End: {{ request.endTime }},
              Start Temp: {{ request.startTemp }}, End Temp: {{ request.endTemp }},
              Airflow: {{ request.airflow }}, Cost: {{ request.cost }}
            </li>
          </ul>
        </template>
      </el-table-column>
      <el-table-column prop="totalCost" label="Total Cost"></el-table-column>
    </el-table>
  </div>
</template>

<script>
export default {
  name: 'ReportView',
  data() {
    return {
      period: '',
      reports: [],
    };
  },
  methods: {
    fetchReports() {
      this.$axios.get('/getTable', { params: { period: this.period } })
        .then(response => {
          this.reports = response.data;
        })
        .catch(() => {
          this.$message.error('Failed to fetch reports');
        });
    },
  },
};
</script>
