/**
 * Created by thibault on 2/24/16.
 */
// Karma configuration

module.exports = function (config) {
    config.set({

        // base path used to resolve all patterns (e.g. files, exclude)
        basePath: '',
        plugins: [
            'karma-mocha',
            'karma-sinon-chai',
            'karma-chai',
            'karma-coverage',
            'karma-phantomjs-launcher',
            'karma-chai-as-promised',
            'karma-chai-things',
            'karma-junit-reporter',
            'karma-ng-html2js-preprocessor'
        ],
        // frameworks to use
        frameworks: ['mocha', 'sinon-chai', 'chai-as-promised', 'chai-things', 'chai'],

        // list of files / patterns to load in the browser
        files: [
            'bower_components/angular/angular.js',
            'bower_components/angular-mocks/angular-mocks.js',
            'bower_components/angular-cookies/angular-cookies.js',
            'bower_components/angular-ui-router/release/angular-ui-router.js',
            'bower_components/ngstorage/ngStorage.js',
            'bower_components/faker/build/build/faker.js',
            'bower_components/restangular/dist/restangular.js',
            'bower_components/lodash/lodash.js',
            'bower_components/spin.js/spin.js',
            'bower_components/ladda/dist/ladda.min.js',
            'bower_components/angular-ladda/dist/angular-ladda.min.js',
            'bower_components/angular-validation-match/dist/angular-validation-match.min.js',
            'bower_components/alertify.js/dist/js/ngAlertify.js',
            'bower_components/angular-bootstrap/ui-bootstrap-tpls.js',
            'bower_components/angular-bootstrap-show-errors/src/showErrors.js',
            'bower_components/angular-wizard/dist/angular-wizard.min.js',
            'bower_components/angular-simple-logger/dist/angular-simple-logger.js',
            'bower_components/angular-google-maps/dist/angular-google-maps.js',
            'bower_components/angular-recaptcha/release/angular-recaptcha.js',
            'bower_components/satellizer/satellizer.js',
            'bower_components/angular-jwt/dist/angular-jwt.js',
            'bower_components/sockjs/sockjs.js',
            'bower_components/stomp-websocket/lib/stomp.min.js',
            'bower_components/ng-stomp/dist/ng-stomp.standalone.min.js',
            'bower_components/angular-loading-bar/build/loading-bar.js',
            'bower_components/angular-ui-notification/dist/angular-ui-notification.js',
            'bower_components/ngAutocomplete/src/ngAutocomplete.js',
            'bower_components/ngTimepicker/src/js/ngTimepicker.min.js',
            'src/app/app.js',
            'src/app/app.routes.js',
            'tests/app.constants.js',
            'src/**/*.js',
            'tests/common/services/userService.js',
            'tests/**/*.js',
            'src/**/*.html',
            'https://maps.googleapis.com/maps/api/js?sensor=false&libraries=places'
        ],

        // list of files to exclude
        exclude: [],

        // preprocess matching files before serving them to the browser
        preprocessors: {
            'src/**/*.js': ['coverage'],
            "src/app/**/*.html": ["ng-html2js"]
        },

        coverageReporter: {
            reporters:[
                {type: 'html', dir:'coverage/'},
                {type: 'cobertura', dir:'coverage/'}
            ]
        },

        ngHtml2JsPreprocessor: {
            stripPrefix: 'src/',
            // the name of the Angular module to create
            moduleName: "mobiliteit"
        },

        // test results reporter to use
        reporters: ['progress', 'coverage', 'dots', 'junit'],

        // web server port
        port: 9876,

        // enable / disable colors in the output (reporters and logs)
        colors: true,

        // level of logging
        logLevel: config.LOG_INFO,

        // start these browsers
        browsers: ['PhantomJS'],

        singleRun: true,

        junitReporter: {
            outputDir: '',
            outputFile: 'test-results.xml',
            useBrowserName: false
        }
    });
};