package co.crystaldev.itemize.api.loot;

import de.exlll.configlib.Configuration;
import de.exlll.configlib.SerializeWith;
import de.exlll.configlib.Serializer;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * @since 0.1.0
 */
@Configuration @SerializeWith(serializer = Chance.Adapter.class)
@AllArgsConstructor @NoArgsConstructor
public final class Chance {

    public static final Chance ZERO = Chance.literal(0);

    public static final Chance ONE = Chance.literal(1);

    private final Random random = new Random();

    private double value1, value2;

    private Type type;

    public int getCount() {
        switch (this.type) {
            case RANGE: return (int) (Math.round(this.random.nextDouble() * (int) (this.value2 - this.value1)) + this.value1);
            case CHANCE: return this.random.nextDouble() < this.value1 ? 1 : 0;
            default: return (int) this.value1;
        }
    }

    public @NotNull Object serialize() {
        switch (this.type) {
            case RANGE: return ((int) this.value1) + ".." + ((int) this.value2);
            case CHANCE: return this.value1;
            default: return (int) this.value1;
        }
    }

    @Override
    public String toString() {
        switch (this.type) {
            case RANGE:
                return ((int) this.value1) + "-" + ((int) this.value2) + "x";
            case CHANCE:
                return (Math.round((this.value1 * 100.0) * 100.0) / 100.0) + "%";
            default:
                return ((int) this.value1) + "x";
        }
    }

    public static @NotNull Chance deserialize(@NotNull Object element) {
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

    public static @NotNull Chance literal(int amount) {
        return new Chance(amount, 0, Type.LITERAL);
    }

    public static @NotNull Chance range(int min, int max) {
        return new Chance(min, max, Type.RANGE);
    }

    public static @NotNull Chance chance(double chance) {
        return new Chance(chance, 0, Type.CHANCE);
    }

    private enum Type {
        LITERAL,
        RANGE,
        CHANCE
    }

    static final class Adapter implements Serializer<Chance, Object> {
        @Override
        public Object serialize(Chance element) {
            return element.serialize();
        }

        @Override
        public Chance deserialize(Object element) {
            return Chance.deserialize(element);
        }
    }
}
