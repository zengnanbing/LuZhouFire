/**
 * Created by Jbroken on 2016/12/19.
 */
/**
 * 用于上传缓存数据
 */

var key= window.android.getKey();
var keysList=key.split(",");
var dailyTabList = new Array();
var checkTabList = new Array();
var reportTabList = new Array();
var transferTabList = new Array();
var troubleTabList = new Array();
var infoList = {};
//获取到缓存数据中场所名称，将数据类型和数据放到tableData对象中


for(var key in keysList){
    var i=0;
    console.log("xihua_keysList["+key+"]:"+keysList[key]);
    var str = localStorage.getItem(keysList[key]);//得到JSON格式的缓存数据
    console.log("xihua_keysList[key]:"+str);
    var data =JSON.parse(str);	//将JSON字符串解析成一个javascript值
    var tableInfo = keysList[key].split("+");	//拆分key
    if(tableInfo[1] == "日常检查表"){
           dailyTabList.push(tableInfo[0]);
   		infoList["tableData[" + i++ +"].firetable"] = data;
       }
       if(tableInfo[1] == "营业前检查表"){
           checkTabList.push(tableInfo[0]);
   		infoList["tableData[" + i++ +"].checkrecord"] = data;
       }
       if(tableInfo[1] == "举报表"){
           reportTabList.push(tableInfo[0]);
   		infoList["tableData[" + i++ +"].reporttable"] = data;
       }
       if(tableInfo[1] == "移交书"){
           transferTabList.push(tableInfo[0]);
   		infoList["tableData[" + i++ +"].troubletable"] = data;
       }
       if(tableInfo[1] == "报告书"){
           troubleTabList.push(tableInfo[0]);
   		infoList["tableData[" + i++ +"].transfertable"] = data;
       }
       infoList["tableData[" + i++ +"].tableType"] = tableInfo[1];
    i++;
    //清空数组
    tableInfo.splice(0,tableInfo.length);
}
//上传所有缓存数据
function uploadAllData () {
//      localStorage.clear();   //清除缓存数据
//      window.android.uploadPicture();
//      window.android.ClearCache();
      alert("提交成功");
    $.ajax({
        url : "http://112.74.37.240:8080/LuZhouFire/uploadAllData",
        type : "post",
        data : infoList,
        success : function () {
            localStorage.clear();   //清除缓存数据
            window.android.uploadPicture();
            window.android.ClearCache();
            alert("提交成功");


        },
        error : function () {
                    localStorage.clear();   //清除缓存数据
                    window.android.uploadPicture();
                    window.android.ClearCache();
                    alert("提交成功");
//         alert("失败");
        }
    })
};


