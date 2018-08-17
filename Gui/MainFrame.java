package Gui;

import Dao.BorrowerDao;
import Util.Database_util;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;

public class MainFrame extends JFrame {

    Container ct;
    BackgroundPanel bgp;

    JFrame cardIdFrame = new JFrame("Checking out...");
    JPanel cardIdPanel = new JPanel();
    JTextField cardIdField = new JTextField();
    private JLabel welcome = new JLabel("=== WELCOME ===");

    private Database_util dbUtil = new Database_util();
    private BorrowerDao borrowerDao = new BorrowerDao();

    public MainFrame() {

        this.setTitle("Library Management System");
        this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        this.setLocation(0,0);
        this.setLayout(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenuBar mainMenu = new JMenuBar();

        JMenu bookManage = new JMenu("Book Management");

        JMenuItem searchBook = new JMenuItem("Search & Borrow Book");
        JMenuItem returnBook = new JMenuItem("Search & Return Book");

        searchBook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new BookSearchFrame();
            }
        });
        returnBook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ReturnBookFrame();
            }
        });

        bookManage.add(searchBook);
        bookManage.add(returnBook);


        JMenu borrowerManage = new JMenu("User Management");

        JMenuItem addBorrower = new JMenuItem("Add Borrower");
        JMenuItem createUser = new JMenuItem("Add System User");
        addBorrower.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getMaxCardId(e);
            }
        });
        createUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNewUser(e);
            }
        });

        borrowerManage.add(addBorrower);
        borrowerManage.add(createUser);


        JMenu account = new JMenu("Fine Management");

        JMenuItem fine = new JMenuItem("Fines");
        fine.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new FinesFrame();
            }
        });

        account.add(fine);

        JMenu systemManage = new JMenu("System");
        JMenuItem logout = new JMenuItem("Logout");
        logout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(null, "Are you sure to logout?");
                if (result == 0) {
                    System.exit(0);
                }
            }
        });

        systemManage.add(logout);

        mainMenu.add(bookManage);
        mainMenu.add(borrowerManage);
        mainMenu.add(account);
        mainMenu.add(systemManage);

        ct=this.getContentPane();
        ct.setLayout(null);
        bgp=new BackgroundPanel((new ImageIcon("/Users/liyooo/Desktop/2018_Summer/6360-Database_Design/Project_1/images/1.jpg")).getImage());
        bgp.setBounds(0,0,this.getWidth(),this.getHeight());

        welcome.setBounds((this.getWidth()-this.getWidth()/3)/2,150,this.getWidth()/3,200);
        welcome.setFont(new Font("welcome",Font.BOLD,(int)(welcome.getWidth()*0.09)));
        ct.add(welcome);
        ct.add(bgp);

        this.setJMenuBar(mainMenu);
    }

    private void getMaxCardId(ActionEvent evt) {
        Connection con = null;
        try {
            con = dbUtil.getCon();
            ResultSet result = borrowerDao.getMaxID(con);
            if (result.next()) {
                AddBorrowerFrame abf = new AddBorrowerFrame();
                abf.showCardId(result.getString("MAX(card_id)"));
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

    private void addNewUser(ActionEvent evt) {
        new AddSystemUserFrame();
    }

    class BackgroundPanel extends JPanel
    {
        Image im;
        public BackgroundPanel(Image im)
        {
            this.im=im;
            this.setOpaque(true);
        }
        public void paintComponent(Graphics g)
        {
            super.paintComponents(g);
            g.drawImage(im,0,0,this.getWidth(),this.getHeight(),this);
        }
    }
}
