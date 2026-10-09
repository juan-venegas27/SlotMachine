package SLOTMACHINEFINAL;

/**
 * Creates wheels according to their requested type.
 */
public final class WheelCreator {
    private WheelCreator() {
    }

    /**
     * Creates a wheel of the requested type.
     * @param type Wheel type.
     * @return Created wheel.
     * @throws IllegalArgumentException If the type is null or unsupported.
     */
    public static Wheel create(String type) {
        if (type == null) {
            throw new IllegalArgumentException(
                "Wheel type cannot be null."
            );
        }
        if (type.equalsIgnoreCase("normal")) {
            return new Wheel();
        }
        if (type.equalsIgnoreCase("lefty")) {
            return new LeftyWheel();
        }
        if (type.equalsIgnoreCase("rebel")) {
            return new RebelWheel();
        }
        throw new IllegalArgumentException(
            "Unsupported wheel type: " + type
        );
    }
}
