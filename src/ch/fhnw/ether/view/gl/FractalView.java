/*
 * Copyright (c) 2013 - 2015 Stefan Muller Arisona, Simon Schubiger, Samuel von Stachelski
 * Copyright (c) 2013 - 2015 FHNW & ETH Zurich
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *  Neither the name of FHNW / ETH Zurich nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package ch.fhnw.ether.view.gl;

import ch.fhnw.comgr.fractal.FractalViewer;
import ch.fhnw.ether.controller.IController;
import ch.fhnw.ether.controller.event.IEvent;
import ch.fhnw.ether.controller.event.IEventScheduler.IAction;
import ch.fhnw.ether.controller.event.IKeyEvent;
import ch.fhnw.ether.controller.event.IPointerEvent;
import ch.fhnw.ether.view.IView;
import ch.fhnw.ether.view.IWindow;
import ch.fhnw.util.Viewport;
import com.jogamp.nativewindow.util.Point;
import com.jogamp.newt.event.*;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

/**
 * Default view class that implements some basic functionality. Use as base for
 * more complex implementations.
 *
 * @author radar
 */
public class FractalView implements IView {

    private final Config viewConfig;

    private final IController controller;

    private NEWTWindow window;

    private volatile Viewport viewport = new Viewport(0, 0, 1, 1);

    private boolean enabled = true;

    private FractalViewer viewer;

    public FractalView(IController controller, int x, int y, int w, int h, Config viewConfig, String title) {
        this.controller = controller;
        this.viewConfig = viewConfig;

        window = new NEWTWindow(w, h, title, viewConfig);
        window.getWindow().addGLEventListener(glEventListener);
        window.getWindow().addWindowListener(windowListener);
        window.getWindow().addMouseListener(mouseListener);
        window.getWindow().addKeyListener(keyListener);

        Point p = window.getPosition();
        if (x != -1)
            p.setX(x);
        if (y != -1)
            p.setY(y);
        window.setPosition(p.getX(), p.getY());

        // note: the order here is quite important. the view starts sending
        // events after setVisible(), and we're still in the view's constructor.
        // need to see if this doesn't get us into trouble in the long run.
        controller.viewCreated(this);
        window.setVisible(true);
    }

    public void setViewer(FractalViewer viewer) {
        this.viewer = viewer;
    }

    @Override
    public void dispose() {
        // the gl event listener below will deal with disposing
        window.dispose();
    }

    @Override
    public final boolean isEnabled() {
        return enabled;
    }

    @Override
    public final void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public Config getConfig() {
        return viewConfig;
    }

    @Override
    public final IController getController() {
        return controller;
    }

    @Override
    public final Viewport getViewport() {
        return viewport;
    }

    @Override
    public IWindow getWindow() {
        return window;
    }

    @Override
    public String toString() {
        return "[view " + hashCode() + "]";
    }

    private void runOnSceneThread(IAction action) {
        controller.run(action);
    }

    // GLEventListener implementation

