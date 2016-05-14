chrome.extension.onMessage.addListener(function(msg) {
    if (msg.action !== 'set-password-or-loginInfo') {
        return;
    }

    console.assert(typeof msg.password === 'string');
    console.assert(typeof msg.loginInfo === 'string');

    var focused = document.activeElement;
    if ((focused.tagName || '').toLowerCase() === 'input') {
        if ((focused.type || '').toLowerCase() === 'password') {
            focused.value = msg.password;
        } else {
            focused.value = msg.loginInfo;
        }
    }
});
