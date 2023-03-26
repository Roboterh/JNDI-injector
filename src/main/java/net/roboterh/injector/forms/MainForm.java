package net.roboterh.injector.forms;

import com.formdev.flatlaf.FlatLightLaf;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import net.roboterh.injector.servers.HTTPServer;
import net.roboterh.injector.servers.LdapServer;
import net.roboterh.injector.utils.JTextAreaAppender;
import net.roboterh.injector.utils.LdapSearchListener;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Objects;

import static net.roboterh.injector.utils.ConstantUtil.*;

public class MainForm {

    private static final Logger logger = LogManager.getLogger(MainForm.class);

    private LdapSearchListener ldapSearchListener;
    private ArrayList<JPanel> panelList = new ArrayList<JPanel>();
    private StyledDocument doc;

    private JPanel InjectorPanel;
    private JPanel TopPanel;
    private JPanel MainPanel;
    private JPanel BottomPanel;
    private JPanel ServerPanel;
    private JTextField IPTextField;
    private JTextField LdapPortTextField;
    private JLabel IPLabel;
    private JLabel LdapPortLabel;
    private JLabel StatusLabel;
    private JLabel HttpPortLabel;
    private JTextField HttpPortTextField;
    private JPanel DescribePanel;
    private JPanel GadgetPanel;
    private JLabel PayloadLabel;
    private JButton ServerButton;
    private JList WaysList;
    private JScrollPane WaysScrollPane;
    private JLabel WaysLabel;
    private JButton WaysButton;
    private JScrollPane PayloadScrollPane;
    private JList PayloadList;
    private JButton PayloadButton;
    private JLabel ldapService;
    private JLabel httpService;
    private JLabel ldapLight;
    private JLabel httpLight;
    private JPanel StatusPanel;
    private JScrollPane OutputScrollPanel;
    private JTextArea outputTextArea;
    private JPanel CommandPanel;
    private JLabel CommandLabel;
    private JTextField CommandTextField;
    private JPanel ReversePanel;
    private JLabel ReverseIpLabel;
    private JTextField ReverseIpTextField;
    private JLabel ReversePortLabel;
    private JTextField ReversePortTextField;
    private JPanel DnsLogPanel;
    private JLabel DnsLogLabel;
    private JTextField DnsLogTextField;
    private JPanel DescriptionPanel;
    private JLabel DescriptionLabel;
    private JLabel LdapSearchLabel;
    private JRadioButton dnsLogRadioButton;
    private JRadioButton commandRadioButton;
    private JTextField cmdOrLinkTextField;
    private JLabel cmdOrLinkLabel;
    private JPanel JDK8u20Panel;
    private JLabel payloadLabel;
    private JPanel XXEPanel;
    private JLabel XMLLabel;
    private JTextField XXETextField;
    private JPanel CCPanel;
    private JLabel BeanClassLabel;
    private JRadioButton configurationRadioButton;
    private JRadioButton configuration2RadioButton;
    private JTextField attributionCoveredPropertiesTextField;
    private JLabel FileLabel;
    private JPanel JdbcPanel;
    private JLabel FactoryLabel;
    private JRadioButton dbcp1TomcatRadioButton;
    private JRadioButton dbcp2TomcatRadioButton;
    private JRadioButton commonsDbcp1RadioButton;
    private JRadioButton commonsDbcp2RadioButton;
    private JRadioButton tomcatJdbcRadioButton;
    private JRadioButton druidRadioButton;
    private JPanel MysqlPanel;
    private JLabel FakeIpLabel;
    private JTextField FakeIpTextField;
    private JLabel FakePortLabel;
    private JTextField FakePortTextField;
    private JLabel FakeAttributeLabel;
    private JTextField FakeAttributeTextField;
    private JPanel UnMysqlPanel;
    private JLabel ScriptFileLabel;
    private JTextField ScriptFileTextField;
    private JTextPane LdapSearchTextPane;
    private JPanel TomcatGroovyPanel;
    private JLabel TomcatGroovyWaysLabel;
    private JPanel FilePanel;
    private JLabel FilenameLabel;
    private JTextField FilenameTextField;
    private JRadioButton NormalRadioButton;
    private JRadioButton ASTTestRadioButton;
    private JPanel JDK8u20;
    private JTextArea outputArea;

