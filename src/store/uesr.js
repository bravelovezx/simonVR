import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    username: '',
    token: '',
  }),
  actions: {
    login(username, token) {
      this.username = username
      this.token = token
    },
    logout() {
      this.username = ''
      this.token = ''
    },
  },
  persist: {
    enabled: true,
    storage: sessionStorage, // 改成 sessionStorage
  },
})

