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

<script lang="ts" setup>
import {ref, onMounted} from 'vue';
import axiosRequest from "@/utils/axiosRequest.ts";
import {ElMessage} from "element-plus";

let slaveStatuses = ref([])

onMounted(() => {
  fetchSlaveStatuses();
});

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
