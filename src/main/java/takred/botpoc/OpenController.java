package takred.botpoc;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import takred.botpoc.client.GameClient;
import takred.botpoc.client.RegisterResponse;
import takred.botpoc.client.RegisterResponseGuess;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@RestController
public class OpenController {
    private final GameClient gameClient;
    private RegisterResponse registerResponse;
    private RegisterResponse startResponse;
    private RegisterResponseGuess guessResponse;
    UUID idGame;

    public OpenController(GameClient gameClient) {
        this.gameClient = gameClient;
    }

    @RequestMapping("/open")
    public String open() {
        if (registerResponse == null) {
            registerResponse = gameClient.register("Kostya");
            return registerResponse.getMessage();
        }
        return gameClient.register("Kostya").getMessage();
    }

    @RequestMapping(value = "/guess")
    public RegisterResponseGuess guess() {
        RegisterResponseGuess registerResponseGuess = gameClient.guess(idGame, ThreadLocalRandom.current().nextInt(0, 10000));
        if (registerResponseGuess.getResult() != null){
            guessResponse = registerResponseGuess;
        }
        return registerResponseGuess;
    }

    @RequestMapping("/exit")
    public String exit() {
        return gameClient.logout("Kostya");
    }

    @RequestMapping(value = "/login")
    public String login() {
        return gameClient.login("Kostya");
    }

    @RequestMapping(value = "/start")
    public RegisterResponse start() {
        if (idGame == null) {
            startResponse = gameClient.start(registerResponse.getUuid());
            idGame = startResponse.getUuid();
            return startResponse;
        }
        return gameClient.start(registerResponse.getUuid());
    }

    @RequestMapping(value = "/bot_start")
    public String startAll() {
        open();
        start();
        guess();
        exit();
        return "Всё успешно.";
    }

    @RequestMapping(value = "/bot")
    public String bot(){
        if (registerResponse == null){
            return open();
        }
        if (idGame == null){
            return start().getMessage();
        }
        if (guessResponse == null){
            return guess().getMessage();
        }
        if (guessResponse.getCount() != 100){
            return guess().getMessage();
        }
        return exit();
    }

    @RequestMapping(value = "/bot_start_bisect")
    public String startAllBisect() {
        open();
        start();
        finderBisection();
        exit();
        return "Успешно.";
    }

    public void finderBisection() {
//        public boolean contains(List<Integer> allNumbers, int number){
        List<Integer> allPossibleOptions = new ArrayList<>();
        for (int i = 0; i < 10001; i++) {
            allPossibleOptions.add(i);
        }
        int border = allPossibleOptions.size() / 2;
        int previousBorder;
        for (int i = 0; i < 100; i++) {
            guessResponse = gameClient.guess(idGame, border);
            if (guessResponse.getResult().equals("<")) {
                previousBorder = border;
                border = border / 2;
            } else if (guessResponse.getResult().equals(">")) {
                previousBorder = border;
                border = border + ((previousBorder - border) / 2);
            }
            System.out.println(allPossibleOptions.get(border));
            if (guessResponse.getResult().equals("=")) {
                break;
            }
        }
    }
}