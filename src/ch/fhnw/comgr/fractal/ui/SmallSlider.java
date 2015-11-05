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

package ch.fhnw.comgr.fractal.ui;

import java.awt.Color;

import ch.fhnw.ether.controller.event.IPointerEvent;
import ch.fhnw.ether.ui.AbstractWidget;
import ch.fhnw.ether.ui.GraphicsPlane;
import ch.fhnw.ether.ui.Slider;
import ch.fhnw.ether.ui.UI;
import ch.fhnw.ether.view.IView;
import ch.fhnw.util.math.MathUtilities;

public class SmallSlider extends AbstractWidget {
    public interface ISliderAction extends IWidgetAction<SmallSlider> {
        @Override
        void execute(SmallSlider slider, IView view);
    }

    private static final int SLIDER_WIDTH = 144;
    private static final int SLIDER_HEIGHT = 24;

    private static final int SLIDER_GAP = 8;

    private static final Color SLIDER_BG = new Color(1f, 1f, 1f, 0.25f);
    private static final Color SLIDER_FG = new Color(0.6f, 0, 0, 0.75f);

    private boolean sliding;
    private float value;

    public SmallSlider(int x, int y, String label, String help) {
        this(x, y, label, help, 0, null);
    }

    public SmallSlider(int x, int y, String label, String help, float value) {
        this(x, y, label, help, value, null);
    }

    public SmallSlider(int x, int y, String label, String help, float value, ISliderAction action) {
        super(x, y, label, help, action);
        this.value = value;
    }

    /**
     * Get Value as float from 0 to 1
     * @return 0...1
     */
    public float getValue() {
        return value;
    }

    /**
     * Get the value as Int between from and to.
     * @param from the smallest value.
     * @param to the highest value.
     * @return int between from and to.
     */
    public int getValue(int from, int to) {
        return Math.round(value*(to-from))+from;
    }

    @Override
    public boolean hit(IPointerEvent e) {
        int x = e.getX();
        int y = e.getY();
        UI ui = getUI();
        float bx = ui.getX() + getX() * (SLIDER_GAP + SLIDER_WIDTH);
        float by = ui.getY() + getY() * (SLIDER_GAP + SLIDER_HEIGHT);
        return x >= bx && x <= bx + SLIDER_WIDTH && y >= by && y <= by + SLIDER_HEIGHT;
    }

    @Override
    public void draw(GraphicsPlane surface) {
        int bw = SmallSlider.SLIDER_WIDTH;
        int bh = SmallSlider.SLIDER_HEIGHT;
        int bg = SmallSlider.SLIDER_GAP;
        int bx = getX() * (bg + bw);
        int by = getY() * (bg + bh);
        surface.fillRect(SLIDER_BG, bx + 4, surface.getHeight() - by - bh - 4, bw, bh);
        surface.fillRect(SLIDER_FG, bx + 4, surface.getHeight() - by - bh - 4, (int) (value * bw), bh);
        String label = getLabel();
        if (label != null)
            surface.drawString(TEXT_COLOR, label, bx + 6, surface.getHeight() - by - 8);
    }

    @Override
    public void fire(IView view) {
        if (getAction() == null)
            throw new UnsupportedOperationException("button '" + getLabel() + "' has no action defined");
        ((ISliderAction) getAction()).execute(this, view);
    }

    @Override
    public boolean pointerPressed(IPointerEvent e) {
        if (hit(e)) {
            sliding = true;
            updateValue(e);
            return true;
        }
        return false;
    }

    @Override
    public boolean pointerReleased(IPointerEvent e) {
        if (sliding) {
            sliding = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean pointerDragged(IPointerEvent e) {
        if (sliding) {
            updateValue(e);
            return true;
        }
        return false;
    }

    private void updateValue(IPointerEvent e) {
        UI ui = getUI();
        float bx = ui.getX() + getX() * (SLIDER_GAP + SLIDER_WIDTH);
        value = MathUtilities.clamp((e.getX() - bx) / SLIDER_WIDTH, 0, 1);
        updateRequest();
        fire(e.getView());
    }
}
