// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SignalModel.java

package ca.etsmtl.log550.oscilloscope;


public interface SignalModel
{

    public abstract int getValuesCount();

    public abstract float getMaximumValue();

    public abstract float getMinimumValue();

    public abstract float getValue(int i);

    public abstract float[] getValues(int i, int j);

    public abstract int getSamplingRate();
}
