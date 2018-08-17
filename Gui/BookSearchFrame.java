/*
package Gui;

import Dao.BookDao;
import Dao.BorrowerDao;
import Model.BookSearch;
import Util.Database_util;
import Util.StringCheck;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.NumberFormat;
import java.util.Vector;

public class BookSearchFrame extends JFrame {

    private JPanel searchPanel = new JPanel();
    private JTextField searchField = new JTextField();
    private JButton search = new JButton("Search");

    private JScrollPane display;
    private JTable table;

    private JPanel infoPanel = new JPanel();
    private JLabel isbnL = new JLabel("ISBN:");
    private JLabel titleL = new JLabel("TITLE:");
    private JLabel authorL = new JLabel("AUTHOR:");
    private JLabel publisherL = new JLabel("PUBLISHER:");
    private JLabel pagesL = new JLabel("PAGES:");
    private JLabel statusL = new JLabel("STATUS:");
    private JTextField isbnF = new JTextField();
    private JTextField titleF = new JTextField();
    private JTextField authorF = new JTextField();
    private JTextField publisherF = new JTextField();
    private JTextField pagesF = new JTextField();
    private JRadioButton inStock = new JRadioButton("In Stock");
    private JRadioButton checkedOut = new JRadioButton("Checked Out");
    private ButtonGroup group = new ButtonGroup();
    private JButton borrowB = new JButton("BORROW");

    JFrame cardIdFrame = new JFrame("Checking out...");
    JPanel cardIdPanel = new JPanel();
    JTextField cardIdField = new JTextField();

    private Database_util dbUtil = new Database_util();
    private BookDao bookDao = new BookDao();
    private BorrowerDao borrowerDao = new BorrowerDao();

    public BookSearchFrame(){
        this.setTitle("Book Searching...");
        this.setSize(1000,800);
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
                new String[] {"ISBN","TITLE","AUTHOR","PUBLISHER","PAGES"}) {
            boolean[] columnEditables = new boolean[] {false, false, false, false, false};
            public boolean isCellEditable(int row, int column) {
                return columnEditables[column];
            }
        });
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(200);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getClickCount() == 2){
                    showBookInfo(e);
                }
            }
        });

        display = new JScrollPane(table);
        display.setBounds(10,140,980,250);

        //set up information panel
        infoPanel.setBounds(10,400,980,370);
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

        statusL.setBounds(80,290,150,40);
        statusL.setFont(new Font("title",Font.BOLD,18));
        inStock.setBounds(200,290,150,40);
        inStock.setFont(new Font("title",Font.ITALIC,18));
        inStock.setSelected(true);
        inStock.setEnabled(false);
        checkedOut.setBounds(330,290,200,40);
        checkedOut.setFont(new Font("title",Font.ITALIC,18));
        checkedOut.setEnabled(false);
        group.add(inStock);
        group.add(checkedOut);


        borrowB.setBounds(600,290,220,40);
        borrowB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(isbnF.getText().trim().equals("")){
                    JOptionPane.showMessageDialog(null,"Please select a book.");
                }else if(checkedOut.isSelected()){
                    JOptionPane.showMessageDialog(null,"FAILED to borrow the book.\nThe book is already CHECKED OUT.");
                }else{
                    getCardId(e);
                }
            }
        });

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
        infoPanel.add(statusL);
        infoPanel.add(inStock);
        infoPanel.add(checkedOut);
        infoPanel.add(borrowB);


        this.add(searchPanel);
        this.add(display);
        this.add(infoPanel);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.fillTable(new BookSearch());
    }

    private void getCardId(ActionEvent evt){
        cardIdFrame.setLocationRelativeTo(null);
        cardIdFrame.setSize(400,225);
        cardIdFrame.setResizable(false);
        cardIdFrame.setLayout(null);

        cardIdPanel.setBounds(0,0,400,200);
        cardIdPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK,2),
                "  Enter the Card ID ", TitledBorder.CENTER,TitledBorder.TOP,new java.awt.Font("title",Font.BOLD,20)));
        cardIdPanel.setLayout(null);

        cardIdField.setBounds(50,50,300,40);
        cardIdField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char keyCh = e.getKeyChar();
                if ((keyCh < '0') || (keyCh > '9')) {
                    if (keyCh != '\r')
                        e.setKeyChar('\0');
                }
            }
        });

        JButton cancel = new JButton("CANCEL");
        cancel.setBounds(50,120,120,40);
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardIdFrame.dispose();
            }
        });
        JButton submit = new JButton("SUBMIT");
        submit.setBounds(230,120,120,40);
        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                borrowerSearch(e);
            }
        });

        cardIdPanel.add(cardIdField);
        cardIdPanel.add(cancel);
        cardIdPanel.add(submit);
        cardIdFrame.add(cardIdPanel);
        cardIdFrame.setVisible(true);
    }

    private void showBookInfo(MouseEvent evt) {
        Connection con = null;
        try{
            con = dbUtil.getCon();
            ResultSet rs = bookDao.checkStatus(con,isbnF.getText());
            if(rs.next()){
                checkedOut.setSelected(true);
            }else{
                inStock.setSelected(true);
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
        int row = this.table.getSelectedRow();
        this.isbnF.setText(table.getValueAt(row,0).toString());
        this.titleF.setText(table.getValueAt(row,1).toString());
        this.authorF.setText(table.getValueAt(row,2).toString());
        this.publisherF.setText(table.getValueAt(row,3).toString());
        this.pagesF.setText(table.getValueAt(row,4).toString());
    }

    private void bookSearch(ActionEvent evt) {
        String text = this.searchField.getText();
        BookSearch bookSearch = new BookSearch(text);
        this.fillTable(bookSearch);
    }

    private void fillTable(BookSearch bookSearch){
        DefaultTableModel dtm = (DefaultTableModel)table.getModel();
        dtm.setRowCount(0);
        Connection con = null;
        try{
            con = dbUtil.getCon();
            ResultSet rs = bookDao.list(con,bookSearch);
            while(rs.next()){
                Vector v = new Vector();
                v.add(rs.getString("ISBN"));
                v.add(rs.getString("TITLE"));
                v.add(rs.getString("NAME"));
                v.add(rs.getString("PUBLISHER"));
                v.add(rs.getInt("PAGES"));
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

    private void borrowerSearch(ActionEvent evt) {
        String cardId = this.cardIdField.getText();

        if(StringCheck.isEmpty(cardId)){
            JOptionPane.showMessageDialog(null,"Card ID can not be NULL!");
            return;
        }

        Connection con = null;
        try{
            con = dbUtil.getCon();
            ResultSet result = borrowerDao.list(con,cardId);
            if(result.next()){
                this.cardIdField.setText("");
                cardIdFrame.dispose();
                BookBorrowFrame bf = new BookBorrowFrame(cardId);
                bf.showBookInfo(this.table);
                bf.showName(result.getString("Bname"));
            }
            else{
                JOptionPane.showMessageDialog(null,"Card ID is INVALID.");
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
*/

