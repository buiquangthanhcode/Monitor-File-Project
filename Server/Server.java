package Server;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.*;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

class UIDetail extends Server {
    private JFrame userClient;
    private JLabel statusUSer;
    private JLabel id_User;
    private ArrayList<String> ts;
    private JPanel controlPanels;
    private String id;
    private String[] folder = { "folder1", "folder2", "folder3", "folder4" };
    private JComboBox<String> comboBox;
    private Socket socketUI;
    private JLabel statusThread;

    public UIDetail(Socket s) {
        ts = new ArrayList<>();
        ts.add("Online");
        socketUI = s;
        System.out.println("Current:" + s.getPort());
        statusThread=new JLabel("No Active",JLabel.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        if (cmd.equals("active")) {

            String selected_text = comboBox.getItemAt(comboBox.getSelectedIndex());
            statusThread.setText("Running at path: " + selected_text);
            try {
                DataOutputStream dout = new DataOutputStream(
                        socketUI.getOutputStream());

                dout.writeUTF("Path:" + selected_text);
                dout.flush();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                
            }
        } else if (cmd.equals("terminate")) {
            statusThread.setText("Stopped");

            try {
                DataOutputStream dout = new DataOutputStream(
                        socketUI.getOutputStream());

                dout.writeUTF("Terminate");
                dout.flush();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
             

            }
        } else if (cmd.equals("activelibarry")) {

            new Thread() {
                public void run() {
                    String selected_text = comboBox.getItemAt(comboBox.getSelectedIndex());
                    statusThread.setText("Running at path: " + selected_text);

                    try {
                        DataOutputStream dout = new DataOutputStream(
                                socketUI.getOutputStream());

                        dout.writeUTF("monitorlibrary:" + selected_text);
                        dout.flush();
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                 
                    }
                };

            }.start();

        } else if (cmd.equals("terminatel")) {
            statusThread.setText("Stopped");

            try {
                DataOutputStream dout = new DataOutputStream(
                        socketUI.getOutputStream());

                dout.writeUTF("TerminateLibary");
                dout.flush();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
            

            }
        }

    }

    public void prepareUI_USER(String ID, String[] allPath) {

        userClient = new JFrame("Client - " + ID);
        id = ID;
        folder = allPath;
        userClient.add(mainControl());
        userClient.setSize(1200, 400);
        userClient.setVisible(true);
        userClient.setDefaultCloseOperation(userClient.DISPOSE_ON_CLOSE);
    }

    public JPanel mainControl() {
        controlPanels = new JPanel();
     
        statusUSer = new JLabel("Online at: " +  LocalDateTime.now().getHour() +":"+LocalDateTime.now().getMinute());
        id_User = new JLabel("ID", JLabel.CENTER);
        controlPanels.setLayout(new BoxLayout(controlPanels, BoxLayout.PAGE_AXIS));
        id_User.setText("ID -- " + id);
        id_User.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        JPanel wrapperFolder = new JPanel(new FlowLayout());
        JLabel labelOfFolder = new JLabel("Folder of client");
        wrapperFolder.add(labelOfFolder);

        comboBox = new JComboBox<>(folder); // push data here
        comboBox.addActionListener(this);
        wrapperFolder.add(comboBox);
        wrapperFolder.setMaximumSize(new Dimension(Integer.MAX_VALUE, wrapperFolder.getMinimumSize().height));
        JButton monitorBtn = new JButton("Monitor");
        monitorBtn.setActionCommand("active");
        monitorBtn.addActionListener(this);
        wrapperFolder.add(monitorBtn);
        JButton monitorBtnLibary = new JButton("Monitor by library");
        monitorBtnLibary.setActionCommand("activelibarry");
        monitorBtnLibary.addActionListener(this);
        wrapperFolder.add(monitorBtnLibary);
        JButton terminateBtn = new JButton("Terminate");
        terminateBtn.setActionCommand("terminate");
        terminateBtn.addActionListener(this);
        wrapperFolder.add(terminateBtn);
        JButton terminateLibaryBtn = new JButton("Terminate Libary");
        terminateLibaryBtn.setActionCommand("terminatel");
        terminateLibaryBtn.addActionListener(this);
        wrapperFolder.add(terminateLibaryBtn);

        JPanel wrappercontent= new JPanel();
        wrappercontent.setLayout(new BorderLayout());
       
        statusThread.setFont(new Font("Serif", Font.PLAIN, 22));
        wrappercontent.add(statusThread,BorderLayout.NORTH);

        statusUSer.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        controlPanels.add(id_User);
        controlPanels.add(statusUSer);
        controlPanels.add(wrapperFolder);
        controlPanels.add(wrappercontent);
        return controlPanels;
    }

    public String toString() {
        return Arrays.toString(ts.toArray());
    }

    public void setSocket(Socket socket) {
        socketUI = socket;
    }
}

public class Server extends Thread implements ActionListener {
    private final String pathfileImage = System.getProperty("user.dir") + "/Server/background.png";
    protected HashMap<String, ArrayList<String>> contentUser;
    protected HashMap<String, UIDetail> wraperUser;
    protected Set<String> user;
    protected JFrame jframe;
    protected JFrame detailUI;
    protected JLabel statusClient;
    protected JPanel controlDetail;
    protected JPanel control;
    protected JPanel controlPanel;
    protected JLabel messagLabel;
    protected JList receveMessaeJList;
    protected ArrayList<String> activity;
    protected JList realTimeActivity;
    protected ArrayList<String> receiveList;
    protected JButton exitBtn;
    protected ServerSocket s1;
    protected Socket ss;
    protected final String[] columnNames = { "ID", "Status", "Last access" };
    protected String[][] data;
    protected String ID_user = "";
    protected ArrayList<String> valueMap = new ArrayList<>();
    protected HashMap<Socket, String> listSocket = new HashMap<Socket, String>();
    private JTextField IPTextField;
    private JTextField PortTextField;

