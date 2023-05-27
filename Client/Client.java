package Client;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.UUID;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.*;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import java.util.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

class Client extends Thread implements ActionListener {
    protected Thread handleFile;
    protected MonitorFile track;
    protected MonitorLibrary trackLibrary;
    protected Thread handleFileLibrary;
    private final String pathfileImage = System.getProperty("user.dir") + "/Client/background.png";

    protected static Socket soc;
    protected static String pathFolderTrack = "asd";
    protected JFrame jframe;
    protected JPanel controlPanel;
    protected JTextField addressField;
    protected JList textContent;
    protected ArrayList<String> arrrayContent;
    protected Thread handle;
    protected JLabel pathFile;
    protected JFileChooser filechoose;
    protected static String ID;
    protected JLabel iP_Label;
    protected JPanel forwardsPanel;
    protected JLabel pathfileChoose;
    protected JPanel chooseFileSuccess;
    protected JList folderMonitor;
    protected ArrayList<String> pathFolder;
    protected JLabel status;
    protected JTextField portText;
    final File pathFolderDefault = new File(System.getProperty("user.home"));

    public void setPathFile(String pathFile) {
        pathfileChoose.setText(pathFile);
    }

    public Client() {
        jframe = new JFrame();
        controlPanel = new JPanel();
        arrrayContent = new ArrayList<String>();
        pathFolder = new ArrayList<String>();
        status = new JLabel("Status");
        iP_Label=new JLabel(" ");

    }

    public JPanel interfaceStart() {
        JPanel introduction_gui;
        JLabel picLabel;
        introduction_gui = new JPanel();
        introduction_gui.setBackground(Color.white);
        introduction_gui.setLayout(new BorderLayout());
        BufferedImage myPicture;
        try {
            String path = pathfileImage;
            myPicture = ImageIO.read(new File(path));
            picLabel = new JLabel(new ImageIcon(myPicture));
            introduction_gui.add(picLabel, BorderLayout.CENTER);
        } catch (IOException e) {
       
        }
        JPanel wrapper_btn_start;
        wrapper_btn_start = new JPanel();
        wrapper_btn_start.setBackground(Color.WHITE);
        wrapper_btn_start.setLayout(new FlowLayout());
        JLabel iplabel = new JLabel("IP:");
        addressField = new JTextField(10);
        addressField.setText("127.0.0.1");
        JLabel portlable = new JLabel("Port");
        portText = new JTextField(6);
        portText.setText("109");
        JButton startbtn;
        startbtn = new JButton("Connect to Server");
        startbtn.setForeground(Color.BLUE);
        startbtn.setActionCommand("start");
        startbtn.addActionListener(this);
        wrapper_btn_start.add(iplabel);
        wrapper_btn_start.add(addressField);
        wrapper_btn_start.add(portlable);
        wrapper_btn_start.add(portText);

        wrapper_btn_start.add(startbtn);
        introduction_gui.add(wrapper_btn_start, BorderLayout.SOUTH);

        return introduction_gui;
    }

    public JPanel mainPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        JLabel header = new JLabel("Welcome to Client monitor file");
        header.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        header.setFont(new Font("Serif", Font.PLAIN, 22));
        headerPanel.add(header);

        JPanel p = new JPanel();
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        headerPanel.add(p);
        
     

        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        JButton exitButton = new JButton("Exit");
        exitButton.setForeground(Color.RED);

        exitButton.setActionCommand("exit");
        exitButton.addActionListener(this);
        sidebar.add(exitButton);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(sidebar, BorderLayout.EAST);

        mainPanel.add(filchoosePanels(), BorderLayout.CENTER);

