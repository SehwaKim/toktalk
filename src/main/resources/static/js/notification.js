document.addEventListener('DOMContentLoaded', function () {
    Notification.requestPermission();
});

function notificationf() {
    if (!Notification) {
        alert('Desctop not supported');
        return;
    }
    if (Notification.permission !== "granted")
        Notification.requestPermission();
    else {
        var notification = new Notification('입장', {
            icon: 'icon.png',
            body: '판다님 입장'
        });

    }
}