// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   WaveSignal.java

package ca.etsmtl.log550.oscilloscope;


// Referenced classes of package ca.etsmtl.log550.oscilloscope:
//            SignalModel

public class WaveSignal
    implements SignalModel
{

    public WaveSignal()
    {
        A = 200F;
        w = 6.283185F;
        k = 0.006283185F;
        p = 0.0F;
        timeStart = System.currentTimeMillis();
    }

    public int getValuesCount()
    {
        return 1000;
    }

    public float getMaximumValue()
    {
        return A;
    }

    public float getMinimumValue()
    {
        return -A;
    }

    public float getValue(int x)
    {
        float t = (float)(System.currentTimeMillis() - timeStart) / 1000F;
        float y = A * (float)Math.cos((w * t - k * (float)x) + p);
        return y;
    }

    public float[] getValues(int offset, int length)
    {
        float values[] = new float[length];
        for(int i = offset; i < offset + length; i++)
            values[i - offset] = getValue(offset);

        return values;
    }

    public int getSamplingRate()
    {
        return 0;
    }

    private float A;
    private float w;
    private float k;
    private float p;
    private long timeStart;
}
