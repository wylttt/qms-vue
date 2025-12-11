import {defineStore} from 'pinia';
import {constantRoutes} from '@/router'
import {getRouters} from '@/api/system/menu';

/**
 * 使用meta.role判断当前用户是否有权限
 * @param roles
 * @param route
 */
function hasPermission(roles, route) {
    if (route.meta && route.meta.roles) {
        return roles.some(role => route.meta.roles.includes(role));
    } else {
        return true;
    }
}

/**
 * 递归过滤异步路由表，返回符合用户角色权限的路由表
 * @param routes asyncRoutes
 * @param roles
 */
function filterAsyncRoutes(routes, roles) {
    const res = [];
    routes.forEach(route => {
        const tmp = {...route};
        if (hasPermission(roles, tmp)) {
            if (tmp.children) {
                tmp.children = filterAsyncRoutes(tmp.children, roles);
            }
            res.push(tmp);
        }
    });

    return res;
}

// 预加载所有视图组件
const modules = import.meta.glob('../views/**/*.vue');

// 过滤异步路由
function filterAsyncRouter(asyncRouterMap, lastRouter = false, type = false) {
    return asyncRouterMap.filter(route => {
        // 检查是否是外链路由
        if (isExternalLink(route.path)) {
            // 外链路由不添加到Vue Router中，只在菜单中显示
            return false;
        }

        if (type && route.children) {
            route.children = filterChildren(route.children, lastRouter)
        }
        if (route.component) {
            // Layout ParentView 组件特殊处理
            if (route.component === 'Layout') {
                route.component = () => import('@/layout/BaseLayout.vue');
            } else if (route.component === 'ParentView') {
                route.component = () => import('@/layout/ParentView/index.vue');
            } else if (route.component === 'InnerLink') {
                route.component = () => import('@/layout/InnerLink/index.vue');
            } else {
                route.component = loadView(route.component)
            }
        }
        if (route.children && route.children.length) {
            route.children = filterAsyncRouter(route.children, route, type);
        } else {
            delete route['children']
            delete route['redirect']
        }
        return true;
    });
}

// 判断是否是外链
function isExternalLink(path) {
    return /^(https?:|mailto:|tel:)/.test(path);
}

// 过滤异步路由（用于侧边栏，保留外链）
function filterAsyncRouterForSidebar(asyncRouterMap, lastRouter = false, type = false) {
    return asyncRouterMap.filter(route => {
        if (type && route.children) {
            route.children = filterChildrenForSidebar(route.children, lastRouter)
        }
        if (route.component) {
            // Layout ParentView 组件特殊处理
            if (route.component === 'Layout') {
                route.component = () => import('@/layout/BaseLayout.vue');
            } else if (route.component === 'ParentView') {
                route.component = () => import('@/layout/ParentView/index.vue');
            } else if (route.component === 'InnerLink') {
                route.component = () => import('@/layout/InnerLink/index.vue');
            } else {
                route.component = loadView(route.component)
            }
        }
        if (route.children && route.children.length) {
            route.children = filterAsyncRouterForSidebar(route.children, route, type);
        }
        return true;
    });
}

function filterChildren(childrenMap, lastRouter = false) {
    var children = []
    childrenMap.forEach((el, index) => {
        // 跳过外链路由
        if (isExternalLink(el.path)) {
            return;
        }

        if (el.children && el.children.length) {
            if (el.component === 'ParentView' && !lastRouter) {
                el.children.forEach(c => {
                    // 跳过外链路由
                    if (isExternalLink(c.path)) {
                        return;
                    }
                    // 确保路径拼接时去除多余的斜杠
                    c.path = `${el.path}/${c.path}`.replace(/\/+/g, '/')
                    if (c.children && c.children.length) {
                        children = children.concat(filterChildren(c.children, c))
                        return
                    }
                    children.push(c)
                })
                return
            }
        }
        if (lastRouter) {
            // 确保路径拼接时去除多余的斜杠
            el.path = `${lastRouter.path}/${el.path}`.replace(/\/+/g, '/')
            if (el.children && el.children.length) {
                children = children.concat(filterChildren(el.children, el))
                return
            }
        }
        children = children.concat(el)
    })
    return children
}

