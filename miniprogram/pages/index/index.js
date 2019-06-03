const app = getApp()

Page({
  data: {
    // longitude: 0,
    // latitude: 0,
    showCurrent: true,
    currentSign: [],
    historySign: []
  },

  selectTab(e) {
    if (e.target.dataset.tabId === 'ended') {
      this.loadHistorySign()
      this.setData({
        showCurrent: false
      })
    } else if (e.target.dataset.tabId === 'current') {
      this.loadCurrentSign()
      this.setData({
        showCurrent: true
      })
    }
  },

  scan(e) {
    wx.scanCode({
      onlyFromCamera: true,
      success: (res) => {
        const url = res.result
        wx.getLocation({
          success: (res) => {
            const latitude = res.latitude
            const longitude = res.longitude
            const accuracy = res.accuracy
            const openId = wx.getStorageSync('openid')
            // const openId = '123'
            const { model } = wx.getSystemInfoSync()
            wx.showLoading({
              title: '签到中',
              mask: true
            })
            wx.request({
              url: url,
              method: "PUT",
              data: {
                openId,
                longitude,
                accuracy,
                latitude,
                device_no: model,
                signId: String(e.currentTarget.dataset.signid)
              },
              header: {
                'content-type': 'application/json'
              },
              success: (res) => {
                wx.hideLoading()
                if (res.data.status === 'success') {
                  wx.showToast({
                    title: '签到成功！',
                  })
                  const signId = e.currentTarget.dataset.signid
                  const newList = this.data.currentSign
                  for (let i = 0; i < newList.length; i ++) {
                    if (newList[i].signId === signId) {
                      newList[i].state = 3
                      break
                    }
                  }
                  this.setData({
                    currentSign: newList
                  })

                  // setTimeout(() => {
                  //   this.loadCurrentSign()
                  // }, 1500)
                } else {
                  if (res.data.msg === 'token跟原存储的token不一致，或加密后token为null!') {
                    wx.showModal({
                      title: '签到失败',
                      content: '二维码已过期，请重新扫码'
                    })
                  } else {
                    wx.showModal({
                      title: '签到失败',
                      content: res.data.msg
                    })
                  }
                }
              }
            })
          },
          fail: () => {
            // wx.showToast({
            //   title: '签到失败。（获取地理位置性信息失败, 请授权）',
            //   icon: 'none'
            // })
            const latitude = -1
            const longitude = -1
            const accuracy = -1
            const openId = wx.getStorageSync('openid')
            const { model } = wx.getSystemInfoSync()
            wx.showLoading({
              title: '签到中',
              mask: true
            })
            wx.request({
              url: url,
              method: "PUT",
              data: {
                openId,
                longitude,
                accuracy,
                latitude,
                device_no: model,
                signId: String(e.currentTarget.dataset.signid)
              },
              header: {
                'content-type': 'application/json'
              },
              success: (res) => {
                wx.hideLoading()
                if (res.data.status === 'success') {
                  wx.showToast({
                    title: '签到成功！(未获取到地理位置信息)',
                  })
                  const signId = e.currentTarget.dataset.signid
                  const newList = this.data.currentSign
                  for (let i = 0; i < newList.length; i++) {
                    if (newList[i].signId === signId) {
                      newList[i].state = 3
                      break
                    }
                  }
                  this.setData({
                    currentSign: newList
                  })

                  // setTimeout(() => {
                  //   this.loadCurrentSign()
                  // }, 1500)
                } else {
                  if (res.data.msg === 'token跟原存储的token不一致，或加密后token为null!') {
                    wx.showModal({
                      title: '签到失败',
                      content: '二维码已过期，请重新扫码'
                    })
                  } else {
                    wx.showModal({
                      title: '签到失败',
                      content: res.data.msg
                    })
                  }
                }
              }
            })
          }
        })
      },
      fail: () => {
        wx.showToast({
          title: '扫码失败',
          icon: 'none'
        })
      }
    })
  },

  handleScan(e) {
    wx.getSetting({
      success: res => {
        if (!res.authSetting['scope.userLocation']) {
          wx.authorize({
            scope: 'scope.userLocation',
            success: () => {
              this.scan(e)
            },
            fail: () => {
              console.log('???what')
              wx.showModal({
                title: '需要授权当前位置',
                content: '需要获取您的地理位置，请确认授权，否则无法完成签到',
                success: function (res) {
                  if (res.cancel) {
                    wx.showToast({
                      title: '授权失败',
                      icon: 'success',
                      duration: 1000
                    })
                  } else if (res.confirm) {
                    wx.openSetting({
                      success: function (dataAu) {
                        if (dataAu.authSetting["scope.userLocation"] == true) {
                          wx.showToast({
                            title: '授权成功',
                            icon: 'success',
                            duration: 1000
                          })
                        } else {
                          wx.showToast({
                            title: '授权失败',
                            icon: 'success',
                            duration: 1000
                          })
                        }
                      }
                    })
                  }
                }
              })
            }
          })
        } else {
          this.scan(e)
        }
      }
    })
  },

  loadCurrentSign() {
    const openid = wx.getStorageSync('openid');
    wx.request({
      url: app.globalData.apiURL + '/v1/student/sign?type=1&openId=' + openid,
      header: {
        'content-type': 'json'
      },
      success: (res) => {
        this.setData({
          currentSign: res.data.list
        })
        wx.stopPullDownRefresh()
      }
    })
  },

  loadHistorySign() {
    const openid = wx.getStorageSync('openid');
    wx.request({
      url: app.globalData.apiURL + '/v1/student/sign?type=0&openId=' + openid,
      header: {
        'content-type': 'json'
      },
      success: (res) => {
        this.setData({
          historySign: res.data.list
        })
        wx.stopPullDownRefresh()
      }
    })
  },

  moveToCenter() {
    this.mapCtx = wx.createMapContext('map')
    this.mapCtx.moveToLocation()
  },

  onLoad: function(options) {
    this.loadCurrentSign()
    wx.getLocation({
      success(res) {

      },
      fail() {
        console.log('获取地理位置失败')
      }

    })
  },

  onReady: function() {
    // this.moveToCenter()
  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function() {
    
  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function() {
    // this.moveToCenter()
    if (this.data.showCurrent) {
      this.loadCurrentSign()
    } else {
      this.loadHistorySign()
    }
  }
})