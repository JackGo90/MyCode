var oper = Request.oper;
var type = Request.type * 1;
//出入库的文本
var textType = '入';
$(function() {
	var url = '';
	
	//订单列表属性设置
	
	//数据类型：js -> function方法
	var gridCfg = {
			columns : getColumns(),
			singleSelect : true,
			pagination:true,
			onDblClickRow: function(rowIndex, rowData){
				/*
				 *  在用户双击一行的时候触发，参数包括：
					rowIndex：点击的行的索引值，该索引值从0开始。
					rowData：对应于点击行的记录, 包含订单内容，订单明细
				 */
				//打开详情窗口
				$('#ordersDlg').dialog('open');
				//订单赋值
				$('#uuid').html(rowData.uuid);
				$('#supplierName').html(rowData.supplierName);
				$('#state').html(formatState(rowData.state));
				$('#createrName').html(rowData.createrName);
				$('#checkerName').html(rowData.checkerName);
				$('#starterName').html(rowData.starterName);
				$('#enderName').html(rowData.enderName);
				$('#createtime').html(formatDate(rowData.createtime));
				$('#checktime').html(formatDate(rowData.checktime));
				$('#starttime').html(formatDate(rowData.starttime));
				$('#endtime').html(formatDate(rowData.endtime));
				
				//运单号
				$('#waybillsn').html(rowData.waybillsn);
				
				$('#itemgrid').datagrid('loadData',rowData.orderdetails);
			}
		};
	
	//详情窗口 属性设置
	var ordersDlgCfg = {
			title:'订单详情',
			width:700,
			height:320,
			closed:true,
			modal:true
		};
	var toolbar = [];//详情窗口的工具栏
	
	
	//明细表格 属性设置
	var itemgridCfg = {
		columns : [ [
		 			{field:'uuid',title:'编号',width:80},
		 			{field:'goodsuuid',title:'商品编号',width:80},
		 			{field:'goodsname',title:'商品名称',width:100},
		 			{field:'price',title:'价格',width:100},
		 			{field:'num',title:'数量',width:80},
		 			{field:'money',title:'金额',width:100},
		 			{field:'state',title:'状态',width:100,formatter:formatDetailState}
		 		] ],
		 		singleSelect : true
	};
	
	//审核业务
	if(oper == 'doCheck'){
		url = 'orders_listByPage.action?t1.type=1&t1.state=0';
		document.title='审核';
		toolbar.push({
			text:'审核',
			iconCls:'icon-search',
			handler:doCheck
		});
	}
	
	//确认业务
	if(oper == 'doStart'){
		url = 'orders_listByPage.action?t1.type=1&t1.state=1';
		document.title='确认';
		toolbar.push({
			text:'确认',
			iconCls:'icon-search',
			handler:doStart
		});
	}
	//导出按钮
	toolbar.push({
		text:'导出',
		iconCls:'icon-excel',
		handler:function(){
			$.download("orders_exportById.action",{id:$('#uuid').html()});
		}
	});
	
	//入库业务
	if(oper == 'doInStore'){
		url = 'orders_listByPage.action?t1.type=1&t1.state=2';
		document.title='入库';
		//明细的双击行事件
		itemgridCfg.onDblClickRow=function(rowIndex, rowData){
			//打开入库窗口
			$('#itemDlg').dialog('open');
			//加载数据
			$('#id').val(rowData.uuid);
			
			$('#goodsuuid').html(rowData.goodsuuid);
			$('#goodsname').html(rowData.goodsname);
			$('#num').html(rowData.num);
		}
	}
	
	//出库业务
	if(oper == 'doOutStore'){
		url = 'orders_listByPage.action?t1.type=2&t1.state=0';
		document.title='出库';
		//明细的双击行事件
		itemgridCfg.onDblClickRow=function(rowIndex, rowData){
			//打开入库窗口
			$('#itemDlg').dialog('open');
			//加载数据
			$('#id').val(rowData.uuid);
			
			$('#goodsuuid').html(rowData.goodsuuid);
			$('#goodsname').html(rowData.goodsname);
			$('#num').html(rowData.num);
		}
	}

	//订单列表的工具栏
	var gridToolbar = [];
	
	//我的订单
	if(oper == 'myorders'){
		url = 'orders_myListByPage.action?t1.type=' + type;
		document.title='我的采购订单';
		var btnText = "采购申请";
		if(type == 2){
			document.title='我的销售订单';
			btnText = "销售订单录入";
			$('#addOrdersSupplier').html('客户');
		}
		gridToolbar.push({
			text:btnText,
			iconCls:'icon-add',
			handler:function(){
				//弹出采购申请窗口
				$('#addOrdersDlg').dialog('open');
			}
		});
	}
	
	//订单查询
	if(oper == 'orders'){
		url = 'orders_listByPage.action?t1.type=' + type;
		document.title='采购订单列表';
		if(type == 2){
			document.title='销售订单列表';
		}
	}
	//物流详情
	toolbar.push({
		text:'物流详情',
		iconCls:'icon-search',
		handler:function(){
			if($('#waybillsn').html() == ''){
				$.messager.alert('提示', "没有运单信息", 'info');
				return;
			}
			//打开物流详情窗口
			$('#waybilldetailDlg').dialog('open');
			//加载物流详情信息
			$('#waybilldetailgrid').datagrid({
				url : 'orders_waybilldetailList.action?waybillsn=' + $('#waybillsn').html(),
				columns : [ [
					{field:'exedate',title:'执行日期',width:80},
					{field:'exetime',title:'执行时间',width:80},
					{field:'info',title:'执行信息',width:200}
				] ],
				singleSelect : true
			});
		}
	});
	//详情窗口的工具栏 有按钮，才加到详情窗口中
	//if(toolbar.length > 0){
		ordersDlgCfg.toolbar=toolbar;
	//}
	
	//订单列表的工具栏 有按钮，加到订单列表的grid中
	if(gridToolbar.length > 0){
		gridCfg.toolbar=gridToolbar;
	}
	
	//设置订单列表请求的url
	gridCfg.url = url;
	$('#grid').datagrid(gridCfg);
	
	//详情窗口
	$('#ordersDlg').dialog(ordersDlgCfg);
	
	//商品明细表格
	$('#itemgrid').datagrid(itemgridCfg);
	
	if(type == 2){
		textType = "出";
	}
	
	//入库窗口
	$('#itemDlg').dialog({
		title: textType + '库',
		width:300,
		height:200,
		modal:true,
		closed:true,
		buttons:[
		    {
		    	text:textType + '库',
		    	iconCls:'icon-save',
		    	handler:doInOutStore
		    }
		]
	});
	
	//采购申请窗口
	$('#addOrdersDlg').show();
	$('#addOrdersDlg').dialog({
		title:'采购申请',
		width:700,
		height:400,
		modal:true,
		closed:true
	});
	
	//运单详情窗口
	$('#waybilldetailDlg').dialog({
		title:'物流信息',
		width:500,
		height:300,
		modal:true,
		closed:true
	});
});

