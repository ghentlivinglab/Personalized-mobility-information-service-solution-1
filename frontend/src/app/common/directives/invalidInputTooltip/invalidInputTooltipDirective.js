/**
 * Created by thibault on 3/15/16.
 */

angular.module("mobiliteit").directive("invalidInputTooltip", ["$compile", function ($compile) {
    return {
        restrict: 'A',
        scope: {
            invalidInputTooltip: "@",
            name: "@"
        },
        terminal: true,
        replace: false,
        priority: -1000,
        require: ['^form', 'ngModel'],
        link: function (scope, element, attr, require_controllers) {
            scope.form = require_controllers[0];

            var invalidAndDirty = "!form." + scope.name + ".$valid" + " && " + "form." + scope.name + ".$touched";
            element.attr("uib-tooltip", "{{invalidInputTooltip}}");
            element.attr("tooltip-enable", invalidAndDirty);
            element.attr("tooltip-is-open", invalidAndDirty);
            element.attr("tooltip-trigger", "none");

            element.removeAttr("invalid-input-tooltip");
            //element.removeAttr("data-invalid-input-tooltip");

            $compile(element)(scope);
        }
    }
}]);