/**
 * Created by thibault on 4/12/16.
 */

function generateFakeRoute(generateId) {
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

describe("Route", function () {
    beforeEach(module("mobiliteit"));

    var Route;
    var travel;

    beforeEach(inject(function (_Route_, Restangular) {
        Route = _Route_;
        travel = Restangular.restangularizeElement(null, generateFakeTravel(true), "travel");
    }));

    it("should give all required methods", function () {
        expect(Route.for).to.be.defined;
        expect(Route.for(travel).getList).to.be.defined;
        expect(Route.for(travel).get).to.be.defined;
        expect(Route.for(travel)).to.be.defined;
    })
});