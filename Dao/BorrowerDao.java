package Dao;



import Model.Borrower;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class BorrowerDao {

    //create a new borrower record
    public int add(Connection con, Borrower borrower) throws Exception{
        String sql = "INSERT INTO BORROWER VALUES (?,?,?,?,?)";
        PreparedStatement p = con.prepareStatement(sql);
        p.setString(1,borrower.getCard_id());
        p.setString(2,borrower.getSsn());
        p.setString(3,borrower.getBname());
        p.setString(4,borrower.getAddress());
        p.setString(5,borrower.getPhone());
        return p.executeUpdate();
    }

    // search for a borrower by card_id.
    public static ResultSet list(Connection con, String card_id)throws Exception{
        String sql= "SELECT * FROM BORROWER WHERE CARD_ID =?";
        PreparedStatement p =con.prepareStatement(sql);
        p.setString(1,card_id);
        return p.executeQuery();
    }

    // get the max card_id in borrower table
    public ResultSet getMaxID(Connection con) throws SQLException {
        String sql= "SELECT MAX(CARD_ID) FROM BORROWER";
        PreparedStatement p =con.prepareStatement(sql);
        return p.executeQuery();
    }
}
