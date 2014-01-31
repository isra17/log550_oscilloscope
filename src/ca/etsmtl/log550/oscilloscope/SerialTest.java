// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SerialTest.java

package ca.etsmtl.log550.oscilloscope;

import ca.etsmtl.log550.oscilloscope.sampling.Sampler;
import ca.etsmtl.log550.oscilloscope.ui.ConfigureDialog;
import gnu.io.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.TooManyListenersException;
import javax.swing.AbstractAction;
import javax.swing.Action;

public class SerialTest
{

    public SerialTest()
    {
        actionConnect = new AbstractAction() {

            public void actionPerformed(ActionEvent e)
            {
                connect(configureDialog.getPortName(), configureDialog.getBaudRate(), configureDialog.getDataBits(), configureDialog.getStopBits(), configureDialog.getParity());
                configureDialog.setVisible(false);
            }

            
            {
                putValue("Name", "Connect");
            }
        };
    }

    public static void main(String args[])
    {
        SerialTest test = new SerialTest();
        test.sepratedThread = true;
        test.configure();
        test.read();
    }

    private void configure()
    {
        System.out.println("SerialTest.configure()");
        configureDialog = new ConfigureDialog(null, true, actionConnect, Sampler.getSerialPorts());
        configureDialog.setVisible(true);
    }

    public synchronized void connect(String portName, int baudrate, int dataBits, int stopBits, int parity)
    {
        System.out.println("SerialTest.connect()");
        try
        {
            CommPortIdentifier identifier = CommPortIdentifier.getPortIdentifier(portName);
            SerialPort port = (SerialPort)identifier.open(getClass().getName(), 1000);
            port.setSerialPortParams(baudrate, dataBits, stopBits, parity);
            port.setFlowControlMode(0);
            port.notifyOnDataAvailable(true);
            in = port.getInputStream();
            out = port.getOutputStream();
            if(sepratedThread)
            {
                port.disableReceiveThreshold();
                port.disableReceiveTimeout();
                System.out.println("isReceiveFramingEnabled=" + port.isReceiveFramingEnabled());
                System.out.println("isReceiveThresholdEnabled=" + port.isReceiveThresholdEnabled());
                System.out.println("isReceiveTimeoutEnabled=" + port.isReceiveTimeoutEnabled());
                Thread thread = new Thread() {

                    public void run()
                    {
                        System.out.println(".run()-->Start");
                        byte buffer[] = new byte[64];
                        int length = 0;
                        try
                        {
                            while((length = in.read(buffer)) > -1) 
                            {
                                System.out.write(buffer, 0, length);
                                System.out.flush();
                            }
                        }
                        catch(IOException e)
                        {
                            e.printStackTrace();
                        }
                        System.out.println(".run()-->Stop");
                    }

                };
                thread.start();
            } else
            {
                port.addEventListener(new SerialPortEventListener() {

                    public void serialEvent(SerialPortEvent event)
                    {
                        byte buffer[] = new byte[64];
                        switch(event.getEventType())
                        {
                        case 1: // '\001'
                            try
                            {
                                for(int length = 0; (length = in.read(buffer)) != 0;)
                                {
                                    System.out.write(buffer, 0, length);
                                    System.out.flush();
                                }

                            }
                            catch(IOException e)
                            {
                                e.printStackTrace();
                            }
                            break;

                        default:
                            System.err.println("UNKNOWN SERIAL EVENT");
                            break;
                        }
                    }

                });
            }
        }
        catch(NoSuchPortException e)
        {
            e.printStackTrace();
        }
        catch(PortInUseException e)
        {
            e.printStackTrace();
        }
        catch(UnsupportedCommOperationException e)
        {
            e.printStackTrace();
        }
        catch(TooManyListenersException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    private void read()
    {
        System.out.println("SerialTest.read()");
        byte buffer[] = new byte[64];
        int length = 0;
        try
        {
            while((length = System.in.read(buffer)) != 0) 
            {
                out.write(buffer, 0, length);
                out.flush();
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    private ConfigureDialog configureDialog;
    private InputStream in;
    private OutputStream out;
    private boolean sepratedThread;
    private Action actionConnect;


}
