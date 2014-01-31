// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ConfigureDialog.java

package ca.etsmtl.log550.oscilloscope.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

public class ConfigureDialog extends JDialog
{

    public ConfigureDialog(Frame owner, boolean modal, Action connectAction, String ports[])
        throws HeadlessException
    {
        super(owner, modal);
        bauds = new JComboBox(new String[] {
            "110", "300", "1200", "2400", "4800", "9600", "19200", "38400", "57600", "115200", 
            "230400", "460800", "921600"
        });
        databits = new JComboBox(new String[] {
            "5", "6", "7", "8"
        });
        parity = new JComboBox(new String[] {
            "None", "Odd", "Even", "Mark", "Space"
        });
        stopbits = new JComboBox(new String[] {
            "1", "1.5", "2"
        });
        flowcontrol = new JComboBox(new String[] {
            "None", "CTS/RTS (Hardware)", "XON/XOFF (Software)"
        });
        this.connectAction = connectAction;
        port = new JComboBox(ports);
        createComponents();
        setTitle("Serial Communication Configuration");
        setDefaultCloseOperation(2);
        setResizable(false);
        pack();
    }

    private void createComponents()
    {
        ConfigureDialog dialog = this;
        GridLayout layoutCenter = new GridLayout(6, 1);
        GridLayout layoutLeft = new GridLayout(6, 1);
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel panelComponents = new JPanel(layoutCenter = new GridLayout(6, 1));
        JPanel panelLabels = new JPanel(layoutLeft = new GridLayout(6, 1));
        Box buttonBox = Box.createVerticalBox();
        layoutCenter.setVgap(10);
        layoutLeft.setVgap(10);
        mainPanel.add(panelComponents, "Center");
        mainPanel.add(panelLabels, "West");
        JPanel panelButton;
        mainPanel.add(panelButton = new JPanel(), "East");
        panelButton.add(buttonBox);
        dialog.setContentPane(mainPanel);
        mainPanel.setBorder(BorderFactory.createTitledBorder("Setup serial communication:"));
        bauds.setSelectedItem("57600");
        databits.setSelectedIndex(3);
        parity.setSelectedIndex(0);
        stopbits.setSelectedIndex(0);
        flowcontrol.setSelectedIndex(0);
        panelLabels.add(new JLabel("Port:"));
        panelLabels.add(new JLabel("Baud Rate:"));
        panelLabels.add(new JLabel("Data:"));
        panelLabels.add(new JLabel("Parity:"));
        panelLabels.add(new JLabel("Stop:"));
        panelLabels.add(new JLabel("Flow control:"));
        panelComponents.add(port);
        panelComponents.add(bauds);
        panelComponents.add(databits);
        panelComponents.add(parity);
        panelComponents.add(stopbits);
        panelComponents.add(flowcontrol);
        JButton connect = new JButton(connectAction);
        connect.setAlignmentX(0.5F);
        JButton close = new JButton(new AbstractAction() {

            public void actionPerformed(ActionEvent arg0)
            {
                dispose();
            }

            
            {
                putValue("Name", "Close");
            }
        });
        close.setAlignmentX(0.5F);
        buttonBox.add(Box.createVerticalStrut(25));
        buttonBox.add(connect);
        buttonBox.add(Box.createVerticalStrut(25));
        buttonBox.add(close);
    }

    public String getPortName()
    {
        return String.valueOf(port.getSelectedItem());
    }

    public int getBaudRate()
    {
        return Integer.parseInt(bauds.getSelectedItem().toString());
    }

    public int getDataBits()
    {
        return Integer.parseInt(databits.getSelectedItem().toString());
    }

    public int getParity()
    {
        return parity.getSelectedIndex();
    }

    public int getStopBits()
    {
        switch(stopbits.getSelectedIndex())
        {
        case 0: // '\0'
            return 1;

        case 1: // '\001'
            return 3;

        case 2: // '\002'
            return 2;
        }
        throw new IllegalArgumentException("Invalid Stop Bits");
    }

    public int getFlowControl()
    {
        switch(flowcontrol.getSelectedIndex())
        {
        case 0: // '\0'
            return 0;

        case 1: // '\001'
            return 3;

        case 2: // '\002'
            return 12;
        }
        throw new IllegalArgumentException("Invalid Flow Control");
    }

    private JComboBox port;
    private JComboBox bauds;
    private JComboBox databits;
    private JComboBox parity;
    private JComboBox stopbits;
    private JComboBox flowcontrol;
    private final Action connectAction;
    private final String sPort = "Port:";
    private final String sBaudRate = "Baud Rate:";
    private final String sData = "Data:";
    private final String sParity = "Parity:";
    private final String sStop = "Stop:";
    private final String sFlowControl = "Flow control:";
}
