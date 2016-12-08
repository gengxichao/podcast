//index.js
//Page data
Page({
  data:{
    nickname: "用户昵称",

    rctud:[
        {img_path:"http://www.lumiaxu.com/static/images/pd.png"},
        {img_path:"http://www.lumiaxu.com/static/images/pd.png"},
        {img_path:"http://www.lumiaxu.com/static/images/pd.png"},
    ],
    nyud:[
        {img_path:"http://www.lumiaxu.com/static/images/pd.png"},
        {img_path:"http://www.lumiaxu.com/static/images/pd.png"},
        {img_path:"http://www.lumiaxu.com/static/images/pd.png"},
    ],
    hot:[
        {img_path:"http://www.lumiaxu.com/static/images/pd.png"},
        {img_path:"http://www.lumiaxu.com/static/images/pd.png"},
        {img_path:"http://www.lumiaxu.com/static/images/pd.png"},
    ],
  },

  onLoad:function(options){
    var that = this;
    // 页面初始化 options为页面跳转所带来的参数
    wx.request({
      url: "http://127.0.0.1:5000/get_user_list",
      method: "GET",

      success: function(res){

        var receiveData = res.data

        console.log(res.data)
        that.setData({
          "nickname": receiveData.nickname,
          "rctud": receiveData.rctud,
          "nyud": receiveData.nyud,
          "hot": receiveData.hot
        })
      },
      fail: function() {
        console.log(err)
      },
      complete: function() {
        console.log("complete")
      }
    })
  },
  onReady:function(){
    // 页面渲染完成
    //String3
  },
  onShow:function(){
    // 页面显示
    //String4
  },
  onHide:function(){
    // 页面隐藏
    //String5
  },
  onUnload:function(){
    // 页面关闭
    //String6
  }
})

//using wx.downloadfile to download image and audio


//using Ajax to trans Json information
