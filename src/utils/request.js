import axios from "axios";
import { useUserStore } from '@/store/uesr';

const request=axios.create({
  withCredentials: true, // 允许携带cookie
    timeout:10000,
    //使用formdata类型传递显示覆盖
    headers: {
    'Content-Type': 'application/json;charset=UTF-8'
  }

})

// 请求拦截器：自动添加 token
request.interceptors.request.use(config => {
  const userStore = useUserStore();

  if (userStore.token) {
    config.headers['Authorization'] = `Bearer ${userStore.token}`;
  }

  return config;
}, error => {
  console.error("请求拦截错误", error);
  return Promise.reject(error);
});


// 响应拦截器（统一错误处理）
request.interceptors.response.use(
  response => {
    console.log("响应拦截器", response);
    // 如果后端返回业务状态码，可以在此统一处理（示例）
    const res = response.data;
    if (response.status == 200) { // 假设 code 0 表示成功
      return res; // 成功时直接返回数据
      // console.error(res.message || '业务错误');
      // return Promise.reject(new Error(res.message || 'Error'));
    }
    else if(response.status==400){
      console.error(res.message || '业务错误');
      return res.message || '业务错误';
    }
      else if(response.status==403){
      console.error(res.message || '业务错误');
      return res.message;
    }
    
  },
  error => {
    let errorMessage = '请求错误';
    if (!error.response) {
      errorMessage = '网络连接异常或请求超时';
    } else {
      switch (error.response.status) {
        case 400:
          errorMessage = response.data.message;
          console.log("1111111111111111111111111111111111111");
          break;
        case 401:
          errorMessage = "未授权，请重新登录";
          // 自动跳转登录（根据项目需求）
          // router.replace('/login');
          break;
        case 403:
          errorMessage = "拒绝访问";
          break;
        case 404:
          errorMessage = `请求地址不存在: ${error.response.config.url}`;
          break;
        case 500:
          errorMessage = "服务器内部错误";
          break;
      }
    }
    console.error(errorMessage);
    // 返回统一错误对象
    return Promise.reject(new Error(errorMessage));
  }
);


export default request