
window.onload = function(){
	$('#loading-mask').fadeOut();
}
var onlyOpenTitle="欢迎使用";//不允许关闭的标签的标题

//全局变量
var _menus={
		"icon":"icon-sys",
		"menuid":"0",
		"menuname":"系统菜单",
		"menus":
			[
			 	{
			 		"icon":"icon-sys","menuid":"100","menuname":"基础数据","menus":
					[
						{"icon":"icon-sys","menuid":"101","menuname":"商品类型","url":"goodstype.html"}	,
						{"icon":"icon-sys","menuid":"102","menuname":"商品","url":"goods.html"},
						{"icon":"icon-sys","menuid":"103","menuname":"仓库","url":"store.html"}
					]
			 	}
			 	
			 ]
		};




$(function(){	
	
	//异步请求
	$.ajax({
		url : 'menu_getMenuTree.action',//请求的url
		dataType : 'json',//把响应回来的内容转成json对象
		type : 'post',//post/get
		success : function(rtn) {//成功后会调用的方法：参数，响应回来的内容转成json对象
			_menus = rtn;
			//初始化左边菜单
			InitLeftMenu();
		}
	});
	
	//初始化标签
	tabClose();
	//标签的关闭事件
	tabCloseEven();
	
	showName();
	
	//绑定退出登陆的方法
	$('#loginOut').bind('click',function(){
		$.ajax({
			url : 'login_loginOut.action',//请求的url
			type : 'post',//post/get
			success : function(rtn) {//成功后会调用的方法：参数，响应回来的内容转成json对象
				location.href="login.html";
			}
		});
	});
	
});

/**
 * 显示登陆用户名
 */
function showName(){
	$.ajax({
		url : 'login_showName.action',//请求的url
		dataType : 'json',//把响应回来的内容转成json对象
		type : 'post',//post/get
		success : function(rtn) {//成功后会调用的方法：参数，响应回来的内容转成json对象
			if(rtn.success){
				//显示登陆用户名
				$('#username').html(rtn.message);
			}else{
				$.messager.alert('提示', rtn.message, 'info',function(){
					//回到登陆的页面
					location.href="login.html";
				});
			}
		}
	});
}




//初始化左侧
function InitLeftMenu() {
	$("#nav").accordion({animate:false,fit:true,border:false});
	var selectedPanelname = '';
	
	    $.each(_menus.menus, function(i, n) {
			var menulist ='';
			menulist +='<ul class="navlist">';
	        $.each(n.menus, function(j, o) {
				menulist += '<li><div ><a ref="'+o.menuid+'" href="#" rel="' + o.url + '" ><span class="icon '+o.icon+'" >&nbsp;</span><span class="nav">' + o.menuname + '</span></a></div> ';
				/*
				if(o.child && o.child.length>0)
				{
					//li.find('div').addClass('icon-arrow');
	
					menulist += '<ul class="third_ul">';
					$.each(o.child,function(k,p){
						menulist += '<li><div><a ref="'+p.menuid+'" href="#" rel="' + p.url + '" ><span class="icon '+p.icon+'" >&nbsp;</span><span class="nav">' + p.menuname + '</span></a></div> </li>'
					});
					menulist += '</ul>';
				}
				*/
				menulist+='</li>';
	        })
			menulist += '</ul>';
	
			$('#nav').accordion('add', {
	            title: n.menuname,
	            content: menulist,
					border:false,
	            iconCls: 'icon ' + n.icon
	        });
	
			if(i==0)
				selectedPanelname =n.menuname;
	
	    });
	
	$('#nav').accordion('select',selectedPanelname);



	$('.navlist li a').click(function(){
		var tabTitle = $(this).children('.nav').text();

		var url = $(this).attr("rel");
		var menuid = $(this).attr("ref");
		var icon = $(this).find('.icon').attr('class');

		var third = find(menuid);
		if(third && third.child && third.child.length>0)
		{
			$('.third_ul').slideUp();

			var ul =$(this).parent().next();
			if(ul.is(":hidden"))
				ul.slideDown();
			else
				ul.slideUp();



		}
		else{
			addTab(tabTitle,url,icon);
			$('.navlist li div').removeClass("selected");
			$(this).parent().addClass("selected");
		}
	}).hover(function(){
		$(this).parent().addClass("hover");
	},function(){
		$(this).parent().removeClass("hover");
	});





	//选中第一个
	//var panels = $('#nav').accordion('panels');
	//var t = panels[0].panel('options').title;
    //$('#nav').accordion('select', t);
}
//获取左侧导航的图标
function getIcon(menuid){
	var icon = 'icon ';
	$.each(_menus.menus, function(i, n) {
		 $.each(n.menus, function(j, o) {
		 	if(o.menuid==menuid){
				icon += o.icon;
			}
		 })
	})

	return icon;
}

