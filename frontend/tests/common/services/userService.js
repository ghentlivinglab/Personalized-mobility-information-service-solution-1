/**
 * Created by thibault on 2/24/16.
 */
faker.locale = "nl";
chai.should();

function generateFakeUser(generate_id) {
    var user = {
        first_name: faker.name.firstName(),
        last_name: faker.name.lastName(),
        email: faker.internet.email(),
        cell_number: faker.phone.phoneNumber(),
        gender: faker.random.arrayElement(["m", "f"]),
        points_of_interest: [
            {
                address: {
                    street: faker.address.streetName(),
                    housenumber: "" + faker.random.number(),
                    city: faker.address.city(),
                    country: faker.address.country(),
                    postal_code: faker.address.zipCode(),
                    coordinates: {
                        lat: parseFloat(faker.address.latitude()),
                        lon: parseFloat(faker.address.longitude())
                    }
                },
                //name: faker.lorem.words().join(" "),
                name: 'lorem ipsum',
                is_home_address: faker.random.boolean(),
                radius: faker.random.number(),
                active: faker.random.boolean()
            }
        ],
        mute_notifications: faker.random.boolean()
    };
    if (generate_id) {
        user.id = faker.random.uuid();
        user = setUserLinksAttribute(user);
    }
    return user;
}

function setUserLinksAttribute(user, id) {
    if (id) {
        user.id = id;
    }
    user.links = [
        {
            rel: "self",
            href: "/user/" + user.id + "/"
        }
    ];
    return user;
}

function verifyUserPromiseAttributes(promise) {
    promise.should.eventually.have.property("id").that.is.a("string");
    promise.should.eventually.have.property("links").that.is.a("array");
    promise.should.eventually.have.property("first_name").that.is.a("string");
    promise.should.eventually.have.property("last_name").that.is.a("string");
    promise.should.eventually.have.property("email").that.is.a("string");
    promise.should.eventually.have.property("gender").that.is.a("string");
    promise.should.eventually.have.property("cell_number").that.is.a("string");
    promise.should.eventually.have.property("mute_notifications").that.is.a("boolean");
    promise.should.eventually.have.property("points_of_interest").that.is.a("array");
}

describe('User', function () {

    beforeEach(module('mobiliteit'));

    var User;
    var $httpBackend;
    var Restangular;

    beforeEach(inject(function (_User_, _$httpBackend_, _Restangular_) {
        User = _User_;
        $httpBackend = _$httpBackend_;
        Restangular = _Restangular_;
    }));

    describe('getList', function () {
        describe('Normal API response', function () {
            var promise;
            var users = [];
            // Generate 10 fake users
            for (var i = 0; i < 10; i++) {
                users[i] = generateFakeUser(true);
            }

            beforeEach(function () {
                $httpBackend.expectGET('/api/user/')
                    .respond(200, users);
                promise = User.getList();
                $httpBackend.flush();
            });

            it('should complete', function () {
                promise.should.be.resolved;
            });

            it('should be the same length as the response', function () {
                promise.should.eventually.have.lengthOf(users.length);
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

    describe('create', function () {
        describe('Normal API response', function () {
            var promise;
            var user = generateFakeUser(false);

            beforeEach(function () {
                var userCopy = Restangular.restangularizeElement(null, user, 'user');
                userCopy = setUserLinksAttribute(userCopy, faker.random.uuid());

                $httpBackend.expectPOST('/api/user/')
                    .respond(201, userCopy);

                promise = User.post(user);

                $httpBackend.flush();
            });

            it('should complete', function () {
                promise.should.eventually.be.fulfilled;
            });

            it('should return a user with an ID', function () {
                promise.should.eventually.have.property("id");
            });

            it('should have correct attributes', function () {
                verifyUserPromiseAttributes(promise);
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
            var user = generateFakeUser(true);

            beforeEach(function () {
                $httpBackend.expectGET('/api/user/' + user.id + "/")
                    .respond(200, user);

                promise = User.one(user.id).get();

                $httpBackend.flush();
            });

            it('should complete', function () {
                promise.should.eventually.be.fulfilled;
            });

            it('should return a user with the same ID as requested', function () {
                promise.should.eventually.have.property("id").that.equals(user.id);
            });

            it('should have correct attributes', function () {
                verifyUserPromiseAttributes(promise);
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
            var user = generateFakeUser(true);

            beforeEach(function () {
                var userObject = Restangular.restangularizeElement(null, user, 'user');

                $httpBackend.expectPUT('/api/user/' + user.id + "/")
                    .respond(200, user);

                promise = userObject.put();

                $httpBackend.flush();
            });

            it('should complete', function () {
                promise.should.eventually.be.fulfilled;
            });

            it("should update the value", function () {
                promise.should.eventually.have.property("first_name").that.equals(user.first_name);
            });

            it('should have correct attributes', function () {
                verifyUserPromiseAttributes(promise);
            });

            afterEach(function () {
                $httpBackend.verifyNoOutstandingExpectation();
                $httpBackend.verifyNoOutstandingRequest();
            });

        });

    });


    describe('delete', function () {
        describe('Normal API response', function () {
            var promise;
            var user = generateFakeUser(true);

            beforeEach(function () {
                var userObject = Restangular.restangularizeElement(null, user, 'user');

                $httpBackend.expectDELETE('/api/user/' + user.id + "/")
                    .respond(204, null);

                promise = userObject.remove();

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

    });


});