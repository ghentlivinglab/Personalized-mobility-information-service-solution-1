/**
 * Created by thibault on 4/12/16.
 */

/**
 * Created by thibault on 2/27/16.
 */

describe("ForgotPasswordCtrl", function () {

    beforeEach(module('mobiliteit'));

    var ForgotPasswordCtrl;
    var scope;
    var User;
    var $state;
    var $q;
    var Restangular;
    var alertify;
    var AuthService;
    var jwtHelper;

    beforeEach(inject(function ($rootScope, $controller, _User_, _$state_, _$q_, _Restangular_, _alertify_, _AuthService_, _jwtHelper_) {
        User = _User_;
        $state = _$state_;
        $q = _$q_;
        Restangular = _Restangular_;
        alertify = _alertify_;
        AuthService = _AuthService_;
        jwtHelper = _jwtHelper_;

        sinon.stub(jwtHelper, 'decodeToken', function () {
            return {
                sub: 'abc'
            }
        });

        sinon.stub(jwtHelper, 'isTokenExpired', function (token) {
            return token != "abc";
        });

        var user = generateFakeUser(true);
        sinon.stub(User, 'one', function () {
            return {
                get: function () {
                    return $q(function (resolve) {
                        resolve(user);
                    })
                }
            }
        });

        sinon.stub(AuthService, 'forgotPassword', function () {
            return $q(function (resolve) {
                resolve();
            });
        });

        sinon.spy(alertify, 'error');

        scope = $rootScope.$new();
        ForgotPasswordCtrl = $controller('ForgotPasswordCtrl', {
            $scope: scope,
            WizardHandler: {
                wizard: function () {
                    return {
                        next: function () {
                            // Do nothing
                        }
                    }
                }
            }
        });
    }));

    describe("Entering valid email and clicking the button", function () {
        beforeEach(function () {
            scope.sendMail("test@ugent.be", "abc");
            scope.$digest();
        });

        it('should send an email', function () {
            expect(AuthService.forgotPassword.callCount).to.equal(1);
        })
    });

    describe("Entering the access token", function () {
        beforeEach(function () {
            scope.checkAccessToken("abc");
            scope.$digest();
        });

        it('should check the access code with the backend', function () {
            expect(User.one.callCount).to.equal(1);
        })
    });

    describe("Entering an invalid access token", function () {
        beforeEach(function () {
            scope.checkAccessToken("lksajf;ldsakjflds");
            scope.$digest();
        });

        it('should error out', function () {
            expect(alertify.error.callCount).to.equal(1);
        })
    });

    describe("Entering a new password and clicking the button", function () {
        var user;
        beforeEach(function () {
            user = {
                put: function () {
                    return $q(function (resolve) {
                        resolve();
                    })
                }
            };
            sinon.spy(user, 'put');

            sinon.stub(alertify, 'alert', function (text, callback) {
                callback();
            });

            scope.user = user;
            scope.changePassword("abc", "new_password");
            scope.$digest();
        });

        it('should change the password in the backend', function () {
            expect(user.put.callCount).to.equal(1);
        });

        it('should show an alert', function () {
            expect(alertify.alert.callCount).to.equal(1);
        });
    });

    describe("Entering a new password and clicking the button, but backend fails", function () {
        var user;
        beforeEach(function () {
            user = {
                put: function () {
                    return $q(function (resolve, reject) {
                        reject();
                    })
                }
            };
            sinon.spy(user, 'put');

            scope.user = user;
            scope.changePassword("abc", "new_password");
            scope.$digest();
        });
        it('should show an alert', function () {
            expect(alertify.error.callCount).to.equal(1);
        });
    });
});