//personal.js
Page({
  data:{
    username: "Wu Haoyu",
    nickname: 'ErZi',
    password:"123456",
    email:"shawnzhu2051@gamil.com",
    telphone:"13500000000",
    date:"2016-01-01",
    gender:"male",
    male:"true",
    female:"",
    fixed:"true"
    // subscribe:[
    //       {img_path : "http://www.server110.com/template/images/logo.png",
    //        img_name : "pic no.1.1"},
    //       {img_path : "http://www.server110.com/template/images/logo.png",
    //       img_name : "pic no.1.2"},
    //       {img_path : "http://www.server110.com/template/images/logo.png",
    //       img_name : "pic no.1.3"},
    //       {img_path : "http://www.server110.com/template/images/logo.png",
    //       img_name : "pic no.1.4"},
    //       {img_path : "http://files.jb51.net/file_images/article/201307/201307130957593.jpg",
    //      img_name : "pic no.1.5"},
    //       {img_path : "http://www.server110.com/template/images/logo.png",
    //       img_name : "pic no.2.1"},
    //       {img_path : "http://files.jb51.net/file_images/article/201307/201307130957593.jpg",
    //       img_name : "pic no.2.2"},
    //       {img_path : "http://www.zhuchenshawn.com/images/pic07.jpg",
    //       img_name : "pic no.2.3"},
    //       {img_path : "http://www.zhuchenshawn.com/images/pic07.jpg",
    //       img_name : "pic no.2.4"},
    // ]
  },
  
  bindDateChange:function(e){
    this.setData({
      date: e.detail.value,
    })
  },
  formBindsubmit:function(e){
      this.setData({
        nickname:e.detail.value.nickname,
        email:e.detail.value.email,
        telphone:e.detail.value.telphone,
      })
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
      if(!checkemail(e.detail.value.email)){
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
  },
  fix:function(e){
    this.setData({
      fixed:""
    })
  },
  save:function(e){
      var sendData = '{ \n \
        "Request":"ChangeUserInfo", \n \
        "UserName":"' + this.data.username + '", \n \
        "NickName":"' + this.data.nickname + '", \n \
        "DateOfBirth":"' + this.data.date + '", \n \
        "Gender":"' + this.data.gender + '", \n \
        "Email":"' + this.data.email + '", \n \
        "PhoneNumber":"' + this.data.telphone + '", \n \
      }'

      wx.request({
        url: 'http://120.77.33.177:8000/Userinfo.test', //仅为示例，并非真实的接口地址
        data: sendData,
        method: "POST",
        success: function(res) {
          var data = res.data
          if(data['return_info'] == 'Info changed')
          {
            wx.showToast({
              title: '修改成功~',
              icon: 'success',
              duration: 2000
            })
            wx.navigateBack()
          }else{
              wx.showModal({
              title: '个人信息修改失败',
              content: '请稍后重试~',
              showCancel: false
            })
          }
        }
      })



      console.log(sendData)
  },
  onLoad:function(options){
  },
  onReady:function(){
  },
  onShow:function(){
    var that = this
    var user = wx.getStorageSync('userSession')

    if (user) {
      var username = wx.getStorageSync('username')
      var sendData = '{\n\
        "Request":"RequestForUserInfo",\n\
        "SearchBy":"Username",\n\
        "Content":"' + username + '",\n\
        }'
      console.log(sendData)
      wx.request({
        url: 'http://120.77.33.177:8000/Userinfo.test', //仅为示例，并非真实的接口地址
        data: sendData,
        method: "POST",
        success: function(res) {
          var data = res.data
          // console.log(data)
          that.setData({
            username: username,
            password: data[''],
            email: data['Email'],
            nickname: data['Nickname'],
            telphone:data['Phonenumber'],
            date:data['DataofBirth'],
            gender:data['Gender'],
            male:data['Gender'] == 'male',
            female:data['Gender'] == 'female'
          })

        }
      })
    }else{
      wx.showToast({
              title: '请登陆',
              icon: 'success',
              duration: 1000
      })
      setTimeout(function(){
        wx.redirectTo({
          url: '../login/login'
        })
      },1000)


    }

  },
  onHide:function(){
  },
  onUnload:function(){
  }
})