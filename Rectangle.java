import java.awt.*;

/**
 * A rectangle that can be manipulated and that draws itself on a canvas.
 * 
 * @author  Michael Kolling and David J. Barnes (Modified)
 * @version 1.0  (15 July 2000)()
 */


 
public class Rectangle{

    public static int EDGES = 4;
    
    private int height;
    private int width;
    private int xPosition;
    private int yPosition;
    private String color;
    private boolean isVisible;

    /**
     * multiply the size of the rectangle by x times
     */
    public void multiplySize(int x){
        if (x > 0){
            erase();
            changeSize(width*x,height*x);
            draw();
        }else{
            erase();
            changeSize(width/Math.abs(x),height/Math.abs(x));
            draw();
        }
    }
    /**
     * Create a square with its perimeter (parameter)
     */
    public void makeSquare(int perimeter){
        
        Rectangle rectangle = new Rectangle();
        changeSize(perimeter/2,perimeter/2);
        draw();
        
    }
    /**
     * Move the rectangle x times down, to the right, or to the left
     */
    public void walk(int x){
        if (x>0){
            for (int i = 0; i < Math.abs(x); i++) {
                erase();
                moveHorizontal(1);
                moveVertical(1);
                draw();
            }
        }else{
            for (int i = 0; i < Math.abs(x); i++) {
                erase();
                moveHorizontal(-1);
                moveVertical(1);
                draw();
            }
        }
    }
    /**
     * zoom in or out on the rectangle.
     */
    public void zoom(char z){
        erase();
        if (z == '+'){
            height += height;
            width += width;
        }
        if (z =='-'){
            height -= height;
            width -= width;
        }
        draw();
    }
    
    /**
     * Calculate the perimeter of the rectangle.
     */
    public int perimeter(){
        int perimeter;
        perimeter = height + width;
        return perimeter;
        
    }
    
    
    /**
     * Create a new rectangle at default position with default color.
     */
    public Rectangle(){
        height = 30;
        width = 40;
        xPosition = 70;
        yPosition = 15;
        color = "magenta";
        isVisible = false;
    }
    

    /**
     * Make this rectangle visible. If it was already visible, do nothing.
     */
    public void makeVisible(){
        isVisible = true;
        draw();
    }
    
    /**
     * Make this rectangle invisible. If it was already invisible, do nothing.
     */
    public void makeInvisible(){
        erase();
        isVisible = false;
    }
    
    /**
     * Move the rectangle a few pixels to the right.
     */
    public void moveRight(){
        moveHorizontal(20);
    }

    /**
     * Move the rectangle a few pixels to the left.
     */
    public void moveLeft(){
        moveHorizontal(-20);
    }

    /**
     * Move the rectangle a few pixels up.
     */
    public void moveUp(){
        moveVertical(-20);
    }

    /**
     * Move the rectangle a few pixels down.
     */
    public void moveDown(){
        moveVertical(20);
    }

    /**
     * Move the rectangle directly to a specific coordinate.
     * @param x the new x position
     * @param y the new y position
     */
    public void moveTo(int x, int y){
        erase();
        xPosition = x;
        yPosition = y;
        draw();
    }

    /**
     * Move the rectangle horizontally.
     * @param distance the desired distance in pixels
     */
    public void moveHorizontal(int distance){
        erase();
        xPosition += distance;
        draw();
    }

    /**
     * Move the rectangle vertically.
     * @param distance the desired distance in pixels
     */
    public void moveVertical(int distance){
        erase();
        yPosition += distance;
        draw();
    }

    /**
     * Slowly move the rectangle horizontally.
     * @param distance the desired distance in pixels
     */
    public void slowMoveHorizontal(int distance){
        int delta;

        if(distance < 0) {
            delta = -1;
            distance = -distance;
        } else {
            delta = 1;
        }

        for(int i = 0; i < distance; i++){
            xPosition += delta;
            draw();
        }
    }

    /**
     * Slowly move the rectangle vertically.
     * @param distance the desired distance in pixels
     */
    public void slowMoveVertical(int distance){
        int delta;

        if(distance < 0) {
            delta = -1;
            distance = -distance;
        } else {
            delta = 1;
        }

        for(int i = 0; i < distance; i++){
            yPosition += delta;
            draw();
        }
    }

    /**
     * Change the size to the new size
     * @param newHeight the new height in pixels. newHeight must be >=0.
     * @param newWidht the new width in pixels. newWidth must be >=0.
     */
    public void changeSize(int newHeight, int newWidth) {
        if (newHeight < 0 || newWidth < 0){
            return;
        }
        erase();
        height = newHeight;
        width = newWidth;
        draw();
    }
    
    /**
     * Change the color. 
     * @param color the new color. Valid colors are "red", "yellow", "blue", "green",
     * "magenta" and "black".
     */
    public void changeColor(String newColor){
        color = newColor;
        draw();
    }

    /*
     * Draw the rectangle with current specifications on screen.
     */

    private void draw() {
        if(isVisible) {
            Canvas canvas = Canvas.getCanvas();
            canvas.draw(this, color,
                new java.awt.Rectangle(xPosition, yPosition, 
                                       width, height));
            canvas.wait(10);
        }
    }

    /*
     * Erase the rectangle on screen.
     */
    private void erase(){
        if(isVisible) {
            Canvas canvas = Canvas.getCanvas();
            canvas.erase(this);
        }
    }
}

