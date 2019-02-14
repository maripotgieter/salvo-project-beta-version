package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    ShipRepository shipRepository;

    @RequestMapping(path = "/games", method = RequestMethod.GET)
    public Map<String, Object> getAllGames(Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<>();
        if (authentication != null) {
            dto.put("player", makePlayerDTO(playerRepository.findByUserName(authentication.getName())));
            dto.put("games", gameRepository
                    .findAll()
                    .stream()
                    .map(game -> makeGameDTO(game))
                    .collect(toList()));

        } else {
            dto.put("games", gameRepository
                    .findAll()
                    .stream()
                    .map(game -> makeGameDTO(game))
                    .collect(toList()));
        }
        return dto;
    }

    private Map<String, Object> makeGameDTO(Game game) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", game.getId());
        dto.put("created", game.getCreationDate());
        dto.put("gamePlayers", game.getGamePlayers().stream().map(gamePlayer -> makeGamePlayerDTO(gamePlayer)).collect(toList()));
        return dto;
    }

    private Map<String, Object> makeGamePlayerDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", gamePlayer.getId());
        dto.put("player", makePlayerDTO(gamePlayer.getPlayer()));
        return dto;
    }

    private Map<String, Object> makePlayerDTO(Player player) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", player.getId());
        dto.put("email", player.getUserName());
        return dto;
    }

    @RequestMapping(path = "/game_view/{id}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> findGamePlayerId(@PathVariable Long id, Authentication authentication) {
        GamePlayer gamePlayer = gamePlayerRepository.findOne(id);
        if (authentication != null) {
            Player player = gamePlayer.getPlayer();
            Player currentUser = playerRepository.findByUserName(authentication.getName());
            if (player.getId() == currentUser.getId()) {
                Map<String, Object> gameViewMap = new HashMap<>();
                gameViewMap.put("games", makeGameDTO(gamePlayer.getGame()));
                gameViewMap.put("ships", gamePlayer.getShips().stream().map(ship -> makeShipDTO(ship)).collect(toList()));
                gameViewMap.put("user_salvoes", salvoArray(gamePlayer));
                if (gamePlayer.getGame().getGamePlayers().size() > 1) {
                    gameViewMap.put("enemy_salvoes", salvoArray(getOpponent(gamePlayer)));
                    gameViewMap.put("hits", getHitsAndTypeOfShip(gamePlayer));
                }
                return new ResponseEntity<>(makeMap("gameView", gameViewMap), HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(makeMap("error", "Not authorized"), HttpStatus.UNAUTHORIZED);
            }
        } else {
            return new ResponseEntity<>(makeMap("error", "Log in"), HttpStatus.UNAUTHORIZED);
        }
    }

    private Map<String, Object> makeShipDTO(Ship ship) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("type", ship.getType());
        dto.put("locations", ship.getShipLocations());
        return dto;
    }

    private GamePlayer getOpponent(GamePlayer gamePlayer) {
        Set<GamePlayer> gamePlayerSet = gamePlayer.getGame().getGamePlayers();
        List<GamePlayer> gamePlayerList = new ArrayList<>();
        for (GamePlayer gp : gamePlayerSet) {
            if (gp != gamePlayer) {
                gamePlayerList.add(gp);
            }
        }
        GamePlayer opponent = gamePlayerList.get(0);
        return opponent;
    }

    private List<Object> salvoArray(GamePlayer gamePlayer) {
        Set<Salvo> salvoSet = gamePlayer.getSalvoes();
        List<Object> salvoList = new ArrayList<>();
        for (Salvo salvo : salvoSet) {
            salvoList.add(makeSalvoDTO(salvo));
        }
        return salvoList;
    }

    private Map<String, Object> makeSalvoDTO(Salvo salvo) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("turn", salvo.getTurn());
        dto.put("player", salvo.getGamePlayer().getId());
        dto.put("locations", salvo.getSalvoLocations());
        return dto;
    }

    @RequestMapping(path = "/scoreboard", method = RequestMethod.GET)
    public List<Object> getScores() {
        return playerRepository.findAll().stream().map(player -> toScoreDTO(player)).collect(toList());

    }

    public Map<String, Object> toScoreDTO(Player player) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("player", player.getUserName());
        dto.put("total", player.getScores().stream().mapToDouble(score -> score.getScore()).sum());
        dto.put("wins", player.getScores().stream().filter(score -> score.getScore() == 1.0).count());
        dto.put("ties", player.getScores().stream().filter(score -> score.getScore() == 0.5).count());
        dto.put("losses", player.getScores().stream().filter(score -> score.getScore() == 0.0).count());
        return dto;
    }

    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createUser(@RequestParam String userName, String password) {
        if (userName.isEmpty()) {
            return new ResponseEntity<>(makeMap("error", "No name given"), HttpStatus.FORBIDDEN);
        } else {
            Player user = playerRepository.findByUserName(userName);
            if (user != null) {
                return new ResponseEntity<>(makeMap("error", "Name in use"), HttpStatus.FORBIDDEN);
            } else {
                Player newPlayer = playerRepository.save(new Player(userName, password));
                return new ResponseEntity<>(makeMap("player", makePlayerDTO(newPlayer)), HttpStatus.CREATED);
            }
        }
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    @RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createGame(Authentication authentication) {
        Player currentUser = playerRepository.findByUserName(authentication.getName());
        if (currentUser != null) {
            Game newGame = new Game();
            gameRepository.save(newGame);
            GamePlayer newGamePlayer = new GamePlayer(new Date(), newGame, currentUser);
            gamePlayerRepository.save(newGamePlayer);
            return new ResponseEntity<>(makeMap("gpId", newGamePlayer.getId()), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(makeMap("error", "Login"), HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(path = "/games/{id}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> joinGame(@PathVariable Long id, Authentication authentication) {
        Player currentUser = playerRepository.findByUserName(authentication.getName());
        if (currentUser != null) {
            Game currentGame = gameRepository.findOne(id);
            if (currentGame != null) {
                if (currentGame.getPlayers().size() < 2) {
                    GamePlayer newGamePlayer = new GamePlayer(new Date(), currentGame, currentUser);
                    gamePlayerRepository.save(newGamePlayer);
                    return new ResponseEntity<>(makeMap("gpId", newGamePlayer.getId()), HttpStatus.CREATED);
                } else {
                    return new ResponseEntity<>(makeMap("error", "Game is full!"), HttpStatus.FORBIDDEN);
                }
            } else {
                return new ResponseEntity<>(makeMap("error", "No such game"), HttpStatus.FORBIDDEN);
            }
        } else {
            return new ResponseEntity<>(makeMap("error", "Login"), HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(value = "/games/players/{id}/ships", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createShips(@PathVariable Long id,
                                                           Authentication authentication,
                                                           @RequestBody Set<Ship> ships) {
        if (authentication != null) {
            Player currentUser = playerRepository.findByUserName(authentication.getName());
            if (currentUser != null) {
                GamePlayer gamePlayer = gamePlayerRepository.getOne(id);
                if (gamePlayer != null) {
                    if (gamePlayer.getPlayer().getUserName() == currentUser.getUserName()) {
                        if (gamePlayer.getShips().size() == 0) {
                            for (Ship ship : ships) {
                                ship.setGamePlayer(gamePlayer);
                                shipRepository.save(ship);
                            }
                            return new ResponseEntity<>(makeMap("success", "Ships created"), HttpStatus.CREATED);
                        } else {
                            return new ResponseEntity<>(makeMap("error", "Already placed ships"), HttpStatus.FORBIDDEN);
                        }
                    } else {
                        return new ResponseEntity<>(makeMap("error", ""), HttpStatus.UNAUTHORIZED);
                    }
                } else {
                    return new ResponseEntity<>(makeMap("error", ""), HttpStatus.UNAUTHORIZED);
                }
            } else {
                return new ResponseEntity<>(makeMap("error", "Login"), HttpStatus.UNAUTHORIZED);
            }
        } else {
            return new ResponseEntity<>(makeMap("error", "Login"), HttpStatus.UNAUTHORIZED);
        }

    }

    @Autowired
    SalvoRepository salvoRepository;

    @RequestMapping(path = "/games/players/{id}/salvos", method = RequestMethod.POST)
    private ResponseEntity<Map<String, Object>> postSalvoes(@PathVariable Long id,
                                                            Authentication authentication,
                                                            @RequestBody Salvo salvo) {
        if (authentication != null) {
            Player currentUser = playerRepository.findByUserName(authentication.getName());
            if (currentUser != null) {
                GamePlayer gamePlayer = gamePlayerRepository.getOne(id);
                if (gamePlayer != null) {
                    if (gamePlayer.getPlayer().getUserName() == currentUser.getUserName()) {
                        salvo.setTurn(getLastTurn(gamePlayer));
                        salvo.setGamePlayer(gamePlayer);
                        salvoRepository.save(salvo);
                        return new ResponseEntity<>(makeMap("success", "salvoes created"), HttpStatus.CREATED);
                    } else {
                        return new ResponseEntity<>(makeMap("error", ""), HttpStatus.UNAUTHORIZED);
                    }
                } else {
                    return new ResponseEntity<>(makeMap("error", ""), HttpStatus.UNAUTHORIZED);
                }
            } else {
                return new ResponseEntity<>(makeMap("error", ""), HttpStatus.UNAUTHORIZED);

            }
        } else {
            return new ResponseEntity<>(makeMap("error", "Login"), HttpStatus.UNAUTHORIZED);
        }
    }

    private Integer getLastTurn(GamePlayer gamePlayer) {
        Integer lastTurn = 0;
        for (Salvo salvo : gamePlayer.getSalvoes()) {
            Integer turn = salvo.getTurn();
            if (lastTurn < turn) {
                lastTurn = turn;
            }
        }
        return lastTurn + 1;
    }

    private List<String> getShipLocations(GamePlayer gamePlayer) {
        List<String> shipLocations = new ArrayList<>();
        Set<Ship> ships = gamePlayer.getShips();
        for (Ship ship : ships) {
            List<String> eachLocation = ship.getShipLocations();
            for (String location : eachLocation) {
                shipLocations.add(location);
            }
        }
        return shipLocations;
    }

    private List<String> getSalvoLocations(GamePlayer gamePlayer) {
        List<String> salvoLocations = new ArrayList<>();
        Set<Salvo> salvos = gamePlayer.getSalvoes();
        for (Salvo salvo : salvos) {
            List<String> eachLocation = salvo.getSalvoLocations();
            for (String location : eachLocation) {
                salvoLocations.add(location);
            }
        }
        return salvoLocations;
    }

    private List<String> getHits(GamePlayer gamePlayer) {
        List<String> getHits = new ArrayList<>();
        List<String> shipLocations = getShipLocations(getOpponent(gamePlayer));
        List<String> salvoLocations = getSalvoLocations(gamePlayer);
        for (String salvoLocation : salvoLocations) {
            for (String shipLocation : shipLocations) {
                if (salvoLocation == shipLocation) {
                    getHits.add(salvoLocation);
                }
            }
        }
        return getHits;
    }

    private Map<String, List<String>> getHitsAndTypeOfShip(GamePlayer gamePlayer) {
        Map<String, List<String>> map = new LinkedHashMap<>();
        Set<Ship> ships = getOpponent(gamePlayer).getShips();
        for (Ship ship : ships) {
            List<String> eachLocation = ship.getShipLocations();
            String shipType = ship.getType();
            List<String> list = new ArrayList<>();
            for (String loc : eachLocation) {
                if (getHits(gamePlayer).contains(loc))
                    list.add(loc);
            }
            map.put(shipType, list);
        }
        return map;
    }



}