/**
 * 日期格式化器
 * format: 2017-11-20
 */
function formatDate(value){
	if(value){//不为null，不为'',0
		return new Date(value).Format('yyyy-MM-dd');
	}
	return '';
}

/**
 * 订单状态格式化器
 * @param value
 * @returns {String}
 */
function formatState(value){
	//采购: 0:未审核 1:已审核, 2:已确认, 3:已入库；
	if(type == 1){
		switch(value * 1){
		case 0: return '未审核';
		case 1: return '已审核';
		case 2: return '已确认';
		case 3: return '已入库';
		default:return '';
		}
	}
	//销售：0:未出库 1:已出库
	if(type == 2){
		switch(value * 1){
		case 0: return '未出库';
		case 1: return '已出库';
		default:return '';
		}
	}
}

/**
 * 明细状态格式化器
 * @returns {String}
 */
function formatDetailState(value){
	//采购：0=未入库，1=已入库  
	if(type == 1){
		switch(value * 1){
			case 0: return '未入库';
			case 1: return '已入库';
			default:return '';
		}
	}
	//销售：0=未出库，1=已出库
	if(type == 2){
		switch(value * 1){
			case 0: return '未出库';
			case 1: return '已出库';
			default:return '';
		}
	}
	
}

/**
 * 审核
 * @returns
 */
