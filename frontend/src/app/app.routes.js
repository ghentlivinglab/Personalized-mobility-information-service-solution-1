/**
 * Created by thibault on 2/18/16.
 */

angular.module("mobiliteit").config(['$stateProvider', '$urlRouterProvider', function ($stateProvider, $urlRouterProvider) {

    // For any unmatched url, redirect to /
    $urlRouterProvider.otherwise("/");

    $stateProvider
        .state('index', {
            url: "/",
            templateUrl: "app/components/index/indexView.html",
            controller: "IndexCtrl"
        })
        .state('signup', {
            url: "/signup",
            templateUrl: "app/components/signup/signUpView.html",
            controller: "SignUpCtrl"
        })
        .state('forgotPassword', {
            url: "/forgot_password",
            templateUrl: "app/components/index/forgotPassword/forgotPasswordView.html",
            controller: "ForgotPasswordCtrl"
        })
        .state('setup', {
            url: "/setup/:user_id/",
            templateUrl: "app/components/setup/setupView.html",
            controller: "SetupCtrl"
        })
        .state('app', {
            templateUrl: "app/components/app.html"
        })
        .state('app.home', {
            url: "/home",
            templateUrl: "app/components/home/homeView.html",
            controller: "HomeCtrl"
        })
        .state('app.event', {
            url: "/event/:event_id/",
            templateUrl: "app/components/event/viewEventView.html",
            controller: "ViewEventCtrl"
        })
        .state('app.profile', {
            url: "/profile",
            templateUrl: "app/components/profile/profileView.html",
            controller: "ProfileCtrl"
        })
        .state('admin', {
            url: '/admin',
            templateUrl: "app/components/admin/adminIndexView.html"
        })
        .state('admin.users', {
            url: "/users",
            templateUrl: "app/components/admin/users/adminUserIndexView.html",
            controller: "AdminUserIndexCtrl"
        })
        .state('admin.operators', {
            url: "/operators",
            templateUrl: "app/components/admin/operators/adminOperatorIndexView.html",
            controller: "AdminOperatorIndexCtrl"
        })
        .state('admin.userDetails', {
            url: "/user/:user_id/",
            templateUrl: "app/components/admin/users/detail/adminUserDetailView.html",
            controller: "AdminUserDetailCtrl"
        })
        .state('operator', {
            url: '/operator',
            templateUrl: "app/components/operator/operatorIndexView.html"
        })
        .state('operator.events', {
            url: "/events",
            templateUrl: "app/components/operator/events/operatorEventIndexView.html",
            controller: "OperatorEventIndexCtrl"
        })
        .state('operator.eventDetails', {
            url: "/event/:event_id/",
            templateUrl: "app/components/operator/events/detail/operatorEventDetailView.html",
            controller: "OperatorEventDetailCtrl"
        })
        .state('app.travels',{
            url:"/travel",
            templateUrl:"app/components/travels/travelIndexView.html",
            controller:"TravelIndexCtrl"

        })
        .state('app.travelDetail', {
            url: "/travel/:travel_id/",
            templateUrl: "app/components/travels/detail/travelDetailView.html",
            controller: "TravelDetailCtrl"
        })
        .state('app.addresses', {
            url: "/addresses",
            templateUrl: "app/components/addresses/addressIndexView.html",
            controller: "AddressIndexCtrl"
        })
}]);
