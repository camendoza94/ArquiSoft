/**
 * Created by ca.mendoza968 on 25/10/2016.
 * Based on https://github.com/angyjoe/eventual
 */
(function (ng) {

    var mod = ng.module("pozoModule");

// the list controller
    mod.controller("pozoListCtrl", ["$scope", "$resource", "apiUrl", function($scope, $resource, apiUrl) {
        var Pozos = $resource(apiUrl + "/pozos"); // a RESTful-capable resource object
        $scope.pozos = Pozos.query(); // for the list of pozos in public/html/main.html
    }]);

// the create controller
    mod.controller("pozoCreateCtrl", ["$scope", "$resource", "$timeout", "apiUrl", "$routeParams", function($scope, $resource, $timeout, apiUrl, $routeParams) {
        // to save a pozo
        $scope.save = function() {
            var CreatePozo = $resource(apiUrl + "/campos/" + $routeParams.id +"/pozos"); // a RESTful-capable resource object
            CreatePozo.save($scope.pozo); // $scope.pozo comes from the detailForm in public/html/detail.html
            $timeout(function() { $scope.go('/pozo'); }); // go back to public/html/main.html
        };
    }]);

// the edit controller
    mod.controller("pozoEditCtrl", ["$scope", "$resource", "$routeParams", "$timeout", "apiUrl", function($scope, $resource, $routeParams, $timeout, apiUrl) {
        var ShowPozo = $resource(apiUrl +"/pozos/:id", {id:"@id"}); // a RESTful-capable resource object
        if ($routeParams.id) {
            // retrieve the corresponding celebrity from the database
            // $scope.pozo.id is now populated so the Delete button will appear in the detailForm in public/html/detail.html
            $scope.pozo = ShowPozo.get({id: $routeParams.id});
            $scope.dbContent = ShowPozo.get({id: $routeParams.id}); // this is used in the noChange function
        }

        // decide whether to enable or not the button Save in the detailForm in public/html/detail.html
        $scope.noChange = function() {
            return angular.equals($scope.pozo, $scope.dbContent);
        };

        // to update a pozo
        $scope.save = function() {
            var UpdatePozo = $resource(apiUrl +"/pozos/" + $routeParams.id,null,{update:{method:'PUT'}}); // a RESTful-capable resource object
            $scope.pozoSinSensores = {
                "id":$scope.pozo.id,
                "estado":$scope.pozo.estado
            };
            UpdatePozo.update($scope.pozoSinSensores); // $scope.celebrity comes from the detailForm in public/html/detail.html
            $timeout(function() { $scope.go('/pozo'); }); // go back to public/html/main.html
        };

        // to delete a pozo
        $scope.delete = function() {
            var DeletePozo = $resource( apiUrl +"/pozos/" + $routeParams.id); // a RESTful-capable resource object
            DeletePozo.delete();
            $timeout(function() { $scope.go('/pozo'); }); // go back to public/html/main.html
        };
    }]);

})(window.angular)