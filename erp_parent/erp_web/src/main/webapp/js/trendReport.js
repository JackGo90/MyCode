
var months = [];
$(function() {
	//获取当前年份
	var year = new Date().getFullYear();
	//设置选中的值
	 $('#year').combobox('setValue',year); 
	 
	$('#grid').datagrid({
		url : 'report_tr.action',
		columns : [[
  		    {field:'name',title:'月份',width:100},
  		    {field:'y',title:'销售额',width:100}
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
            type: 'line'
        },
        title: {
            text: $('#year').combobox('getValue') + '年度销售趋势'
        },
        subtitle: {
            text: 'Source: itheima.com'
        },
        xAxis: {
            categories: months
        },
        yAxis: {
        	title: {
                text: '销售额（元）'
            },
            plotLines: [{
                value: 0,
                width: 1,
                color: '#808080'
            }]
        },
        plotOptions: {
            line: {
                dataLabels: {
                    enabled: true
                },
                enableMouseTracking: false
            }
        },
        tooltip: {
            valueSuffix: '元'
        },
        series: [{
            name: '销售趋势',
            data: _data
        }]
    });
}
