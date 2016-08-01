module.exports = function() {
    var root = './';
    var src = root + "src/";
    var assets = src + "assets/";
    var wiredep = require('wiredep');
    //var bowerFiles = wiredep({devDependencies: true})['js'];
    var bower = {
        json: require('./bower.json'),
        directory: './bower_components/',
        ignorePath: '../..'
    };
    //var nodeModules = 'node_modules';

    var config = {
        alljs: './src/**/*.js',
        build: './build/',
        src: src,
        assets: assets,
        css: assets + 'css/*.css',
        less: assets + 'less/bootstrap.less',
        fonts: bower.directory + 'font-awesome/fonts/**/*.*',
        partials: src + 'app/**/*.html',
        images: assets + 'img/*.*',
        index: src + 'index.html',
        js: src + '**/*.js',
        jsOrder: [
            '**/app.js',
            '**/app.routes.js',
            '**/app.constants.js',
            '**/*.js'
        ],
        root: root,
        optimized: {
            app: 'app.js',
            lib: 'lib.js'
        },
        /**
         * Bower and NPM files
         */
        bower: bower,
        packages: [
            './package.json',
            './bower.json'
        ]
    };

    /**
     * wiredep and bower settings
     */
    config.getWiredepDefaultOptions = function() {
        return {
            bowerJson: config.bower.json,
            directory: config.bower.directory,
            ignorePath: config.bower.ignorePath
        };
    };

    return config;
};