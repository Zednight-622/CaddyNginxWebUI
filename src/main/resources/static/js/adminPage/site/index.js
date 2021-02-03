$(function() {
	form.on('switch(enable)', function(data) {

		$.ajax({
			type: 'POST',
			url: ctx + '/adminPage/server/setEnable',
			data: {
				enable: data.elem.checked ? 1 : 0,
				id: data.elem.value
			},
			dataType: 'json',
			success: function(data) {

			},
			error: function() {
				layer.alert(commonStr.errorInfo);
			}
		});
	});


	form.on('select(type)', function(data) {
		checkType(data.value, $(data.elem).attr("lang"));
	});
	form.on('select(ssl)', function(data) {
		checkSsl(data.value);
	});
	form.on('select(proxyType)', function(data) {
		checkProxyType(data.value);
	});
	form.on('select(rewrite)', function(data) {
		checkRewrite(data.value);
	});
	
	layui.use('upload', function() {
		var upload = layui.upload;
		upload.render({
			elem: '#pemBtn',
			url: '/adminPage/main/upload/',
			accept: 'file',
			done: function(res) {
				// 上传完毕回调
				if (res.success) {
					$("#pem").val(res.obj);
					$("#pemPath").html(res.obj);
				}

			},
			error: function() {
				// 请求异常回调
			}
		});

		upload.render({
			elem: '#keyBtn',
			url: '/adminPage/main/upload/',
			accept: 'file',
			done: function(res) {
				// 上传完毕回调
				if (res.success) {
					$("#key").val(res.obj);
					$("#keyPath").html(res.obj);
				}
			},
			error: function() {
				// 请求异常回调
			}
		});
	});
})

function checkType(type, id) {
	if (type == 0) {
		$("#" + id + " span[name='valueSpan']").show();
		$("#" + id + " span[name='rootPathSpan']").hide();
		$("#" + id + " span[name='upstreamSelectSpan']").hide();
		$("#" + id + " span[name='blankSpan']").hide();
		$("#" + id + " span[name='headerSpan']").show();
	}
	if (type == 1) {
		$("#" + id + " span[name='valueSpan']").hide();
		$("#" + id + " span[name='rootPathSpan']").show();
		$("#" + id + " span[name='upstreamSelectSpan']").hide();
		$("#" + id + " span[name='blankSpan']").hide();
		$("#" + id + " span[name='headerSpan']").hide();
	}
	if (type == 2) {
		$("#" + id + " span[name='valueSpan']").hide();
		$("#" + id + " span[name='rootPathSpan']").hide();
		$("#" + id + " span[name='upstreamSelectSpan']").show();
		$("#" + id + " span[name='blankSpan']").hide();
		$("#" + id + " span[name='headerSpan']").show();
	}
	if (type == 3) {
		$("#" + id + " span[name='valueSpan']").hide();
		$("#" + id + " span[name='rootPathSpan']").hide();
		$("#" + id + " span[name='upstreamSelectSpan']").hide();
		$("#" + id + " span[name='blankSpan']").show();
		$("#" + id + " span[name='headerSpan']").hide();
	}
}

function checkSsl(value) {
	if (value == 0) {
		$(".pemDiv").hide();
	}
	if (value == 1) {
		$(".pemDiv").show();
	}
}

function checkProxyType(value) {
	if (value == 0) {
		$(".proxyHttp").show();
		$(".proxyTcp").hide();

	}
	if (value == 1 || value == 2) {
		$(".proxyHttp").hide();
		$(".proxyTcp").show();
	}

}

function checkRewrite(value){
	if (value == null || value == '' || value == 0) {
		$("#rewriteListenDiv").hide();

	}
	if (value == 1) {
		$("#rewriteListenDiv").show();
	}
}

function search() {
	$("#searchForm").submit();
}

function add() {
	$("#id").val("");
	$("#listen").val("");
	$("#def").prop("checked", false);
	$("#ip").val("");
	$("#serverName").val("");
	$("#ssl option:first").prop("selected", true);
	$("#rewrite option:first").prop("selected", true);
	$("#http2 option:first").prop("selected", true);
	$("#proxyType option:first").prop("selected", true);
	$("#proxyUpstreamId option:first").prop("selected", true);
	$("#passwordId option:first").prop("selected", true);
	
	$("#rewriteListen").val("443");

	$("#pem").val("");
	$("#pemPath").html("");
	$("#key").val("");
	$("#keyPath").html("");
	$("#itemList").html("");
	$("#paramJson").val("");
	
	$(".protocols").prop("checked",true);
	
	checkProxyType(0);
	checkSsl(0);
	checkRewrite(1);

	form.render();
	showWindow(serverStr.add);
}

