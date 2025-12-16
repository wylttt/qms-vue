<template>
  <view class="edit-container">
    <view class="section">
      <view class="section-title">基本信息</view>
      
      <!-- OCR身份证识别 -->
      <view class="ocr-section">
        <button class="ocr-btn" @tap="ocrIdCard">
          <text class="icon">📷</text>
          <text>扫描身份证自动填写</text>
        </button>
      </view>

      <view class="form-item">
        <view class="label">姓名 <text class="required">*</text></view>
        <input 
          class="input" 
          v-model="formData.realName" 
          placeholder="请输入姓名"
        />
      </view>

      <view class="form-item">
        <view class="label">身份证号 <text class="required">*</text></view>
        <input 
          class="input" 
          v-model="formData.idCardNo" 
          placeholder="请输入身份证号"
          maxlength="18"
          @blur="parseIdCard"
        />
      </view>

      <view class="form-item">
        <view class="label">性别 <text class="required">*</text></view>
        <picker mode="selector" :range="genderOptions" range-key="label" @change="onGenderChange">
          <view class="picker">
            {{ formData.gender !== null ? genderOptions[formData.gender].label : '请选择性别' }}
          </view>
        </picker>
      </view>

      <view class="form-item">
        <view class="label">出生日期 <text class="required">*</text></view>
        <picker mode="date" :value="formData.birthDate" @change="onBirthDateChange">
          <view class="picker">
            {{ formData.birthDate || '请选择出生日期' }}
          </view>
        </picker>
      </view>

      <view class="form-item">
        <view class="label">联系电话 <text class="required">*</text></view>
        <input 
          class="input" 
          v-model="formData.phoneNumber" 
          type="number"
          placeholder="请输入联系电话"
          maxlength="11"
        />
      </view>
    </view>

    <view class="section">
      <view class="section-title">居住地址</view>

      <view class="form-item">
        <view class="label">省份 <text class="required">*</text></view>
        <picker mode="selector" :range="provinces" range-key="regionName" @change="onProvinceChange">
          <view class="picker">
            {{ selectedProvince || '请选择省份' }}
          </view>
        </picker>
      </view>

      <view class="form-item">
        <view class="label">城市 <text class="required">*</text></view>
        <picker mode="selector" :range="cities" range-key="regionName" @change="onCityChange">
          <view class="picker">
            {{ selectedCity || '请选择城市' }}
          </view>
        </picker>
      </view>

      <view class="form-item">
        <view class="label">区县 <text class="required">*</text></view>
        <picker mode="selector" :range="districts" range-key="regionName" @change="onDistrictChange">
          <view class="picker">
            {{ selectedDistrict || '请选择区县' }}
          </view>
        </picker>
      </view>

      <view class="form-item">
        <view class="label">街道/乡镇 <text class="required">*</text></view>
        <picker mode="selector" :range="streets" range-key="regionName" @change="onStreetChange">
          <view class="picker">
            {{ selectedStreet || '请选择街道/乡镇' }}
          </view>
        </picker>
      </view>

      <view class="form-item">
        <view class="label">社区/村 <text class="required">*</text></view>
        <picker mode="selector" :range="communities" range-key="regionName" @change="onCommunityChange">
          <view class="picker">
            {{ selectedCommunity || '请选择社区/村' }}
          </view>
        </picker>
      </view>

      <view class="form-item">
        <view class="label">详细地址</view>
        <textarea 
          class="textarea" 
          v-model="formData.detailAddress" 
          placeholder="请输入详细地址（街道门牌号）"
          maxlength="200"
        ></textarea>
      </view>
    </view>

    <view class="btn-group">
      <button class="submit-btn" @tap="handleSubmit">保存</button>
    </view>
  </view>
</template>

<script>
import { 
  addResident, 
  checkIdCard,
  getProvinces,
  getCities,
  getDistricts,
  getStreets,
  getCommunities
} from '@/api/gc.js';
import { smartOCRIdCard, validateIdCard } from '@/utils/ocrHelper.js';

