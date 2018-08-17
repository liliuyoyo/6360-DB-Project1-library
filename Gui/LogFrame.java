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

public class LogFrame extends JFrame {

    private JTextField unField;
    private JPasswordField pwField;

    private Database_util dbUtil = new Database_util();
    private UserDao userDao = new UserDao();

    public LogFrame(){
        this.setSize(400,300);
        this.setTitle("Log On");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel title = new JLabel("Library Manage System");
        title.setBounds(50,20,300,50);
        title.setFont(new Font("title",Font.BOLD,24));

        JLabel un = new JLabel("Username:");
        un.setBounds(60,90,150,50);
        un.setFont(new Font("title",Font.ITALIC,18));

        JLabel pw = new JLabel("Password:");
        pw.setBounds(60,140,150,50);
        pw.setFont(new Font("title",Font.ITALIC,18));

        unField = new JTextField();
        unField.setBounds(160,100,170,30);

        pwField = new JPasswordField();
        pwField.setBounds(160,150,170,30);

        JButton reset = new JButton("RESET");
        reset.setBounds(50,210,120,32);
        reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetValue(e);
            }
        });

        JButton login = new JButton("LOGIN");
        login.setBounds(220,210,120,32);
        login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login(e);
            }
        });

        this.add(title);
        this.add(un);
        this.add(pw);
        this.add(unField);
        this.add(pwField);
        this.add(reset);
        this.add(login);

        this.setLayout(null);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void login(ActionEvent evt) {
        String username = this.unField.getText();
        String password = new String(this.pwField.getPassword());
        if(StringCheck.isEmpty(username)){
            JOptionPane.showMessageDialog(null,"Username can not be NULL!");
            return;
        }
        if(StringCheck.isEmpty(password)){
            JOptionPane.showMessageDialog(null,"Password can not be NULL!");
            return;
        }
        User user = new User(username,password);
        Connection con = null;
        try {
            con = dbUtil.getCon();
            User curUser = userDao.login(con,user);
            if(curUser != null){
                dispose();
                new MainFrame().setVisible(true);
            }else{
                JOptionPane.showMessageDialog(null,"Invalid Username or Password!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resetValue(ActionEvent evt) {
        this.unField.setText("");
        this.pwField.setText("");
    }

    public static void main(String[] args) {
        new LogFrame();
    }
}
