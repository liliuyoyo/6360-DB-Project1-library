package Gui;

import Dao.BookDao;
import Dao.LoanDao;
import Model.Loan;
import Util.Database_util;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

public class BookBorrowFrame extends JFrame {

    private JPanel infoPanel = new JPanel();
    private JLabel isbnL = new JLabel("ISBN:");
    private JLabel titleL = new JLabel("TITLE:");
    private JLabel authorL = new JLabel("AUTHOR:");
    private JLabel publisherL = new JLabel("PUBLISHER:");
    private JLabel pagesL = new JLabel("PAGES:");
    private JLabel cardId = new JLabel("CARD NO. ");
    private JLabel name = new JLabel("NAME:");
    private JTextField isbnF = new JTextField();
    private JTextField titleF = new JTextField();
    private JTextField authorF = new JTextField();
    private JTextField publisherF = new JTextField();
    private JTextField pagesF = new JTextField();
    private JTextField cardIdF = new JTextField();
    private JTextField nameF = new JTextField();
    private JButton cancel = new JButton("CANCEL");
    private JButton returnBu = new JButton("RETURN");
    private JButton borrow = new JButton("BORROW");

    private JScrollPane borrowPane;
    private JTable borrowTable;

    private Database_util dbUtil = new Database_util();
    private BookDao bookDao = new BookDao();
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    Calendar cal = Calendar.getInstance();


