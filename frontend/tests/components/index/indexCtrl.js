/**
 * Created by thibault on 2/27/16.
 */

describe("IndexController", function () {

    beforeEach(module('mobiliteit'));

    var IndexCtrl;
    var scope;
    var User;
    var $state;
    var $q;
    var Restangular;
    var alertify;
    var AuthService;
    var $httpBackend;

    beforeEach(inject(function ($rootScope, $controller, _User_, _$state_, _$q_, _$httpBackend_, _Restangular_, _alertify_, _AuthService_) {
        scope = $rootScope.$new();
        IndexCtrl = $controller('IndexCtrl', {$scope: scope});
        User = _User_;
        $state = _$state_;
        $q = _$q_;
        Restangular = _Restangular_;
        alertify = _alertify_;
        AuthService = _AuthService_;
        $httpBackend = _$httpBackend_;

        sinon.stub($state, "go", function () {
        })
    }));

    describe("Logging in", function () {
        var user;

        beforeEach(function () {
            user = {
                email: "test@ugent.be",
                password: "very_secure_password"
            };
        });

        describe("Successful login", function () {
            beforeEach(function () {
                sinon.spy(scope, 'login');

                sinon.stub(AuthService, 'regularLogin', function () {
                    return $q(function (resolve, reject) {
                        resolve({
                            role: "USER"
                        })
                    });
                });

                $httpBackend.expectPOST('/api/access_token/')
                    .respond(200, {
                        token: "test"
                    });


                scope.login(user);
                // Need to run digest to resolve promises
                scope.$digest();
            });

            it('should have called scope.login once', function () {
                expect(scope.login.callCount).to.equal(1);
            });
        });

        describe("unsuccessful login", function () {
            beforeEach(function () {
                sinon.spy(scope, 'login');

                sinon.stub(AuthService, 'regularLogin', function () {
                    return $q(function (resolve, reject) {
                        reject({status: 403});
                    });
                });

                sinon.stub(alertify, 'error', function () {
                });

                $httpBackend.expectPOST('/api/access_token/')
                    .respond(200, {
                        token: "test"
                    });


                scope.login(user);
                // Need to run digest to resolve promises
                scope.$digest();
            });

            it('should have called scope.login once', function () {
                expect(scope.login.callCount).to.equal(1);
            });

            it('should have shown an error', function () {
                expect(alertify.error.callCount).to.equal(1);
            });
        });

        describe("unsuccessful login 500", function () {
            beforeEach(function () {
                sinon.spy(scope, 'login');

                sinon.stub(AuthService, 'regularLogin', function () {
                    return $q(function (resolve, reject) {
                        reject({status: 500});
                    });
                });

                sinon.stub(alertify, 'error', function () {
                });

                $httpBackend.expectPOST('/api/access_token/')
                    .respond(200, {
                        token: "test"
                    });


                scope.login(user);
                // Need to run digest to resolve promises
                scope.$digest();
            });

            it('should have called scope.login once', function () {
                expect(scope.login.callCount).to.equal(1);
            });

            it('should have shown an error', function () {
                expect(alertify.error.callCount).to.equal(1);
            });
        });

        describe("Logging in with facebook", function () {

            beforeEach(function () {

                sinon.spy(scope, 'facebookLogin');
                sinon.stub(AuthService, "facebookLogin", function () {
                    return $q(function (resolve) {
                        resolve("USER", true);
                    })
                });

                $httpBackend.expectPOST('/api/access_token/')
                    .respond(200, {
                        token: "test"
                    });

                scope.facebookLogin();
                scope.$digest();
            });

            it('should have called scope.facebookLogin once', function () {
                expect(scope.facebookLogin.callCount).to.equal(1);
            });
        });

        describe("unsuccesfully with facebook 503", function () {
            beforeEach(function () {
                sinon.spy(scope, 'facebookLogin');

                sinon.stub(AuthService, 'facebookLogin', function () {
                    return $q(function (resolve, reject) {
                        reject({status: 503});
                    });
                });

                sinon.stub(alertify, 'error', function () {
                });

                $httpBackend.expectPOST('/api/access_token/')
                    .respond(200, {
                        token: "test"
                    });


                scope.facebookLogin(user);
                // Need to run digest to resolve promises
                scope.$digest();
            });

            it('should have called scope.facebookLogin once', function () {
                expect(scope.facebookLogin.callCount).to.equal(1);
            });

            it('should have shown an error', function () {
                expect(alertify.error.callCount).to.equal(1);
            });
        });

        describe("unsuccesfully with facebook 403", function () {
            beforeEach(function () {
                sinon.spy(scope, 'facebookLogin');

                sinon.stub(AuthService, 'facebookLogin', function () {
                    return $q(function (resolve, reject) {
                        reject({status: 403});
                    });
                });

                sinon.stub(alertify, 'error', function () {
                });

                $httpBackend.expectPOST('/api/access_token/')
                    .respond(200, {
                        token: "test"
                    });


                scope.facebookLogin(user);
                // Need to run digest to resolve promises
                scope.$digest();
            });

            it('should have called scope.facebookLogin once', function () {
                expect(scope.facebookLogin.callCount).to.equal(1);
            });

            it('should have shown an error', function () {
                expect(alertify.error.callCount).to.equal(1);
            });
        });

        describe("unsuccesfully with facebook 500", function () {
            beforeEach(function () {
                sinon.spy(scope, 'facebookLogin');

                sinon.stub(AuthService, 'facebookLogin', function () {
                    return $q(function (resolve, reject) {
                        reject({status: 500});
                    });
                });

                sinon.stub(alertify, 'error', function () {
                });

                $httpBackend.expectPOST('/api/access_token/')
                    .respond(200, {
                        token: "test"
                    });


                scope.facebookLogin(user);
                // Need to run digest to resolve promises
                scope.$digest();
            });

            it('should have called scope.facebookLogin once', function () {
                expect(scope.facebookLogin.callCount).to.equal(1);
            });

            it('should have shown an error', function () {
                expect(alertify.error.callCount).to.equal(1);
            });
        });

        describe("Logging in with google", function () {

            beforeEach(function () {

                sinon.spy(scope, 'googleLogin');
                sinon.stub(AuthService, "googleLogin", function () {
                    return $q(function (resolve) {
                        resolve("USER", true);
                    })
                });

                $httpBackend.expectPOST('/api/access_token/')
                    .respond(200, {
                        token: "test"
                    });

                scope.googleLogin();
                scope.$digest();
            });

            it('should have called scope.googleLogin once', function () {
                expect(scope.googleLogin.callCount).to.equal(1);
            });
        });

        describe("unsuccesfully with google 403", function () {
            beforeEach(function () {
                sinon.spy(scope, 'googleLogin');

                sinon.stub(AuthService, 'googleLogin', function () {
                    return $q(function (resolve, reject) {
                        reject({status: 403});
                    });
                });

                sinon.stub(alertify, 'error', function () {
                });

                $httpBackend.expectPOST('/api/access_token/')
                    .respond(200, {
                        token: "test"
                    });


                scope.googleLogin(user);
                // Need to run digest to resolve promises
                scope.$digest();
            });

            it('should have called scope.googleLogin once', function () {
                expect(scope.googleLogin.callCount).to.equal(1);
            });

            it('should have shown an error', function () {
                expect(alertify.error.callCount).to.equal(1);
            });
        });

        describe("unsuccesfully with google 500", function () {
            beforeEach(function () {
                sinon.spy(scope, 'googleLogin');

                sinon.stub(AuthService, 'googleLogin', function () {
                    return $q(function (resolve, reject) {
                        reject({status: 500});
                    });
                });

                sinon.stub(alertify, 'error', function () {
                });

                $httpBackend.expectPOST('/api/access_token/')
                    .respond(200, {
                        token: "test"
                    });


                scope.googleLogin(user);
                // Need to run digest to resolve promises
                scope.$digest();
            });

            it('should have called scope.googleLogin once', function () {
                expect(scope.googleLogin.callCount).to.equal(1);
            });

            it('should have shown an error', function () {
                expect(alertify.error.callCount).to.equal(1);
            });
        });


    });

});