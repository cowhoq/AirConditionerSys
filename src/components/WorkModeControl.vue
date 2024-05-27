<template>
  <div style="text-align: center">
    <text style="font-size: 25px; font-weight: bold; margin-bottom: 30px">{{ workMode }}</text>
    <el-form @submit.prevent="setWorkMode">
      <el-form-item label="工作模式">
        <el-button type="primary" @click="changeMode(1)">制冷模式</el-button>
        <el-button type="danger" @click="changeMode(2)">供暖模式</el-button>
      </el-form-item>

      <el-form-item label="工作温度">
        <el-slider v-model="range" range :marks="marks" :max="40"/>
      </el-form-item>
      <el-button type="primary" native-type="submit" style="margin-top: 30px">设置工作状态</el-button>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import axiosRequest from "@/utils/axiosRequest.ts";
import {ElMessage} from "element-plus";
import {onMounted, ref, reactive} from 'vue';

type Marks = Record<number, string>
const marks = reactive<Marks>({
  0: '0°C',
  18: '18°C',
  22: '22°C',
  30: '30°C',
})

let workMode = ref('')
let range = ref([])

onMounted(() => {
  getWorkStatus()
})

async function getWorkStatus() {
  try {
    let r = await axiosRequest({
      url: '/getWorkStatus',
      method: 'get',
    })
    console.log(r.data)
    if (r.code == 1) {
      if (r.data.workmode == 'REFRIGERATION')
        workMode.value = '制冷模式'
      else if (r.data.workMode == 'HEATING')
        workMode.value = '供暖模式'
      range.value[0] = r.data.range[0] / 100
      range.value[1] = r.data.range[1] / 100
    } else
      throw new Error(r.msg || '操作失败')

  } catch (e: any) {
    ElMessage.error('出错了: ' + e.message);
  }
}

async function setWorkMode() {
  console.log(workMode.value, range.value)
  try {
    let _workMode = workMode.value == '制冷模式' ? 'REFRIGERATION' : 'HEATING';
    let r = await axiosRequest({
      url: '/setWorkStatus',
      method: 'post',
      params: {
        workMode: _workMode,
        firstValue: range.value[0],
        secondValue: range.value[1],
      }
    })

    if (r.code == 1)
      ElMessage.success('Work Mode Set Successfully');
    else
      throw new Error(r.msg || '操作失败')

  } catch (e: any) {
    ElMessage.error('出错了: ' + e.message);
  }
}

function changeMode(mode: number) {
  if (mode == 1)
    workMode.value = '制冷模式'
  else if (mode == 2)
    workMode.value = '供暖模式'
}
</script>

<style>

</style>
