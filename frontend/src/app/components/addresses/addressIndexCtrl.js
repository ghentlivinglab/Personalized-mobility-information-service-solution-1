/**
 * Created by Lukas on 27/04/2016.
 */

angular.module("mobiliteit")
    .controller("AddressIndexCtrl",
        ['$scope', 'User', "AuthService", "alertify", "$uibModal",
            "PointOfInterest", "uiGmapGoogleMapApi",
            function ($scope, User, AuthService, alertify, $uibModal, PointOfInterest, uiGmapGoogleMapApi) {

                User.one(AuthService.getCurrentUserID()).get()
                    .then(function (user) {
                        $scope.user = user;
                        PointOfInterest.for(user).getList()
                            .then(function (pois) {
                                $scope.points_of_interests = pois;
                            });
                    });

                $scope.togglePoiActive = function (poi) {
                    poi.active = !poi.active;
                    poi.put()
                        .catch(function () {
                            alertify.error("Er is een fout opgetreden.");
                        });
                };

                $scope.removePOI = function (poi) {
                    alertify.okBtn("Ja").cancelBtn("Annuleren")
                        .confirm("Bent u zeker dat u dit adres wilt verwijderen?",
                            function () {
                                poi.remove()
                                    .then(function () {
                                        $scope.points_of_interests.splice($scope.points_of_interests.indexOf(poi), 1);
                                        alertify.success("Adres verwijderd");
                                    })
                                    .catch(function () {
                                        alertify.error("Er is een fout opgetreden.");
                                    });
                            });
                };

                $scope.openNewPOIModal = function () {
                    var poi = {
                        active: true,
                        address: {
                            city: "Gent",
                            postal_code: "9000",
                            country: "be"
                        },
                        radius: 100,
                        notify: {}
                    };
                    var modal = $scope.openPOIModal(poi);
                    modal.result.then(function (poi) {
                        //Get coordinates
                        uiGmapGoogleMapApi.then(function (maps) {
                            var geocoder = new maps.Geocoder();
                            var addressString = poi.address.housenumber + "+" + poi.address.street + "+" + poi.address.postal_code + "+" + poi.address.city;
                            geocoder.geocode({'address': addressString}, function (results, status) {
                                var lat = results[0].geometry.location.lat();
                                var lng = results[0].geometry.location.lng();

                                poi.address.coordinates = {
                                    lat: lat,
                                    lon: lng
                                };

                                PointOfInterest.for($scope.user)
                                    .post(poi)
                                    .then(function (poi) {
                                        $scope.points_of_interests.push(poi);
                                        alertify.success("Het adres is toegevoegd");
                                    });
                            });
                        });
                    });
                };

                $scope.editPOI = function (poi) {
                    var modal = $scope.openPOIModal(poi);
                    modal.result.then(function (poi) {
                        //Get coordinates
                        uiGmapGoogleMapApi.then(function (maps) {
                            var geocoder = new maps.Geocoder();
                            var addressString = poi.address.housenumber + "+" + poi.address.street + "+" + poi.address.postal_code + "+" + poi.address.city;
                            geocoder.geocode({'address': addressString}, function (results, status) {
                                var lat = results[0].geometry.location.lat();
                                var lng = results[0].geometry.location.lng();

                                poi.address.coordinates = {
                                    lat: lat,
                                    lon: lng
                                };

                                poi.put().then(function () {
                                    alertify.success("Het adres is aangepast");
                                })
                            });
                        });
                    });
                };

                $scope.openPOIModal = function (poi) {
                    return $uibModal.open({
                        templateUrl: 'newPOIModal.html',
                        controller: ["$scope", "$uibModalInstance", "poi",
                            function ($scope, $uibModalInstance, poi) {
                                $scope.poi = poi;
                                $scope.cancel = function () {
                                    $uibModalInstance.dismiss('cancel')
                                };
                                $scope.close = $uibModalInstance.close;
                            }],
                        size: 'lg',
                        backdrop: 'static',
                        keyboard: false,
                        resolve: {
                            poi: poi
                        }
                    });
                };
            }]);