/**
 * Created by Lukas on 9/05/2016.
 */


describe("EditAddressFormDirective", function () {

    beforeEach(module('mobiliteit'));

    var innerScope, outerScope, element;

    beforeEach(inject(function ($rootScope, $compile) {

        var outerelement = angular.element('<form name="editAddressForm"><edit-address-form address="address"></edit-address-form></form>');
        element = outerelement.find('edit-address-form');
        outerScope = $rootScope;
        $compile(outerelement)(outerScope);
        outerScope.$digest();
        innerScope = element.isolateScope();
    }));

    describe("After filling in the form", function () {
        var details2;
        beforeEach(function () {

            var address = generateFakeAddress(false);

            innerScope.$apply(function () {
                innerScope.address = address;
            });

            var details = {
                address_components: [
                    {
                        types: ["country"],
                        "short_name": "be"
                    }
                ]
            };

            innerScope.$apply(function () {
                innerScope.details = details;
            });

            details2 = {
                address_components: [
                    {
                        types: ["street_number"],
                        long_name: "12"
                    },
                    {
                        types: ["country"],
                        short_name: "BE"
                    },
                    {
                        types: ["locality"],
                        long_name: "Ghent"
                    },
                    {
                        types: ["route"],
                        long_name: "Dorpstraat"
                    },
                    {
                        types: ["postal_code"],
                        long_name: "9000"
                    }
                ]
            };

            innerScope.$apply(function () {
                innerScope.details = details2;
            });

        });

        it('should set the correct values in $scope.address', function () {
            expect(innerScope.address.country).to.equal("be");
            expect(innerScope.address.street).to.equal("Dorpstraat");
            expect(innerScope.address.city).to.equal("Ghent");
            expect(innerScope.address.housenumber).to.equal("12");
            expect(innerScope.address.postal_code).to.equal("9000");
        });
    });
});