/**
 * Created by thibault on 2/24/16.
 */
angular.module("mobiliteit")
    //.constant("apiBaseURL", "/api");
    .constant("apiBaseURL", window.location.host.match(/localhost/) ? "http://localhost:8080/api" : '/api')
    .constant("ApiKeys", {
        "Geocoding": "https://maps.googleapis.com/maps/api/geocode/json?key=AIzaSyB6KVvQS9huHG7albCYOH01rGCV1HB19DE"
    });
