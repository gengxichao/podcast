Page({
  data:{
    title: "Hotel California",
    pic:"https://imgsa.baidu.com/baike/c0%3Dbaike80%2C5%2C5%2C80%2C26/sign=0a856a98b3b7d0a26fc40ccfaa861d6c/50da81cb39dbb6fdf5d7e30c0824ab18972b370f.jpg",
    author:"Eagles",
    audio_path:"",
    detail:"Welcome to the Hotel California Such a lovely place Such a lovely place Such a lovely face Plenty of room at the Hotel California Any time of year Any time of year You can find it here"
  },
  onLoad:function(options){
    var that = this
    console.log(options)

    that.setData({
      title: options.id
    })

    var sendData = '{\n\
                  "Request":"AskForResource",\n\
                  "FileType":"Images",\n\
                  "FileName":"' + options.id + '",\n\
                }'
    wx.request({
        url: 'http://120.77.33.177:8000/askResource.test', //仅为示例，并非真实的接口地址
        method: 'POST',
        data: sendData,
        success: function(res) {
        var recevData = res.data
        that.setData({
         pic: "http://120.77.33.177:8000" + recevData['FilePath']
        })
    }
    })           
               
    var sendData = '{\n\
                  "Request":"AskForResource",\n\
                  "FileType":"Audio",\n\
                  "FileName":"' + options.id + '",\n\
                }'
    wx.request({
        url: 'http://120.77.33.177:8000/askResource.test', //仅为示例，并非真实的接口地址
        method: 'POST',
        data: sendData,
        success: function(res) {
        var recevData = res.data
        that.setData({
         audio_path: "http://120.77.33.177:8000" + recevData['FilePath']
        })
    }
    }) 





    // this.audioCtx = wx.createAudioContext('myAudio');    
  },
  /*
   audioPlay: function () {
    this.audioCtx.play()
  },
  audioPause: function () {
    this.audioCtx.pause()
  },
  */
  onReady:function(){
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