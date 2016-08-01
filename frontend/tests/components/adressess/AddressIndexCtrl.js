/**
 * Created by Lukas on 8/05/2016.
 */
/**
 * Created by thibault on 2/28/16.
 */

describe("AddressIndexCtrl", function () {

    beforeEach(module('mobiliteit'));

    var AddressIndexCtrl;
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



        sinon.stub(PointOfInterest, 'for', function () {
            return {
                getList: function () {
                    return $q(function (resolve) {
                        resolve([generateFakePointOfInterest(true)]);
                    })
                },
                post: function (poi) {
                    return $q(function (resolve) {
                        resolve(poi);
                    })
                }
            }
        });

        scope = $rootScope.$new();
        AddressIndexCtrl = $controller('AddressIndexCtrl', {$scope: scope, uiGmapGoogleMapApi: $q(function (resolve) {
            resolve({
                Geocoder: function () {
                    return {
                        geocode: function (address, callback) {
                            callback([{
                                geometry: {
                                    location: {
                                        lat: function () {
                                            return 1;
                                        },
                                        lng: function () {
                                            return 1;
                                        }
                                    }
                                }
                            }])
                        }
                    }
                }
            })
        })});
    }));

    describe("Removing a POI", function () {
        var spy;
        beforeEach(function () {
            var poi = generateFakePointOfInterest(true);
            poi.remove = function () {
                return $q.when();
            };
            spy = sinon.spy(poi, 'remove');

            sinon.stub(alertify, 'confirm', function (msg, callback) {
                callback();
            });

            scope.points_of_interests = [poi];

            scope.removePOI(poi);
            scope.$digest();
        });

        it('should call poi.delete', function () {
            expect(spy).to.be.called;
        })
    });

    describe("Adding a new POI", function () {
        var spy;
        beforeEach(function () {
            sinon.stub($uibModal, 'open', function () {
                return {
                    result: $q(function (resolve) {
                        resolve(generateFakePointOfInterest(false));
                    })
                }
            });

            spy = sinon.spy(scope, 'openPOIModal');
            scope.openNewPOIModal();
            scope.$digest();
        });

        it('should open the modal', function () {
            expect(spy).to.be.called;
        })
    });

    describe("Editing a POI", function () {
        var spy, poi;
        beforeEach(function () {
            spy = sinon.spy(scope, 'openPOIModal');

            poi = generateFakePointOfInterest(true);
            poi.put = function () {
                return $q(function (resolve) {
                    resolve(poi);
                });
            };

            sinon.stub($uibModal, 'open', function () {
                return {
                    result: $q(function (resolve) {
                        resolve(poi);
                    })
                }
            });

            sinon.spy(poi, 'put');

            scope.editPOI(poi);
            scope.$digest();
        });

        it('should open the modal', function () {
            expect(spy).to.be.called;
        });

        it('should update the POI in the backend', function () {
            expect(poi.put).to.be.called;
        });
    });
});