    public MainForm() {
        // init the LdapSearchTextPane
        doc = LdapSearchTextPane.getStyledDocument();

        initService();
        initGadgetSelect();
        initCommandOutput();

        // init the panelList
        panelList.add(CommandPanel);
        panelList.add(FilePanel);
        panelList.add(TomcatGroovyPanel);
        panelList.add(ReversePanel);
        panelList.add(DnsLogPanel);
        panelList.add(JDK8u20Panel);
        panelList.add(XXEPanel);
        panelList.add(CCPanel);
        panelList.add(JdbcPanel);

        // init the visible component
//        CommandPanel.setVisible(false);
//        ReversePanel.setVisible(false);
//        DnsLogPanel.setVisible(false);

        // init the LdapSearchListener
//        ldapSearchListener.register();

        // init the LdapSearchLink
//        LdapSearchLabel.setText("LDAP Search Link:   " + "Please start your server ...");
        LdapSearchTextPane.setText("LDAP Search Link:   " + "Please start your server ...");
//        try {
//            doc.remove(0, doc.getLength());
//            doc.insertString(0, "LDAP Search Link:   " + "Please start your server ...", null);
//            // highlight
//            doc.setCharacterAttributes(10, 4, LdapSearchTextPane.getStyle("highlight"), false);
//        } catch (BadLocationException e) {
//            e.printStackTrace();
//        }

        // init the selected list
        WaysList.setSelectedIndex(0);
        PayloadList.setSelectedIndex(0);

    }

