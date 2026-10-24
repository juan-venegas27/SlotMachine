import java.awt.geom.Ellipse2D;

/**
 * A circle that can be manipulated and that draws itself on a canvas.
 */
public class Circle extends Figure {
    public static final double PI = 3.1416;
    private int diameter;

    /**
     * Creates a circle at the default position with the default color.
     */
    public Circle() {
        super(20, 15, "blue");
        diameter = 30;
    }

    /**
     * Changes the circle diameter.
     * @param newDiameter New diameter.
     */
    public void changeSize(int newDiameter) {
        if (newDiameter < 0) {
            return;
        }
        erase();
        diameter = newDiameter;
        draw();
    }

    /**
     * Resizes the circle using the smaller requested dimension.
     * @param newHeight New height.
     * @param newWidth New width.
     */
    @Override
    protected void resize(int newHeight, int newWidth) {
        changeSize(Math.min(newHeight, newWidth));
    }

    /**
     * Returns the current diameter.
     * @return Current diameter.
     */
    protected int getDiameter() {
        return diameter;
    }

    /**
     * Creates the AWT geometric representation of the circle.
     * @return Geometric circle.
     */
    @Override
    protected java.awt.Shape createShape() {
        return new Ellipse2D.Double(
            getXPosition(),
            getYPosition(),
            diameter,
            diameter
        );
    }
}