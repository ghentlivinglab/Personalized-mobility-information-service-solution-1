/**
 * Created by Lukas on 5/03/2016.
 */


angular.module("mobiliteit").controller("TravelDetailCtrl", ["AuthService", "$scope", "Travel", "User", "Route", "$stateParams", "$q", "alertify", "$state", "$uibModal",
    function (AuthService, $scope, Travel, User, Route, $stateParams, $q, alertify, $state, $uibModal) {
        $scope.travel = {
            time_interval: []
        };

        User.one(AuthService.getCurrentUserID()).get().then(function (r) {
            $scope.user = r;
            Travel.for($scope.user).one($stateParams.travel_id).get().then(function (s) {
                $scope.travel = s;
                $scope.travel.date = new Date($scope.travel.date);
                Route.for($scope.travel).getList().then(function (t) {
                    $scope.routes = t;
                });
            });
        });


        $scope.save = function (travel) {
            travel.put().then(function () {
                    alertify.success("De route is opgeslaan.");
                })
                .catch(function () {
                    alertify.error("Er is een fout opgetreden.");
                });
        };

        $scope.toggleRouteActive = function (route) {
            route.active = !route.active;
            route.put()
                .catch(function () {
                    alertify.error("Er is een fout opgetreden.");
                });
        };

        $scope.deleteRoute = function (route) {
            if ($scope.routes.length > 1) {
                alertify.confirm("Bent u zeker dat u dit traject wilt verwijderen?", function () {
                    route.remove()
                        .then(function () {
                            alertify.success("Traject verwijderd");
                            $scope.routes.splice($scope.routes.indexOf(route), 1);
                        })
                        .catch(function () {
                            alertify.error("Er is een fout opgetreden.");
                        });
                });
            }
            else {
                alertify.confirm("Bent u zeker dat u dit traject wilt verwijderen? Aangezien dit het laatste traject is zal de route ook verwijderd worden.", function () {
                    $scope.travel.remove()
                        .then(function () {
                            alertify.success("Traject en Route verwijderd");
                            $state.go("app.travels");
                        })
                        .catch(function () {
                            alertify.error("Er is een fout opgetreden.");
                        });
                });
            }
        };

        $scope.openAddRouteModal = function () {
            var modal = $uibModal.open({
                templateUrl: 'app/common/templates/addRouteModal/addRouteModalView.html',
                controller: "AddRouteModalCtrl",
                size: 'lg',
                backdrop: 'static',
                keyboard: false,
                resolve: {
                    travel: function () {
                        return $scope.travel;
                    }
                }
            });
            modal.result.then(function (result) {
                Route.for($scope.travel)
                    .post(result)
                    .then(function () {
                        $scope.routes.push(result);
                        alertify.success("De route is aangemaakt.");
                    })
                    .catch(function () {
                        alertify.error("Er is een fout opgetreden.");
                    });
            });
        }

    }]);