Page({
  data:{
    tip:"",
    username: "",
    password:"",
    gender:"",
    email:"",
    telphone:"",
    date:"",
    choosebirthday:"点击此处选取您的生日"
  },
  bindDateChange:function(e){
    this.setData({
      date: e.detail.value,
      choosebirthday:""
    })
  },
  formBindsubmit:function(e){
    var that = this
    if(e.detail.value.username.length==0||e.detail.value.password.length==0||e.detail.value.gender.length==0||e.detail.value.email.length==0||e.detail.value.telphone.length==0)
    {

      wx.showModal({
        title: '信息不完整',
        content: '所有字段都是必填项喔~',
        showCancel: false
      })

    }else{
      var sendData = '{ \n \
        "Request":"Register", \n \
        "UserName":"' + that.data.username + '", \n \
        "Password":"' + that.data.password + '", \n \
        "ComfirmedPassword":"' + that.data.password + '", \n \
        "NickName":"' + that.data.password + '", \n \
        "DateOfBirth":"' + that.data.password + '", \n \
        "Gender":"' + that.data.gender + '", \n \
        "Email":"' + that.data.email + '", \n \
        "PhoneNumber":"' + that.data.telphone + '", \n \
}'

      console.log(sendData)

      wx.request({
        url: 'http://120.77.33.177:8000/sign_up.Request', //仅为示例，并非真实的接口地址
        data: sendData,
        method: "POST",
        success: function(res) {
          var data = res.data
          
          console.log(data)

          wx.showToast({
            title: '注册成功~',
            icon: 'success',
            duration: 2000
          })

          wx.navigateBack()
        }
      })







      // wx.navigateBack({
      //   delta: 2, // 回退前 delta(默认为1) 页面
      //   success: function(res){
      //     // success
      //   },
      //   fail: function() {
      //     // fail
      //   },
      //   complete: function() {
      //     // complete
      //   }
      // })
      // this.setData({
      //   tip:'',
      //   username:e.detail.value.userName,
      //   password:e.detail.value.password,
      //   email:e.detail.value.email,
      //   telphone:e.detail.value.telphone,})
      // if(e.detail.value.gender == 'maie'){
      //   this.setdata({
      //     male:'true',
      //     female:'',
      //   })
      // }
      // else{
      //   this.setdata({
      //     male:'',
      //     female:'true',})
      // }
    }
  },

  formBindreset:function(){
      this.setData({
        tip:'',
        userName:'',
        password:'',
        gender:'',
        email:"",
        telphone:""
      })
  },
  onLoad:function(options){
    // 页面初始化 options为页面跳转所带来的参数    
  },
  onReady:function(){
    // 页面渲染完成   
  },
  onShow:function(){
    // 页面显示   
  },
  onHide:function(){
    // 页面隐藏    
  },
  onUnload:function(){
    // 页面关闭
  }
})