package Gui;

import Dao.BookDao;
import Dao.BorrowerDao;
import Model.BookSearch;
import Util.Database_util;
import Util.StringCheck;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.NumberFormat;
import java.util.Vector;

public class BookSearchFrame extends JFrame {

    private JPanel searchPanel = new JPanel();
    private JTextField searchField = new JTextField();
    private JButton search = new JButton("Search");

    private JLabel note = new JLabel("*** CLICK the row to get the book detailed infromation ***");
    private JScrollPane display;
    private JTable table;

    private JPanel infoPanel = new JPanel();
    private JLabel isbnL = new JLabel("ISBN:");
    private JLabel titleL = new JLabel("TITLE:");
    private JLabel authorL = new JLabel("AUTHOR:");
    private JLabel publisherL = new JLabel("PUBLISHER:");
    private JLabel pagesL = new JLabel("PAGES:");
    private JLabel statusL = new JLabel("STATUS:");
    private JTextField isbnF = new JTextField();
    private JTextField titleF = new JTextField();
    private JTextField authorF = new JTextField();
    private JTextField publisherF = new JTextField();
    private JTextField pagesF = new JTextField();
    private JRadioButton inStock = new JRadioButton("In Stock");
    private JRadioButton checkedOut = new JRadioButton("Checked Out");
    private ButtonGroup group = new ButtonGroup();
    private JButton borrowB = new JButton("BORROW");

    JFrame cardIdFrame = new JFrame("Checking out...");
    JPanel cardIdPanel = new JPanel();
    JTextField cardIdField = new JTextField();

    private Database_util dbUtil = new Database_util();
    private BookDao bookDao = new BookDao();
    private BorrowerDao borrowerDao = new BorrowerDao();

