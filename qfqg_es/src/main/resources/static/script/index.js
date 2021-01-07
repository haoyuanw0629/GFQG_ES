//按下回车键搜索
$(document).keyup(function (event) {
    if(event.keyCode == 13){
        search(1);
    }
});
//清空搜索框内容
function reSearch() {
    $("#keyword").val("");
}
//搜索第page页内容
function search(page) {
    //清空当前搜索结果
    $('#search-info').children('span').remove();
    $('#file-card').children('div').remove();
    $('#page-selector').children('span').remove();
    $('#page-selector').children('button').remove();
    //清空topK列表
    $('#top-keyword').children('table').remove();
    //获取用户输入的关键字
    var keyword = $("#keyword").val();
    //后台方法路径
    var searchPath = "/esgf/search/"+keyword+"/"+(page-1);
    //校检用户输入以及用户登陆状态，若未登录则返回登录界面
    if(localStorage.getItem("loginName") == null){
        alert("请先登录");
        //重定向至登录页面
        window.location.href ="/user/";
        return false;
    }
    if(keyword == ""){
        alert("搜索内容不能为空");
        return false;
    }
    //使用ajax向后台发送请求
    $.ajax({
        url:searchPath,
        type:"get",
        dataType:"json",
        contentType: "application/json",
        async: false,
        //搜索成功后返回函数
        //res包含的属性：
        success:function callbackFun(res){
            //循环打印文件内容
            fileout(res);
            //设置分页按钮
            pageout(res);
            //用于根据页面内容调整页脚位置
            reloadfooter();
        },
        //搜索失败后返回函数
        error:function(err){
            localStorage.removeItem("loginName");
            if(err.status==401){
                alert("请先登录");
                window.location.href ="/user/";
            }
        }
    });
}
//搜索关键词（topK）
function searchkey(keyword){
    $('#keyword').val(keyword);
    search(1);
}
//循环打印文件内容
function fileout(res){
    //循环打印文件信息
    //对于返回结果中的每个文件，打印文件标题，高亮内容，路径
    for(var i=0;i<res.data.length;i++){
        var list = "<div class='file-desc'><div><a class='file-name' target='_blank'>"
            +res.data[i].fileName+"</a></div>"
            +"<div class='file-text' style='size: 200px'><p>"+res.data[i].highLightFields+"</p></div>"
            +"<div><a>"+res.data[i].fileUrl+"</a></div></div>";
        $('#file-card').append(list);
        //为每一个文件名称添加url
        document.getElementsByClassName("file-name")[i].setAttribute('href','/esgf/file/'+res.data[i].id);
    }
    //搜索信息：结果数目，搜索用时
    $('#search-info').append(
        "<span>共"+res.hits+"条结果, 用时"+res.searchTime+"ms</span>"
    );
}
//设置分页按钮
function pageout(res){
    //分页按钮
    $('#page-selector').append(
        "<button class='btn btn-link' onclick='search(1)'>首页</button>");
    for(var i=0;i<res.hits/6;i++){
        if(i>res.current+9){
            break;
        } else if(i<res.current-1){

        } else {
            var pageNum = "<button class='btn btn-link' onclick='search(this.innerHTML)'>"+(i+1)+"</button>";
            $('#page-selector').append(pageNum);
        }
        //$('#page-selector').append(pageNum);
    }
}
//比较两数大小
function min(a,b){
    if(a-b<=0){
        return a;
    } else{
        return b;
    }
}
//重载页脚
function reloadfooter(){
    var footer = document.getElementById("footer");
    footer.style.marginTop = '0px';
}
//用户登出
function logout(){
    //后台登出方法路径
    var logoutPath = "/user/logout";
    //使用ajax向后台发送请求
    $.ajax({
        url:logoutPath,
        type:"get",
        dataType:"json",
        contentType: "application/json",
        async: false,
        //若成功退出则删除本地存储的用户信息
        success:function callbackFun(data){
            //解析json
            if(data.responseCode == 200){
                //删除用户信息
                localStorage.removeItem("loginName");
                window.location.href ="/user/";
            }
        },
        //登出失败则提示错误信息
        error:function(err){
            alert(err);
        }
    });
}
//页面加载是调用，欢迎标语
$(document).ready(function(){
    var username = localStorage.getItem("loginName");
    $("#user-name-li").append("您好，"+username);
})