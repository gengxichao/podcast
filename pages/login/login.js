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
            duration: 2000
      })
      
      var sendData = '{ \
               "Request":"Login", \
               "UserName":"' + this.password +'", \
               "UserPassword":"' + this.userName +'", \
                }'

      wx.request({
        url: 'http://localhost/login.Request', //仅为示例，并非真实的接口地址
        data: sendData,
        success: function(res) {
          var data = res.data
          
          console.log(res.data)


        }
      })
    }
  },
})