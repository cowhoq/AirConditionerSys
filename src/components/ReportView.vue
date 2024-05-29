<template>
  <div>
    <el-form @submit.prevent="fetchReports">
      <el-form-item label="选择报告类型">
        <el-select v-model="period" placeholder="选择报告类型">
          <el-option label="日报" value="DAY"/>
          <el-option label="周报" value="WEEK"/>
          <el-option label="月报" value="MONTH"/>
        </el-select>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" native-type="submit">获取报表</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="reports" style="width: 100%" stripe border show-summary>
      <el-table-column prop="roomId" label="房间ID" align="center"/>
      <!-- TODO: 加上这个字段-->
      <!--      <el-table-column prop="powerOnCount" label="总计消耗电量" align="center"/>-->
      <el-table-column prop="startTime" label="Start Time"/>
      <el-table-column prop="stopTime" label="End Time"/>
      <el-table-column prop="startTemp" label="Start Temperature"/>
      <el-table-column prop="stopTemp" label="End Temperature"/>
      <el-table-column prop="fanSpeed" label="Airflow"/>
      <el-table-column prop="totalFee" label="Cost"/>

      <!--      <el-table-column prop="总计消费" label="Total Cost" align="center"/>-->
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
    console.log(period.value)
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
