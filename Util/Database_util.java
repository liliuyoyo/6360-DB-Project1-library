package Util;

import java.sql.Connection;
import java.sql.DriverManager;

public class Database_util {

    private String dbUrl = "jdbc:mysql://localhost:3306/library?useSSL=false";
    private String dbUserName = "root";
    private String dbPassword = "A19880929s!";
    private String jdbcName = "com.mysql.jdbc.Driver";

    //get database connection
    public Connection getCon() throws Exception{
        Class.forName(jdbcName);
        Connection con = DriverManager.getConnection(dbUrl,dbUserName,dbPassword);
        return con;
    }

    public void closeCon(Connection con) throws Exception{
        if(con != null){
            con.close();
        }
    }

    public static void main(String[] args){
        Database_util db = new Database_util();
        try {
            db.getCon();
            System.out.println("Database connected!");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Fail database connection!");
        }
    }
}
