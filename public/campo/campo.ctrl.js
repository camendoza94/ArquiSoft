/**
 * Created by d.althviz10 on 23/10/2016.
 * Based on https://github.com/angyjoe/eventual
 */
(function (ng) {

    var mod = ng.module("campoModule");

// the list controller
    mod.controller("campoListCtrl", ["$scope", "$resource", "apiUrl", function($scope, $resource, apiUrl) {
        var campos = $resource(apiUrl + "/campos"); // a RESTful-capable resource object
        $scope.campos = campos.query(); // for the list of campos in public/html/main.html
    }]);

// the create controller
    mod.controller("campoCreateCtrl", ["$scope", "$resource", "$timeout", "apiUrl", "$routeParams",function($scope, $resource, $timeout, apiUrl, $routeParams) {
        // to save a region
        $scope.save = function() {
            var CreateCampo = $resource(apiUrl +"/regiones/"+$routeParams.id+"/campos"); // a RESTful-capable resource object
            CreateCampo.save($scope.campo); // $scope.campo comes from the detailForm in public/html/detail.html
            $timeout(function() { $scope.go('/campo'); }); // go back to public/html/main.html
        };
    }]);

    //TODO agregar controlador para edición

})(window.angular)