<template>
  <div class="login-container">
    <h1>登录</h1>
    <form @submit.prevent="login">
      <div class="form-group">
        <label for="roomNumber">房间号:</label>
        <input type="text" id="roomNumber" v-model="roomNumber" required>
      </div>
      <div class="form-group">
        <label for="idNumber">身份证号:</label>
        <input type="text" id="idNumber" v-model="idNumber" required>
      </div>
      <button type="submit">登录</button>
    </form>
  </div>
</template>

<script>
export default {
  data() {
    return {
      roomNumber: '',
      idNumber: ''
    };
  },
  methods: {
    login() {
      // 这里假设验证总是成功
      this.$router.push('/control-panel');
    }
  }
}
</script>

<style scoped>
.login-container {
  max-width: 400px; /* 限制容器宽度 */
  margin: 50px auto; /* 垂直和水平居中 */
  padding: 20px; /* 内边距 */
  background-color: #ffffff; /* 背景颜色 */
  border-radius: 8px; /* 边角圆滑 */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); /* 阴影效果，增加立体感 */
  border: 1px solid #ccc; /* 边框 */
}

.form-group {
  margin-bottom: 20px; /* 表单组件之间的间距 */
}

label {
  display: block; /* 让标签独占一行 */
  font-size: 16px; /* 标签字体大小 */
  color: #333; /* 标签字体颜色 */
  margin-bottom: 5px; /* 标签与输入框之间的间距 */
}

input[type="text"], input[type="password"] {
  width: 100%; /* 输入框宽度充满容器 */
  padding: 10px; /* 输入框内边距 */
  font-size: 14px; /* 输入框字体大小 */
  border: 1px solid #ccc; /* 输入框边框 */
  border-radius: 4px; /* 输入框边角圆滑 */
  box-sizing: border-box; /* box-sizing方式确保元素的宽度包含padding和border */
}

button {
  width: 100%; /* 按钮宽度充满容器 */
  padding: 12px; /* 按钮内边距 */
  background-color: #007BFF; /* 按钮背景颜色 */
  color: white; /* 按钮字体颜色 */
  font-size: 16px; /* 按钮字体大小 */
  border: none; /* 去除边框 */
  border-radius: 4px; /* 边角圆滑 */
  cursor: pointer; /* 鼠标悬停时显示指针 */
  transition: background-color 0.3s; /* 背景色过渡动画 */
}

button:hover {
  background-color: #0056b3; /* 鼠标悬停时的背景色 */
}

</style>
