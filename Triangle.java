package SLOTMACHINEFINAL;

import java.awt.Polygon;

/**
 * A triangle that can be manipulated and that draws itself on a canvas.
 */
public class Triangle extends Figure {
    public static int VERTICES = 3;
    private int height;
    private int width;

    /**
     * Creates a triangle at the default position with the default color.
     */
    public Triangle() {
        super(140, 15, "green");
        height = 30;
        width = 40;
    }

    /**
     * Changes the triangle size.
     * @param newHeight New height.
     * @param newWidth New width.
     */
    public void changeSize(int newHeight, int newWidth) {
        if (newHeight < 0 || newWidth < 0) {
            return;
        }

        erase();
        height = newHeight;
        width = newWidth;
        draw();
    }

    /**
     * Resizes the triangle through the Figure interface.
     * @param newHeight New height.
     * @param newWidth New width.
     */
    @Override
    protected void resize(int newHeight, int newWidth) {
        changeSize(newHeight, newWidth);
    }

    /**
     * Creates the AWT geometric representation of the triangle.
     * @return Geometric triangle.
     */
    @Override
    protected java.awt.Shape createShape() {
        int x = getXPosition();
        int y = getYPosition();
        int[] xpoints = {
            x,
            x + width / 2,
            x - width / 2
        };
        int[] ypoints = {
            y,
            y + height,
            y + height
        };

        return new Polygon(xpoints, ypoints, VERTICES);
    }
}
