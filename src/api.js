// src/api.js
import axios from 'axios';

const apiClient = axios.create({
  baseURL: 'http://localhost:8080', // 根据你的后端服务地址进行修改
  headers: {
    'Content-Type': 'application/json'
  }
});

export default apiClient;
