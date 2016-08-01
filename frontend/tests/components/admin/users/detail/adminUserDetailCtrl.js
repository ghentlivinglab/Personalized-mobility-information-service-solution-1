/**
 * Created by thibault on 2/28/16.
 */
/**
 * Created by thibault on 2/28/16.
 */

describe("AdminUserDetailCtrl", function () {
    beforeEach(module("mobiliteit"));

    var AdminUserDetailCtrl;
    var scope;
    var User;
    var $state;
    var $q;
    var Restangular;
    var alertify;

    beforeEach(inject(function (_User_, _alertify_, $rootScope, $controller, _$q_, _$state_, _Restangular_, $stateParams) {
        User = _User_;
        alertify = _alertify_;
        Restangular = _Restangular_;
        $q = _$q_;
        $state = _$state_;

        $stateParams.user_id = faker.random.uuid();

        sinon.stub(User, "one", function (id) {
            return {
                get: function () {
                    return $q(function (resolve) {
                        var user = generateFakeUser(false);
                        user = setUserLinksAttribute(user, id);
                        resolve(user);
                    });
                }
            }
        });

        scope = $rootScope.$new();
        AdminUserDetailCtrl = $controller('AdminUserDetailCtrl', {$scope: scope});
    }));

    describe("show the user", function () {
        beforeEach(function () {
            scope.$digest();
        });

        it("should call User.one once", function () {
            expect(User.one.callCount).to.be.equal(1);
        })
    });

    describe("edit and save a user", function () {
        var user;

        beforeEach(function () {
            user = Restangular.restangularizeElement(null, generateFakeUser(true), "user");
            user.first_name = faker.name.firstName();
        });

        describe("successfully", function () {
            beforeEach(function () {
                sinon.stub(user, "put", function () {
                    return $q(function (resolve) {
                        resolve(user);
                    });
                });

                sinon.stub(alertify, "success", function () {

                });

                scope.save(user);
                scope.$digest();
            });

            it("should call User.put once", function () {
                expect(user.put.callCount).to.be.equal(1);
            });

            it("should show a success message", function () {
                expect(alertify.success.callCount).to.be.equal(1);
            });

        });

        describe("failiure", function () {
            beforeEach(function () {
                sinon.stub(user, "put", function () {
                    return $q(function (resolve, reject) {
                        reject(user);
                    });
                });

                sinon.stub(alertify, "error", function () {

                });

                scope.save(user);
                scope.$digest();
            });

            it("should call User.put once", function () {
                expect(user.put.callCount).to.be.equal(1);
            });

            it("should show an error message", function () {
                expect(alertify.error.callCount).to.be.equal(1);
            });

        });


    });

    describe("delete a POI", function () {
        var user;
        beforeEach(function () {
            user = Restangular.restangularizeElement(null, generateFakeUser(true), "user");
            user.points_of_interest[1] = user.points_of_interest[0];

            sinon.stub(alertify, "success", function () {
            });
        });

        describe("when cancelled", function () {
            beforeEach(function () {
                sinon.spy(user, "put");

                sinon.stub(alertify, "error", function () {
                });

                sinon.stub(alertify, "confirm", function (message, callback, cancelCallback) {
                    cancelCallback();
                });

                scope.removePOI(user, 0);
                scope.$digest();
            });

            it("should show an error message", function () {
                expect(alertify.error.callCount).to.be.equal(1);
            });

            it("should not show a success error", function () {
                expect(alertify.success.callCount).to.be.equal(0);
            });
        });

        describe("when successful", function () {
            beforeEach(function () {
                sinon.stub(user, "put", function () {
                    return $q(function (resolve) {
                        resolve(user);
                    });
                });

                sinon.stub(alertify, "confirm", function (message, callback) {
                    callback();
                });

                scope.removePOI(user, 0);
                scope.$digest();
            });

            it("should show a confirmation message", function () {
                expect(alertify.success.callCount).to.be.equal(1);
            });

            it("should show a confirmation dialog", function () {
                expect(alertify.confirm.callCount).to.be.equal(1);
            });

            it("should call User.put", function () {
                expect(user.put.callCount).to.be.equal(1);
            });
        });

        describe("when failed", function () {
            beforeEach(function () {
                sinon.stub(alertify, "error", function () {
                });

                sinon.stub(user, "put", function () {
                    return $q(function (resolve, reject) {
                        reject();
                    });
                });

                sinon.stub(alertify, "confirm", function (message, callback) {
                    callback();
                });

                scope.removePOI(user, 0);

                scope.$digest();
            });

            it("should show an error message", function () {
                expect(alertify.error.callCount).to.be.equal(1);
            });

            it("should not show a success message", function () {
                expect(alertify.success.callCount).to.be.equal(0);
            });

            it("should show a confirmation dialog", function () {
                expect(alertify.confirm.callCount).to.be.equal(1);
            });

            it("should call User.put", function () {
                expect(user.put.callCount).to.be.equal(1);
            });
        });
    });
});