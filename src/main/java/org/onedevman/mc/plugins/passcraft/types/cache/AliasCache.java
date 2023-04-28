package org.onedevman.mc.plugins.passcraft.types.cache;

import org.onedevman.mc.plugins.passcraft.types.KeyFormatter;

public class AliasCache<Key, AliasKey, Value> implements CacheInterface<Key, Value> {

    private final KeyFormatter<Key, AliasKey> aliasformatter;

    public final Cache<AliasKey, Value> cache = new Cache<>();

    //

    public AliasCache(KeyFormatter<Key, AliasKey> aliasformatter) {
        this.aliasformatter = aliasformatter;
    }

    public AliasCache(KeyFormatter<Key, AliasKey> aliasformatter, CachePersistency persistency) {
        this(aliasformatter);
        cache.persistency(persistency);
    }

    public AliasCache(KeyFormatter<Key, AliasKey> aliasformatter, long delay) {
        this(aliasformatter);
        this.delay(delay);
    }

    //

    @Override
    public boolean contains(Key key) {
        return this.cache.contains(aliasformatter.format(key));
    }

    @Override
    public void store(Key key, Value value) {
        this.cache.store(aliasformatter.format(key), value);
    }

    @Override
    public void store(Key key, Value value, CacheDataPersistency persistency) {
        this.cache.store(aliasformatter.format(key), value, persistency);
    }

    @Override
    public void store(Key key, Value value, CacheDataPersistency persistency, long delay) {
        this.cache.store(aliasformatter.format(key), value, persistency, delay);
    }

    @Override
    public Value get(Key key) {
        return this.cache.get(aliasformatter.format(key));
    }

    @Override
    public Value withdraw(Key key) {
        return this.cache.withdraw(aliasformatter.format(key));
    }

    @Override
    public long delay() {
        return this.cache.delay();
    }

    @Override
    public long delay(long ms) {
        return this.cache.delay(ms);
    }

    @Override
    public CachePersistency persistency() {
        return this.cache.persistency();
    }

    @Override
    public CachePersistency persistency(CachePersistency persistency) {
        return this.cache.persistency(persistency);
    }

    @Override
    public CacheDataPersistency persistencyOf(Key key) {
        return this.cache.persistencyOf(aliasformatter.format(key));
    }

    @Override
    public long delayOf(Key key) {
        return this.cache.delayOf(aliasformatter.format(key));
    }

    @Override
    public boolean expired(Key key) {
        return this.cache.expired(aliasformatter.format(key));
    }

    @Override
    public boolean expired(Key key, long delay) {
        return this.cache.expired(aliasformatter.format(key), delay);
    }

    @Override
    public void cleanup() {
        this.cache.cleanup();
    }

    @Override
    public void cleanup(boolean force) {
        this.cache.cleanup(force);
    }

}
