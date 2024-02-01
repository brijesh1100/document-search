package com.hevodata.core.storage.support;

public class CloudStorageClientSingleton<T> {

	private static volatile CloudStorageClient<?> instance;

	private CloudStorageClientSingleton() {
		// Private constructor to prevent instantiation
	}

	public static CloudStorageClient<?> getInstance(ClientProvider<?> provider) throws Exception {
		if (instance == null) {
			synchronized (CloudStorageClientSingleton.class) {
				// Double-check to ensure that the instance is still null before creating
				if (instance == null) {
					// Lazy initialization
					instance = provider.createClient();
				}
			}
		}
		return instance;
	}
}
