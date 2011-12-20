$(function() {
	var mbeans = [
		{name: "java.lang:type=Memory"},
		{name: "com.blogspot.nurkiewicz.spring:name=dataSource,type=ManagedBasicDataSource"},
		{name: "java.lang:type=OperatingSystem"},
		{name: "java.lang:type=Runtime"},
		{name: "quartz:type=QuartzScheduler,name=schedulerFactory,instance=NON_CLUSTERED"},
		{name: "java.lang:type=Threading"},
		{name: 'Catalina:name="http-bio-8080",type=ThreadPool', attribute: 'currentThreadsBusy'},
		{name: 'Catalina:type=GlobalRequestProcessor,name="http-bio-8080"'},
		{name: 'org.hibernate:type=Statistics,application=spring-pitfalls'}
	];

	var request = _.map(mbeans, function(mbean) {
		return {
			type:'read',
			mbean: mbean.name,
			attribute: mbean.attribute
		}
	});
	$.ajax({
		url: 'jmx',
		type: "POST",
		dataType: "json",
		data: JSON.stringify(request),
		contentType: "application/json",
		success: function(response) {
			var resp = {};
			_.each(response, function(mbean) {
				resp[mbean.request.mbean] = mbean.value;
			});
			var model = buildTreeModel(resp);
			var jsTree = _.map(model, treeModelToJsTree);


			$("#jmxTree").jstree({
				"json_data" : { "data" : jsTree },
				"core" : { "animation" : 250 },
				"plugins" : ["json_data", "ui", "themeroller"]
			}).bind("select_node.jstree", function (e, data) {
						data.inst.toggle_node(data.rslt.obj);
					});
		}
	});

	function buildTreeModel(response) {
		function threshold(attention, warning, error) {
			if(attention > warning && warning > error) {
				return function(value) {
					if(value >= attention) { return 1.0; }
					if(value >= warning) { return 0.5; }
					if(value >= error) { return 0.0; } else { return -1.0; }
				}
			}
			if(attention < warning && warning < error) {
				return function(value) {
					if(value <= attention) { return 1.0; }
					if(value <= warning) { return 0.5; }
					if(value <= error) { return 0.0; } else { return -1.0; }
				}
			}
			throw new Error("All thresholds should either be increasing or decreasing");
		}
		return [
			{
				label: 'General',
				children: [
					{
						label: 'Operating system',
						children: [
							{
								label: 'Open file descriptor count',
								value: Math.random() * 25,
								evaluator: threshold(10, 18, 22)
							},
							{
								label: 'System load average',
								value: Math.random() * 25,
								evaluator: threshold(10, 18, 22)
							},
							{
								label: 'Memory',
								children: [
									{
										label: 'Free swap space size',
										value: Math.random() * 25,
										evaluator: threshold(10, 18, 22)
									},
									{
										label: 'Free physical memory size',
										value: Math.random() * 25,
										evaluator: threshold(10, 18, 22)
									}
								]
							}
						]
					},
					{
						label: 'Persistence',
						value: Math.random() * 25,
						evaluator: threshold(10, 18, 22)
					},
					{
						label: 'Web',
						value: Math.random() * 25,
						evaluator: threshold(10, 18, 22)
					}
				]
			}
		];
	}

	function treeModelToJsTree(model) {
		var data = {title: model.label + ' (' + model.value + ')', icon: 'img/accept.png'};
		if (model.children) {
			return {data: data, children: _.map(model.children, treeModelToJsTree)}
		} else {
			return {data: data};
		}
	}

});