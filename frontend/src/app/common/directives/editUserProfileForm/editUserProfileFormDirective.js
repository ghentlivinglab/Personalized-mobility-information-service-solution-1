/**
 * Created by thibault on 2/28/16.
 */

angular.module("mobiliteit").directive("editUserProfileForm", function() {
    return {restrict: 'E',
        scope: {
            user: '=',
            passwordRequired: '@',
            confirmPassword: '=?'
        },
        require: '^form',
        templateUrl: 'app/common/directives/editUserProfileForm/editUserProfileFormView.html',
        link: function (scope, element, attr, ctrl) {
            scope.form = ctrl;
        }
    }
});