    private void initService() {
        // add listener to reset
        IPTextField.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                IPTextField.setText(null);
            }
        });
        LdapPortTextField.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                LdapPortTextField.setText(null);
            }
        });
        HttpPortTextField.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                HttpPortTextField.setText(null);
            }
        });

        // success and fail images
        // success image
        ImageIcon successImageIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/ui/imgs/success.png")));
        Image successImage = successImageIcon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        ImageIcon successScaledIcon = new ImageIcon(successImage);
        // fail image
        ImageIcon failImageIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/ui/imgs/fail.png")));
        Image failImage = failImageIcon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        ImageIcon failScaledIcon = new ImageIcon(failImage);

        // init the lights
        ldapLight.setIcon(failScaledIcon);
        ldapLight.repaint();
        httpLight.setIcon(failScaledIcon);
        httpLight.repaint();

        // add listener to start the server
        ServerButton.addActionListener(e -> {
            try {
                LdapServer ldapServer = new LdapServer(IPTextField.getText(), Integer.parseInt(LdapPortTextField.getText()));
                HTTPServer httpServer = new HTTPServer(IPTextField.getText(), Integer.parseInt(HttpPortTextField.getText()));

                // start the ldap and http servers
                try {
                    ldapServer.startServer();
                    // set the ldapLight to color green
                    ldapLight.setIcon(successScaledIcon);
                    ldapLight.repaint();
                } catch (Exception ex) {
                    logger.info(String.format("Start ldap service wrong: %s", ex.getMessage()));
                }
                try {
                    httpServer.startServer();
                    // set the httpLight to color green
                    httpLight.setIcon(successScaledIcon);
                    httpLight.repaint();
                } catch (Exception exc) {
                    logger.info(String.format("Start http service wrong: %s", exc.getMessage()));
                }
            } catch (Exception exception) {
                logger.info(exception.getMessage());
            }
        });

        // print the successful flag
        logger.info("Function initService ended...");
    }

    private void initGadgetSelect() {
        // Buttons pressed
        WaysButton.addActionListener(e -> JOptionPane.showMessageDialog(InjectorPanel, WaysNote, "JNDI注入方式", JOptionPane.QUESTION_MESSAGE));
        PayloadButton.addActionListener(e -> JOptionPane.showMessageDialog(InjectorPanel, PayloadNote, "Payload的选择", JOptionPane.QUESTION_MESSAGE));

        // notes suspend
        WaysList.setToolTipText(""); // 禁用JList默认的鼠标悬浮提示
        WaysList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setToolTipText((String) value); // 设置当前元素的鼠标悬浮提示为元素的值
                switch ((String) value) {
                    case "Basic":
                        label.setToolTipText(BasicNote);
                        break;
                    case "Deserialization":
                        label.setToolTipText(DeserializationNote);
                        break;
                    case "Tomcat":
                        label.setToolTipText(TomcatNote);
                        break;
                    case "TomcatEL":
                        label.setToolTipText(TomcatElNote);
                        break;
                    case "TomcatGroovy":
                        label.setToolTipText(TomcatGroovyNote);
                        break;
                    case "TomcatSnakeYaml":
                        label.setToolTipText(TomcatSnakeYamlNote);
                        break;
                    case "TomcatXStream":
                        label.setToolTipText(TomcatXStreamNote);
                        break;
                    case "TomcatMVEL":
                        label.setToolTipText(TomcatMVELNote);
                        break;
                    case "TomcatCommonsConfiguration":
                        label.setToolTipText(TomcatCommonsConfigurationNote);
                        break;
                    case "TomcatOrDruidJdbc":
                        label.setToolTipText(TomcatOrDruidJdbc);
                        break;
                }
                return label;
            }
        });

        // change particular payloads
        WaysList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                // obtain the element of waysList
                String selected = (String) WaysList.getSelectedValue();
                switch (selected) {
                    case "Basic":
                        PayloadList.setListData(BasicPayload);
                        DescriptionLabel.setText(BasicDescription);
                        break;
                    case "Deserialization":
                        PayloadList.setListData(DeserializationPayload);
                        DescriptionLabel.setText(DeserializationDescription);
                        break;
                    case "Tomcat":
                        PayloadList.setListData(TomcatPayload);
                        DescriptionLabel.setText(TomcatDescription);
                        break;
                    case "TomcatEL":
                        PayloadList.setListData(TomcatELPayload);
                        DescriptionLabel.setText(TomcatELDescription);
                        break;
                    case "TomcatGroovy":
                        PayloadList.setListData(TomcatGroovyPayload);
                        DescriptionLabel.setText(TomcatGroovyDescription);
                        break;
                    case "TomcatSnakeYaml":
                        PayloadList.setListData(TomcatSnakeYamlPayload);
                        DescriptionLabel.setText(TomcatSnakeYamlDescription);
                        break;
                    case "TomcatXStream":
                        PayloadList.setListData(TomcatXStreamPayload);
                        DescriptionLabel.setText(TomcatXStreamDescription);
                        break;
                    case "TomcatMVEL":
                        PayloadList.setListData(TomcatMVELPayload);
                        DescriptionLabel.setText(TomcatMVELDescription);
                        break;
                    case "TomcatCommonsConfiguration":
                        PayloadList.setListData(TomcatCommonsConfigurationPayload);
                        DescriptionLabel.setText(TomcatCommonsConfigurationDescription);
                        break;
                    case "TomcatOrDruidJdbc":
                        PayloadList.setListData(TomcatOrDruidJdbcPayload);
                        DescriptionLabel.setText(TomcatOrDruidJdbcDescription);
                        break;
                }
            }
        });

        // print the successful flag
        logger.info("Function initGadgetSelect ended...");
    }

    private void initCommandOutput() {
//        PrintStream printStream = new PrintStream(new OutputStream() {
//            @Override
//            public void write(int b) throws IOException {
//                outputArea.append(String.valueOf((char) b));
//                outputArea.setCaretPosition(outputArea.getDocument().getLength());
//            }
//        });
//        System.setOut(printStream);
//        System.setErr(printStream);
        JTextAreaAppender.setTextArea(outputTextArea);

        PayloadList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                String selected = (String) PayloadList.getSelectedValue();
                try {
                    switch (selected) {
                        case "Command":
                            setPanelVisibleToTrue(CommandPanel);

                            if (ldapSearchListener != null) {
                                ldapSearchListener.unregister();
                            }

                            if ((((String) WaysList.getSelectedValue()).equals("TomcatGroovy"))) {
                                // if the way is TomcatGroovy
                                TomcatGroovyPanel.setVisible(true);
                                ButtonGroup buttonGroup = new ButtonGroup();
                                buttonGroup.add(NormalRadioButton);
                                buttonGroup.add(ASTTestRadioButton);

                                ldapSearchListener = new LdapSearchListener(WaysList, PayloadList, CommandTextField, buttonGroup, LdapSearchTextPane);
                            } else {
                                // normal command
                                ldapSearchListener = new LdapSearchListener(WaysList, PayloadList, CommandTextField, LdapSearchTextPane);
                            }

                            ldapSearchListener.register();
                            break;
                        case "ReverseShell":
                            setPanelVisibleToTrue(ReversePanel);

                            if (ldapSearchListener != null) {
                                ldapSearchListener.unregister();
                            }
                            ldapSearchListener = new LdapSearchListener(WaysList, PayloadList,
                                    ReverseIpTextField, ReversePortTextField, LdapSearchTextPane);
                            ldapSearchListener.register();
                            break;
                        case "File":
                            setPanelVisibleToTrue(FilePanel);

                            // init the FilenameTextField
                            String way = (String) WaysList.getSelectedValue();
                            if (way.equals("TomcatEL")) {
                                FilenameTextField.setText("TomcatEL.js");
                            } else if (way.equals("TomcatGroovy")) {
                                FilenameTextField.setText("Groovy.groovy");
                            } else if (way.equals("TomcatSnakeYaml")) {
                                FilenameTextField.setText("SnakeYaml.yaml");
                            } else if (way.equals("TomcatXStream")) {
                                FilenameTextField.setText("XStream.xml");
                            } else if (way.equals("TomcatMVEL")) {
                                FilenameTextField.setText("MVEL.txt");
                            }

                            if (ldapSearchListener != null) {
                                ldapSearchListener.unregister();
                            }
                            ldapSearchListener = new LdapSearchListener(WaysList, PayloadList,
                                    FilenameTextField, LdapSearchTextPane);
                            ldapSearchListener.register();
                            break;
                        case "URLDNS":
                        case "DnsLog":
                            setPanelVisibleToTrue(DnsLogPanel);

                            if (ldapSearchListener != null) {
                                ldapSearchListener.unregister();
                            }

                            if ((((String) WaysList.getSelectedValue()).equals("TomcatGroovy"))) {
                                // if the way is TomcatGroovy
                                TomcatGroovyPanel.setVisible(true);
                                ButtonGroup buttonGroup = new ButtonGroup();
                                buttonGroup.add(NormalRadioButton);
                                buttonGroup.add(ASTTestRadioButton);

                                ldapSearchListener = new LdapSearchListener(WaysList, PayloadList, DnsLogTextField, buttonGroup, LdapSearchTextPane);
                            } else {
                                ldapSearchListener = new LdapSearchListener(WaysList, PayloadList, DnsLogTextField, LdapSearchTextPane);
                            }

                            ldapSearchListener.register();
                            break;
                        case "JDK8u20":
                            setPanelVisibleToTrue(JDK8u20Panel);

                            ButtonGroup buttonGroup = new ButtonGroup();
                            buttonGroup.add(dnsLogRadioButton);
                            buttonGroup.add(commandRadioButton);

                            if (ldapSearchListener != null) {
                                ldapSearchListener.unregister();
                            }
                            ldapSearchListener = new LdapSearchListener(WaysList, PayloadList, cmdOrLinkTextField, buttonGroup
                                    , LdapSearchTextPane);
                            ldapSearchListener.register();
                            break;
                        case "XXE":
                            setPanelVisibleToTrue(XXEPanel);

                            if (ldapSearchListener != null) {
                                ldapSearchListener.unregister();
                            }
                            ldapSearchListener = new LdapSearchListener(WaysList, PayloadList, XXETextField, LdapSearchTextPane);
                            ldapSearchListener.register();
                            break;
                        case "BeanFactory":
                        case "GenericNamingResourcesFactory":
                            setPanelVisibleToTrue(CCPanel);
                            ButtonGroup buttonGroup1 = new ButtonGroup();
                            buttonGroup1.add(configurationRadioButton);
                            buttonGroup1.add(configuration2RadioButton);

                            if (ldapSearchListener != null) {
                                ldapSearchListener.unregister();
                            }
                            ldapSearchListener = new LdapSearchListener(WaysList, PayloadList
                                    , attributionCoveredPropertiesTextField, buttonGroup1, LdapSearchTextPane);
                            ldapSearchListener.register();
                            break;
                        case "Mysql":
                            setPanelVisibleToTrue(JdbcPanel);
                            MysqlPanel.setVisible(true);
                            UnMysqlPanel.setVisible(false);

                            ButtonGroup buttonGroup2 = new ButtonGroup();
                            buttonGroup2.add(dbcp1TomcatRadioButton);
                            buttonGroup2.add(dbcp2TomcatRadioButton);
                            buttonGroup2.add(commonsDbcp1RadioButton);
                            buttonGroup2.add(commonsDbcp2RadioButton);
                            buttonGroup2.add(tomcatJdbcRadioButton);
                            buttonGroup2.add(druidRadioButton);

                            if (ldapSearchListener != null) {
                                ldapSearchListener.unregister();
                            }
                            ldapSearchListener = new LdapSearchListener(WaysList, PayloadList, FakeIpTextField, FakePortTextField,
                                    FakeAttributeTextField, buttonGroup2, LdapSearchTextPane);
                            ldapSearchListener.register();
                            break;
                        case "H2":
                            setPanelVisibleToTrue(JdbcPanel);
                            UnMysqlPanel.setVisible(true);
                            MysqlPanel.setVisible(false);

                            ButtonGroup buttonGroup3 = new ButtonGroup();
                            buttonGroup3.add(dbcp1TomcatRadioButton);
                            buttonGroup3.add(dbcp2TomcatRadioButton);
                            buttonGroup3.add(commonsDbcp1RadioButton);
                            buttonGroup3.add(commonsDbcp2RadioButton);
                            buttonGroup3.add(tomcatJdbcRadioButton);
                            buttonGroup3.add(druidRadioButton);

                            if (ldapSearchListener != null) {
                                ldapSearchListener.unregister();
                            }
                            ldapSearchListener = new LdapSearchListener(WaysList, PayloadList, ScriptFileTextField, buttonGroup3,
                                    LdapSearchTextPane);
                            ldapSearchListener.register();
                            break;
                        default:
                            logger.info(String.format("Wrong payload is %s ...", selected));
                    }
                } catch (NullPointerException exx) {
                    //ignore
                }
            }
        });

        // print the successful flag
        logger.info("Function initCommandOutput ended ...");
    }

    private void setPanelVisibleToTrue(JPanel panel) {
        for (JPanel jPanel : panelList) {
            if (jPanel.equals(panel)) {
                jPanel.setVisible(true);
            } else {
                jPanel.setVisible(false);
            }
        }
    }

    public static void main() {
        FlatLightLaf.setup();
        JFrame frame = new JFrame("JNDI-injector");
        frame.setIconImage(new ImageIcon(MainForm.class.getResource("/ui/imgs/img1.png")).getImage());
        frame.setContentPane(new MainForm().InjectorPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        // print the successful flag
        logger.info("Form MainForm started...");
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        InjectorPanel = new JPanel();
        InjectorPanel.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        TopPanel = new JPanel();
        TopPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        InjectorPanel.add(TopPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        TopPanel.setBorder(BorderFactory.createTitledBorder(null, "服务启动区", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        ServerPanel = new JPanel();
        ServerPanel.setLayout(new GridLayoutManager(1, 7, new Insets(0, 0, 0, 0), -1, -1));
        TopPanel.add(ServerPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        IPLabel = new JLabel();
        IPLabel.setText("IP:");
        ServerPanel.add(IPLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        IPTextField = new JTextField();
        IPTextField.setText("127.0.0.1");
        ServerPanel.add(IPTextField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        LdapPortLabel = new JLabel();
        LdapPortLabel.setText("LDAP-PORT");
        ServerPanel.add(LdapPortLabel, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        LdapPortTextField = new JTextField();
        LdapPortTextField.setText("1389");
        ServerPanel.add(LdapPortTextField, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        HttpPortLabel = new JLabel();
        HttpPortLabel.setText("HTTP-PORT");
        ServerPanel.add(HttpPortLabel, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        HttpPortTextField = new JTextField();
        HttpPortTextField.setText("3456");
        ServerPanel.add(HttpPortTextField, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        ServerButton = new JButton();
        ServerButton.setText("运行服务");
        ServerPanel.add(ServerButton, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        StatusPanel = new JPanel();
        StatusPanel.setLayout(new GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1));
        TopPanel.add(StatusPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        StatusLabel = new JLabel();
        StatusLabel.setText("运行状态：");
        StatusPanel.add(StatusLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ldapService = new JLabel();
        ldapService.setText("LDAP service");
        StatusPanel.add(ldapService, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        httpService = new JLabel();
        httpService.setText("HTTP service");
        StatusPanel.add(httpService, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ldapLight = new JLabel();
        ldapLight.setText("");
        StatusPanel.add(ldapLight, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        httpLight = new JLabel();
        httpLight.setText("");
        StatusPanel.add(httpLight, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        MainPanel = new JPanel();
        MainPanel.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        InjectorPanel.add(MainPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        MainPanel.setBorder(BorderFactory.createTitledBorder(null, "Gadget配置区", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        DescribePanel = new JPanel();
        DescribePanel.setLayout(new GridLayoutManager(7, 1, new Insets(0, 0, 0, 0), -1, -1));
        MainPanel.add(DescribePanel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        DescribePanel.setBorder(BorderFactory.createTitledBorder(null, "Gadget的描述", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        CommandPanel = new JPanel();
        CommandPanel.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        DescribePanel.add(CommandPanel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        CommandLabel = new JLabel();
        CommandLabel.setText("command: ");
        CommandPanel.add(CommandLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        CommandTextField = new JTextField();
        CommandTextField.setText("calc");
        CommandPanel.add(CommandTextField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        ReversePanel = new JPanel();
        ReversePanel.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        DescribePanel.add(ReversePanel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        ReverseIpLabel = new JLabel();
        ReverseIpLabel.setText("IP: ");
        ReversePanel.add(ReverseIpLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ReverseIpTextField = new JTextField();
        ReverseIpTextField.setText("127.0.0.1");
        ReversePanel.add(ReverseIpTextField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        ReversePortLabel = new JLabel();
        ReversePortLabel.setText("PORT: ");
        ReversePanel.add(ReversePortLabel, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ReversePortTextField = new JTextField();
        ReversePortTextField.setText("8888");
        ReversePanel.add(ReversePortTextField, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        DnsLogPanel = new JPanel();
        DnsLogPanel.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        DescribePanel.add(DnsLogPanel, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        DnsLogLabel = new JLabel();
        DnsLogLabel.setText("link: ");
        DnsLogPanel.add(DnsLogLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        DnsLogTextField = new JTextField();
        DnsLogTextField.setText("www.baidu.com");
        DnsLogPanel.add(DnsLogTextField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        DescriptionPanel = new JPanel();
        DescriptionPanel.setLayout(new GridLayoutManager(5, 1, new Insets(0, 0, 0, 0), -1, -1));
        DescribePanel.add(DescriptionPanel, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        DescriptionLabel = new JLabel();
        DescriptionLabel.setText("");
        DescriptionPanel.add(DescriptionLabel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        XXEPanel = new JPanel();
        XXEPanel.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        DescriptionPanel.add(XXEPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        XMLLabel = new JLabel();
        XMLLabel.setText("XML-FILE：");
        XXEPanel.add(XMLLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        XXETextField = new JTextField();
        XXETextField.setText("XXE.xml");
        XXEPanel.add(XXETextField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        CCPanel = new JPanel();
        CCPanel.setLayout(new GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1));
        DescriptionPanel.add(CCPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        configurationRadioButton = new JRadioButton();
        configurationRadioButton.setSelected(true);
        configurationRadioButton.setText("configuration");
        CCPanel.add(configurationRadioButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        BeanClassLabel = new JLabel();
        BeanClassLabel.setText("BeanClass: ");
        CCPanel.add(BeanClassLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        configuration2RadioButton = new JRadioButton();
        configuration2RadioButton.setText("configuration2");
        CCPanel.add(configuration2RadioButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        attributionCoveredPropertiesTextField = new JTextField();
        attributionCoveredPropertiesTextField.setText("AttributionCovered.properties");
        CCPanel.add(attributionCoveredPropertiesTextField, new GridConstraints(1, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        FileLabel = new JLabel();
        FileLabel.setText("File: ");
        CCPanel.add(FileLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        JdbcPanel = new JPanel();
        JdbcPanel.setLayout(new GridLayoutManager(4, 7, new Insets(0, 0, 0, 0), -1, -1));
        DescriptionPanel.add(JdbcPanel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        UnMysqlPanel = new JPanel();
        UnMysqlPanel.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        JdbcPanel.add(UnMysqlPanel, new GridConstraints(3, 0, 1, 7, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        ScriptFileLabel = new JLabel();
        ScriptFileLabel.setText("ScriptFile: ");
        UnMysqlPanel.add(ScriptFileLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ScriptFileTextField = new JTextField();
        ScriptFileTextField.setText("Groovy.groovy / JavaScript.js / SQL.sql");
        UnMysqlPanel.add(ScriptFileTextField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        MysqlPanel = new JPanel();
        MysqlPanel.setLayout(new GridLayoutManager(2, 4, new Insets(0, 0, 0, 0), -1, -1));
        JdbcPanel.add(MysqlPanel, new GridConstraints(2, 0, 1, 7, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        FakeIpLabel = new JLabel();
        FakeIpLabel.setText("FakeIp: ");
        MysqlPanel.add(FakeIpLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        FakeIpTextField = new JTextField();
        FakeIpTextField.setText("127.0.0.1");
        MysqlPanel.add(FakeIpTextField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        FakePortLabel = new JLabel();
        FakePortLabel.setText("FakePort: ");
        MysqlPanel.add(FakePortLabel, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        FakePortTextField = new JTextField();
        FakePortTextField.setText("3306");
        MysqlPanel.add(FakePortTextField, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        FakeAttributeLabel = new JLabel();
        FakeAttributeLabel.setText("FakeAttribute: ");
        MysqlPanel.add(FakeAttributeLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        FakeAttributeTextField = new JTextField();
        FakeAttributeTextField.setText("autoDeserialize=true&statementInterceptors=com.mysql.jdbc.interceptors.ServerStatusDiffInterceptor&user=yso_CommonsCollections4_calc");
        MysqlPanel.add(FakeAttributeTextField, new GridConstraints(1, 1, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        FactoryLabel = new JLabel();
        FactoryLabel.setText("Factory: ");
        JdbcPanel.add(FactoryLabel, new GridConstraints(0, 0, 2, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        dbcp1TomcatRadioButton = new JRadioButton();
        dbcp1TomcatRadioButton.setSelected(true);
        dbcp1TomcatRadioButton.setText("dbcp1Tomcat");
        JdbcPanel.add(dbcp1TomcatRadioButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        commonsDbcp1RadioButton = new JRadioButton();
        commonsDbcp1RadioButton.setText("commonsDbcp1");
        JdbcPanel.add(commonsDbcp1RadioButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        dbcp2TomcatRadioButton = new JRadioButton();
        dbcp2TomcatRadioButton.setText("dbcp2Tomcat");
        JdbcPanel.add(dbcp2TomcatRadioButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        commonsDbcp2RadioButton = new JRadioButton();
        commonsDbcp2RadioButton.setText("commonsDbcp2");
        JdbcPanel.add(commonsDbcp2RadioButton, new GridConstraints(1, 2, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        druidRadioButton = new JRadioButton();
        druidRadioButton.setText("druid");
        JdbcPanel.add(druidRadioButton, new GridConstraints(1, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tomcatJdbcRadioButton = new JRadioButton();
        tomcatJdbcRadioButton.setText("tomcatJdbc");
        JdbcPanel.add(tomcatJdbcRadioButton, new GridConstraints(0, 4, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        LdapSearchTextPane = new JTextPane();
        LdapSearchTextPane.setText("");
        DescriptionPanel.add(LdapSearchTextPane, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        JDK8u20Panel = new JPanel();
        JDK8u20Panel.setLayout(new GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1));
        DescribePanel.add(JDK8u20Panel, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        dnsLogRadioButton = new JRadioButton();
        dnsLogRadioButton.setEnabled(true);
        dnsLogRadioButton.setHideActionText(false);
        dnsLogRadioButton.setSelected(true);
        dnsLogRadioButton.setText("DnsLog");
        JDK8u20Panel.add(dnsLogRadioButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        commandRadioButton = new JRadioButton();
        commandRadioButton.setText("Command");
        JDK8u20Panel.add(commandRadioButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cmdOrLinkLabel = new JLabel();
        cmdOrLinkLabel.setText("cmdOrLink: ");
        JDK8u20Panel.add(cmdOrLinkLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cmdOrLinkTextField = new JTextField();
        cmdOrLinkTextField.setText("www.baidu.com");
        JDK8u20Panel.add(cmdOrLinkTextField, new GridConstraints(1, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        payloadLabel = new JLabel();
        payloadLabel.setText("payload");
        JDK8u20Panel.add(payloadLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        FilePanel = new JPanel();
        FilePanel.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        DescribePanel.add(FilePanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        FilenameLabel = new JLabel();
        FilenameLabel.setText("Filename: ");
        FilePanel.add(FilenameLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        FilenameTextField = new JTextField();
        FilenameTextField.setText("");
        FilePanel.add(FilenameTextField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        TomcatGroovyPanel = new JPanel();
        TomcatGroovyPanel.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        DescribePanel.add(TomcatGroovyPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        TomcatGroovyWaysLabel = new JLabel();
        TomcatGroovyWaysLabel.setText("Ways: ");
        TomcatGroovyPanel.add(TomcatGroovyWaysLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        NormalRadioButton = new JRadioButton();
        NormalRadioButton.setSelected(true);
        NormalRadioButton.setText("Normal");
        TomcatGroovyPanel.add(NormalRadioButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ASTTestRadioButton = new JRadioButton();
        ASTTestRadioButton.setText("ASTTest");
        TomcatGroovyPanel.add(ASTTestRadioButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        GadgetPanel = new JPanel();
        GadgetPanel.setLayout(new GridLayoutManager(2, 4, new Insets(0, 0, 0, 0), -1, -1));
        MainPanel.add(GadgetPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        GadgetPanel.setBorder(BorderFactory.createTitledBorder(null, "Gadget的选择", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        WaysScrollPane = new JScrollPane();
        GadgetPanel.add(WaysScrollPane, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        WaysList = new JList();
        final DefaultListModel defaultListModel1 = new DefaultListModel();
        defaultListModel1.addElement("Basic");
        defaultListModel1.addElement("Deserialization");
        defaultListModel1.addElement("Tomcat");
        defaultListModel1.addElement("TomcatEL");
        defaultListModel1.addElement("TomcatGroovy");
        defaultListModel1.addElement("TomcatSnakeYaml");
        defaultListModel1.addElement("TomcatXStream");
        defaultListModel1.addElement("TomcatMVEL");
        defaultListModel1.addElement("TomcatCommonsConfiguration");
        defaultListModel1.addElement("TomcatOrDruidJdbc");
        WaysList.setModel(defaultListModel1);
        WaysScrollPane.setViewportView(WaysList);
        WaysLabel = new JLabel();
        WaysLabel.setText("JNDI注入的方式");
        GadgetPanel.add(WaysLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        WaysButton = new JButton();
        WaysButton.setText("提示");
        GadgetPanel.add(WaysButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        PayloadScrollPane = new JScrollPane();
        GadgetPanel.add(PayloadScrollPane, new GridConstraints(1, 2, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        PayloadList = new JList();
        final DefaultListModel defaultListModel2 = new DefaultListModel();
        PayloadList.setModel(defaultListModel2);
        PayloadScrollPane.setViewportView(PayloadList);
        PayloadLabel = new JLabel();
        PayloadLabel.setText("Payload的选择");
        GadgetPanel.add(PayloadLabel, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        PayloadButton = new JButton();
        PayloadButton.setText("提示");
        GadgetPanel.add(PayloadButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        BottomPanel = new JPanel();
        BottomPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        InjectorPanel.add(BottomPanel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        BottomPanel.setBorder(BorderFactory.createTitledBorder(null, "命令行输出区", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        OutputScrollPanel = new JScrollPane();
        BottomPanel.add(OutputScrollPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        outputTextArea = new JTextArea();
        outputTextArea.setBackground(new Color(-16383482));
        outputTextArea.setForeground(new Color(-7196270));
        OutputScrollPanel.setViewportView(outputTextArea);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return InjectorPanel;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
