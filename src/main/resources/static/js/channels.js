var sock = null;

$(document).ready(function () {
    sock = new SockJS('/sock');

    sock.onopen = function () {
        console.log("connected");
    };

    sock.onmessage = function (e) {
        var data = JSON.parse(e.data);
        var channelId = data.channelId;
        if($("#"+channelId).css('display') != 'none'){
            showMessage(data);
        }
    };

    sock.onclose = function () {
        console.log('disconnected');
    };

    $("#chatInput_1").keypress(function(e) {

        if (e.keyCode == 13){
            sendMsg(1);

        }
    });

    $("#chatInput_2").keypress(function(e) {

        if (e.keyCode == 13){
            sendMsg(2);

        }
    });

    $("#chatInput_3").keypress(function(e) {

        if (e.keyCode == 13){
            sendMsg(3);

        }
    });

    $("#chatInput_4").keypress(function(e) {

        if (e.keyCode == 13){
            sendMsg(4);

        }
    });
});

function enter(channelId) {
    if($("#"+channelId).css('display') == 'none'){
        $.ajax({
            url: '/api/channels/'+channelId,
            method: 'get',
            dataType: "json",
            contentType: "application/json",
            success: function (data) {
                if(data != null){
                    for(var i=0;i<data.length;i++){
                        showMessage(data[i]);
                    }
                }
            }
        });

        $("#"+channelId).show();
    }
}

function sendMsg(channelId) {
    sock.send(JSON.stringify({'channelId' : channelId, 'text' : $("#chatInput_"+channelId).val()}));

    /*var JSONObject= {
        "channelId" : channelId,
        "text" : $("#chatInput_"+channelId).val()
    };
    var jsonData = JSON.stringify( JSONObject );

    $.ajax({
        url: '/api/channels/'+channelId+'/messages',
        method: 'post',
        data: jsonData,
        dataType: "json",
        contentType: "application/json",
        success: function (data) {
            // showMessage(data);
        }
    });*/

    $("#chatInput_"+channelId).val("");
}

function disconnect() {
    sock.close();
}

function showMessage(data) {
    $('#messages_'+data.channelId).append("[" + data.nickname + "] " + data.text + '\n');

    var textArea = $('#messages_'+data.channelId);
    textArea.scrollTop( textArea[0].scrollHeight - textArea.height() );
}