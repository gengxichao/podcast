//index.js
//Page data
Page({
  data:{
    nickname:"XiaoWu",
    rctud:[
        {img_path:"http://www.lumiaxu.com/static/images/pd.png",
         img_name:"pic no.1"},
        {img_path:"http://www.lumiaxu.com/static/images/pd.png",
        img_name:"pic no.2"},
        {img_path:"http://www.lumiaxu.com/static/images/pd.png",
        img_name:"pic no.3"},
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
    wx.downloadFile({
      url: "http://www.zhuchenshawn.com",//adjust after we set up the server.
      // header: {}, // 设置请求的 header
      success: function(res){
        that.setData({
          "rctud[0].img_path" : "http://www.server110.com/template/images/logo.png",
          "rctud[0].img_name" : "pic no.1.1",
          "rctud[1].img_path" : "http://www.server110.com/template/images/logo.png",
          "rctud[1].img_name" : "pic no.1.2",
          "rctud[2].img_path" : "http://www.server110.com/template/images/logo.png",
          "rctud[2].img_name" : "pic no.1.3",
          "rctud[3].img_path" : "http://www.server110.com/template/images/logo.png",
          "rctud[3].img_name" : "pic no.1.4",
          "rctud[4].img_path" : "http://files.jb51.net/file_images/article/201307/201307130957593.jpg",
          "rctud[4].img_name" : "pic no.1.5",
          "nyud[0].img_path" : "http://www.server110.com/template/images/logo.png",
          "nyud[0].img_name" : "pic no.2.1",
          "nyud[1].img_path" : "http://files.jb51.net/file_images/article/201307/201307130957593.jpg",
          "nyud[1].img_name" : "pic no.2.2",
          "nyud[2].img_path" : "http://www.zhuchenshawn.com/images/pic07.jpg",
          "nyud[2].img_name" : "pic no.2.3",
          "nyud[3].img_path" : "http://www.zhuchenshawn.com/images/pic07.jpg",
          "nyud[3].img_name" : "pic no.2.4",
          "nyud[4].img_path" : "http://www.zhuchenshawn.com/images/pic07.jpg",
          "nyud[4].img_name" : "pic no.2.5",
          "hot[0].img_path" : "http://files.jb51.net/file_images/article/201307/201307130957593.jpg",
          "hot[0].img_name" : "pic no.3.1",
          "hot[1].img_path" : "http://www.zhuchenshawn.com/images/pic07.jpg",
          "hot[1].img_name" : "pic no.3.2",
          "hot[2].img_path" : "http://www.server110.com/template/images/logo.png",
          "hot[2].img_name" : "pic no.3.3",         
          "hot[3].img_path" : "http://www.server110.com/template/images/logo.png",
          "hot[3].img_name" : "pic no.3.4",          
          "hot[4].img_path" : "http://www.server110.com/template/images/logo.png",
          "hot[4].img_name" : "pic no.3.5",          
        })
        //console.log(res)
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
  },
})

//using wx.downloadfile to download image and audio


//using Ajax to trans Json information
