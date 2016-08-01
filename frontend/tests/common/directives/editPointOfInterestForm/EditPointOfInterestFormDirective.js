/**
 * Created by thibault on 4/12/16.
 */

describe("EditPointOfInterestFormDirective", function () {

    beforeEach(module('mobiliteit'));

    var EventType, TransportationType, $q, innerScope, outerScope, element;

    beforeEach(inject(function ($rootScope, $compile, _EventType_, _TransportationType_, _$q_) {
        EventType = _EventType_;
        TransportationType = _TransportationType_;
        $q = _$q_;

        sinon.stub(EventType, "getList", function () {
            return $q(function (resolve) {
                // Generate 5 fake eventtypes
                var event_types = [];
                for (var i = 0; i < 5; i++) {
                    event_types.push(generateFakeEventType());
                }
                resolve(event_types);
            })
        });

        sinon.stub(TransportationType, "getList", function () {
            return $q(function (resolve) {
                resolve(POSSIBLE_TRANSPORTATION_TYPES);
            })
        });
        var outerelement = angular.element('<form name="editPointOfInterestForm"><edit-point-of-interest-form poi="poi"></edit-point-of-interest-form></form>');
        element = outerelement.find('edit-point-of-interest-form');
        outerScope = $rootScope;
        outerScope.poi = undefined;
        $compile(outerelement)(outerScope);
        innerScope = element.scope();

    }));

    describe("After filling in the form", function () {
        beforeEach(function () {

            innerScope.$digest();
            var poi = generateFakePointOfInterest(false);

            innerScope.$apply(function () {
                innerScope.poi = poi;
            });
        });

        it('should render a valid form', function () {
            expect(innerScope.editPointOfInterestForm.$valid).to.equal(true);
        });
    });

    describe("After filling in the form while poi was already filled in", function () {
        beforeEach(function () {
            var poi = generateFakePointOfInterest(false);
            poi.notify_for_event_types = [
                {id: faker.random.uuid(), type: "WEATHERHAZARD", relevant_for_transportation_types: ["car"]},
                {id: faker.random.uuid(), type: "HAZARD_ON_ROAD", relevant_for_transportation_types: ["car"]},
                {id: faker.random.uuid(), type: "JAM", relevant_for_transportation_types: ["car"]},
                {id: faker.random.uuid(), type: "CONSTRUCTION", relevant_for_transportation_types: ["car"]},
                {id: faker.random.uuid(), type: "ROAD_CLOSED_HAZARD", relevant_for_transportation_types: ["car"]},
                {id: faker.random.uuid(), type: "OTHER", relevant_for_transportation_types: ["car"]}
            ];
            innerScope.poi = poi;
            innerScope.$digest();
        });

        it('should render a valid form', function () {
            expect(innerScope.editPointOfInterestForm.$valid).to.equal(true);
        });
    });

    describe("If the POI isn't filled in on creation", function () {
        beforeEach(function () {
            outerScope.poi = undefined;
            innerScope.$digest();
        });

        it('should set a default', function () {
            expect(innerScope.poi).to.not.equal(undefined);
        });
    });

});