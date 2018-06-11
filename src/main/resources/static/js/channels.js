var sock = null;

$(document).ready(function () {
    sock = new SockJS('/sock');

    sock.onopen = function () {
        console.log("connected");
    };

    sock.onmessage = function (e) {
        var data = JSON.parse(e.data);
        var channelId = data.channelId;
        showMessage(data);
        /*if($("#"+channelId).css('display') != 'none'){
            showMessage(data);
        }*/
    };

    sock.onclose = function () {
        console.log('disconnected');
    };

    sock.onheartbeat = function (e) {
        console.log(e);
        sock.send("h");
    }

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
    // if(!$("#"+channelId).css('display') == 'none'){
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

    // $("#"+channelId).show();
    // }
}

function sendMsg(channelId) {
    sock.send(JSON.stringify({'channelId' : channelId, 'text' : $("#chatInput_"+channelId).val()}));
    $("#chatInput_"+channelId).val("");
}

// function downFile(channelId) {
//     sock.send(JSON.stringify({'channelId' : channelId, 'text' : $("#chatInput_"+channelId).val()}));
//     $("#chatInput_"+channelId).val("");
// }

function disconnect() {
    sock.close();
}

function showMessage(data) {
    $('#messages_'+data.channelId).append("[" + data.nickname + "] " + data.text + '\n');
    var textArea = $('#messages_'+data.channelId);
    textArea.scrollTop( textArea[0].scrollHeight - textArea.height() );
}

function getFormData($form){
    var unindexed_array = $form.serializeArray();
    var indexed_array = {};

    $.map(unindexed_array, function(n, i){
        indexed_array[n['name']] = n['value'];
    });

    return indexed_array;
}

function createChannel() {
    var $form = $("#creatForm");
    var data = getFormData($form)
    var jsonData = JSON.stringify(data);

    $.ajax({
        url: '/api/channels',
        method: 'post',
        data: jsonData,
        dataType: "json",
        contentType: "application/json",
        success: function (data) {
            freshChannelList(data);
            $('.pop-layer').hide();
            $('#name').val("");
            $('#purpose').val("");
            $('#invite').val("");
        }
    });
}

function freshChannelList(data) {
    $("#channelList").empty();
    for(var key in data){
        var $div = $('<div></div>').appendTo($("#channelList"));
        $('<a></a>').attr('onclick', 'enter(1)').attr('value', data[key].name)
                                .addClass('btn btn-light btn-block').appendTo($div);
    }
}