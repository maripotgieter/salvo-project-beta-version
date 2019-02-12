var app = new Vue({
    el: "#main",
    data: {
        games: {},
        gameInfo: {},
        scoreboard: [],
        userName: "",
        password: "",
        currentUser: "",
        loggedIn: false,
        helpfulMessage: "",
        response: 0,
        gpId: 0,
    },
    created: function () {
        this.getData();
        this.getLeaderboard();
    },
    methods: {
        getData: function () {
            fetch("/api/games", {
                    method: "GET",
                    credentials: "include",
                })
                .then(r => r.json())
                .then(json => {
                    app.games = json;
                    if (app.games.player != null) {
                        app.currentUser = app.games.player.email;
                        app.loggedIn = true;
                        app.gameInformation();
                    } else {
                        app.helpfulMessage = "";
                    }

                })
                .catch(e => console.log(e));
        },
        gameInformation: function () {
            var empty = [];
            for (var i = 0; i < app.games.games.length; i++) {
                var date = new Date(app.games.games[i].created).toLocaleString();
                var numberOfGame = app.games.games[i].id;
                var playerOne = app.games.games[i].gamePlayers[0].player.email;
                var gpIdOne = app.games.games[i].gamePlayers[0].id;
                if (app.games.games[i].gamePlayers[1] != null) {
                    var playerTwo = app.games.games[i].gamePlayers[1].player.email;
                    var gpIdTwo = app.games.games[i].gamePlayers[1].id;
                } else {
                    var playerTwo = null;
                    var gpIdTwo = "";
                }
                var object = {
                    game: numberOfGame,
                    emailOne: playerOne,
                    gpIdOne: gpIdOne,
                    emailTwo: playerTwo,
                    gpIdTwo: gpIdTwo,
                    date: date,
                }
                empty.push(object);
            }
            app.gameInfo = empty;
        },
        getLeaderboard: function () {
            fetch("/api/scoreboard", {
                    method: "GET",
                    credentials: "include",
                })
                .then(r => r.json())
                .then(json => {
                    app.scoreboard = json;
                })
                .catch(e => console.log(e));
        },
        login: function () {
            var fetchConfig =
                fetch("/api/login", {
                    credentials: 'include',
                    method: 'POST',
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                    body: 'userName=' + this.userName + '&password=' + this.password,
                })
                .then(r => {
                    if (r.status == 200) {
                        app.getData();
                        app.gameInformation();
                        app.loggedIn = true;
                        app.helpfulMessage = "Success!"
                    } else {
                        app.helpfulMessage = "Login was not successful! Please try again!"
                        app.loggedIn = false;
                    }
                })
                .catch(e => {
                    console.log(e)
                })
        },
        logout: function () {
            var fetchConfig =
                fetch("/api/logout", {
                    credentials: 'include',
                    method: 'POST',
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                })
                .then(r => {
                    if (r.status == 200) {
                        app.loggedIn = false;
                        app.getData();
                        app.helpfulMessage = "Please login or signup!";
                    } else {
                        app.helpfulMessage = "Logout was not succesful";
                        app.loggedIn = true;
                        app.currentUser = "";
                    }

                })
                .catch(e => {
                    console.log(e)
                })
        },
        createUser: function () {
            var fetchConfig =
                fetch("/api/players", {
                    credentials: 'include',
                    method: 'POST',
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                    body: 'userName=' + this.userName + '&password=' + this.password,

                }).then(r => {
                    if (r.status == 201) {
                        app.loggedIn = true;
                        app.login();
                    } else if (r.status == 403) {
                        app.helpfulMessage = "Name already in use. Please try again!";
                        app.loggedIn = false;
                        app.currentUser = "";
                    } else {
                        app.helpfulMessage = "No name given!";
                        app.loggedIn = false;
                        app.currentUser = "";
                    }
                })

                .catch(function (error) {
                    console.log(error);
                })
        },
        validateEmail(email) {
            var pattern = /^[a-zA-Z0-9\-_]+(\.[a-zA-Z0-9\-_]+)*@[a-z0-9]+(\-[a-z0-9]+)*(\.[a-z0-9]+(\-[a-z0-9]+)*)*\.[a-z]{2,4}$/;
            if (pattern.test(email)) {
                app.createUser();
            } else {
                app.helpfulMessage = "Bad email address: " + email + ". Please try again!";
            }
        },
        returnToGame: function (id) {
            location.assign("/web/game.html?gp=" + id);
        },
        createGame: function () {
            var fetchConfig =
                fetch("/api/games", {
                    credentials: 'include',
                    method: 'POST',
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                })
                .then(response => {
                    console.log(response)
                    if (response.status == 201) {
                        response.json()
                            .then(json => {
                                app.gpId = json.gpId;
                                location.assign("game.html?gp=" + app.gpId);
                            })
                            .catch(function (error) {
                                console.log(error);
                            })
                    } else {
                        app.helpfulMessage = "Could not create game! Please try again!"
                    }
                })
        },
        joinGame: function (gameId) {
            var fetchConfig =
                fetch("/api/games/" + gameId + "/players", {
                    credentials: 'include',
                    method: 'POST',
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                })
                .then(response => {
                    console.log(response)
                    if (response.status == 201) {
                        response.json()
                            .then(json => {
                                app.gpId = json.gpId;
                                location.assign("game.html?gp=" + app.gpId);
                            })
                            .catch(function (error) {
                                console.log(error);
                            })
                    } else {
                        app.helpfulMessage = "Could not join game! Please try again!"
                    }
                })
        },
    }
});
