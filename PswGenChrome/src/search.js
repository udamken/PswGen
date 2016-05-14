angular.module('pgcApp').controller('searchController', ['$scope', '$http', 'options', function($scope, $http, options) {
    var server;
    var token;

    options(function(items) {
        server = items.server;
        token = items.token;

        $http.get(server + '/api/service', {
            headers : {
                'X-SECURITY-TOKEN' : token
            }
        }).then(function(response) {
            $scope.services = response.data;
        }, function(response) {
            if (response.status === 401) {
                alert('Error - Invalid security token set!')
            } else if (response.status === 404) {
                alert('Error - Invalid URL specified!')
            } else {
                alert('Error ' + response.status);
            }
        });
    });

    $scope.loadService = function(serviceName) {
        if (!serviceName) {
            serviceName = $('#service-0').attr('data-service');
        }

        $http.get(server + '/api/service/' + encodeURIComponent(serviceName), {
            headers : {
                'X-SECURITY-TOKEN' : token
            }
        }).then(function(response) {
            chrome.tabs.getSelected(null, function(tab) {
                chrome.tabs.sendMessage(tab.id, {
                    action : 'set-password-or-loginInfo',
                    password : response.data.password,
                    loginInfo : response.data.loginInfo
                });
            });
            // Timeout to cause all messages to be executed before the pop-up gets closed.
            setTimeout(window.close, 0);
        }, function(response) {
            if (response.status === 401) {
                alert('Error - Invalid security token set!')
            } else if (response.status === 404) {
                alert('Error - Invalid URL specified!')
            } else {
                alert('Error ' + response.status);
            }
        });
    };
}]);
