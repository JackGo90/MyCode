$(function() {
	$('#grid').datagrid({
		url : 'storeoper_listByPage.action',
		columns : [[
  		    {field:'uuid',title:'编号',width:100},
  		    {field:'empName',title:'操作员工',width:100},
  		    {field:'opertime',title:'操作日期',width:100,formatter:formatDate},
  		    {field:'storeName',title:'仓库',width:100},
  		    {field:'goodsName',title:'商品',width:100},
  		    {field:'num',title:'数量',width:100},
  		    {field:'type',title:'类型',width:100,formatter:function(value){
  		    	switch(value * 1){
	  		    	case 1: return '入库';
	  		    	case 2: return '出库';
	  		    	default: return '';
  		    	}
  		    }}
		]],
		singleSelect : true,
		pagination:true
	});
	//点击查询按钮
	$('#btnSearch').bind('click',function(){
		//把表单数据转换成json对象
		var formData = $('#searchForm').serializeJSON();
		$('#grid').datagrid('load',formData);
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
}