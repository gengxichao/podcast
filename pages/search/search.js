  Page({
    data:{
         result:[],
          array: ['主播', '节目分类'],
          index: 0,
          searchField:""
        },
      listenerPickerSelected: function(e) {
      //改变index值，通过setData()方法重绘界面
      this.setData({
        index: e.detail.value
      });
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
    },

    setSearchField:function(e)
    {      
      this.setData({
        searchField: e.detail.value
      })
    },

    formBindsubmit:function(e){

      var search = this.data.searchField

      if(search == "主播")
      {
        var sendData = '{ \
                  "Request":"RequestForSpecificKind",\
                  "Category":"' + search + '",\
                  "Sortby":"Hotindex",\
                  }'
        wx.request({
          url: 'http://localhost/Search', //仅为示例，并非真实的接口地址
          data: sendData,
          success: function(res) {
            var data = res.data
            
            console.log(res.data)


          }
        })
      }else{
        var sendData = '{ \
          "Request":"RequestForUserInfo",\
          "SearchBy":"Username",\
          "Content":"' + search + '",\
          }'
        wx.request({
          url: 'http://localhost/Search', //仅为示例，并非真实的接口地址
          data: sendData,
          success: function(res) {
            var data = res.data
            
            console.log(res.data)


          }
        })










      }




      // wx.request({
      //   url: 'http://localhost/Search', //仅为示例，并非真实的接口地址
      //   data: sendData,
      //   success: function(res) {
      //     var data = res.data
          
      //     console.log(res.data)


      //   }
      // })





      // this.setData({
      //   "result[0].img_path":"http://www.zhuchenshawn.com/images/pic07.jpg",
      //   "result[0].img_name":"result no.1",
      //   "result[1].img_path":"http://www.zhuchenshawn.com/images/pic07.jpg",
      //   "result[1].img_name":"result no.2",
      //   "result[2].img_path":"",
      //   "result[2].img_name":"",
      //   "result[3].img_path":"",
      //   "result[3].img_name":"",
      //   "result[4].img_path":"",
      //   "result[4].img_name":"",
      //   "result[5].img_path":"",
      //   "result[5].img_name":"",
      // })
  },
  })