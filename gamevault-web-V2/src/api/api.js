// src/api/api.js
import axios from "axios";

const api = axios.create({
  baseURL: '/api', // 和后端保持一致
});

api.interceptors.request.use(config => {
  const token = sessionStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  console.log("当前请求带的 headers:", config.headers);
  return config;
});

export default api;