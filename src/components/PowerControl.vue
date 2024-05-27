<template>
  <div style="text-align: center">
    <el-button type="primary" @click="powerOn">开机</el-button>
    <el-button type="danger" @click="powerOff">关机</el-button>
    <h3>{{ workMode }}</h3>
  </div>
</template>

<script setup lang="ts">
import axiosRequest from "@/utils/axiosRequest.ts";
import {ElMessage} from "element-plus";
import {computed} from "vue";

let props = defineProps({
  workMode: String
})
let emit = defineEmits(['refresh'])

let workMode = computed(() => {
  let _workMode = props.workMode
  if (_workMode == 'OFF')
    return "主机尚未启动"
  else if (_workMode == 'HEATING' || _workMode == 'REFRIGERATION')
    return "主机已经启动"
  else
    return "未知模式"
})

async function powerOn() {
  let _workMode = props.workMode
  if (_workMode != 'OFF')
    return ElMessage.warning('主机已经启动')
  try {
    let r = await axiosRequest({
      url: '/powerOn',
      method: 'get',
    })

    if (r.code == 1) {
      emit('refresh', true) // 通知主界面刷新界面
      ElMessage.success('Power On Successful');
    } else
      throw new Error(r.msg || '操作失败')

  } catch (e: any) {
    ElMessage.error('出错了: ' + e.message);
  }
}

async function powerOff() {
  let _workMode = props.workMode
  if (_workMode == 'OFF')
    return ElMessage.warning('主机已经关机')

  try {
    let r = await axiosRequest({
      url: '/powerOff',
      method: 'get',
    })

    if (r.code == 1) {
      emit('refresh', true) // 通知主界面刷新界面
      ElMessage.success('Power Off Successful');
    }
    else
      throw new Error(r.msg || '操作失败')

  } catch (e: any) {
    ElMessage.error('出错了: ' + e.message);
  }
}
</script>
