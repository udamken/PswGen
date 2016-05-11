var pgcApp = angular.module('pgcApp', []);

pgcApp.factory('options', function() {
    return function(callback) {
        chrome.storage.local.get({
            server : 'http://localhost:9000',
            token : null
        }, callback);
    };
});
