import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  server:{
  proxy:{
    "/api/deepseek":{
target:"http://192.144.135.67:5000",//之后修改为后端url和端口
        changeOrigin:true
    },
      "/api":{
        target:"http://123.249.20.245:8080",//之后修改为后端url和端口
        changeOrigin:true
      }
      
    }
  },
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
})
