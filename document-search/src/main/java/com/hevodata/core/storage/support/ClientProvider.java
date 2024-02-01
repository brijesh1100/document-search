package com.hevodata.core.storage.support;

public interface ClientProvider<T> {
	CloudStorageClient<T> createClient() throws Exception;
}
