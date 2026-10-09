package SLOTMACHINEFINAL;

/**
 * Creates symbols according to their requested type.
 */
public final class SymbolCreator {
    private SymbolCreator() {
    }
    
    /**
     * Creates a symbol of the requested type.
     * @param type Symbol type.
     * @param color Color of the symbol.
     * @return Created symbol.
     * @throws IllegalArgumentException If the type is null or unsupported.
     */
    public static Symbol create(String type, String color) {
        if (type == null) {
            throw new IllegalArgumentException(
                "Symbol type cannot be null."
            );
        }
    
        if (type.equalsIgnoreCase("normal")) {
            return new Symbol(color);
        }
    
        if (type.equalsIgnoreCase("ephemeral")) {
            return new EphemeralSymbol(color);
        }
    
        if (type.equalsIgnoreCase("shy")) {
            return new ShySymbol(color);
        }
    
        if (type.equalsIgnoreCase("wildcard")) {
            return new WildcardSymbol(color);
        }
    
        throw new IllegalArgumentException(
            "Unsupported symbol type: " + type
        );
    }
}
