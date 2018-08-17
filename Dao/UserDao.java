package Dao;

import Model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDao {

    public User login(Connection con, User user) throws Exception{
        User resultUser = null;
        String sql="SELECT * FROM USER WHERE user_name=? AND password=?";
        PreparedStatement p = con.prepareStatement(sql);
        p.setString(1,user.getUserName());
        p.setString(2,user.getPassword());
        ResultSet result = p.executeQuery();
        if(result.next()){
            resultUser = new User();
            resultUser.setId(result.getInt("id"));
            resultUser.setUserName(result.getString("user_name"));
            resultUser.setPassword(result.getString("password"));
        }
        return resultUser;
    }

    public static int addUser(Connection con,User user) throws Exception{
        String sql = "INSERT INTO USER VALUES (null,?,?)";
        PreparedStatement p = con.prepareStatement(sql);
        p.setString(1,user.getUserName());
        p.setString(2,user.getPassword());
        return p.executeUpdate();
    }
}
