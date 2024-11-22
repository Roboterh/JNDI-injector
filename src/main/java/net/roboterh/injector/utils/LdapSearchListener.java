package net.roboterh.injector.utils;

import net.roboterh.injector.servers.LdapServer;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;

/**
 * Dynamic ldap connection assembler
 */
public class LdapSearchListener implements DocumentListener, ListSelectionListener, RegistrableListener, ItemListener {
    public static String ldapSearch = null;

    private JList waysList;
    private JList payloadList;
    private JTextField textField1 = null;
    private JTextField textField2 = null;
    private JTextField textField3 = null;
    private JTextPane label;
    private ButtonGroup buttonGroup;


    public LdapSearchListener(JList waysList, JList payloadList, JTextPane label) {
        this.waysList = waysList;
        this.payloadList = payloadList;
        this.label = label;
    }
    public LdapSearchListener(JList waysList, JList payloadList, JTextField textField1, JTextPane label) {
        this.waysList = waysList;
        this.payloadList = payloadList;
        this.textField1 = textField1;
        this.label = label;
    }
    public LdapSearchListener(JList waysList, JList payloadList, JTextField textField1, ButtonGroup buttonGroup, JTextPane label) {
        this.waysList = waysList;
        this.payloadList = payloadList;
        this.textField1 = textField1;
        this.label = label;
        this.buttonGroup = buttonGroup;
    }
    public LdapSearchListener(JList waysList, JList payloadList, JTextField textField1, JTextField textField2, JTextPane label) {
        this.waysList = waysList;
        this.payloadList = payloadList;
        this.textField1 = textField1;
        this.textField2 = textField2;
        this.label = label;
    }
    public LdapSearchListener(JList waysList, JList payloadList, JTextField textField1, JTextField textField2, JTextField textField3, ButtonGroup buttonGroup, JTextPane label) {
        this.waysList = waysList;
        this.payloadList = payloadList;
        this.textField1 = textField1;
        this.textField2 = textField2;
        this.textField3 = textField3;
        this.label = label;
        this.buttonGroup = buttonGroup;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        changeUtil();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        changeUtil();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        changeUtil();
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        changeUtil();
    }

    private void changeUtil() {
        ldapSearch = "";
        if (LdapServer.ldapBase == null) {
            // if the server is stopping
            String s = new StringBuilder("LDAP Search Link:   " + "Please start your server ...").toString();
            label.setText(s);
            return;
        }
        // base link
        StringBuilder builder = new StringBuilder("LDAP Search Link:   " + LdapServer.ldapBase);
        // way and payload
        builder.append((String) waysList.getSelectedValue() + "/").append((String) payloadList.getSelectedValue() + "/");
        if (buttonGroup != null) {
            ButtonModel selectedModel = buttonGroup.getSelection();
            if (selectedModel instanceof JRadioButton.ToggleButtonModel) {
                String selectedText = null;
                Enumeration<AbstractButton> buttons = buttonGroup.getElements();
                while (buttons.hasMoreElements()) {
                    AbstractButton button = buttons.nextElement();
                    if (button.isSelected()) {
                        selectedText = button.getText();
                        break;
                    }
                }
                // append optional text
                builder.append(selectedText + "/");
            } else if (selectedModel instanceof JToggleButton.ToggleButtonModel) {
                String selectedText = ((JToggleButton) selectedModel).getText();
                builder.append(selectedText + "/");
            }

        }
        if (textField1 != null) {
            builder.append(textField1.getText() + "/");
        }
        if (textField2 != null) {
            builder.append(textField2.getText() + "/");
        }
        if (textField3 != null) {
            builder.append(textField3.getText() + "/");
        }
        ldapSearch = builder.delete(builder.length() - 1, builder.length()).toString();

        label.setText(ldapSearch);
    }

    @Override
    public void register() {
        waysList.addListSelectionListener(this);
        payloadList.addListSelectionListener(this);
        if (buttonGroup != null) {
            for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {
                AbstractButton button = buttons.nextElement();
                button.addItemListener(this);
            }
        }
        if (textField1 != null) {
            textField1.getDocument().addDocumentListener(this);
        }
        if (textField2 != null) {
            textField2.getDocument().addDocumentListener(this);
        }
        if (textField3 != null) {
            textField3.getDocument().addDocumentListener(this);
        }
    }

    @Override
    public void unregister() {
        waysList.removeListSelectionListener(this);
        payloadList.removeListSelectionListener(this);
        if (textField1 != null) {
            textField1.getDocument().removeDocumentListener(this);
        }
        if (textField2 != null) {
            textField2.getDocument().removeDocumentListener(this);
        }
        if (textField3 != null) {
            textField3.getDocument().removeDocumentListener(this);
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            changeUtil();
        }
    }
}
