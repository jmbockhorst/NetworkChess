package database;

public class UserStatController {
    UserStatDAO userStatDAO;

    public UserStatController() {
        userStatDAO = new UserStatDAO(DatabaseConnection.getConnection());
    }

    public UserStat get(String userId) {
        return userStatDAO.list().stream().filter(stat -> stat.getUserId().equals(userId)).findFirst().orElse(null);
    }

    public void addWin(String userId) {
        UserStat userStat = get(userId);

        if (userStat == null) {
            userStatDAO.insert(new UserStat(userId, 1, 0));
        } else {
            userStat.setWins(userStat.getWins() + 1);
            userStatDAO.update(userStat);
        }
    }

    public void addLoss(String userId) {
        UserStat userStat = get(userId);

        if (userStat == null) {
            userStatDAO.insert(new UserStat(userId, 0, 1));
        } else {
            userStat.setLosses(userStat.getLosses() + 1);
            userStatDAO.update(userStat);
        }
    }
}