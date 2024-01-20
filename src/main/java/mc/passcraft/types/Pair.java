package mc.passcraft.types;

public record Pair<F, L>(F first, L last) {

    public static <F, L> Pair<F, L> of(F first, L last) {
        return new Pair<>(first, last);
    }

}
