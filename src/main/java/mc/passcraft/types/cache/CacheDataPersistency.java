package mc.passcraft.types.cache;

public enum CacheDataPersistency {
    CACHE_RELATIVE(cache -> cache.persistency().getDataPersistency()),
    VOLATILE(null),
    PERSISTENT(null);

    private interface PersistencyGetter {
        CacheDataPersistency get(Cache<?, ?> cache);
    }

    private final PersistencyGetter getter;

    CacheDataPersistency(PersistencyGetter getter) {
        this.getter = getter;
    }

    public CacheDataPersistency get(Cache<?, ?> cache) {
        if(this.getter == null)
            return this;

        return this.getter.get(cache);
    }
}
