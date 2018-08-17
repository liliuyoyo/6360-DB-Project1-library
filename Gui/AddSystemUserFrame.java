package Gui;

import Dao.UserDao;
import Model.User;
import Util.Database_util;
import Util.StringCheck;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;

public class AddSystemUserFrame extends JFrame{
    private JTextField usernameF,passwordF;
    private Database_util dbUtil = new Database_util();
    private UserDao userDao = new UserDao();

    public AddSystemUserFrame () {
        this.setTitle("Create New System User");
        this.setSize(600, 400);
        this.setLayout(null);

        JLabel usernameL = new JLabel("* Username:");
        usernameL.setFont(new Font("text", Font.BOLD, 18));
        usernameL.setBounds(100, 70, 200, 50);
        usernameF = new JTextField();
        usernameF.setBounds(270, 70, 200, 40);

        JLabel passwordL = new JLabel("* Password:");
        passwordL.setFont(new Font("text", Font.BOLD, 18));
        passwordL.setBounds(100, 170, 200, 50);
        passwordF = new JTextField();
        passwordF.setBounds(270, 170, 200, 40);

        JButton cancle = new JButton("CANCLE");
        cancle.setBounds(120, 270, 120, 40);
        cancle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetValue();
                dispose();
            }
        });

        JButton submit = new JButton("SUBMIT");
        submit.setBounds(320, 270, 120, 40);
        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createBorrwer(e);
                resetValue();
            }
        });

        this.add(usernameL);
        this.add(usernameF);
        this.add(passwordL);
        this.add(passwordF);
        this.add(cancle);
        this.add(submit);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void createBorrwer(ActionEvent evt) {

        String username = this.usernameF.getText();
        String password = this.passwordF.getText();

        if(StringCheck.isEmpty(username)||StringCheck.isEmpty(password)){
            JOptionPane.showMessageDialog(null,"Username and Password can not be NULL!");
            return;
        }

        User user = new User(username,password);

        Connection con = null;
        try{
            con = dbUtil.getCon();
            int addNum = UserDao.addUser(con,user);
            if(addNum == 1){
                JOptionPane.showMessageDialog(null,"A new user is SUCCESSFULLY created!");
                resetValue();
            }else {
                JOptionPane.showMessageDialog(null,"FAILED to create a new user!");
            }
        }catch (Exception e){
            if (e instanceof com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException) {
                JOptionPane.showMessageDialog(null,"FAILED to create a new user!\n USERNAME has already been used.");
            }
        }finally {
            try{
                dbUtil.closeCon(con);
            }catch (Exception e){
            }
        }
    }

    private void resetValue() {
        this.usernameF.setText("");
        this.passwordF.setText("");
    }
}
