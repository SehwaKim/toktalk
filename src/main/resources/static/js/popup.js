$(document).ready(function () {
    $("#btn-create").click(function () {
        $("#createChannel").show();
    });
    $('.btn-close').click(function () {
        $('.pop-layer').hide();
    })
    $("#name").keyup(function() {
        if($("#name").val().length > 0){
            $(".btn-start").attr('disabled', false);
        }else{
            $(".btn-start").attr('disabled', true);
        }
    });
});