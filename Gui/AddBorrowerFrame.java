package Gui;

import Dao.BorrowerDao;
import Model.Borrower;
import Util.Database_util;
import Util.StringCheck;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;


public class AddBorrowerFrame extends JFrame{

    private JTextField ssnF,bnameF,phoneF;
    private JTextArea addrF;
    private JLabel cardIdL = new JLabel(" Card NO.   ");
    private JTextField cardIdF = new JTextField();
    private Database_util dbUtil = new Database_util();
    private BorrowerDao borrowerDao = new BorrowerDao();

    public AddBorrowerFrame () {
        this.setTitle("Create New Borrower");
        this.setSize(600, 700);
        this.setLayout(null);

        cardIdL.setFont(new Font("text", Font.BOLD, 18));
        cardIdL.setBounds(100, 50, 400, 50);
        cardIdF.setBounds(200, 55, 200, 40);
        cardIdF.setEditable(false);
        cardIdF.setBackground(Color.LIGHT_GRAY );

        JLabel ssnL = new JLabel("* Ssn:");
        ssnL.setFont(new Font("text", Font.BOLD, 18));
        ssnL.setBounds(100, 130, 100, 50);
        ssnF = new JTextField();
        ssnF.setBounds(200, 140, 270, 40);
        ssnF.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char keyCh = e.getKeyChar();
                if ((keyCh < '0') || (keyCh > '9')) {
                    if (keyCh != '\r')
                        e.setKeyChar('\0');
                }
            }
        });

        JLabel bnameL = new JLabel("* Name:");
        bnameL.setFont(new Font("text", Font.BOLD, 18));
        bnameL.setBounds(100, 210, 100, 50);
        bnameF = new JTextField();
        bnameF.setBounds(200, 220, 270, 40);

        JLabel phoneL = new JLabel("Phone:");
        phoneL.setFont(new Font("text", Font.BOLD, 18));
        phoneL.setBounds(100, 290, 100, 50);
        phoneF = new JTextField();
        phoneF.setBounds(200, 295, 270, 40);
        phoneF.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char keyCh = e.getKeyChar();
                if ((keyCh < '0') || (keyCh > '9')) {
                    if (keyCh != '\r')
                        e.setKeyChar('\0');
                }
            }
        });

        JLabel addrL = new JLabel("* Address:");
        addrL.setFont(new Font("text", Font.BOLD, 18));
        addrL.setBounds(100, 370, 100, 50);
        addrF = new JTextArea();
        addrF.setBounds(200, 380, 270, 100);
        addrF.setBorder(new LineBorder(Color.LIGHT_GRAY));

        JButton cancle = new JButton("CANCLE");
        cancle.setBounds(120, 555, 120, 40);
        cancle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetValue();
                dispose();
            }
        });

        JButton submit = new JButton("SUBMIT");
        submit.setBounds(320, 555, 120, 40);
        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createBorrwer(e);
            }
        });

        this.add(cardIdL);
        this.add(cardIdF);
        this.add(ssnL);
        this.add(ssnF);
        this.add(bnameL);
        this.add(bnameF);
        this.add(phoneL);
        this.add(phoneF);
        this.add(addrL);
        this.add(addrF);
        this.add(cancle);
        this.add(submit);

        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void createBorrwer(ActionEvent evt) {
        int len= ssnF.getText().length();
        if(len != 9 ){
            if(len == 0){
                JOptionPane.showMessageDialog(null,"Ssn, Name and Address can not be NULL!");
                return;
            }
            JOptionPane.showMessageDialog(null,"Invalid SSN!");
            this.ssnF.setText("");
            return;
        }
        int len1= phoneF.getText().length();
        if(len1 != 10 ){
            JOptionPane.showMessageDialog(null,"Invalid Phone Number!");
            this.phoneF.setText("");
            return;
        }

        String card_id = this.cardIdF.getText();
        String ssn = this.ssnF.getText();
        String bname = this.bnameF.getText();
        String address = this.addrF.getText();
        String phone = this.phoneF.getText();

        if(StringCheck.isEmpty(ssn)||StringCheck.isEmpty(bname)||StringCheck.isEmpty(address)){
            JOptionPane.showMessageDialog(null,"Ssn, Name and Address can not be NULL!");
            return;
        }

        Borrower newBorrower = new Borrower(card_id,ssn,bname,address,phone);

        Connection con = null;
        try{
            con = dbUtil.getCon();
            int addNum = borrowerDao.add(con,newBorrower);
            if(addNum == 1){
                JOptionPane.showMessageDialog(null,"A new borrower is SUCCESSFULLY created!");
                cardIdF.setText(Integer.toString(Integer.parseInt(cardIdF.getText())+1));
                resetValue();
            }else {
                JOptionPane.showMessageDialog(null,"FAILED to create a new borrower!");
            }
        }catch (Exception e){
            if (e instanceof com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException) {
                JOptionPane.showMessageDialog(null,"FAILED to create a new borrower!\nSSN is already used.");
                ssnF.setText("");
            }
        }finally {
            try{
                dbUtil.closeCon(con);
            }catch (Exception e){
            }
        }
    }

    private void resetValue() {
        this.ssnF.setText("");
        this.bnameF.setText("");
        this.phoneF.setText("");
        this.addrF.setText("");
    }

    public void showCardId(String cardID){
        int x = Integer.parseInt(cardID)+1;
        this.cardIdF.setText(Integer.toString(x));
    }
}
