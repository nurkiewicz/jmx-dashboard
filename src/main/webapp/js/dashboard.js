$(function() {

	function request() {
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
		return _.map(mbeans, function(mbean) {
			return {
				type:'read',
				mbean: mbean
			}
		});
	}

	function jmx(response) {
		var jmxMap = {};
		_.each(response, function(mbean) {
			jmxMap[mbean.request.mbean] = mbean.value;
		});
		return jmxMap;
	}

	$.ajax({
		url: 'jmx?ignoreErrors=true',
		type: "POST",
		dataType: "json",
		data: JSON.stringify(request),
		contentType: "application/json",
		success: function(response) {
			var model = buildTreeModel(response);
			var jsTree = treeModelToJsTree(model);

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


	function Node(label, metric, evaluatorFn) {
		this.label = label;
		this.metric = metric;
		if(evaluatorFn) {
			this.value = evaluatorFn(metric);
		}
	}

	Node.threshold = function(attention, warning, error) {
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
		};

	function CompositeNode(label, children) {
		Node.call(this, label, undefined, function() {
			var childMetrics = _.pluck(children, 'value');
			return _.min(childMetrics);
		});
		this.children = children;
	}

	CompositeNode.prototype = new Node;
	CompositeNode.prototype.constructor = CompositeNode;

	function buildTreeModel(jmx) {
		return new CompositeNode('General', [
			new CompositeNode('Operating system', [
				new Node('Open file descriptor count', Math.random() * 25, Node.threshold(10, 18, 22)),
				new Node('System load average', Math.random() * 25, Node.threshold(10, 18, 22)),
				new CompositeNode('Memory', [
					new Node('Free swap space size', Math.random() * 25, Node.threshold(10, 18, 22)),
					new Node('Free physical memory size', Math.random() * 25, Node.threshold(10, 18, 22))
				])
			]),
			new Node('Persistence', Math.random() * 25, Node.threshold(10, 18, 22)),
			new Node('Web', Math.random() * 25, Node.threshold(10, 18, 22))
		]);
	}

	function treeModelToJsTree(node) {
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

		var iconImg = nodeIcon(node.value);
		return {
			data: {
				title: node.label,
				icon: iconImg
			},
			metadata: {metric: node.metric},
			children: _.map(node.children, treeModelToJsTree)
		};
	}

});