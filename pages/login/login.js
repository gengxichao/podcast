//input.js
Page({
  data:{
    tip:'',
    userName:'',
    password:''
  },

  setUsername:function(e){
    this.setData({
      userName: e.detail.value
    })
  },
  setPassword:function(e){
    this.setData({
      password: e.detail.value
    })
  },

  formBindsubmit:function(e){
    var that = this
    if(e.detail.value.userName.length==0||e.detail.value.password.length==0){
      wx.showModal({
        title: '信息不完整',
        content: '请认真填写用户名和密码',
        showCancel: false
      })
    }
    else{
      wx.showToast({
            title: '登录中~',
            icon: 'loading',
            duration: 10000
      })
      
      var sendData = '{\n\
               "Request":"Login",\n\
               "UserName":"' + this.data.userName +'",\n\
               "UserPassword":"' + this.data.password +'",\n\
                }'
      console.log(sendData)
      wx.request({
        url: 'http://120.77.33.177:8000/login.Request', //仅为示例，并非真实的接口地址
        data: sendData,
        method: "POST",
        success: function(res) {
          var data = res.data
          if(data['Return_info'] == 'Login successfully')
          {
                  wx.hideToast()

                  wx.showToast({
                    title: '登陆成功~',
                    icon: 'success',
                    duration: 2000
                  })
                  
                  wx.setStorageSync('userSession', data['ConfirmCode'])
                  
                  var sendData = '{ \n\
                      "Request":"RequestForUserInfo",\n\
                      "SearchBy":"Username",\n\
                      "Content":"' + that.data.userName + '",\n\
                      }'

                   wx.request({
                    url: 'http://120.77.33.177:8000/Userinfo.test', //仅为示例，并非真实的接口地址
                    data: sendData,
                    method: "POST",
                    success: function(res) {
                      wx.setStorageSync('nickname', res.data['Nickname'])
                    }
                   })


                  wx.navigateBack()
          }
          else{
                  wx.hideToast()

                    wx.showModal({
                      title: '登录失败',
                      content: '请检查用户名密码后重新尝试',
                      showCancel: false
                    })

          }
          // console.log(res.data)


        }
      })
    }
  },
})