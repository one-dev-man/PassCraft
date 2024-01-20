package mc.passcraft.types.cache;

import java.util.HashMap;
import java.util.Map;

public class Cache<Key, Value> implements CacheInterface<Key, Value> {

    public static long TIMESTAMP() { return System.currentTimeMillis(); }

    public static class DELAY {

        public static long MILLISECONDS(long count) { return count; }
        public static long SECONDS(long count) { return count * 1000; }
        public static long MINUTES(long count) { return count * 60 * 1000; }
        public static long HOURS(long count) { return count * 60 * 60 * 1000; }

    }

    //

    private record CacheData<Value>(long timestamp, CacheDataPersistency persistency, long delay, Value value) {

        public static <Value> CacheData<Value> of(long timestamp, CacheDataPersistency persistency, long delay, Value value) {
            return new CacheData<>(timestamp, persistency, delay, value);
        }

    }

    //

    private final Map<Key, CacheData<Value>> _cache = new HashMap<>();

    private long _delay = 0;

    private CachePersistency _persistency = CachePersistency.VOLATILE;

    //

    public Cache() {

    }

    public Cache(CachePersistency persistency) {
        this.persistency(persistency);
    }

    public Cache(long delay) {
        this.delay(delay);
    }

    //

    public boolean contains(Key key) {
        return this.contains(key, true);
    }

    private boolean contains(Key key, boolean cleanup) {
        if(cleanup)
            this.cleanup();

        return this._cache.containsKey(key);
    }

    //

    public void store(Key key, Value value) {
        this.store(key, value, true);
    }

    private void store(Key key, Value value, boolean cleanup) {
        this.store(key, value, this.persistency().getDataPersistency(), cleanup);
    }

    public void store(Key key, Value value, CacheDataPersistency persistency) {
        this.store(key, value, persistency, true);
    }

    private void store(Key key, Value value, CacheDataPersistency persistency, boolean cleanup) {
        this.store(key, value, persistency, -1, cleanup);
    }

    public void store(Key key, Value value, CacheDataPersistency persistency, long delay) {
        this.store(key, value, persistency, delay, true);
    }

    private void store(Key key, Value value, CacheDataPersistency persistency, long delay, boolean cleanup) {
        this.withdraw(key, cleanup);
        this._cache.put(key, CacheData.of(Cache.TIMESTAMP(), persistency, delay, value));
    }

    //

    public Value get(Key key) {
        return this.get(key, true);
    }

    private Value get(Key key, boolean cleanup) {
        return this.contains(key, cleanup) ? this._cache.get(key).value() : null;
    }

    //

    public Value withdraw(Key key) {
        return this.withdraw(key, true);
    }

    private Value withdraw(Key key, boolean cleanup) {
        return this.contains(key, cleanup) ? this._cache.remove(key).value() : null;
    }

    //

    public long delay() { return this._delay; }
    public long delay(long ms) { this._delay = ms; return this.delay(); }

    public CachePersistency persistency() { return this._persistency; }
    public CachePersistency persistency(CachePersistency persistency) { this._persistency = persistency; return this.persistency(); }

    //

    public CacheDataPersistency persistencyOf(Key key) {
        return this.persistencyOf(key, true);
    }

    private CacheDataPersistency persistencyOf(Key key, boolean cleanup) {
        if(!this.contains(key, cleanup))
            return null;

        return this._cache.get(key).persistency().get(this);
    }

    //

    public long delayOf(Key key) {
        return this.delayOf(key, true);
    }

    private long delayOf(Key key, boolean cleanup) {
        if(!this.contains(key, cleanup))
            return 0;

        long delay = this._cache.get(key).delay();

        if(delay < 0)
            return this.delay();

        return delay;
    }

    //

    public boolean expired(Key key) {
        return this.expired(key, true);
    }

    private boolean expired(Key key, boolean cleanup) {
        return this.expired(key, this.delayOf(key, cleanup), cleanup);
    }

    //

    public boolean expired(Key key, long delay) {
        return this.expired(key, delay, true);
    }

    private boolean expired(Key key, long delay, boolean cleanup) {
        if(!this.contains(key, cleanup))
            return true;

        return (Cache.TIMESTAMP() - this._cache.get(key).timestamp()) > delay;
    }

    //

    public void cleanup() {
        cleanup(false);
    }

    public void cleanup(boolean force) {
        for(Key key : this._cache.keySet())
            if(this.expired(key, false) && (this.persistencyOf(key, false).equals(CacheDataPersistency.VOLATILE) || force))
                this.withdraw(key, false);
    }

}