    public BookBorrowFrame(String id){
        this.setTitle("Book Check Out");
        this.setSize(1000,680);
        this.setLayout(null);
        this.cardIdF.setText(id);

        //set up information panel
        infoPanel.setBounds(0,0,1000,300);
        infoPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK,2),
                "  Book Information  ", TitledBorder.LEFT,TitledBorder.TOP,new java.awt.Font("title",Font.BOLD,20)));
        infoPanel.setLayout(null);

        titleL.setBounds(80,50,150,40);
        titleL.setFont(new Font("title",Font.BOLD,18));
        titleF.setBounds(150,50,740,40);
        titleF.setEditable(false);
        titleF.setBackground(Color.LIGHT_GRAY );

        isbnL.setBounds(80,130,150,40);
        isbnL.setFont(new Font("title",Font.BOLD,18));
        isbnF.setBounds(150,130,200,40);
        isbnF.setEditable(false);
        isbnF.setBackground(Color.LIGHT_GRAY );

        authorL.setBounds(440,130,150,40);
        authorL.setFont(new Font("title",Font.BOLD,18));
        authorF.setBounds(550,130,340,40);
        authorF.setEditable(false);
        authorF.setBackground(Color.LIGHT_GRAY );

        pagesL.setBounds(80,210,150,40);
        pagesL.setFont(new Font("title",Font.BOLD,18));
        pagesF.setBounds(150,210,200,40);
        pagesF.setEditable(false);
        pagesF.setBackground(Color.LIGHT_GRAY );

        publisherL.setBounds(440,210,150,40);
        publisherL.setFont(new Font("title",Font.BOLD,18));
        publisherF.setBounds(550,210,340,40);
        publisherF.setEditable(false);
        publisherF.setBackground(Color.LIGHT_GRAY );

        infoPanel.add(titleL);
        infoPanel.add(titleF);
        infoPanel.add(isbnL);
        infoPanel.add(isbnF);
        infoPanel.add(authorL);
        infoPanel.add(authorF);
        infoPanel.add(publisherL);
        infoPanel.add(publisherF);
        infoPanel.add(pagesL);
        infoPanel.add(pagesF);

        cardId.setBounds(80,330,150,40);
        cardId.setFont(new Font("title",Font.BOLD,18));
        cardIdF.setBounds(200,330,200,40);
        cardIdF.setEditable(false);
        cardIdF.setBackground(Color.LIGHT_GRAY );
        name.setBounds(550,330,150,40);
        name.setFont(new Font("title",Font.BOLD,18));
        nameF.setBounds(650,330,200,40);
        nameF.setEditable(false);
        nameF.setBackground(Color.LIGHT_GRAY );

        //set up Jtable
        borrowTable = new JTable();
        borrowTable.setModel((TableModel) new DefaultTableModel(new Object[][] {},
                new String[] {"LOAN_ID","ISBN","TITLE","CHECK OUT DATE","DUE DATE","CHECK IN DATE"}) {
            boolean[] columnEditables = new boolean[] {false, false, false, false, false,false,false};
            public boolean isCellEditable(int row, int column) {
                return columnEditables[column];
            }
        });
        borrowTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        borrowTable.getColumnModel().getColumn(1).setPreferredWidth(50);
        borrowTable.getColumnModel().getColumn(2).setPreferredWidth(250);

        fillBorTable();

        borrowPane = new JScrollPane(borrowTable);
        borrowPane.setBounds(10,400,980,150);

        cancel.setBounds(230,580,150,40);
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        borrow.setBounds(640,580,150,40);
        borrow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addLoanRecord();
            }
        });

        this.add(infoPanel);
        this.add(cardId);
        this.add(cardIdF);
        this.add(name);
        this.add(nameF);
        this.add(borrowPane);
        this.add(cancel);
        this.add(borrow);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public void showBookInfo(JTable table) {
        int row = table.getSelectedRow();
        this.isbnF.setText(table.getValueAt(row,0).toString());
        this.titleF.setText(table.getValueAt(row,1).toString());
        this.authorF.setText(table.getValueAt(row,2).toString());
        this.publisherF.setText(table.getValueAt(row,3).toString());
        this.pagesF.setText(table.getValueAt(row,4).toString());
    }

    public void showName(String name){
        this.nameF.setText(name);
    }

    private void fillBorTable(){
        DefaultTableModel dtm = (DefaultTableModel)borrowTable.getModel();
        dtm.setRowCount(0);
        Connection con = null;
        try{
            con = dbUtil.getCon();
            ResultSet rs = LoanDao.loanlist(con,cardIdF.getText());
            while(rs.next()){
                Vector v = new Vector();
                v.add(rs.getString("LOAN_ID"));
                v.add(rs.getString("LOAN_ISBN"));
                v.add(rs.getString("TITLE"));
                v.add(rs.getString("DATE_OUT"));
                v.add(rs.getString("DUE_DATE"));
                if(rs.getString("DATE_IN")==null){
                    v.add("CHECKED OUT");
                }
                else{
                    v.add(rs.getString("DATE_IN"));
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

    private void addLoanRecord() {
        int loanid = 0;
        String loan_isbn = isbnF.getText();
        Connection con = null;
        try{
            con = dbUtil.getCon();
            ResultSet bookStatus = bookDao.checkStatus(con,loan_isbn);
            if(bookStatus.next()){
                JOptionPane.showMessageDialog(null,"The book has already been checked out.");
                return;
            }else{
                ResultSet num = LoanDao.getBorrowedNum(con,cardIdF.getText());
                if(num.next()){
                    if(num.getInt("COUNT(*)")>=3){
                        JOptionPane.showMessageDialog(null,"Can not borrow more than 3 books.");
                        return;
                    }
                }
                ResultSet rs = LoanDao.getMaxLoanID(con);
                if(rs.next()){
                    String tmp = rs.getString("MAX(LOAN_ID)");
                    if(tmp != null) {
                        loanid = Integer.parseInt(tmp);
                    }
                }
                String loan_id = Integer.toString(loanid+1);
                String card_id = cardIdF.getText();
                Date dayOut = cal.getTime();
                String date_out = df.format(dayOut);
                cal.add(Calendar.DATE, 14);
                Date dueDay = cal.getTime();
                String due_date =df.format(dueDay);
                Loan loanRecord = new Loan(loan_id,loan_isbn,card_id,date_out,due_date);

                int addNum = LoanDao.add(con,loanRecord);
                if(addNum == 1){
                    fillBorTable();
                    JOptionPane.showMessageDialog(null,"SUCCESSFULLY borrowed the book!");
                }else {
                    JOptionPane.showMessageDialog(null,"Sorry!FAILED to borrow");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                dbUtil.closeCon(con);
            }catch (Exception e){
            }
        }
    }
}
