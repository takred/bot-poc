package takred.botpoc.client;

public class logoutResponse {
    private boolean logoutStatus;
    private String message;

    public logoutResponse(boolean logoutStatus, String message) {
        this.logoutStatus = logoutStatus;
        this.message = message;
    }

    public boolean getLogoutStatus() {
        return logoutStatus;
    }

    public logoutResponse withLogoutStatus(boolean logoutStatus) {
        return new logoutResponse(logoutStatus, message);
    }

    public String getMessage() {
        return message;
    }

    public logoutResponse withMessage(String message) {
        return new logoutResponse(logoutStatus, message);
    }
}
