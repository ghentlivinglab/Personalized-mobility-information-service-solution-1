/**
 * Created by thibault on 2/24/16.
 */

function generateFakeEvent(generate_id) {
    var event = {
        coordinates: {
            lat: parseFloat(faker.address.latitude()),
            lon: parseFloat(faker.address.longitude())
        },
        active: faker.random.boolean(),
        publication_time: faker.date.past().toISOString(),
        last_edit_time: faker.date.past().toISOString(),
        //description: faker.lorem.paragraph(),
        description: "lorem ipsum dolor sit amet",
        jams: faker.random.boolean() ? [] : [{
            points:[{
                lat: parseFloat(faker.address.latitude()),
                lon: parseFloat(faker.address.longitude())
            }],
            speed: faker.random.number(),
            delay: faker.random.number()
        }],
        source: {
            name: "Unittests",
            icon_url: faker.image.imageUrl()
        }
    };
    if (generate_id) {
        event.id = faker.random.uuid();
        event = setEventLinksAttribute(event);
    }
    return event;
}

function setEventLinksAttribute(event, id) {
    if (id) {
        event.id = id;
    }
    event.links = [
        {
            ref: "self",
            href: "/event/" + id + "/"
        }
    ];
    return event;
}

function verifyEventPromiseAttributes(promise) {
    promise.should.eventually.have.property("id").that.is.a("string");
    promise.should.eventually.have.property("links").that.is.a("array");
    promise.should.eventually.have.property("coordinates").that.is.a("object");
    promise.should.eventually.have.property("coordinates").that.has.property("lat");
    promise.should.eventually.have.property("coordinates").that.has.property("lon");
    promise.should.eventually.have.property("active").that.is.a("boolean");
    promise.should.eventually.have.property("publication_time").that.is.a("string");
    promise.should.eventually.have.property("last_edit_time").that.is.a("string");
    promise.should.eventually.have.property("description").that.is.a("string");
    promise.should.eventually.have.property("source").that.is.a("object");
    promise.should.eventually.have.property("source").that.has.property("name").that.is.a("string");
    promise.should.eventually.have.property("source").that.has.property("icon_url").that.is.a("string");
}

describe('EventService', function() {

    beforeEach(module('mobiliteit'));

    var Event;
    var $httpBackend;
    var Restangular;

    beforeEach(inject(function(_Event_, _$httpBackend_, _Restangular_) {
        Event = _Event_;
        $httpBackend = _$httpBackend_;
        Restangular = _Restangular_;
    }));

    describe('getList', function () {
        describe('Normal API response', function () {
            var promise;
            var events = [];
            // Generate 10 fake events
            for (var i = 0; i < 10; i++) {
                events[i] = generateFakeEvent();
            }

            beforeEach(function () {
                $httpBackend.expectGET('/api/event/')
                    .respond(200, events);
                promise = Event.getList();
                $httpBackend.flush();
            });

            it('should complete', function () {
                promise.should.be.resolved;
            });

            it('should be the same length as the response', function () {
                promise.should.eventually.have.lengthOf(events.length);
            });

            afterEach(function () {
                $httpBackend.verifyNoOutstandingExpectation();
                $httpBackend.verifyNoOutstandingRequest();
            });

        });

    });

    describe('create', function () {
        describe('Normal API response', function () {
            var promise;
            var event = generateFakeEvent(false);
            var eventCopy;
            beforeEach(function () {
                eventCopy = Restangular.restangularizeElement(null, event, 'event');
                eventCopy = setEventLinksAttribute(eventCopy, faker.random.uuid());

                $httpBackend.expectPOST('/api/event/')
                    .respond(201, eventCopy);

                promise = Event.post(event);

                $httpBackend.flush();
            });

            it('should complete', function () {
                promise.should.eventually.be.fulfilled;
            });

            it('should return an event with an ID', function () {
                promise.should.eventually.have.property("id").equal(eventCopy.id);
            });

            it('should return an event with correct attributes', function () {
                verifyEventPromiseAttributes(promise);
            });

            afterEach(function () {
                $httpBackend.verifyNoOutstandingExpectation();
                $httpBackend.verifyNoOutstandingRequest();
            });

        });

    });

    describe('get single', function () {
        describe('Normal API response', function () {
            var promise;
            var event = generateFakeEvent(true);

            beforeEach(function () {
                $httpBackend.expectGET('/api/event/' + event.id + "/")
                    .respond(200, event);

                promise = Event.one(event.id).get();

                $httpBackend.flush();
            });

            it('should complete', function () {
                promise.should.eventually.be.fulfilled;
            });

            it('should return an event with the same ID as requested', function () {
                promise.should.eventually.have.property("id").that.equals(event.id);
            });

            it('should return an event with correct attributes', function () {
                verifyEventPromiseAttributes(promise);
            });

            afterEach(function () {
                $httpBackend.verifyNoOutstandingExpectation();
                $httpBackend.verifyNoOutstandingRequest();
            });

        });

    });

    describe('update', function () {
        describe('Normal API response', function () {
            var promise;
            var event = generateFakeEvent(true);

            beforeEach(function () {
                var eventObject = Restangular.restangularizeElement(null, event, 'event');

                eventObject.active = !eventObject.active;

                $httpBackend.expectPUT('/api/event/' + event.id + "/")
                    .respond(200, event);

                promise = eventObject.put();

                $httpBackend.flush();
            });

            it('should complete', function () {
                promise.should.eventually.be.fulfilled;
            });

            it("should update the value", function () {
                promise.should.eventually.have.property("active").that.equals(event.active);
            });

            it('should return an event with correct attributes', function () {
                verifyEventPromiseAttributes(promise);
            });

            afterEach(function () {
                $httpBackend.verifyNoOutstandingExpectation();
                $httpBackend.verifyNoOutstandingRequest();
            });

        });

    });


});