/**
 * Created by Lukas on 30/03/2016.
 */

angular.module("mobiliteit").factory("TransportationType", ["Restangular", function (Restangular) {
    return Restangular.service("transportationtype");
}]);