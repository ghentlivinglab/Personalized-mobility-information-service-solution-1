/**
 * Created by thibault on 4/13/16.
 */

describe("ViewEventCtrl", function () {
    beforeEach(module("mobiliteit"));

    var scope, ViewEventCtrl, Event;

    beforeEach(inject(function ($rootScope, $controller, $q, _Event_, $stateParams) {
        $stateParams.event_id = 1;
        Event = _Event_;

        sinon.stub(_Event_, 'one', function () {
            return {
                get: function () {
                    return $q(function (resolve) {
                        resolve(generateFakeEvent(true));
                    })
                }
            }
        });

        scope = $rootScope.$new();
        ViewEventCtrl = $controller("ViewEventCtrl", {$scope: scope});
    }));

    describe('On load', function () {
        beforeEach(function () {
            scope.$digest();
        });

        it('should GET the event', function () {
            expect(Event.one).to.be.called;
        })
    });

});