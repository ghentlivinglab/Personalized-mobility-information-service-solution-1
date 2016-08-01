/**
 * Created by thibault on 2/27/16.
 */

function generateFakeAddress() {
    return {
        street: faker.address.streetName(),
        housenumber: "" + faker.random.number(),
        city: faker.address.city(),
        country: faker.address.country(),
        postal_code: faker.address.zipCode(),
        coordinates: {
            lat: parseFloat(faker.address.latitude()),
            lon: parseFloat(faker.address.longitude())
        }
    }
}

function generateFakeRoute() {
    return {
        waypoints: [
            {
                lat: parseFloat(faker.address.latitude()),
                lon: parseFloat(faker.address.longitude())
            }
        ],
        transportation_types: faker.random.arrayElement(POSSIBLE_TRANSPORTATION_TYPES),
        notify_for_event_types: [
            generateFakeEventType()
        ]
    };
}

function generateFakeTravel(generate_id) {
    var travel = {
        //name: faker.lorem.words().join(" "),
        name: 'Travelll',
        routes: [generateFakeRoute()],
        date: faker.date.past(),
        time_interval: [
            faker.random.number(23) + ":" + faker.random.number(59),
            faker.random.number(23) + ":" + faker.random.number(59)
        ],
        startpoint: generateFakeAddress(),
        endpoint: generateFakeAddress(),
        is_arrival_time: faker.random.boolean(),
        recurring: [
            faker.random.boolean(),
            faker.random.boolean(),
            faker.random.boolean(),
            faker.random.boolean(),
            faker.random.boolean(),
            faker.random.boolean(),
            faker.random.boolean()
        ]
    };
    if (generate_id) {
        travel.id = faker.random.uuid();
    }
    return travel;
}

function verifyTravelPromiseAttributes(promise) {
    promise.should.eventually.have.property("id").that.is.a("string");
    promise.should.eventually.have.property("date").that.is.a("date");
    promise.should.eventually.have.property("time_interval").that.is.a("array");
    promise.should.eventually.have.property("is_arrival_time").that.is.a("boolean");
    promise.should.eventually.have.property("recurring").that.is.a("array").that.has.lengthOf(7);
}

