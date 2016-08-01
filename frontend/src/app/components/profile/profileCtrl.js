/**
 * Created by thibault on 2/28/16.
 */

angular.module("mobiliteit")
    .controller("ProfileCtrl",
        ['$scope', 'User', "AuthService", "alertify", '$state', "$rootScope", "$uibModal",
            function ($scope, User, AuthService, alertify, $state, $rootScope, $uibModal) {

                User.one(AuthService.getCurrentUserID()).get()
                    .then(function (user) {
                        $scope.user = user;
                    });

                $scope.save = function (user) {
                    $uibModal.open({
                        templateUrl: 'passwordModal.html',
                        controller: ["$scope", "$uibModalInstance", "user",
                            function ($scope, $uibModalInstance, user) {
                                $scope.user = user;
                                $scope.cancel = function () {
                                    $uibModalInstance.dismiss('cancel')
                                };
                                $scope.close = $uibModalInstance.close;
                            }],
                        backdrop: 'static',
                        keyboard: false,
                        resolve: {
                            user: user
                        }
                    }).result
                        .then(function (user) {
                            $scope.loading = true;
                            user.put()
                                .then(function () {
                                    user.password = "";
                                    if ($scope.emailChanged) {
                                        alertify
                                            .prompt("U heeft een nieuwe activatiemail gekregen. Vul de code hieronder in.")
                                            .then(function (result) {
                                                var pin = result.inputValue;
                                                user.customPUT({email_verification_pin: pin}, "verify")
                                                    .then(function () {
                                                        $scope.emailChanged = false;
                                                        alertify.success("De veranderingen zijn opgeslagen.");
                                                    })
                                                    .catch(function () {
                                                        alertify.error("Er is een fout opgetreden.");
                                                    })
                                            })
                                    } else {
                                        alertify.success("De veranderingen zijn opgeslagen.");
                                    }
                                })
                                .catch(function () {
                                    alertify.error("Er is een fout opgetreden.");
                                })
                                .finally(function () {
                                    $scope.loading = false;
                                });
                        });
                };

                $scope.deleteUser = function () {
                    alertify.okBtn("Ja").cancelBtn("Annuleren")
                        .confirm("Bent u zeker dat u uw account wilt verwijderen?",
                            function () {
                                $scope.user.remove()
                                    .then(function () {
                                        $rootScope.logout();
                                    })
                                    .catch(function () {
                                        alertify.error("Er is een fout opgetreden.");
                                    });
                            });
                };

                $scope.changePassword = function () {
                    $uibModal.open({
                        templateUrl: 'changePasswordModal.html',
                        controller: ["$scope", "$uibModalInstance", "user",
                            function ($scope, $uibModalInstance, user) {
                                $scope.user = user;
                                $scope.cancel = function () {
                                    $uibModalInstance.dismiss('cancel')
                                };
                                $scope.close = $uibModalInstance.close;
                            }],
                        backdrop: 'static',
                        keyboard: false,
                        resolve: {
                            user: $scope.user
                        }
                    }).result
                        .then(function (user) {
                            $scope.loading = true;
                            user.put()
                                .then(function () {
                                    user.password = "";
                                    alertify.success("De veranderingen zijn opgeslagen.");
                                })
                                .catch(function () {
                                    alertify.error("Er is een fout opgetreden.");
                                })
                                .finally(function () {
                                    $scope.loading = false;
                                });
                        });
                }


            }]);