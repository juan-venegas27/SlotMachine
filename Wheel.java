import java.util.ArrayList;

/**
 * Representa una rueda individual de la máquina tragamonedas (Slot Machine).
 * Cada rueda contiene una secuencia ordenada de símbolos (colores CSS),
 * mantiene la posición actual de visualización y su representación gráfica con shapes.
 * 
 * @author Tu Nombre / DOPO-POOB
 * @version 1.0
 */
public class Wheel {
    private ArrayList<String> symbols;
    private int currentIndex;
    private Rectangle shape;
    private boolean isVisible;
    private int xPosition;
    private int yPosition;
    private static final int DEFAULT_WIDTH = 60;
    private static final int DEFAULT_HEIGHT = 80;

    /**
     * Constructor para crear una rueda en una posición gráfica específica.
     * 
     * @param x Posición horizontal en el canvas.
     * @param y Posición vertical en el canvas.
     */
    public Wheel(int x, int y) {
        this.symbols = new ArrayList<String>();
        this.currentIndex = 0;
        this.isVisible = false;
        this.xPosition = x;
        this.yPosition = y;
        
        // Configuración visual de la rueda
        this.shape = new Rectangle();
        this.shape.changeSize(DEFAULT_HEIGHT, DEFAULT_WIDTH);
        this.shape.changeColor("white");
        this.shape.moveTo(x, y);
    }

    /**
     * Constructor por defecto.
     */
    public Wheel() {
        this(50, 50);
    }

    /**
     * Adiciona un símbolo (color) en una posición dada (base 1).
     * Aplica la regla:
     * - Si pos < 1, se inserta en la posición 1.
     * - Si pos > tamaño actual + 1, se inserta al final.
     * 
     * @param pos Posición donde insertar (iniciando en 1).
     * @param color Nombre del color CSS del símbolo.
     */
    public void addSymbol(int pos, String color) {
        if (color == null || color.trim().isEmpty()) {
            return;
        }
        
        // Ajuste de límites según especificación (base 1)
        if (pos < 1) {
            pos = 1;
        } else if (pos > symbols.size() + 1) {
            pos = symbols.size() + 1;
        }
        
        int index = pos - 1; // Conversión a base 0 de Java
        symbols.add(index, color);
        
        updateVisual();
    }

    /**
     * Elimina la primera ocurrencia de un símbolo por su color.
     * 
     * @param symbol Color del símbolo a eliminar.
     * @return true si el símbolo existía y fue eliminado, false de lo contrario.
     */
    public boolean delSymbol(String symbol) {
        int index = symbols.indexOf(symbol);
        if (index == -1) {
            return false;
        }
        
        symbols.remove(index);
        
        // Ajustar currentIndex si queda fuera de rango
        if (symbols.isEmpty()) {
            currentIndex = 0;
        } else if (currentIndex >= symbols.size()) {
            currentIndex = symbols.size() - 1;
        }
        
        updateVisual();
        return true;
    }

    /**
     * Gira la rueda hacia la siguiente posición.
     */
    public void spin() {
        if (!symbols.isEmpty()) {
            currentIndex = (currentIndex + 1) % symbols.size();
            updateVisual();
        }
    }

    /**
     * Ubica la rueda en un símbolo específico si este existe.
     * 
     * @param symbol Color del símbolo a ubicar.
     * @return true si el símbolo fue encontrado y ubicado, false de lo contrario.
     */
    public boolean placeSymbol(String symbol) {
        int index = symbols.indexOf(symbol);
        if (index != -1) {
            currentIndex = index;
            updateVisual();
            return true;
        }
        return false;
    }

    /**
     * Retorna el color del símbolo visible actualmente en la rueda.
     * 
     * @return Color CSS del símbolo actual, o null si la rueda no tiene símbolos.
     */
    public String getCurrentSymbol() {
        if (symbols.isEmpty()) {
            return null;
        }
        return symbols.get(currentIndex);
    }

    /**
     * Retorna los símbolos de la rueda en orden, iniciando por la posición 1.
     * 
     * @return Arreglo de Strings con los colores de los símbolos.
     */
    public String[] getSymbols() {
        return symbols.toArray(new String[0]);
    }

    /**
     * Retorna el número total de símbolos en la rueda.
     * 
     * @return Cantidad de símbolos.
     */
    public int countSymbols() {
        return symbols.size();
    }

    /**
     * Hace visible la representación gráfica de la rueda.
     */
    public void makeVisible() {
        this.isVisible = true;
        updateVisual();
        shape.makeVisible();
    }

    /**
     * Oculta la representación gráfica de la rueda.
     */
    public void makeInvisible() {
        this.isVisible = false;
        shape.makeInvisible();
    }

    /**
     * Mueve la rueda horizontalmente.
     * 
     * @param distance Distancia en píxeles.
     */
    public void moveHorizontal(int distance) {
        this.xPosition += distance;
        shape.moveHorizontal(distance);
    }

    /**
     * Mueve la rueda verticalmente.
     * 
     * @param distance Distancia en píxeles.
     */
    public void moveVertical(int distance) {
        this.yPosition += distance;
        shape.moveVertical(distance);
    }

    /**
     * Actualiza el color de la figura visual según el símbolo actual.
     */
    private void updateVisual() {
        String current = getCurrentSymbol();
        if (current != null) {
            shape.changeColor(current);
        } else {
            shape.changeColor("white");
        }
        
        if (isVisible) {
            shape.makeVisible();
        }
    }
}
