/**
 * Created by thibault on 2/27/16.
 */

var POSSIBLE_TRANSPORTATION_TYPES = ["car", "bike"];

function generateFakeEventType() {
    return {
        id: faker.random.uuid(),
        //type: faker.lorem.words().join(" "),
        type: "testtype",
        relevant_for_transportation_types: [
            faker.random.arrayElement(POSSIBLE_TRANSPORTATION_TYPES),
            faker.random.arrayElement(POSSIBLE_TRANSPORTATION_TYPES),
            faker.random.arrayElement(POSSIBLE_TRANSPORTATION_TYPES)
        ]
    }
}

describe('EventService', function() {

    beforeEach(module('mobiliteit'));

    var EventType;
    var $httpBackend;
    var Restangular;

    beforeEach(inject(function(_EventType_, _$httpBackend_, _Restangular_) {
        EventType = _EventType_;
        $httpBackend = _$httpBackend_;
        Restangular = _Restangular_;
    }));

    describe('getList', function () {
        describe('Normal API response', function () {
            var promise;
            var eventTypes = [];

            // Generate 10 fake EventTypes
            for (var i = 0; i < 10; i++) {
                eventTypes[i] = generateFakeEventType();
            }

            beforeEach(function () {
                $httpBackend.expectGET('/api/eventtype/')
                    .respond(200, eventTypes);
                promise = EventType.getList();
                $httpBackend.flush();
            });

            it('should complete', function () {
                promise.should.be.resolved;
            });

            it('should be the same length as the response', function () {
                promise.should.eventually.have.lengthOf(eventTypes.length);
            });

            it('each element should have correct attributes', function () {
                promise.should.eventually.all.have.property("id");
                promise.should.eventually.all.have.property("type");
                promise.should.eventually.all.have.property("relevant_for_transportation_types");
            });

            afterEach(function () {
                $httpBackend.verifyNoOutstandingExpectation();
                $httpBackend.verifyNoOutstandingRequest();
            });

        });

    });
});