// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   OutputStreamCopier.java

package ca.etsmtl.log550.oscilloscope.sampling;

import java.io.*;

public class OutputStreamCopier extends FilterOutputStream
{

    public OutputStreamCopier(OutputStream out)
    {
        super(out);
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

    public void write(byte b[])
        throws IOException
    {
        super.write(b);
        pipedOutputStream.write(b, 0, b.length);
    }

    public void write(byte b[], int off, int len)
        throws IOException
    {
        super.write(b, off, len);
        pipedOutputStream.write(b, off, len);
    }

    public void write(int b)
        throws IOException
    {
        super.write(b);
        pipedOutputStream.write(b);
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
