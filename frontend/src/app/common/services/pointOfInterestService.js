/**
 * Created by thibault on 4/1/16.
 */

angular.module("mobiliteit").factory("PointOfInterest", ["Restangular", function (Restangular) {
    return {
        for: function (user) {
            return Restangular.service("point_of_interest", user);
        }
    }
}]);