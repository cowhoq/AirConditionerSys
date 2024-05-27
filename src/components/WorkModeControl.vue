<template>
  <div>
    <el-form @submit.prevent="setWorkMode">
      <el-form-item label="Work Mode">
        <el-select v-model="workMode" placeholder="Select Work Mode">
          <el-option label="Mode 1" value="MODE_1"></el-option>
          <el-option label="Mode 2" value="MODE_2"></el-option>
          <!-- Add other modes as needed -->
        </el-select>
      </el-form-item>
      <el-form-item label="First Value">
        <el-input v-model="firstValue" type="number"></el-input>
      </el-form-item>
      <el-form-item label="Second Value">
        <el-input v-model="secondValue" type="number"></el-input>
      </el-form-item>
      <el-button type="primary" native-type="submit">Set Work Mode</el-button>
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
