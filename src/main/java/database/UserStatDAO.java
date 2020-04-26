package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserStatDAO implements IDAO<UserStat> {

    private Connection connection;

    public UserStatDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<UserStat> list() {
        List<UserStat> list = new ArrayList<>();

        try {
            ResultSet rs = connection.prepareStatement("SELECT * FROM user_stat").executeQuery();
            while (rs.next()) {
                String user_id = rs.getString("user_id");
                int wins = rs.getInt("wins");
                int losses = rs.getInt("losses");

                list.add(new UserStat(user_id, wins, losses));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public void insert(UserStat item) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO user_stat VALUES (?, ?, ?)");
            statement.setString(1, item.getUserId());
            statement.setInt(2, item.getWins());
            statement.setInt(3, item.getLosses());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(UserStat item) {
        try {
            PreparedStatement statement = connection
                    .prepareStatement("UPDATE user_stat SET wins = ? AND losses = ? WHERE user_id = ?");
            statement.setInt(1, item.getWins());
            statement.setInt(2, item.getLosses());
            statement.setString(3, item.getUserId());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}