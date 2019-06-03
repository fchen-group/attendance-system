const app = getApp()

Page({
  data: {
    name: '',
    studentNum: '',
    classInfo: ''
  },

  onLoad: function (options) {
    const openid = wx.getStorageSync("openid")
    wx.request({
      url: app.globalData.apiURL + '/v1/student?openId=' + openid,
      header: {
        'content-type': 'json'
      },
      fail: (res) => {
        console.log(res)
      },
      success: (res) => {
        this.setData({
          name: res.data.name,
          studentNum: res.data.studentNum,
          classInfo: res.data.classInfo
        })
      }
    })
  },

  handleModifyPwd() {
    wx.navigateTo({
      url: '../password/password',
    })
  }
})