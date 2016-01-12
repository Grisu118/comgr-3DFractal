package ch.fhnw.comgr.fractal.ui;

import ch.fhnw.ether.controller.event.IKeyEvent;
import ch.fhnw.ether.controller.event.IPointerEvent;
import ch.fhnw.ether.ui.AbstractWidget;
import ch.fhnw.ether.ui.GraphicsPlane;
import ch.fhnw.ether.ui.UI;
import ch.fhnw.ether.view.IView;

import java.awt.*;

/**
 * Created by benjamin on 12.01.2016.
 */
public class SmallButton extends AbstractWidget {
    public interface IButtonAction extends IWidgetAction<SmallButton> {
        @Override
        void execute(SmallButton button, IView view);
    }

    private static final int BUTTON_WIDTH = 64;
    private static final int BUTTON_HEIGHT = 24;

    private static final int BUTTON_GAP = 8;

    public enum State {
        DEFAULT(0.6f, 0, 0, 0.75f), PRESSED(1, 0.2f, 0.2f, 0.75f), DISABLED(0.5f, 0.5f, 0.5f, 0.75f);

        State(float r, float g, float b, float a) {
            this.color = new Color(r, g, b, a);
        }

        public Color getColor() {
            return color;
        }

        private final Color color;
    }

    private int key;
    private State state = State.DEFAULT;

    public SmallButton(int x, int y, String label, String help, int key) {
        this(x, y, label, help, key, null);
    }

    public SmallButton(int x, int y, String label, String help, int key, IButtonAction action) {
        super(x, y, label, help, action);
        this.key = key;
    }

    public SmallButton(int x, int y, String label, String help, int key, State state, IButtonAction action) {
        this(x, y, label, help, key, action);
        setState(state);
    }

    public SmallButton(int x, int y, String label, String help, int key, boolean pressed, IButtonAction action) {
        this(x, y, label, help, key, action);
        setState(pressed);
    }

    public int getKey() {
        return key;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
        updateRequest();
    }

    public void setState(boolean pressed) {
        setState(pressed ? State.PRESSED : State.DEFAULT);
        updateRequest();
    }

    @Override
    public boolean hit(IPointerEvent e) {
        int x = e.getX();
        int y = e.getY();
        UI ui = getUI();
        float bx = ui.getX() + getX() * (BUTTON_GAP + BUTTON_WIDTH);
        float by = ui.getY() + getY() * (BUTTON_GAP + BUTTON_HEIGHT);
        return x >= bx && x <= bx + BUTTON_WIDTH && y >= by && y <= by + BUTTON_HEIGHT;
    }

    @Override
    public void draw(GraphicsPlane surface) {
        int bw = SmallButton.BUTTON_WIDTH;
        int bh = SmallButton.BUTTON_HEIGHT;
        int bg = SmallButton.BUTTON_GAP;
        int bx = getX() * (bg + bw);
        int by = getY() * (bg + bh);
        surface.fillRect(getState().getColor(), bx + 4, surface.getHeight() - by - bh - 4, bw, bh);
        String label = getLabel();
        if (label != null)
            surface.drawString(TEXT_COLOR, label, bx + 6, surface.getHeight() - by - 8);

    }

    @Override
    public void fire(IView view) {
        if (state == State.DISABLED)
            return;

        if (getAction() == null)
            throw new UnsupportedOperationException("button '" + getLabel() + "' has no action defined");
        ((IButtonAction) getAction()).execute(this, view);
    }

    @Override
    public boolean keyPressed(IKeyEvent e) {
        if (getKey() == e.getKeyCode()) {
            fire(e.getView());
            return true;
        }
        return false;
    }

    @Override
    public boolean pointerPressed(IPointerEvent e) {
        if (hit(e)) {
            fire(e.getView());
            return true;
        }
        return false;
    }
}
