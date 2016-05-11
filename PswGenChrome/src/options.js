angular.module('pgcApp').controller('optionsController', ['$scope', 'options', function($scope, options) {
    options(function(items) {
        $scope.server = items.server;
        $scope.token = items.token;

        $scope.$apply();
    });

    $scope.save = function() {
        chrome.storage.local.set({
            server : $scope.server,
            token : $scope.token
        }, function() {
            alert('Options saved!');
        });
    };
}]);
