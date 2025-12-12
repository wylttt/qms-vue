/**
 * v-hasPermi 操作权限处理
 * Copyright (c) 2019 ruoyi
 */

import { useUserStore } from '@/stores/user';

export default {
  mounted(el, binding) {
    const { value } = binding;
    const all_permission = "*:*:*";
    const permissions = useUserStore().permissions;

    // 如果没有传权限值(null, undefined, 空数组)，则不进行权限控制，默认显示
    if (!value || (value instanceof Array && value.length === 0)) {
      return;
    }

    // 权限值必须是非空数组
    if (value instanceof Array && value.length > 0) {
      const permissionFlag = value;

      const hasPermissions = permissions.some(permission => {
        return all_permission === permission || permissionFlag.includes(permission);
      });

      if (!hasPermissions) {
        el.parentNode && el.parentNode.removeChild(el);
      }
    } else {
      throw new Error('请设置操作权限标签值');
    }
  }
};
