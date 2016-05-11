angular.module('pgcApp').controller('searchController', ['$scope', '$http', 'options', function($scope, $http, options) {
    var server;
    var token;

    options(function(items) {
        server = items.server;
        token = items.token;

        $http.get(server + '/service-list', {
            headers : {
                'X-TOKEN' : token
            }
        }).then(function(response) {
            $scope.services = [];
            for (var i = 0; i < response.data.length; i++) {
                $scope.services.push({
                    name : response.data[i]
                });
            }
        }, function(response) {
            if (response.status === 403) {
                alert('Error - Invalid security token set!')
            } else if (response.status === 404) {
                alert('Error - Invalid URL specified!')
            } else {
                alert('Error');
            }
        });
    });

    $scope.loadService = function(serviceName) {
        $http.get(server + '/service?' + serviceName, {
            headers : {
                'X-TOKEN' : token
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
            if (response.status === 403) {
                alert('Error - Invalid security token set!')
            } else if (response.status === 404) {
                alert('Error - Invalid URL specified!')
            } else {
                alert('Error');
            }
        });
    };
}]);
