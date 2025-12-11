import { defineStore } from 'pinia';
import { login, logout, getInfo } from '@/api/login';
import { getToken, setToken, removeToken } from '@/utils/auth';

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken(),
    name: '',
    avatar: '',
    roles: [],
    permissions: [],
    nickName: '',
    loginDate: '',
    loginIp: ''
  }),

  getters: {
    // 用户信息对象，用于向后兼容
    userInfo: (state) => ({
      userName: state.name,
      nickName: state.nickName,
      avatar: state.avatar,
      roles: state.roles,
      permissions: state.permissions,
      loginDate: state.loginDate,
      loginIp: state.loginIp
    })
  },

  actions: {
    // 登录
    async login(loginInfo) {
      try {
        const res = await login({
          username: loginInfo.username,
          password: loginInfo.password,
          code: loginInfo.code,
          uuid: loginInfo.uuid
        });
        setToken(res.token);
        this.token = res.token;
      } catch (error) {
        throw error;
      }
    },

    // 获取用户信息
    async getInfo() {
      try {
        const res = await getInfo();
        this.roles = res.roles;
        this.name = res.user.userName;
        this.avatar = res.user.avatar;
        this.permissions = res.permissions;
        this.nickName = res.user.nickName;
        this.loginDate = res.user.loginDate;
        this.loginIp = res.user.loginIp;
        return res;
      } catch (error) {
        throw error;
      }
    },

    // 退出系统
    async logout() {
      try {
        await logout();
        this.token = '';
        this.roles = [];
        this.permissions = [];
        removeToken();
      } catch (error) {
        throw error;
      }
    },

    // 前端 登出
    fedLogout() {
      this.token = '';
      removeToken();
    }
  }
});