function showWindow(title) {
	layer.open({
		type: 1,
		title: title,
		area: ['1250px', '700px'], // 宽高
		content: $('#windowDiv')
	});
}

function addOver() {
	// if ($("#port").val().trim() == '') {
	// 	layer.msg(serverStr.noPort);
	// 	return;
	// }
	//
	// if ($("#ssl").val() == 1 && $("#serverName").val() == '') {
	// 	layer.msg(serverStr.sslTips);
	// 	return;
	// }
	//
	// var over = true;
	// $("input[name='path']").each(function() {
	// 	if ($(this).val().trim() == '') {
	// 		over = false;
	// 	}
	// })
	// $("input[name='value']").each(function() {
	// 	if (!$(this).is(":hidden") && $(this).val().trim() == '') {
	// 		over = false;
	// 	}
	// })
	// if (!over) {
	// 	layer.msg(serverStr.noFill);
	// 	return;
	// }


	var site = {};
	site.id = $("#id").val();
	site.name = $("#ip").val();
	site.port = $("#port").val();
	site.isGzip = $("#gzip").val();
	site.isZstd = $("#zstd").val();
	site.isRedir = $("#redir").val();


	var siteParamJson = $("#siteParamJson").val();

	var tos = [];

	$(".itemList").children().each(function() {
		var to = {};
		to.location = $(this).find("input[name='location']").val();
		to.proxyAddress = $(this).find("input[name='proxyAddress']").val();
		to.toParamJson = $(this).find("textarea[name='toParamJson']").val();
		tos.push(to);
	})
	$.ajax({
		type: 'POST',
		url: ctx + '/adminPage/site/addOver',
		data: {
			siteJson: JSON.stringify(site),
			siteParamJson: siteParamJson,
			toJson: JSON.stringify(tos),
		},
		dataType: 'json',
		success: function(data) {
			if (data.success) {
				location.reload();
			} else {
				layer.msg(data.msg);
			}
		},
		error: function() {
			layer.alert(commonStr.errorInfo);
		}
	});
}

function edit(id, clone) {
	$("#id").val(id);

	$.ajax({
		type: 'GET',
		url: ctx + '/adminPage/site/detail',
		dataType: 'json',
		data: {
			id: id
		},
		success: function(data) {
			if (data.success) {

				var site = data.obj.site;
				$("#ip").val(site.name)
				$("#port").val(site.port)

				$("#def").prop("checked", site.def == 1);
				$("#serverName").val(site.serverName);
				$("#siteParamJson").val(data.obj.paramJson);
				if (site.isGzip != null) {
					$("#gzip").val(site.isGzip);
				}else{
					$("#gzip option:first").prop("selected", true);
				}
				if (site.isZstd != null) {
					$("#zstd").val(site.isZstd);
				}else{
					$("#zstd option:first").prop("selected", true);
				}
				if (site.isRedir != null) {
					$("#redir").val(site.isRedir);
				}else{
					$("#redir option:first").prop("selected", true);
				}

				form.render();

				
				var list = data.obj.toList;

				$("#itemList").html("");
				for (let i = 0; i < list.length; i++) {
					var to = list[i];
					var uuid = guid();
					var html = buildHtml(uuid, to, upstreamSelect);

					$("#itemList").append(html);
					$("#" + uuid + " input[name='proxyAddress']").val(to.proxyAddress);
					$("#" + uuid + " input[name='rootType']").val(location.rootType);
					$("#" + uuid + " input[name='rootPath']").val(location.rootPath);
					$("#" + uuid + " input[name='rootPage']").val(location.rootPage);
					$("#" + uuid + " select[name='rootType']").val(location.rootType);
					$("#" + uuid + " select[name='upstreamId']").val(location.upstreamId);
					$("#" + uuid + " input[name='upstreamPath']").val(location.upstreamPath);

					checkType(location.type, uuid)
				}

				form.render();
				showWindow(serverStr.edit);
			} else {
				layer.msg(data.msg);
			}
		},
		error: function() {
			layer.alert(commonStr.errorInfo);
		}
	});
}


function del(id) {
	if (confirm(commonStr.del)) {
		$.ajax({
			type: 'POST',
			url: ctx + '/adminPage/site/del',
			data: {
				id: id
			},
			dataType: 'json',
			success: function(data) {
				if (data.success) {
					location.reload();
				} else {
					layer.msg(data.msg)
				}
			},
			error: function() {
				layer.alert(commonStr.errorInfo);
			}
		});
	}
}



