//index.js
//Page data
Page({
  data:{
    nickname:"游客",
    state:"登录",
    nyud:[
        {img_path:"http://www.lumiaxu.com/static/images/pd.png",
        id:"001"},
        {img_path:"http://www.lumiaxu.com/static/images/pd.png",
        id:"002"},
        {img_path:"http://www.lumiaxu.com/static/images/pd.png",
        id:"003"},
    ],
    nyud_num:"10",
    hot:[
        {img_path:"http://www.lumiaxu.com/static/images/pd.png"},
        {img_path:"http://www.lumiaxu.com/static/images/pd.png"},
        {img_path:"http://www.lumiaxu.com/static/images/pd.png"},
    ],
    hot_num:"15",
  },

  onLoad:function(options){
    var that = this;
    var sendData = '{\n\
      "Request":"Openpagehot",\n\
      "Sortby":"uploaddate",\n\
      }'
    wx.request({
      url: 'http://120.77.33.177:8000/HOT',
      data: sendData,
      method: 'POST', // OPTIONS, GET, HEAD, POST, PUT, DELETE, TRACE, CONNECT
      success: function(res){
        var data = res.data
        data = data['filelist']
        data.forEach(function(e){
            e['imagepath'] = 'http://120.77.33.177:8000' + e['imagepath']
        })
        console.log(data)
        that.setData({
          nyud: data
        })
      }
    })
    var sendData = '{\n\
        "Request":"OpenpageDate",\n\
        "Sortby":"uploaddate",\n\
      }'
    wx.request({
      url: 'http://120.77.33.177:8000/HOT',
      data: sendData,
      method: 'POST', // OPTIONS, GET, HEAD, POST, PUT, DELETE, TRACE, CONNECT
      success: function(res){
        var data = res.data
        data = data['filelist']
        data.forEach(function(e){
            e['imagepath'] = 'http://120.77.33.177:8000' + e['imagepath']
          }
        )
        console.log(data)
        that.setData({
          hot: data
        })
      }
    })












    // 页面初始化 options为页面跳转所带来的参数
    wx.downloadFile({
      url: "http://www.zhuchenshawn.com",//adjust after we set up the server.
      // header: {}, // 设置请求的 header
      success: function(res){
        that.setData({
          "rctud[0].img_path" : "http://www.zhuchenshawn.com/images/pic07.jpg",
          "rctud[0].img_name" : "pic no.1.1",
          "rctud[1].img_path" : "http://www.zhuchenshawn.com/images/pic07.jpg",
          "rctud[1].img_name" : "pic no.1.2",
          "rctud[2].img_path" : "http://www.zhuchenshawn.com/images/pic07.jpg",
          "rctud[2].img_name" : "pic no.1.3",
          "rctud[3].img_path" : "http://www.zhuchenshawn.com/images/pic07.jpg",
          "rctud[3].img_name" : "pic no.1.4",
          "rctud[4].img_path" : "http://www.zhuchenshawn.com/images/pic07.jpg",
          "rctud[4].img_name" : "pic no.1.5",
          "nyud[0].img_path" : "http://www.zhuchenshawn.com/images/pic07.jpg",
          "nyud[0].img_name" : "pic no.2.1",
          "nyud[1].img_path" : "http://www.zhuchenshawn.com/images/pic07.jpg",
          "nyud[1].img_name" : "pic no.2.2",
          "nyud[2].img_path" : "http://www.zhuchenshawn.com/images/pic07.jpg",
          "nyud[2].img_name" : "pic no.2.3",
          "nyud[3].img_path" : "http://www.zhuchenshawn.com/images/pic07.jpg",
          "nyud[3].img_name" : "pic no.2.4",
          "nyud[4].img_path" : "http://www.zhuchenshawn.com/images/pic07.jpg",
          "nyud[4].img_name" : "pic no.2.5",
          "hot[0].img_path" : "http://www.zhuchenshawn.com/images/pic07.jpg",
          "hot[0].img_name" : "pic no.3.1",
          "hot[1].img_path" : "http://www.zhuchenshawn.com/images/pic07.jpg",
          "hot[1].img_name" : "pic no.3.2",
          "hot[2].img_path" : "http://www.zhuchenshawn.com/images/pic07.jpg",
          "hot[2].img_name" : "pic no.3.3",         
          "hot[3].img_path" : "http://www.zhuchenshawn.com/images/pic07.jpg",
          "hot[3].img_name" : "pic no.3.4",          
          "hot[4].img_path" : "http://www.zhuchenshawn.com/images/pic07.jpg",
          "hot[4].img_name" : "pic no.3.5",          
        })
        //console.log(res)
      },
      fail: function() {
        console.log()
      },
      complete: function() {
        console.log("complete")
      }
    })
  },

  logout: function(e){
    var that = this

    wx.clearStorageSync() 
    that.setData({
        "nickname": "游客",
        "state":"登录"
    })
  },
  play: function(e){
    console.log(e);
    wx.navigateTo({
      url: '../play/play?id=' + e.target.id,
    })
  },
  onReady:function(){
    // 页面渲染完成
    //String3
  },
  onShow:function(){
   var that = this
    var value = wx.getStorageSync('userSession')

    if (value) {
      console.log(value)
      
      var nickname = wx.getStorageSync('nickname')

      that.setData(
        {
          "nickname": nickname,
          "state":"注销"
        }
      )
    }else{
      console.log("not exist")
    }
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
