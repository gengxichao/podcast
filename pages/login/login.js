//input.js
Page({
  data:{
    tip:'',
    userName:'',
    password:''
  },
  formBindsubmit:function(e){
    if(e.detail.value.userName.length==0||e.detail.value.password.length==0){
      this.setData({
        tip:'Username and Password cannot be empty!',
        userName:'',
        password:''
      })
    }
    else{
      wx.navigateBack({
        delta: 1, // 回退前 delta(默认为1) 页面
        success: function(res){
          // success
        },
        fail: function() {
          // fail
        },
        complete: function() {
          // complete
        }
      })
    }
  },
})