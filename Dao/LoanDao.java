package Dao;

import Model.BookSearch;
import Model.DateDiff;
import Model.Loan;
import Util.StringCheck;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoanDao {

    //insert a new loan record
    public static int add(Connection con, Loan loan) throws Exception{
        String sql = "INSERT INTO BOOK_LOANS VALUES (?,?,?,?,?,null,null,null)";
        PreparedStatement p = con.prepareStatement(sql);
        p.setString(1,loan.getLoan_id());
        p.setString(2,loan.getLoan_isbn());
        p.setString(3,loan.getCard_id());
        p.setString(4,loan.getDate_out());
        p.setString(5,loan.getDue_date());
        return p.executeUpdate();
    }

    // get all loan records plus correspond book title by card_id
    public static ResultSet loanlist(Connection con,String cardID)throws Exception{
        String sql= "SELECT * FROM BOOK_LOANS JOIN BOOK ON LOAN_ISBN= ISBN WHERE CARD_ID=?";
        PreparedStatement p =con.prepareStatement(sql);
        p.setString(1,cardID);
        return p.executeQuery();
    }

    // search for loan records
    public static ResultSet loanlist1(Connection con, BookSearch bookSearch)throws Exception{
        String text = bookSearch.getFieldTxt();
        StringBuffer sb = new StringBuffer("SELECT B.CARD_ID,LOAN_ID,ISBN,TITLE,DATE_OUT,DUE_DATE,DATE_IN,BNAME" +
                " FROM BORROWER AS B JOIN (SELECT LOAN_ID,CARD_ID,DATE_OUT,DUE_DATE,DATE_IN,ISBN,TITLE FROM BOOK_LOANS AS BL JOIN BOOK ON LOAN_ISBN=ISBN)" +
                " AS X ON B.CARD_ID=X.CARD_ID");
        /*if(StringCheck.isNotEmpty(bookSearch.getFieldTxt())){
            sb.append(" WHERE B.CARD_ID LIKE '%"+text+"%' OR ISBN LIKE '%"+text+"%' OR BNAME LIKE '%"+text+"%'");
        }*/
        if(StringCheck.isNotEmpty(bookSearch.getFieldTxt())){
            sb.append(" WHERE B.CARD_ID="+text+" OR ISBN="+text+" OR BNAME LIKE '%"+text+"%'");
        }
        PreparedStatement p =con.prepareStatement(sb.toString());
        return p.executeQuery();
    }

    //get max loan_id in book_loans table
    public static ResultSet getMaxLoanID(Connection con) throws SQLException {
        String sql= "SELECT MAX(LOAN_ID) FROM BOOK_LOANS";
        PreparedStatement p =con.prepareStatement(sql);
        return p.executeQuery();
    }

    //get the number of unreturned books of user
    public static ResultSet getBorrowedNum(Connection con,String cardID) throws SQLException{
        String sql= "SELECT COUNT(*) FROM BOOK_LOANS WHERE CARD_ID=? AND DATE_IN IS NULL";
        PreparedStatement p =con.prepareStatement(sql);
        p.setString(1,cardID);
        return p.executeQuery();
    }

    //check in the book with current date
    public static int updateLoan(Connection con,String loanid,String dayin)throws Exception{
        String sql= "UPDATE BOOK_LOANS SET DATE_IN=? WHERE LOAN_ID=?";
        PreparedStatement p =con.prepareStatement(sql);
        p.setString(1,dayin);
        p.setString(2,loanid);
        return p.executeUpdate();
    }

    //update the fine information and paid status for current returning book.
    public static int updateLoanFine(Connection con,String loanid,int diffDays)throws Exception{
        Double fineamt = 0.0;
        int paid = 1;
        String sql= "UPDATE BOOK_LOANS SET FINE_AMT=?,PAID=? WHERE LOAN_ID=?";
        PreparedStatement p =con.prepareStatement(sql);
        if(diffDays > 0 ){
            fineamt = diffDays*0.25;
            paid = 0;
        }
        p.setDouble(1,fineamt);
        p.setInt(2,paid);
        p.setString(3,loanid);
        return p.executeUpdate();
    }

    //update the fine information for each book
    public static int updateLoanFine1(Connection con,String loanid,int diffDays)throws Exception{
        Double fineamt = 0.0;
        int paid = 1;
        String sql= "UPDATE BOOK_LOANS SET FINE_AMT=? WHERE LOAN_ID=?";
        PreparedStatement p =con.prepareStatement(sql);
        if(diffDays > 0 ){
            fineamt = diffDays*0.25;
        }
        p.setDouble(1,fineamt);
        p.setString(2,loanid);
        return p.executeUpdate();
    }

    //get the diffdays between two dates.
    public static ResultSet dayDiff(Connection con, DateDiff diff)throws Exception{
        String sql = "select DATEDIFF(?,?) AS DIFFDAYS";
        PreparedStatement p = con.prepareStatement(sql);
        p.setString(1, diff.getCalValue1());
        p.setString(2, diff.getCalValue2());
        return p.executeQuery();
    }

    //get the balance due for each user.
    public static ResultSet totalFines(Connection con)throws Exception{
        String sql = "SELECT CARD_ID,SUM(FINE_AMT) AS TOTAL_FINE FROM BOOK_LOANS WHERE PAID = 0 OR PAID IS NULL GROUP BY CARD_ID";
        PreparedStatement p = con.prepareStatement(sql);
        return p.executeQuery();
    }

    //get all loan records for selected user.
    public static ResultSet detailFines(Connection con,String cardid)throws Exception{
        String sql = "SELECT * FROM BOOK_LOANS WHERE CARD_ID=?";
        PreparedStatement p = con.prepareStatement(sql);
        p.setString(1,cardid);
        return p.executeQuery();
    }

    //get unpaid loan records for selected user.
    public static ResultSet unpaidFines(Connection con,String cardid)throws Exception{
        String sql = "SELECT * FROM BOOK_LOANS WHERE CARD_ID=? AND (PAID=0 OR PAID IS NULL)";
        PreparedStatement p = con.prepareStatement(sql);
        p.setString(1,cardid);
        return p.executeQuery();
    }

    //update pay status.
    public static int payFine(Connection con,String loanid)throws Exception{
        String sql = "UPDATE BOOK_LOANS SET PAID=1 WHERE LOAN_ID=?";
        PreparedStatement p = con.prepareStatement(sql);
        p.setString(1,loanid);
        return p.executeUpdate();
    }

    //get all records of book_loans table.
    public static ResultSet allLoans(Connection con)throws Exception{
        String sql = "SELECT * FROM BOOK_LOANS WHERE DATE_IN IS NULL OR (DATE_IN > DUE_DATE AND (PAID=0 OR PAID IS NULL))";
        PreparedStatement p = con.prepareStatement(sql);
        return p.executeQuery();
    }

   /* // check whether the book is checked out.
    public ResultSet checkStatus(Connection con, String isbn) throws SQLException {
        String sql = new String("SELECT * FROM BOOK_LOANS WHERE LOAN_ISBN=? AND DATE_IN IS NULL");
        PreparedStatement p =con.prepareStatement(sql);
        p.setString(1,isbn);
        return p.executeQuery();
    }*/
}