        return mainPanel;
    }

    public JPanel filchoosePanels() {
        // layout choose file after file exists
        chooseFileSuccess = new JPanel();
        chooseFileSuccess.setLayout((new BoxLayout(chooseFileSuccess, BoxLayout.Y_AXIS)));
        chooseFileSuccess.setBorder(BorderFactory.createLineBorder(Color.lightGray));

        JPanel wrapperID = new JPanel();
        wrapperID.setLayout(new BoxLayout(wrapperID, BoxLayout.X_AXIS));
        wrapperID.add(iP_Label);
        chooseFileSuccess.add(wrapperID);

        JPanel wrapperLabel = new JPanel();
        wrapperLabel.setLayout(new BoxLayout(wrapperLabel, BoxLayout.LINE_AXIS));
        pathfileChoose = new JLabel("Waiting server select folder....", JLabel.CENTER);

        wrapperLabel.add(pathfileChoose);
        wrapperLabel.add(Box.createRigidArea(new Dimension(20, 50)));
        chooseFileSuccess.add(wrapperLabel);
        folderMonitor = new JList(pathFolder.toArray());
        chooseFileSuccess.add(folderMonitor);
        chooseFileSuccess.add(Box.createRigidArea(new Dimension(20, 20)));
        status.setFont(new Font("Serif", Font.PLAIN, 22));
        JPanel wrapperStatus = new JPanel(new FlowLayout(FlowLayout.CENTER));
        wrapperStatus.add(status);
        chooseFileSuccess.add(wrapperStatus);


        return chooseFileSuccess;
    }

    public JPanel controlPanels() {
        controlPanel.setLayout(new CardLayout());
        controlPanel.add("introduction", interfaceStart());
        controlPanel.add("main", mainPanel());

        return controlPanel;
    }

    public void prepareUi() {
        jframe.setTitle("Client Monitor File");

        jframe.add(controlPanels());
        jframe.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (JOptionPane.showConfirmDialog(jframe,
                        "Are you sure you want to close this window?", "Close Window?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                
                    if(soc!=null)
                    {
                        try {
                            DataOutputStream dout = new DataOutputStream(soc.getOutputStream());
                            dout.writeUTF("Client " + ID + ": " + " offline ");
                            dout.flush();

                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                           
                        }
                    }
                  

                    System.exit(0);

                }
            }
        });

        jframe.setLocationRelativeTo(null);
        jframe.pack();
        jframe.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String accommed = ae.getActionCommand();
        if (accommed.equals("start")) {

            new Thread() {
                public void run() {
                    try {
                        String ip = addressField.getText();
                        ID = UUID.randomUUID().toString();
                        iP_Label.setText("ID - "+ ID);
                        String portConect = portText.getText();
                        setupConnect(ip, portConect);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                };
            }.start();
            CardLayout cardLayout = (CardLayout) (controlPanel.getLayout());
            cardLayout.show(controlPanel, "main");

        } else if (accommed.equals("exit")) {
            int dialogButton = JOptionPane.YES_NO_OPTION;
            int dialogResult = JOptionPane.showConfirmDialog(null, "Would You Like to exit program?", "Warning",
            dialogButton);
            if (dialogResult == JOptionPane.YES_OPTION) {

            JOptionPane.showMessageDialog(jframe, "Connection Dismissed.. :)",
            "INFORMATION CLIENT",
            JOptionPane.INFORMATION_MESSAGE);

            try {
                DataOutputStream dout = new DataOutputStream(soc.getOutputStream());
                dout.writeUTF("Client " + ID + ": " + " offline ");
                dout.flush();

            } catch (IOException e) {
                // TODO Auto-generated catch block
              
            }

            System.exit(0);

            }


        }

    }

    public void setupConnect(String ip, String portConect) {

        try {
            // in brackets ip address of server to connect and port number

            soc = new Socket(ip, Integer.parseInt((portConect)));
            DataOutputStream doutsends = new DataOutputStream(soc.getOutputStream());

            doutsends.writeUTF("Client " + ID + "" + " online get ID");
            doutsends.flush();
            int f = 0;

            JOptionPane.showMessageDialog(jframe, "Connection Established...",
                    "INFORMATION CLIENT",
                    JOptionPane.INFORMATION_MESSAGE);

            DataOutputStream doutsend = new DataOutputStream(soc.getOutputStream());
            doutsend.writeUTF("Client " + ID + "" + " online ");
            doutsend.flush();
            doutsend.writeUTF("Folder " + ID + "" + " online " + getPathAllFolders(pathFolderDefault));
            doutsend.flush();

            while (true) {

                String msgin;
                try {
                    DataInputStream din;

                    din = new DataInputStream(soc.getInputStream());
                    msgin = din.readUTF();
                    String[] getFolder = msgin.split(": ");
                    track = new MonitorFile();
                    trackLibrary = new MonitorLibrary();

                    if (getFolder[0].equals("Path")) {
                        pathFolderTrack = getFolder[1];
                      
                        track.active();
                        setPathFile("Path: " + pathFolderTrack);

                        handleFile = new Thread(track, "MonitorFiles");
                        handleFile.start();
                        status.setText("Running");

                    }

                    if (getFolder[0].equals("monitorlibrary")) {

                       
                        pathFolderTrack = getFolder[1];

                        setPathFile("Path: " + pathFolderTrack);
                        trackLibrary.active();
                        handleFileLibrary = new Thread(trackLibrary, "monitorlibrary");
                        handleFileLibrary.start();
                        status.setText("Running");

                    }
                    if (msgin.equals("Terminate")) {
                        track.cancel();
                        handleFile.interrupt();
                        try {
                            handleFile.join();
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                          
                        }

                        if (handleFile.isAlive()) {
                            status.setText("Running");
                        } else {
                            status.setText("Stopped");
                        }
                    }
                    if (msgin.equals("TerminateLibary")) {
                        trackLibrary.cancel();
                        handleFileLibrary.interrupt();
                        try {
                            handleFileLibrary.join();
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                           
                        }

                        if (handleFileLibrary.isAlive()) {
                            status.setText("Running");
                        } else {
                            status.setText("Stopped");
                        }
                    }


                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(jframe, "Server disconnected",
                            "WARNING CLIENT", JOptionPane.WARNING_MESSAGE);
                    System.exit(0);
                }

            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(jframe, "Server do not working",
                    "WARNING CLIENT", JOptionPane.WARNING_MESSAGE);
            CardLayout cardLayout = (CardLayout) (controlPanel.getLayout());

            cardLayout.show(controlPanel, "introduction");

        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Client client = new Client();
                client.prepareUi();
            }
        });

    }

    public String getPathAllFolders(final File folder) {
        ArrayList<String> folders = new ArrayList<String>();
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                if (!fileEntry.getName().contains(".")) {
                    folders.add(fileEntry.getAbsolutePath());
                }

            }
        }
        return folders.toString();
    }
}