describe('Travel', function () {

    beforeEach(module('mobiliteit'));

    var Travel;
    var $httpBackend;
    var Restangular;

    beforeEach(inject(function (_Travel_, _$httpBackend_, _Restangular_) {
        Travel = _Travel_;
        $httpBackend = _$httpBackend_;
        Restangular = _Restangular_;
    }));

    describe('getList', function () {
        describe('Normal API response', function () {
            var promise;
            var user;
            var travels = [];


            beforeEach(function () {
                user = Restangular.restangularizeElement(null, generateFakeUser(true), 'user');
                // Generate 10 fake travels
                for (var i = 0; i < 10; i++) {
                    travels[i] = generateFakeTravel(true);
                }

                $httpBackend.expectGET('/api/user/' + user.id + '/travel/')
                    .respond(200, travels);
                promise = Travel.for(user).getList();
                $httpBackend.flush();
            });

            it('should complete', function () {
                promise.should.be.resolved;
            });

            it('should be the same length as the response', function () {
                promise.should.eventually.have.lengthOf(travels.length);
            });

            it('each element should have the id property', function () {
                promise.should.eventually.all.have.property("id");
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
            var user;
            var travel;

            beforeEach(function () {
                user = Restangular.restangularizeElement(null, generateFakeUser(true), 'user');
                travel = generateFakeTravel(true);

                $httpBackend.expectGET('/api/user/' + user.id + "/travel/" + travel.id + "/")
                    .respond(200, travel);

                promise = Travel.for(user).one(travel.id).get();

                $httpBackend.flush();
            });

            it('should complete', function () {
                promise.should.eventually.be.fulfilled;
            });

            it('should return a travel with the same ID as requested', function () {
                promise.should.eventually.have.property("id").that.equals(travel.id);
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
            var user;
            var travel;

            beforeEach(function () {
                user = Restangular.restangularizeElement(null, generateFakeUser(true), 'user');
                travel = generateFakeTravel(true);

                $httpBackend.expectPOST('/api/user/' + user.id + "/travel/")
                    .respond(201, travel);

                promise = Travel.for(user).post(travel);

                $httpBackend.flush();
            });

            it('should complete', function () {
                promise.should.eventually.be.fulfilled;
            });

            it('should return a route with an ID', function () {
                promise.should.eventually.have.property("id");
            });

            it('should have correct attributes', function () {
                verifyTravelPromiseAttributes(promise);
            });

            afterEach(function () {
                $httpBackend.verifyNoOutstandingExpectation();
                $httpBackend.verifyNoOutstandingRequest();
            });

        });

        describe("Travel already exists", function () {
            var travel;
            var user;
            var promise;

            beforeEach(function () {
                user = Restangular.restangularizeElement(null, generateFakeUser(true), 'user');
                travel = generateFakeTravel(false);

                $httpBackend.expectPOST('/api/user/' + user.id + "/travel/")
                    .respond(409, {
                        code: 409,
                        message: "Travel already exists",
                        fields: []
                    });

                promise = Travel.for(user).post(travel);

                $httpBackend.flush();
            });

            it("Should return a 409 error", function () {
                promise.should.eventually.be.fulfilled;
                promise.should.eventually.be.rejected;
                promise.should.eventually.have.property("code").that.is.equal(409);
            })
        });
    });


    describe('update', function () {
        describe('Normal API response', function () {
            var promise;
            var user;
            var travel;

            beforeEach(function () {
                user = Restangular.restangularizeElement(null, generateFakeUser(true), 'user');
                travel = Restangular.restangularizeElement(user, generateFakeTravel(true), 'travel');

                travel.is_arrival_time = !travel.is_arrival_time;

                $httpBackend.expectPUT('/api/user/' + user.id + "/travel/" + travel.id + "/")
                    .respond(200, travel);

                promise = travel.put();

                $httpBackend.flush();
            });

            it('should complete', function () {
                promise.should.eventually.be.fulfilled;
            });

            it("should update the value", function () {
                promise.should.eventually.have.property("is_arrival_time").that.equals(travel.is_arrival_time);
            });

            it('should have correct attributes', function () {
                verifyTravelPromiseAttributes(promise);
            });

            afterEach(function () {
                $httpBackend.verifyNoOutstandingExpectation();
                $httpBackend.verifyNoOutstandingRequest();
            });

        });

        describe("Travel already exists", function () {
            var travel;
            var user;
            var promise;

            beforeEach(function () {
                user = Restangular.restangularizeElement(null, generateFakeUser(true), 'user');
                travel = Restangular.restangularizeElement(user, generateFakeTravel(true), 'travel');

                $httpBackend.expectPUT('/api/user/' + user.id + "/travel/" + travel.id + "/")
                    .respond(409, {
                        code: 409,
                        message: "Travel already exists",
                        fields: []
                    });

                promise = travel.put();

                $httpBackend.flush();
            });

            it("Should return a 409 error", function () {
                promise.should.eventually.be.fulfilled;
                promise.should.eventually.be.rejected;
                promise.should.eventually.have.property("code").that.is.equal(409);
            })
        });
    });


    describe('delete', function () {
        describe('Normal API response', function () {
            var promise;
            var user;
            var travel;

            beforeEach(function () {
                user = Restangular.restangularizeElement(null, generateFakeUser(true), 'user');
                travel = Restangular.restangularizeElement(user, generateFakeTravel(true), 'travel');

                $httpBackend.expectDELETE('/api/user/' + user.id + "/travel/" + travel.id + "/")
                    .respond(204, null);

                promise = travel.remove();

                $httpBackend.flush();
            });

            it('should complete', function () {
                promise.should.eventually.be.fulfilled;
            });

            it("should return nothing", function () {
                promise.should.eventually.equal(undefined);
            });

            afterEach(function () {
                $httpBackend.verifyNoOutstandingExpectation();
                $httpBackend.verifyNoOutstandingRequest();
            });

        });

        describe("Travel does not exist", function () {
            var travel;
            var user;
            var promise;

            beforeEach(function () {
                user = Restangular.restangularizeElement(null, generateFakeUser(true), 'user');
                travel = Restangular.restangularizeElement(user, generateFakeTravel(true), 'travel');

                travel.id = "non_existing_travel_id";

                $httpBackend.expectPUT('/api/user/' + user.id + "/travel/" + travel.id + "/")
                    .respond(404, {
                        code: 404,
                        message: "Travel does not exist",
                        fields: []
                    });

                promise = travel.put();

                $httpBackend.flush();
            });

            it("Should return a 404 error", function () {
                promise.should.eventually.be.fulfilled;
                promise.should.eventually.be.rejected;
                promise.should.eventually.have.property("code").that.is.equal(404);
            })
        });

    });


});