/**
 * Created by thibault on 2/27/16.
 */

angular.module("mobiliteit").controller("IndexCtrl", ['$scope', 'User', '$state', 'AuthService', 'alertify',
    function ($scope, User, $state, AuthService, alertify) {
        function redirectToHomepage(role) {
            switch (role) {
                case "USER":
                    $state.go("app.home");
                    break;
                case "OPERATOR":
                    $state.go("operator.events");
                    break;
                case "ADMIN":
                    $state.go("admin.users");
                    break;
                default:
                    alertify.error("Onbekende groep: " + role);
            }
        }

        $scope.facebookLogin = function () {
            $scope.facebookLoginLoading = true;
            AuthService.facebookLogin()
                .then(function (response) {
                    if (response.created) {
                        $state.go("setup");
                    } else {
                        redirectToHomepage(response.role);
                    }
                })
                .catch(function (response) {
                    switch (response.status) {
                        case 503:
                            alertify.error("Uw account heeft geen email adres. U kan niet inloggen zonder email adres.");
                            break;
                        case 403:
                            alertify.error("Uw email is nog niet geverifieerd. Contacteer de helpdesk.");
                            break;
                        default:
                            alertify.error("Er heeft zich een interne fout voorgedaan");
                            break;
                    }
                })
                .finally(function () {
                    $scope.facebookLoginLoading = false;
                })
        };

        $scope.googleLogin = function () {
            $scope.googleLoginLoading = true;
            AuthService.googleLogin()
                .then(function (response) {
                    if (response.created) {
                        $state.go("setup");
                    } else {
                        redirectToHomepage(response.role);
                    }
                })
                .catch(function (response) {
                    switch (response.status) {
                        case 403:
                            alertify.error("Uw email is nog niet geverifieerd. Contacteer de helpdesk.");
                            break;
                        default:
                            alertify.error("Er heeft zich een interne fout voorgedaan");
                            break;
                    }
                })
                .finally(function () {
                    $scope.googleLoginLoading = false;
                })
        };

        $scope.login = function (user) {
            $scope.loginLoading = true;
            AuthService.regularLogin(user.email, user.password)
                .then(redirectToHomepage)
                .catch(function (response) {
                    switch (response.status) {
                        case 403:
                            alertify.error("Uw email is nog niet geverifieerd. Contacteer de helpdesk.");
                            break;
                        default:
                            alertify.error("Ongeldige email/wachtwoord combinatie.");
                            break;
                    }
                })
                .finally(function () {
                    $scope.loginLoading = false;
                })
        };

        if (AuthService.isLoggedIn()) {
            AuthService.refreshAccessToken()
                .then(function () {
                    $state.go("app.home");
                })
        }
    }]);