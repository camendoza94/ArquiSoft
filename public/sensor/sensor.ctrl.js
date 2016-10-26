/**
 * Created by d.althviz10 on 23/10/2016.
 * Based on https://github.com/angyjoe/eventual
 */
(function (ng) {

    var mod = ng.module("sensorModule");
    var tipoOr = ['Fluido', 'Energia', 'Temperatura', 'Emergencia'];
// the list controller
    mod.controller("sensorListCtrl", ["$scope", "$resource", "apiUrl", function($scope, $resource, apiUrl) {
        var sensores = $resource(apiUrl + "/sensores"); // a RESTful-capable resource object
        $scope.sensores = sensores.query(); // for the list of sensores in public/html/main.html
        $scope.tipos= tipoOr;
        //$scope.getMediciones = function(p){
          //  var mediciones = $resource(apiUrl +"/campos?periodo="+p);
            //$scope.mediciones = mediciones.query();
        //};
    }]);


// the edit controller
    mod.controller("sensorEditCtrl", ["$scope", "$resource", "$routeParams", "$timeout", "apiUrl", function($scope, $resource, $routeParams, $timeout, apiUrl) {
        var ShowSensor = $resource(apiUrl +"/sensores/:id", {id:"@id"}); // a RESTful-capable resource object
        $scope.tipos= tipoOr;
        if ($routeParams.id) {
            // retrieve the corresponding celebrity from the database
            // $scope.sensor.id is now populated so the Delete button will appear in the detailForm in public/html/detail.html
            $scope.sensor = ShowSensor.get({id: $routeParams.id});
            $scope.dbContent = ShowSensor.get({id: $routeParams.id}); // this is used in the noChange function
        }

        // decide whether to enable or not the button Save in the detailForm in public/html/detail.html
        var original="";
        $scope.noChange = function() {
            if($scope.selectedTipo==undefined){
                $scope.selectedTipo=$scope.tipos[$scope.sensor.tipo];
                original=$scope.selectedTipo;
                console.log($scope.selectedTipo);
            }
            var bien = true;
            if(original)bien=!original.localeCompare($scope.selectedTipo);
            return angular.equals($scope.sensor, $scope.dbContent) && bien;
        };

        // to update a sensor
        $scope.save = function() {
            var UpdateSensor = $resource(apiUrl +"/sensores/" + $routeParams.id,null,{update:{method:'PUT'}}); // a RESTful-capable resource object
            $scope.sensorSinMediciones = {
                "id":$scope.sensor.id,
                "nombre":$scope.sensor.nombre,
                "tipo":$scope.tipos.indexOf($scope.selectedTipo)
            };
            UpdateSensor.update($scope.sensorSinMediciones); // $scope.celebrity comes from the detailForm in public/html/detail.html
            $timeout(function() { $scope.go('/sensor'); }); // go back to public/html/main.html
        };

        // to delete a sensor
        $scope.delete = function() {
            var DeleteSensor = $resource( apiUrl +"/sensores/" + $routeParams.id); // a RESTful-capable resource object
            DeleteSensor.delete();
            $timeout(function() { $scope.go('/sensor'); }); // go back to public/html/main.html
        };
        $scope.updateTipo = function(e){
            $scope.sensor.tipo=$scope.tipos.indexOf(e);
        };
    }]);

})(window.angular)