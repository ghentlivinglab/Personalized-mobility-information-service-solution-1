## Backend


#### Running locally
`./gradlew jettyRunWar`
This will run the backend on `http://localhost:8080/api/`.

#### Testing
`./gradlew test`
An HTML report of the test will be in `build/reports/tests/index.html`.

For coverage data, use `./gradlew jacocoTestReport`.
An HTML report of the coverage will be in `build/reports/jacoco/test/html/index.html`

#### Generate IntelliJ project
`./gradlew idea`
