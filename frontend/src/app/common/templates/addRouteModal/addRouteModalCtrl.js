/**
 * Created by Lukas on 28/04/2016.
 */

// helper function to convert addresses to strings which google understands
function addressToString(address) {
    return address.housenumber + "+" + address.street + "+" + address.postal_code + "+" + address.city
}

// Helper function to translate our transportation types to google maps's transportation types

var transportMapper = {
    "BIKE": "BICYCLING",
    "CAR": "DRIVING"
};

function getTravelMode(transportation_type) {
    return transportMapper[transportation_type];
}

angular.module("mobiliteit").controller("AddRouteModalCtrl",
    ["$scope", "$uibModalInstance", "uiGmapGoogleMapApi", "alertify", "$q", "TransportationType", "$uibModal", "travel",
        function ($scope, $uibModalInstance, uiGmapGoogleMapApi, alertify, $q, TransportationType, $uibModal, travel) {

            var weather = [
                "HAZARD_WEATHER_FLOOD",
                "HAZARD_WEATHER_FOG",
                "HAZARD_WEATHER_FREEZING_RAIN",
                "HAZARD_WEATHER_HAIL",
                "HAZARD_WEATHER_HEAT_WAVE",
                "HAZARD_WEATHER_HEAVY_RAIN",
                "HAZARD_WEATHER_HEAVY_SNOW",
                "HAZARD_WEATHER_HURRICANE",
                "HAZARD_WEATHER_MONSOON",
                "HAZARD_WEATHER_TORNADO",
                "WEATHERHAZARD"
            ];
            var hazard = [
                "ACCIDENT_MAJOR",
                "ACCIDENT_MINOR",
                "HAZARD_ON_ROAD",
                "HAZARD_ON_ROAD_CAR_STOPPED",
                "HAZARD_ON_ROAD_CONSTRUCTION",
                "HAZARD_ON_ROAD_ICE",
                "HAZARD_ON_ROAD_LANE_CLOSED",
                "HAZARD_ON_ROAD_OBJECT",
                "HAZARD_ON_ROAD_OIL",
                "HAZARD_ON_ROAD_POT_HOLE",
                "HAZARD_ON_ROAD_ROAD_KILL",
                "HAZARD_ON_SHOULDER",
                "HAZARD_ON_SHOULDER_ANIMALS",
                "HAZARD_ON_SHOULDER_CAR_STOPPED",
                "HAZARD_ON_SHOULDER_MISSING_SIGN"
            ];
            var jam = [
                "JAM_HEAVY_TRAFFIC",
                "JAM_LIGHT_TRAFFIC",
                "JAM_MODERATE_TRAFFIC",
                "JAM_STAND_STILL_TRAFFIC"
            ];
            var construction = [
                "CONSTRUCTION"
            ];

            var closed = [
                "ROAD_CLOSED_CONSTRUCTION",
                "ROAD_CLOSED_EVENT",
                "ROAD_CLOSED_HAZARD"
            ];

            var other = [
                "OTHER"
            ];


            var relevantTypes = [weather, hazard, jam, construction, closed, other];
            $scope.relevantTypesBool = [true, true, true, true, true, true];


            $scope.$watchCollection("relevantTypesBool", function (oldValue, newValue) {
                $scope.updateNotifyForEventTypes();
            });

            $scope.cancel = function () {
                $uibModalInstance.dismiss('cancel');
            };

            $scope.travel = travel;


            $scope.route = {
                active: true,
                notify: {
                    email: false,
                    cell_number: false
                },
                notify_for_event_types: []
            };


            $scope.finish = function () {
                $scope.setWayPoints();
                $uibModalInstance.close($scope.route);
            };


            /// STEP 3
            // Hacky workaround to reload the google maps frame when entering it's step
            TransportationType.getList().then(function (r) {
                $scope.transportationTypes = r;
            });

            var directionsDisplay;
            var directionsService;
            $scope.showMap = false;

            $scope.$watch("route.transportation_type", function (newValue, oldValue) {
                if (newValue) {
                    $scope.enableMap();
                }
                else {
                    $scope.showMap = false;
                }
            });


            $scope.enableMap = function () {

                $scope.showMap = true;
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

                    addressToCoordinates(addressToString($scope.travel.startpoint))
                        .then(function (result) {
                            $scope.travel.startpoint.coordinates = result;
                        })
                        .catch(function () {
                            alertify.alert('We konden geen locatie vinden voor het adres ' + addressToString($scope.travel.startpoint));
                        });
                    addressToCoordinates(addressToString($scope.travel.endpoint))
                        .then(function (result) {
                            $scope.travel.endpoint.coordinates = result;
                        })
                        .catch(function () {
                            alertify.alert('We konden geen locatie vinden voor het adres ' + addressToString($scope.travel.endpoint));
                        });
                    var directions = {
                        origin: addressToString($scope.travel.startpoint),
                        destination: addressToString($scope.travel.endpoint),

                        travelMode: maps.TravelMode[getTravelMode($scope.route.transportation_type)]
                    };

                    directionsDisplay = directionsDisplay || new maps.DirectionsRenderer;
                    directionsService = directionsService || new maps.DirectionsService;
                    // maakt de route verplaatsbaar
                    directionsDisplay.setOptions({draggable: true});

                    directionsService.route(directions, function (response, status) {
                        if (status === maps.DirectionsStatus.OK && response.routes.length > 0) {
                            // Show the route on the map
                            response.routes = [response.routes[0]]; // Only show the first route
                            directionsDisplay.setDirections(response);

                            // de map kan hier pas gezet worden. Pas vanaf hier is de control ingevuld in de scope
                            directionsDisplay.setMap($scope.map.control.getGMap());

                            // Add the waypoints from google maps to the travel is delayed when route is saved!!!

                        } else {
                            alertify.error('Er is geen route gevonden tussen uw start- en eindpunt.');
                        }
                    });
                });
            };

            // defaults for the map
            $scope.map = {
                center: {
                    // coordinates of Ghent
                    latitude: 51.053570,
                    longitude: 3.718695
                },
                zoom: 10,
                // this control is filled in via the control="map.control" parameter in the html
                control: {},
                options: {
                    // prevents scroll to zoom
                    scrollwheel: false
                }
            };

            //if the usere manually changes the route/waypoints it will be saved
            $scope.setWayPoints = function () {
                $scope.route.waypoints = [];
                var gmapsWaypoints = directionsDisplay.getDirections().routes[0].overview_path;
                for (var i = 0; i < gmapsWaypoints.length; i++) {
                    var waypoint = gmapsWaypoints[i];
                    $scope.route.waypoints.push({
                        lat: waypoint.lat(),
                        lon: waypoint.lng()
                    });
                }
            };


            $scope.updateNotifyForEventTypes = function () {
                $scope.route.notify_for_event_types = [];
                for (var i = 0; i < $scope.relevantTypesBool.length; i++) {
                    if ($scope.relevantTypesBool[i]) {
                        for (var j = 0; j < relevantTypes[i].length; j++) {
                            $scope.route.notify_for_event_types.push({type: relevantTypes[i][j]});
                        }
                    }
                }
            };
        }]);