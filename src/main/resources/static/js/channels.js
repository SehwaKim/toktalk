var sock = null;
var current = 0;

$(document).ready(function () {
    sock = new SockJS('/sock');

    sock.onopen = function () {
        console.log("connected");
    };

    sock.onmessage = function (e) {
        var data = JSON.parse(e.data);

        if('chat' == data.type) {
            if (data.notification) {
                notifyUnread(data);
            } else {
                showMessage(data);
            }
        }else if('messageList' == data.type){
            if(data.messages != null){
                for(var i=0;i<data.messages.length;i++){
                    markAsRead(data.channelId);
                    showMessage(data.messages[i]);
                }
            }
        }else if('unread' == data.type){
            markAsUnread(data);
        }
    };

    sock.onclose = function () {
        console.log('disconnected');
    };

    sock.onheartbeat = function (e) {
        sock.send(JSON.stringify({'type' : 'pong'}));
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

function switchChannel(element) {
    var channelId = element.id;
    if(channelId == current){
        return false;
    }
    current = channelId;
    sock.send(JSON.stringify({'type' : 'switch', 'channelId' : channelId}));
}

function sendMsg(channelId) {
    sock.send(JSON.stringify({'type' : 'chat', 'channelId' : channelId, 'text' : $("#chatInput_"+channelId).val()}));
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
        var $a = $('<a></a>')
                    .attr('href', 'javascript:void(0);')
                    .attr('id', data[key].id)
                    .attr('onclick', 'switchChannel(this)')
                    .attr('style', 'text-align:left;')
                    .addClass('btn btn-light btn-block').appendTo($div);
        $('<span></span>').text(data[key].name).appendTo($a);
        $('<span></span>').attr('style', 'badge badge-pill badge-danger unread').appendTo($a);
    }
}

function markAsUnread(data) {
    var arr = data.unreadMessages;
    for(key in arr){
        $('#'+arr[key].channelId).find('.unread').text(arr[key].unreadCnt);
    }
}

function markAsRead(channelId) {
    $('#'+channelId).find('.unread').text('');
}

function notifyUnread(data) {
    var cnt = $('#'+data.channelId).find('.unread').text();
    if(typeof cnt === "undefined" || cnt == ''){
        console.log(cnt);
        cnt = 0;
    }
    $('#'+data.channelId).find('.unread').text(++cnt);
}