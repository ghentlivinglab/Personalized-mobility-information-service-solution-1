/**
 * Created by thibault on 2/27/16.
 */

angular.module("mobiliteit").factory("EventType", ["Restangular", function (Restangular) {
    return Restangular.service("eventtype");
}]);