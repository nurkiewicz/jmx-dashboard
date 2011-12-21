$(function() {
	var mbeans = [
		"java.lang:type=Memory",
		"java.lang:type=MemoryPool,name=Code Cache",
		"java.lang:type=MemoryPool,name=PS Eden Space",
		"java.lang:type=MemoryPool,name=PS Old Gen",
		"java.lang:type=MemoryPool,name=PS Perm Gen",
		"java.lang:type=MemoryPool,name=PS Survivor Space",
		"java.lang:type=OperatingSystem",
		"java.lang:type=Runtime",
		"java.lang:type=Threading",
		"com.blogspot.nurkiewicz.spring:name=dataSource,type=ManagedBasicDataSource",
		"quartz:type=QuartzScheduler,name=schedulerFactory,instance=NON_CLUSTERED",
		"net.sf.ehcache:type=CacheStatistics,CacheManager=spring-pitfalls,name=org.hibernate.cache.StandardQueryCache",
		"net.sf.ehcache:type=CacheStatistics,CacheManager=spring-pitfalls,name=org.hibernate.cache.UpdateTimestampsCache",
		'Catalina:name="http-bio-8080",type=ThreadPool',
		'Catalina:type=GlobalRequestProcessor,name="http-bio-8080"',
		'Catalina:type=Manager,context=/spring-pitfalls,host=localhost',
		'org.hibernate:type=Statistics,application=spring-pitfalls'
	];
	var request = _.map(mbeans, function(mbean) {
		return {
			type:'read',
			mbean: mbean
		}
	});
	$.ajax({
		url: 'jmx?ignoreErrors=true',
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
						console.info(data.rslt.obj.data('metric'));
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
								metric: Math.random() * 25,
								evaluator: threshold(10, 18, 22)
							},
							{
								label: 'System load average',
								metric: Math.random() * 25,
								evaluator: threshold(10, 18, 22)
							},
							{
								label: 'Memory',
								children: [
									{
										label: 'Free swap space size',
										metric: Math.random() * 25,
										evaluator: threshold(10, 18, 22)
									},
									{
										label: 'Free physical memory size',
										metric: Math.random() * 25,
										evaluator: threshold(10, 18, 22)
									}
								]
							}
						]
					},
					{
						label: 'Persistence',
						metric: Math.random() * 25,
						evaluator: threshold(10, 18, 22)
					},
					{
						label: 'Web',
						metric: Math.random() * 25,
						evaluator: threshold(10, 18, 22)
					}
				]
			}
		];
	}

	function evaluatedMetric(model) {
		if(model.evaluator) {
			return model.evaluator(model.metric)
		}
		return _(model.children).chain().
				map(evaluatedMetric).
				min().
				value();
	}

	function nodeIcon(evaluatedValue) {
		if(evaluatedValue >= 1) {
			return 'img/accept.png';
		}
		if(evaluatedValue >= 0.5) {
			return 'img/magnifier.png';
		}
		if(evaluatedValue >= 0.0) {
			return 'img/error.png';
		}
		return 'img/alarm_bell.png';
	}

	function treeModelToJsTree(model) {
		var iconImg = nodeIcon(evaluatedMetric(model));
		return {
			data: {
				title: model.label,
				icon: iconImg
			},
			metadata: {metric: model.metric},
			children: _.map(model.children, treeModelToJsTree)
		};
	}

});