    public BookSearchFrame(){
        this.setTitle("Book Searching...");
        this.setSize(1000,800);
        this.setLayout(null);

        searchPanel.setBounds(10,0,980,115);
        searchPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK,2),
                "  Searching  ", TitledBorder.LEFT,TitledBorder.TOP,new java.awt.Font("title",Font.BOLD,20)));
        searchPanel.setLayout(null);
        searchField.setBounds(80,40,700,40);
        search.setBounds(830,40,100,40);
        search.setFont(new Font("title",Font.BOLD,18));
        search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bookSearch(e);
            }
        });

        searchPanel.add(searchField);
        searchPanel.add(search);

        note.setBounds(265,113,450,40);
        note.setForeground(Color.BLUE);

        //set up Jtable
        table = new JTable();
        table.setModel((TableModel) new DefaultTableModel(new Object[][] {},
                new String[] {"ISBN","TITLE","AUTHOR","PUBLISHER","PAGES"}) {
            boolean[] columnEditables = new boolean[] {false, false, false, false, false};
            public boolean isCellEditable(int row, int column) {
                return columnEditables[column];
            }
        });
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(200);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int row = table.getSelectedRow();
                String loan_isbn = table.getValueAt(row,0).toString();
                showBookInfo(loan_isbn);
            }
        });

        display = new JScrollPane(table);
        display.setBounds(10,150,980,250);

        //set up information panel
        infoPanel.setBounds(10,410,980,360);
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

        statusL.setBounds(80,290,150,40);
        statusL.setFont(new Font("title",Font.BOLD,18));
        inStock.setBounds(200,290,150,40);
        inStock.setFont(new Font("title",Font.ITALIC,18));
        inStock.setSelected(true);
        inStock.setEnabled(false);
        checkedOut.setBounds(330,290,200,40);
        checkedOut.setFont(new Font("title",Font.ITALIC,18));
        checkedOut.setEnabled(false);
        group.add(inStock);
        group.add(checkedOut);


        borrowB.setBounds(600,290,220,40);
        borrowB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(isbnF.getText().trim().equals("")){
                    JOptionPane.showMessageDialog(null,"Please select a book.");
                }else if(checkedOut.isSelected()){
                    JOptionPane.showMessageDialog(null,"FAILED to borrow the book.\nThe book is already CHECKED OUT.");
                }else{
                    getCardId(e);
                }
            }
        });

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
        infoPanel.add(statusL);
        infoPanel.add(inStock);
        infoPanel.add(checkedOut);
        infoPanel.add(borrowB);


        this.add(searchPanel);
        this.add(note);
        this.add(display);
        this.add(infoPanel);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.fillTable(new BookSearch());
    }

    private void getCardId(ActionEvent evt){
        cardIdFrame.setLocationRelativeTo(null);
        cardIdFrame.setSize(400,225);
        cardIdFrame.setResizable(false);
        cardIdFrame.setLayout(null);

        cardIdPanel.setBounds(0,0,400,200);
        cardIdPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK,2),
                "  Enter the Card ID ", TitledBorder.CENTER,TitledBorder.TOP,new java.awt.Font("title",Font.BOLD,20)));
        cardIdPanel.setLayout(null);

        cardIdField.setBounds(50,50,300,40);
        cardIdField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char keyCh = e.getKeyChar();
                if ((keyCh < '0') || (keyCh > '9')) {
                    if (keyCh != '\r')
                        e.setKeyChar('\0');
                }
            }
        });

        JButton cancel = new JButton("CANCEL");
        cancel.setBounds(50,120,120,40);
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardIdFrame.dispose();
            }
        });
        JButton submit = new JButton("SUBMIT");
        submit.setBounds(230,120,120,40);
        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                borrowerSearch(e);
            }
        });

        cardIdPanel.add(cardIdField);
        cardIdPanel.add(cancel);
        cardIdPanel.add(submit);
        cardIdFrame.add(cardIdPanel);
        cardIdFrame.setVisible(true);
    }

    private void showBookInfo(String loan_isbn) {

        Connection con = null;
        try{
            con = dbUtil.getCon();
            ResultSet rs = bookDao.checkStatus(con,loan_isbn);
            if(rs.next()){
                checkedOut.setSelected(true);
            }else{
                inStock.setSelected(true);
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
        int row = this.table.getSelectedRow();
        this.isbnF.setText(table.getValueAt(row,0).toString());
        this.titleF.setText(table.getValueAt(row,1).toString());
        this.authorF.setText(table.getValueAt(row,2).toString());
        this.publisherF.setText(table.getValueAt(row,3).toString());
        this.pagesF.setText(table.getValueAt(row,4).toString());
    }

    private void bookSearch(ActionEvent evt) {
        String text = this.searchField.getText().trim();
        BookSearch bookSearch = new BookSearch(text);
        this.fillTable(bookSearch);
    }

    private void fillTable(BookSearch bookSearch){
        DefaultTableModel dtm = (DefaultTableModel)table.getModel();
        dtm.setRowCount(0);
        Connection con = null;
        try{
            con = dbUtil.getCon();
            ResultSet rs = bookDao.list(con,bookSearch);
            while(rs.next()){
                Vector v = new Vector();
                v.add(rs.getString("ISBN"));
                v.add(rs.getString("TITLE"));
                v.add(rs.getString("NAME"));
                v.add(rs.getString("PUBLISHER"));
                v.add(rs.getInt("PAGES"));
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

    private void borrowerSearch(ActionEvent evt) {
        String cardId = this.cardIdField.getText();

        if(StringCheck.isEmpty(cardId)){
            JOptionPane.showMessageDialog(null,"Card ID can not be NULL!");
            return;
        }

        Connection con = null;
        try{
            con = dbUtil.getCon();
            ResultSet result = borrowerDao.list(con,cardId);
            if(result.next()){
                this.cardIdField.setText("");
                cardIdFrame.dispose();
                BookBorrowFrame bf = new BookBorrowFrame(cardId);
                bf.showBookInfo(this.table);
                bf.showName(result.getString("Bname"));
            }
            else{
                JOptionPane.showMessageDialog(null,"Card ID is INVALID.");
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
