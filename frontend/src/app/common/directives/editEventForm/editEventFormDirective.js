/**
 * Created by lukas on 3/03/2016.
 */

angular.module("mobiliteit").directive("editEventForm", function () {
    return {
        restrict: "E",
        scope: {
            event: '='
        },
        require: '^form',
        templateUrl: 'app/common/directives/editEventForm/editEventFormView.html',
        link: function (scope, element, attr, ctrl) {
            scope.form = ctrl;
        },
        controller: ["$scope", "TransportationType", "EventType",
            function ($scope, TransportationType, EventType) {
                // If we're creating a new event, set some defaults
                $scope.event = $scope.event || {
                        active: true,
                        source: {
                            name: "Operator",
                            icon_url: "http://icons.iconarchive.com/icons/icons8/ios7/128/Industry-Manual-icon.png",
                            type: "EXTERNAL"
                        },
                        jams: [],
                        type: {
                            type: "ACCIDENT_MAJOR"
                        }
                    };
                

                // Need this in the view
                TransportationType.getList().then(function (types) {
                    $scope.transportationTypes = types;
                });

                // Need this in the view
                EventType.getList().then(function (data) {
                    $scope.eventtypes = data;
                });

                // Initialize the google maps
                $scope.map = {
                    center: {
                        latitude: 51.058353,
                        longitude: 3.730848
                    },
                    markers: [],
                    zoom: 9,
                    events: {
                        click: function (mapModel, eventName, originalEventArgs) {
                            $scope.$apply(function () {
                                var e = originalEventArgs[0];
                                var latitude = e.latLng.lat();
                                var longitude = e.latLng.lng();
                                $scope.event.coordinates = {
                                    lat: latitude,
                                    lon: longitude
                                };
                                $scope.map.markers = [
                                    {
                                        id: 1,
                                        coords: {
                                            latitude: latitude,
                                            longitude: longitude
                                        }
                                    }
                                ]
                            });
                        }
                    }
                };

                $scope.$watch("event", function() {
                    // Show the marker if the event already has coordinates
                    if ($scope.event.coordinates != undefined) {
                        $scope.map.markers = [
                            {
                                id: 1,
                                coords: {
                                    latitude: $scope.event.coordinates.lat,
                                    longitude: $scope.event.coordinates.lon
                                }
                            }
                        ]
                    }
                }, false);
            }]
    }
});