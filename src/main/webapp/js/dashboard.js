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
});