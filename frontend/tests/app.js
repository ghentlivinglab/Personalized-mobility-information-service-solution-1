describe("AdminUserIndexCtrl", function () {
    var $auth;
    var $rootScope;
    var $localStorage;
    var $state;
    var AuthService;

    beforeEach(module("mobiliteit"));


    beforeEach(inject(function (_$auth_, _$rootScope_, _$localStorage_, _$state_, _AuthService_) {
        $auth = _$auth_;
        $rootScope = _$rootScope_;
        $localStorage = _$localStorage_;
        $state = _$state_;
        AuthService = _AuthService_;

        sinon.stub(AuthService, "disconnect", function () {
        });
        sinon.stub($auth, "logout", function () {
        });
        sinon.stub($localStorage, "$reset", function () {
        });
        sinon.stub($state, "go", function () {
        });

    }));

    describe("Logging out", function () {
        beforeEach(function () {
            $rootScope.logout();
        });

        it("should clear everything and go to index", function () {
            expect(AuthService.disconnect.callCount).to.be.equal(1);
            expect($auth.logout.callCount).to.be.equal(1);
            expect($localStorage.$reset.callCount).to.be.equal(1);
            expect($state.go.callCount).to.be.equal(1);
            expect($state.go).to.be.calledWith("index");
        })
    });

    describe("On state change when not logged in", function () {
        beforeEach(function () {
            sinon.stub(AuthService, "isLoggedIn", function () {
                return false;
            });
        });

        describe("To index", function () {
            beforeEach(function () {
                $rootScope.$broadcast('$stateChangeStart', "index");
                $rootScope.$digest();
            });

            it("should not need to know if the user is logged in", function () {
                expect(AuthService.isLoggedIn.callCount).to.be.equal(1);
            })
        });
    })

    describe("On state change when not logged in", function () {
        beforeEach(function () {
            sinon.stub(AuthService, "isLoggedIn", function () {
                return true;
            });
        });

        describe("To index", function () {
            beforeEach(function () {
                $rootScope.$broadcast('$stateChangeStart', "index");
                $rootScope.$digest();
            });

            it("should not need to know if the user is logged in", function () {
                expect(AuthService.isLoggedIn.callCount).to.be.equal(1);
            })
        });
    });
});