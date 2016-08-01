/**
 * Created by Lukas on 8/05/2016.
 */
/**
 * Created by thibault on 4/12/16.
 */


describe("AddRouteModalCtrl", function () {

    beforeEach(module('mobiliteit'));

    var AddRouteModalCtrl;
    var scope;
    var EventType;
    var $q;
    var TransportationType;
    var alertify;
    var maps;

    beforeEach(inject(function ($rootScope, $controller, _EventType_, _$q_, _TransportationType_, _alertify_, User, PointOfInterest) {
        EventType = _EventType_;
        $q = _$q_;
        TransportationType = _TransportationType_;
        alertify = _alertify_;

        sinon.stub(TransportationType, 'getList', function () {
            return $q(function (resolve) {
                resolve(POSSIBLE_TRANSPORTATION_TYPES);
            });
        });

        sinon.stub(EventType, 'getList', function () {
            return $q(function (resolve) {
                resolve([generateFakeEventType()]);
            });
        });

        sinon.stub(User, 'one', function () {
            return {
                get: function () {
                    return $q(function (resolve) {
                        resolve(generateFakeUser(true));
                    })
                }
            }
        });

        sinon.stub(PointOfInterest, 'for', function () {
            return {
                getList: function () {
                    return $q(function (resolve) {
                        resolve([generateFakePointOfInterest(true)]);
                    });
                }
            }
        });

        maps = {
            GeocoderStatus: {OK: true},
            TravelMode: {
                DRIVING: 'DRIVING'
            },
            Geocoder: function () {
                return {
                    geocode: function (obj, callback) {
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
                        }], true);
                    }
                }
            },
            DirectionsRenderer: function () {
                return {
                    setOptions: function () {

                    },
                    setDirections: function () {

                    },
                    setMap: function () {

                    },
                    getDirections: function () {
                        return {
                            routes: [
                                {
                                    overview_path: [{
                                        lat: function () {
                                            return 1;
                                        },
                                        lon: function () {
                                            return 0;
                                        }
                                    }]
                                }
                            ]
                        }
                    }
                }
            },
            DirectionsStatus: {
                OK: true
            },
            DirectionsService: function () {
                return {
                    route: function (directions, callback) {
                        callback({
                            routes: [0, 1, 2]
                        }, true)
                    }
                }
            }
        };

        scope = $rootScope.$new();
        var modalInstance = {
            close: function () {
            },
            dismiss: function () {
            }
        };
        AddRouteModalCtrl = $controller("AddRouteModalCtrl", {
            $scope: scope,
            $uibModalInstance: modalInstance,
            travel: generateFakeTravel(true),
            uiGmapGoogleMapApi: $q(function (resolve) {
                resolve(maps);
            })
        });
        scope.map.control = {
            getGMap: function () {

            }
        }
    }));

    describe("Fill in forms", function () {
        beforeEach(function () {
            var route = generateFakeRoute(false);

            scope.$apply(function () {
                scope.route = route;
                scope.enableMap();
            });

        });

        it('should finish without errors', function () {
            // We need an 'it' clause, otherwise no beforeEach elemets get executed
            // The test is successful if there is no exception
        })
    })
});