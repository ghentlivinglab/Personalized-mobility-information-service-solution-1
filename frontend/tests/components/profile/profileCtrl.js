/**
 * Created by thibault on 2/28/16.
 */

describe("ProfileCtrl", function () {

    beforeEach(module('mobiliteit'));

    var ProfileCtrl;
    var scope;
    var User;
    var $q;
    var Restangular;
    var alertify;
    var AuthService;
    var user;
    var Travel;
    var PointOfInterest;
    var $uibModal;
    var $rootScope;

    beforeEach(inject(function (_$rootScope_, $controller, _User_, _$q_, _Restangular_, _alertify_, _AuthService_, _Travel_, _PointOfInterest_, _$uibModal_, Route) {
        AuthService = _AuthService_;
        User = _User_;
        $q = _$q_;
        Restangular = _Restangular_;
        alertify = _alertify_;
        Travel = _Travel_;
        PointOfInterest = _PointOfInterest_;
        $uibModal = _$uibModal_;

        user = Restangular.restangularizeElement(null, generateFakeUser(true), 'user');
        sinon.stub(User, "one", function () {
            return {
                get: function () {
                    return $q(function (resolve) {
                        resolve(user);
                    });
                }
            }
        });

        $rootScope = _$rootScope_;
        scope = $rootScope.$new();
        ProfileCtrl = $controller('ProfileCtrl', {$scope: scope});
    }));

    describe("show the user", function () {
        beforeEach(function () {
            scope.$digest();
        });

        it("should call User.one once", function () {
            expect(User.one.callCount).to.be.equal(1);
        })
    });

    describe("Editing the personal details", function () {
        beforeEach(function () {
            sinon.stub(AuthService, "getCurrentUserID", function () {
                return "some_user_id";
            });

            sinon.stub($uibModal, 'open', function () {
                return {
                    result: $q(function (resolve) {
                        user.password = "password";
                        resolve(user);
                    })
                }
            });

        });

        describe("successfully without changed email", function () {

            beforeEach(function () {
                sinon.stub(alertify, "success", function () {
                });

                sinon.stub(user, "put", function () {
                    return $q(function (resolve) {
                        resolve(user);
                    });
                });

                scope.emailChanged = false;
                scope.save(user);
                // Need to run digest to resolve promises
                scope.$digest();
            });


            it('should call user.put once', function () {
                expect(user.put.callCount).to.equal(1);
            });

            it('should show a success message', function () {
                expect(alertify.success.callCount).to.equal(1);
            });
        });

        describe("unsuccessfully without changed email", function () {

            beforeEach(function () {
                sinon.stub(alertify, "error", function () {
                });

                sinon.stub(user, "put", function () {
                    return $q(function (resolve, reject) {
                        reject(user);
                    });
                });

                scope.save(user);
                // Need to run digest to resolve promises
                scope.$digest();
            });

            it('should call user.put once', function () {
                expect(user.put.callCount).to.equal(1);
            });

            it('should show an error message', function () {
                expect(alertify.error.callCount).to.equal(1);
            });
        });

        describe("successfully with changed email", function () {

            beforeEach(function () {
                sinon.stub(alertify, "success", function () {
                });

                sinon.stub(alertify, "prompt", function () {
                    return $q(function (resolve) {
                        answer = {
                            inputValue: 123
                        };
                        resolve(answer);
                    });
                });

                sinon.stub(user, "put", function () {
                    return $q(function (resolve) {
                        resolve(user);
                    });
                });

                sinon.stub(user, "customPUT", function (verification) {
                    return $q(function (resolve) {
                        resolve(user);
                    });
                });

                scope.emailChanged = true;
                scope.save(user);
                // Need to run digest to resolve promises
                scope.$digest();
            });


            it('should call user.put once', function () {
                expect(user.put.callCount).to.equal(1);
            });

            it('should call user.customPUT once', function () {
                expect(user.customPUT.callCount).to.equal(1);
            });

            it('should show a success message', function () {
                expect(alertify.success.callCount).to.equal(1);
            });

            it('should show a prompt message', function () {
                expect(alertify.prompt.callCount).to.equal(1);
            });
        });

        describe("unsuccessfully with changed email", function () {

            beforeEach(function () {
                sinon.stub(alertify, "prompt", function () {
                    return $q(function (resolve) {
                        answer = {
                            inputValue: 123
                        };
                        resolve(answer);
                    });
                });

                sinon.stub(user, "put", function () {
                    return $q(function (resolve) {
                        resolve(user);
                    });
                });

                sinon.stub(user, "customPUT", function (verification) {
                    return $q(function (resolve, reject) {
                        reject(user);
                    });
                });

                sinon.stub(alertify, "error", function () {
                });

                scope.emailChanged = true;
                scope.save(user);
                // Need to run digest to resolve promises
                scope.$digest();

            });

            it('should call user.put once', function () {
                expect(user.put.callCount).to.equal(1);
            });

            it('should call user.customPUT once', function () {
                expect(user.customPUT.callCount).to.equal(1);
            });

            it('should show an error message', function () {
                expect(alertify.error.callCount).to.equal(1);
            });

            it('should show a prompt message', function () {
                expect(alertify.prompt.callCount).to.equal(1);
            });
        });

    });

    describe("deleting a user", function () {

        beforeEach(function () {
            sinon.stub(alertify, "okBtn", function () {
                    return {
                        cancelBtn: function () {
                            return {
                                confirm: function (text, f) {
                                    f();
                                }
                            }
                        }
                    }
                }
            );
        });


        describe("successfully deleting a user", function () {
            beforeEach(function () {
                scope.user = Restangular.restangularizeElement(null, generateFakeUser(true), 'user');
                sinon.stub(scope.user, "remove", function () {
                    return $q(function (resolve) {
                        resolve();
                    });
                });
                sinon.stub($rootScope, "logout", function () {
                });

                scope.deleteUser();
                scope.$digest();
            });
            it('should show a prompt message', function () {
                expect(alertify.okBtn.callCount).to.equal(1);

            });
            it('should logout', function () {
                expect($rootScope.logout).to.be.called;
            });
        });

        describe("unsuccessfully deleting a user", function () {
            beforeEach(function () {
                scope.user = Restangular.restangularizeElement(null, generateFakeUser(true), 'user');
                sinon.stub(scope.user, "remove", function () {
                    return $q(function (resolve, reject) {
                        reject(scope.user);
                    });
                });
                sinon.stub($rootScope, "logout", function () {
                });

                scope.deleteUser();
                scope.$digest();
            });
            it('should show a prompt message', function () {
                expect(alertify.okBtn.callCount).to.equal(1);
            });

            it('should not logout', function () {
                expect($rootScope.logout).to.not.be.called;
            });

        });
    });

    describe("changing password", function () {

        beforeEach(function () {
                sinon.stub($uibModal, 'open', function () {
                    return {
                        result: $q(function (resolve) {
                            user.password = "password";
                            resolve(user);
                        })
                    }
                });

                sinon.stub(alertify, "success", function () {
                });
                sinon.stub(alertify, "error", function () {
                });
                scope.deleteUser();
                // Need to run digest to resolve promises
                scope.$digest();
            }
        );

        describe("successfully deleting a user", function () {
            beforeEach(function () {
                    sinon.stub(user, "put", function () {
                        return $q(function (resolve) {
                            resolve(user);
                        });
                    });

                    scope.changePassword();
                    scope.$digest();
                }
            );

            it('should show a success message', function () {
                expect(alertify.success.callCount).to.equal(1);
            });


            it('should open modal once', function () {
                expect($uibModal.open.callCount).to.equal(1);
            });
        });
        describe("unsuccessfully deleting a user", function () {
            beforeEach(function () {
                    sinon.stub(user, "put", function () {
                        return $q(function (resolve, reject) {
                            reject(user);
                        });
                    });

                    scope.changePassword();
                    scope.$digest();
                }
            );

            it('should show a success message', function () {
                expect(alertify.error.callCount).to.equal(1);
            });


            it('should open modal once', function () {
                expect($uibModal.open.callCount).to.equal(1);
            });
        });
    });

});
