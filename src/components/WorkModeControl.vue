<template>
  <div style="text-align: center">
    <text style="font-size: 25px; font-weight: bold; margin-bottom: 30px">{{ workMode }}</text>
    <el-form @submit.prevent="setRange">
      <el-form-item label="工作模式">
        <el-button type="primary" @click="changeWorkMode('REFRIGERATION')">制冷模式</el-button>
        <el-button type="danger" @click="changeWorkMode('HEATING')">供暖模式</el-button>
      </el-form-item>

      <el-form-item label="工作温度">
        <el-slider v-model="range" range :marks="marks" :max="40"/>
      </el-form-item>

      <el-form-item v-if="flag">
        <text style="font-size: 25px; font-weight: bold; margin-bottom: 30px">工作温度修改但未提交</text>
      </el-form-item>

      <el-button type="primary" native-type="submit" style="margin-top: 30px">设置工作温度</el-button>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import axiosRequest from "@/utils/axiosRequest.ts";
import {ElMessage} from "element-plus";
import {onMounted, ref, reactive, watch} from 'vue';

type Marks = Record<number, string>
const marks = reactive<Marks>({
  0: '0°C',
  18: '18°C',
  22: '22°C',
  30: '30°C',
})

let workMode = ref('')
let range = ref([])
let flag = ref(false)

onMounted(() => {
  getWorkStatus()
})
watch(range, () => {
  flag.value = true
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
      else if (r.data.workmode == 'HEATING')
        workMode.value = '供暖模式'

      range.value[0] = r.data.range[0] / 100
      range.value[1] = r.data.range[1] / 100
    } else
      throw new Error(r.msg || '操作失败')

  } catch (e: any) {
    ElMessage.error('出错了: ' + e.message);
  }
}

async function setRange() {
  try {
    let r = await axiosRequest({
      url: '/setRange',
      method: 'post',
      params: {
        firstValue: range.value[0],
        secondValue: range.value[1],
      }
    })

    if (r.code == 1) {
      ElMessage.success('Work Mode Set Successfully');
      // 如果修改成功则重新获取工作状态
      await getWorkStatus()
      flag.value = false
    } else
      throw new Error(r.msg || '操作失败')

  } catch (e: any) {
    ElMessage.error('出错了: ' + e.message);
  }
}

async function changeWorkMode(_workMode: string) {
  try {
    let r = await axiosRequest({
      url: '/setWorkMode',
      method: 'post',
      params: {
        workMode: _workMode,
      }
    })

    if (r.code == 1) {
      ElMessage.success('Work Mode Set Successfully');
      // 如果修改成功则重新获取工作状态
      await getWorkStatus()
    } else
      throw new Error(r.msg || '操作失败')

  } catch (e: any) {
    ElMessage.error('出错了: ' + e.message);
  }
}
</script>

<style>

</style>
