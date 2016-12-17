Page({
  data:{
    pic_path: "",
    audio_path:"",
  },

  choose_pic: function() {
    wx.navigateTo({
      url: '../logs/logs',
    })
  },
  touch_start:function(){
    wx.navigateTo({
      url: '../logs/logs',
    })
  },
  finish: function(){
    wx.navigateTo({
      url: '../logs/logs',
    })
  }
})