package app.controllers;

import app.inputBeans.PlayerCreationInput;
import app.game.Game;
import app.game.Player;
import app.inputBeans.RaceInput;
import app.inputBeans.RevealInput;
import app.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;

/**
 * Created by Phaethon on 22-Jan-17
 */
@Controller
public class GamePage {
    private GameService gameService;

    @Autowired
    public GamePage(GameService gameService) {
        this.gameService = gameService;
    }

    @RequestMapping("/game")
    public String home(HttpSession session) {
        return "game";
    }

    @RequestMapping("/game/restart")
    public String restart(HttpSession session) {
        gameService.stopGame();
        session.removeAttribute("player");
        gameService.startGame();
        return "restart";
    }

    @RequestMapping("/game/start")
    public String start() {
        gameService.startGame();
        return "game";
    }

    @RequestMapping("/game/addPlayer")
    public String addPlayer(PlayerCreationInput playerInput, HttpSession session) {
        if (session.getAttribute("player") == null && playerInput != null && playerInput.getName() != null) {
            Game game = gameService.getGame();
            Player player = gameService.addPlayer(game, playerInput.getName());
            session.setAttribute("player", player);
        }
        return "game";
    }

    @RequestMapping("/game/quit")
    public String quit(PlayerCreationInput playerInput, HttpSession session) {
        if (session.getAttribute("player") != null && playerInput != null && playerInput.getName() != null) {
            Game game = gameService.getGame();
            Player player = new Player(playerInput.getName(), game);
            if (gameService.quitPlayer(game, player)) {
                session.removeAttribute("player");
            }
        }
        return "game";
    }

    @RequestMapping(path = "/game/reveal", method = RequestMethod.POST)
    public String reveal(RevealInput input) {
        Game game = gameService.getGame();
        if (game.getRevealPassword().equals(input.getPassword())) {
            gameService.revealChoices(game);
        }
        return "game";
    }

    @RequestMapping("/game/chooseRace")
    public String choose(RaceInput race, HttpSession session) {
        if (race != null && race.getName() != null && !getGame().isRevealed()) {
            Player player = getPlayer(session);
            gameService.setChosenRace(gameService.getGame(), player, race.getName());
        }
        return "game";
    }

    @ModelAttribute(name = "playerInput")
    public PlayerCreationInput getPlayerInput(HttpSession session) {
        return new PlayerCreationInput();
    }

    @ModelAttribute(name = "raceInput")
    public RaceInput getRaceInput() {
        return new RaceInput();
    }

    @ModelAttribute(name = "revealInput")
    public RevealInput getRevealInput() {
        return new RevealInput();
    }

    @ModelAttribute(name = "player")
    public Player getPlayer(HttpSession session) {
        return (Player) session.getAttribute("player");
    }

    @ModelAttribute(name = "game")
    public Game getGame() {
        return gameService.getGame();
    }
}
