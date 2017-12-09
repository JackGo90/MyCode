//当前编辑行的索引
var exitEditIndex=-1;
$(function() {
	$('#ordersgrid').datagrid({
		columns : [ [
			{field:'goodsuuid',title:'商品编号',width:100,editor:{type:'numberbox',options:{
				disabled:true//不可编辑
			}}},
			{field:'goodsname',title:'商品名称',width:100,editor:{type:'combobox',options:{
				url:'goods_list.action',
				valueField:'name',
				textField:'name',
				onSelect:function(goods){
					//在用户选择列表项的时候触发, goods=>选中的商品
					
					//alert(JSON.stringify(record));
					//getEditor: 获取单元格的编辑器
					// 参数：options， 
					//        index：行索引。
					//        field：字段名称。
					//获取索引，通过获取所有的行的length
		    		/*var rows = $('#ordersgrid').datagrid('getRows');
		    		var index = rows.length-1;*/
		    		//商品编号
					var goodsuuidEditor = getEditor('goodsuuid');
					$(goodsuuidEditor.target).val(goods.uuid);
					//价格
					var priceEditor = getEditor('price');
					//$(priceEditor.target).numberbox('setValue',goods.inprice);
					if(type == 1){
						$(priceEditor.target).val(goods.inprice);
					}
					if(type == 2){
						$(priceEditor.target).val(goods.outprice);
					}
					
					//选中数量输入框
					var numEditor = getEditor('num');
					$(numEditor.target).select();
					
					//计算 商品的金额
					cal();
					//计算合计金额
					sum();

				}
			}}},
			{field:'price',title:'价格',width:100,editor:{type:'numberbox',options:{
				disabled:true,//不可编辑
				precision:2
				//,prefix:'￥'
			}}},
			{field:'num',title:'数量',width:100,editor:'numberbox'},
			{field:'money',title:'金额',width:100,editor:{type:'numberbox',options:{
				disabled:true,//不可编辑
				precision:2
			}}},
			{field:'-',title:'操作',width:100,formatter:function(value,row,index){
				if(row.num == '合计'){
					return;
				}
				return '<a href="javascript:void(0)" onclick="deleteRow(' + index + ')">删除</a>';
			}}
		] ],
		singleSelect : true,
		showFooter:true,//显示行脚
		toolbar:[
		    {
		    	text:'新增',
		    	iconCls:'icon-add',
		    	handler:function(){
		    		
		    		//获取索引，通过获取所有的行的length
		    		//var rows = $('#ordersgrid').datagrid('getRows');
		    		//var index = rows.length-1;
		    		//当前存在编辑的行
		    		if(exitEditIndex > -1){
		    			//不是第一次增加行，原来就已经有记录行存在了
		    			//关闭编辑行
		    			$('#ordersgrid').datagrid('endEdit',exitEditIndex);
		    		}
		    		
		    		//追加一行，新的行将会添加到最后
		    		$('#ordersgrid').datagrid('appendRow',{
		    			num: 0,
		    			money:0
		    		});
		    		//由于加了一行，索引也要+1
		    		//index++;
		    		var rows = $('#ordersgrid').datagrid('getRows');
		    		exitEditIndex = rows.length - 1;
		    		//开启编辑
		    		$('#ordersgrid').datagrid('beginEdit',exitEditIndex);
		    		//绑定事件
					bindGridEvent();
		    	}
		    },'-',
		    {
		    	text:'提交',
		    	iconCls:'icon-save',
		    	handler:function(){
		    		if(exitEditIndex > -1){
		    			//关闭编辑行
		    			$('#ordersgrid').datagrid('endEdit',exitEditIndex);
		    		}
		    		var submitData = $('#ordersForm').serializeJSON();
		    		
		    		if(submitData['t.supplieruuid'] == ''){
		    			$.messager.alert('提示',"请选择供应商", 'info');
		    			return;
		    		}
		    		var rows = $('#ordersgrid').datagrid('getRows');
		    		submitData.jsonData = JSON.stringify(rows);//map.put(jsonData, JSON.stringify(rows));
		    		//submitData['jsonData'] = JSON.stringify(rows);
		    		//submitData['t.type']=type;
		    		
		    		$.ajax({
						url : 'orders_add.action?t.type=' + type,//请求的url
						data : submitData,//提交的数据
						dataType : 'json',//把响应回来的内容转成json对象
						type : 'post',//post/get
						success : function(rtn) {//成功后会调用的方法：参数，响应回来的内容转成json对象
							$.messager.alert('提示', rtn.message, 'info',function(){
								//清除供应商
								$('#supplier').combogrid('clear');
								//清空列表
								$('#ordersgrid').datagrid('loadData',{
									total:0,
									rows:[],
									footer:[{
										num: '合计', money: 0
									}]
								});
								
								//关闭申请窗口
								$('#addOrdersDlg').dialog('close');
								//刷新订单列表
								$('#grid').datagrid('reload');
							});
						}
					});
		    		
		    	}
		    }
		],
		onClickRow:function(rowIndex, rowData){
			//在用户点击一行的时候触发，参数包括
			/*
			 rowIndex：点击的行的索引值，该索引值从0开始。
			 rowData：对应于点击行的记录
			*/
			//关闭正在编辑的行
			$('#ordersgrid').datagrid('endEdit',exitEditIndex);
			
			//开启单击中的这一行的编辑, 这会编辑行变成了单击的这一行
			exitEditIndex = rowIndex;
			//开启编辑
			$('#ordersgrid').datagrid('beginEdit',exitEditIndex);
			//绑定事件
			bindGridEvent();
		}
	});
	
	//加载行脚数据
	$('#ordersgrid').datagrid('reloadFooter',[{num: '合计', money: 0}]);
	
	type==1?$('#addOrdersSupplier').html('供应商'):$('#addOrdersSupplier').html('客户');;
	
	//供应商下拉表格
	$('#supplier').combogrid({    
	    panelWidth:750,//表格的宽度
	    //value:'006',//选中的值
	    idField:'uuid',//要提交的字段的值
	    textField:'name',
	    mode:'remote',//用户输入将会发送到名为'q'的http请求参数，向服务器检索新的数据
	    url:'supplier_list.action?t1.type=' + type,
	    columns:[[    
	         {field:'uuid',title:'编号',width:100},
	  		 {field:'name',title:'名称',width:100},
	  		 {field:'address',title:'联系地址',width:130},
	  		 {field:'contact',title:'联系人',width:100},
	  		 {field:'tele',title:'联系电话',width:100},
	  		 {field:'email',title:'邮件地址',width:180}
	    ]]    
	}); 
});

