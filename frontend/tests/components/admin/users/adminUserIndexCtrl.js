/**
 * Created by thibault on 2/28/16.
 */

describe("AdminUserIndexCtrl", function () {
    beforeEach(module("mobiliteit"));

    var AdminUserIndexCtrl;
    var scope;
    var User;
    var $state;
    var $q;
    var Restangular;
    var alertify;

    beforeEach(inject(function (_User_, _alertify_, $rootScope, $controller, _$q_, _$state_, _Restangular_) {
        User = _User_;
        alertify = _alertify_;
        Restangular = _Restangular_;
        $q = _$q_;
        $state = _$state_;

        sinon.stub(User, "getList", function () {
            return $q(function (resolve) {
                var users = [];
                // Generate 10 fake users
                for (var i = 0; i < 10; i++) {
                    users[i] = generateFakeUser(true);
                }
                resolve(users);
            })
        });

        scope = $rootScope.$new();
        AdminUserIndexCtrl = $controller('AdminUserIndexCtrl', {$scope: scope});
    }));

    describe("show all users", function () {
        beforeEach(function () {
            scope.$digest();
        });

        it("should call User.getList() once", function () {
            expect(User.getList.callCount).to.be.equal(1);
        })
    });

    describe("delete a user", function () {
        var user;
        beforeEach(function () {
            user = Restangular.restangularizeElement(null, generateFakeUser(true), "user");

            sinon.stub(alertify, "confirm", function (message, callback) {
                callback();
            });
        });

        describe("when successful", function () {
            beforeEach(function () {
                sinon.stub(alertify, "success", function () {
                });

                sinon.stub(user, "remove", function () {
                    return $q(function (resolve) {
                        resolve();
                    });
                });

                scope.deleteUser(user);

                scope.$digest();
            });
            it("should show a confirmation dialog", function () {
                expect(alertify.confirm.callCount).to.be.equal(1);
            });

            it("should call User.remove", function () {
                expect(user.remove.callCount).to.be.equal(1);
            });

            it("should show a confirmation message", function () {
                expect(alertify.success.callCount).to.be.equal(1);
            });
        });

        describe("when failed", function () {
            beforeEach(function () {
                sinon.stub(alertify, "success", function () {
                });

                sinon.stub(alertify, "error", function () {
                });

                sinon.stub(user, "remove", function () {
                    return $q(function (resolve, reject) {
                        reject();
                    });
                });

                scope.deleteUser(user);

                scope.$digest();
            });
            it("should show a confirmation dialog", function () {
                expect(alertify.confirm.callCount).to.be.equal(1);
            });

            it("should call User.remove", function () {
                expect(user.remove.callCount).to.be.equal(1);
            });

            it("should show an error message", function () {
                expect(alertify.error.callCount).to.be.equal(1);
            });

            it("should not show a success message", function () {
                expect(alertify.success.callCount).to.be.equal(0);
            });
        });


    });
});