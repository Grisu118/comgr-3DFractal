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

package ch.fhnw.ether.controller;

import ch.fhnw.ether.controller.event.DefaultEventScheduler;
import ch.fhnw.ether.controller.event.IEventScheduler.IAction;
import ch.fhnw.ether.controller.event.IEventScheduler.IAnimationAction;
import ch.fhnw.ether.controller.event.IKeyEvent;
import ch.fhnw.ether.controller.event.IPointerEvent;
import ch.fhnw.ether.controller.tool.ITool;
import ch.fhnw.ether.controller.tool.NavigationTool;
import ch.fhnw.ether.controller.tool.PickTool;
import ch.fhnw.ether.render.DefaultRenderManager;
import ch.fhnw.ether.render.IRenderManager;
import ch.fhnw.ether.render.IRenderer;
import ch.fhnw.ether.render.forward.ForwardRenderer;
import ch.fhnw.ether.scene.IScene;
import ch.fhnw.ether.scene.camera.ICamera;
import ch.fhnw.ether.ui.UI;
import ch.fhnw.ether.view.IView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Default controller that implements some basic common functionality. Use as
 * base for more complex implementations.
 *
 * @author radar
 */
// FIXME: PickTool doesn't really belong here (any tools at all?)
public class FractalController implements IController {
    private static final boolean DBG = false;

    private final DefaultEventScheduler scheduler;
    private final IRenderer renderer;
    private final IRenderManager renderManager;

    private IScene scene;

    private final ArrayList<IView> views = new ArrayList<>();
    private UI ui;

    private NavigationTool navigationTool;
    private PickTool pickTool;

    private IView currentView;
    private ITool currentTool;
    private boolean navigationToolActive = true;

    public FractalController() {
        this(60);
    }

    public FractalController(float fps) {
        this.renderer = new ForwardRenderer();
        this.renderManager = new DefaultRenderManager(this, renderer);
        this.scheduler = new DefaultEventScheduler(this, renderManager.getRenderRunnable(), fps);
        run(time -> {
            this.ui = new UI(this);
            this.navigationTool = new NavigationTool(this);
            this.pickTool = new PickTool(this);
            currentTool = pickTool;
        });
        currentView = null;
    }

    @Override
    public final IScene getScene() {
        return scene;
    }

    @Override
    public final void setScene(IScene scene) {
        this.scene = scene;
    }

    @Override
    public final List<IView> getViews() {
        return Collections.unmodifiableList(views);
    }

    @Override
    public final IView getCurrentView() {
        return currentView;
    }

    @Override
    public final void enableViews(Collection<IView> views) {
        if (views != null) {
            for (IView view : this.views) {
                view.setEnabled(views.contains(view));
            }
        } else {
            for (IView view : this.views) {
                view.setEnabled(true);
            }
        }
    }

    @Override
    public ICamera getCamera(IView view) {
        return renderManager.getCamera(view);
    }

    @Override
    public void setCamera(IView view, ICamera camera) {
        renderManager.setCamera(view, camera);
    }

    @Override
    public final ITool getCurrentTool() {
        return currentTool;
    }

    @Override
    public final void setCurrentTool(ITool tool) {
        if (tool == null)
            tool = pickTool;

        if (currentTool == tool)
            return;

        currentTool.deactivate();
        currentTool = tool;
        currentTool.activate();
        currentTool.refresh(getCurrentView());
    }

    @Override
    public final NavigationTool getNavigationTool() {
        return navigationTool;
    }

    @Override
    public final UI getUI() {
        return ui;
    }

    @Override
    public final IRenderManager getRenderManager() {
        return renderManager;
    }

    @Override
    public void animate(IAnimationAction action) {
        scheduler.animate(action);
    }

    @Override
    public void kill(IAnimationAction action) {
        scheduler.kill(action);
    }

    @Override
    public void run(IAction action) {
        scheduler.run(action);
    }

    @Override
    public void run(double delay, IAction action) {
        scheduler.run(delay, action);
    }

    @Override
    public final void repaint() {
        scheduler.repaint();
    }

    @Override
    public boolean isSceneThread() {
        return scheduler.isSceneThread();
    }

    @Override
    public void ensureSceneThread() {
        if (!isSceneThread())
            throw new IllegalThreadStateException("must be called on scene thread");
    }

    // view listener

