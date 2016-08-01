/**
 * Created by Lukas on 5/03/2016.
 */


describe("EditEventFormDirective", function () {

    beforeEach(module('mobiliteit'));

    var outerelement;
    var element;
    var outerScope;
    var innerScope;
    var EventType;
    var TransportationType;
    var $q;

    beforeEach(inject(function($rootScope, $compile, _EventType_, _TransportationType_, _$q_) {
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

        outerelement = angular.element('<form name="editEventForm"><edit-event-form event="event"></edit-event-form></form>');
        element = outerelement.find('edit-event-form');
        outerScope = $rootScope;
        outerScope.$digest();

        $compile(outerelement)(outerScope);
        outerScope.$digest();
        innerScope = element.scope();

        innerScope.$digest();
    }));

    describe("incomplete form should not be correct",function(){
        var event;
        beforeEach(function () {
            event = {
                //empty description
                description: "",
                event_type: {
                    type: "onweer"
                },
                coordinates: {
                    lat: parseFloat(faker.address.latitude()),
                    lon: parseFloat(faker.address.longitude())
                },
                active: faker.random.boolean()
            };
            innerScope.event = event;
        });

        it("should be invalid form", function () {
            expect(innerScope.editEventForm.$invalid).to.equal(true);
        });
    });

    describe("after filling out the form correctly", function () {
        var event;
        beforeEach(function () {
            event = {
                coordinates: {
                    lat: parseFloat(faker.address.latitude()),
                    lon: parseFloat(faker.address.longitude())
                },
                active: true,
                description: "Unittest " + faker.random.number(),
                jam: faker.random.boolean() ? undefined : {
                    start_node: {
                        lat: parseFloat(faker.address.latitude()),
                        lon: parseFloat(faker.address.longitude())
                    },
                    end_node: {
                        lat: parseFloat(faker.address.latitude()),
                        lon: parseFloat(faker.address.longitude())
                    },
                    speed: faker.random.number(),
                    delay: faker.random.number()
                },
                source: {
                    name: "Unittests",
                    icon_url: faker.image.imageUrl()
                },
                type: generateFakeEventType(),
                relevant_for_transportation_types: [
                    generateFakeTransportationType()
                ]

            };

            innerScope.$apply(function () {
                innerScope.event = event;
                innerScope.editEventForm.description.$setViewValue("test");
            });
        });

        it("should have a valid form", function () {
            angular.forEach(innerScope.editEventForm.$error.required, function (error) {
                console.log("Required: " + error.$name);
            });
            expect(innerScope.editEventForm.$valid).to.equal(true);
        });

        it("should update the event in the outer scope", function () {
            expect(outerScope.event).to.deep.equal(event);
        });

    })
});