package com.hevodata.core.storage.google;

import com.google.api.client.auth.oauth2.Credential;
import com.hevodata.core.storage.support.CloudStorageClient;
import com.hevodata.core.storage.support.ClientProvider;

public class GoogleDriveClientProvider implements ClientProvider<Credential> {

	// Volatile keyword ensures visibility across threads
	private static volatile GoogleDriveClientProvider instance;

	private GoogleDriveClientProvider() {
		// Private constructor to prevent instantiation
	}

	public static GoogleDriveClientProvider getInstance() {
		if (instance == null) {
			synchronized (GoogleDriveClientProvider.class) {
				if (instance == null) {
					instance = new GoogleDriveClientProvider();
				}
			}
		}
		return instance;
	}

	@Override
	public CloudStorageClient<Credential> createClient() throws Exception {
		return GoogleDriveClient.getInstance();
	}
}