    public Server() {
        jframe = new JFrame();
        controlPanel = new JPanel();
        receiveList = new ArrayList<>();
        exitBtn = new JButton("Exit");
        statusClient = new JLabel("");
        controlDetail = new JPanel();
        messagLabel = new JLabel("Nothing");
        contentUser = new HashMap<String, ArrayList<String>>();
        user = new HashSet<String>();
        wraperUser = new HashMap<String, UIDetail>();

    }

    public JPanel detailClient() {
        controlDetail.setLayout(new BoxLayout(controlDetail, BoxLayout.PAGE_AXIS));
        statusClient.setFont(new Font("Serif", Font.PLAIN, 20));
        statusClient.setAlignmentX(0);
        controlDetail.add(statusClient);
        controlDetail.add(messagLabel);

        return controlDetail;
    }

    public JPanel interfaceStart() {
        JPanel introduction_gui;
        JLabel picLabel;
        introduction_gui = new JPanel();
        introduction_gui.setBackground(Color.white);
        introduction_gui.setLayout(new BoxLayout(introduction_gui, BoxLayout.Y_AXIS));
        BufferedImage myPicture;
        try {
            String path = pathfileImage;
            myPicture = ImageIO.read(new File(path));
            picLabel = new JLabel(new ImageIcon(myPicture));
            introduction_gui.add(picLabel);
        } catch (IOException e) {
            
        }
        JPanel wrapper_btn_start;
        wrapper_btn_start = new JPanel();
        wrapper_btn_start.setBackground(Color.WHITE);
        wrapper_btn_start.setLayout(new FlowLayout(FlowLayout.CENTER));
        JButton startbtn;
        JPanel wrapperSetup=new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel helper = new JLabel("Set up Server -> ");
        JLabel iPJLabel=new JLabel("IP : ");
        IPTextField=new JTextField(15);
        JLabel portLabel = new JLabel("Port : ");
        IPTextField.setText("127.0.0.1");
        PortTextField = new JTextField(5);
        PortTextField.setText("109");

        wrapperSetup.add(helper);
        wrapperSetup.add(iPJLabel);
        wrapperSetup.add(IPTextField);
        wrapperSetup.add(portLabel);
        wrapperSetup.add(PortTextField);
        wrapperSetup.setBackground(Color.WHITE);

     
        startbtn = new JButton("Start Server");
        startbtn.setActionCommand("start");
        startbtn.addActionListener(this);
        wrapper_btn_start.add(wrapperSetup);
        wrapper_btn_start.add(startbtn);
        wrapper_btn_start.setBorder(BorderFactory.createLineBorder(Color.gray));
        introduction_gui.add(wrapper_btn_start);

        return introduction_gui;
    }

