/**
 * Created by thibault on 4/12/16.
 */

function generateFakePointOfInterest(generateId) {
    return {
        id: generateId ? faker.random.uuid() : undefined,
        address: generateFakeAddress(),
        //name: faker.lorem.words().join(' '),
        name: 'tralala',
        radius: faker.random.number(),
        active: faker.random.boolean(),
        notify_for_event_types: [
            generateFakeEventType()
        ]

    }
}

describe("PointOfInterest", function () {
    beforeEach(module("mobiliteit"));

    var PointOfInterest;
    var user;

    beforeEach(inject(function (_PointOfInterest_, Restangular) {
        PointOfInterest = _PointOfInterest_;
        user = Restangular.restangularizeElement(null, generateFakeUser(true), "user");
    }));

    it("should give all required methods", function () {
        expect(PointOfInterest.for).to.be.defined;
        expect(PointOfInterest.for(user).getList).to.be.defined;
        expect(PointOfInterest.for(user).get).to.be.defined;
        expect(PointOfInterest.for(user)).to.be.defined;
    })
});