    private GLEventListener glEventListener = new GLEventListener() {
        @Override
        public final void init(GLAutoDrawable drawable) {
            try {
                GL gl = drawable.getGL();

                // FIXME: need to make this configurable and move to renderer
                gl.glClearColor(0.1f, 0.2f, 0.3f, 1.0f);
                gl.glClearDepth(1.0f);

                if (viewConfig.has(ViewFlag.SMOOTH_LINES)) {
                    gl.glEnable(GL.GL_LINE_SMOOTH);
                    gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
                }

                gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public final void display(GLAutoDrawable drawable) {
            drawable.getGL().glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT);
        }

        @Override
        public final void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
            try {
                GL gl = drawable.getGL();
                height = Math.max(1,  height);
                gl.glViewport(0, 0, width, height);
                viewport = new Viewport(0, 0, width, height);
                if (viewer != null) {
                    viewer.reshape(viewport);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public final void dispose(GLAutoDrawable drawable) {
            runOnSceneThread(time -> {
                controller.viewDisposed(FractalView.this);
                window = null;
            });
        }
    };

    // window listener

    private WindowListener windowListener = new WindowAdapter() {
        @Override
        public void windowGainedFocus(WindowEvent e) {
            runOnSceneThread(time -> controller.viewGainedFocus(FractalView.this));
        }

        @Override
        public void windowLostFocus(WindowEvent e) {
            runOnSceneThread(time -> controller.viewLostFocus(FractalView.this));
        }

        @Override
        public void windowResized(WindowEvent e) {
            runOnSceneThread(time -> controller.viewChanged(FractalView.this));
        }
    };

    // key listener
    private class ViewKeyEvent implements IKeyEvent {
        final int modifiers;
        final short keySym;
        final short keyCode;
        final char keyChar;
        final boolean isAutoRepeat;

        ViewKeyEvent(KeyEvent e) {
            modifiers = e.getModifiers() & IEvent.MODIFIER_MASK;
            keySym = e.getKeySymbol();
            keyCode = e.getKeyCode();
            keyChar = e.getKeyChar();
            isAutoRepeat = e.isAutoRepeat();
        }

        @Override
        public IView getView() {
            return FractalView.this;
        }

        @Override
        public int getModifiers() {
            return modifiers;
        }

        @Override
        public short getKeySym() {
            return keySym;
        }

        @Override
        public short getKeyCode() {
            return keyCode;
        }

        @Override
        public char getKeyChar() {
            return keyChar;
        }

        @Override
        public boolean isAutoRepeat() {
            return isAutoRepeat;
        }
    }

    private KeyListener keyListener = new KeyListener() {
        @Override
        public void keyPressed(KeyEvent e) {
            runOnSceneThread(time -> controller.keyPressed(new ViewKeyEvent(e)));
        }

        @Override
        public void keyReleased(KeyEvent e) {
            runOnSceneThread(time -> controller.keyReleased(new ViewKeyEvent(e)));
        }
    };

    // mouse listener

    private class ViewPointerEvent implements IPointerEvent {
        final int modifiers;
        final int button;
        final int clickCount;
        final int x;
        final int y;
        final float scrollX;
        final float scrollY;

        ViewPointerEvent(MouseEvent e) {
            modifiers = e.getModifiers() & IEvent.MODIFIER_MASK;
            button = e.getButton();
            clickCount = e.getClickCount();
            x = e.getX();
            y = getViewport().h - e.getY();
            if (e.getPointerCount() > 0) {
                scrollX = e.getRotationScale() * e.getRotation()[0];
                scrollY = -e.getRotationScale() * e.getRotation()[1];
            } else {
                scrollX = 0;
                scrollY = 0;
            }
        }

        @Override
        public IView getView() {
            return FractalView.this;
        }

        @Override
        public int getModifiers() {
            return modifiers;
        }

        @Override
        public int getButton() {
            return button;
        }

        @Override
        public int getClickCount() {
            return clickCount;
        }

        @Override
        public int getX() {
            return x;
        }

        @Override
        public int getY() {
            return y;
        }

        @Override
        public float getScrollX() {
            return scrollX;
        }

        @Override
        public float getScrollY() {
            return scrollY;
        }
    }

    private MouseListener mouseListener = new MouseListener() {
        @Override
        public void mouseEntered(MouseEvent e) {
            runOnSceneThread(time -> controller.pointerEntered(new ViewPointerEvent(e)));
        }

        @Override
        public void mouseExited(MouseEvent e) {
            runOnSceneThread(time -> controller.pointerExited(new ViewPointerEvent(e)));
        }

        @Override
        public void mousePressed(MouseEvent e) {
            window.requestFocus();
            runOnSceneThread(time -> controller.pointerPressed(new ViewPointerEvent(e)));
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            runOnSceneThread(time -> controller.pointerReleased(new ViewPointerEvent(e)));
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            runOnSceneThread(time -> controller.pointerClicked(new ViewPointerEvent(e)));
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            runOnSceneThread(time -> controller.pointerMoved(new ViewPointerEvent(e)));
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            runOnSceneThread(time -> controller.pointerDragged(new ViewPointerEvent(e)));
        }

        @Override
        public void mouseWheelMoved(MouseEvent e) {
            runOnSceneThread(time -> controller.pointerScrolled(new ViewPointerEvent(e)));
        }
    };
}
