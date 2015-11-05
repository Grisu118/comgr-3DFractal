package ch.fhnw.comgr.fractal.ui;

import ch.fhnw.ether.ui.AbstractWidget;
import ch.fhnw.ether.ui.GraphicsPlane;
import ch.fhnw.ether.view.IView;

import java.awt.*;

/**
 * Created by benjamin on 05.11.2015.
 */
public class TextWidget extends AbstractWidget {

    private static final int TEXT_WIDTH = 144;
    private static final int TEXT_HEIGHT = 24;

    private static final int TEXT_GAP = 8;

    private static final Color TEXT_BG = new Color(1f, 1f, 1f, 0.25f);


    private int labelLegth;
    private String content;

    public TextWidget(int x, int y, int labelLength, String label) {
        super(x, y, label, null, null);
        this.labelLegth = labelLength;
    }

    @Override
    public void draw(GraphicsPlane surface) {
        int bw = TEXT_WIDTH;
        int bh = TEXT_HEIGHT;
        int bg = TEXT_GAP;
        int bx = getX() * (bg + bw);
        int by = getY() * (bg + bh);
        surface.fillRect(TEXT_BG, bx + 4, surface.getHeight() - by - bh - 4, bw, bh);
        String label = getLabel();
        if (label != null) {
            surface.drawString(TEXT_COLOR, label, bx + 6, surface.getHeight() - by - 8);
        }
        if (content != null) {
            surface.drawString(TEXT_COLOR, content, bx + 6 + labelLegth, surface.getHeight() - by - 8);
        }
    }

    @Override
    public void fire(IView view) {
        //No action
    }

    public void setContent(String content) {
        this.content = content;
    }
}
