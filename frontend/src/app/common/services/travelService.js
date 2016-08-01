/**
 * Created by thibault on 2/27/16.
 */

angular.module("mobiliteit").factory("Travel", ["Restangular", function (Restangular) {
    return {
        for: function (user) {
            return Restangular.service("travel", user);
        }
    }
}]);