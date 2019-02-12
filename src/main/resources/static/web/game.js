var app = new Vue({
    el: "#main",
    data: {
        columns: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10"],
        rows: ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"],
        id: 0,
        allShipLocations: [],
        gamePlayerUser: "",
        gamePlayerEnemy: "",
        gameData: {},
        helpfulMessage: "",
        state: "horizontal",
        currentShipObject: {},
        arrayOfCells: [],
        arrayOfShips: [],
        shipsHaveBeenPlaced: false,
        indexOfShip: 0,
        salvoes: [],
        salvoesToPost:{},
        ships: [
            {
                name: "Panda",
                image: "images/panda-bear.png",
                loop: 5,
                horizontalRule: 7,
                verticalRule: 6,
                counter: 0,
                },
            {
                name: "Llama",
                image: "images/llama.png",
                loop: 4,
                horizontalRule: 8,
                verticalRule: 7,
                counter: 0,

               },
            {
                name: "Raccoon",
                image: "images/raccoon.png",
                loop: 3,
                horizontalRule: 9,
                verticalRule: 8,
                counter: 0,
               },
            {
                name: "Fox",
                image: "images/fox.png",
                loop: 3,
                horizontalRule: 9,
                verticalRule: 8,
                counter: 0,
               },
            {
                name: "Hedgehog",
                image: "images/hedgehog.png",
                loop: 2,
                horizontalRule: 10,
                verticalRule: 9,
                counter: 0,
                }
        ],
    },
    created: function () {
        this.findTheID();
        this.getData(this.id);
    },
    methods: {
        increment(i) {
            app.ships[i].counter++;
        },
        decrement(i) {
            app.ships[i].counter = 0;
        },
        findTheID: function () {
            this.id = location.search.split("=")[1];
        },
        getData: function (id) {
            fetch("/api/game_view/" + id)
                .then(response => {
                    if (response.ok) {
                        return response.json()
                            .then(json => {
                                app.gameData = json;
                                app.printingTheShips();
                            app.salvoes = [];
                                if (app.gameData.gameView.ships.length > 0) {
                                    app.shipsHaveBeenPlaced = true;
                                } else {
                                    app.shipsHaveBeenPlaced = false;
                                }
                                app.displayingThePlayers();
                                //                                app.printingTheSalvoesAndHits("user_salvoes", "E");
                                //                                app.printingTheSalvoesAndHits("enemy_salvoes", "U");
                            })
                    } else {
                        app.helpfulMessage = "You are not authorised to see this game!"
                    }
                })
                .catch(error => console.log(error))
        },
        printingTheShips: function () {
            for (var i = 0; i < app.gameData.gameView.ships.length; i++) {
                for (var j = 0; j < app.gameData.gameView.ships[i].locations.length; j++) {
                    var addingTheShipsClass = document.getElementById("U" + app.gameData.gameView.ships[i].locations[j]);
                    if (app.gameData.gameView.ships[i].type == "Panda") {
                        addingTheShipsClass.classList.add("panda");
                    }
                    if (app.gameData.gameView.ships[i].type == "Raccoon") {
                        addingTheShipsClass.classList.add("raccoon");
                    }
                    if (app.gameData.gameView.ships[i].type == "Llama") {
                        addingTheShipsClass.classList.add("llama");
                    }
                    if (app.gameData.gameView.ships[i].type == "Hedgehog") {
                        addingTheShipsClass.classList.add("hedgehog");
                    }
                    if (app.gameData.gameView.ships[i].type == "Fox") {
                        addingTheShipsClass.classList.add("fox");
                    }

                }
            }
        },
        displayingThePlayers: function () {
            for (var i = 0; i < app.gameData.gameView.games.gamePlayers.length; i++) {
                if (app.gameData.gameView.games.gamePlayers[i].id == app.id) {
                    app.gamePlayerUser = app.gameData.gameView.games.gamePlayers[i].player.email;
                } else {
                    app.gamePlayerEnemy = app.gameData.gameView.games.gamePlayers[i].player.email;
                }
            }
        },
        printingTheSalvoesAndHits: function (salvoArray, tableId) {
            for (var i = 0; i < app.gameData.gameView[salvoArray].length; i++) {
                for (var j = 0; j < app.gameData.gameView[salvoArray][i].locations.length; j++) {
                    //                    console.log(app.gameData)
                    var cell = document.getElementById(tableId + app.gameData.gameView[salvoArray][i].locations[j]);
                    //                   console.log(cell)
                    if (cell.classList.contains("ship-location")) {
                        cell.removeAttribute("class", "ship-location");
                        cell.setAttribute("class", "hits-location")
                    } else {
                        cell.setAttribute("class", "salvo-location");
                    }
                }
            }

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
                        window.location.href = "http://localhost:8080/web/games.html";
                    } else {
                        app.helpfulMessage = "Logout was not succesful";
                    }

                })
                .catch(e => {
                    console.log(e)
                })
        },
        postShips: function (id) {
            var fetchConfig =
                fetch("/api/games/players/" + id + "/ships", {
                    credentials: 'include',
                    method: 'POST',
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(app.arrayOfShips)
                }).then(response => {
                    app.shipsHaveBeenPlaced = true;
                    app.getData(this.id);
                })
                .catch(function (error) {
                    console.log(error);
                })
        },
        changingTheState(state) {
            app.state = state;
        },
        hoveringEffectBeforePlacement(cell) {
            if (app.arrayOfCells.length > 0) {
                app.removeTheClassFromTheCells(app.arrayOfCells);
            }
            if (app.state == "horizontal") {
                var array = app.horizontalHover(cell);
                array.forEach(function (element) {
                    if (document.getElementById("U" + element) != null) {
                        document.getElementById("U" + element).classList.add("hover_class");
                    }
                });
            } else {
                var array = app.verticalHover(cell);
                array.forEach(function (element) {
                    if (document.getElementById("U" + element) != null) {
                        document.getElementById("U" + element).classList.add("hover_class");
                    }
                });
            }
        },
        currentShipInfo(index) {
            app.currentShipObject = app.ships[index];
            app.indexOfShip = index;
            if (app.currentShipObject.counter == 0) {
                app.addBlinker(app.currentShipObject.name)
            }
        },
        horizontalHover(cell) {
            var arrayOfCells = [];
            var elementNumber = parseInt(cell.substr(2));
            var elementLetter = cell.slice(1, 2);

            for (var i = 0; i < app.currentShipObject.loop; i++) {
                if (!isNaN(elementNumber)) {
                    if (elementNumber < app.currentShipObject.horizontalRule) {
                        var newCell = elementNumber + i;
                        arrayOfCells.push(elementLetter + newCell);
                    }
                }
            }
            app.arrayOfCells = arrayOfCells;
            return app.arrayOfCells;
        },
        verticalHover(cell) {
            var arrayOfCells = [];
            var elementNumber = parseInt(cell.substr(2));
            var elementLetter = cell.slice(1, 2);
            var index;

            for (var i = 0; i < app.rows.length; i++) {
                if (elementLetter == app.rows[i]) {
                    index = i;
                }
            }
            if (index < app.currentShipObject.verticalRule) {
                var verticalArray = app.rows.slice(index, index + app.currentShipObject.loop);
                for (var i = 0; i < verticalArray.length; i++) {
                    arrayOfCells.push(verticalArray[i] + elementNumber)
                }
            }
            app.arrayOfCells = arrayOfCells;
            return app.arrayOfCells;
        },
        removeTheClassFromTheCells(array) {
            for (var i = 0; i < array.length; i++) {
                document.getElementById("U" + array[i]).classList.remove("hover_class");
            }
        },
        placingTheShip() {
            if (app.currentShipObject.counter == 0) {
                var overlap = app.rules(app.arrayOfCells)
                if (overlap == false && app.arrayOfCells.length > 0) {
                    app.increment(app.indexOfShip);
                    for (var i = 0; i < app.arrayOfCells.length; i++) {
                        var cell = document.getElementById("U" + app.arrayOfCells[i]);
                        if (app.currentShipObject.name == "Panda") {
                            cell.classList.add("panda");
                        }
                        if (app.currentShipObject.name == "Raccoon") {
                            cell.classList.add("raccoon");
                        }
                        if (app.currentShipObject.name == "Llama") {
                            cell.classList.add("llama");
                        }
                        if (app.currentShipObject.name == "Hedgehog") {
                            cell.classList.add("hedgehog");
                        }
                        if (app.currentShipObject.name == "Fox") {
                            cell.classList.add("fox");
                        }
                    }
                    var object = {
                        type: app.currentShipObject.name,
                        shipLocations: app.arrayOfCells,
                    };

                    app.removeBlinker(app.currentShipObject.name);
                    app.removeTheClassFromTheCells(app.arrayOfCells);
                    app.allShipLocations.push(...app.arrayOfCells);
                    app.arrayOfShips.push(object);
                    app.currentShipObject = {};
                    //                 app.addingTheEventListener(app.arrayOfCells, app.indexOfShip)
                } else {
                    app.decrement(app.indexOfShip);
                    console.log("Not a valid placement!")
                }
            } else {
                console.log("Can't place ship again!")
            }
        },
        rules(shipArray) {
            let found = shipArray.some(r => app.allShipLocations.includes(r))
            return found;
        },
        goHome() {
            location.assign("/web/games.html");
        },
        addBlinker(shipId) {
            var image = document.getElementById(shipId);
            image.classList.add("blink");
        },
        removeBlinker(shipId) {
            var image = document.getElementById(shipId);
            image.classList.remove("blink");
        },
        addingTheEventListener(array, ix) {
            console.log(array)
            console.log(ix)
            for (var i = 0; i < array.length; i++) {
                var arrayElement = document.getElementById("U" + array[i]);
                arrayElement.addEventListener('click', app.removeTheShipAfterAlreadyPlaced("hi"))
            }

        },
        removeTheShipAfterAlreadyPlaced(cellID) {
            console.log(cellID);
            //            var findTheCellClass = document.getElementById(cellID).classList;
            //            var theClass = findTheCellClass.value;
            //            if (findTheCellClass.contains("panda") || findTheCellClass.contains("llama") || findTheCellClass.contains("raccoon") || findTheCellClass.contains("hedgehog") || findTheCellClass.contains("fox")) {
            //                var allCellsContainingTheClass = document.getElementsByClassName(theClass);
            //                var array = [];
            //                for (var i = 0; i < allCellsContainingTheClass.length; i++) {
            //                    var locationCells = allCellsContainingTheClass[i].id;
            //                    locationCells = locationCells.substr(1);
            //                    array.push(locationCells);                  
            //                }
            //                  console.log(array)

        },
        postSalvoes: function (salvoInfo, id) {
            var fetchConfig =
                fetch("/api/games/players/" + id + "/salvos", {
                    credentials: 'include',
                    method: 'POST',
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(salvoInfo)

                }).then(response => {
//                    console.log(response)

                    response.json()
                        .then(json => {
                       app.getData(id)
                        })
                })

                .catch(function (error) {
                    console.log(error);
                })
        },
        getSalvoId: function (id) {
            var cellId = id;
            var dataCell = document.getElementById("E" + id);

            if (dataCell.classList.contains("salvo-location")) {
                var index = app.salvoes.indexOf(cellId);
                if (index > -1) {
                    app.salvoes.splice(index, 1)
                    var toBeRemoved = document.getElementById("E" + id);
                    toBeRemoved.classList.remove("salvo-location");
                }
            } else {
                dataCell.classList.add("salvo-location")
                app.salvoes.push(cellId)
            }
            app.salvoRules(app.salvoes);
            var newSalvoes = {
                salvoLocations: app.salvoes,
            }
            app.salvoesToPost = newSalvoes;
        },
        salvoRules: function (salvoes) {
            if (salvoes.length > 5) {
                var lastElement = salvoes.pop();
                var toBeRemoved = document.getElementById("E" + lastElement);
                toBeRemoved.classList.remove("salvo-location");
            }
        },
    }
});
