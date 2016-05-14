var pgcApp = angular.module('pgcApp', []);

pgcApp.factory('options', function() {
    return function(callback) {
        chrome.storage.local.get({
            server : 'http://localhost:9000',
            token : null
        }, callback);
    };
});

pgcApp.directive('pgc-enter', function() {
	return function($scope, element, attributes) {
		console.log(element);
		element.bind('keydown keypress', function(event) {
			if ((event.which || element.keyCode) === 13) {
				$scope.$apply(function() {
					$scope.$eval(attributes.pgcEnter);
				});

				event.preventDefault();
			}
		});
	};
});