    @Override
    public final void viewCreated(IView view) {
        if (DBG)
            System.out.println("view created " + view);

        views.add(view);
        renderManager.addView(view);
    }

    @Override
    public void viewDisposed(IView view) {
        if (DBG)
            System.out.println("view disposed " + view);

        views.remove(view);
        if (currentView == view) {
            navigationTool.deactivate();
            setCurrentView(null);
        }
        renderManager.removeView(view);
    }

    @Override
    public void viewGainedFocus(IView view) {
        if (DBG)
            System.out.println("view gained focus " + view);

        setCurrentView(view);
        navigationTool.activate();
    }

    @Override
    public void viewLostFocus(IView view) {
        if (DBG)
            System.out.println("view lost focus " + view);

        if (view == currentView) {
            navigationTool.deactivate();
            setCurrentView(null);
        }
    }

    @Override
    public void viewChanged(IView view) {
        if (DBG)
            System.out.println("view changed " + view);

        getCamera(view).getUpdater().request();
        currentTool.refresh(view);
        navigationTool.refresh(view);
    }

    // key listener

    @Override
    public void keyPressed(IKeyEvent e) {
        if (DBG)
            System.out.println("key pressed " + e.getView());

        setCurrentView(e.getView());

        // ui has precedence over everything else
        if (ui != null && ui.keyPressed(e))
            return;

        // always handle ESC (if not handled by button)
        if (e.getKeyCode() == IKeyEvent.VK_ESCAPE)
            System.exit(0);

        // finally, pass on to tool
        currentTool.keyPressed(e);
    }

    @Override
    public void keyReleased(IKeyEvent e) {
    }

    // pointer listener

    @Override
    public void pointerEntered(IPointerEvent e) {
        // if (DBG)
        // System.out.println("pointer entered " + e.getView());
    }

    @Override
    public void pointerExited(IPointerEvent e) {
        // if (DBG)
        // System.out.println("pointer exited " + e.getView());
    }

    @Override
    public void pointerPressed(IPointerEvent e) {
        if (DBG)
            System.out.println("pointer pressed " + e.getView());

        setCurrentView(e.getView());

        // ui has precedence over everything else
        if (ui != null && ui.pointerPressed(e))
            return;

        // handle tools (with active navigation when modifier is pressed)
        if (!e.isModifierDown())
            currentTool.pointerPressed(e);
        else if (navigationToolActive)
            navigationTool.pointerPressed(e);
    }

    @Override
    public void pointerReleased(IPointerEvent e) {
        if (DBG)
            System.out.println("pointer released " + e.getView());

        if (ui != null && ui.pointerReleased(e))
            return;

        if (!e.isModifierDown())
            currentTool.pointerReleased(e);
        else if (navigationToolActive)
            navigationTool.pointerReleased(e);
    }

    @Override
    public void pointerClicked(IPointerEvent e) {
        // if (DBG)
        // System.out.println("pointer clicked " + e.getView());
    }

    // pointer motion listener

    @Override
    public void pointerMoved(IPointerEvent e) {
        // if (DBG)
        // System.out.println("pointer moved " + e.getView());

        if (ui != null)
            ui.pointerMoved(e);
        currentTool.pointerMoved(e);
        if (navigationToolActive)
        navigationTool.pointerMoved(e);
    }

    @Override
    public void pointerDragged(IPointerEvent e) {
        // if (DBG)
        // System.out.println("pointer dragged " + e.getView());

        // ui has precedence over everything else
        if (ui != null && ui.pointerDragged(e))
            return;

        if (!e.isModifierDown())
            currentTool.pointerDragged(e);
        else if (navigationToolActive)
            navigationTool.pointerDragged(e);
    }

    // pointer scrolled listener

    @Override
    public void pointerScrolled(IPointerEvent e) {
        // if (DBG)
        // System.out.println("pointer scrolled " + e.getView());

        // currently, only navigation tool receives scroll events
        if (navigationToolActive)
        navigationTool.pointerScrolled(e);
    }

    public static void printHelp(String[] help) {
        for (String s : help)
            System.out.println(s);
    }

    // private stuff

    private void setCurrentView(IView view) {
        if (DBG)
            System.out.println("set current view " + view);
        if (currentView != view) {
            currentView = view;
            if (currentView != null)
                getCurrentTool().refresh(currentView);
        }
    }

    public void setNavigationToolActivate(boolean isActiv) {
        navigationToolActive = isActiv;
    }
}