/**
 * 获取编辑器
 * @param _field
 * @returns
 */
function getEditor(_field){
	return $('#ordersgrid').datagrid('getEditor',{index:exitEditIndex,field:_field})
}

/**
 * 计算金额
 */
function cal(){
	//拿出价格
	var priceEditor = getEditor('price');
	var price = $(priceEditor.target).val();
	
	//拿出数量
	var numEditor = getEditor('num');
	var num = $(numEditor.target).val();
	
	var money = num * price;
	//取两小数
	money = money.toFixed(2);
	
	//alert(money);
	
	var moneyEditor = getEditor('money');
	$(moneyEditor.target).val(money);
	
	//给datagrid的数据手工赋值
	$('#ordersgrid').datagrid('getRows')[exitEditIndex].money=money;
	
}

/**
 * 绑定事件
 */
function bindGridEvent(){
	//获取数量编辑器
	var numEditor = getEditor('num');
	//$(numEditor.target).bind('change',cal);
	$(numEditor.target).bind('input',function(){
		//计算 商品的金额
		cal();
		//计算合计金额
		sum();
	});
}

/**
 * 删除行
 * @param _index
 */
function deleteRow(_index){
	//关闭编辑
	$('#ordersgrid').datagrid('endEdit',exitEditIndex);
	
	$('#ordersgrid').datagrid('deleteRow',_index);
	
	//刷新数据，触发行的重新加载，解决删除时的index越界问题
	var data = $('#ordersgrid').datagrid('getData');
	//loadData：旧的行将被移除，达到刷新的效果
	$('#ordersgrid').datagrid('loadData',data);
	//重新计算合计金额
	sum();
}

/**
 * 计算合计金额
 */
function sum(){
	var rows = $('#ordersgrid').datagrid('getRows');
	/*for(var i = 0; i < rows.length; i++){
		row = rows[i]
	}*/
	var totalMoney = 0;
	$.each(rows, function(i,row){
		totalMoney+=row.money * 1;
	});
	//alert(totalMoney);
	
	//加载行脚数据
	$('#ordersgrid').datagrid('reloadFooter',[{num: '合计', money: totalMoney.toFixed(2)}]);
}