<template>
  <div>
    <div class="header">
      <h1>中央空调控制</h1>
    </div>
    <div class="content">
      <el-row :gutter="24" type="flex" align="middle">
        <el-col :lg="8" :xs="24">
          <div class="window">
            <h2>开关控制</h2>
            <PowerControl />
          </div>
        </el-col>
        <el-col :lg="8" :xs="24">
          <div class="window">
            <h2>模式控制</h2>
            <WorkModeControl />
          </div>
        </el-col>
        <el-col :lg="8" :xs="24">
          <div class="window">
            <h2>从机状态</h2>
            <SlaveStatus />
          </div>
        </el-col>
      </el-row>
      <el-row :gutter="24" style="margin-top: 20px;">
        <el-col :span="24">
          <div class="window">
            <h2>查看报表</h2>
            <ReportView />
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import PowerControl from './components/PowerControl.vue';
import WorkModeControl from './components/WorkModeControl.vue';
import SlaveStatus from './components/SlaveStatus.vue';
import ReportView from './components/ReportView.vue';

export default {
  name: 'App',
  components: {
    PowerControl,
    WorkModeControl,
    SlaveStatus,
    ReportView,
  },
};
</script>

<style>
.header {
  text-align: center;
  margin-bottom: 20px;
}

.content {
  padding: 20px;
}

.window {
  border: 1px solid #ccc;
  padding: 20px;
  border-radius: 8px;
  background-color: #fff;
  height: 30vh;
}

.window h2 {
  text-align: center;
  margin-bottom: 20px;
}


</style>
