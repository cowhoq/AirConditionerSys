<template>
  <div style="text-align: center">
    <el-form @submit.prevent="setWorkMode" >
      <el-form-item label="工作模式">
        <el-select v-model="workMode" placeholder="设置工作状态" style="width:100%">
          <el-option label="制冷模式" value="制冷模式"></el-option>
          <el-option label="供暖模式" value="供暖模式"></el-option>
          <!-- Add other modes as needed -->
        </el-select>
      </el-form-item>
      <el-button type="primary"  native-type="submit">设置工作状态</el-button>
    </el-form>
  </div>
</template>

<script>
export default {
  name: 'WorkModeControl',
  data() {
    return {
      workMode: '',
      firstValue: 0,
      secondValue: 0,
    };
  },
  methods: {
    setWorkMode() {
      this.$axios.post('/setWorkStatus', {
        workMode: this.workMode,
        firstValue: this.firstValue,
        secondValue: this.secondValue,
      })
          .then(() => {
            this.$message.success('Work Mode Set Successfully');
          })
          .catch(() => {
            this.$message.error('Setting Work Mode Failed');
          });
    },
  },
};
</script>

<style>

</style>
