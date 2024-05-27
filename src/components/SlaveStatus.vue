<template>
  <div>
    <div style="margin-bottom: 20px;">
      <span>设置查询间隔时间 (毫秒)： </span>
      <el-input-number v-model="intervalTime" :min="100" @change="startFetching"/>
      <el-switch v-model="isFetching" active-text="开启定时查询" inactive-text="关闭定时查询"
                 style="margin-left: 20px;"/>
    </div>

    <el-table :data="slaveStatuses" style="width: 100%">
      <el-table-column prop="id" label="房间ID" align="center"/>
      <el-table-column prop="status" label="工作状态" align="center"/>
      <el-table-column prop="curTemp" label="当前温度" align="center"/>
      <el-table-column prop="setTemp" label="设定温度" align="center"/>
      <el-table-column prop="mode" label="风速" align="center"/>
      <el-table-column prop="wind" label="是否送风" align="center"/>
    </el-table>
  </div>
</template>

<script lang="ts" setup>
import {ref, onMounted, watch} from 'vue';
import axiosRequest from "@/utils/axiosRequest.ts";
import {ElMessage} from "element-plus";

let slaveStatuses = ref([])
let intervalTime = ref(1000); // Default interval time in milliseconds
let intervalId = ref(null);
let isFetching = ref(false)

onMounted(() => {
  fetchSlaveStatuses();
});

watch(isFetching, (newVal: any) => {
  if (newVal)
    startFetching();
  else
    stopFetching();

});

function startFetching() {
  if (intervalId.value)
    clearInterval(intervalId.value);
  console.log(intervalTime.value)
  fetchSlaveStatuses();
  intervalId.value = setInterval(fetchSlaveStatuses, intervalTime.value);
}

function stopFetching() {
  if (intervalId.value) {
    clearInterval(intervalId.value);
    intervalId.value = null;
  }
}

async function fetchSlaveStatuses() {
  try {
    let r = await axiosRequest({
      url: '/getSlaveStatus',
      method: 'get'
    })

    if (r.code == 1)
      slaveStatuses.value = r.data
    else
      throw new Error(r.msg || '操作失败')

  } catch (e: any) {
    ElMessage.error('出错了: ' + e.message);
  }
}


</script>
