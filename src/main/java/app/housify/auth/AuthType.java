package app.housify.auth;

public enum AuthType {
    NONE(0),
    CLIENT(1),
    AGENT(2),
    MANAGER(3);

    public int level;

    AuthType(int level) {
        this.level = level;
    }

}