class MonitorFile extends Client {
    static private volatile boolean cancel = false;
    private int current = 0;
    private int lastState = 0;
    private int currentFile = 0;
    private int lastStateFile = 0;
    private int currentFolder = 0;
    private int lastStateFolder = 0;
    private ArrayList<String> fileList = new ArrayList<String>();
    private ArrayList<String> folderList = new ArrayList<String>();
    private ArrayList<String> lastFileList = new ArrayList<String>();
    private ArrayList<String> currentFileList = new ArrayList<String>();
    private ArrayList<String> lastFolderList = new ArrayList<String>();
    private ArrayList<String> currentFolderList = new ArrayList<String>();
    private long lastStateModifile;
    private long currentModifile;

    private TreeMap<Long, String> stateList = new TreeMap<Long, String>();

    public int listFilesForFolder(final File folder) {

        for (final File fileEntry : folder.listFiles()) {
            stateList.put(fileEntry.lastModified(), fileEntry.getName());
            if (fileEntry.isDirectory()) {
                // System.out.println("Folder :" + fileEntry.getName());
                folderList.add("Folder :" + fileEntry.getName());
                listFilesForFolder(fileEntry);
            } else {

                if (fileEntry.getParentFile().getName().equals("project02")) {
                    System.out.println("File :" + fileEntry.getName());
                } else {
                    // System.out.println("-------File :" + fileEntry.getName());
                }
                fileList.add("File:" + fileEntry.getName());

            }
        }
        return fileList.size() + folderList.size();
    }

    public void cancel() {
        cancel = true;
    }

    public void active() {
        cancel = false;
    }

