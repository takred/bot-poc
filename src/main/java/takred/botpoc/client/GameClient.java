package takred.botpoc.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@FeignClient(name = "GameClient", url = "localhost:8080")
public interface GameClient {
    @RequestMapping(value = "/register/{loginName}")
    public RegisterResponse register(@PathVariable("loginName") String loginName);

    @RequestMapping(value = "/guess/{gameSessionId}/{number}")
    public RegisterResponseGuess guess(@PathVariable("gameSessionId") UUID gameSessionId, @PathVariable("number") Integer number);

    @RequestMapping(value = "/logout/{loginName}")
    public logoutResponse logout(@PathVariable("loginName") String loginName);

    @RequestMapping(value = "/login/{loginName}")
    public RegisterResponse login(@PathVariable("loginName") String loginName);

    @RequestMapping(value = "/start/{loginSessionId}")
    public RegisterResponse start(@PathVariable("loginSessionId") UUID loginSessionId);

}
