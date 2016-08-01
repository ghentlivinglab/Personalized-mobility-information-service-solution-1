/**
 * Created by thibault on 2/18/16.
 */

angular.module("mobiliteit",
    [
        'ngCookies',
        'ui.router',
        'ngStorage',
        'restangular',
        'angular-ladda',
        'validation.match',
        'ngAlertify',
        'ui.bootstrap',
        'ui.bootstrap.showErrors',
        'mgo-angular-wizard',
        'uiGmapgoogle-maps',
        'vcRecaptcha',
        'satellizer',
        'angular-jwt',
        'ngStomp',
        'angular-loading-bar',
        'ui-notification',
        'ngAutocomplete',
        'jkuri.timepicker'
    ])
    .run(['alertify', function (alertify) {
        alertify.maxLogItems(20);
    }])
    .config(['RestangularProvider', 'apiBaseURL', function (RestangularProvider, apiBaseURL) {
        RestangularProvider.setBaseUrl(apiBaseURL);
        RestangularProvider.setRequestSuffix('/');
        //RestangularProvider.setErrorInterceptor(function (response) {
        //console.error("API returned error response", response);
        //return false;
        //});

        RestangularProvider.setRestangularFields({
            route: "restangularRoute"
        });
    }])
    .config(["laddaProvider", function (laddaProvider) {
        laddaProvider.setOption({
            style: 'expand-left'
        });
    }])
    .config(['showErrorsConfigProvider', function (showErrorsConfigProvider) {
        showErrorsConfigProvider.showSuccess(true);
    }])
    .config(["uiGmapGoogleMapApiProvider", function (uiGmapGoogleMapApiProvider) {
        uiGmapGoogleMapApiProvider.configure({
            key: 'AIzaSyDTF_guVStzjCqG4OxxD1xagX_yAPSq4iQ',
            libraries: 'geometry'
        });
    }])
    .config(["$authProvider", 'apiBaseURL', function ($authProvider, apiBaseURL) {
        $authProvider.loginUrl = apiBaseURL + '/refresh_token/regular';

        $authProvider.facebook({
            clientId: '149531008775487',
            url: apiBaseURL + "/refresh_token/facebook"
        });

        $authProvider.google({
            clientId: '660075208604-tnm8fcl20oue06asjoeu2a3tkrvgbbeg.apps.googleusercontent.com',
            url: apiBaseURL + "/refresh_token/google"
        });
    }])
    .config(['cfpLoadingBarProvider', function (cfpLoadingBarProvider) {
        cfpLoadingBarProvider.includeSpinner = false;
    }])
    .config(function (NotificationProvider) {
        NotificationProvider.setOptions({
            delay: 30000,
            startTop: 20,
            startRight: 10,
            verticalSpacing: 20,
            horizontalSpacing: 20,
            positionX: 'left',
            positionY: 'bottom'
        });
    })
    .run(["$auth", "$rootScope", "$state", "$localStorage", "AuthService",
        function ($auth, $rootScope, $state, $localStorage, AuthService) {
            $rootScope.$on('$stateChangeStart',
                function (event, toState) {
                    if (toState.name != "index" && toState.name != "signup" && toState.name != "forgotPassword" && toState.name != "setup") {
                        if (!AuthService.isLoggedIn()) {
                            event.preventDefault();
                            console.error("Not logged in!");
                            $state.go("index");
                        }
                    }
                });

            $rootScope.logout = function () {
                AuthService.disconnect();
                $auth.logout();
                $localStorage.$reset();
                $state.go('index');
            };
        }]);