/**
 * Created by thibault on 4/7/16.
 */

angular.module("mobiliteit")
    .controller("ForgotPasswordCtrl", ["$scope", "User", "vcRecaptchaService", "WizardHandler", "AuthService", "jwtHelper", "alertify", "$auth", "$state",
        function ($scope, User, vcRecaptchaService, WizardHandler, AuthService, jwtHelper, alertify, $auth, $state) {
            $scope.key = '6LeRAhsTAAAAAJpvPH4vNmXgO4KfNFGmBLy-XWAs';

            $scope.setResponse = function (response) {
                $scope.response = response;
            };
            $scope.setWidgetId = function (widgetId) {
                $scope.widgetId = widgetId;
            };
            $scope.cbExpiration = function () {
                vcRecaptchaService.reload($scope.widgetId);
                delete $scope.response;
            };

            $scope.sendMail = function (email, response) {
                AuthService.forgotPassword(email, response)
                    .then(function () {
                        WizardHandler.wizard().next();
                    })
            };

            $scope.checkAccessToken = function (accessToken) {
                var payload = jwtHelper.decodeToken(accessToken);
                if (payload == null || jwtHelper.isTokenExpired(accessToken)) {
                    alertify.error("Uw activatiecode is niet geldig of is vervallen.");
                    return;
                }
                var userId = payload.sub;
                $auth.setToken(accessToken);
                User.one(userId).get()
                    .then(function (user) {
                        $scope.user = user;
                        WizardHandler.wizard().next();
                    })
                    .catch(function (err) {
                        alertify.error("Uw activatiecode is niet geldig.");
                    })
            };

            $scope.changePassword = function (accessToken, newPassword) {
                $scope.user.password = newPassword;
                $auth.setToken(accessToken);
                $scope.user.put()
                    .then(function () {
                        alertify.alert("Uw wachtwoord is aangepast", function () {
                            $state.go("index");
                        })
                    })
                    .catch(function () {
                        alertify.error("Er is een fout opgretreden");
                    })
            };
        }]);