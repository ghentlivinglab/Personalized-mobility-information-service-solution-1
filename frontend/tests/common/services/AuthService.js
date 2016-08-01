/**
 * Created by thibault on 4/12/16.
 */



describe('AuthService', function () {

    beforeEach(module('mobiliteit'));

    var AuthService;
    var $httpBackend;
    var Restangular;
    var $auth;
    var $q;
    var $rootScope;

    beforeEach(inject(function (_AuthService_, _$httpBackend_, _Restangular_, _$auth_, _$q_, _$rootScope_, $stomp) {
        AuthService = _AuthService_;
        $httpBackend = _$httpBackend_;
        Restangular = _Restangular_;
        $auth = _$auth_;
        $q = _$q_;
        $rootScope = _$rootScope_;

        AuthService.setCurrentUserID(1);

        sinon.stub($auth, 'login', function () {
            return $q(function (resolve) {
                resolve({
                    data: {
                        refresh_token: "abc",
                        user_id: 1,
                        role: "USER"
                    }
                });
            });
        });

        sinon.stub($stomp, 'connect', function () {
            return $q.when();
        });

        sinon.stub($stomp, 'disconnect', function (f) {
            f();
        });
        sinon.stub($stomp, 'subscribe', function (channell, callback) {
            callback(generateFakeEvent(true), null, null);
        });


    }));

    describe('Getting current user id', function () {
        it('should return the value set by setCurrentUserID', function () {
            expect(AuthService.getCurrentUserID()).to.equal(1);
        });
    });

    describe('Forgot Password', function () {
        beforeEach(function () {
            $httpBackend.expectPOST("/api/user/forgot_password")
                .respond(200, {});

            AuthService.forgotPassword("abc@ugent.be", "abc");
            $rootScope.$digest();
            $httpBackend.flush();
        });
        it('should not have any outstanding requests', function () {
            $httpBackend.verifyNoOutstandingExpectation();
            $httpBackend.verifyNoOutstandingRequest();
        });
    });

    describe('Regular login', function () {
        beforeEach(function () {
            $httpBackend.expectPOST('/api/access_token/')
                .respond(200, {
                    token: "abc"
                });

            AuthService.regularLogin("abc@ugent.be", "password");
            $rootScope.$digest();
        });
        it('should call $auth.login', function () {
            expect($auth.login).to.be.called;
        });
    });

    describe('Get access token', function () {
        var promise;
        beforeEach(function () {
            $httpBackend.expectPOST('/api/access_token/')
                .respond(200, {
                    token: "abc",
                    exp: "2016-05-16T09:52:18.999+00:00"
                });

            promise = AuthService.refreshAccessToken();
            $httpBackend.flush();
            $rootScope.$digest();
        });
        it('should call $auth.login', function () {
            expect(promise).to.eventually.be.resolved;
        });
    });

    describe('Facebook login', function () {
        beforeEach(function () {
            sinon.stub($auth, 'authenticate', function () {
                return $q(function (resolve) {
                    resolve({
                        data: {
                            refresh_token: "abc",
                            user_id: 1,
                            role: "USER"
                        }
                    });
                });
            });
            $httpBackend.expectPOST('/api/access_token/')
                .respond(200, {
                    token: "abc",
                    exp: "2016-05-16T09:52:18.999+00:00"
                });
            AuthService.facebookLogin();
            $httpBackend.flush();
            $rootScope.$digest();
        });
        it('should call $auth.authenticate', function () {
            expect($auth.authenticate).to.be.called;
        });
    });

    describe('Google login', function () {
        beforeEach(function () {
            sinon.stub($auth, 'authenticate', function () {
                return $q(function (resolve) {
                    resolve({
                        data: {
                            refresh_token: "abc",
                            user_id: 1,
                            role: "USER"
                        }
                    });
                });
            });
            $httpBackend.expectPOST('/api/access_token/')
                .respond(200, {
                    token: "abc",
                    exp: "2016-05-16T09:52:18.999+00:00"
                });
            AuthService.googleLogin();
            $httpBackend.flush();
            $rootScope.$digest();
        });
        it('should call $auth.authenticate', function () {
            expect($auth.authenticate).to.be.called;
        });
    });

    describe('disconnect', function () {
        beforeEach(function () {
            sinon.stub(console, 'log', function () {
            });
            AuthService.disconnect();
            $rootScope.$digest();
        });
        it('should call console.log once', function () {
            expect(console.log).to.be.called;
        });
    });


});