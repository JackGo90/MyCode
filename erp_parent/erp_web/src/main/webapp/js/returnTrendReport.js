
var months = [];
$(function() {
	//获取当前年份
	var year = new Date().getFullYear();
	//设置选中的值
	 $('#year').combobox('setValue',year); 
	 
	$('#grid').datagrid({
		url : 'report_returnTrendReport.action',
		columns : [[
  		    {field:'name',title:'月份',width:100},
  		    {field:'y',title:'退货金额',width:100}
		]],
		singleSelect : true,
		onLoadSuccess:function(data){
			//alert(JSON.stringify(data));
			showChart(data.rows);
		},
		queryParams: {//在请求远程数据的时候发送额外的参数
			year:year
		}
	});
	//点击查询按钮
	$('#btnSearch').bind('click',function(){
		//把表单数据转换成json对象
		var formData = $('#searchForm').serializeJSON();
		$('#grid').datagrid('load',formData);
	});
	
	for(var i = 1; i <=12; i++){
		months.push(i + "月");
	}
	
});

function showChart(_data){
	$('#chart').highcharts({
		 chart: {
	            type: 'column'
	        },
	        title: {
	            text: '月退货趋势'
	        },
	        subtitle: {
	            text: $('#year').combobox('getValue') + '年度退货趋势'
	        },
	        xAxis: {
	            categories: [
	                '一月',
	                '二月',
	                '三月',
	                '四月',
	                '五月',
	                '六月',
	                '七月',
	                '八月',
	                '九月',
	                '十月',
	                '十一月',
	                '十二月'
	            ],
	            crosshair: true
	        },
	        yAxis: {
	            min: 0,
	            title: {
	                text: '退货金额 (元)'
	            }
	        },
	        tooltip: {
	            headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
	            pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
	            '<td style="padding:0"><b>{point.y:.1f} mm</b></td></tr>',
	            footerFormat: '</table>',
	            shared: true,
	            useHTML: true
	        },
	        plotOptions: {
	            column: {
	                pointPadding: 0.2,
	                borderWidth: 0
	            }
	        },
	        series: [{
	        	name: '退货趋势',
	        	data: _data
	             }]
		
	});

}