export default {
  data() {
    return {
      formData: {
        realName: '',
        idCardNo: '',
        gender: null,
        birthDate: '',
        phoneNumber: '',
        provinceId: null,
        cityId: null,
        districtId: null,
        streetId: null,
        communityId: null,
        detailAddress: '',
        registrationType: '0' // 0:居民自主  1:调查员协助
      },
      genderOptions: [
        { label: '男', value: '0' },
        { label: '女', value: '1' }
      ],
      provinces: [],
      cities: [],
      districts: [],
      streets: [],
      communities: [],
      selectedProvince: '',
      selectedCity: '',
      selectedDistrict: '',
      selectedStreet: '',
      selectedCommunity: ''
    };
  },

  onLoad() {
    this.loadProvinces();
  },

  methods: {
    // OCR识别身份证
    async ocrIdCard() {
      try {
        uni.showLoading({ title: '识别中...' });
        
        // 调用智能OCR识别（自动选择最优方案）
        const result = await smartOCRIdCard();
        
        uni.hideLoading();
        
        if (result.success) {
          // 填充识别结果
          if (result.data.name) {
            this.formData.realName = result.data.name;
          }
          
          if (result.data.idCard) {
            this.formData.idCardNo = result.data.idCard;
            // 验证身份证号
            if (!validateIdCard(result.data.idCard)) {
              uni.showModal({
                title: '提示',
                content: '识别的身份证号校验失败，请手动核对',
                showCancel: false
              });
            } else {
              // 自动解析身份证信息
              this.parseIdCard();
            }
          }
          
          // 如果有地址信息，尝试自动填充行政区划
          if (result.data.address) {
            this.formData.detailAddress = result.data.address;
            // TODO: 可以调用parseRegionFromAddress解析行政区划
          }
          
          uni.showToast({
            title: `识别成功（${result.method}）`,
            icon: 'success',
            duration: 2000
          });
        } else {
          uni.showToast({
            title: result.message || '识别失败，请手动输入',
            icon: 'none',
            duration: 2000
          });
        }
      } catch (error) {
        uni.hideLoading();
        console.error('OCR识别失败', error);
        uni.showToast({
          title: '识别失败，请手动输入',
          icon: 'none'
        });
      }
    },

    // 解析身份证号
    parseIdCard() {
      const idCard = this.formData.idCardNo;
      if (!idCard || idCard.length !== 18) return;

      // 解析性别
      const genderCode = parseInt(idCard.charAt(16));
      this.formData.gender = (genderCode % 2 === 0) ? '1' : '0';

      // 解析出生日期
      const year = idCard.substring(6, 10);
      const month = idCard.substring(10, 12);
      const day = idCard.substring(12, 14);
      this.formData.birthDate = `${year}-${month}-${day}`;
    },

    // 性别选择
    onGenderChange(e) {
      this.formData.gender = this.genderOptions[e.detail.value].value;
    },

    // 出生日期选择
    onBirthDateChange(e) {
      this.formData.birthDate = e.detail.value;
    },

    // 加载省份列表
    async loadProvinces() {
      try {
        const res = await getProvinces();
        this.provinces = res.data || [];
      } catch (error) {
        console.error('加载省份失败', error);
      }
    },

    // 省份改变
    async onProvinceChange(e) {
      const index = e.detail.value;
      const province = this.provinces[index];
      this.selectedProvince = province.regionName;
      this.formData.provinceId = province.regionId;

      // 重置下级
      this.cities = [];
      this.districts = [];
      this.streets = [];
      this.communities = [];
      this.selectedCity = '';
      this.selectedDistrict = '';
      this.selectedStreet = '';
      this.selectedCommunity = '';

      // 加载城市
      try {
        const res = await getCities(province.regionId);
        this.cities = res.data || [];
      } catch (error) {
        console.error('加载城市失败', error);
      }
    },

    // 城市改变
    async onCityChange(e) {
      const index = e.detail.value;
      const city = this.cities[index];
      this.selectedCity = city.regionName;
      this.formData.cityId = city.regionId;

      // 重置下级
      this.districts = [];
      this.streets = [];
      this.communities = [];
      this.selectedDistrict = '';
      this.selectedStreet = '';
      this.selectedCommunity = '';

      // 加载区县
      try {
        const res = await getDistricts(city.regionId);
        this.districts = res.data || [];
      } catch (error) {
        console.error('加载区县失败', error);
      }
    },

    // 区县改变
    async onDistrictChange(e) {
      const index = e.detail.value;
      const district = this.districts[index];
      this.selectedDistrict = district.regionName;
      this.formData.districtId = district.regionId;

      // 重置下级
      this.streets = [];
      this.communities = [];
      this.selectedStreet = '';
      this.selectedCommunity = '';

      // 加载街道
      try {
        const res = await getStreets(district.regionId);
        this.streets = res.data || [];
      } catch (error) {
        console.error('加载街道失败', error);
      }
    },

    // 街道改变
    async onStreetChange(e) {
      const index = e.detail.value;
      const street = this.streets[index];
      this.selectedStreet = street.regionName;
      this.formData.streetId = street.regionId;

      // 重置下级
      this.communities = [];
      this.selectedCommunity = '';

      // 加载社区
      try {
        const res = await getCommunities(street.regionId);
        this.communities = res.data || [];
      } catch (error) {
        console.error('加载社区失败', error);
      }
    },

    // 社区改变
    onCommunityChange(e) {
      const index = e.detail.value;
      const community = this.communities[index];
      this.selectedCommunity = community.regionName;
      this.formData.communityId = community.regionId;
    },

    // 表单验证
    validate() {
      if (!this.formData.realName) {
        uni.showToast({ title: '请输入姓名', icon: 'none' });
        return false;
      }

      if (!this.formData.idCardNo) {
        uni.showToast({ title: '请输入身份证号', icon: 'none' });
        return false;
      }

      if (!/^\d{17}[\dXx]$/.test(this.formData.idCardNo)) {
        uni.showToast({ title: '身份证号格式不正确', icon: 'none' });
        return false;
      }

      // 使用增强的身份证号验证（包含校验码）
      if (!validateIdCard(this.formData.idCardNo)) {
        uni.showToast({ title: '身份证号校验失败', icon: 'none' });
        return false;
      }

      if (this.formData.gender === null) {
        uni.showToast({ title: '请选择性别', icon: 'none' });
        return false;
      }

      if (!this.formData.birthDate) {
        uni.showToast({ title: '请选择出生日期', icon: 'none' });
        return false;
      }

      if (!this.formData.phoneNumber) {
        uni.showToast({ title: '请输入联系电话', icon: 'none' });
        return false;
      }

      if (!/^1[3-9]\d{9}$/.test(this.formData.phoneNumber)) {
        uni.showToast({ title: '手机号格式不正确', icon: 'none' });
        return false;
      }

      if (!this.formData.provinceId) {
        uni.showToast({ title: '请选择省份', icon: 'none' });
        return false;
      }

      if (!this.formData.cityId) {
        uni.showToast({ title: '请选择城市', icon: 'none' });
        return false;
      }

      if (!this.formData.districtId) {
        uni.showToast({ title: '请选择区县', icon: 'none' });
        return false;
      }

      if (!this.formData.streetId) {
        uni.showToast({ title: '请选择街道/乡镇', icon: 'none' });
        return false;
      }

      if (!this.formData.communityId) {
        uni.showToast({ title: '请选择社区/村', icon: 'none' });
        return false;
      }

      return true;
    },

    // 提交表单
    async handleSubmit() {
      if (!this.validate()) {
        return;
      }

      try {
        // 先验证身份证号是否已存在
        uni.showLoading({ title: '验证中...' });
        const checkRes = await checkIdCard(this.formData.idCardNo);
        
        if (!checkRes.data) {
          uni.hideLoading();
          uni.showToast({
            title: '该身份证号已存在',
            icon: 'none'
          });
          return;
        }

        // 保存居民信息
        uni.showLoading({ title: '保存中...' });
        const res = await addResident(this.formData);
        uni.hideLoading();

        if (res.code === 200) {
          uni.showToast({
            title: '保存成功',
            icon: 'success'
          });

          // 跳转到问卷填写页面
          setTimeout(() => {
            uni.navigateTo({
              url: `/pages/questionnaire/fill?residentId=${res.data.residentId}`
            });
          }, 1500);
        }
      } catch (error) {
        uni.hideLoading();
        console.error('保存失败', error);
      }
    }
  }
};
</script>

