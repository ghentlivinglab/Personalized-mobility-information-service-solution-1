/**
 * Created by lukas on 11/03/2016.
 */
angular.module("mobiliteit").directive("editAddressForm", function () {
    return {
        restrict: "E",
        scope: {
            address: '='
        },
        require: '^form',
        templateUrl: 'app/common/directives/editAddressForm/editAddressFormView.html',
        link: function (scope, element, attr, ctrl) {
            scope.form = ctrl;
        },
        controller: ["$scope", function ($scope) {

            if (!$scope.address) {
                $scope.address = {
                    country: 'be'
                };
            }

            $scope.optionsaddress = {
                country: $scope.address.country,
                types: 'address'
            };

            $scope.optionscity = {
                country: $scope.address.country,
                types: '(regions)'
            };

            $scope.$watch("details", function (newValue) {
                if (newValue) {
                    for (var i = 0; i < newValue.address_components.length; i++) {
                        var addressType = newValue.address_components[i].types[0];
                        if (addressType == "country") {
                            $scope.address.country = newValue.address_components[i]["short_name"].toLowerCase();
                        }
                        else if (addressType == "locality") {
                            $scope.address.city = newValue.address_components[i]["long_name"];
                        }
                        else if (addressType == "postal_code") {
                            $scope.address.postal_code = newValue.address_components[i]["long_name"];
                        }
                        else if (addressType == "street_number") {
                            $scope.address.housenumber = newValue.address_components[i]["long_name"];
                        }
                        else if (addressType == "route") {
                            $scope.address.street = newValue.address_components[i]["long_name"];
                        }
                    }
                }
            }, true);

            $scope.$watch("address.country", function (newValue, oldValue) {
                if (newValue != undefined) {
                    $scope.optionsaddress.country = newValue;
                    $scope.optionscity.country = newValue;
                }
            }, true);
        }]
    }
});