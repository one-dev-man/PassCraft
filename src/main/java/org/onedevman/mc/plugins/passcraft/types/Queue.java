package org.onedevman.mc.plugins.passcraft.types;

import org.onedevman.mc.plugins.passcraft.PluginMain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Queue<Element, ElementKey> {

    private final List<ElementKey> queue = new ArrayList<>();

    private final KeyFormatter<Element, ElementKey> keyformatter;

    //

    public Queue(KeyFormatter<Element, ElementKey> keyformatter) {
        this.keyformatter = keyformatter;
    }

    //

    public int size() { return this.queue.size(); }

    //

    public boolean contains(Element element) {
        ElementKey key = keyformatter.format(element);
        return this.queue.contains(key);
    }

    public void add(Element element) {
        ElementKey key = this.keyformatter.format(element);
        this.remove(element);
        this.queue.add(key);
    }

    public boolean remove(Element element) {
        ElementKey key = this.keyformatter.format(element);
        this.queue.remove(key);
        return this.contains(element);
    }

}
