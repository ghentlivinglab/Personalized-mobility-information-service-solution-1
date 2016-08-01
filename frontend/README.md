## Frontend

The frontend will be made with AngularJS and Bootstrap. 

AngularJS plugins worth mentioning:
* [RestAngular](https://github.com/mgonto/restangular#lets-code)
* [Angular UI Router](https://github.com/angular-ui/ui-router)
* [Angular Cookies](https://docs.angularjs.org/api/ngCookies)

### Application Structure

The `src` folder contains 3 items: the `index.html` page, the `assets` directory and the `app` directory.

The `assets` directory contains all static content, such as CSS files and images. 

The `app` directory contains everything Angular-related to our application. Herein you will find `app.js` (which is the
file where the application will be initialized), `app.routes.js` (which will contain all our routes), `components` and 
`common`. 

The `common` directory contains our directives and shared services. The `components` directory contains all
components. Each component should correspond with a route. Every component has a controller and a view.

This application structure was inspired by [scotch.io's design](https://scotch.io/tutorials/angularjs-best-practices-directory-structure), with a few modifications of my own, based on experience.

### Dependencies
#### Bower
You'll notice we have a `package.json` and `bower.json` file here. These are files for NPM and Bower.


[Bower](http://bower.io/) is the de-facto default package manager for frontend packages. 
You can install bower with `npm install -g bower` (Linux users should use `sudo`). 

`npm` is the Node Package Manager, so you'll need [Node.js](https://nodejs.org/en/).

After bower is installed, navigate to the frontend directory and run `bower install`. 
This will install all dependencies listed in the `bower.json` file. 
Whenever someone adds a new dependency, you'll need to run this command again.

Examples of bower packages we'll be using are Bootstrap, AngularJS and all it's plugins,...

**Be sure to read about Angular UI Router, as it's fundamentally different from the normal AngularJS routing.**

#### Gulp

We'll use [gulp](http://gulpjs.com/) as our main building tool. Gulp is a javascript task/build runner for development.
The `gulpfile.js` and `gulp.conf.js` files are our configuration files for gulp.
These are already set up, so you shouldn't modify them, unless you know what you're doing.

You can install gulp with `npm install -g gulp` (again, use `sudo` if necessary).

Since gulp has so many plugins, we'll be using some of them. You can view them in the `package.json` file.

To install all gulp dependencies (actually, they are npm dependencies, but nevermind that), run `npm install` inside
this frontend folder.

##### Gulp Tasks
To execute a gulp task, run `gulp [task name]` in this folder. 
Gulp will search for our `gulpfile.js` and look for the specific task you request.

**The `build` task is our main task.**

**When you have created a new component, be sure to run the `inject` task.**


These are our main tasks:

* `build`: Builds the whole application and puts it in the `build/` folder
* `inject`: Task which will inject all CSS, JS and Bower dependencies into `index.html`
* `clean`: Deletes `build/` folder

These tasks are 'helper' tasks, and should normally not be used.

* `help`: Will return a list of all tasks.
* `wiredep`: Injects all Bower dependencies into `index.html`
* `inject-css`: Will look for CSS files in the `src/app/assets/css` folder and place them in the `index.html` file
* `inject-css`: Will look for JS files in the `src/app/` folder and place them in the `index.html` file
* `fonts`: Copies fonts to the `build/` folder
* `images`: Compresses and copies images to the `build/` folder

#### Karma

We use [Karma](https://karma-runner.github.io/) as our task runner for testing. We have multiple libraries for actual testing:

* The framework we'll be using for the unittests is called [Mocha](https://mochajs.org/).
* We'll include another library, called [Chai](http://chaijs.com/) for advanced assertions. 
* To be able to use this powerful assertion library with [Promises](http://andyshora.com/promises-angularjs-explained-as-cartoon.html), we include [chai-as-promised](https://github.com/domenic/chai-as-promised#chai-assertions-for-promises).
* The [Sinon](http://sinonjs.org/) library is used for mocking and stubs.
* The [Faker.js](https://github.com/Marak/faker.js) library is used for generating fake data (ex. fake first name, fake email, ...)

You can find the main API for Chai [here](http://chaijs.com/api/bdd/).
Karma, and all the plugins we use, will be installed when you run `npm install`.

You can run all tests with `./node_modules/karma/bin/karma start`. 
Yes, the `./node_modules/` part is required, because Karma searches for it's plugins relative to it's install directory.

You can find the code coverage report in the `coverage/` directory after running the tests. 
The coverage report is a HTML file, located in a subdirectory of the `coverage` directory. (the subdirectory name starts with `PhantomJS`)