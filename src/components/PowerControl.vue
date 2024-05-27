<template>
  <div>
    <el-button type="primary" @click="powerOn">Power On</el-button>
    <el-button type="danger" @click="powerOff">Power Off</el-button>
  </div>
</template>

<script>
export default {
  name: 'PowerControl',
  methods: {
    powerOn() {
      this.$axios.get('/powerOn')
        .then(() => {
          this.$message.success('Power On Successful');
        })
        .catch(() => {
          this.$message.error('Power On Failed');
        });
    },
    powerOff() {
      this.$axios.get('/powerOff')
        .then(() => {
          this.$message.success('Power Off Successful');
        })
        .catch(() => {
          this.$message.error('Power Off Failed');
        });
    },
  },
};
</script>
