describe("operatorEventIndexCtrl", function () {
    beforeEach(module("mobiliteit"));
    var OperatorEventIndexCtrl;
    var scope;
    var Event;
    var EventType;
    var $state;
    var $q;
    var Restangular;
    var alertify;
    var $uibModal;


    beforeEach(inject(function (_Event_, _EventType_, _alertify_, $rootScope, $controller, _$q_, _$state_, _Restangular_, _$uibModal_) {
        Event = _Event_;
        EventType = _EventType_;
        alertify = _alertify_;
        Restangular = _Restangular_;
        $q = _$q_;
        $state = _$state_;
        $uibModal= _$uibModal_;

        sinon.stub(Event, "getList", function () {
            return $q(function (resolve) {
                var events = [];
                // Generate 10 fake events
                for (var i = 0; i < 10; i++) {
                    events[i] = generateFakeEvent(true);
                }
                resolve(events);

            })
        });

        sinon.stub(EventType, "getList", function () {
            return $q(function (resolve) {
                var eventtypes = [];
                // Generate 5 fake eventtypes
                for (var i = 0; i < 5; i++) {
                    eventtypes[i] = generateFakeEventType(true);
                }
                resolve(eventtypes);
            })
        });

        sinon.stub($uibModal, 'open', function () {
            return {
                result: $q(function (resolve) {
                    resolve(generateFakeEvent(false));
                })
            }
        });


        scope = $rootScope.$new();
        OperatorEventIndexCtrl = $controller('OperatorEventIndexCtrl', {$scope: scope});
    }));

    describe("show all events", function () {
        beforeEach(function () {
            scope.$digest();
        });

        it("should call Event.getList() once", function () {
            expect(Event.getList.callCount).to.be.equal(1);
        });
    });


    describe("adding an event", function () {
        var newEvent;
        beforeEach(function () {
            newEvent = {
                description: "description",
                event_type: {
                    type: "onweer"
                },
                coordinates: {
                    lat: parseFloat(faker.address.latitude()),
                    lon: parseFloat(faker.address.longitude())
                }
            };
        });

        describe("Successful added", function () {
            beforeEach(function () {
                sinon.stub(Event, 'post', function () {
                    return $q(function (resolve) {
                        var eventObject = Restangular.restangularizeElement(null, newEvent, 'event');
                        resolve(eventObject);
                    });
                });

                scope.openModal();
                // Need to run digest to resolve promises
                scope.$digest();
            });

            it('should have called Event.post once', function () {
                expect(Event.post.callCount).to.equal(1);
            });
        });

        describe("failiure", function () {
            beforeEach(function () {
                sinon.stub(Event, "post", function () {
                    return $q(function (resolve, reject) {
                        reject(event);
                    });
                });

                sinon.stub(alertify, "error", function () {

                });

                scope.openModal();
                scope.$digest();
            });

            it("should call Event.post once", function () {
                expect(Event.post.callCount).to.be.equal(1);
            });

            it("should show an error message", function () {
                expect(alertify.error.callCount).to.be.equal(1);
            });
        });
    });
});