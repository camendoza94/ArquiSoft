(function(ng)
{
	var mod = ng.module("app",[

		"ngResource",
		"ngRoute",
		"regionModule",
		"campoModule",
		"pozoModule",
		"sensorModule"

	]);

	mod.constant("apiUrl","/api");

	mod.config(["$routeProvider", function($routeProvider) {
		return $routeProvider.when("/region", {
			templateUrl: "/views/region/main",
			controller: "regionListCtrl"
		}).when("/region/create", {
			templateUrl: "/views/region/detail",
			controller: "regionCreateCtrl"
		}).when("/region/edit/:id", {
			templateUrl: "/views/region/detail",
			controller: "regionEditCtrl"
		}).when("/campo", {
			templateUrl: "/views/campo/main",
			controller: "campoListCtrl"
		}).when("/region/:id/campo/create", {
			templateUrl: "/views/campo/detail",
			controller: "campoCreateCtrl"
		}).when("/campo/edit/:id", {
			templateUrl: "/views/campo/detail",
			controller: "campoEditCtrl"
		}).when("/pozo", {
			templateUrl: "/views/pozo/main",
			controller: "pozoListCtrl"
		}).when("/campo/:id/pozo/create", {
			templateUrl: "/views/pozo/detail",
			controller: "pozoCreateCtrl"
		}).when("/pozo/edit/:id", {
			templateUrl: "/views/pozo/detail",
			controller: "pozoEditCtrl"
		}).when("/sensor", {
			templateUrl: "/views/sensor/main",
			controller: "sensorListCtrl"
		}).when("/inicio",
			{templateUrl:"/views/inicio"
		}).otherwise({
			redirectTo: "/inicio"
		});
	}
	]).config([
		"$locationProvider", function($locationProvider) {
			return $locationProvider.html5Mode({
				enabled: true,
				requireBase: false
			}).hashPrefix("!"); // enable the new HTML5 routing and history API
			// return $locationProvider.html5Mode(true).hashPrefix("!"); // enable the new HTML5 routing and history API
		}]);

	// the global controller
	mod.controller("AppCtrl", ["$scope", "$location", function($scope, $location) {
		// the very sweet go function is inherited by all other controllers
		$scope.go = function (path) {
			$location.path(path);
		};
	}]);
})(window.angular);




