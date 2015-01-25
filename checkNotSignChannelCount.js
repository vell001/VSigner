/*
 * checkNotSignChannelCount
 * 检查没有签到的频道数
 */
function onRequest(request, response, modules) {
	var userObjectId = request.body.userObjectId;
    var db = modules.oData;
    var rel = modules.oRelation;
	
	var notSign = new Array();
	var notSignIndex = 0;
	
	var normalSign = new Array();
	var normalSignIndex = 0;
	
	var historySign = new Array();
	var historySignIndex = 0;
	
	rel.query({
		"table":"ChannelSubscriber",
		"include":"channel",
		"where":{"channel":{"$inQuery":{"where":{"isActive":true},"className":"Channel"}}, "subscriber":userObjectId}
	},function(err,data1){ // 查询所有活动着的订阅频道
		var jsonData1 = JSON.parse(data1);
		
		var ChannelSubscribers = jsonData1.results;
		
		rel.query({
    		"table":"ChannelSigner",
    		"include":"channel",
    		"where":{"signer":userObjectId}
    	},function(err,data2){ // 查询所有签到
    	    var jsonData2 = JSON.parse(data2);
    	    
    	    var ChannelSigners = jsonData2.results;
    	    
			for(var i=0; i<ChannelSubscribers.length; i++) {
				var ChannelSubscriber = ChannelSubscribers[i];
				var isSigned = false;
				
				for(var j=0; j<ChannelSigners.length; j++) {
				    var ChannelSigner = ChannelSigners[j];
				    
				    if(ChannelSigner.channel.objectId == ChannelSubscriber.channel.objectId) { // 匹配上签到表
				        var startSignDate = new Date(ChannelSigner.channel.startSignDate.iso);
				        var signDate = new Date(ChannelSigner.signDate.iso);
				        
				        if(startSignDate > signDate) { // 历史签到
				            historySign[historySignIndex++] = ChannelSubscriber.channel.objectId;
				        } else { // 正常已签到
				            normalSign[normalSignIndex++] = ChannelSubscriber.channel.objectId;
				            isSigned = true;
				        }
				    }
				}
				
				if(!isSigned) {
				    notSign[notSignIndex++] = ChannelSubscriber.channel.objectId;
				}
			}
    	    
    	    response.send(notSign.length);
    	});
		
	});
}