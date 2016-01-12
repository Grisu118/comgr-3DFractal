package ch.fhnw.comgr.fractal;

import ch.fhnw.comgr.fractal.fractals.FractalType;

import javax.swing.*;

/**
 * Created by benjamin on 29.10.2015.
 */
public class MainApplication {

    public static void main(String[] args) {
        FractalType r = (FractalType) JOptionPane.showInputDialog(null, "Wählen Sie den Fractal Typ", "Fractal Typ", JOptionPane.QUESTION_MESSAGE, null, new FractalType[] {FractalType.SIMPLE_TREE, FractalType.MANDELBULB, FractalType.SHADER}, FractalType.MANDELBULB);

        if (r != null) {
            new FractalViewer(r);
        }
    }

}
