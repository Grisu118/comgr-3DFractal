package ch.fhnw.comgr.fractal.ui;

import ch.fhnw.ether.controller.event.IPointerEvent;
import ch.fhnw.ether.ui.AbstractWidget;
import ch.fhnw.ether.ui.GraphicsPlane;
import ch.fhnw.ether.ui.IWidget;
import ch.fhnw.ether.ui.UI;
import ch.fhnw.ether.view.IView;
import ch.fhnw.util.math.MathUtilities;

import java.awt.*;

/**
 * Created by benjamin on 05.11.2015.
 */
public class BooleanWidget extends AbstractWidget {
    public interface IBooleanAction extends IWidgetAction<BooleanWidget> {
        @Override
        void execute(BooleanWidget widget, IView view);
    }

    private static final int BOOLEAN_WIDTH = 56;
    private static final int BOOLEAN_HEIGHT = 24;

    private static final int BOOLEAN_GAP = 8;

    private static final Color BOOLEAN_BG = new Color(1f, 1f, 1f, 0.25f);
    private static final Color BOOLEAN_FG = new Color(0.6f, 0, 0, 0.75f);

    private boolean value;
    private String onLabel;
    private String offLabel;

    public BooleanWidget(int x, int y, String label, String onLabel, String offLabel, String help, IBooleanAction action) {
        super(x, y, label, help, action);
        this.onLabel = onLabel;
        this.offLabel = offLabel;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public boolean hit(IPointerEvent e) {
        int x = e.getX();
        int y = e.getY();
        UI ui = getUI();
        float bx = ui.getX() + getX() * (BOOLEAN_GAP + BOOLEAN_WIDTH);
        float by = ui.getY() + getY() * (BOOLEAN_GAP + BOOLEAN_HEIGHT);
        return x >= bx && x <= bx + BOOLEAN_WIDTH && y >= by && y <= by + BOOLEAN_HEIGHT;
    }

    @Override
    public void draw(GraphicsPlane surface) {
        int bw = BOOLEAN_WIDTH;
        int bw2 = bw/2;
        int bh = BOOLEAN_HEIGHT;
        int bg = BOOLEAN_GAP;
        int bx = getX() * (bg + bw);
        int by = getY() * (bg + bh);
        surface.fillRect(BOOLEAN_BG, bx + 4, surface.getHeight() - by - bh - 4, bw, bh);
        if (value) {
            surface.fillRect(BOOLEAN_FG, bx + 4, surface.getHeight() - by - bh - 4, bw2, bh);
        } else {
            surface.fillRect(BOOLEAN_FG, bx + 4 + bw2 ,surface.getHeight() - by - bh - 4, bw2, bh);
        }

        if (onLabel != null) {
            surface.drawString(TEXT_COLOR, onLabel, bx + 6, surface.getHeight() - by - 8);
        }
        if (offLabel != null) {
            surface.drawString(TEXT_COLOR, offLabel, bx + 6 + bw2, surface.getHeight() - by - 8);
        }
    }

    @Override
    public void fire(IView view) {
        if (getAction() == null)
            throw new UnsupportedOperationException("BooleanWidget '" + getLabel() + "' has no action defined");
        ((IBooleanAction) getAction()).execute(this, view);
    }

    @Override
    public boolean pointerPressed(IPointerEvent e) {
        if (hit(e)) {
            updateValue(e);
            return true;
        }
        return false;
    }

    private void updateValue(IPointerEvent e) {
        UI ui = getUI();
        float bx = ui.getX() + getX() * (BOOLEAN_GAP + BOOLEAN_WIDTH);
        value = e.getX() - bx <= BOOLEAN_WIDTH/2;
        updateRequest();
        fire(e.getView());
    }
}
