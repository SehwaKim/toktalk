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
                notifyUnread(data.channelId);
                notificationf(data.channelId);
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
        }else if('channel_joined' == data.type){
            addNewChannel(data.channel);
            notifyUnread(data);
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

function notificationf(channelId) {
    var channelName = $('#'+channelId).find('.name').text();
    var cnt = $('#'+channelId).find('.unread').text();
    if (!Notification) {
        alert('Desctop not supported');
        return;
    }
    if (Notification.permission !== "granted")
        Notification.requestPermission();
    else {
        var notification = new Notification(channelName+" 방으로부터", {
            icon: 'icon.png',
            body: cnt+'개의 새로운 메시지가 있습니다.'
        });

    }
}

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
            $("#btn-invite").attr('disabled', false);
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
            inviteMember(data.id, $("#invitedId").val()); // 멤버 초대하기 - 지금은 1명만 가능...
            addNewChannel(data);
            $('.pop-layer').hide();
            $('#name').val("");
            $('#purpose').val("");
            $('#invitedId').val("");
            switchChannel(data.id);
        }
    });
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
    var channelName = data.name.toString();
    var placeholder = "Message To "+channelName;
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

function notifyUnread(channelId) {
    var cnt = $('#'+channelId).find('.unread').text();
    if(typeof cnt === "undefined" || cnt == ''){
        console.log(cnt);
        cnt = 0;
    }
    $('#'+channelId).find('.unread').text(++cnt);
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
    $("#btn-invite").attr('disabled', true);
    current = 0;
    $('#chatInput').attr('placeholder', '');
}

function inviteMember() {
    var a = arguments; // 모든 함수는 생성되면서 arguments 라는 내장 매개변수를 얻게된다. 전달된 인자를 값으로 가짐. 자바스크립트는 메소드 오버로딩이 안되서 이렇게 함.

    switch (a.length) {
        case 0: // 이미 생성된 채널에서 초대
            if(current == 0){
                return false;
            }
            // 임시로 test3(판다)만 초대하기로 구현해놓음 (다수일경우 for문)
            // 유저 검색시 얻은 userId, nickname 을 넘겨주기
            sock.send(JSON.stringify({'type' : 'invite_member', 'channelId' : current, 'userId' : 3, 'nickname' : '판다'}));
            break;

        case 2: // 채널 최초 생성시 초대
            var channelId = a[0];
            var visitedId = a[1];
            if(visitedId != ""){
                sock.send(JSON.stringify({'type' : 'invite_member', 'channelId' : channelId, 'userId' : visitedId}));
            }
            break;
    }
}