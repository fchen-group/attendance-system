const app = getApp()

Page({
  data: {
    oldPassword: '',
    newPassword: '',
    cfmPassword: ''
  },

  handleInputOldPwd(e) {
    this.setData({ oldPassword: e.detail.value })
  },
  
  handleInputNewPwd(e) {
    this.setData({ newPassword: e.detail.value })
  },

  handleInputCfmPwd(e) {
    this.setData({ cfmPassword: e.detail.value })
  },

  handleSubmitPwd() {    
    const { oldPassword, newPassword, cfmPassword } = this.data
    if (oldPassword === '' || newPassword === '' || cfmPassword === '') {
      wx.showModal({
        title: '提示',
        content: '信息未填写完整！'
      })
    } else if(newPassword !== cfmPassword) {
      wx.showModal({
        title: '提示',
        content: '两次输入的新密码不一致！'
      })
    } else {
      const openId = wx.getStorageSync('openid');
      wx.request({
        url: app.globalData.apiURL + '/v1/student/password',
        method: "PUT",
        data: {
          oldPassword,
          newPassword,
          openId
        },
        header: {
          'content-type': 'application/json'
        },
        success: (res) => {
          if (res.data.status === "success") {
            wx.showToast({
              duration: 1500,
              title: '修改成功',
              success: () => {
                setTimeout(() => {
                  wx.navigateBack()
                }, 1500)
              }
            })
            
          } else {
            wx.showModal({
              title: '修改失败',
              content: '请检查旧密码是否正确，或者联系老师修改'
            })
          }
        }
      })
    }
  }
})