<template>
  <div>
    <div class="header">
      <h1>中央空调控制</h1>
    </div>
    <div class="content">
      <el-row :gutter="24" type="flex" align="middle">
        <el-col :lg="12" :xs="24">
          <div class="window">
            <h2>开关控制</h2>
            <PowerControl :workMode="workMode" @refresh="refresh"/>
          </div>
        </el-col>

        <el-col :lg="12" :xs="24" v-if="workMode != 'OFF'">
          <div class="window">
            <h2>模式控制</h2>
            <WorkModeControl/>
          </div>
        </el-col>
      </el-row>

      <el-row ::gutter="24" style="margin-top: 20px;" >
        <el-col :span="24" v-if="workMode != 'OFF'">
          <div class="window">
            <h2>从机状态</h2>
            <SlaveStatus/>
          </div>
        </el-col>
      </el-row>

      <el-row :gutter="24" style="margin-top: 20px;" v-if="workMode != 'OFF'">
        <el-col :span="24">
          <div class="window">
            <h2>查看报表</h2>
            <ReportView/>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup lang="ts">
import PowerControl from './components/PowerControl.vue';
import WorkModeControl from './components/WorkModeControl.vue';
import SlaveStatus from './components/SlaveStatus.vue';
import ReportView from './components/ReportView.vue';
import axiosRequest from "@/utils/axiosRequest.ts";
import {ElMessage} from "element-plus";
import {onMounted, ref} from "vue";

let workMode = ref('')

onMounted(async () => {
  await getWorkStatus()
})
let refresh = (v:any) => {
  console.log(v)
  getWorkStatus()
}
async function getWorkStatus() {
  try {
    let r = await axiosRequest({
      url: '/getWorKMode',
      method: 'get',
    })
    console.log(r.data);
    if (r.code == 1)
      workMode.value = r.data
    else
      throw new Error(r.msg || '操作失败')

  } catch (e: any) {
    ElMessage.error('出错了: ' + e.message);
  }
}
</script>

<style scoped>
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