function addItem() {
	var uuid = guid();

	var upstreamSelect = $("#upstreamSelect").html();

	var html = buildHtml(uuid, null, upstreamSelect);

	$("#itemList").append(html);
	checkType(0, uuid);
	form.render();

}



function buildHtml(uuid, to, upstreamSelect) {
	if (to == null) {
		to = {
			location: "/",
			type: "0",
			toParamJson: ""
		};
	}


	var str = `<tr id='${uuid}'>
				<td>
					<input type="text" name="location" class="layui-input short" value="${to.location}">
				</td>
				<td>
					<span name="valueSpan">
						<div class="layui-inline">
							<input type="text"  style="width: 315px;" name="proxyAddress" id="value_${uuid}" class="layui-input long" value=""  placeholder="${serverStr.example}：127.0.0.1:8080 多个代理目标用空格隔开">
						</div>
					</span>
				</td> 
				<td>
					<textarea style="display: none;" id="locationParamJson_${uuid}" name="toParamJson" >${to.toParamJson}</textarea>
					<button type="button" class="layui-btn layui-btn-sm" onclick="locationParam('${uuid}')">${serverStr.extParm}</button>
					<button type="button" class="layui-btn layui-btn-sm layui-btn-danger" onclick="delTr('${uuid}')">${commonStr.del}</button>
					
					<button type="button" class="layui-btn layui-btn-normal layui-btn-sm" onclick="setParamOrder('${uuid}', -1)">${commonStr.up}</button>
					<button type="button" class="layui-btn layui-btn-normal layui-btn-sm" onclick="setParamOrder('${uuid}', 1)">${commonStr.down}</button>
				</td>
			</tr>`

	return str;
}


function delTr(id) {
	$("#" + id).remove();
}

var certIndex;
function selectCert() {
	certIndex = layer.open({
		type: 1,
		title: serverStr.selectCert,
		area: ['500px', '300px'], // 宽高
		content: $('#certDiv')
	});

}

function selectCertOver() {
	var id = $("#certId").val();

	$.ajax({
		type: 'POST',
		url: ctx + '/adminPage/cert/detail',
		data: {
			id: id
		},
		dataType: 'json',
		success: function(data) {
			if (data.success) {
				var cert = data.obj;
				$("#pem").val(cert.pem);
				$("#pemPath").html(cert.pem);
				$("#key").val(cert.key);
				$("#keyPath").html(cert.key);

				layer.close(certIndex);
			} else {
				layer.msg(data.msg)
			}
		},
		error: function() {
			layer.alert(commonStr.errorInfo);
		}
	});
}



function selectPem() {
	rootSelect.selectOne(function(rs) {
		$("#pem").val(rs);
		$("#pemPath").html(rs);
	})
}


function selectKey() {
	rootSelect.selectOne(function(rs) {
		$("#key").val(rs);
		$("#keyPath").html(rs);
	})
}


function siteParam() {
	var json = $("#siteParamJson").val();
	$("#targertId").val("siteParamJson");
	var params = json != '' ? JSON.parse(json) : [];
	fillTable(params);

}

function locationParam(uuid) {
	var json = $("#locationParamJson_" + uuid).val();
	$("#targertId").val("locationParamJson_" + uuid);
	var params = json != '' ? JSON.parse(json) : [];
	fillTable(params);
}

var paramIndex;
function fillTable(params) {
	var html = "";
	for (var i = 0; i < params.length; i++) {
		var param = params[i];

		var uuid = guid();
		if (param.templateValue == null) {
			html += `
			<tr name="param" id=${uuid}>
				<td>
					<textarea  name="name" class="layui-textarea">${param.name}</textarea>
				</td>
				<td  style="width: 50%;">
					<textarea  name="value" class="layui-textarea">${param.value}</textarea>
				</td>
				<td>
					<button type="button" class="layui-btn layui-btn-sm layui-btn-danger" onclick="delTr('${uuid}')">${commonStr.del}</button>
					
					<button class="layui-btn layui-btn-normal layui-btn-sm" onclick="setParamOrder('${uuid}', -1)">${commonStr.up}</button>
					<button class="layui-btn layui-btn-normal layui-btn-sm" onclick="setParamOrder('${uuid}', 1)">${commonStr.down}</button>
				</td>
			</tr>
			`;
		} else {
			html += buildTemplateParam(uuid,param);
		}
	}

	$("#paramList").html(html);

	paramIndex = layer.open({
		type: 1,
		title: serverStr.extParm,
		area: ['800px', '600px'], // 宽高
		content: $('#paramJsonDiv')
	});
}

