// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   InputStreamCopier.java

package ca.etsmtl.log550.oscilloscope.sampling;

import java.io.*;

public class InputStreamCopier extends FilterInputStream
{

    public InputStreamCopier(InputStream in)
    {
        super(in);
        try
        {
            pipedOutputStream = new PipedOutputStream();
            pipedInputStream = new PipedInputStream(pipedOutputStream);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public InputStream getStreamCopy()
    {
        return pipedInputStream;
    }

    public int read()
        throws IOException
    {
        int value = super.read();
        if(value >= 0)
            pipedOutputStream.write(value);
        return value;
    }

    public int read(byte b[])
        throws IOException
    {
        int byteCount = super.read(b);
        if(byteCount != 0)
            pipedOutputStream.write(b, 0, byteCount);
        return byteCount;
    }

    public int read(byte b[], int off, int len)
        throws IOException
    {
        int byteCount = super.read(b, off, len);
        if(byteCount != 0)
            pipedOutputStream.write(b, off, byteCount);
        return byteCount;
    }

    public void close()
        throws IOException
    {
        super.close();
        pipedOutputStream.close();
    }

    private PipedInputStream pipedInputStream;
    private PipedOutputStream pipedOutputStream;
}