function filterChildrenForSidebar(childrenMap, lastRouter = false) {
    var children = []
    childrenMap.forEach((el, index) => {
        if (el.children && el.children.length) {
            if (el.component === 'ParentView' && !lastRouter) {
                el.children.forEach(c => {
                    // 对于外链，不进行路径拼接
                    if (!isExternalLink(c.path)) {
                        // 确保路径拼接时去除多余的斜杠
                        c.path = `${el.path}/${c.path}`.replace(/\/+/g, '/')
                    }
                    if (c.children && c.children.length) {
                        children = children.concat(filterChildrenForSidebar(c.children, c))
                        return
                    }
                    children.push(c)
                })
                return
            }
        }
        if (lastRouter && !isExternalLink(el.path)) {
            // 确保路径拼接时去除多余的斜杠
            el.path = `${lastRouter.path}/${el.path}`.replace(/\/+/g, '/')
            if (el.children && el.children.length) {
                children = children.concat(filterChildrenForSidebar(el.children, el))
                return
            }
        }
        children = children.concat(el)
    })
    return children
}

// 路由懒加载
export const loadView = (view) => {
    let res;
    for (const path in modules) {
        const dir = path.split('views/')[1].split('.vue')[0];
        if (dir === view) {
            res = () => modules[path]();
        }
    }
    if (!res) {
        // console.warn(`找不到组件: ${view}，可用路径:`, Object.keys(modules));
        res = () => import('../views/error/404.vue');
    }
    return res;
}

// 动态路由遍历，验证是否具备权限
export function filterDynamicRoutes(routes) {
    const res = []
    routes.forEach(route => {
        if (route.permissions) {
            if (auth.hasPermiOr(route.permissions)) {
                res.push(route)
            }
        } else if (route.roles) {
            if (auth.hasRoleOr(route.roles)) {
                res.push(route)
            }
        }
    })
    return res
}

export const usePermissionStore = defineStore('permission', {
    state: () => ({
        routes: [],
        addRoutes: [],
        defaultRoutes: [],
        topbarRouters: [],
        sidebarRouters: []
    }),

    actions: {
        setRoutes(routes) {
            this.addRoutes = routes;
            this.routes = constantRoutes.concat(routes);
        },
        setDefaultRoutes(routes) {
            this.defaultRoutes = constantRoutes.concat(routes)
        },
        setTopbarRoutes(routes) {
            this.topbarRouters = routes
        },
        setSidebarRouters(routes) {
            this.sidebarRouters = routes
        },

        generateRoutes() {
            return new Promise(resolve => {
                // 向后端请求路由数据
                getRouters().then(res => {
                    const sdata = JSON.parse(JSON.stringify(res.data));
                    const rdata = JSON.parse(JSON.stringify(res.data));
                    const defaultData = JSON.parse(JSON.stringify(res.data));

                    // 侧边栏路由保留外链，用于菜单显示 - 注意传入 type=true
                    const sidebarRoutes = filterAsyncRouterForSidebar(sdata, false, true);
                    // Vue Router路由过滤掉外链，避免路由错误
                    const rewriteRoutes = filterAsyncRouter(rdata, false, true);
                    const defaultRoutes = filterAsyncRouterForSidebar(defaultData, false, true);
                    // const asyncRoutes = filterDynamicRoutes(dynamicRoutes)
                    // asyncRoutes.forEach(route => { router.addRoute(route) })
                    this.setRoutes(rewriteRoutes)
                    this.setSidebarRouters(sidebarRoutes)
                    this.setDefaultRoutes(sidebarRoutes)
                    this.setTopbarRoutes(defaultRoutes)
                    console.log('生成的路由:', rewriteRoutes);
                    console.log('侧边栏路由:', sidebarRoutes);
                    // this.setDefaultRoutes(defaultRoutes);
                    // this.topbarRouters = defaultRoutes;
                    // this.sidebarRouters = sidebarRoutes;
                    resolve(rewriteRoutes);
                });
            });
        }
    }
});
