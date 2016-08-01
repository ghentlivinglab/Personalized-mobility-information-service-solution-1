/**
 * Created by Lukas on 9/05/2016.
 */


describe("EditUserProfileFormDirective", function () {

    beforeEach(module('mobiliteit'));

    var innerScope, outerScope, element;

    beforeEach(inject(function ($rootScope, $compile ) {

        var outerelement = angular.element('<form name="editUserProfileForm"><edit-user-profile-form user="user"></edit-user-profile-form></form>');
        element = outerelement.find('edit-user-profile-form');
        outerScope = $rootScope;
        $compile(outerelement)(outerScope);
        innerScope = element.scope();

    }));

    describe("After filling in the form", function () {
        beforeEach(function () {

            innerScope.$digest();
            var user = generateFakeUser(false);

            innerScope.$apply(function () {
                innerScope.user = user;
            });
        });

        it('should render a valid form', function () {
            expect(innerScope.editUserProfileForm.$valid).to.equal(true);
        });
    });
});