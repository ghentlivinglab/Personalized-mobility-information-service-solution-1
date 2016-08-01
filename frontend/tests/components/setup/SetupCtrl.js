/**
 * Created by thibault on 4/12/16.
 */

describe("SetupCtrl", function () {
    beforeEach(module("mobiliteit"));

    var SetupCtrl, scope, $q, user, PointOfInterest;

    beforeEach(inject(function ($rootScope, $controller, _$q_, _PointOfInterest_, $stateParams, User, $state, alertify) {
        $q = _$q_;
        PointOfInterest = _PointOfInterest_;

        user = generateFakeUser(true);
        sinon.stub(User, 'one', function () {
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
                post: function () {
                    return $q(function (resolve) {
                        resolve(generateFakePointOfInterest(true));
                    })
                }
            }
        });

        sinon.stub($state, 'go', function () {});
        sinon.stub(alertify, 'error', function (text, callback) {callback();});

        scope = $rootScope.$new();
        SetupCtrl = $controller("SetupCtrl", {
            $scope: scope,
            uiGmapGoogleMapApi: $q(function (resolve) {
                resolve({
                    GeocoderStatus: {
                        OK: true
                    },
                    Geocoder: function () {
                        return {
                            geocode: function (obj, callback) {
                                callback([
                                    {
                                        geometry: {
                                            location: {
                                                lat: function () {
                                                    return 1;
                                                },
                                                lng: function () {
                                                    1;
                                                }
                                            }
                                        }
                                    }
                                ], true);
                            }
                        }
                    }
                });
            })
        })
    }));

    describe("When completing the form with homeaddress", function () {
        beforeEach(function () {
            scope.user = generateFakeUser(true);
            scope.poi = generateFakePointOfInterest(true);
            scope.confirm();
            scope.$digest();
        });

        it('should call the backend to create POIs', function () {
            expect(PointOfInterest.for.callCount).to.equal(1);
        })
    });
    describe("When completing the form without homeaddress", function () {
        beforeEach(function () {
            scope.user = generateFakeUser(true);
            scope.confirm();
            scope.$digest();
        });

        it('should not call the backend to create POIs', function () {
            expect(PointOfInterest.for.callCount).to.equal(0);

        })
    })
});