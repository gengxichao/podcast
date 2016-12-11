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
      this.setData({
        tip:'',
        userName:e.detail.value.userName,
        password:e.detail.value.password
      })
    }
  },
  formReset:function(){
    this.setData({
      tip:'',
      userName:'',
      password:''
    })
  }
})