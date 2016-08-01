/**
 * Created by thibault on 4/4/16.
 */

angular.module("mobiliteit").controller("AdminOperatorIndexCtrl",
    ["$scope", "Operator", "alertify", "$uibModal", function ($scope, Operator, alertify, $uibModal) {

        $scope.loading=true;

        Operator.getList().then(function (r) {
            $scope.operators = r;
            $scope.loading = false;
        });

        $scope.deleteOperator = function (operator) {
            alertify.okBtn("Ja").cancelBtn("Annuleren").confirm("Bent u zeker dat u deze operator wilt verwijderen?",
                function () {
                    operator.remove()
                        .then(function () {
                            $scope.operators.splice($scope.operators.indexOf(operator), 1);
                            alertify.success("Operator verwijderd");
                        })
                        .catch(function () {
                            alertify.error("Er is een fout opgetreden.");
                        });
                });
        };

        $scope.openNewOperatorModal = function () {
            var modal = $uibModal.open({
                templateUrl: 'newOperatorModal.html',
                backdrop: 'static',
                keyboard: false,
                controller: ["$scope", "$uibModalInstance", function ($scope, $uibModalInstance) {
                    $scope.cancel = function () {
                        $uibModalInstance.dismiss('cancel');
                    };
                    $scope.close = $uibModalInstance.close;
                }]
            });

            modal.result.then(function (operator) {
                Operator.post(operator)
                    .then(function (operator) {
                        $scope.operators.push(operator);
                        alertify.success("Operator toegevoegd");
                    })
                    .catch(function () {
                        alertify.error("Er is een fout opgetreden.");
                    });
            });
        };
    }]);