/**
 * Created by thibault on 2/28/16.
 */

angular.module("mobiliteit").directive("editPointOfInterestForm", function () {
    return {
        restrict: "E",
        scope: {
            poi: '='
        },
        require: '^form',
        templateUrl: 'app/common/directives/editPointOfInterestForm/editPointOfInterestFormView.html',
        link: function (scope, element, attr, ctrl) {
            scope.form = ctrl;
        },
        controller: ["$scope", "EventType", function ($scope, EventType) {
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
                "JAM_STAND_STILL_TRAFFIC",
                "JAM"
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

            if (!$scope.poi) {
                $scope.poi = {
                    radius: 100,
                    notify: {
                        cell_number: false
                    }
                };
            }


            if ($scope.poi.notify_for_event_types) {
                $scope.relevantTypesBool = [false, false, false, false, false, false];
                angular.forEach($scope.poi.notify_for_event_types, function (type) {
                    if (weather.indexOf(type.type) > -1) {
                        $scope.relevantTypesBool[0] = true;
                    }
                    else if (hazard.indexOf(type.type) > -1) {
                        $scope.relevantTypesBool[1] = true;
                    }
                    else if (jam.indexOf(type.type) > -1) {
                        $scope.relevantTypesBool[2] = true;
                    }
                    else if (construction.indexOf(type.type) > -1) {
                        $scope.relevantTypesBool[3] = true;
                    }
                    else if (closed.indexOf(type.type) > -1) {
                        $scope.relevantTypesBool[4] = true;
                    }
                    else if (other.indexOf(type.type) > -1) {
                        $scope.relevantTypesBool[5] = true;
                    }
                });
            }
            else {
                $scope.relevantTypesBool = [true, true, true, true, true, true];
            }

            EventType.getList().then(function (types) {
                $scope.eventtypes = types;
            });

            if (!$scope.poi) {
                $scope.poi = {
                    radius: 100
                };
            }


            $scope.$watch("relevantTypesBool", function () {
                $scope.updateNotifyForEventTypes();
            }, true);

            $scope.$watch("poi", function () {
                $scope.updateNotifyForEventTypes();
            }, false);

            $scope.updateNotifyForEventTypes = function () {
                $scope.poi.notify_for_event_types = [];
                for (var i = 0; i < $scope.relevantTypesBool.length; i++) {
                    if ($scope.relevantTypesBool[i]) {
                        for (var j = 0; j < relevantTypes[i].length; j++) {
                            $scope.poi.notify_for_event_types.push({type: relevantTypes[i][j]});
                        }
                    }
                }
            };
        }]
    }
});