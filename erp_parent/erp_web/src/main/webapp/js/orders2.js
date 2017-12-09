// 业务类型
var oper = Request.oper;
// 订单类型
var type = Request.type * 1;

$(function() {
	// 初始化订单列表
	initOrdersDatagrid();
	// 初始化订单详情窗口
	initOrdersDlg();
	// 初始化出入库窗口
	initItemDlg();
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
	// 采购: 0:未审核 1:已审核, 2:已确认, 3:已入库；
	if(type == 1){
		switch(value * 1){
		case 0: return '未审核';
		case 1: return '已审核';
		case 2: return '已确认';
		case 3: return '已入库';
		default:return '';
		}
	}
	// 销售：0:未出库 1:已出库
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
	// 采购：0=未入库，1=已入库  
	if(type == 1){
		switch(value * 1){
			case 0: return '未入库';
			case 1: return '已入库';
			default:return '';
		}
	}
	// 销售：0=未出库，1=已出库
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
		$.ajax({
			url : 'orders_doCheck.action',//请求的url
			data : {id:$('#uuid').html()},//提交的数据
			dataType : 'json',//把响应回来的内容转成json对象
			type : 'post',//post/get
			success : function(rtn) {//成功后会调用的方法：参数，响应回来的内容转成json对象
				$.messager.alert('提示', rtn.message, 'info',function(){
					if(rtn.success){
						// 关闭详情窗口
						$('#ordersDlg').dialog('close');
						// 刷新订单列表
						$('#grid').datagrid('reload');
					}
				});
			}
		});
	});
}

/**
 * 确认
 * @returns
 */
function doStart(){
	$.messager.confirm('确认','确定要确认吗?',function(yes){
		$.ajax({
			url : 'orders_doStart.action',// 请求的url
			data : {id:$('#uuid').html()},// 提交的数据
			dataType : 'json',// 把响应回来的内容转成json对象
			type : 'post',// post/get
			success : function(rtn) {// 成功后会调用的方法：参数，响应回来的内容转成json对象
				$.messager.alert('提示', rtn.message, 'info',function(){
					if(rtn.success){
						// 关闭详情窗口
						$('#ordersDlg').dialog('close');
						// 刷新订单列表
						$('#grid').datagrid('reload');
					}
				});
			}
		});
	});
}

/**
 * 出入库
 */