    public JPanel mainPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JLabel header = new JLabel("Welcome to Server monitor file");
        header.setHorizontalAlignment(0);
        header.setFont(new Font("Serif", Font.PLAIN, 20));
        mainPanel.add(header, BorderLayout.NORTH);
        receveMessaeJList = new JList<>();
        receveMessaeJList.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {

                if (!e.getValueIsAdjusting()) {

                    Object selection = receveMessaeJList.getSelectedValue();

                    if (selection != null) {
                        String[] values = ((String) selection).split(" ");
                        receveMessaeJList.clearSelection();

                        if (values.length > 0 || values[2].equals("online") || values[2].equals("offline")) {
                            if (values[2].equals("online")) {

                                SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {

                                        String[] folderClient = { "" };
                                        String foldertoString = "";
                                        for (String es : contentUser.get(values[1])) {
                                            if (es.contains("[")) {
                                                String str = "";
                                                int index = es.indexOf("[");
                                                foldertoString = (es.substring(index)).replace("[", " ").replace("]",
                                                        " ");

                                            }
                                        }
                                        folderClient = foldertoString.split(",");
                                        wraperUser.get(values[1]).prepareUI_USER(values[1], folderClient);

                                    }
                                });

                            } else {

                            }

                        }

                    }
                }
            }
        });
        exitBtn.setActionCommand("exit");
        exitBtn.setForeground(Color.red);
        exitBtn.addActionListener(this);

        mainPanel.add(exitBtn, BorderLayout.EAST);
        JPanel wrapperCenter = new JPanel();
        wrapperCenter.setLayout(new GridLayout(2, 1, 5, 5));
        JScrollPane scrollPaneReceive = new JScrollPane();
        scrollPaneReceive.setViewportView(receveMessaeJList);
        JPanel wrappeReal = new JPanel();
        wrapperCenter.add(scrollPaneReceive);

        wrappeReal.setLayout(new BoxLayout(wrappeReal, BoxLayout.PAGE_AXIS));

        JLabel labelrealtime = new JLabel("Activity",JLabel.LEFT);

        String[] exampeStrings = { "" };
        realTimeActivity = new JList<>(exampeStrings);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(realTimeActivity);
        wrappeReal.add(labelrealtime);
        wrappeReal.add(scrollPane);
        wrapperCenter.add(wrappeReal);
        mainPanel.add(wrapperCenter, BorderLayout.CENTER);
        return mainPanel;
    }

    public JPanel controlPanels() {
        controlPanel.setLayout(new CardLayout());
        controlPanel.add("introduction", interfaceStart());
        controlPanel.add("main", mainPanel());

        return controlPanel;
    }

    public void prepareUi() {
        jframe.setTitle("Server Monitor File");

        jframe.add(controlPanels());
        jframe.setLocationRelativeTo(null);
        jframe.pack();
        // jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (JOptionPane.showConfirmDialog(jframe,
                        "Are you sure you want to close this window?", "Close Window?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {

                    jframe.dispose();
                    System.exit(0);

                }
            }
        });
        jframe.setVisible(true);
    }

    public static void main(String[] args) throws IOException {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Server program = new Server();
                program.prepareUi();

            }
        });

    }

    public void actionPerformed(ActionEvent ae) {
        String accommed = ae.getActionCommand();
        if (accommed.equals("start")) {

            CardLayout cardLayout = (CardLayout) (controlPanel.getLayout());
            cardLayout.show(controlPanel, "main");

            try {
                s1 = new ServerSocket(Integer.parseInt(PortTextField.getText()) );
            } catch (IOException e3) {
                // TODO Auto-generated catch block
              
            }

            new Thread() {
                public void run() {

                    receiveList.add("Waiting for client....");
                    receveMessaeJList.setListData(receiveList.toArray());

                    while (true) {
                        try {

                            ss = s1.accept();
                            System.out.println("new connet");

                        } catch (IOException e1) {
                            // TODO Auto-generated catch block
                           
                        }

                        new Thread() {
                            @Override
                            public void run() {
                                String[] name;
                                int f = 0;
                                DataInputStream din;
                                try {

                                    din = new DataInputStream(ss.getInputStream());
                                    while (true) {

                                        try {
                                            String msgin = din.readUTF();
                                            String[] value = msgin.split(" ");
                                            if (
                                                (msgin.contains("online")&& value.length<=3 )){
                                                receiveList.add(msgin + " -> Click here to choose folder");
                                                receveMessaeJList.setListData(receiveList.toArray());
                                            }
                                            if( (msgin.contains("offline")&& value.length<=3 )){
                                                receiveList.add(msgin);
                                                receveMessaeJList.setListData(receiveList.toArray());
                                            };
                                           

                                            if (!listSocket.containsKey(ss)) {
                                                listSocket.put(ss, value[1]);
                                            } else {

                                                if (!msgin.equals("Waiting for client....")) {

                                                    new Thread() {
                                                        @Override
                                                        public void run() {
                                                            if (!contentUser.containsKey(listSocket.get(ss))) {

                                                                valueMap.add(
                                                                        "Client " + listSocket.get(ss) + " online");
                                                                contentUser.put(listSocket.get(ss), valueMap);
                                                            } else {

                                                                valueMap.add(msgin);
                                                                contentUser.put(listSocket.get(ss), valueMap);
                                                            }
                                                        }
                                                    }.start();

                                                    new Thread() {
                                                        public void run() {
                                                            while (true) {

                                                                if (!wraperUser.containsKey(listSocket.get(ss))) {

                                                                    UIDetail userWraper = new UIDetail(ss);
                                                                    wraperUser.put(listSocket.get(ss), userWraper);

                                                                } else {
                                                                    ArrayList<String> formats=new ArrayList<String>();
                                                                    try {
                                                                        if (listSocket.get(ss) != null) {
                                                                            for (String e : contentUser.get(
                                                                                    listSocket.get(ss))) {

                                                                                if (!e.contains("Folder")) {
                                                                                    formats.add(e);
                                                                                }
                                                                            }

                                                                            realTimeActivity
                                                                                    .setListData(formats.toArray());
                                                                        }
                                                                    } catch (Exception e) {
                                                                        // TODO: handle exception
                                                                        
                                                                    }
                                                                   
                                                                  

                                                                }
                                                            }
                                                        };
                                                    }.start();

                                                }
                                                if (msgin.contains("offline")) {
                                                    break;
                                                }

                                            }

                                        } catch (IOException e) {
                                            // TODO Auto-generated catch block
                                            
                                           
                                        }

                                    }

                                } catch (IOException e1) {
                                    // TODO Auto-generated catch block
                                  
                                }

                            }
                        }.start();

                    }

                }

            }.start();

        } else if (accommed.equals("exit"))

        {
            int dialogButton = JOptionPane.YES_NO_OPTION;
            int dialogResult = JOptionPane.showConfirmDialog(null, "Would You Like to exit program?",
                    "Warning", dialogButton);
            if (dialogResult == JOptionPane.YES_OPTION) {
                try {
                    DataOutputStream dout = new DataOutputStream(
                            ss.getOutputStream());

                    dout.writeUTF("Client: " + " bye");
                    dout.flush();

                    jframe.dispose();
                    System.exit(0);

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                   
                }
            }

        } 

    }
}