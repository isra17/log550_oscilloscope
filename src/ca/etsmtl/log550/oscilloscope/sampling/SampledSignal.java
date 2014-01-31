// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SampledSignal.java

package ca.etsmtl.log550.oscilloscope.sampling;

import ca.etsmtl.log550.oscilloscope.SignalModel;

// Referenced classes of package ca.etsmtl.log550.oscilloscope.sampling:
//            ChannelListener

public class SampledSignal
    implements SignalModel, ChannelListener
{

    public SampledSignal(float minimumValue, float maximumValue, int bufferSize)
    {
        currentPosition = 0;
        this.maximumValue = maximumValue;
        this.minimumValue = minimumValue;
        buffer = new float[bufferSize];
        lastComputation = System.nanoTime();
    }

    public int getValuesCount()
    {
        return buffer.length;
    }

    public float getMaximumValue()
    {
        return maximumValue;
    }

    public float getMinimumValue()
    {
        return minimumValue;
    }

    public synchronized float getValue(int x)
    {
        return buffer[(x + currentPosition) % buffer.length];
    }

    public synchronized float[] getValues(int offset, int length)
    {
        float values[] = new float[length];
        int length1 = Math.min(buffer.length - currentPosition, length);
        System.arraycopy(buffer, currentPosition, values, 0, length1);
        if(length1 < length)
            System.arraycopy(buffer, 0, values, length1, Math.min(currentPosition, length - length1));
        return values;
    }

    public synchronized void sampleReceived(float value)
    {
        buffer[currentPosition] = (maximumValue - minimumValue) * value + minimumValue;
        currentPosition = (currentPosition + 1) % buffer.length;
        samplesReceived++;
        long now = System.nanoTime();
        if((float)(now - lastComputation) >= 1E+09F)
        {
            float bruteSamplingRate = (1E+09F * (float)samplesReceived) / (float)(now - lastComputation);
            samplingRate = 0.0F * samplingRate + 1.0F * bruteSamplingRate;
            lastComputation = now;
            samplesReceived = 0;
        }
    }

    public int getSamplingRate()
    {
        return (int)samplingRate;
    }

    private final float buffer[];
    private int currentPosition;
    private final float maximumValue;
    private final float minimumValue;
    private long lastComputation;
    private int samplesReceived;
    private float samplingRate;
}
