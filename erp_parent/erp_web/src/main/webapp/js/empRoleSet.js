$(function() {
	$('#grid').datagrid({
		url : 'emp_list.action',
		columns : [ [
		    {field:'uuid',title:'编号',width:100},
			{field:'name',title:'名称',width:200}
		] ],
		singleSelect : true,
		onSelect: function( rowIndex, rowData){
			$('#tree').tree({
				animate:true,
				checkbox:true,
				url:'http://localhost:8080/erp/emp_readEmpRoles.action?id=' + rowData.uuid
			});
		} 
	});
	
	
	$('#btnSave').bind('click',function(){
		//获取选中的用户的行数据
		var emp = $('#grid').datagrid('getSelected');
		
		if(!emp){
			//没有选择用户
			$.messager.alert('提示','请选择用户', 'info');
			return;
		}
		//获取所有选中的节点
		var nodes = $('#tree').tree('getChecked');
		var ids = [];
		$.each(nodes,function(i,node){
			//选中的角色编号
			ids.push(node.id);
		});
		// 提交的数据
		var submitData = {};
		submitData.id=emp.uuid;
		submitData.ids = ids.toString();
		// 提交请求
		$.ajax({
			url : 'emp_updateEmpRoles.action',//请求的url
			data : submitData,//提交的数据
			dataType : 'json',//把响应回来的内容转成json对象
			type : 'post',//post/get
			success : function(rtn) {//成功后会调用的方法：参数，响应回来的内容转成json对象
				$.messager.alert('提示', rtn.message, 'info');
			}
		});
		
	});
});