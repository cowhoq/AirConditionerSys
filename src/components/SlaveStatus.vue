<template>
  <div>
    <el-table :data="slaveStatuses" style="width: 100%">
      <el-table-column prop="id" label="房间ID" align="center"></el-table-column>
      <el-table-column prop="status" label="工作状态" align="center"></el-table-column>
      <el-table-column prop="temperature" label="温度" align="center"></el-table-column>
      <!-- Add other columns as needed -->
    </el-table>
  </div>
</template>

<script>
export default {
  name: 'SlaveStatus',
  data() {
    return {
      slaveStatuses: [],
    };
  },
  mounted() {
    this.fetchSlaveStatuses();
  },
  methods: {
    fetchSlaveStatuses() {
      this.$axios.get('/getSlaveStatus')
        .then(response => {
          this.slaveStatuses = response.data;
        })
        .catch(() => {
          this.$message.error('Failed to fetch slave statuses');
        });
    },
  },
};
</script>
