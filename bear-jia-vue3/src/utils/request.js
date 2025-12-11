import axios from 'axios';
import { useUserStore } from '@/stores/user';
import { getToken } from '@/utils/auth';
import errorCode from '@/utils/errorCode';
import { notification } from 'ant-design-vue';
import { blobValidate, tansParams } from '@/utils/bearjia';
import BearJiaUtil from '@/utils/BearJiaUtil.js';

axios.defaults.headers['Content-Type'] = 'application/json;charset=utf-8';
// 创建axios实例
const service = axios.create({
    // axios中请求配置有baseURL选项，表示请求URL公共部分
    baseURL: import.meta.env.VITE_APP_BASE_API,
    // 超时
    timeout: 10000,
});
// 白名单接口，不需要token验证
const whiteList = [
    '/captchaImage',  // 验证码接口
    '/login'          // 登录接口
];

// request拦截器
service.interceptors.request.use(
    (config) => {
        const token = getToken();
        if (token) {
            config.headers['Authorization'] = 'Bearer ' + token;
        }
        // 判断是否为白名单接口
        const isWhiteList = whiteList.some(url => config.url.includes(url));
        // 是否需要设置 token
        const isToken = (config.headers || {}).isToken === false;
        if (!isWhiteList && !isToken && !token) {
            return Promise.reject('未登录或登录已过期');
        }
        // get请求映射params参数
        if (config.method === 'get' && config.params) {
            let url = config.url + '?';
            for (const propName of Object.keys(config.params)) {
                const value = config.params[propName];
                var part = encodeURIComponent(propName) + '=';
                if (value !== null && typeof value !== 'undefined') {
                    if (typeof value === 'object') {
                        for (const key of Object.keys(value)) {
                            if (value[key] !== null && typeof value[key] !== 'undefined') {
                                let params = propName + '[' + key + ']';
                                let subPart = encodeURIComponent(params) + '=';
                                url += subPart + encodeURIComponent(value[key]) + '&';
                            }
                        }
                    } else {
                        url += part + encodeURIComponent(value) + '&';
                    }
                }
            }
            url = url.slice(0, -1);
            config.params = {};
            config.url = url;
        }
        return config;
    },
    (error) => {
        console.error(error);
        return Promise.reject(error);
    }
);

// 响应拦截器
service.interceptors.response.use(
    (res) => {
        // 未设置状态码则默认成功状态
        const code = res.data.code || 200;
        // 获取错误信息
        const msg = errorCode[code] || res.data.msg || errorCode['default'];

        if (code === 401) {
            const userStore = useUserStore();
            notification.error({
                message: '系统提示',
                description: '登录状态已过期，请重新登录',
                duration: 3
            });
            userStore.logout().then(() => {
                location.href = '/login';
            });
            return Promise.reject('无效的会话，或者会话已过期，请重新登录。');
        } else if (code === 500) {
            notification.error({
                message: '系统提示',
                description: msg,
                duration: 3
            });
            return Promise.reject(new Error(msg));
        } else if (code !== 200) {
            notification.error({
                message: '系统提示',
                description: msg,
                duration: 3
            });
            return Promise.reject('error');
        } else {
            return res.data;
        }
    },
    (error) => {
        console.error('err' + error);
        let { message } = error;
        if (message == "Network Error") {
            message = "后端接口连接异常";
        } else if (message.includes("timeout")) {
            message = "系统接口请求超时";
        } else if (message.includes("Request failed with status code")) {
            message = "系统接口" + message.substr(message.length - 3) + "异常";
        }
        notification.error({
            message: '错误',
            description: message,
            duration: 3
        });
        return Promise.reject(error);
    }
);

// 通用下载方法
export function download(url, params, filename) {
    BearJiaUtil.messageInfo('开始下载数据，请稍候...');
    return service
        .get(url, params, {
            transformRequest: [
                (params) => {
                    return tansParams(params);
                },
            ],
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            responseType: 'blob',
        })
        .then(async (data) => {
            const isLogin = await blobValidate(data);
            if (isLogin) {
                const blob = new Blob([data]);
                saveAs(blob, filename);
            } else {
                const resText = await data.text();
                const rspObj = JSON.parse(resText);
                const errMsg = errorCode[rspObj.code] || rspObj.msg || errorCode['default'];
                notification.error({
                    message: '请求后端服务失败',
                    description: errMsg,
                    // duration: null,
                });
            }
        })
        .catch((r) => {
            console.error(r);
            notification.error({
                message: '请求后端服务失败',
                description: '下载文件出现错误，请联系管理员！',
                // duration: null,
            });
        });
}

export default service;
