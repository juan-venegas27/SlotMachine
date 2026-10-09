package SLOTMACHINEFINAL;

/**
 * A rectangle that can be manipulated and that draws itself on a canvas.
 */
public class Rectangle extends Figure {
    public static int EDGES = 4;
    private int height;
    private int width;

    /**
     * Creates a rectangle at the default position with the default color.
     */
    public Rectangle() {
        super(70, 15, "gold");
        height = 30;
        width = 40;
    }

    /**
     * Multiplies the rectangle size.
     * @param x Size factor.
     */
    public void multiplySize(int x) {
        if (x > 0) {
            erase();
            changeSize(width * x, height * x);
            draw();
        } else if (x < 0) {
            erase();
            changeSize(
                width / Math.abs(x),
                height / Math.abs(x)
            );
            draw();
        }
    }

    /**
     * Creates a square using the given perimeter.
     * @param perimeter Value used to calculate the square size.
     */
    public void makeSquare(int perimeter) {
        changeSize(perimeter / 2, perimeter / 2);
        draw();
    }

    /**
     * Moves the rectangle diagonally.
     * @param x Number of steps.
     */
    public void walk(int x) {
        int horizontal = x >= 0 ? 1 : -1;

        for (int i = 0; i < Math.abs(x); i++) {
            erase();
            moveHorizontal(horizontal);
            moveVertical(1);
            draw();
        }
    }

    /**
     * Zooms the rectangle in or out.
     * @param z '+' to increase or '-' to decrease.
     */
    public void zoom(char z) {
        erase();

        if (z == '+') {
            height += height;
            width += width;
        } else if (z == '-') {
            height = 0;
            width = 0;
        }

        draw();
    }

    /**
     * Calculates the rectangle perimeter according to the project implementation.
     * @return Calculated perimeter.
     */
    public int perimeter() {
        return height + width;
    }

    /**
     * Changes the rectangle size.
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
     * Resizes the rectangle through the Figure interface.
     * @param newHeight New height.
     * @param newWidth New width.
     */
    @Override
    protected void resize(int newHeight, int newWidth) {
        changeSize(newHeight, newWidth);
    }

    /**
     * Creates the AWT geometric representation of the rectangle.
     * @return Geometric rectangle.
     */
    @Override
    protected java.awt.Shape createShape() {
        return new java.awt.Rectangle(
            getXPosition(),
            getYPosition(),
            width,
            height
        );
    }
}
