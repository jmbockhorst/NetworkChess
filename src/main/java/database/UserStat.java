package database;

public class UserStat {
    private String userId;
    private int wins;
    private int losses;

    public UserStat(String userId, int wins, int losses) {
        this.userId = userId;
        this.wins = wins;
        this.losses = losses;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

}