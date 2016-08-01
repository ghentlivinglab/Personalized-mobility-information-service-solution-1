/**
 * Created by lukas on 4/03/2016.
 */



describe("OperatorEventDetailCtrl", function () {
    beforeEach(module("mobiliteit"));

    var OperatorEventDetailCtrl;
    var scope;
    var Event;
    var EventType;
    var $state;
    var $q;
    var Restangular;
    var alertify;

    beforeEach(inject(function (_Event_, _EventType_, _alertify_, $rootScope, $controller, _$q_, _$state_, _Restangular_, $stateParams) {

        Event = _Event_;
        EventType = _EventType_;
        alertify = _alertify_;
        Restangular = _Restangular_;
        $q = _$q_;
        $state = _$state_;

        $stateParams.event_id = faker.random.uuid();

        sinon.stub(Event, "one", function (id) {
            return {
                get: function () {
                    return $q(function (resolve) {
                        var event = generateFakeEvent(false);
                        event = setEventLinksAttribute(event, id);
                        resolve(event);
                    });
                }
            }
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

        scope = $rootScope.$new();
        OperatorEventDetailCtrl = $controller('OperatorEventDetailCtrl', {$scope: scope});
    }));

    describe("show the event", function () {
        beforeEach(function () {
            scope.$digest();
        });

        it("should call Event.one once", function () {
            expect(Event.one.callCount).to.be.equal(1);
        });
    });

    describe("edit and save a event", function () {
        var event;

        beforeEach(function () {
            event = Restangular.restangularizeElement(null, generateFakeEvent(true), "event");
            event.coordinates.lat = parseFloat(faker.address.longitude())
        });

        describe("successfully", function () {
            beforeEach(function () {
                sinon.stub(event, "put", function () {
                    return $q(function (resolve) {
                        resolve(event);
                    });
                });

                sinon.stub(alertify, "success", function () {

                });

                scope.save(event);
                scope.$digest();
            });

            it("should call event.put once", function () {
                expect(event.put.callCount).to.be.equal(1);
            });

            it("should show a success message", function () {
                expect(alertify.success.callCount).to.be.equal(1);
            });

        });

        describe("failiure", function () {
            beforeEach(function () {
                sinon.stub(event, "put", function () {
                    return $q(function (resolve, reject) {
                        reject(event);
                    });
                });

                sinon.stub(alertify, "error", function () {

                });

                scope.save(event);
                scope.$digest();
            });

            it("should call event.put once", function () {
                expect(event.put.callCount).to.be.equal(1);
            });

            it("should show an error message", function () {
                expect(alertify.error.callCount).to.be.equal(1);
            });

        });


    });

});