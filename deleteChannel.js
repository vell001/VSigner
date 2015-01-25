/*
 * deleteChannel
 * 删除频道
 */
function onRequest(request, response, modules) {
    var channelObjectId = request.body.channelObjectId;
    var db = modules.oData;
    var rel = modules.oRelation;
    
    rel.query({
        "table":"ChannelSubscriber",
        "where":{"channel":channelObjectId}
    },function(err,data) {
        var ChannelSubscribers = JSON.parse(data).results;
        for(var i=0; i<ChannelSubscribers.length; i++) {
            var ChannelSubscriber = ChannelSubscribers[i];
            db.remove({
                "table":"ChannelSubscriber",             //表名
                "objectId":ChannelSubscriber.objectId        //记录的objectId
            },function(err,data){         //回调函数
            });
        }
    });
    
    rel.query({
        "table":"ChannelSigner",
        "where":{"channel":channelObjectId}
    },function(err,data) {
        var ChannelSigners = JSON.parse(data).results;
        for(var i=0; i<ChannelSigners.length; i++) {
            var ChannelSigner = ChannelSigners[i];
            db.remove({
                "table":"ChannelSigner",             //表名
                "objectId":ChannelSigner.objectId        //记录的objectId
            },function(err,data){         //回调函数
            });
        }
    });
    
    db.remove({
        "table":"Channel",             //表名
        "objectId":channelObjectId        //记录的objectId
    },function(err,data){         //回调函数
    });
	
	response.send("ok");
}                         