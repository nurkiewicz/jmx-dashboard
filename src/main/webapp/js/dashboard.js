$(function() {
    var jolokia = new Jolokia("jmx");
    var response = jolokia.request(
            [
                {
                    type: "read",
                    mbean: "java.lang:type=Memory"
                },
                {
                    type: "read",
                    mbean: "java.lang:type=Threading"
                },
                {
                    type: "read",
                    mbean: "java.lang:type=OperatingSystem"
                },
                {
                    type: "read",
                    mbean: "java.lang:type=Runtime"
                }
            ],
            {
            success: function(response) {
                $('#stats').
                        append($('<h3/>').text(response.request.mbean)).
                        append($('<pre/>').append(JSON.stringify(response.value, null, '\t')))
                        ;
            }
            }
    );

	$("#jmxTree").jstree({
		"json_data" : {
			"data" : [
				{
					"data" : {title: "A node", icon: 'img/accept.png'},
					"metadata" : { id : 23 },
					"children" : [
						{
							"data" : {title: "B node", icon: 'img/magnifier.png'},
							"children" : [ "Child 1", "A Child 2" ]
						},
						{
							"data" : {title: "C node", icon: 'img/error.png'},
							"children" : [ "Child 1", "A Child 2" ]
						},
						{
							"data" : {title: "D node", icon: 'img/alarm_bell.png'},
							"children" : [ "Child 1", "A Child 2" ]
						}

					]
				},
				{
					"attr" : { "id" : "li.node.id1" },
					"data" : {
						"title" : "Long format demo",
						"attr" : { "href" : "#" }
					}
				}
			]
		},
		"core" : { "animation" : 250 },
		"plugins" : ["json_data", "ui", "themeroller"]
	}).bind("select_node.jstree", function (e, data) {
				data.inst.toggle_node(data.rslt.obj);
			});

});