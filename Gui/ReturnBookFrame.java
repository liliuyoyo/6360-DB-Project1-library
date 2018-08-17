package Gui;

import Dao.LoanDao;
import Model.BookSearch;
import Model.DateDiff;
import Util.Database_util;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

public class ReturnBookFrame extends JFrame{
    private JPanel searchPanel = new JPanel();
    private JTextField searchField = new JTextField();
    private JButton search = new JButton("Search");
    private JScrollPane display;
    private JTable table;
    private JButton cancelB = new JButton("CANCEL");
    private JButton returnB = new JButton("RETURN");

    private Database_util dbUtil = new Database_util();
    private String loanid;
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    Calendar cal = Calendar.getInstance();

    public ReturnBookFrame(){
        this.setTitle("Book Searching...");
        this.setSize(1000,500);
        this.setLayout(null);

        searchPanel.setBounds(10,0,980,135);
        searchPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK,2),
                "  Searching  ", TitledBorder.LEFT,TitledBorder.TOP,new java.awt.Font("title",Font.BOLD,20)));
        searchPanel.setLayout(null);
        searchField.setBounds(80,50,700,40);
        search.setBounds(830,50,100,40);
        search.setFont(new Font("title",Font.BOLD,18));
        search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                bookSearch(e);
            }
        });

        searchPanel.add(searchField);
        searchPanel.add(search);

        //set up Jtable
        table = new JTable();
        table.setModel((TableModel) new DefaultTableModel(new Object[][] {},
                new String[] {"CARD_ID","LOAN_ID","ISBN","TITLE","DATE OUT","DUE DATE","DATE IN"}) {
            boolean[] columnEditables = new boolean[] {false, false, false, false, false,false,false};
            public boolean isCellEditable(int row, int column) {
                return columnEditables[column];
            }
        });
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(40);
        table.getColumnModel().getColumn(3).setPreferredWidth(270);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                loanid = getLoanid();
                }
            });

        display = new JScrollPane(table);
        display.setBounds(10,150,980,250);

        cancelB.setBounds(250,430,150,40);
        cancelB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        returnB.setBounds(600,430,150,40);
        returnB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(loanid==null){
                    JOptionPane.showMessageDialog(null,"Please select the book you want to return.");
                }else{
                    returnBook(e,loanid);
                }
            }
        });

        this.add(searchPanel);
        this.add(display);
        this.add(cancelB);
        this.add(returnB);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void returnBook(ActionEvent evt,String loanid) {
        Connection con = null;
        int diffDays = 0;
        int row = table.getSelectedRow();
        if(row == -1){
            JOptionPane.showMessageDialog(null,"Please select the book you want to return.");
            return;
        }
        String dueDate = table.getValueAt(row,5).toString();
        DateDiff dateDiff = new DateDiff();
        Date dayIn = cal.getTime();
        String dayin = df.format(dayIn);
        dateDiff.setCalValue1(dayin);
        dateDiff.setCalValue2(dueDate);

        if(loanid!=null && table.getValueAt(row,6).equals("CHECKED OUT")){
            try{
                con = dbUtil.getCon();
                ResultSet days = LoanDao.dayDiff(con,dateDiff);
                if(days.next()){
                    diffDays= days.getInt("DIFFDAYS");
                }
                int updateLoanNum = LoanDao.updateLoan(con,loanid,dayin);
                if(updateLoanNum == 1){
                    JOptionPane.showMessageDialog(null,"Returned Successfully!");
                }else{
                    JOptionPane.showMessageDialog(null,"FAILED to return the book.");
                }
                int updateFineNum = LoanDao.updateLoanFine(con,loanid,diffDays);
                if(updateFineNum == 1){
                    bookSearch(evt);
                }

            }catch (Exception e){
                e.printStackTrace();
            }finally {
                try {
                    dbUtil.closeCon(con);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else{
            JOptionPane.showMessageDialog(null,"The book is already returned.");
        }
    }

    private String getLoanid() {
        String result="";
        int row = this.table.getSelectedRow();
        String date = table.getValueAt(row,6).toString();
        if(!date.equals("CHECKED OUT")){
            return result;
        }
        result = table.getValueAt(row,1).toString();
        return result;
    }

    private void bookSearch(ActionEvent evt) {
        String text = this.searchField.getText();
        BookSearch bookSearch = new BookSearch(text);
        this.fillBorTable(bookSearch);
    }

    private void fillBorTable(BookSearch bookSearch){
        DefaultTableModel dtm = (DefaultTableModel)table.getModel();
        dtm.setRowCount(0);
        Connection con = null;
        try{
            con = dbUtil.getCon();
            ResultSet rs = LoanDao.loanlist1(con,bookSearch);
            while(rs.next()){
                Vector v = new Vector();
                v.add(rs.getString("CARD_ID"));
                v.add(rs.getString("LOAN_ID"));
                v.add(rs.getString("ISBN"));
                v.add(rs.getString("TITLE"));
                v.add(rs.getString("DATE_OUT"));
                v.add(rs.getString("DUE_DATE"));
                try{
                    String tmp = rs.getString("DATE_IN");
                    if(tmp==null){
                        tmp="CHECKED OUT";
                    }
                    v.add(tmp);
                }catch (Exception e){
                    e.printStackTrace();
                }
                dtm.addRow(v);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                dbUtil.closeCon(con);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
