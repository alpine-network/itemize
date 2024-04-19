package co.crystaldev.itemize.api.loot;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * @since 1.1.0
 */
@AllArgsConstructor @NoArgsConstructor
public final class Chance {

    private final Random random = new Random();

    private double value1, value2;

    private Type type;

    public int getCount() {
        switch (this.type) {
            case RANGE: return (int) (this.random.nextInt((int) (this.value2 - this.value1)) + this.value1);
            case CHANCE: return this.random.nextDouble() < this.value1 ? 1 : 0;
            default: return (int) this.value1;
        }
    }

    @NotNull
    Object serialize() {
        switch (this.type) {
            case RANGE: return ((int) this.value1) + ".." + ((int) this.value2);
            case CHANCE: return this.value1;
            default: return (int) this.value1;
        }
    }

    @NotNull
    static Chance deserialize(@NotNull Object element) {
        if (element instanceof String) {
            String[] split = ((String) element).split("\\.\\.");
            if (split.length == 2) {
                int from = Integer.parseInt(split[0]);
                int to = Integer.parseInt(split[1]);
                return range(Math.min(from, to), Math.max(from, to));
            }
        }
        else if (element instanceof Integer) {
            return literal((Integer) element);
        }
        else if (element instanceof Number) {
            return chance(((Number) element).doubleValue());
        }

        throw new IllegalStateException("invalid drop chance \"" + element + "\"");
    }

    @NotNull
    public static Chance literal(int amount) {
        return new Chance(amount, 0, Type.LITERAL);
    }

    @NotNull
    public static Chance range(int min, int max) {
        return new Chance(min, max, Type.RANGE);
    }

    @NotNull
    public static Chance chance(double chance) {
        return new Chance(chance, 0, Type.CHANCE);
    }

    private enum Type {
        LITERAL,
        RANGE,
        CHANCE
    }
}