function doCheck(){
	$.messager.confirm('确认','确认要审核吗?',function(yes){
		if(yes){
			$.ajax({
				url : 'orders_doCheck.action',//请求的url
				data : {id:$('#uuid').html()},//提交的数据
				dataType : 'json',//把响应回来的内容转成json对象
				type : 'post',//post/get
				success : function(rtn) {//成功后会调用的方法：参数，响应回来的内容转成json对象
					$.messager.alert('提示', rtn.message, 'info',function(){
						if(rtn.success){
							//关闭详情窗口
							$('#ordersDlg').dialog('close');
							//刷新订单列表
							$('#grid').datagrid('reload');
						}
					});
				}
			});
		}
	});
}

/**
 * 确认
 * @returns
 */
function doStart(){
	$.messager.confirm('确认','确定要确认吗?',function(yes){
		if(yes){
			$.ajax({
				url : 'orders_doStart.action',//请求的url
				data : {id:$('#uuid').html()},//提交的数据
				dataType : 'json',//把响应回来的内容转成json对象
				type : 'post',//post/get
				success : function(rtn) {//成功后会调用的方法：参数，响应回来的内容转成json对象
					$.messager.alert('提示', rtn.message, 'info',function(){
						if(rtn.success){
							//关闭详情窗口
							$('#ordersDlg').dialog('close');
							//刷新订单列表
							$('#grid').datagrid('reload');
						}
					});
				}
			});
		}
	});
}

/**
 * 出入库
 */
function doInOutStore(){
	$.messager.confirm('确认','确定要' + textType +'库吗?',function(yes){
		if(yes){
			var submitData = $('#itemForm').serializeJSON();
			var url = 'orderdetail_doInStore.action';
			if(type == 2){
				//url = 'orderdetail_doOutStoreJava.action';//java实现出库
				url = 'orderdetail_doOutStore.action';//调用存储过程实现出库
			}
			$.ajax({
				url : url,//请求的url
				data : submitData,//提交的数据
				dataType : 'json',//把响应回来的内容转成json对象
				type : 'post',//post/get
				success : function(rtn) {//成功后会调用的方法：参数，响应回来的内容转成json对象
					$.messager.alert('提示', rtn.message, 'info',function(){
						if(rtn.success){
							//关闭入库窗口
							$('#itemDlg').dialog('close');
							
							//获取当前正在入库明细记录
							var row = $('#itemgrid').datagrid('getSelected');
							//改变明细的状态
							row.state = '1';
							
							//刷新明细列表
							var data = $('#itemgrid').datagrid('getData');
							$('#itemgrid').datagrid('loadData',data);
							
							//判断所有明细是否都已经入库了
							var flg = true;//是否关闭订单详情
							$.each(data.rows, function(i,r){
								if(r.state == '0'){
									flg = false;
									return false;//break;跳出循环
								}
							});
							
							if(flg == true){
								//所有的明细都完成入库了
								$('#ordersDlg').dialog('close');
								//刷新订单列表
								$('#grid').datagrid('reload');
							}
						}
					});
				}
			});
		}
	});
}

/**
 * 获取订单列表中的列的定义
 */
function getColumns(){
	if(type == 1){
		//采购
		return [ [
		  		{field:'uuid',title:'编号',width:60},
				{field:'createtime',title:'生成日期',width:80,formatter:formatDate},
				{field:'checktime',title:'审核日期',width:80,formatter:formatDate},
				{field:'starttime',title:'确认日期',width:80,formatter:formatDate},
				{field:'endtime',title:'入库日期',width:80,formatter:formatDate},
				{field:'createrName',title:'下单员',width:80},
				{field:'checkerName',title:'审核员',width:80},
				{field:'starterName',title:'采购员',width:80},
				{field:'enderName',title:'库管员',width:80},
				{field:'supplierName',title:'供应商',width:80},
				{field:'totalmoney',title:'合计金额',width:80},
				{field:'state',title:'状态',width:60,formatter:formatState},
				{field:'waybillsn',title:'运单号',width:80}
			] ];
	}
	if(type == 2){
		//销售
		return [ [
		  		{field:'uuid',title:'编号',width:100},
				{field:'createtime',title:'生成日期',width:100,formatter:formatDate},
				{field:'endtime',title:'出库日期',width:100,formatter:formatDate},
				{field:'createrName',title:'下单员',width:100},
				{field:'enderName',title:'库管员',width:100},
				{field:'supplierName',title:'客户',width:100},
				{field:'totalmoney',title:'合计金额',width:100},
				{field:'state',title:'状态',width:100,formatter:formatState},
				{field:'waybillsn',title:'运单号',width:100}
			] ];
	}
	
}
