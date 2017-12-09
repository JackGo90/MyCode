//提交的方法名称
var method = "";
var height = 200;
var listParam = "";
var saveParam = "";
$(function(){
	//加载表格数据
	$('#grid').datagrid({
		url:name + '_listByPage.action' + listParam,
		columns:columns,
		singleSelect: true,
		pagination: true,
		toolbar: [{
			text: '新增',
			iconCls: 'icon-add',
			handler: function(){
				//设置保存按钮提交的方法为add
				method = "add";
				//关闭编辑窗口
				$('#editDlg').dialog('open');
			}
		},'-',{
			text: '导出',
			iconCls: 'icon-excel',
			handler: function(){
				var formData = $('#searchForm').serializeJSON();
				//formData['t1.type'] = 1;
				$.download(name + "_export.action" + listParam,formData);
			}
		},'-',{
			text: '导入',
			iconCls: 'icon-save',
			handler: function(){
				//打开导入窗口
				$('#importDlg').dialog('open');
			}
		}]
	});

	//点击查询按钮
	$('#btnSearch').bind('click',function(){
		//把表单数据转换成json对象
		var formData = $('#searchForm').serializeJSON();
		$('#grid').datagrid('load',formData);
	});

	//初始化编辑窗口
	$('#editDlg').dialog({
		title: '编辑',//窗口标题
		width: 300,//窗口宽度
		height: height,//窗口高度
		closed: true,//窗口是是否为关闭状态, true：表示关闭
		modal: true,//模式窗口
		buttons:[{
			text:'保存',
			iconCls: 'icon-save',
			handler:function(){
				var flg = $('#editForm').form('validate');
				if(flg == false){
					return;
				}
				//用记输入的部门信息
				var submitData= $('#editForm').serializeJSON();
				$.ajax({
					url: name + '_' + method + ".action" + saveParam,
					data: submitData,
					dataType: 'json',
					type: 'post',
					success:function(rtn){
						//{success:true, message: 操作失败}
						$.messager.alert('提示',rtn.message, 'info',function(){
							if(rtn.success){
								//关闭弹出的窗口
								$('#editDlg').dialog('close');
								//刷新表格
								$('#grid').datagrid('reload');
							}
						});
					}
				});
			}
		},{
			text:'关闭',
			iconCls:'icon-cancel',
			handler:function(){
				//关闭弹出的窗口
				$('#editDlg').dialog('close');
			}
		}]
	});

	//导入窗口初始化
	var importDlg = document.getElementById('importDlg');
	if(importDlg){
		$('#importDlg').dialog({
			title:'导入数据',
			width:360,
			height:106,
			modal:true,
			closed:true,
			buttons:[
			    {
			    	text:'导入',
			    	iconCls:'icon-save',
			    	handler:function(){
			    		$.ajax({
							url : 'supplier_doImport.action',//请求的url
							data : new FormData($('#importForm')[0]),//提交的数据
							dataType : 'json',//把响应回来的内容转成json对象
							type : 'post',//post/get
							processData:false,//告诉jquery，不要动我的数据, 字节流
							contentType:false,//告诉jQuery不要设置任何内容类型文件头,服务器以设置好的格式来读取字节流
							success : function(rtn) {//成功后会调用的方法：参数，响应回来的内容转成json对象
								$.messager.alert('提示', rtn.message, 'info',function(){
									if(rtn.success){
										$('#importDlg').dialog('close');
										$('#grid').datagrid('reload');
									}
								});
							}
						});
			    	}
			    }
			]
		});
	}
});


/**
 * 删除
 */
function del(uuid){
	$.messager.confirm("确认","确认要删除吗？",function(yes){
		if(yes){
			$.ajax({
				url: name + '_delete.action?id=' + uuid ,
				dataType: 'json',
				type: 'post',
				success:function(rtn){
					$.messager.alert("提示",rtn.message,'info',function(){
						//刷新表格数据
						$('#grid').datagrid('reload');
					});
				}
			});
		}
	});
}

/**
 * 修改
 */
function edit(uuid){
	//弹出窗口
	$('#editDlg').dialog('open');

	//清空表单内容
	$('#editForm').form('clear');

	//设置保存按钮提交的方法为update
	/**
	 * @param value
	 * @returns
	 */
	method = "update";

	//加载数据
	/*var data ={"t.address":"建材城西路中腾商务大厦","t.birthday":"1949-10-01",
			"t.dep":{"name":"管理员组","tele":"000000999","uuid":1},
			"t.email":"admin@itcast.cn","t.gender":1,"t.name":"超级管理员","t.tele":"12345678","t.username":"admin","t.uuid":1};
	var dep = data['t.dep'];
	delete data['t.dep'];
	data['t.dep.name']=dep.name;*/
	
	/*{"t.address":"建材城西路中腾商务大厦","t.birthday":"1949-10-01",
		"t.dep.name":"管理员组","t.dep.tele":"000000999","t.dep.uuid":1,
		"t.email":"admin@itcast.cn","t.gender":1,"t.name":"超级管理员","t.tele":"12345678","t.username":"admin","t.uuid":1};
	*/
	$('#editForm').form('load',name + '_get.action?id=' + uuid);
	
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