    @Override
    public void run() {
        System.out.println("\n" + currentThread().getName());
        System.out.println(pathFolderTrack);
        File folder = new File(pathFolderTrack);
        lastState = listFilesForFolder(folder);
        lastStateFile = fileList.size();
        lastStateFolder = folderList.size();
        currentFolderList = (ArrayList) folderList.clone();
        lastFolderList = (ArrayList) folderList.clone();
        currentFileList = (ArrayList) fileList.clone();
        lastFileList = (ArrayList) fileList.clone();
        lastStateModifile = stateList.lastEntry().getKey();
        int count = 0;
        while (!cancel) {
            System.out.println("Con chay trong thread" + count++);
            stateList.clear();
            fileList.clear();
            folderList.clear();
            current = listFilesForFolder(folder);
            if (stateList != null) {
                currentModifile = stateList.lastEntry().getKey();
            }
            currentFile = fileList.size();
            currentFolder = folderList.size();
            currentFolderList = (ArrayList) folderList.clone();
            currentFileList = (ArrayList) fileList.clone();

            if ((current != lastState)) {
                if (current > lastState) {
                    System.out.println(fileList.size());
                    if (currentFile > lastStateFile) {

                        try {
                            DataOutputStream doutsend = new DataOutputStream(soc.getOutputStream());
                            if (stateList != null) {
                                doutsend.writeUTF(
                                        "Client " + ID + "" + " create 1 file :" + stateList.lastEntry().getValue());
                                doutsend.flush();
                            }

                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                       
                        }

                    }
                    if (currentFolder > lastStateFolder) {
                        try {
                            DataOutputStream doutsend = new DataOutputStream(soc.getOutputStream());
                            if (stateList != null) {
                                doutsend.writeUTF(
                                        "Client " + ID + "" + " create 1 folder:" + stateList.lastEntry().getValue());
                                doutsend.flush();
                            }
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                          
                        }
                    }

                } else {
                    if (currentFile < lastStateFile) {

                        String deleteFileName = "";
                        for (int i = 0; i < lastFileList.size(); i++) {
                            {
                                if (!currentFileList.contains(lastFileList.get(i))) {

                                    deleteFileName = lastFileList.get(i);
                                }
                            }

                        }
                        try {
                            DataOutputStream doutsend = new DataOutputStream(soc.getOutputStream());
                            doutsend.writeUTF(
                                    "Client " + ID + "" + " delete 1 file: " + deleteFileName.split(":")[1]);
                            doutsend.flush();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                         
                        }

                    }
                    if (currentFolder < lastStateFolder) {

                        String deleteFolderName = "";
                        for (int i = 0; i < lastFolderList.size(); i++) {
                            {
                                if (!currentFolderList.contains(lastFolderList.get(i))) {
                                    System.out.println();
                                    deleteFolderName = lastFolderList.get(i);
                                }
                            }

                        }
                        try {
                            DataOutputStream doutsend = new DataOutputStream(soc.getOutputStream());
                            doutsend.writeUTF(
                                    "Client " + ID + "" + " delete 1 folder: " + deleteFolderName.split(":")[1]);
                            doutsend.flush();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                          
                        }
                    }

                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

            if ((currentModifile != lastStateModifile) && (current == lastState)) {
                try {
                    DataOutputStream doutsend = new DataOutputStream(soc.getOutputStream());
                    if (stateList != null) {
                        doutsend.writeUTF(
                                "Client " + ID + " modifiled 1 file: " + stateList.lastEntry().getValue());
                        doutsend.flush();
                    }

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                   
                }

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                  
                }

            }

            lastStateModifile = currentModifile;
            lastState = current;
            lastStateFile = currentFile;
            lastStateFolder = currentFolder;
            lastFolderList = currentFolderList;
            lastFileList = currentFileList;

        }

    }
}

class MonitorLibrary extends Client {
    static private volatile boolean cancel = false;

    public void cancel() {
        cancel = true;
    }

    public void active() {
        cancel = false;
    }

    public MonitorLibrary() {
        super();
    }

    @Override
    public void run() {
        // TODO: Change to following line to point to your input directory

        Path myDir = Paths.get(pathFolderTrack);

        while (!cancel) {
            try {
                // create the watcher service and register for events
                WatchService watcher = myDir.getFileSystem().newWatchService();
                myDir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_DELETE,
                        StandardWatchEventKinds.ENTRY_MODIFY);

                // let's grab a watch key
                WatchKey watchKey = watcher.take();

                // now let's process the events pending for this watch key
                List<WatchEvent<?>> events = watchKey.pollEvents();
                for (WatchEvent event : events) {

                    // get the file name for the event
                    Path fileName = (Path) event.context();

                    // determine the type of event and call appropriate handler method
                    if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                        handleCreatedFile(fileName);
                    }
                    if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                        handleDeletedFile(fileName);
                    }
                    if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                        handleModifiedFile(fileName);
                    }
                }

            } catch (Exception e) {
               
            }
        }
    }

    // HELPER METHODS

    private void handleCreatedFile(Path fileName) {
        try {
            DataOutputStream doutsend = new DataOutputStream(soc.getOutputStream());
            doutsend.writeUTF(
                    "Client " + ID + " create 1 file: " + fileName);
            doutsend.flush();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void handleDeletedFile(Path fileName) {

        try {
            DataOutputStream doutsend = new DataOutputStream(soc.getOutputStream());
            doutsend.writeUTF(
                    "Client " + ID + " delete 1 file: " + fileName);
            doutsend.flush();

        } catch (IOException e) {
            // TODO Auto-generated catch block
           
        }

    }

    private void handleModifiedFile(Path fileName) {

        try {
            DataOutputStream doutsend = new DataOutputStream(soc.getOutputStream());
            doutsend.writeUTF(
                    "Client " + ID + " modifiled 1 file: " + fileName);
            doutsend.flush();

        } catch (IOException e) {
            // TODO Auto-generated catch block
           
        }

    }
}
