/**
 * Created by thibault on 4/12/16.
 */


describe("EditTravelModalCtrl", function () {

    beforeEach(module('mobiliteit'));

    var EditTravelModalCtrl;
    var scope;
    var EventType;
    var $q;
    var TransportationType;
    var alertify;
    var maps;
    var modalInstance;

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
                                        lng: function () {
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
        modalInstance = {
            close: sinon.spy(),
            dismiss: sinon.spy()
        };
        EditTravelModalCtrl = $controller("EditTravelModalCtrl", {
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
            var travel = generateFakeTravel(false);

            scope.$apply(function () {
                scope.travel = travel;
                scope.enableMap();
            });

        });

        it('should finish without errors', function () {
            // We need an 'it' clause, otherwise no beforeEach elemets get executed
            // The test is successful if there is no exception
        })
    });

    describe("Save routes", function () {
        beforeEach(function () {
            scope.route = generateFakeRoute();
            scope.routes = [];
            scope.enableMap();
            scope.$digest();
            scope.saveroute();
        });

        it('should add the route to scope.routes', function () {
            expect(scope.routes.length).to.be.equal(1);
        })
    });

    describe("Finishing", function () {
        beforeEach(function () {
            sinon.stub(scope, "saveroute", function () {

            });
            scope.finish();
        });

        it('should call scope.saveroute', function () {
            expect(scope.saveroute.callCount).to.be.equal(1);
        });

        it('should exit the modal', function () {
            expect(modalInstance.close.callCount).to.be.equal(1);
        });
    });

    describe("Adding reverse route", function () {
        beforeEach(function () {
            sinon.stub(scope, "saveroute", function () {

            });
            scope.addReverse();
        });

        it('should call scope.saveroute', function () {
            expect(scope.saveroute.callCount).to.be.equal(1);
        });

        it('should exit the modal', function () {
            expect(modalInstance.close.callCount).to.be.equal(1);
        });
    });

    describe("Pressing cancel", function () {
        beforeEach(function () {
            scope.cancel();
        });

        it('should exit the modal', function () {
            expect(modalInstance.dismiss.callCount).to.be.equal(1);
        });
    });

    describe("Clearing the map", function () {
        beforeEach(function () {
            scope.clearMap();
        });

        it('should set showMap to false', function () {
            expect(scope.showMap).to.be.equal(false);
        });
    });
});