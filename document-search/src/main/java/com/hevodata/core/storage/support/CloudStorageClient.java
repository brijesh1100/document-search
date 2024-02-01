package com.hevodata.core.storage.support;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface CloudStorageClient<T> {

	/**
	 * @param query can be file name file path or id based on implementation
	 */
	List<Map<String, Object>> listFiles(String query);

	// Upload a file
	void uploadFile(String filePath, InputStream content);

	// Download a file
	InputStream downloadFile(String filePath);

	// Delete a file
	void deleteFile(String filePath);

	// Check if a file exists
	boolean fileExists(String filePath);

	T getCredentials() throws Exception;

	// Additional generic method for provider-specific operations
	<R> R execute(T operation);
}
