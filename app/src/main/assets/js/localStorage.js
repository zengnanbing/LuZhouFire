//查询浏览器是否可以使用localStorage
//页面缓存机制
if(window.localStorage){
	//储存页面内容,以JSON格式保存
	function saveSettings(){

		var data = new Object();
		$("input:text").each(function(){
			data[this.name] = this.value;
    	});
		$("input:hidden").each(function(){
			data[this.name] = this.value;
    	});
    		    		
    	$("input:radio:checked").each(function(){
    		data[this.name] = this.value;
    	});
    		
    	$("input:checkbox:checked").each(function(){
    		data[this.name] = this.value;
    	});
    	
    	var str = JSON.stringify(data);
    	localStorage.setItem(data.unitname+"+"+data.tableType,str);
    	window.android.save(data.unitname+"+"+data.tableType);
    	location.hash = '#list';
    }
}