package com.hevodata.core.storage.support;

public class CloudStorageClientManager {
	private static CloudStorageClient<?> instance;

    private CloudStorageClientManager() {
        // Private constructor to prevent instantiation
    }

    public synchronized static CloudStorageClient<?> getInstance(ClientProvider<?> provider) throws Exception {
        if (instance == null) {
            // Lazy initialization
            instance = provider.createClient();
        }
        return instance;
    }

    public static synchronized <T> CloudStorageClient<?> createClient(ClientProvider<T> provider) throws Exception {
        // The factory method is synchronized to ensure thread safety during creation
        return (CloudStorageClient<?>) getInstance(provider);
    }
}
