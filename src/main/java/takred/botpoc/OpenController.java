package takred.botpoc;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import takred.botpoc.client.GameClient;
import takred.botpoc.client.RegisterResponse;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@RestController
public class OpenController {
    private final GameClient gameClient;
    private RegisterResponse registerResponse = null;
    private RegisterResponse startResponse = null;
    UUID idGame = null;

    public OpenController(GameClient gameClient) {
        this.gameClient = gameClient;
    }

    @RequestMapping("/open")
    public String open() {
        if(registerResponse == null){
            registerResponse =  gameClient.register("Kostya");
            return registerResponse.getMessage();
        }
        return gameClient.register("Kostya").getMessage();
    }

    @RequestMapping(value = "/guess")
    public String guess() {
        return gameClient.guess(idGame, ThreadLocalRandom.current().nextInt(1, 10000));
    }

    @RequestMapping("/exit")
    public String exit(){
        return gameClient.logout("Kostya");
    }

    @RequestMapping(value = "/login")
    public String login(){
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
    @RequestMapping(value = "/botstart")
    public String startAll(){
        open();
        start();
        guess();
        exit();
        return "Всё успешно.";
    }
}