function find(menuid){
	var obj=null;
	$.each(_menus.menus, function(i, n) {
		 $.each(n.menus, function(j, o) {
		 	if(o.menuid==menuid){
				obj = o;
			}
		 });
	});

	return obj;
}

function addTab(subtitle,url,icon){
	if(!$('#tabs').tabs('exists',subtitle)){
		$('#tabs').tabs('add',{
			title:subtitle,
			content:createFrame(url),
			closable:true,
			icon:icon
		});
	}else{
		$('#tabs').tabs('select',subtitle);
		$('#mm-tabupdate').click();
	}
	tabClose();
}

function createFrame(url)
{
	var s = '<iframe scrolling="auto" frameborder="0"  src="'+url+'" style="width:100%;height:100%;"></iframe>';
	return s;
}

function tabClose()
{
	/*双击关闭TAB选项卡*/
	$(".tabs-inner").dblclick(function(){
		var subtitle = $(this).children(".tabs-closable").text();
		$('#tabs').tabs('close',subtitle);
	})
	/*为选项卡绑定右键*/
	$(".tabs-inner").bind('contextmenu',function(e){
		$('#mm').menu('show', {
			left: e.pageX,
			top: e.pageY
		});

		var subtitle =$(this).children(".tabs-closable").text();

		$('#mm').data("currtab",subtitle);
		$('#tabs').tabs('select',subtitle);
		return false;
	});
}


//绑定右键菜单事件
function tabCloseEven() {

    $('#mm').menu({
        onClick: function (item) {
            closeTab(item.id);
        }
    });

    return false;
}

function closeTab(action)
{
    var alltabs = $('#tabs').tabs('tabs');
    var currentTab =$('#tabs').tabs('getSelected');
	var allTabtitle = [];
	$.each(alltabs,function(i,n){
		allTabtitle.push($(n).panel('options').title);
	})


    switch (action) {
        case "refresh":
            var iframe = $(currentTab.panel('options').content);
            var src = iframe.attr('src');
            $('#tabs').tabs('update', {
                tab: currentTab,
                options: {
                    content: createFrame(src)
                }
            })
            break;
        case "close":
            var currtab_title = currentTab.panel('options').title;
            $('#tabs').tabs('close', currtab_title);
            break;
        case "closeall":
            $.each(allTabtitle, function (i, n) {
                if (n != onlyOpenTitle){
                    $('#tabs').tabs('close', n);
				}
            });
            break;
        case "closeother":
            var currtab_title = currentTab.panel('options').title;
            $.each(allTabtitle, function (i, n) {
                if (n != currtab_title && n != onlyOpenTitle)
				{
                    $('#tabs').tabs('close', n);
				}
            });
            break;
        case "closeright":
            var tabIndex = $('#tabs').tabs('getTabIndex', currentTab);

            if (tabIndex == alltabs.length - 1){
                alert('亲，后边没有啦 ^@^!!');
                return false;
            }
            $.each(allTabtitle, function (i, n) {
                if (i > tabIndex) {
                    if (n != onlyOpenTitle){
                        $('#tabs').tabs('close', n);
					}
                }
            });

            break;
        case "closeleft":
            var tabIndex = $('#tabs').tabs('getTabIndex', currentTab);
            if (tabIndex == 1) {
                alert('亲，前边那个上头有人，咱惹不起哦。 ^@^!!');
                return false;
            }
            $.each(allTabtitle, function (i, n) {
                if (i < tabIndex) {
                    if (n != onlyOpenTitle){
                        $('#tabs').tabs('close', n);
					}
                }
            });

            break;
        case "exit":
            $('#closeMenu').menu('hide');
            break;
    }
}


