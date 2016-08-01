/**
 * Created by thibault on 4/1/16.
 */

angular.module("mobiliteit")
    .controller("ViewEventCtrl", ["$scope", "Event", "$stateParams", "EventType", "TransportationType",
        function ($scope, Event, $stateParams, EventType, TransportationType) {
            Event.one($stateParams.event_id).get().then(function (r) {
                $scope.event = r;

                // Recenter the map
                $scope.map.center = {
                    latitude: $scope.event.coordinates.lat,
                    longitude: $scope.event.coordinates.lon
                };
                $scope.map.zoom = 17;
                // Place marker
                $scope.map.markers = [
                    {
                        id: 1,
                        coords: {
                            latitude: $scope.event.coordinates.lat,
                            longitude: $scope.event.coordinates.lon
                        }
                    }
                ]
            });
            
            // Initialize the google maps widget
            $scope.map = {
                center: {
                    latitude: 51.058353,
                    longitude: 3.730848
                },
                markers: [],
                zoom: 11
            };
        }]);