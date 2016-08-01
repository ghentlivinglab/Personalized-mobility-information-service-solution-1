/**
 * Created by thibault on 3/15/16.
 */

angular.module("mobiliteit").controller("SignUpCtrl",
    ["vcRecaptchaService", "$scope", "User", 'alertify', '$state', "AuthService", "WizardHandler",
        function (vcRecaptchaService, $scope, User, alertify, $state, AuthService, WizardHandler) {
            $scope.key = '6LeRAhsTAAAAAJpvPH4vNmXgO4KfNFGmBLy-XWAs';

            $scope.user = {
                mute_notifications: false,
                cell_number: null,
                validated: {
                    email: false,
                    cell_number: false
                } // This isn't for the backend, it will be ignored (or it should be)
            };

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


            $scope.confirm = function (user) {
                // Do the actual signing up
                $scope.registerLoading = true;
                User.post(user, {}, {"X-Captcha-Response": $scope.response})
                    .then(function (createdUser) {
                        AuthService.regularLogin(user.email, user.password)
                            .then(function () {
                                $scope.user = createdUser;
                                WizardHandler.wizard().next();
                            })
                            .catch(function (response) {
                                if (response.status == 403) {
                                    // Don't care about email not verified
                                    $scope.user = createdUser;
                                    WizardHandler.wizard().next();
                                } else {
                                    alertify.error("Er is een fout opgetreden.");
                                }
                            });
                    })
                    .catch(function (error) {
                        switch (error.status) {
                            case 409:
                                alertify.error("Uw email is al in gebruik");
                                break;
                            case 503:
                                alertify.error("Onze mailserver is momenteel niet beschikbaar. Probeer het later opnieuw.");
                                break;
                            default:
                                alertify.error("Er is een fout opgetreden.");
                                break;
                        }
                    })
                    .finally(function () {
                        $scope.registerLoading = false;
                    });
            };

            $scope.verifyEmail = function (pin) {
                $scope.user.customPUT({email_verification_pin: pin}, "verify")
                    .then(function () {
                        $state.go("setup", {user_id: $scope.user.id});
                    })
                    .catch(function () {
                        alertify.error("Er is een fout opgetreden.");
                    })
            }


        }]);