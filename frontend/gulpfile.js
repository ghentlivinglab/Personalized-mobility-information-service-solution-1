/**
 * Created by thibault on 2/18/16.
 */
var config = require('./gulp.config')();
var del = require('del');
var gulp = require('gulp');
var $ = require('gulp-load-plugins')({lazy: true});

/**
 * List the available gulp tasks
 */
gulp.task('help', $.taskListing);
gulp.task('default', ['help']);

/**
 * Inject all css in src/assets/css into the index.html
 */
gulp.task('compile-less', function () {
    log('Compiling less');
    return gulp
        .src(config.less)
        .pipe($.less())
        .pipe(gulp.dest(config.assets + "css/"));
});

/**
 * Inject all css in src/assets/css into the index.html
 */
gulp.task('inject-css', ["compile-less"], function () {
    log('Inject css into index.html');
    return gulp
        .src(config.index)
        .pipe($.inject(gulp.src(config.css), {read: false, ignorePath: '/src/', addRootSlash: false}))
        .pipe(gulp.dest(config.src));
});

/**
 * Inject all js into the index.html
 */
gulp.task('inject-js', ['wiredep'], function () {
    log('Inject js into the html');
    return gulp
        .src(config.index)
        .pipe(inject(config.js, '', config.jsOrder))
        .pipe(gulp.dest(config.src));
});
/**
 * Build everything
 */
gulp.task('build', ['optimize', 'images', 'fonts'], function () {
    log('Gulp Build Finished');
});

/**
 * Copy fonts
 */
gulp.task('fonts', function() {
    log('Copying fonts');

    return gulp
        .src(config.fonts)
        .pipe(gulp.dest(config.build + 'assets/fonts'));
});

/**
 * Compress images
 */
gulp.task('images', function() {
    log('Compressing and copying images');

    return gulp
        .src(config.images)
        .pipe($.imagemin({optimizationLevel: 4}))
        .pipe(gulp.dest(config.build + 'assets/img'));
});

/**
 * Wire-up the bower dependencies
 */
gulp.task('wiredep', ['inject-css'], function() {
    log('Wiring the bower dependencies into the html');

    var wiredep = require('wiredep').stream;
    var options = config.getWiredepDefaultOptions();

    return gulp
        .src(config.index)
        .pipe(wiredep(options))
        .pipe(gulp.dest(config.src));
});


/**
 * Inject task for easy access to injecting everything
 */
gulp.task('inject', ['inject-js'], function() {
    log('All files injected.');
});

/**
 * Optimize all files, move to a build folder,
 */
gulp.task('optimize', ['inject', 'partials'], function() {
    // Filters are named for the gulp-useref path
    var cssFilter = $.filter('**/*.css', {restore: true});
    var jsAppFilter = $.filter('**/' + config.optimized.app, {restore: true});
    var jslibFilter = $.filter('**/' + config.optimized.lib, {restore: true});

    return gulp
        .src(config.index)
        .pipe($.plumber())
        // Gather all assets from the html with useref
        .pipe($.useref())
        // Get the css
        .pipe(cssFilter)
        // Minify the CSS
        .pipe($.csso())
        .pipe(cssFilter.restore)
        // Get the custom javascript
        .pipe(jsAppFilter)
        // Make sure all angular $inject's are working
        .pipe($.ngAnnotate())
        // Minify JavaScript
        .pipe($.uglify())
        .pipe(jsAppFilter.restore)
        // Get the dependencies's javascript
        .pipe(jslibFilter)
        // Minify JavaScript
        .pipe($.uglify())
        .pipe(jslibFilter.restore)
        .pipe(gulp.dest(config.build));
});

/**
 * Copy partials
 */
gulp.task('partials', function() {
    log('Copying partials');

    return gulp
        .src(config.partials)
        .pipe(gulp.dest(config.build + 'app'));
});

/**
 * Remove all files from the build folder
 */
gulp.task('clean', function(done) {
    log('Cleaning: ' + $.util.colors.blue(config.build));
    del(config.build, done);
});

//////////////////////
// Helper Functions //
//////////////////////

/**
 * Delete all files in a given path
 * @param  {Array}   path - array of paths to delete
 * @param  {Function} done - callback when complete
 */
function clean(path, done) {
    log('Cleaning: ' + $.util.colors.blue(path));
    del(path, done);
}

/**
 * Inject files in a sorted sequence at a specified inject label
 * @param   {Array} src   glob pattern for source files
 * @param   {String} label   The label name
 * @param   {Array} order   glob pattern for sort order of the files
 * @returns {Stream}   The stream
 */
function inject(src, label, order) {
    var options = {read: false, ignorePath: '/src/', addRootSlash: false};
    if (label) {
        options.name = 'inject:' + label;
    }

    return $.inject(orderSrc(src, order), options);
}

/**
 * Order a stream
 * @param   {Stream} src   The gulp.src stream
 * @param   {Array} order Glob array pattern
 * @returns {Stream} The ordered stream
 */
function orderSrc (src, order) {
    //order = order || ['**/*'];
    return gulp
        .src(src)
        .pipe($.if(order, $.order(order)));
}

/**
 * Log a message or series of messages using chalk's blue color.
 * Can pass in a string, object or array.
 */
function log(msg) {
    if (typeof(msg) === 'object') {
        for (var item in msg) {
            if (msg.hasOwnProperty(item)) {
                $.util.log($.util.colors.blue(msg[item]));
            }
        }
    } else {
        $.util.log($.util.colors.blue(msg));
    }
}

module.exports = gulp;