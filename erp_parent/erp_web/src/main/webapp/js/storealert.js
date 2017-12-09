$(function() {
	$('#grid').datagrid({
		url : 'storedetail_storealertList.action',
		columns : [[
  		    {field:'uuid',title:'商品编号',width:100},
  		    {field:'name',title:'商品名称',width:100},
  		    {field:'storenum',title:'库存数量',width:100},
  		    {field:'outnum',title:'待发货数量',width:100}
		]],
		singleSelect : true,
		toolbar:[
		    {
		    	text:'发送预警邮件',
		    	iconCls:'icon-alert',
		    	handler:function(){
		    		$.ajax({
						url : 'storedetail_sendStorealertMail.action',//请求的url
						dataType : 'json',//把响应回来的内容转成json对象
						type : 'post',//post/get
						success : function(rtn) {//成功后会调用的方法：参数，响应回来的内容转成json对象
							$.messager.alert('提示', rtn.message, 'info');
						}
					});
		    	}
		    }
		]
	});
});