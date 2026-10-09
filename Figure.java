package SLOTMACHINEFINAL;

/**
 * Abstract base class for drawable figures.
 */
public abstract class Figure {
    private int xPosition;
    private int yPosition;
    private String color;
    private boolean isVisible;

    /**
     * Creates a figure with the given position and color.
     * @param x Initial horizontal position.
     * @param y Initial vertical position.
     * @param color Initial color.
     */
    protected Figure(int x, int y, String color) {
        xPosition = x;
        yPosition = y;
        this.color = color;
        isVisible = false;
    }

    /**
     * Makes the figure visible.
     */
    public void makeVisible() {
        isVisible = true;
        draw();
    }

    /**
     * Makes the figure invisible.
     */
    public void makeInvisible() {
        erase();
        isVisible = false;
    }

    /**
     * Moves the figure to the right.
     */
    public void moveRight() {
        moveHorizontal(20);
    }

    /**
     * Moves the figure to the left.
     */
    public void moveLeft() {
        moveHorizontal(-20);
    }

    /**
     * Moves the figure up.
     */
    public void moveUp() {
        moveVertical(-20);
    }

    /**
     * Moves the figure down.
     */
    public void moveDown() {
        moveVertical(20);
    }

    /**
     * Moves the figure horizontally.
     * @param distance Distance to move.
     */
    public void moveHorizontal(int distance) {
        erase();
        xPosition += distance;
        draw();
    }

    /**
     * Moves the figure vertically.
     * @param distance Distance to move.
     */
    public void moveVertical(int distance) {
        erase();
        yPosition += distance;
        draw();
    }

    /**
     * Moves the figure horizontally one pixel at a time.
     * @param distance Distance to move.
     */
    public void slowMoveHorizontal(int distance) {
        int delta = 1;

        if (distance < 0) {
            delta = -1;
            distance = -distance;
        }

        for (int i = 0; i < distance; i++) {
            xPosition += delta;
            draw();
        }
    }

    /**
     * Moves the figure vertically one pixel at a time.
     * @param distance Distance to move.
     */
    public void slowMoveVertical(int distance) {
        int delta = 1;

        if (distance < 0) {
            delta = -1;
            distance = -distance;
        }

        for (int i = 0; i < distance; i++) {
            yPosition += delta;
            draw();
        }
    }

    /**
     * Moves the figure to a given coordinate.
     * @param x New horizontal position.
     * @param y New vertical position.
     */
    public void moveTo(int x, int y) {
        erase();
        xPosition = x;
        yPosition = y;
        draw();
    }

    /**
     * Changes the figure color.
     * @param newColor New color.
     */
    public void changeColor(String newColor) {
        color = newColor;
        draw();
    }

    /**
     * Returns the horizontal position.
     * @return Horizontal position.
     */
    protected int getXPosition() {
        return xPosition;
    }

    /**
     * Returns the vertical position.
     * @return Vertical position.
     */
    protected int getYPosition() {
        return yPosition;
    }

    /**
     * Returns the current color.
     * @return Current color.
     */
    protected String getColor() {
        return color;
    }

    /**
     * Resizes the figure using height and width.
     * @param newHeight New height.
     * @param newWidth New width.
     */
    protected abstract void resize(int newHeight, int newWidth);

    /**
     * Creates the geometric shape used by Canvas.
     * @return Java AWT geometric shape.
     */
    protected abstract java.awt.Shape createShape();

    /**
     * Draws the figure on the Canvas.
     */
    protected void draw() {
        if (isVisible) {
            Canvas.getCanvas().draw(this, color, createShape());
        }
    }

    /**
     * Erases the figure from the Canvas.
     */
    protected void erase() {
        if (isVisible) {
            Canvas.getCanvas().erase(this);
        }
    }
}
