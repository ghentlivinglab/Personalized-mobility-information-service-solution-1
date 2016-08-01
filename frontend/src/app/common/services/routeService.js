/**
 * Created by Lukas on 30/03/2016.
 */
angular.module("mobiliteit").factory("Route", ["Restangular", function (Restangular) {
    return {
        for: function (travel) {
            return Restangular.service("route", travel);
        }
    }
}]);