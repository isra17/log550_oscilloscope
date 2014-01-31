// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3)
// Source File Name:   Sampler.java

package ca.etsmtl.log550.oscilloscope.sampling;

import gnu.io.*;
import java.io.*;
import java.util.*;

// Referenced classes of package ca.etsmtl.log550.oscilloscope.sampling:
//            ChannelListener

public class Sampler
{
    private static interface SamplerReader
    {

        public abstract void load();

        public abstract void unload();
    }

    private class EventDrivenSamplerReader
        implements SamplerReader, SerialPortEventListener
    {

        public void load()
        {
            port.notifyOnDataAvailable(true);
            try
            {
                port.addEventListener(this);
            }
            catch(TooManyListenersException e)
            {
                e.printStackTrace();
            }
        }

        public void unload()
        {
            port.notifyOnDataAvailable(false);
            port.removeEventListener();
        }

        public void serialEvent(SerialPortEvent event)
        {
            switch(event.getEventType())
            {
            case 1: // '\001'
                try
                {
                    for(int length = 0; (length = in.read(buffer)) != 0;)
                        handleData(buffer, 0, length);

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

        byte buffer[];

        EventDrivenSamplerReader()
        {
            buffer = new byte[64];
        }
    }

    private class ThreadedDrivenSamplerReader
        implements SamplerReader, Runnable
    {

        public void load()
        {
            readerThread = new Thread(this);
            readerThread.setName("ThreadedDrivenSamplerReader");
            readerThread.start();
        }

        public void unload()
        {
            readerThread.interrupt();
            try
            {
                readerThread.join();
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
            readerThread = null;
        }

        public void run()
        {
            System.out.println(".run()-->Start");
            byte buffer[] = new byte[64];
            int length = 0;
            try
            {
                while((length = in.read(buffer)) > -1 && !readerThread.isInterrupted())
                    handleData(buffer, 0, length);
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
            System.out.println(".run()-->Stop");
        }

        Thread readerThread;

        ThreadedDrivenSamplerReader()
        {
        }
    }


    public static String[] getSerialPorts()
    {
        ArrayList list = new ArrayList();
        for(Enumeration portIdentifiers = CommPortIdentifier.getPortIdentifiers(); portIdentifiers.hasMoreElements();)
        {
            CommPortIdentifier pid = (CommPortIdentifier)portIdentifiers.nextElement();
            if(pid.getPortType() == 1)
                list.add(pid.getName());
        }

        String ports[] = new String[list.size()];
        return (String[])list.toArray(ports);
    }

    public Sampler(int readerType)
    {
        channelListeners = new ChannelListener[2];
        switch(readerType)
        {
        case 1: // '\001'
            reader = new EventDrivenSamplerReader();
            break;

        case 2: // '\002'
            reader = new ThreadedDrivenSamplerReader();
            break;

        default:
            throw new IllegalArgumentException("Unknown Reader Type");
        }
    }

    public InputStream getInputStream()
    {
        return in;
    }

    public OutputStream getOutputStream()
    {
        return out;
    }

    public synchronized void connect(String portName, int baudrate, int dataBits, int stopBits, int parity)
    {
        if(connected)
            throw new RuntimeException("Already Connected");
        try
        {
            CommPortIdentifier identifier = CommPortIdentifier.getPortIdentifier(portName);
            port = (SerialPort)identifier.open(getClass().getName(), 1000);
            port.setSerialPortParams(baudrate, dataBits, stopBits, parity);
            port.setFlowControlMode(0);
            in = port.getInputStream();
            out = port.getOutputStream();
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
        catch(IOException e)
        {
            e.printStackTrace();
        }
        reader.load();
        connected = true;
    }

    public synchronized void close()
    {
        if(!connected)
            throw new RuntimeException("Not Connected");
        try
        {
            in.close();
            out.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        reader.unload();
        port.close();
        in = null;
        out = null;
        port = null;
        connected = false;
    }

    public synchronized void start()
    {
        if(!connected)
            throw new RuntimeException("Not Connected");
        if(started)
            throw new RuntimeException("Already Started");
        try
        {
            out.write(115);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        started = true;
    }

    public synchronized void stop()
    {
        if(!connected)
            throw new RuntimeException("Not Connected");
        if(!started)
            throw new RuntimeException("Not Started");
        try
        {
            out.write(120);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        started = false;
    }

    protected void handleData(byte data[], int offset, int length)
    {
        for(int i = offset; i < length; i++)
        {
            int value = data[i] & 0xFF;
            int channel = value & 1;
            value &= -2;
            value <<= 2;
            sendValueToChannel(channel, (float)value / 1023F);
        }

    }

    protected void sendValueToChannel(int channel, float value)
    {
        ChannelListener listener = channelListeners[channel];
        if(listener != null)
            listener.sampleReceived(value);
    }

    public void setChannelListener(int channel, ChannelListener channelListener)
    {
        channelListeners[channel] = channelListener;
    }

    public static final int EVENT_READER = 1;
    public static final int THREAD_READER = 2;
    private static final int CHANNELS = 2;
    private static final float DIVIDER = 1023F;
    private ChannelListener channelListeners[];
    private SerialPort port;
    private InputStream in;
    private OutputStream out;
    private boolean connected;
    private boolean started;
    private SamplerReader reader;


}
