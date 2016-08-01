/**
 * Created by thibault on 4/12/16.
 */

describe("AdminOperatorIndexCtrl", function () {
    beforeEach(module('mobiliteit'));

    var AdminOperatorIndexCtrl;
    var scope;
    var $q;
    var alertify;
    var Operator;

    beforeEach(inject(function ($controller, $rootScope, _$q_, _Operator_, _alertify_) {
        alertify = _alertify_;
        $q = _$q_;
        Operator = _Operator_;

        sinon.stub(_alertify_, 'error', function () {});
        sinon.stub(_alertify_, 'success', function () {});
        sinon.stub(_alertify_, 'okBtn', function () {return this;});
        sinon.stub(_alertify_, 'cancelBtn', function () {return this;});
        sinon.stub(_alertify_, 'confirm', function (text, callback) {callback();});

        sinon.stub(Operator, 'getList', function () {
            return $q(function (resolve) {
                var operators = [];
                // Generate 10 fake operators
                for (var i = 0; i < 10; i++) {
                    operators[i] = generateFakeUser(true);
                }
                resolve(operators);
            })
        });

        scope = $rootScope.$new();
        AdminOperatorIndexCtrl = $controller("AdminOperatorIndexCtrl", {
            $scope: scope,
            $uibModal: {
                open: function () {
                    return {
                        result: $q(function (resolve) {
                            resolve({
                                email: "test@ugent.be"
                            });
                        })
                    }
                }
            }
        });
    }));

    describe("Deleting an operator", function () {
        describe("success", function () {
            var operator;
            beforeEach(function () {
                operator = {
                    remove: function () {
                        return $q(function (resolve) {
                            resolve();
                        })
                    }
                };
                sinon.spy(operator, 'remove');
                scope.deleteOperator(operator);
                scope.$digest();
            });

            it('should call remove on the operator', function () {
                expect(operator.remove).to.be.called;
            });

            it('should show a success message', function () {
                expect(alertify.success).to.be.called;
            });
        });
        describe("error", function () {
            var operator;
            beforeEach(function () {
                operator = {
                    remove: function () {
                        return $q(function (resolve, reject) {
                            reject();
                        })
                    }
                };
                sinon.spy(operator, 'remove');
                scope.deleteOperator(operator);
                scope.$digest();
            });

            it('should show an error message', function () {
                expect(alertify.error).to.be.called;
            });
        });
    });
    
    describe("Creating new operator", function () {
        describe("success", function () {
            beforeEach(function () {
                sinon.stub(Operator, 'post', function () {
                    return $q(function (resolve) {
                        resolve()
                    });
                });

                scope.openNewOperatorModal();
                scope.$digest();
            });

            it('should show success message', function () {
                expect(alertify.success).to.be.called;
            })
        });
        describe("error", function () {
            beforeEach(function () {
                sinon.stub(Operator, 'post', function () {
                    return $q(function (resolve, reject) {
                        reject()
                    });
                });

                scope.openNewOperatorModal();
                scope.$digest();
            });

            it('should show error message', function () {
                expect(alertify.error).to.be.called;
            })
        });
    });
});