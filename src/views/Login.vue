<template>
  <div class="reception">
    <h1>酒店前台</h1>
    <el-form @submit.prevent="registerCheckIn" class="window">
      <h2 style="text-align: center">入住登记</h2>
      <el-form-item label="入住登记">
        <el-input class="input" v-model="name" placeholder="输入客户姓名"></el-input>
        <el-input class="input" v-model="roomId" placeholder="输入房间号"></el-input>
        <el-button type="primary" native-type="submit">登记入住</el-button>
      </el-form-item>
    </el-form>

    <div class="window">
      <el-form @submit.prevent="printBill">
        <h2 style="text-align: center">打印账单</h2>
        <el-form-item label="输入房间号">
          <el-input class="input" v-model="roomId" placeholder="输入房间号"></el-input>
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
  </div>
</template>

<script setup lang="ts">
import {ref} from 'vue';
import axiosRequest from "@/utils/axiosRequest.js";
import {ElMessage} from "element-plus";


let name = ref('');
let roomId = ref('');

let reports = ref([])

async function registerCheckIn() {
  try {
    let r = await axiosRequest({
      // 通过姓名和房间号登记入住
      url: '/register',
      method: 'post',
      params: {
        name: name.value,
        roomId: roomId.value,
      }
    });

    if (r.code == 1)
      ElMessage.success('登记成功');
    else
      throw new Error(r.msg || '操作失败');
  } catch (e: any) {
    ElMessage.error('出错了: ' + e.message);
  }
}

async function printBill() {
  try {
    let r = await axiosRequest({
      // 根据房间号获取对应详单
      url: '/bill',
      method: 'get',
      params: {
        roomId: roomId.value
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

<style scoped>
.reception {
  padding: 20px;
}

.reception h1 {
  text-align: center;
  margin-bottom: 20px;
}

.reception .el-form-item {
  margin-bottom: 20px;
}

.input {
  width: 200px;
}

.window {
  border: 1px solid #ccc;
  padding: 20px;
  border-radius: 8px;
  background-color: #fff;
  height: 30vh;
}
</style>
