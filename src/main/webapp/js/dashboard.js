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
			'Catalina:name="http-bio-8080",type=ThreadPool',
			'Catalina:type=GlobalRequestProcessor,name="http-bio-8080"',
			'Catalina:type=Manager,context=/spring-pitfalls,host=localhost',
			'org.hibernate:type=Statistics,application=spring-pitfalls',
			"net.sf.ehcache:type=CacheStatistics,CacheManager=spring-pitfalls,name=org.hibernate.cache.StandardQueryCache",
			"net.sf.ehcache:type=CacheStatistics,CacheManager=spring-pitfalls,name=org.hibernate.cache.UpdateTimestampsCache",
			"quartz:type=QuartzScheduler,name=schedulerFactory,instance=NON_CLUSTERED",
			'org.apache.activemq:BrokerName=localhost,Type=Queue,Destination=requests',
			"com.blogspot.nurkiewicz.spring:name=dataSource,type=ManagedBasicDataSource"
		];
		return _.map(mbeans, function(mbean) {
			return {
				type:'read',
				mbean: mbean
			}
		});
	}

	function jmxAttributes(response) {
		var jmxMap = {};
		_.each(response, function(mbean) {
			if(mbean.status == 200) {
				jmxMap[mbean.request.mbean] = mbean.value;
			} else {
				throw new Error(mbean.error)
			}
		});
		return jmxMap;
	}

	function buildTree(model) {
		$("#jmxTree").jstree({
			"json_data" : { "data" : model},
			"core" : { "animation" : 250 },
			"plugins" : ["json_data", "ui", "themeroller"]
		}).bind("select_node.jstree", function (e, data) {
					data.inst.toggle_node(data.rslt.obj);
					console.info(data.rslt.obj.data('metric'));
				});
	}

	$.ajax({
		url: 'jmx?ignoreErrors=true',
		type: "POST",
		dataType: "json",
		data: JSON.stringify(request()),
		contentType: "application/json",
		success: function(response) {
			var model = buildTreeModel(jmxAttributes(response));
			var jsTree = treeModelToJsTree(model);
			buildTree(jsTree);
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
					if(value > attention) { return 1.0; }
					if(value > warning) { return 0.5; }
					if(value > error) { return 0.0; } else { return -1.0; }
				}
			}
			if(attention < warning && warning < error) {
				return function(value) {
					if(value < attention) { return 1.0; }
					if(value < warning) { return 0.5; }
					if(value < error) { return 0.0; } else { return -1.0; }
				}
			}
			throw new Error("All thresholds should either be increasing or decreasing: " + attention + ", " + warning + ", " + error);
		};
	Node.relativeThreshold = function(attention, warning, error, max) {
		return Node.threshold(attention * max, warning * max, error * max);
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
		console.info(jmx);
		return new CompositeNode('Overall', [
			new CompositeNode('Servlet container', [
				new Node(
						'Active HTTP sessions',
						jmx['Catalina:context=/spring-pitfalls,host=localhost,type=Manager'].activeSessions,
						Node.threshold(200, 300, 500)
				),
				new Node(
						'HTTP sessions create rate',
						jmx['Catalina:context=/spring-pitfalls,host=localhost,type=Manager'].sessionCreateRate,
						Node.threshold(5, 10, 50)
				),
				new Node(
						'Rejected HTTP sessions',
						jmx['Catalina:context=/spring-pitfalls,host=localhost,type=Manager'].rejectedSessions,
						Node.threshold(1, 5, 10)
				),
				new Node(
						'Busy worker threads count',
						jmx['Catalina:name="http-bio-8080",type=ThreadPool'].currentThreadsBusy,
						Node.relativeThreshold(0.85, 0.9, 0.95, jmx['Catalina:name="http-bio-8080",type=ThreadPool'].maxThreads)
				),
			]),
			new CompositeNode('JVM', [
				new Node(
						'Code cache memory usage',
						jmx['java.lang:name=Code Cache,type=MemoryPool'].Usage.used,
						Node.relativeThreshold(0.75, 0.85, 0.95, jmx['java.lang:name=Code Cache,type=MemoryPool'].Usage.max)
				),
				new Node(
						'Perm Gen memory usage',
						jmx['java.lang:name=PS Perm Gen,type=MemoryPool'].Usage.used,
						Node.relativeThreshold(0.75, 0.85, 0.95, jmx['java.lang:name=PS Perm Gen,type=MemoryPool'].Usage.max)
				),
				new Node(
						'Heap memory usage',
						jmx['java.lang:type=Memory'].HeapMemoryUsage.used,
						Node.relativeThreshold(0.75, 0.85, 0.95, jmx['java.lang:type=Memory'].HeapMemoryUsage.max)
				),
				new Node(
						'Non-heap memory usage',
						jmx['java.lang:type=Memory'].NonHeapMemoryUsage.used,
						Node.relativeThreshold(0.75, 0.85, 0.95, jmx['java.lang:type=Memory'].NonHeapMemoryUsage.max)
				),
				new Node(
						'Total active threads',
						jmx['java.lang:type=Threading'].ThreadCount,
						Node.threshold(50, 75, 100)
				)
			]),
			new CompositeNode('Operating system', [
				new Node(
						'Free physical memory',
						jmx['java.lang:type=OperatingSystem'].FreePhysicalMemorySize,
						Node.relativeThreshold(0.75, 0.85, 0.95, jmx['java.lang:type=OperatingSystem'].TotalPhysicalMemorySize)
				),
				new Node(
						'Free swap space',
						jmx['java.lang:type=OperatingSystem'].FreeSwapSpaceSize,
						Node.relativeThreshold(0.75, 0.85, 0.95, jmx['java.lang:type=OperatingSystem'].TotalSwapSpaceSize)
				),
				new Node(
						'Average system load (%)',
						jmx['java.lang:type=OperatingSystem'].SystemLoadAverage,
						Node.threshold(0.75, 0.85, 0.95)
				),
				new Node(
						'Open file descriptors',
						jmx['java.lang:type=OperatingSystem'].OpenFileDescriptorCount,
						Node.threshold(100, 150, 200)
				)
			]),
			new CompositeNode('External systems', [
				new CompositeNode('Persistence', [
					new Node(
							'Active database connections',
							jmx['com.blogspot.nurkiewicz.spring:name=dataSource,type=ManagedBasicDataSource'].NumActive,
							Node.relativeThreshold(0.75, 0.85, 0.95, jmx['com.blogspot.nurkiewicz.spring:name=dataSource,type=ManagedBasicDataSource'].MaxActive)
					),
				]),
				new CompositeNode('JMS messaging broker', [
					new Node(
							'Waiting in "requests" queue',
							jmx['org.apache.activemq:BrokerName=localhost,Destination=requests,Type=Queue'].QueueSize,
							Node.threshold(2, 5, 10)
					),
					new Node(
							'Number of consumers',
							jmx['org.apache.activemq:BrokerName=localhost,Destination=requests,Type=Queue'].ConsumerCount,
							Node.threshold(0.2, 0.1, 0)
					)
				]),
			])
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