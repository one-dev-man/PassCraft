package mc.passcraft.types;

import java.util.ArrayList;
import java.util.List;

public class Queue<Element, ElementKey> {

    private final List<ElementKey> queue = new ArrayList<>();

    private final Formatter<Element, ElementKey> keyformatter;

    //

    public Queue(Formatter<Element, ElementKey> keyformatter) {
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
