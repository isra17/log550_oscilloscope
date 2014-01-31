// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Oscilloscope.java

package ca.etsmtl.log550.oscilloscope;

import ca.etsmtl.log550.oscilloscope.sampling.SampledSignal;
import ca.etsmtl.log550.oscilloscope.sampling.Sampler;
import ca.etsmtl.log550.oscilloscope.ui.ConfigureDialog;
import ca.etsmtl.log550.oscilloscope.ui.GLSignalView;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// Referenced classes of package ca.etsmtl.log550.oscilloscope:
//            SignalModel

public class Oscilloscope extends JFrame
{

    public static void main(String args[])
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(ClassNotFoundException classnotfoundexception) { }
        catch(InstantiationException instantiationexception) { }
        catch(IllegalAccessException illegalaccessexception) { }
        catch(UnsupportedLookAndFeelException unsupportedlookandfeelexception) { }
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        System.setProperty("sun.java2d.noddraw", "true");
        System.setProperty("sun.java2d.opengl", "true");
        Oscilloscope frame = new Oscilloscope(1000);
        frame.setDefaultCloseOperation(2);
        frame.pack();
        frame.setSize(500, 500);
        frame.setVisible(true);
    }

    public Oscilloscope(int samplesPerChannel)
    {
        super("Oscilloscope");
        actionConfigure = new AbstractAction() {

            public void actionPerformed(ActionEvent e)
            {
                configureDialog.setLocationRelativeTo(Oscilloscope.this);
                configureDialog.setVisible(true);
            }

            
            {
                putValue("Name", "Connect...");
            }
        };
        actionConnect = new AbstractAction() {

            public void actionPerformed(ActionEvent e)
            {
                if(connected)
                    disconnect();
                connect();
                configureDialog.setVisible(false);
            }

            
            {
                putValue("Name", "Connect");
            }
        };
        actionDisconnect = new AbstractAction() {

            public void actionPerformed(ActionEvent e)
            {
                if(started)
                    stop();
                if(connected)
                    disconnect();
            }

            
            {
                putValue("Name", "Disconnect");
                setEnabled(false);
            }
        };
        actionStart = new AbstractAction() {

            public void actionPerformed(ActionEvent e)
            {
                start();
            }

            
            {
                putValue("Name", "Start Acquisition");
                setEnabled(false);
            }
        };
        actionStop = new AbstractAction() {

            public void actionPerformed(ActionEvent e)
            {
                stop();
            }

            
            {
                putValue("Name", "Stop Acquisition");
                setEnabled(false);
            }
        };
        actionExit = new AbstractAction() {

            public void actionPerformed(ActionEvent e)
            {
                dispose();
            }

            
            {
                putValue("Name", "Exit");
            }
        };
        this.samplesPerChannel = samplesPerChannel;
        addWindowListener(new WindowAdapter() {

            public void windowClosed(WindowEvent e)
            {
                if(started)
                    stop();
                if(connected)
                    disconnect();
            }

        });
        sampler = new Sampler(1);
        models = new SignalModel[2];
        for(int i = 0; i < models.length; i++)
        {
            models[i] = new SampledSignal(0.0F, 5F, samplesPerChannel);
            sampler.setChannelListener(i, (SampledSignal)models[i]);
        }

        createComponents();
        setJMenuBar(createMenu());
    }

    private JMenuBar createMenu()
    {
        JMenuBar menubar = new JMenuBar();
        JMenu menu = new JMenu("Sampler");
        menubar.add(menu);
        menu.add(actionConfigure);
        menu.add(actionDisconnect);
        menu.addSeparator();
        menu.add(actionStart);
        menu.add(actionStop);
        menu.addSeparator();
        menu.add(actionExit);
        return menubar;
    }

    private void createComponents()
    {
        configureDialog = new ConfigureDialog(this, true, actionConnect, Sampler.getSerialPorts());
        Panel mainPanel = new Panel();
        mainPanel.setLayout(new BorderLayout());
        Panel signalPanel = new Panel();
        signalPanel.setLayout(new BoxLayout(signalPanel, 3));
        signalViews = new GLSignalView[models.length];
        for(int i = 0; i < signalViews.length; i++)
        {
            signalViews[i] = new GLSignalView(models[i], samplesPerChannel, 2, 130);
            signalPanel.add(signalViews[i]);
        }

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        mainPanel.add(signalPanel, "Center");
        contentPane.add(mainPanel);
    }

    public void connect()
    {
        sampler.connect(configureDialog.getPortName(), configureDialog.getBaudRate(), configureDialog.getDataBits(), configureDialog.getStopBits(), configureDialog.getParity());
        connected = true;
        actionDisconnect.setEnabled(true);
        actionStart.setEnabled(true);
    }

    public void disconnect()
    {
        sampler.close();
        connected = false;
        actionDisconnect.setEnabled(false);
        actionStart.setEnabled(false);
        actionStop.setEnabled(false);
    }

    public void start()
    {
        for(int i = 0; i < signalViews.length; i++)
            signalViews[i].startAnimation();

        sampler.start();
        started = true;
        actionStart.setEnabled(false);
        actionStop.setEnabled(true);
    }

    public void stop()
    {
        if(!started)
            return;
        sampler.stop();
        for(int i = 0; i < signalViews.length; i++)
            signalViews[i].stopAnimation();

        started = false;
        actionStart.setEnabled(true);
        actionStop.setEnabled(false);
    }

    protected GLSignalView signalViews[];
    protected SignalModel models[];
    protected Sampler sampler;
    private final int samplesPerChannel;
    private boolean connected;
    private boolean started;
    protected ConfigureDialog configureDialog;
    protected Action actionConfigure;
    protected Action actionConnect;
    protected Action actionDisconnect;
    protected Action actionStart;
    protected Action actionStop;
    protected Action actionExit;


}