//弹出信息窗口 title:标题 msgString:提示信息 msgType:信息类型 [error,info,question,warning]
function msgShow(title, msgString, msgType) {
	$.messager.alert(title, msgString, msgType);
}




//设置登录窗口
function openPwd() {
    $('#w').dialog({
        title: '修改密码',
        width: 300,
        modal: true,
        shadow: true,
        closed: true,
        height: 180,
        buttons:[
            {
            	text:'保存',
            	iconCls:'icon-save',
            	handler:function(){
            		var $oldPass = $('#txtOldPass');//旧密码
            		//$oldPass.val($.trim($oldPass.val()));//val(),有参数，则为赋值，否则为取值
            		var $newpass = $('#txtNewPass');//新密码
            	    var $rePass = $('#txtRePass');//重复密码

            	    if ($oldPass.val() == '') {
            	        msgShow('系统提示', '请输入原密码！', 'warning');
            	        return false;
            	    }
            	    
            	    if ($newpass.val() == '') {
            	        msgShow('系统提示', '请输入新密码！', 'warning');
            	        return false;
            	    }
            	    if ($rePass.val() == '') {
            	        msgShow('系统提示', '请再一次输入密码！', 'warning');
            	        return false;
            	    }

            	    if ($newpass.val() != $rePass.val()) {
            	        msgShow('系统提示', '两次密码不一至！请重新输入', 'warning');
            	        return false;
            	    }
            	    
            	    var submitData = $('#editPwdForm').serializeJSON();
            	    $.ajax({
						url : 'emp_updatePwd.action',//请求的url
						data : submitData,//提交的数据
						dataType : 'json',//把响应回来的内容转成json对象
						type : 'post',//post/get
						success : function(rtn) {//成功后会调用的方法：参数，响应回来的内容转成json对象
							$.messager.alert('提示', rtn.message, 'info',function(){
								if(rtn.success){
									//清空表单内容
				            		$('#editPwdForm').form('clear');
				            		//关闭窗口
				            		 $('#w').dialog('close');
								}
							});
						}
					});
            	}
            },
            {
            	text:'取消',
            	iconCls:'icon-cancel',
            	handler:function(){
            		//清空表单内容
            		$('#editPwdForm').form('clear');
            		//关闭窗口
            		 $('#w').dialog('close');
            	}
            }
        ]
    });
}
//关闭登录窗口
function closePwd() {
    $('#w').window('close');
}



//修改密码
function serverLogin() {
    var $newpass = $('#txtNewPass');
    var $rePass = $('#txtRePass');

    if ($newpass.val() == '') {
        msgShow('系统提示', '请输入密码！', 'warning');
        return false;
    }
    if ($rePass.val() == '') {
        msgShow('系统提示', '请在一次输入密码！', 'warning');
        return false;
    }

    if ($newpass.val() != $rePass.val()) {
        msgShow('系统提示', '两次密码不一至！请重新输入', 'warning');
        return false;
    }

    $.post('/ajax/editpassword.ashx?newpass=' + $newpass.val(), function(msg) {
        msgShow('系统提示', '恭喜，密码修改成功！<br>您的新密码为：' + msg, 'info');
        $newpass.val('');
        $rePass.val('');
        close();
    })
    
}

$(function() {

    openPwd();

    $('#editpass').click(function() {
        $('#w').window('open');
    });

    $('#btnEp').click(function() {
        serverLogin();
    })

	$('#btnCancel').click(function(){closePwd();})

   
});


