/**
 * Created by Lukas on 3/03/2016.
 */
angular.module("mobiliteit").controller("OperatorEventIndexCtrl", ["$scope", "Event", "$uibModal", "alertify",
    function ($scope, Event, $uibModal, alertify) {
        $scope.events = [];
        $scope.filters={
            active: true
        };

        $scope.loading = true;

        $scope.$watch('filters', function () {
            if ($scope.filters) {
                $scope.loading = true;
                var params = {
                    active: $scope.filters.active
                };
                Event.getList(params).then(function (r) {
                    $scope.events = r;
                    $scope.loading=false;
                });
            }
        }, true);

        $scope.openModal = function () {
            var modalInstance = $uibModal.open({
                templateUrl: 'addEventModal.html',
                size: 'lg',
                backdrop: 'static',
                keyboard: false,
                controller: ["$uibModalInstance", "$scope", function ($uibModalInstance, $scope) {
                    $scope.ok = function () {
                        $uibModalInstance.close($scope.newEvent);
                    };

                    $scope.cancel = function () {
                        $uibModalInstance.dismiss('cancel');
                    };
                }]
            });

            modalInstance.result.then(function (event) {
                Event.post(event).then(function (e) {
                        $scope.events.push(e);
                        alertify.success("Het event is aangemaakt.");
                    })
                    .catch(function () {
                        alertify.error("Er is een fout opgetreden.");
                    });
            })
        };

    }]);