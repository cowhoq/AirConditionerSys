import axios, {AxiosRequestConfig} from "axios";
import {ElMessage} from "element-plus";

/**
 * 与后端交互返回的数据
 * */
interface R<T> {
    code: number,
    msg: string,
    data: T,
}

axios.defaults.headers['Content-Type'] = 'application/json;charset=utf-8'
// 创建 axios 实例
const service = axios.create({
    baseURL: '/api', // axios 中请求配置有 baseURL 选项, 表示请求中 URL 的公共部分
    timeout: 60000 // 请求超时时间
})

// 请求拦截器
service.interceptors.request.use((config: any) => {
    // 当请求方法为 get 时, 映射 params 中的参数
    if (config.method === 'get' && config.params) {
        let url = config.url + '?';
        for (let propName of Object.keys(config.params)) {
            let value = config.params[propName];
            let part = encodeURIComponent(propName) + '=';
            if (value !== null && typeof (value) !== "undefined") {
                if (typeof value === 'object') {
                    for (let key of Object.keys(value)) {
                        let params = propName + '[' + key + ']';
                        let subPart = encodeURIComponent(params) + '=';
                        url += subPart + encodeURIComponent(value[key]) + '&';
                    }
                } else
                    url += part + encodeURIComponent(value) + '&';
            }
        }
        url = url.slice(0, -1);
        config.params = {};
        config.url = url;
    }
    return config
}, (error: any) => {
    console.log(error)
})

// 响应拦截器
service.interceptors.response.use((response: any) => {
        if (response.data.code === 0 && response.data.msg === 'NOTLOGIN') {
            // 遇到需要登录的情况, 从缓存中删除这两项
            localStorage.removeItem('userInfo')
            localStorage.removeItem('activePage')
            window.location.href = '/login'
        } else
            return response
    },
    (error: any) => {
        console.log('err: ' + error)
        let {message} = error;
        if (message == "Network Error")
            message = "后端接口连接异常";
        else if (message.includes("timeout"))
            message = "系统接口请求超时";
        else if (message.includes("Request failed with status code")) {
            message = "系统接口 (" + message.substring(message.length - 3) + ") 异常";
        }
        ElMessage({
            message: message,
            type: 'error',
            duration: 5000
        });
        return Promise.reject(error)
    }
)
export default async function axiosRequest<T = any>(config: AxiosRequestConfig) {
    return service.request<R<T>>(config).then((res) => res.data)
}