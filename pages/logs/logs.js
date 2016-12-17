//logs.js
Page({
  data:{
  },
  back:function(){
    wx.navigateBack({
      delta: 2, // 回退前 delta(默认为1) 页面
    })
  }
})