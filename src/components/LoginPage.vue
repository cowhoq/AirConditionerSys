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
/* import axios from 'axios'; */
import axiosRequest from '../axiosRequest';
export default {
  data() {
    return {
      roomNumber: '',
      idNumber: ''
    };
  },
  methods: {
    
    async login() {
    console.log(this.roomNumber, this.idNumber);
    try {
      let r = await axiosRequest({
        url: '/login',
        method: 'post',
        params: {
          roomId: this.roomNumber,
          name: this.idNumber,
          password: '123456'
        }
      })
      console.log('Response status:', r);
      if (r.data.code == 1) {
        console.log('Login successful:', r.data);
        this.$store.commit('SET_ROOM_NUMBER', this.roomNumber);  // 调用 mutation 并传递参数
        this.$router.push('/control-panel');
      } else{
        console.log('Login failed:', r.data);
        alert(r.data.message || '登录失败，请检查输入或稍后重试。');
      }
        
      
    } catch (r) {
        console.error('出错了: ' + r.message);
    }
  }
    /* login() {
      console.log('Login with room number:', this.roomNumber, 'and id number:', this.idNumber);
      axios.post('/api/login', {
        params: {
          roomId: this.roomNumber,
          name: this.idNumber,
          password: 'czl'
        }
      })
      .then(response => {
      console.log('Response status:', response.status);
      if (response.status === 200) {
        console.log('Login successful:', response.data);
        this.$store.dispatch('setRoomNumber', this.roomNumber);
        this.$router.push('/control-panel');
      } else {
        console.log('Login failed:', response.data);
        alert(response.data.message || '登录失败，请检查输入或稍后重试。');
      }
    })
    .catch(error => {
      console.error('Error on login:', error);
      alert(error.response && error.response.data && error.response.data.message ? error.response.data.message : '登录失败，请检查输入或稍后重试。');
    });
    } */
  }
}
</script>

<style scoped>
.login-container {
  max-width: 400px;
  margin: 100px auto;
  padding: 30px;
  background: rgba(255, 255, 255, 0.9); /* 轻微透明的白色背景 */
  border-radius: 10px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  border: none;
  backdrop-filter: blur(5px); /* 背景模糊效果，仅在支持的浏览器有效 */
}

body {
  background-image: url('https://your-image-url.jpg'); /* 替换为你的图片URL */
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
}

.form-group {
  margin-bottom: 25px;
}

input[type="text"], input[type="password"] {
  width: 100%;
  padding: 12px;
  font-size: 14px;
  border: 2px solid #ccc;
  border-radius: 5px;
  transition: all 0.3s ease-in-out;
}

input[type="text"]:focus, input[type="password"]:focus {
  border-color: #007BFF;
  box-shadow: 0 0 8px rgba(0, 123, 255, 0.8); /* 蓝色光晕聚焦效果 */
}

button {
  width: 100%;
  padding: 12px;
  background-color: #007BFF;
  color: white;
  border-radius: 5px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s ease-in-out;
}

button:hover, button:focus {
  background-color: #0056B3;
  box-shadow: 0 5px 15px rgba(0, 56, 179, 0.4);
}


</style>