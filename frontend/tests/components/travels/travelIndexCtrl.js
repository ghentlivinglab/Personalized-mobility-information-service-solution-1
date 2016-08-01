/**
 * Created by Lukas on 8/05/2016.
 */

describe("TravelIndexCtrl", function () {

    beforeEach(module('mobiliteit'));

    var TravelIndexCtrl;
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

    beforeEach(inject(function ($rootScope, $controller, _User_, _$q_, _Restangular_, _alertify_, _AuthService_, _Travel_, _PointOfInterest_, _$uibModal_, Route) {
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
        sinon.stub(Route, 'for', function () {
            return {
                post: function () {
                    return $q.when();
                }
            }
        });

        sinon.stub(Travel, 'for', function () {
            return {
                getList: function () {
                    return $q(function (resolve) {
                        resolve([generateFakeTravel(true)]);
                    })
                },
                post: function (travel) {
                    return $q(function (resolve) {
                        travel.id = 1;
                        resolve(Restangular.restangularizeElement(null, travel, 'travel'));
                    })
                }
            }
        });


        scope = $rootScope.$new();
        TravelIndexCtrl = $controller('TravelIndexCtrl', {$scope: scope});
    }));

    describe("Deleting a Travel", function () {
        var travel;
        beforeEach(function () {
            sinon.stub(alertify, 'confirm', function (txt, callback) {
                callback();
            });
            travel = generateFakeTravel(true);
            travel.remove = function () {
                return $q.when();
            };
            sinon.spy(travel, 'remove');
            scope.travels = [travel];
            scope.deleteTravel(travel);
            scope.$digest();
        });

        it('should remove the Travel in the backend', function () {
            expect(travel.remove).to.be.called;
        });
    });

    describe("Adding a Travel", function () {
        var travel;
        beforeEach(function () {
            travel = generateFakeTravel(false);

            sinon.stub($uibModal, 'open', function () {
                return {
                    result: $q(function (resolve) {
                        resolve([travel, [generateFakeRoute(), generateFakeRoute()]]);
                    })
                }
            });

            sinon.spy(alertify, 'success');

            scope.openAddTravelModal();
            scope.$digest();
        });

        it('should show a success message', function () {
            expect(alertify.success).to.be.called;
        });
    });
    describe("Adding reverse travel", function(){
        var travel;
        var count=0;
        beforeEach(function () {
            travel = generateFakeTravel(false);

            sinon.stub($uibModal, 'open', function () {
                return {
                    result: $q(function (resolve) {
                        resolve([travel, [generateFakeRoute(), generateFakeRoute()],count==0]);
                        count++;
                    })
                }
            });

            sinon.spy(alertify, 'success');

            scope.openAddTravelModal();
            scope.$digest();
        });


        it('should show a success message', function () {
            expect(alertify.success).to.be.called;
        });

    });
});