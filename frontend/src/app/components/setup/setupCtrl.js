/**
 * Created by thibault on 4/5/16.
 */

function addressToString(address) {
    return address.housenumber + "+" + address.street + "+" + address.postal_code + "+" + address.city
}

angular.module("mobiliteit")
    .controller("SetupCtrl", ["$scope", "uiGmapGoogleMapApi", "$q", "PointOfInterest", "$stateParams", "User", "$state", "alertify",
        function ($scope, uiGmapGoogleMapApi, $q, PointOfInterest, $stateParams, User, $state, alertify) {

            User.one($stateParams.user_id).get()
                .then(function (user) {
                    $scope.user = user;
                });

            $scope.poi = {
                address: {
                    country: "be"
                },
                is_home_address: true,
                name: "Woonplaats",
                active: true,
                radius: 100,
                notify: {
                    email: true,
                    cell_number: false
                }
            };

            $scope.confirm = function () {
                // This is a bit messy, but it works. So leave it.
                var promise = $q(function (resolve, reject) {
                    if ($scope.poi.address.street != undefined) {
                        // The user has entered a POI
                        // First, get the coordinates of the address
                        uiGmapGoogleMapApi.then(function (maps) {
                            function addressToCoordinates(address) {
                                return $q(function (resolve, reject) {
                                    var geocoder = new maps.Geocoder();
                                    geocoder.geocode({'address': address}, function (results, status) {
                                        if (status == maps.GeocoderStatus.OK && results.length > 0) {
                                            resolve({
                                                lat: results[0].geometry.location.lat(),
                                                lon: results[0].geometry.location.lng()
                                            });
                                        } else {
                                            reject();
                                        }
                                    });
                                });
                            }

                            var addressString = addressToString($scope.poi.address);
                            addressToCoordinates(addressString)
                                .then(function (result) {
                                    // So now we got the coordinates, create the POI now
                                    $scope.poi.address.coordinates = result;
                                    PointOfInterest.for($scope.user)
                                        .post($scope.poi)
                                        .then(resolve) // We can continue the signup process here
                                        .catch(function () {
                                            alertify.error("Er is een fout opgetreden.");
                                            reject();
                                        });
                                })
                                .catch(function () {
                                    alertify.alert('We konden geen locatie vinden voor het adres ' + addressString);
                                });
                        });
                    } else {
                        resolve();
                    }
                });

                promise.then(function () {
                        alertify.alert("Uw registratie is gelukt.", function () {
                            $state.go("app.home");
                        });
                    })
                    .catch(function () {
                        alertify.alert("Er is een interne fout opgetreden");
                    });
            }
        }]);