function doInOutStore(){
	$.messager.confirm('确认','确定要' + (type==1?'入':'出') +'库吗?',function(yes){
		if(yes){
			var submitData = $('#itemForm').serializeJSON();
			var url = type==1?'orderdetail_doInStore.action':'orderdetail_doOutStore.action';
			$.ajax({
				url : url,// 请求的url
				data : submitData,// 提交的数据
				dataType : 'json',// 把响应回来的内容转成json对象
				type : 'post',// post/get
				success : function(rtn) {// 成功后会调用的方法：参数，响应回来的内容转成json对象
					$.messager.alert('提示', rtn.message, 'info',function(){
						if(rtn.success){
							// 关闭入库窗口
							$('#itemDlg').dialog('close');
							
							// 获取当前正在入库明细记录
							var row = $('#itemgrid').datagrid('getSelected');
							// 改变明细的状态
							row.state = '1';
							
							// 刷新明细列表
							var data = $('#itemgrid').datagrid('getData');
							$('#itemgrid').datagrid('loadData',data);
							
							// 判断所有明细是否都已经入库了
							var flg = true;// 是否关闭订单详情
							$.each(data.rows, function(i,r){
								if(r.state == '0'){
									flg = false;
									return false;//break;跳出循环
								}
							});
							
							if(flg == true){
								// 所有的明细都完成入库了
								$('#ordersDlg').dialog('close');
								// 刷新订单列表
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

function initOrdersDatagrid(){
	//订单列表的工具栏
	var gridToolbar = [];
	var url="";
	switch(oper){
		case 'orders': url='orders_listByPage.action?t1.type=' + type; break;//订单查询
		case 'myorders': 
			url='orders_myListByPage.action?t1.type=' + type;
			gridToolbar.push({
					text:type==1?"采购申请":"销售订单录入", 
					iconCls:'icon-add', 
					handler:function(){
						//弹出采购申请窗口
						$('#addOrdersDlg').dialog('open');
					}
				}
			);
			// 初始化新增订单窗口
			initAddOrdersDlg();
			break;
		case 'doCheck': url = 'orders_listByPage.action?t1.type=1&t1.state=0'; break;//审核业务
		case 'doStart': url = 'orders_listByPage.action?t1.type=1&t1.state=1'; break;//确认业务
		case 'doInStore': url = 'orders_listByPage.action?t1.type=1&t1.state=2'; break;//入库业务
		case 'doOutStore': url = 'orders_listByPage.action?t1.type=2&t1.state=0'; break;//出库业务
	}
	
	// 数据表格的属性设置
	var gridCfg = {
		url:url,
		columns : getColumns(),
		singleSelect : true,
		pagination:true
	};
	// 订单列表的工具栏
	if(gridToolbar.length > 0){
		gridCfg.toolbar = gridToolbar;
	}
		
	// 双击行事件
	gridCfg.onDblClickRow = function(rowIndex, rowData){
		// 打开详情窗口
		$('#ordersDlg').dialog('open');
		// 显示订单编号
		$('#uuid').html(rowData.uuid);
		// 显示供应商名称
		$('#supplierName').html(rowData.supplierName);
		// 显示订单状态
		$('#state').html(formatState(rowData.state));
		// 显示下单人
		$('#createrName').html(rowData.createrName);
		// 显示审核人
		$('#checkerName').html(rowData.checkerName);
		// 显示确认人
		$('#starterName').html(rowData.starterName);
		// 显示库管员
		$('#enderName').html(rowData.enderName);
		// 显示创建日期
		$('#createtime').html(formatDate(rowData.createtime));
		// 显示审核日期
		$('#checktime').html(formatDate(rowData.checktime));
		// 显示确认日期
		$('#starttime').html(formatDate(rowData.starttime));
		// 显示入库日期
		$('#endtime').html(formatDate(rowData.endtime));
		// 加载订单明细
		$('#itemgrid').datagrid('loadData',rowData.orderdetails);
	};
	// 加载订单列表
	$('#grid').datagrid(gridCfg);
}

/**
 * 订单详情窗口
 */
function initOrdersDlg(){
	// 详情窗口 属性设置
	var ordersDlgCfg = {title:'订单详情', width:700, height:320, closed:true, modal:true};
	// 详情窗口的工具栏
	var toolbar = [];
	// 根据参数不同，添加按钮
	switch(oper){
		// 审核业务
		case 'doCheck': toolbar.push({text:'审核', iconCls:'icon-search', handler:doCheck }); break;
		// 确认业务
		case 'doStart': toolbar.push({text:'确认', iconCls:'icon-ok', handler:doStart }); break;
	}
	// 明细窗口的工具栏
	if(toolbar.length > 0){
		ordersDlgCfg.toolbar = toolbar;
	}
	
	// 详情窗口
	$('#ordersDlg').dialog(ordersDlgCfg);
	
	// 初始化订单明细列表
	initOrderdetailGrid();
}

/**
 * 新增订单窗口
 */
function initAddOrdersDlg(){
	//显示新增订单窗口
	$('#addOrdersDlg').show();
	//采购申请窗口
	$('#addOrdersDlg').dialog({
		title:type==1?'采购申请':'新增销售订单',
		width:700, height:400, modal:true, closed:true
	});
}

/**
 * 订单明细列表
 */
function initOrderdetailGrid(){
	//明细表格 属性设置
	var itemgridCfg = {
		columns : [[
 			{field:'uuid',title:'编号',width:80},
 			{field:'goodsuuid',title:'商品编号',width:80},
 			{field:'goodsname',title:'商品名称',width:100},
 			{field:'price',title:'价格',width:100},
 			{field:'num',title:'数量',width:80},
 			{field:'money',title:'金额',width:100},
 			{field:'state',title:'状态',width:100,formatter:formatDetailState}
 		]],
		singleSelect : true
	};
	
	if(oper == 'doInStore' || oper == 'doOutStore'){
		// 明细的双击行事件
		itemgridCfg.onDblClickRow=function(rowIndex, rowData){
			// 打开入库窗口
			$('#itemDlg').dialog('open');
			// 加载数据
			$('#id').val(rowData.uuid);
			// 显示商品编号
			$('#goodsuuid').html(rowData.goodsuuid);
			// 显示商品名称
			$('#goodsname').html(rowData.goodsname);
			// 显示商品数量
			$('#num').html(rowData.num);
		}
	}
	
	//商品明细表格
	$('#itemgrid').datagrid(itemgridCfg);
}

/**
 * 出入库窗口
 */
function initItemDlg(){
	var text = type==1?'入库':'出库';
	//入库窗口
	$('#itemDlg').dialog({
		title: text, width:300, height:200,modal:true,closed:true,
		buttons:[{text:text, iconCls:'icon-save', handler:doInOutStore}]
	});
}
