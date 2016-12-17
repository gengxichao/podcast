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
      var index = this.data.index
      var that = this
      var searchResult = []

      if(index == 1)
      {
        var sendData = '{\n \
                  "Request":"RequestForSpecificKind",\n\
                  "Category":"' + search + '",\n\
                  "Sortby":"Hotindex",\n\
                  }'
        console.log(sendData)
        wx.request({
          url: 'http://120.77.33.177:8000/Search', //仅为示例，并非真实的接口地址
          method: 'POST',
          data: sendData,
          success: function(res) {
            var data = res.data
            data = data["filenamelist"]
            console.log(data)
            
            data.forEach(function(e){

              var sendData = '{\n\
                  "Request":"AskForResource",\n\
                  "FileType":"Images",\n\
                  "FileName":"' + e['FileNAME'] + '",\n\
                }'
              console.log(sendData)
              wx.request({
                url: 'http://120.77.33.177:8000/askResource.test', //仅为示例，并非真实的接口地址
                method: 'POST',
                data: sendData,
                success: function(res) {
                  var data = res.data
                  searchResult.push({
                    name: e['FileNAME'],
                    filePath: data['FilePath']
                  })
                }
              })
            })

              
              console.log(searchResult)
              that.setData({
                result: searchResult
              })
              console.log(result)

          }
        })
      }else{
        var sendData = '{\n\
          "Request":"SpecificHostAudio",\n\
          "HostName":"' + search + '",\n\
          "Sortby":"Hotindex",\n\
          }'
        wx.request({
          url: 'http://120.77.33.177:8000/Search', //仅为示例，并非真实的接口地址
          method: 'POST',
          data: sendData,
          success: function(res) {
            var data = res.data
            
            console.log(res.data)


          }
        })

      }

  },
  })




  
          
              // var sendData = '{\n\
              //     "Request":"AskForResource",\n\
              //     "FileType":"Audio",\n\
              //     "FileName":"' + e['FileNAME'] + '",\n\
              //   }'
              // console.log(sendData)
              // wx.request({
              //   url: 'http://120.77.33.177:8000/askResource.test', //仅为示例，并非真实的接口地址
              //   method: 'POST',
              //   data: sendData,
              //   success: function(res) {
              //     var data = res.data
              //     console.log(data)
              //   }
              // })
              