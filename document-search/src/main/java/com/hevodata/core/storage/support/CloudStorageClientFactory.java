package com.hevodata.core.storage.support;

public class CloudStorageClientFactory {

	public static <T> CloudStorageClient<T> createClient(ClientProvider<T> provider) throws Exception {
        return provider.createClient();
    }
}
