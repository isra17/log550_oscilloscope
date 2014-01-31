// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   GLSignalView.java

package ca.etsmtl.log550.oscilloscope.ui;

import ca.etsmtl.log550.oscilloscope.SignalModel;
import com.sun.opengl.util.*;
import java.awt.*;
import java.io.PrintStream;
import javax.media.opengl.*;
import javax.swing.BorderFactory;
import javax.swing.JComponent;

// Referenced classes of package ca.etsmtl.log550.oscilloscope.ui:
//            FPSAnimatorFR

public class GLSignalView extends JComponent
    implements GLEventListener
{

    public GLSignalView(SignalModel model, int samplesShown, int canvasType, int animatorType)
    {
        glut = new GLUT();
        this.model = model;
        this.samplesShown = samplesShown;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(0, 0));
        setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
        switch(canvasType)
        {
        case 1: // '\001'
            canvas = new GLCanvas();
            break;

        case 2: // '\002'
            canvas = new GLJPanel();
            break;

        default:
            throw new IllegalArgumentException("Unknown Canvas Type");
        }
        canvas.addGLEventListener(this);
        add((Component)canvas);
        if((animatorType & 0x80) != 0)
        {
            if(sharedAnimator == null)
            {
                animatorType &= 0xffffff7f;
                sharedAnimator = createAnimator(animatorType);
            }
            sharedAnimator.add(canvas);
            animator = sharedAnimator;
        } else
        {
            animator = createAnimator(animatorType);
        }
    }

    private Animator createAnimator(int animatorType)
    {
        Animator animator;
        switch(animatorType)
        {
        case 0: // '\0'
            animator = null;
            break;

        case 1: // '\001'
            animator = new Animator(canvas);
            break;

        case 2: // '\002'
            animator = new FPSAnimator(canvas, 35, false);
            break;

        case 4: // '\004'
            animator = new FPSAnimatorFR(canvas, 35);
            break;

        case 3: // '\003'
        default:
            throw new IllegalArgumentException("Unknown Animator Type");
        }
        return animator;
    }

    public void startAnimation()
    {
        if(animator != null && !animator.isAnimating())
            animator.start();
        timerStart = System.currentTimeMillis();
        drawCount = 0L;
    }

    public void stopAnimation()
    {
        if(animator != null && animator.isAnimating())
            animator.stop();
        System.out.println((1000F * (float)drawCount) / (float)(System.currentTimeMillis() - timerStart) + " FPS");
    }

    public void init(GLAutoDrawable drawable)
    {
        drawable.setGL(new DebugGL(drawable.getGL()));
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
    {
        GL gl = drawable.getGL();
        gl.glMatrixMode(5889);
        gl.glLoadIdentity();
        gl.glMatrixMode(5888);
        gl.glLoadIdentity();
        float yscale = 2.0F / (model.getMaximumValue() - model.getMinimumValue());
        yscale = (float)((double)yscale * 0.94999999999999996D);
        gl.glScalef(1.0F, yscale, 1.0F);
        float ycenter = -(model.getMaximumValue() - (model.getMaximumValue() - model.getMinimumValue()) / 2.0F);
        gl.glTranslatef(0.0F, ycenter, 0.0F);
        float xscale = 2.0F / (float)samplesShown;
        gl.glScalef(xscale, 1.0F, 1.0F);
        float xstart = (float)(-samplesShown) / 2.0F;
        gl.glTranslatef(xstart, 0.0F, 0.0F);
    }

    public void display(GLAutoDrawable drawable)
    {
        drawCount++;
        GL gl = drawable.getGL();
        gl.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
        if((drawable instanceof GLJPanel) && !((GLJPanel)drawable).isOpaque() && ((GLJPanel)drawable).shouldPreserveColorBufferIfTranslucent())
            gl.glClear(256);
        else
            gl.glClear(16640);
        gl.glPushMatrix();
        drawSignal(gl);
        gl.glPopMatrix();
        gl.glPushMatrix();
        gl.glColor3f(0.0F, 1.0F, 0.0F);
        String samplingRate = String.valueOf(model.getSamplingRate());
        int width = glut.glutBitmapLength(8, samplingRate);
        gl.glRasterPos2f(samplesShown - 2 * width, model.getMaximumValue() * 0.95F);
        glut.glutBitmapString(8, samplingRate);
        gl.glPopMatrix();
        gl.glFlush();
    }

    protected void drawSignal(GL gl)
    {
        int xmin = Math.max(0, model.getValuesCount() - samplesShown);
        int xlength = Math.min(samplesShown, model.getValuesCount());
        float values[] = model.getValues(xmin, xlength);
        gl.glColor3f(0.0F, 1.0F, 0.0F);
        gl.glBegin(0);
        for(int x = 0; x < xlength; x++)
            gl.glVertex2f(x, values[x]);

        gl.glEnd();
    }

    public void displayChanged(GLAutoDrawable glautodrawable, boolean flag, boolean flag1)
    {
    }

    public static final int AWT_CANVAS = 1;
    public static final int SWING_CANVAS = 2;
    public static final int NO_ANIMATOR = 0;
    public static final int CONTINIOUS_ANIMATOR = 1;
    public static final int FPS_ANIMATOR = 2;
    public static final int FPS_ANIMATOR_FIXED_RATE = 4;
    public static final int SHARED_ANIMATOR = 128;
    private static Animator sharedAnimator;
    protected final GLAutoDrawable canvas;
    protected final Animator animator;
    protected final SignalModel model;
    protected final int samplesShown;
    protected GLUT glut;
    long timerStart;
    long drawCount;
}
