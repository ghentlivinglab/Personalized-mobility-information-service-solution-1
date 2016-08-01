/**
 * Created by thibault on 2/28/16.
 */

angular.module("mobiliteit").factory("AuthService",
    ["$localStorage", "$auth", "$q", "$http", "$state", "$timeout", "apiBaseURL", "$stomp", "alertify", "$filter", "Notification", "$rootScope",
        function ($localStorage, $auth, $q, $http, $state, $timeout, apiBaseURL, $stomp, alertify, $filter, Notification, $rootScope) {
            // $stomp.setDebug(function (args) {
            // console.log(args);
            // });

            var currentUserID = $localStorage.currentUserID;
            var subscription;

            var subscribe = function () {
                $stomp.connect(apiBaseURL + '/ws')
                    .then(function () {
                        console.log("Connected to Realtime Event Tracking Live™!");
                        subscription = $stomp.subscribe('/user/' + currentUserID + '/events', function (payload) {
                            var text = payload.description;
                            if (!text) {
                                text = $filter("translateFilter")(payload.type.type);
                            }

                            var href = $state.href('app.event', {event_id: payload.id});

                            Notification.success({
                                    title: 'Nieuwe gebeurtenis',
                                    message: '<p>'+text+ '</p> <a href="' + href + '"><div>' + "Klik hier voor meer info" + '</div></a>'

                                // message: text
                                })
                                .catch(function () {
                                    alertify.success("Nieuwe gebeurtenis: " + text);
                                });

                            $rootScope.$broadcast("$newEvent", payload);
                        });
                    })
                    .catch(function (err) {
                        console.error(err);
                    });
            };

            var refreshAccessToken = function () {
                return $q(function (resolve, reject) {
                    $http({
                        method: "POST",
                        url: apiBaseURL + "/access_token/",
                        data: {
                            refresh_token: $localStorage.refreshToken
                        }
                    })
                        .then(function (response) {
                            $localStorage.accessToken = response.data.token;

                            $localStorage.accessTokenExpiry = response.data.exp;
                            var newExp = response.data.exp.replace(/\..*$/, "Z");
                            console.log(newExp);
                            var exp = new Date(newExp);
                            var millisecondsToExp = exp.getTime() - (new Date()).getTime();
                            $timeout(refreshAccessToken, millisecondsToExp);
                            subscribe();
                            resolve();
                        })
                        .catch(function (response) {
                            $auth.logout();
                            $localStorage.$reset();
                            $state.go("index");
                            reject(response);
                        })
                    ;
                });
            };

            return {
                getCurrentUserID: function () {
                    return currentUserID;
                },
                setCurrentUserID: function (id) {
                    currentUserID = id;
                    $localStorage.currentUserID = id;
                },
                isLoggedIn: function () {
                    return $localStorage.currentUserID != undefined;
                },
                refreshAccessToken: refreshAccessToken,
                regularLogin: function (email, password) {
                    return $q(function (resolve, reject) {
                        $auth.login({
                                email: email,
                                password: password
                            })
                            .then(function (response) {
                                var refreshToken = response.data.token;
                                var userId = response.data.user_id;
                                var role = response.data.role;
                                var created = (response.status == 201);

                                // Save the current user in local storage
                                currentUserID = userId;
                                $localStorage.currentUserID = userId;
                                $localStorage.refreshToken = refreshToken;

                                if (!response.data.email_verified) {
                                    reject({
                                        status: 403
                                    });
                                }

                                refreshAccessToken().then(function () {
                                    resolve(role, created);
                                })
                            })
                            .catch(function (response) {
                                reject(response);
                            });
                    });
                },
                facebookLogin: function () {
                    return $q(function (resolve, reject) {
                        $auth.authenticate("facebook")
                            .then(function (response) {
                                var refreshToken = response.data.token;
                                var userId = response.data.user_id;
                                var role = response.data.role;
                                var created = response.status == 201;
                                
                                // Save the current user in local storage
                                currentUserID = userId;
                                $localStorage.currentUserID = userId;
                                $localStorage.refreshToken = refreshToken;

                                refreshAccessToken().then(function () {
                                    resolve({role: role, created: created});
                                });
                            })
                            .catch(function (err) {
                                reject(err);
                            });
                    });
                },
                googleLogin: function () {
                    return $q(function (resolve, reject) {
                        $auth.authenticate("google")
                            .then(function (response) {
                                var refreshToken = response.data.token;
                                var userId = response.data.user_id;
                                var role = response.data.role;
                                var created = response.status == 201;
                                // Save the current user in local storage
                                currentUserID = userId;
                                $localStorage.currentUserID = userId;
                                $localStorage.refreshToken = refreshToken;

                                refreshAccessToken().then(function () {
                                    resolve({role: role, created: created});
                                });
                            })
                            .catch(function (err) {
                                reject(err);
                            });
                    });
                },
                forgotPassword: function (email, captchaResponse) {
                    return $q(function (resolve, reject) {
                        $http({
                            method: "POST",
                            url: apiBaseURL + "/user/forgot_password",
                            data: {email: email},
                            headers: {"X-Captcha-Response": captchaResponse}
                        }).then(resolve).catch(reject);
                    });
                },
                subscribe: subscribe,
                disconnect: function () {
                    if (subscription) {
                        subscription.unsubscribe();
                    }
                    try {
                        $stomp.disconnect(function () {
                            console.log('Disconnected from Realtime Event Tracking Live™.')
                        });
                    } catch (e) {
                        console.error("Error while disconnecting: ", e);
                    }

                }
            }
        }]);