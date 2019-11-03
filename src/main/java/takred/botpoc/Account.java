package takred.botpoc;

import takred.botpoc.client.RegisterResponseGuess;

import java.util.UUID;

public class Account {
    private final String loginName;
    private final UUID loginSessionId;
    private final UUID gameSessionId;
    private Integer countTry;

    public Account(String loginName, UUID loginSessionId, UUID gameSessionId, Integer countTry) {
        this.loginName = loginName;
        this.loginSessionId = loginSessionId;
        this.gameSessionId = gameSessionId;
        this.countTry = countTry;
    }

    public String getLoginName() {
        return loginName;
    }

    public Account withLoginName(String loginName) {
        return new Account(loginName, loginSessionId, gameSessionId, countTry);
    }

    public UUID getLoginSessionId() {
        return loginSessionId;
    }

    public Account withLoginSessionId(UUID loginSessionId) {
        return new Account(loginName, loginSessionId, gameSessionId, countTry);
    }

    public UUID getGameSessionId() {
        return gameSessionId;
    }

    public Account withGameSessionId(UUID gameSessionId) {
        return new Account(loginName, loginSessionId, gameSessionId, countTry);
    }

    public Integer getCountTry() {
        return countTry;
    }
    public Account withCountTry(Integer countTry) {
        return new Account(loginName, loginSessionId, gameSessionId, countTry);
    }
}