function addParam() {
	var uuid = guid();

	var html = `
	<tr name="param" id="${uuid}">
		<td>
			<textarea  name="name" class="layui-textarea"></textarea>
		</td>
		<td style="width: 50%;">
			<textarea  name="value" class="layui-textarea"></textarea>
		</td>
		<td>
			<button type="button" class="layui-btn layui-btn-sm layui-btn-danger" onclick="delTr('${uuid}')">${commonStr.del}</button>
			
			<button class="layui-btn layui-btn-normal layui-btn-sm" onclick="setParamOrder('${uuid}', -1)">${commonStr.up}</button>
			<button class="layui-btn layui-btn-normal layui-btn-sm" onclick="setParamOrder('${uuid}', 1)">${commonStr.down}</button>
		</td>
	</tr>
	`;

	$("#paramList").append(html);

}


function addParamOver() {

	var targertId = $("#targertId").val();
	var params = [];
	$("tr[name='param']").each(function() {
		var param = {};
		if ($(this).find("input[name='templateValue']").val() == null) {
			param.name = $(this).find("textarea[name='name']").val();
			param.value = $(this).find("textarea[name='value']").val();
		} else {
			param.templateValue = $(this).find("input[name='templateValue']").val();
			param.templateName = $(this).find("input[name='templateName']").val();
		}
		params.push(param);
	})
	$("#" + targertId).val(JSON.stringify(params));

	layer.close(paramIndex);
}


function sort(id) {
	$("#sort").val(id.replace("Sort", ""))
	if ($("#" + id).attr("class").indexOf("blue") > -1) {
		if ($("#direction").val() == 'asc') {
			$("#direction").val("desc")
		} else {
			$("#direction").val("asc")
		}
	} else {
		$("#direction").val("asc")
	}

	search();
}


var wwwIndex;
var uuid;
function selectWww(id) {
	uuid = id;
	rootSelect.selectOne(function callBack(val) {
		$("#rootPath_" + uuid).val(val);
	});
}


function clone(id) {
	if (confirm(serverStr.confirmClone)) {
		edit(id, true);
	}
}


function importServer() {
	var formData = new FormData();
	formData.append("nginxPath", $("#nginxPath").val());
	$.ajax({
		type: 'POST',
		url: ctx + '/adminPage/server/importServer',
		data: formData,
		dataType: 'json',
		processData: false,
		contentType: false,
		success: function(data) {
			if (data.success) {
				location.reload();
			} else {
				layer.msg(data.msg);
			}
		},
		error: function() {
			layer.alert(commonStr.errorInfo);
		}
	});
}

var importIndex;
function openImport() {
	importIndex = layer.open({
		type: 1,
		title: serverStr.importServer,
		area: ['500px', '300px'], // 宽高
		content: $('#importDiv')
	});
}
// 选择系统文件
function selectRootCustom(inputId) {
	rootSelect.selectOne(function callBack(val) {
		$("#" + inputId).val(val);
	});
}

function testPort() {
	if (confirm(serverStr.testAllPort)) {
		layer.load();
		$.ajax({
			type: 'POST',
			url: ctx + '/adminPage/server/testPort',
			dataType: 'json',
			processData: false,
			contentType: false,
			success: function(data) {
				layer.closeAll();
				if (data.success) {
					layer.msg(serverStr.noPortUsed);
				} else {
					layer.alert(data.msg);
				}
			},
			error: function() {
				layer.closeAll();
				layer.alert(commonStr.errorInfo);
			}
		});
	}

}

function editDescr(id, descr) {
	$("#serverId").val(id);
	$("#descr").val(descr);
	layer.open({
		type: 1,
		title: serverStr.descr,
		area: ['500px', '300px'], // 宽高
		content: $('#descrDiv')
	});

}

function editDescrOver(){
	$.ajax({
		type: 'POST',
		url: ctx + '/adminPage/server/editDescr',
		data: {
			id: $("#serverId").val(),
			descr : $("#descr").val()
		},
		dataType: 'json',
		success: function(data) {
			if (data.success) {
				location.reload();
			} else {
				layer.msg(data.msg)
			}
		},
		error: function() {
			layer.alert(commonStr.errorInfo);
		}
	});
}
