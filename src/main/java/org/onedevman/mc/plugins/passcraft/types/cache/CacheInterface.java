package org.onedevman.mc.plugins.passcraft.types.cache;

public interface CacheInterface<Key, Value> {

    boolean contains(Key key);

    void store(Key key, Value value);

    void store(Key key, Value value, CacheDataPersistency persistency);

    void store(Key key, Value value, CacheDataPersistency persistency, long delay);

    Value get(Key key);

    Value withdraw(Key key);

    long delay();
    long delay(long ms);

    CachePersistency persistency();
    CachePersistency persistency(CachePersistency persistent);

    //

    CacheDataPersistency persistencyOf(Key key);

    long delayOf(Key key);

    boolean expired(Key key);

    boolean expired(Key key, long delay);

    //

    void cleanup();

    void cleanup(boolean force);

}
