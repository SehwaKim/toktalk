
window.onload = function () {

    //google버튼 클릭시.
    $("#google").on("click", function () {
        $.ajax({
            url: '/users/check-status',
            type: 'POST',
            success: function (data) {
                if ($("#google").hasClass("btn-google")) {
                    //연결되어 있지않은 상태.
                    showConfirmBox("소셜 연결을 사용하시겠습니까?", "/login/google");

                } else if ($("#google").hasClass("btn-outline-google")) {
                    //연결되어 있지 않은 상태

                    if (data == "oauth") {
                        showConfirmBox("소셜 연결을 해지 하시려면 패스워드 변경을 먼저 하셔야 합니다. 진행 하시겠습니까?", "/identity/password/forgot");

                    } else {
                        showConfirmBox("소셜 연결을 해지 하시겠습니까?", "/users/disconnect");
                    }
                }
            }
        });
    });

    //google 버튼 레이아웃 결정.
    $.ajax({
        url: '/users/check-oauth',
        type: 'POST',
        success: function (isEmptyOauth) {

            if (isEmptyOauth == false) {
                $('#google').attr("value", "연결끊기");
                $("#google").removeClass("btn-google");
                $("#google").addClass("btn-outline-google");
            } else if (isEmptyOauth == true) {
                $('#google').attr("value", "연결하기");
                $("#google").addClass("btn-google");
                $("#google").removeClass("btn-outline-google");
            }
        }
    });

}


function progressBar() {
    var dialog;
    //프로그래스바 진행
    $.ajax({
        url: '/identity/password/sent',
        type: 'GET',
        beforeSend: function () {
            dialog = bootbox.dialog({
                message: '<p class="text-center">이메일을 보내는 중입니다. 잠시만 기다려 주세요.<br><i class="fa fa-spin fa-spinner"/></p>',
                closeButton: false
            });
        },
        success: function () {
            dialog.modal('hide');
        }
    });

}

function showConfirmBox(message, path) {
    bootbox.confirm({
        message: message,
        buttons: {
            confirm: {
                label: '확인',
                className: 'alert-no'
            },
            cancel: {
                label: '취소',
                className: 'alert-yes'
            }
        },
        callback: function (result) {
            location.href = path;
        }
    });
}