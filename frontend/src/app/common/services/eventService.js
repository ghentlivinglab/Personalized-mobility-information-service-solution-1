/**
 * Created by thibault on 2/24/16.
 */

angular.module("mobiliteit").factory("Event", ["Restangular", function (Restangular) {
    return Restangular.service("event");
}]);
