/**
 * 行政区划缓存管理工具
 * 优化行政区划加载速度，避免重复请求
 */

const CACHE_KEY_PREFIX = 'region_cache_';
const CACHE_EXPIRE_TIME = 24 * 60 * 60 * 1000; // 24小时过期

/**
 * 缓存数据结构
 * {
 *   data: [],           // 实际数据
 *   timestamp: number   // 缓存时间戳
 * }
 */

/**
 * 设置缓存
 * @param {String} key - 缓存键
 * @param {Any} data - 缓存数据
 */
export function setCache(key, data) {
  const cacheData = {
    data: data,
    timestamp: Date.now()
  };
  
  try {
    uni.setStorageSync(CACHE_KEY_PREFIX + key, JSON.stringify(cacheData));
  } catch (error) {
    console.error('设置缓存失败:', error);
  }
}

/**
 * 获取缓存
 * @param {String} key - 缓存键
 * @returns {Any|null} 缓存数据，如果不存在或过期则返回null
 */
export function getCache(key) {
  try {
    const cacheStr = uni.getStorageSync(CACHE_KEY_PREFIX + key);
    if (!cacheStr) {
      return null;
    }
    
    const cacheData = JSON.parse(cacheStr);
    const now = Date.now();
    
    // 检查是否过期
    if (now - cacheData.timestamp > CACHE_EXPIRE_TIME) {
      // 删除过期缓存
      removeCache(key);
      return null;
    }
    
    return cacheData.data;
  } catch (error) {
    console.error('获取缓存失败:', error);
    return null;
  }
}

/**
 * 删除缓存
 * @param {String} key - 缓存键
 */
export function removeCache(key) {
  try {
    uni.removeStorageSync(CACHE_KEY_PREFIX + key);
  } catch (error) {
    console.error('删除缓存失败:', error);
  }
}

/**
 * 清空所有区划缓存
 */
export function clearAllRegionCache() {
  try {
    const keys = uni.getStorageInfoSync().keys || [];
    keys.forEach(key => {
      if (key.startsWith(CACHE_KEY_PREFIX)) {
        uni.removeStorageSync(key);
      }
    });
  } catch (error) {
    console.error('清空缓存失败:', error);
  }
}

/**
 * 获取缓存键名
 * @param {String} type - 区划类型（province/city/district/street/community）
 * @param {String} parentId - 父级ID
 * @returns {String} 缓存键名
 */
export function getCacheKey(type, parentId = '') {
  return `${type}_${parentId}`;
}

/**
 * 带缓存的区划数据获取
 * @param {Function} apiFn - API请求函数
 * @param {String} cacheKey - 缓存键
 * @returns {Promise} API响应数据
 */
export async function getCachedRegionData(apiFn, cacheKey) {
  // 先尝试从缓存获取
  const cachedData = getCache(cacheKey);
  if (cachedData) {
    console.log(`从缓存加载: ${cacheKey}`);
    return Promise.resolve({ data: cachedData });
  }
  
  // 缓存不存在，请求API
  console.log(`从API加载: ${cacheKey}`);
  const response = await apiFn();
  
  // 保存到缓存
  if (response && response.data) {
    setCache(cacheKey, response.data);
  }
  
  return response;
}

/**
 * 预加载常用区划数据
 * 在应用启动时调用，预先加载省市区数据
 */
export async function preloadRegionData(getProvinces, getCities, getDistricts) {
  try {
    // 加载省份数据
    const provinceKey = getCacheKey('province');
    if (!getCache(provinceKey)) {
      const provinces = await getProvinces();
      setCache(provinceKey, provinces.data);
    }
    
    // 可选：预加载热门城市数据
    // TODO: 根据实际需求配置热门省份ID
    const hotProvinceIds = ['360000']; // 江西省
    for (const provinceId of hotProvinceIds) {
      const cityKey = getCacheKey('city', provinceId);
      if (!getCache(cityKey)) {
        const cities = await getCities(provinceId);
        setCache(cityKey, cities.data);
      }
    }
    
    console.log('区划数据预加载完成');
  } catch (error) {
    console.error('区划数据预加载失败:', error);
  }
}
