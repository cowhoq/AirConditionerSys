<template>
  <div>
    <el-form @submit.prevent="fetchReports">
      <el-form-item label="选择报告时间">
        <el-date-picker v-model="period" type="date" placeholder="选择日期"></el-date-picker>
        <el-button type="primary" native-type="submit">获取报表</el-button>

      </el-form-item>
    </el-form>
    <el-table :data="reports" style="width: 100%">
      <el-table-column prop="roomId" label="房间ID" align="center"></el-table-column>
      <el-table-column prop="powerOnCount" label="总计消耗电量" align="center"></el-table-column>
      <el-table-column label="请求" align="center">
        <template v-slot:default="scope">
          <el-table :data="scope.row.requests" style="width: 100%">
            <el-table-column prop="startTime" label="Start Time"></el-table-column>
            <el-table-column prop="endTime" label="End Time"></el-table-column>
            <el-table-column prop="startTemp" label="Start Temperature"></el-table-column>
            <el-table-column prop="endTemp" label="End Temperature"></el-table-column>
            <el-table-column prop="airflow" label="Airflow"></el-table-column>
            <el-table-column prop="cost" label="Cost"></el-table-column>
          </el-table>
        </template>
      </el-table-column>
      <el-table-column prop="总计消费" label="Total Cost" align="center"></el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import {ref} from 'vue';
import axiosRequest from "@/utils/axiosRequest.ts";
import {ElMessage} from "element-plus";

let period = ref('')
let reports = ref([])

async function fetchReports() {
  try {
    let r = await axiosRequest({
      url: '/getTable',
      method: 'get',
      params: {
        period: period.value
      }
    })

    if (r.code == 1)
      reports.value = r.data
    else
      throw new Error(r.msg || '操作失败')

  } catch (e: any) {
    ElMessage.error('出错了: ' + e.message);
  }
}
</script>
