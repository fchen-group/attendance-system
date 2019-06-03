App({
  onLaunch: function () {
    // 展示本地存储能力
    var logs = wx.getStorageSync('logs') || []
    logs.unshift(Date.now())
    wx.setStorageSync('logs', logs)

    wx.showLoading({
      title: '加载中',
      mask: true
    })

    // 登录
    wx.login({
      success: res => {
        // 发送 res.code 到后台换取 openId, sessionKey, unionId
        const url = this.globalData.baseURL + '/user/openid?code=' + res.code;
        wx.request({
          url: url,
          header: {
            'content-type': 'json'
          },
          success: (res) => {
            const openid = res.data.openid
            wx.getStorageSync('openid') || wx.setStorageSync('openid', res.data.openid);
            wx.request({
              // url: this.globalData.baseURL + '/user/isExist?openid=' + openid,
              url: this.globalData.apiURL + '/v1/student/OpenIdExist/' + openid,
              header: {
                'content-type': 'json'
              },
              success: (res) => {
                if (res.data.status === "success") {
                  wx.switchTab({
                    url: '../index/index',
                  });
                } else {
                  // wx.redirectTo({
                  //   url: '../login/login',
                  // })
                }
              }, 
              fail: () => {
                // wx.redirectTo({
                //   url: '../login/login',
                // })
              }, complete: () => {
                wx.hideLoading()
              }
            })
          }
        })
      }
    })
  },

  globalData: {
    baseURL: 'https://www.inforsecszu.net',
    apiURL: 'https://api.inforsecszu.net'
  }
})