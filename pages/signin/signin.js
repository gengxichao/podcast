Page({
  data:{
    tip:"",
    username: "",
    password:"",
    nickname:"",
    gender:"",
    male:"",
    female:"",
    email:"",
    telphone:"",
    date:"",
    choosebirthday:"点击此处选取您的生日"
  },
  bindDateChange:function(e){
    console.log("date:")
    console.log(e.detail.value)
    this.setData({
      date: e.detail.value,
      choosebirthday:""
    })
  },
  formBindsubmit:function(e){
    var that = this
    //邮箱正则表达式验证
    console.log(e.detail.value)
    function checkemail(email_addr){
      var regex = /^([0-9A-Za-z\-_\.]+)@([0-9a-z]+\.[a-z]{2,3}(\.[a-z]{2})?)$/g;
      if (regex.test(email_addr))
      {
          return true
      }
      else{
        return false
      }
    }
    if(e.detail.value.username.length==0||e.detail.value.password.length==0||e.detail.value.nickname.length==0||e.detail.value.gender.length==0||e.detail.value.email.length==0||e.detail.value.telphone.length==0)
    {
      wx.showModal({
        title: '信息不完整',
        content: '所有字段都是必填项喔~',
        showCancel: false
      })
    }
    else if(e.detail.value.password != e.detail.value.repassword){
        wx.showModal({
        title: '确认密码不相同',
        content: '请重新输入确认密码~',
        showCancel: false
      })
    }
    else if(!checkemail(e.detail.value.email)){
        wx.showModal({
        title: '邮箱格式不正确',
        content: '请重新输入邮箱~',
        showCancel: false
      })
    }
    else if(e.detail.value.telphone.length != 11){
        wx.showModal({
        title: '手机号码位数不正确',
        content: '请重新输入手机号~',
        showCancel: false
      })
    }
    else{
      that.setData({
        username:e.detail.value.username,
        nickname:e.detail.value.nickname,
        password:e.detail.value.password,
        email:e.detail.value.email,
        telphone:e.detail.value.telphone,
        gender:e.detail.value.gender,
      })
      if(e.detail.value.gender == "male"){
        male:"true"
        female:"false"
      }
      else{
        male:"false"
        female:"true"       
      }
      var sendData = '{ \n \
        "Request":"Register", \n \
        "UserName":"' + that.data.username + '", \n \
        "Password":"' + that.data.password + '", \n \
        "Repassword":"'+ that.data.password + '", \n \
        "NickName":"' + that.data.nickname + '", \n \
        "DateOfBirth":"' + that.data.date + '", \n \
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