<style scoped>
.edit-container {
  padding: 30rpx;
  background: #f5f5f5;
  min-height: 100vh;
}

.section {
  background: #ffffff;
  border-radius: 20rpx;
  padding: 30rpx;
  margin-bottom: 30rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333333;
  margin-bottom: 30rpx;
  padding-bottom: 20rpx;
  border-bottom: 2rpx solid #f0f0f0;
}

.ocr-section {
  margin-bottom: 30rpx;
}

.ocr-btn {
  width: 100%;
  height: 100rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #ffffff;
  border-radius: 10rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
}

.icon {
  font-size: 36rpx;
  margin-right: 10rpx;
}

.form-item {
  margin-bottom: 30rpx;
}

.label {
  font-size: 28rpx;
  color: #333333;
  margin-bottom: 15rpx;
  display: block;
}

.required {
  color: #ff4d4f;
}

.input, .picker, .textarea {
  width: 100%;
  height: 80rpx;
  line-height: 80rpx;
  border: 2rpx solid #e0e0e0;
  border-radius: 10rpx;
  padding: 0 20rpx;
  font-size: 28rpx;
}

.textarea {
  height: 150rpx;
  padding: 20rpx;
  line-height: 1.5;
}

.picker {
  color: #333333;
}

.btn-group {
  padding: 30rpx;
}

.submit-btn {
  width: 100%;
  height: 90rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #ffffff;
  border-radius: 10rpx;
  font-size: 32rpx;
  border: none;
}
</style>
