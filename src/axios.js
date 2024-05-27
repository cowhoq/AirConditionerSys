// src/axios.js
import axios from 'axios';

const instance = axios.create({
  baseURL: 'http://localhost:8080', // Adjust the URL to your backend
  timeout: 1000,
});

export default instance;
