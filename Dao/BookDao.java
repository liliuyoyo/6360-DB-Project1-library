package Dao;

import Model.BookSearch;
import Util.StringCheck;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BookDao {

    // search book.
    public ResultSet list(Connection con, BookSearch bookSearch)throws Exception{
        String text = bookSearch.getFieldTxt();
        StringBuffer sb = new StringBuffer("SELECT B.ISBN,TITLE,PUBLISHER,PAGES,GROUP_CONCAT(NAME) AS NAME " +
                "FROM BOOK AS B LEFT JOIN " +
                "(SELECT BA.ISBN, A.NAME FROM BOOK_AUTHORS AS BA LEFT JOIN AUTHORS AS A ON BA.AUTHOR_ID = A.AUTHOR_ID) AS C " +
                "ON B.ISBN=C.ISBN");

        if(StringCheck.isNotEmpty(bookSearch.getFieldTxt())){
            sb.append(" WHERE B.Isbn LIKE '%"+text+"%' OR TITLE LIKE '%"+text+"%' "
                    + "OR NAME LIKE '%"+text+"%'");
        }
        sb.append(" GROUP BY B.ISBN");
        PreparedStatement p =con.prepareStatement(sb.toString());
        return p.executeQuery();
    }

    // check whether the book is checked out.
    public ResultSet checkStatus(Connection con, String isbn) throws SQLException {
        String sql = new String("SELECT * FROM BOOK_LOANS WHERE LOAN_ISBN=? AND DATE_IN IS NULL");
        PreparedStatement p =con.prepareStatement(sql);
        p.setString(1,isbn);
        return p.executeQuery();
    }
}
