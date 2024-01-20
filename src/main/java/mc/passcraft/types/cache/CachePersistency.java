package mc.passcraft.types.cache;

public enum CachePersistency {
    PERSISTENT(CacheDataPersistency.PERSISTENT),
    VOLATILE(CacheDataPersistency.VOLATILE);

    private final CacheDataPersistency dataPersistency;

    CachePersistency(CacheDataPersistency data_persistency) {
        this.dataPersistency = data_persistency;
    }

    public CacheDataPersistency getDataPersistency() {
        return this.dataPersistency;
    }
}
