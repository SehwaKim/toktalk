var sock = null;
var current = 0;
var in5Sec = false;
var timer1;
var timer2;

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
                $("#typingAlarm").text('');
                clearTimeout(timer1);
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
        }else if('system' == data.type){
            showMessage(data);
        }else if('typing' == data.type){
            $("#typingAlarm").text(data.text);
            timer1 = setTimeout(function () {
                $("#typingAlarm").text('');
            }, 6000);
        }else if('channel_mark' == data.type){
            markAsRead(data.channelId);
        }else if('upload_file' == data.type){
            showMessage(data);
        }
    };

    sock.onclose = function () {
        console.log('disconnected');
    };

    sock.onheartbeat = function (e) {
        sock.send(JSON.stringify({'type' : 'pong'}));
    }

    $("#chatInput").keypress(function(e) {
        if (e.keyCode == 13){
            sendMsg(current);
        }
    });


    $("#chatInput").keydown(function(e) {
        typingAlarm();
    });
});

function typingAlarm() {
    // if($("#chatInput").val().length > 0){
        if(!in5Sec){
            sock.send(JSON.stringify({'type' : 'typing'}));
            in5Sec = true;
            timer2 = setTimeout(function () {
                in5Sec = false;
            }, 5000);
        }
    // }
}

function switchChannel(channelId) {
    if(channelId == current){
        return false;
    }
    $.ajax({
        url: '/api/channels/'+channelId,
        method: 'get',
        dataType: "json",
        contentType: "application/json",
        success: function (data) {
            if(data){
                if (!window.confirm("채널에 입장하시겠습니까?")) {
                    return false;
                }
            }
            $('#'+current).removeClass('active');
            current = channelId;

            $('#msgArea').remove();
            $('<textarea></textarea>')
                .attr('id', 'msgArea')
                .attr('rows', '23')
                .attr('readonly', 'true')
                .appendTo($('#msgDiv'));

            sock.send(JSON.stringify({'type' : 'switch', 'channelId' : channelId}));
            //active
            $('#'+channelId).addClass('active');
            $("#btn-exit").attr('disabled', false);
            var channelName = $('#'+channelId).find('.name').text();
            var input_box = document.getElementById("chatInput");
            var placeholder = "Message to "+channelName;
            input_box.placeholder = placeholder;
        }
    });
}

function sendMsg(channelId) {
    if(channelId == 0){
        return false;
    }
    sock.send(JSON.stringify({'type' : 'chat', 'channelId' : channelId, 'text' : $("#chatInput").val()}));
    $("#chatInput").val("");
    clearTimeout(timer2);
    in5Sec = false;
}

function disconnect() {
    sock.close();
}

function showMessage(data) {
    if(current != data.channelId){
        return false;
    }
    if('system' == data.type){
        $('#msgArea').append(data.text + '\n');
    }else if('upload_file' == data.type){
        $('#msgArea').append("[" + data.nickname + "] "+"fileupload"+" (download)" + '\n');
    }else {
        $('#msgArea').append("[" + data.nickname + "] " + data.text + '\n');
    }
    var textArea = $('#msgArea');
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
            // freshChannelList(data);
            addNewChannel(data);
            $('.pop-layer').hide();
            $('#name').val("");
            $('#purpose').val("");
            $('#invite').val("");
            // var lastIdx = data.length-1;
            switchChannel(data.id);
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
                    .addClass('btn btn-dark btn-block channel').appendTo($div);
        $('<span></span>').text(data[key].name).appendTo($a);
        $('<span></span>').attr('style', 'badge badge-pill badge-light unread').appendTo($a);
    }
}

function addNewChannel(data) {
    var $div = $('<div></div>').appendTo($("#channelList"));
    var $a = $('<a></a>')
        .attr('href', 'javascript:void(0);')
        .attr('id', data.id)
        .attr('onclick', 'switchChannel(this.id)')
        .attr('style', 'text-align:left;')
        .addClass('btn btn-dark btn-block channel').appendTo($div);
    $('<span></span>').text(data.name).appendTo($a);
    $('<span></span>').attr('style', 'badge badge-pill badge-light unread').appendTo($a);

    var input_box = document.getElementById("chatInput");
    var placeholder = "Message To "+data.name;
    input_box.placeholder = placeholder;
}

function markAsUnread(data) {
    var arr = data.unreadMessages;
    for(var key in arr){
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

window.onload = function() {
    $.ajax({
        url: '/users/check-oauth',
        type: 'POST',
        success: function (data) {
            console.log('data :' + data);
            if (data == 'not_empty') {
                $('#google').prop("disabled", true);
            }
        }
    });
}
function exitChannel() {
    if(current == 0){
        return false;
    }
    if (!window.confirm("현재 채널에서 나가시겠습니까?")) {
        return false;
    }
    sock.send(JSON.stringify({'type' : 'exit_channel', 'channelId' : current}));
    $('#'+current).remove();

    $('#msgArea').remove();
    $('<textarea></textarea>')
        .attr('id', 'msgArea')
        .attr('rows', '23')
        .attr('readonly', 'true')
        .appendTo($('#msgDiv'));

    $("#btn-exit").attr('disabled', true);
    current = 0;
    $('#chatInput').attr('placeholder', '');
}