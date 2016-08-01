/**
 * Created by Lukas on 27/04/2016.
 */


angular.module("mobiliteit")
    .controller("TravelIndexCtrl",
        ['$scope', 'User', "AuthService", "alertify", "Travel", "$uibModal", "Route", "$q",
            function ($scope, User, AuthService, alertify, Travel, $uibModal, Route, $q) {

                User.one(AuthService.getCurrentUserID()).get()
                    .then(function (user) {
                        $scope.user = user;
                        Travel.for(user).getList()
                            .then(function (travels) {
                                $scope.travels = travels;
                            });
                    });

                $scope.deleteTravel = function (travel) {
                    alertify.okBtn("Ja").cancelBtn("Annuleren")
                        .confirm("Bent u zeker dat u deze route wilt verwijderen?", function () {
                            travel.remove()
                                .then(function () {
                                    alertify.success("Route verwijderd");
                                    $scope.travels.splice($scope.travels.indexOf(travel), 1);
                                })
                                .catch(function () {
                                    alertify.error("Er is een fout opgetreden.");
                                });
                        });
                };

                onModalFinish = function(result){
                    var travel = result[0];
                    var reverse = result[2];

                    Travel.for($scope.user)
                        .post(travel)
                        .then(function (travel) {
                            // Create the routes
                            var routes = angular.copy(result[1]);
                            var chain = $q.when();
                            angular.forEach(routes, function (route) {
                                chain = chain.then(function () {
                                    Route.for(travel)
                                        .post(route)
                                        .catch(function () {
                                            alertify.error("Er is een fout opgetreden.");
                                        })
                                });
                            });
                            chain.then(function () {
                                $scope.travels.push(travel);
                                alertify.success("De route is aangemaakt.");
                            });

                        })
                        .catch(function () {
                            alertify.error("Er is een fout opgetreden.");
                        }).then(function (){
                        if(reverse){

                            var temp = travel.startpoint;
                            travel.startpoint = travel.endpoint;
                            travel.endpoint = temp;
                            travel.reverse = true;

                            var modal2 = $uibModal.open({
                                templateUrl: 'app/common/templates/editTravelModal/editTravelModalView.html',
                                controller: "EditTravelModalCtrl",
                                size: 'lg',
                                backdrop: 'static',
                                keyboard: false,
                                resolve: {
                                    travel: function () {
                                        return travel;
                                    }
                                }
                            });
                            modal2.result.then(function (result) {
                                onModalFinish(result);
                            });
                        }
                    })


                };

                $scope.openAddTravelModal = function () {
                    var modal = $uibModal.open({
                        templateUrl: 'app/common/templates/editTravelModal/editTravelModalView.html',
                        controller: "EditTravelModalCtrl",
                        size: 'lg',
                        backdrop: 'static',
                        keyboard: false,
                        resolve: {
                            travel: function () {
                                return null;
                            }
                        }
                    });

                    modal.result.then(function (result) {
                       onModalFinish(result);
                    });
                }
            }]);