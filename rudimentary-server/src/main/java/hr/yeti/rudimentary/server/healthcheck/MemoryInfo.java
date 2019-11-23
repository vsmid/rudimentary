package hr.yeti.rudimentary.server.healthcheck;

public class MemoryInfo {

    private long maxMemory;
    private long totalMemory;
    private long freeMemory;

    public MemoryInfo(long maxMemory, long totalMemory, long freeMemory) {
        this.maxMemory = maxMemory;
        this.totalMemory = totalMemory;
        this.freeMemory = freeMemory;
    }

    public long getMaxMemory() {
        return maxMemory;
    }

    public long getTotalMemory() {
        return totalMemory;
    }

    public long getFreeMemory() {
        return freeMemory;
    }

}
