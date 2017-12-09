$(function() {
	$('#grid').datagrid({
		url : 'emp_listByPage.action',
		columns : [ [
			{field:'uuid',title:'编号',width:100},
			{field:'username',title:'登陆名',width:100},
			{field:'name',title:'真实姓名',width:100},
			{field:'gender',title:'性别',width:100,formatter:function(value){
				value = value * 1;//转成数值类型
				if(value == 0){
					return '女';
				}
				if(value == 1){
					return '男';
				}
			}},
			{field:'email',title:'邮件地址',width:100},
			{field:'tele',title:'联系电话',width:100},
			{field:'address',title:'联系地址',width:100},
			{field:'birthday',title:'出生年月日',width:100,formatter:formatDate},
			{field:'dep',title:'部门编号',width:100,formatter:function(value){
				return value.name;
			}},
			
			{field:'-',title:'操作',width:200,formatter: function(value,row,index){
				var oper = "<a href=\"javascript:void(0)\" onclick=\"updatePwd_reset(" + row.uuid + ')">重置密码</a>';
				return oper;
			}}
		] ],
		singleSelect : true
	});
	
	//重置密码窗口
	$('#editDlg').dialog({
		title:'重置密码',
		modal:true,
		width:260,
		height:120,
		closed:true,
		buttons:[
		    {
		        text:'保存',
		        iconCls:'icon-save',
		        handler:function(){
		        	var submitData = $('#resetPwdForm').serializeJSON();
		        	$.ajax({
						url : 'emp_updatePwd_reset.action',//请求的url
						data : submitData,//提交的数据
						dataType : 'json',//把响应回来的内容转成json对象
						type : 'post',//post/get
						success : function(rtn) {//成功后会调用的方法：参数，响应回来的内容转成json对象
							$.messager.alert('提示', rtn.message, 'info',function(){
								if(rtn.success){
									$('#editDlg').dialog('close');
								}
							});
						}
					});
		        }
		    }
		]
	});
});

/**
 * 重置密码
 * @param uuid
 */
function updatePwd_reset(uuid){
	$('#editDlg').dialog('open');
	//设置员工的编号
	$('#id').val(uuid);
}

/**
 * 日期格式化器
 * format: 2017-11-20
 */
function formatDate(value){
	if(value){//不为null，不为'',0
		return new Date(value).Format('yyyy-MM-dd');
	}
}