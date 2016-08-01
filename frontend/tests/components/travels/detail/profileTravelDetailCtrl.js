describe("TravelDetailCtrl", function () {
    beforeEach(module("mobiliteit"));
    var TravelDetailCtrl;
    var scope;
    var Travel;
    var User;
    var $state;
    var $q;
    var alertify;
    var AuthService;
    var Restangular;
    var user;
    var Route;
    var $uibModal;

    beforeEach(inject(function (_Restangular_, _AuthService_, _User_, _Travel_, _$uibModal_, _alertify_, $rootScope, $controller, _$q_, _$state_, $stateParams, _Route_) {
        Restangular = _Restangular_;
        User = _User_;
        Travel = _Travel_;
        alertify = _alertify_;
        $q = _$q_;
        $state = _$state_;
        AuthService = _AuthService_;
        Route = _Route_;
        $uibModal = _$uibModal_;

        $stateParams.travel_id = faker.random.uuid();
        scope = $rootScope.$new();

        user = Restangular.restangularizeElement(null, generateFakeUser(true), 'user');

        sinon.stub(AuthService, "getCurrentUserID", function () {
            return user.id;
        });

        sinon.stub(User, "one", function () {
            return {
                get: function () {
                    return $q(function (resolve) {
                        resolve(user);
                    });
                }
            }
        });

        sinon.stub(Travel, "for", function () {
            return {
                one: function () {
                    return {
                        get: function () {
                            return $q(function (resolve) {
                                resolve(generateFakeTravel());
                            });
                        }
                    }

                }
            }
        });



        TravelDetailCtrl = $controller('TravelDetailCtrl', {$scope: scope});
    }));

    describe("show a travel", function () {
        beforeEach(function () {
            sinon.stub(Route, "for", function () {
                return {
                    getList: function () {
                        return $q(function (resolve) {
                            resolve([generateFakeRoute(), generateFakeRoute()]);
                        });
                    }
                }
            });
            scope.$digest();
        });

        it("should call User.one once", function () {
            expect(User.one.callCount).to.be.equal(1);
        });

        it("should call Travel.for() once", function () {
            expect(Travel.for.callCount).to.be.equal(1);
        });


        it("should call AuthService.getCurrentUserID() once", function () {
            expect(AuthService.getCurrentUserID.callCount).to.be.equal(1);
        });


    });


    describe("edit and save a travel", function () {
        var travel;

        beforeEach(function () {
            sinon.stub(Route, "for", function () {
                return {
                    getList: function () {
                        return $q(function (resolve) {
                            resolve([generateFakeRoute(), generateFakeRoute()]);
                        });
                    }
                }
            });
            travel = Restangular.restangularizeElement(null, generateFakeTravel(), "travel");
            travel.date = faker.date.past().toISOString();
        });

        describe("successfully", function () {
            beforeEach(function () {
                sinon.stub(travel, "put", function () {
                    return $q(function (resolve) {
                        resolve(travel);
                    });
                });

                sinon.stub(alertify, "success", function () {

                });

                scope.save(travel);
                scope.$digest();
            });

            it("should call travel.put once", function () {
                expect(travel.put.callCount).to.be.equal(1);
            });

            it("should show a success message", function () {
                expect(alertify.success.callCount).to.be.equal(1);
            });

        });

        describe("failiure", function () {
            beforeEach(function () {
                sinon.stub(travel, "put", function () {
                    return $q(function (resolve, reject) {
                        reject(travel);
                    });
                });

                sinon.stub(alertify, "error", function () {

                });

                scope.save(travel);
                scope.$digest();
            });

            it("should call travel.put once", function () {
                expect(travel.put.callCount).to.be.equal(1);
            });

            it("should show an error message", function () {
                expect(alertify.error.callCount).to.be.equal(1);
            });

        });
    });

    describe("delete a route", function () {
        var route;
        var route2;
        beforeEach(function () {
            sinon.stub(Route, "for", function () {
                return {
                    getList: function () {
                        return $q(function (resolve) {
                            resolve([generateFakeRoute(), generateFakeRoute()]);
                        });
                    }
                }
            });
            route = Restangular.restangularizeElement(null, generateFakeRoute(), "route");
            route2 = Restangular.restangularizeElement(null, generateFakeRoute(), "route");

        });

        describe("succesfully with more then one route left", function () {
            beforeEach(function () {
                scope.routes = [route, route2];
                sinon.stub(alertify, "confirm", function (text, f) {
                    f();
                });

                sinon.stub(alertify, "success", function () {
                });

                sinon.stub(route, "remove", function () {
                    return $q(function (resolve) {
                        resolve(route);
                    });
                });

                scope.deleteRoute(route);
                scope.$digest();

            });
            it("should call route.remove once", function () {
                expect(route.remove.callCount).to.be.equal(1);
            });

            it("should call alertify.success once", function () {
                expect(alertify.success.callCount).to.be.equal(1);
            });
        });

        describe("succesfully with one route left", function () {
            beforeEach(function () {
                scope.routes = [route];
                scope.travel = Restangular.restangularizeElement(null, generateFakeTravel(), "travel");

                sinon.stub(alertify, "confirm", function (text, f) {
                    f();
                });

                sinon.stub(alertify, "error", function () {
                });


                scope.travel.remove = function () {
                    return $q.when();
                };

                sinon.stub($state,"go");

                scope.deleteRoute(route);
                scope.$digest();

            });
            it("should change state, function ()",function() {
                expect($state.go).to.be.called;
            });

        });
    });

    describe("toggleRoute", function () {
        var route;
        beforeEach(function () {
            sinon.stub(Route, "for", function () {
                return {
                    getList: function () {
                        return $q(function (resolve) {
                            resolve([generateFakeRoute(), generateFakeRoute()]);
                        });
                    }
                }
            });
            route = Restangular.restangularizeElement(null, generateFakeRoute(), "travel");
        });

        describe("succesfully", function () {

            beforeEach(function () {

                sinon.stub(route, "put", function () {
                    return $q(function (resolve) {
                        resolve(route);
                    });
                });
                scope.toggleRouteActive(route);
                scope.$digest();
            });

            it("should call route.put once", function () {
                expect(route.put.callCount).to.be.equal(1);
            });
        });


        describe("unsuccesfully", function () {
            beforeEach(function () {
                sinon.stub(route, "put", function () {
                    return $q(function (resolve, reject) {
                        reject(route);
                    });
                });
                sinon.stub(alertify, "error", function () {
                });


                scope.toggleRouteActive(route);
                scope.$digest();
            });


            it("should call route.put once", function () {
                expect(route.put.callCount).to.be.equal(1);
            });
            it("should call alertify.error once", function () {
                expect(alertify.error.callCount).to.be.equal(1);
            });
        });
    });

    describe("creating extra route", function () {
        var route;
        var route2;
        beforeEach(function () {
            route = Restangular.restangularizeElement(null, generateFakeRoute(), "travel");
            route2 = Restangular.restangularizeElement(null, generateFakeRoute(), "travel");
            scope.routes=[route2];
            sinon.stub($uibModal, 'open', function () {
                return {
                    result: $q(function (resolve) {
                        resolve(route);
                    })
                }
            });
        });

        describe("succesfully",function(){
            beforeEach(function(){
                sinon.stub(Route, "for", function () {
                    return {
                        getList: function () {
                            return $q(function (resolve) {
                                resolve([generateFakeRoute(), generateFakeRoute()]);
                            });
                        },
                        post: function () {
                            return $q(function (resolve) {
                                resolve(route);
                            });
                        }
                    }
                });


                sinon.stub(alertify,"success",function(){});
                scope.openAddRouteModal();
                scope.$digest();
            });

            it("should call Route.for once", function () {
                expect(Route.for.callCount).to.be.equal(2);
            });
            it("should call alertify.success once", function () {
                expect(alertify.success.callCount).to.be.equal(1);
            });
        });

        describe("unsuccesfully",function(){
            beforeEach(function(){
                sinon.stub(Route, "for", function () {
                    return {
                        getList: function () {
                            return $q(function (resolve) {
                                resolve([generateFakeRoute(), generateFakeRoute()]);
                            });
                        },
                        post: function () {
                            return $q(function (resolve,reject) {
                                reject(route);
                            });
                        }
                    }
                });
                sinon.stub(alertify,"error",function(){});
                scope.openAddRouteModal();
                scope.$digest();
            });

            it("should call Route.for once", function () {
                expect(Route.for.callCount).to.be.equal(2);
            });
            it("should call alertify.error once", function () {
                expect(alertify.error.callCount).to.be.equal(1);
            });
        });

    });
});