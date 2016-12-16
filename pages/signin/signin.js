Page({
  data:{
    tip:"",
    username: "",
    password:"",
    gender:"",
    email:"",
    telphone:""
  },
  formBindsubmit:function(e){
    if(e.detail.value.username.length==0||e.detail.value.password.length==0||e.detail.value.gender.length==0||e.detail.value.email.length==0||e.detail.value.telphone.length==0)
    {
      this.setData({
        tip:'Username and Password cannot be empty!',
        username: "",
        password:"",
        male:"",
        female:"",
        email:"",
        telphone:""
      })
    }
    else{      
      wx.navigateBack({
        delta: 2, // 回退前 delta(默认为1) 页面
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
      this.setData({
        tip:'',
        username:e.detail.value.userName,
        password:e.detail.value.password,
        email:e.detail.value.email,
        telphone:e.detail.value.telphone,})
      if(e.detail.value.gender == 'maie'){
        this.setdata({
          male:'true',
          female:'',
        })
      }
      else{
        this.setdata({
          male:'',
          female:'true',})
      }
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