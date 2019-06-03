const app = getApp()

Page({
  data: {
    username: '',
    password: '',
    logining: false,
    hasUserInfo: false,
    canIUse: wx.canIUse('button.open-type.getUserInfo')
  },

  handleInputUsername(e) {
    this.setData({ username: e.detail.value })
  },

  handleInputPassword(e) {
    this.setData({ password: e.detail.value })
  },

  handleLogin() {
    if (this.data.username === '' || this.data.password === '') {
      wx.showToast({
        title: '学号和密码都需要输入',
        icon: 'none',
        duration: 2000
      })
    } else {
      wx.showLoading({ title: '登录中...' })
      const openId = wx.getStorageSync('openid')
      const { username, password } = this.data
      wx.request({
        url: app.globalData.apiURL + '/v1/student/wxlogin',
        method: 'POST',
        header: {
          'content-type': 'application/json'
        },
        data: { username, password, openId },
        success: (res) => {
          wx.hideLoading()
          console.log(res)
          if (res.data.status === 'failed') {
            wx.showToast({
              title: '学号或者密码错误',
              icon: 'none',
              duration: 2000
            })
          } else if (res.data.status === 'success') {
            wx.switchTab({
              url: '../index/index',
            })
          }
        }
      })
    }
  }
})
