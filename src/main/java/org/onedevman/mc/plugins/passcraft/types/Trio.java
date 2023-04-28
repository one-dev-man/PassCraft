package org.onedevman.mc.plugins.passcraft.types;

public record Trio<A, B, C>(A one, B two, C three) {

    public static <A, B, C> Trio<A, B, C> of(A one, B two, C three) {
        return new Trio<>(one, two, three);
    }

}
