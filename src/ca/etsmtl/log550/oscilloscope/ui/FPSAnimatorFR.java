// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   FPSAnimatorFR.java

package ca.etsmtl.log550.oscilloscope.ui;

import com.sun.opengl.util.Animator;
import java.util.Timer;
import java.util.TimerTask;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLException;

public class FPSAnimatorFR extends Animator
{

    public FPSAnimatorFR(int fps)
    {
        this(null, fps);
    }

    public FPSAnimatorFR(GLAutoDrawable drawable, int fps)
    {
        this.fps = fps;
        if(drawable != null)
            add(drawable);
    }

    public synchronized void start()
    {
        if(timer != null)
        {
            throw new GLException("Already started");
        } else
        {
            long delay = (long)(1000F / (float)fps);
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {

                public void run()
                {
                    display();
                }

            }, 0L, delay);
            return;
        }
    }

    public synchronized boolean isAnimating()
    {
        return timer != null;
    }

    public synchronized void stop()
    {
        if(timer == null)
        {
            throw new GLException("Already stopped");
        } else
        {
            timer.cancel();
            timer = null;
            return;
        }
    }

    private Timer timer;
    private int fps;

}
