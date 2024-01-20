package mc.passcraft.types;

public interface Formatter<From, To> {

    To format(From input);

}