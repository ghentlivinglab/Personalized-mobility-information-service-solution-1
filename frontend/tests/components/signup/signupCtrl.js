/**
 * Created by thibault on 4/12/16.
 */


describe("SignUpCtrl", function () {
    beforeEach(module('mobiliteit'));

    var SignUpCtrl;
    var scope;
    var $q;
    var nextHandler;
    var User;
    var AuthService;
    var $state;

    beforeEach(inject(function ($controller, $rootScope, vcRecaptchaService, _User_, alertify, _$state_, _AuthService_, _$q_) {
        $q = _$q_;
        User = _User_;
        AuthService = _AuthService_;
        $state=_$state_;

        sinon.stub(alertify, 'error', function () {
        });
        sinon.stub($state, 'go', function () {
        });

        nextHandler = sinon.spy();

        scope = $rootScope.$new();
        SignUpCtrl = $controller("SignUpCtrl", {
            $scope: scope,
            WizardHandler: {
                wizard: function () {
                    return {
                        next: nextHandler
                    }
                }
            }
        });
    }));

    describe("Entering email, password & clicking the button", function () {
        describe("Unused email", function () {
            beforeEach(function () {

                sinon.stub(AuthService, 'regularLogin', function () {
                    return $q(function (resolve) {
                        resolve()
                    })
                });
                sinon.stub(User, 'post', function () {
                    return $q(function (resolve) {
                        resolve();
                    });
                });

                scope.confirm({
                    email: "test@ugent.be",
                    password: "very_secure_password"
                });
                scope.$digest();
            });

            it('should go one step further in the wizard', function () {
                expect(nextHandler).to.be.called;
            })
        });

        describe("Unverified mail (doesn't matter)", function () {
            beforeEach(function () {

                sinon.stub(AuthService, 'regularLogin', function () {
                    return $q(function (resolve, reject) {
                        reject({
                            status: 403
                        })
                    });
                });

                sinon.stub(User, 'post', function () {
                    return $q(function (resolve) {
                        resolve();
                    });
                });

                scope.confirm({
                    email: "test@ugent.be",
                    password: "very_secure_password"
                });
                scope.$digest();
            });

            it('should go one step further in the wizard', function () {
                expect(nextHandler).to.be.called;
            })
        });

        describe("Backend error from AuthService", function () {
            beforeEach(function () {

                sinon.stub(AuthService, 'regularLogin', function () {
                    return $q(function (resolve, reject) {
                        reject({
                            status: 500
                        })
                    });
                });

                sinon.stub(User, 'post', function () {
                    return $q(function (resolve) {
                        resolve();
                    });
                });

                scope.confirm({
                    email: "test@ugent.be",
                    password: "very_secure_password"
                });
                scope.$digest();
            });

            it('should go one step further in the wizard', function () {
                expect(nextHandler).to.not.be.called;
            })
        });

        describe("Used email", function () {
            beforeEach(function () {
                sinon.stub(AuthService, 'regularLogin', function () {
                    return $q(function (resolve) {
                        resolve()
                    })
                });

                sinon.stub(User, 'post', function () {
                    return $q(function (resolve, reject) {
                        reject({
                            status: 409
                        })
                    });
                });

                scope.confirm({
                    email: "test@ugent.be",
                    password: "very_secure_password"
                });
                scope.$digest();
            });

            it('should not go one step further in the wizard', function () {
                expect(nextHandler).to.not.be.called;
            });
        });

        describe("Backend error", function () {
            beforeEach(function () {
                sinon.stub(User, 'post', function () {
                    return $q(function (resolve, reject) {
                        reject({
                            status: 500
                        })
                    });
                });

                sinon.stub(AuthService, 'regularLogin', function () {
                    return $q(function (resolve) {
                        resolve()
                    })
                });

                scope.confirm({
                    email: "test@ugent.be",
                    password: "very_secure_password"
                });
                scope.$digest();
            });

            it('should not go one step further in the wizard', function () {
                expect(nextHandler).to.not.be.called;
            });
        });

        describe("mailserver not available", function () {
            beforeEach(function () {
                sinon.stub(User, 'post', function () {
                    return $q(function (resolve, reject) {
                        reject({
                            status: 503
                        })
                    });
                });

                sinon.stub(AuthService, 'regularLogin', function () {
                    return $q(function (resolve) {
                        resolve()
                    })
                });

                scope.confirm({
                    email: "test@ugent.be",
                    password: "very_secure_password"
                });
                scope.$digest();
            });

            it('should not go one step further in the wizard', function () {
                expect(nextHandler).to.not.be.called;
            });
        });
    });


    describe("Entering verification PIN", function () {
        var user;
        describe("succesfully", function () {
            beforeEach(function () {
                user = {
                    customPUT: function () {
                        return $q(function (resolve) {
                            resolve();
                        });
                    }
                };
                sinon.spy(user, 'customPUT');
                scope.user = user;

                scope.verifyEmail(123);
                scope.$digest();
            });

            it('should call the backend', function () {
                expect(user.customPUT).to.be.called;
            });

            it('should call $change.go once',function(){
                expect($state.go).to.be.called
            })
        });
        describe("unsuccesfully", function () {
            beforeEach(function () {
                user = {
                    customPUT: function () {
                        return $q(function (resolve,reject) {
                            reject();
                        });
                    }
                };
                sinon.spy(user, 'customPUT');
                scope.user = user;

                scope.verifyEmail(123);
                scope.$digest();
            });

            it('should call the backend', function () {
                expect(user.customPUT).to.be.called;
            });
            it('should not call change in state once',function(){
                expect($state.go).to.not.be.called;
            })
        });
    });
});