/**
 * Created by thibault on 2/27/16.
 */

angular.module("mobiliteit").controller("AdminUserIndexCtrl", ["$scope", "User", "alertify", function ($scope, User, alertify) {

    $scope.loading = true;

    User.getList().then(function (r) {
        $scope.users = r;
        $scope.loading = false;
    });

    $scope.deleteUser = function (user) {
        alertify.okBtn("Ja").cancelBtn("Annuleren").confirm("Bent u zeker dat u deze gebruiker wilt verwijderen?",
            function () {
                user.remove()
                    .then(function () {
                        $scope.users.splice($scope.users.indexOf(user), 1);
                        alertify.success("Gebruiker verwijderd");
                    })
                    .catch(function () {
                        alertify.error("Er is een fout opgetreden.");
                    });
            });
    }
}]);