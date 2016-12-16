  Page({
    data:{
         result:[
            ],
          array: ['name', 'author', 'hot'],
          index: 0,
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
    formBindsubmit:function(e){
      this.setData({
        "result[0].img_path":"http://www.zhuchenshawn.com/images/pic07.jpg",
        "result[0].img_name":"result no.1",
        "result[1].img_path":"http://www.zhuchenshawn.com/images/pic07.jpg",
        "result[1].img_name":"result no.2",
        "result[2].img_path":"",
        "result[2].img_name":"",
        "result[3].img_path":"",
        "result[3].img_name":"",
        "result[4].img_path":"",
        "result[4].img_name":"",
        "result[5].img_path":"",
        "result[5].img_name":"",
      })
  },
  })