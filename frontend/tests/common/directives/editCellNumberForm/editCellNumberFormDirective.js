/**
 * Created by Lukas on 9/05/2016.
 */

describe("EditCellNumberFormDirective", function () {

    beforeEach(module('mobiliteit'));

    var innerScope, outerScope, element;

    beforeEach(inject(function ($rootScope, $compile ) {

        var outerelement = angular.element('<form name="editCellNumberForm"><edit-cell-number-form cellNumber="cellNumber"></edit-cell-number-form></form>');
        element = outerelement.find('edit-cell-Number-form');
        outerScope = $rootScope;
        $compile(outerelement)(outerScope);
        innerScope = element.scope();

    }));

    describe("After filling in the form", function () {
        beforeEach(function () {

            innerScope.$digest();
            var cellNumber = 12341234;

            innerScope.$apply(function () {
                innerScope.cellNumber = cellNumber;
            });
        });

        it('should render a valid form', function () {
            expect(innerScope.editCellNumberForm.$valid).to.equal(true);
        });
    });
});