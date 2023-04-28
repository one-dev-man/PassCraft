package org.onedevman.mc.plugins.passcraft.types;

public interface KeyFormatter<ElementType, ElementKeyType> {

    ElementKeyType format(ElementType element);

}