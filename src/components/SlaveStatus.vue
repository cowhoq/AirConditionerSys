<template>
  <div>
    <el-table :data="slaveStatuses" style="width: 100%">
      <el-table-column prop="id" label="ID"></el-table-column>
      <el-table-column prop="status" label="Status"></el-table-column>
      <el-table-column prop="temperature" label="Temperature"></el-table-column>
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
