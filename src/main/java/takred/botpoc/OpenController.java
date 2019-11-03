package takred.botpoc;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import takred.botpoc.client.GameClient;
import takred.botpoc.client.RegisterResponse;
import takred.botpoc.client.RegisterResponseGuess;
import takred.botpoc.client.logoutResponse;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@RestController
//@RequestMapping("/v2")
public class OpenController {
    private final GameClient gameClient;
    private RegisterResponse registerResponse;
    private RegisterResponse startResponse;
    private RegisterResponseGuess guessResponse;
    private UUID idGame;
    Map<String, Account> allPlayers = new HashMap<>();
    private int count = 0;

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
    @RequestMapping("/open/{loginName}")
    public String openRandom(@PathVariable("loginName") String loginName) {
        if (!allPlayers.containsKey(loginName)) {
            RegisterResponse registerResponseResult = gameClient.register(loginName);
            allPlayers.put(loginName, new Account(loginName, registerResponseResult.getUuid(), null, null));
            return registerResponseResult.getMessage();
        }
        return gameClient.register(loginName).getMessage();
    }

    @RequestMapping(value = "/guess")
    public RegisterResponseGuess guess() {
        RegisterResponseGuess registerResponseGuess = gameClient.guess(idGame,
                ThreadLocalRandom.current().nextInt(0, 10000));
        if (registerResponseGuess.getResult() != null){
            guessResponse = registerResponseGuess;
        }
        return registerResponseGuess;
    }

    @RequestMapping(value = "/guess/{loginName}")
    public RegisterResponseGuess guessRandom(@PathVariable("loginName") String loginName) {
        RegisterResponseGuess registerResponseGuess = gameClient.guess(allPlayers.get(loginName).getGameSessionId(),
                ThreadLocalRandom.current().nextInt(0, 10000));
        if (registerResponseGuess.getResult() != null){
            allPlayers.put(loginName, allPlayers.get(loginName).withCountTry(registerResponseGuess.getCount()));
            if (registerResponseGuess.getResult().equals("=")) {
                allPlayers.put(loginName, allPlayers.get(loginName).withGameSessionId(null));
                allPlayers.put(loginName, allPlayers.get(loginName).withCountTry(null));
            }
        }
        return registerResponseGuess;
    }

    @RequestMapping("/exit")
    public logoutResponse exit() {
        return gameClient.logout("Kostya");
    }
    @RequestMapping("/exit/{loginName}")
    public logoutResponse exitRandom(@PathVariable("loginName") String loginName) {
        logoutResponse logoutResponse = gameClient.logout(loginName);
        if (logoutResponse.getLogoutStatus()) {
            allPlayers.put(loginName, allPlayers.get(loginName).withLoginSessionId(null));
            allPlayers.put(loginName, allPlayers.get(loginName).withGameSessionId(null));
            allPlayers.put(loginName, allPlayers.get(loginName).withCountTry(null));
        }
        return logoutResponse;
    }

    @RequestMapping(value = "/login")
    public RegisterResponse login() {
        return gameClient.login("Kostya");
    }

    @RequestMapping(value = "/login/{loginName}")
    public RegisterResponse loginRandom(@PathVariable("loginName") String loginName) {
        RegisterResponse loginResponse = gameClient.login(loginName);
        if (loginResponse.getUuid() != null) {
            allPlayers.put(loginName, allPlayers.get(loginName).withLoginSessionId(loginResponse.getUuid()));
        }
        return loginResponse;
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

    @RequestMapping(value = "/start/{loginName}")
    public RegisterResponse startRandom(@PathVariable("loginName") String loginName) {
        if (allPlayers.containsKey(loginName) && allPlayers.get(loginName).getGameSessionId() == null) {
            RegisterResponse startResponseResult = gameClient.start(allPlayers.get(loginName).getLoginSessionId());
            allPlayers.put(loginName, allPlayers.get(loginName).withGameSessionId(startResponseResult.getUuid()));
            return startResponseResult;
        }
        return gameClient.start(allPlayers.get(loginName).getLoginSessionId());
    }

    @RequestMapping(value = "/bot_start")
    public String startAll() {
        open();
        start();
        guess();
        exit();
        return "Всё успешно.";
    }

    @RequestMapping(value = "/bot/{loginName}")
    public String bot(@PathVariable("loginName") String loginName){
        if (!allPlayers.containsKey(loginName)){
            return openRandom(loginName);
        }else if(allPlayers.containsKey(loginName) && allPlayers.get(loginName).getLoginSessionId() == null){
            return loginRandom(loginName).getMessage();
        }
        if (allPlayers.get(loginName).getGameSessionId() == null){
            return startRandom(loginName).getMessage();
        }
        if (allPlayers.get(loginName).getCountTry() == null){
            return guessRandom(loginName).getMessage();
        }
        if (allPlayers.get(loginName).getCountTry() != 20){
            return guessRandom(loginName).getMessage();
        }
        return exitRandom(loginName).getMessage();
    }

    @RequestMapping(value = "/bot_random_name")
    public String botRandomName() {
        String randomName = UUID.randomUUID().toString();
        while (true){
            String result = bot(randomName);
            if (result.equals("Игра прервана. Увидимся вновь!") || result.equals("До свидания!")) {
                break;
            }
        }
        return "Успешно.";
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
    @Scheduled(fixedRate = 1000)
    public void playGame() {
        if (count < 10) {
            System.out.println("Запустился.");
            botRandomName();
            count++;
        } else {
            System.out.println("Играю.");
            int randomNumber = ThreadLocalRandom.current().nextInt(0, 9);
            List<Account> accounts = new ArrayList<>(allPlayers.values());
            String login = accounts.get(randomNumber).getLoginName();
            while (true){
                System.out.println("В цикле.");
                String result = bot(login);
                System.out.println(result);
                if (result.equals("Игра прервана. Увидимся вновь!") || result.equals("До свидания!")) {
                    break;
                }
            }
        }
    }
}