// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   StreamPanel.java

package ca.etsmtl.log550.oscilloscope.ui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

public class StreamPanel extends JPanel
    implements KeyListener
{
    private final class ReaderThread extends Thread
    {

        public void run()
        {
            do
            {
                while(inputStream == null) 
                    try
                    {
                        synchronized(this)
                        {
                            wait();
                        }
                    }
                    catch(InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                byte buffer[] = new byte[128];
                try
                {
                    StringBuffer stringBuffer = new StringBuffer();
                    int length;
                    while(inputStream != null && (length = inputStream.read(buffer)) > 0) ;
                    if(inputStream != null)
                    {
                        inputStream.close();
                        inputStream = null;
                    }
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
            } while(true);
        }

        ReaderThread(String name)
        {
            super(name);
            setDaemon(true);
        }
    }


    public StreamPanel(String title)
    {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(500, 150));
        awtTextArea = new TextArea(null, 0, 0, 1);
        awtTextArea.setBackground(Color.WHITE);
        awtTextArea.setEditable(false);
        add(awtTextArea);
        JPanel topPanel = new JPanel(new BorderLayout());
        add(topPanel, "North");
        checkbox = new JCheckBox("Hex Format");
        checkbox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                awtTextArea.setText(null);
            }

        });
        topPanel.add(checkbox, "East");
        topPanel.add(new JLabel(title), "West");
        readerThread = new ReaderThread("StreamPanel");
        readerThread.start();
    }

    public InputStream getInputStream()
    {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream)
    {
        this.inputStream = inputStream;
        if(inputStream != null)
            synchronized(readerThread)
            {
                readerThread.notifyAll();
            }
    }

    public OutputStream getOutputStream()
    {
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream)
    {
        this.outputStream = outputStream;
        if(outputStream != null)
            awtTextArea.addKeyListener(this);
        else
            awtTextArea.removeKeyListener(this);
    }

    public void keyTyped(KeyEvent e)
    {
        try
        {
            outputStream.write(e.getKeyChar());
            outputStream.flush();
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
        e.consume();
    }

    public void keyPressed(KeyEvent keyevent)
    {
    }

    public void keyReleased(KeyEvent keyevent)
    {
    }

    private TextArea awtTextArea;
    private InputStream inputStream;
    private JCheckBox checkbox;
    private Thread readerThread;
    private OutputStream outputStream;



}
