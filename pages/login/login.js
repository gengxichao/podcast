//input.js
Page({
  data:{
    tip:'',
    userName:'',
    password:''
  },

  setUsername:function(e){
    this.setData({
      "userName": e.detail.value
    })
  },
  setPassword:function(e){
    this.setData({
      "password": e.detail.value
    })
  },

  formBindsubmit:function(e){
    if(e.detail.value.userName.length==0||e.detail.value.password.length==0){
      this.setData({
        tip:'用户名、密码不能为空~',
        userName:'',
        password:''
      })
    }
    else{
      this.setData({
        tip:'登录中...'
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