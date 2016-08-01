/**
 * Created by thibault on 2/27/16.
 */

describe("HomeController", function () {

    beforeEach(module('mobiliteit'));

    var HomeCtrl;
    var scope;
    var $q;
    var Event;
    var Route;
    var Restangular;
    var $rootScope;
    var AuthService;

    beforeEach(inject(function (_$rootScope_, $controller, _$q_, _AuthService_, _Event_, Travel, User, _Restangular_, PointOfInterest, _Route_) {
        $rootScope = _$rootScope_;
        $q = _$q_;
        Event = _Event_;
        Route = _Route_;
        Restangular = _Restangular_;
        AuthService = _AuthService_;

        sinon.stub(AuthService, 'getCurrentUserID', function () {
            return 1
        });

        var user = Restangular.restangularizeElement(null, generateFakeUser(true), 'user');
        sinon.stub(User, 'one', function () {
            return {
                get: function () {
                    return $q(function (resolve, reject) {
                        resolve(user);
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

        sinon.stub(Travel, 'for', function () {
            return {
                getList: function () {
                    return $q(function (resolve) {
                        resolve([Restangular.restangularizeElement(null, generateFakeTravel(true), 'travel')]);
                    });
                }
            }
        });

        sinon.stub(Route, 'for', function (travel) {
            return {
                getList: function () {
                    return $q(function (resolve) {
                        var route = Restangular.restangularizeElement(travel, generateFakeRoute(), 'route');
                        route.active = true;
                        resolve([route]);
                    });
                }
            }
        });

        sinon.stub(Event, 'getList', function () {
            return $q(function (resolve) {
                events = [];
                for (var i = 0; i < 20; i++) {
                    events[i] = generateFakeEvent(true);
                }
                resolve(events);
            });
        });

        scope = $rootScope.$new();
        HomeCtrl = $controller('HomeCtrl', {
            $scope: scope,
            uiGmapIsReady: {
                promise: function () {
                    return $q(function (resolve) {
                        resolve();
                    })
                }
            }
        });
    }));

    describe("On load", function () {
        beforeEach(function () {
            scope.$digest();
        });

        it('should get all events', function () {
            expect(Event.getList).to.be.called;
        })
    });

    describe("When changing filters", function () {
        describe("When not enabling relevant", function () {
            beforeEach(function () {
                scope.filters.only_relevant = false;
                scope.$digest();
            });

            it("Should still get events", function () {
                expect(Event.getList.callCount).to.be.greaterThan(0);
            });
        });

        describe("When not enabling routes", function () {
            beforeEach(function () {
                scope.filters.show_routes = false;
                scope.$digest();
            });

            it("Should show no routes", function () {
                expect(scope.map.routes.length).to.equal(0);
            });
        });

        describe("When not enabling POIs", function () {
            beforeEach(function () {
                scope.filters.show_pois = false;
                scope.$digest();
            });

            it("Should show no POIs", function () {
                expect(scope.map.pois.length).to.equal(0);
            });
        });

        describe("When enabling POIs but they are already loaded", function () {
            beforeEach(function () {
                scope.filters.show_pois = true;
                scope.map.pois = [generateFakePointOfInterest(true)];
                scope.$digest();
            });

            it("Should show one POI", function () {
                expect(scope.map.pois.length).to.equal(1);
            });
        });

        describe("When enabling routes", function () {
            beforeEach(function () {
                scope.filters.show_routes = true;
                scope.map.routes = [];
                scope.$digest();
            });

            it("Should show routes", function () {
                expect(scope.map.routes.length).to.not.equal(0);
            });
        });

        describe("When enabling routes who are not active", function () {
            beforeEach(function () {
                Route.for.restore();
                sinon.stub(Route, 'for', function (travel) {
                    return {
                        getList: function () {
                            return $q(function (resolve) {
                                var route = Restangular.restangularizeElement(travel, generateFakeRoute(), 'route');
                                route.active = false;
                                resolve([route]);
                            });
                        }
                    }
                });

                scope.filters.show_routes = true;
                scope.map.routes = [];
                scope.$digest();
            });

            it("Should not show routes", function () {
                expect(scope.map.routes.length).to.equal(0);
            });
        });
    });

    describe("When a new event is broadcasted", function () {
        var event;
        beforeEach(function () {
            event = generateFakeEvent(true);
            $rootScope.$broadcast("$newEvent", event);
            scope.events = [];
            scope.$digest();
        });

        it("should add the event to the scope", function () {
            expect(scope.events.length).to.be.greaterThan(0);
        })
    })
});