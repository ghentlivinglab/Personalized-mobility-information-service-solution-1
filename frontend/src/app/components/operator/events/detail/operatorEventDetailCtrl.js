/**
 * Created by Lukas on 3/03/2016.
 */
angular.module("mobiliteit").controller("OperatorEventDetailCtrl", ["$scope", "Event", "EventType", "$stateParams", "$q", "alertify", 
    function ($scope, Event, EventType, $stateParams, $q, alertify) {
        Event.one($stateParams.event_id).get().then(function (r) {
            $scope.event = r;
        });

        $scope.save = function (event) {
            event.put()
                .then(function () {
                    alertify.success("Het event is opgeslagen.");
                })
                .catch(function () {
                    alertify.error("Er is een fout opgetreden.");
                })
        };
    }]);