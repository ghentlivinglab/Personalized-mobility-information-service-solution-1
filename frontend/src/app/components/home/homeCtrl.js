/**
 * Created by thibault on 2/18/16.
 */

angular.module("mobiliteit").controller("HomeCtrl",
    ['$scope', 'AuthService', 'Event', 'Travel', 'User', 'PointOfInterest', 'Route', "uiGmapIsReady", "$filter",
        function ($scope, AuthService, Event, Travel, User, PointOfInterest, Route, uiGmapIsReady, $filter) {
            // Max pages the user can go back in time (page is 100 items)
            //$scope.totalItems = 500;

            // Loading
            $scope.loading = true;
            $scope.needEventRefresh = true;

            // Default filters
            $scope.filters = {
                only_relevant: true,
                show_markers: true,
                show_jams: true,
                show_pois: true,
                show_routes: true,
                previous: {
                    only_relevant: true
                }
                //    page: 1
            };

            // Initialize the map
            $scope.map = {
                center: {
                    latitude: 51.058353,
                    longitude: 3.730848
                },
                markers: [],
                jams: [],
                routes: [],
                pois: [],
                zoom: 11
            };


            var getUserData = function () {

                User.one(AuthService.getCurrentUserID()).get()
                    .then(function (user) {
                            $scope.user = user;

                            if (!$scope.filters.show_routes) {
                                $scope.map.routes = [];
                            }
                            // only get the data for the routes if show_routes is true and the current routes array is empty
                            // you dont need to get the data if it is already there
                            else if ($scope.map.routes.length == 0) {
                                Travel.for($scope.user).getList()
                                    .then(function (travels) {
                                            $scope.travels = travels;

                                            angular.forEach(travels, function (travel) {
                                                Route.for(travel).getList()
                                                    .then(function (routes) {
                                                        angular.forEach(routes, function (route, i) {
                                                            if (route.active) {
                                                                var temp = {id: i};
                                                                temp.path = [];
                                                                temp.path.push({
                                                                    latitude: travel.startpoint.coordinates.lat,
                                                                    longitude: travel.startpoint.coordinates.lon
                                                                });
                                                                angular.forEach(route.waypoints, function (waypoint) {
                                                                    temp.path.push({
                                                                        latitude: waypoint.lat,
                                                                        longitude: waypoint.lon
                                                                    })
                                                                });
                                                                temp.path.push({
                                                                    latitude: travel.endpoint.coordinates.lat,
                                                                    longitude: travel.endpoint.coordinates.lon
                                                                });

                                                                $scope.map.routes.push(temp);
                                                            }
                                                        })
                                                    })
                                            })

                                        }
                                    );
                            }
                            if (!$scope.filters.show_pois) {
                                $scope.map.pois = [];
                            }
                            else if ($scope.map.pois.length == 0) {

                                PointOfInterest.for(user).getList()
                                    .then(function (pois) {
                                        $scope.points_of_interests = pois;
                                        // Draw circles on the map
                                        uiGmapIsReady.promise().then(function () {

                                            angular.forEach(pois, function (poi, i) {

                                                if (poi.active) {
                                                    $scope.map.pois.push({
                                                        id: $scope.map.pois.length,
                                                        center: {
                                                            latitude: poi.address.coordinates.lat,
                                                            longitude: poi.address.coordinates.lon
                                                        },
                                                        radius: poi.radius
                                                    });
                                                }
                                            });
                                        });
                                    })
                            }


                            $scope.loading = false;
                        }
                    );
            };

            getUserData();

            // Watch for changes in filters and re-get the events
            $scope.$watch('filters', function () {
                var params = {
                    page: $scope.filters.page
                };
                if ($scope.filters.only_relevant) {
                    params.user_id = AuthService.getCurrentUserID();
                }

                if ($scope.filters.only_relevant !== $scope.filters.previous.only_relevant) {
                    $scope.needEventRefresh = true;
                    $scope.filters.previous.only_relevant = $scope.filters.only_relevant;
                }


                $scope.loading = true;
                Event.getList(params).then(function (r) {
                    $scope.events = r;
                    $scope.loading = false;
                });
                getUserData();
            }, true);

            // Watch for changes in the events, then update the map (list is auto-updated by the view)
            $scope.$watchCollection("events", function (events) {
                var jamsInit = $scope.map.jams.length > 0;
                if ($scope.needEventRefresh || !$scope.filters.show_markers) {
                    $scope.map.markers = [];
                }
                if ($scope.needEventRefresh || !$scope.filters.show_jams) {
                    $scope.map.jams = [];
                    jamsInit = false;
                }
                if (events) {
                    if (($scope.filters.show_markers && $scope.map.markers.length === 0 ) || ($scope.filters.show_jams && $scope.map.jams.length === 0 )) {
                        uiGmapIsReady.promise().then(function () {
                            angular.forEach(events, function (event, i) {
                                var title;
                                if (event.description) {
                                    title = event.description
                                } else {
                                    title = $filter("translateFilter")(event.type.type);
                                }
                                if (event.jams.length > 0 && $scope.filters.show_jams && !jamsInit) {
                                    angular.forEach(event.jams, function (jam, j) {
                                        var temp = {id: i * 10 + j};
                                        temp.path = [];
                                        angular.forEach(jam.points, function (point, k) {
                                            temp.path.push({
                                                latitude: point.lat,
                                                longitude: point.lon
                                            })
                                        });
                                        temp.stroke = {
                                            color: '#000000',
                                            weight: 6
                                        };

                                        // Color the jams using a spectrum from yellow to red
                                        if (jam.speed > 0) {
                                            var hue = 0;
                                            // Everything from approximately 3 an up is considered heavy by waze
                                            if (jam.speed < 3) {
                                                hue = 20 * (3 - jam.speed);
                                            }
                                            temp.stroke.color = 'hsl(' + hue + ',100%,50%)';
                                        }

                                        $scope.map.jams.push(temp);
                                    })
                                }
                                if ($scope.filters.show_markers) {
                                    $scope.map.markers.push({
                                        id: i,
                                        coords: {
                                            latitude: event.coordinates.lat,
                                            longitude: event.coordinates.lon
                                        },
                                        title: title
                                    });
                                }
                            });
                        });
                    }
                    $scope.needEventRefresh = false;
                }
            });

            $scope.$on("$newEvent", function (dontneedthis, data) {
                if ($scope.events) {
                    $scope.events.push(data);
                } else {
                    $scope.events = [data];
                }
            });
        }
    ]);