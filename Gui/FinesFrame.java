package Gui;

import Dao.LoanDao;
import Model.DateDiff;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

public class FinesFrame extends JFrame {

    private JPanel topPanel = new JPanel();
    private JScrollPane totalTablePanel;
    private JTable totalTable;
    private JButton refresh = new JButton("REFERSH");
    private JButton searchCard = new JButton("SEARCH");

    private JPanel bottomPanel = new JPanel();
    private JLabel cardID = new JLabel("CARD NO. ");
    private JTextField cardIDF = new JTextField();
    private JButton payfine = new JButton("PAY FINE");
    private JButton showUnpaid = new JButton("SHOW UNPAID");
    private String loanid;
    private JScrollPane detailTablePanel;
    private JTable detailTable;

    JFrame cardIdFrame = new JFrame("Searching borrower...");
    JPanel cardIdPanel = new JPanel();
    //JFormattedTextField cardIdField = new JFormattedTextField(NumberFormat.getIntegerInstance());
    JTextField cardIdField = new JTextField();

    private Database_util dbUtil = new Database_util();
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    Calendar cal = Calendar.getInstance();

    public FinesFrame() {
        this.setTitle("Fine Management");
        this.setSize(1000, 795);
        this.setLayout(null);

        //set up top panel
        topPanel.setBounds(10, 0, 980, 340);
        topPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 2),
                "  Fines:  ", TitledBorder.LEFT, TitledBorder.TOP, new java.awt.Font("title", Font.BOLD, 20)));
        topPanel.setLayout(null);

        totalTable = new JTable();
        totalTable.setModel(new DefaultTableModel(new Object[][]{},
                new String[]{"CARD_ID", "TOTAL BALANCE DUE"}) {
            boolean[] columnEditables = new boolean[]{false, false};

            public boolean isCellEditable(int row, int column) {
                return columnEditables[column];
            }
        });
        totalTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int row = totalTable.getSelectedRow();
                String id= totalTable.getValueAt(row, 0).toString();
                showDetail(id);
            }
        });

        totalTablePanel = new JScrollPane(totalTable);
        totalTablePanel.setBounds(60, 45, 600, 250);

        refresh.setBounds(750, 100, 120, 40);
        refresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshFines();
            }
        });

        searchCard.setBounds(750, 200, 120, 40);
        searchCard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
                        if (StringCheck.isEmpty(cardIdField.getText())){
                            JOptionPane.showMessageDialog(null, "Card ID can not be NULL!");
                            return;
                        }

                        if(cardIDSearch()){
                            showDetail(cardIdField.getText());
                            cardIdField.setText("");
                            cardIdFrame.dispose();
                        }
                        else{
                            JOptionPane.showMessageDialog(null, "No records found.");
                        }
                    }
                });

                cardIdPanel.add(cardIdField);
                cardIdPanel.add(cancel);
                cardIdPanel.add(submit);
                cardIdFrame.add(cardIdPanel);
                cardIdFrame.setVisible(true);
            }
        });
        topPanel.add(refresh);
        topPanel.add(searchCard);
        topPanel.add(totalTablePanel);
        this.fillTotalTable();

        //set up bottom panel
        bottomPanel.setBounds(10, 350, 980, 400);
        bottomPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 2),
                "  Account Info:  ", TitledBorder.LEFT, TitledBorder.TOP, new java.awt.Font("title", Font.BOLD, 20)));
        bottomPanel.setLayout(null);

        cardID.setBounds(70, 45, 150, 40);
        cardID.setFont(new Font("title", Font.BOLD, 18));
        cardIDF.setBounds(200, 45, 300, 40);
        cardIDF.setEditable(false);
        cardIDF.setBackground(Color.LIGHT_GRAY);
        showUnpaid.setBounds(550, 45, 150, 40);
        showUnpaid.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showUnpaidRecord();
            }
        });
        payfine.setBounds(750, 45, 150, 40);
        payfine.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                payFine();
            }
        });

        detailTable = new JTable();
        detailTable.setModel(new DefaultTableModel(new Object[][]{},
                new String[]{"LOAN_ID", "ISBN", "DATE_OUT", "DUE_DATE", "DATE_IN", "AMOUNT", "PAID"}) {
            boolean[] columnEditables = new boolean[]{false, false, false, false, false, false, false};

            public boolean isCellEditable(int row, int column) {
                return columnEditables[column];
            }
        });

        TableColumn tc = detailTable.getColumnModel().getColumn(6);
        tc.setCellEditor(detailTable.getDefaultEditor(Boolean.class));
        tc.setCellRenderer(detailTable.getDefaultRenderer(Boolean.class));

        detailTablePanel = new JScrollPane(detailTable);
        detailTablePanel.setBounds(60, 104, 850, 250);

        bottomPanel.add(cardID);
        bottomPanel.add(cardIDF);
        bottomPanel.add(showUnpaid);
        bottomPanel.add(payfine);
        bottomPanel.add(detailTablePanel);

        refreshFines();
        this.add(topPanel);
        this.add(bottomPanel);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }


    private void fillTotalTable() {
        DefaultTableModel dtm = (DefaultTableModel) totalTable.getModel();
        dtm.setRowCount(0);
        Connection con = null;
        try {
            con = dbUtil.getCon();
            ResultSet rs = LoanDao.totalFines(con);
            while (rs.next()) {
                Vector v = new Vector();
                v.add(rs.getString("CARD_ID"));
                v.add(rs.getString("TOTAL_FINE"));
                dtm.addRow(v);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                dbUtil.closeCon(con);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showDetail(String id) {
        cardIDF.setText(id);
        DefaultTableModel dtm = (DefaultTableModel) detailTable.getModel();
        dtm.setRowCount(0);
        Connection con = null;
        try {
            con = dbUtil.getCon();
            ResultSet rs = LoanDao.detailFines(con, cardIDF.getText());
            while (rs.next()) {
                Vector v = new Vector();
                v.add(rs.getString("LOAN_ID"));
                v.add(rs.getString("LOAN_ISBN"));
                v.add(rs.getString("DATE_OUT"));
                v.add(rs.getString("DUE_DATE"));
                v.add(rs.getString("DATE_IN"));
                v.add(rs.getString("FINE_AMT"));
                v.add(rs.getBoolean("PAID"));
                dtm.addRow(v);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                dbUtil.closeCon(con);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void payFine() {
        int row = detailTable.getSelectedRow();
        if(row == -1){
            int totalRow = totalTable.getSelectedRow();
            if(totalRow == -1){
                JOptionPane.showMessageDialog(null, "Please select one user to get the loan records!");
                return;
            }else {
                JOptionPane.showMessageDialog(null, "Please select one row to pay the fine!");
                return;
            }
        }

        loanid = detailTable.getValueAt(row, 0).toString();


        Connection con = null;
        if(detailTable.getValueAt(row,6).toString().equals("true")){
            JOptionPane.showMessageDialog(null, "Fine has already been paid!");
            return;
        }
        if(detailTable.getValueAt(row,4) == null){
            JOptionPane.showMessageDialog(null, "Please return the book first!");
            return;
        }
        try {
            con = dbUtil.getCon();
            int payNum = LoanDao.payFine(con, loanid);
            if (payNum == 1) {
                JOptionPane.showMessageDialog(null, "Payment received!");
                showDetail(cardIDF.getText());
            } else {
                JOptionPane.showMessageDialog(null, "FAILED to pay!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                dbUtil.closeCon(con);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        fillTotalTable();
    }

    /**
     * refresh each fine records
     */
    private void refreshFines() {
        Connection con = null;
        Date dayIn = cal.getTime();
        String dayin = df.format(dayIn);
        DateDiff dateDiff = new DateDiff();
        dateDiff.setCalValue1(dayin);
        try{
            con = dbUtil.getCon();
            ResultSet rs = LoanDao.allLoans(con);
            while (rs.next()){
                dateDiff.setCalValue2(rs.getString("DUE_DATE"));
                ResultSet day = LoanDao.dayDiff(con,dateDiff);
                if(day.next()){
                    LoanDao.updateLoanFine1(con,rs.getString("LOAN_ID"),day.getInt("DIFFDAYS"));
                }
            }
            JOptionPane.showMessageDialog(null, "Fine amounts UPDATED!");
            fillTotalTable();
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

    private void showUnpaidRecord() {
        DefaultTableModel dtm = (DefaultTableModel) detailTable.getModel();
        dtm.setRowCount(0);
        Connection con = null;
        try {
            con = dbUtil.getCon();
            ResultSet rs = LoanDao.unpaidFines(con, cardIDF.getText());
            while (rs.next()) {
                Vector v = new Vector();
                v.add(rs.getString("LOAN_ID"));
                v.add(rs.getString("LOAN_ISBN"));
                v.add(rs.getString("DATE_OUT"));
                v.add(rs.getString("DUE_DATE"));
                v.add(rs.getString("DATE_IN"));
                v.add(rs.getString("FINE_AMT"));
                v.add(rs.getBoolean("PAID"));
                dtm.addRow(v);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                dbUtil.closeCon(con);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean cardIDSearch() {
        Connection con = null;
        try {
            con = dbUtil.getCon();
            ResultSet result = LoanDao.detailFines(con, cardIdField.getText());
            if (result.next()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                dbUtil.closeCon(con);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}
