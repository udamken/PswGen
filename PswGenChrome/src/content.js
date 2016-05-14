var data = {
    target : null
};

document.addEventListener('contextmenu', function(event) {
    data.target = event.target;
});
document.addEventListener('click', function(event) {
    if ((event.target.tagName || '').toLowerCase() === 'input') {
        data.target = event.target;
    }
});

chrome.extension.onMessage.addListener(function(msg) {
    if (msg.action !== 'set-password-or-loginInfo' || data.target === null) {
        return;
    }

    console.assert(typeof msg.password === 'string');
    console.assert(typeof msg.loginInfo === 'string');

    if (data.target.type === 'password') {
        data.target.value = msg.password;
    } else {
        data.target.value = msg.loginInfo;
    }
});
