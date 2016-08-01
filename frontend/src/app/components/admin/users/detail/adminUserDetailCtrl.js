/**
 * Created by thibault on 2/28/16.
 */

angular.module("mobiliteit").controller("AdminUserDetailCtrl", ["$scope", "User", "$stateParams", "$q", "alertify",
    function ($scope, User, $stateParams, $q, alertify) {
        User.one($stateParams.user_id).get().then(function (r) {
            $scope.user = r;
        });

        $scope.save = function (user) {
            $scope.saveLoading = true;
            user.put()
                .then(function () {
                    alertify.success("De gebruiker is opgeslagen.");
                })
                .catch(function () {
                    alertify.error("Er is een fout opgetreden.");
                })
                .finally(function () {
                    $scope.saveLoading = false;
                });
        };

        $scope.removePOI = function (user, poiIndex) {
            alertify.okBtn("Ja").cancelBtn("Annuleren").confirm("Bent u zeker dat u dit adres wilt verwijderen?",
                function () {
                    user.points_of_interest.splice(poiIndex, 1);
                    user.put()
                        .then(function () {
                            alertify.success("Adres verwijderd");
                        })
                        .catch(function () {
                            alertify.error("Er is een fout opgetreden.");
                        });
                }, function () {
                    alertify.error("Adres niet verwijderd");
                });
        }
    }]);