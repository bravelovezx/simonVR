import axios from "axios";

const request=axios.create({
    timeout:10000,

})

// 响应拦截器（统一错误处理）
request.interceptors.response.use(
    response => {
      // 直接返回业务数据（根据后端数据结构调整）
      return response.data;
    },
    error => {
      // 统一错误处理
      if (error.response) {
        switch (error.response.status) {
          case 401:
            console.error("未授权，请重新登录");
            break;
          case 404:
            console.error("请求资源不存在");
            break;
          case 500:
            console.error("服务器错误");
            break;
          default:
            console.error(`请求错误：${error.message}`);
        }
      }
      return Promise.reject(error);
    }
  );

export default request