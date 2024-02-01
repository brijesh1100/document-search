package com.hevodata.core.storage.google;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.hevodata.core.storage.support.CloudStorageClient;

public class GoogleDriveClient implements CloudStorageClient<Credential> {

	/**
	 * Application name.
	 */
	private static final String APPLICATION_NAME = "hevodata-test";
	/**
	 * Global instance of the JSON factory.
	 */
	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	/**
	 * Directory to store authorization tokens for this application.
	 */
	private static final String TOKENS_DIRECTORY_PATH = "tokens";

	private static final List<String> SCOPES = new ArrayList<String>();
	private static final String CREDENTIALS_FILE_PATH = "/auth/google_drive_auth.json";

	// Volatile keyword ensures visibility across threads
	private static volatile GoogleDriveClient instance;

	private final NetHttpTransport HTTP_TRANSPORT;

	// Private constructor to prevent instantiation
	private GoogleDriveClient() throws Exception {
		this.HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
	}

	public static GoogleDriveClient getInstance() {
		if (instance == null) {
			synchronized (GoogleDriveClient.class) {
				if (instance == null) {
					try {
						instance = new GoogleDriveClient();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return instance;
	}

	@Override
	public List<Map<String, Object>> listFiles(String query) {
//		String q = "'18Avwp7OrL3eNdaLuWNmF0hPJlWLCkEqv' in parents and name = 'file2' and name = 'file3'";
		List<Map<String, Object>> results = new ArrayList<>();
		try {
			FileList result = getDrive(getCredentials()).files().list().setPageSize(1000).setFields(
					"nextPageToken, files(id, name,driveId,contentHints,createdTime,description,fileExtension,parents)")
					.setQ(query).execute();

			List<File> files = result.getFiles();
			if (files == null || files.isEmpty()) {
				System.out.println("No files found.");
			} else {
				System.out.println("Files:");
				for (File file : files) {
					Map<String, Object> map = new HashMap<>();
					map.put("path", file.getParents().get(0) + "/" + file.getId());
					map.put("extension", file.getFileExtension());
					map.put("name", file.getName());
					results.add(map);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return results;
	}

	@Override
	public void uploadFile(String filePath, InputStream content) {
	}

	@Override
	public InputStream downloadFile(String filePath) {
		return null;
	}

	@Override
	public void deleteFile(String filePath) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean fileExists(String filePath) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Credential getCredentials() throws Exception {
		// Load client secrets.
		InputStream in = GoogleDriveClient.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
		if (in == null) {
			throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
		}
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				clientSecrets, DriveScopes.all())
				.setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
				.setAccessType("offline").build();
		LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
		Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
		return credential;
	}

	public Drive getDrive(Credential credentials) {
		return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credentials).setApplicationName(APPLICATION_NAME)
				.build();
	}

	@Override
	public <R> R execute(Credential operation) {
		// TODO Auto-generated method stub
		return null;
	}

//	public static void main(String[] args) {
//		try {
//			// Using Factory (Internally ensuring Singleton)
//			GoogleDriveClientProvider provider = GoogleDriveClientProvider.getInstance();
//			CloudStorageClient<?> googleDriveClient = provider.createClient();
//			Credential credentials = (Credential) googleDriveClient.getCredentials();
//			Drive service = ((GoogleDriveClient) googleDriveClient).getDrive(credentials);
//			// Use googleDriveClient and credentials as needed
//
//			FileList result = service.files().list().setPageSize(10).setFields(
//					"nextPageToken, files(id, name,driveId,contentHints,createdTime,description,fileExtension,parents)")
//					.setQ("'18Avwp7OrL3eNdaLuWNmF0hPJlWLCkEqv' in parents").execute();
//			List<File> files = result.getFiles();
//			if (files == null || files.isEmpty()) {
//				System.out.println("No files found.");
//			} else {
//				System.out.println("Files:");
//				for (File file : files) {
//					System.out.println(file.getParents());
//					System.out.println(file.getCreatedTime());
//					System.out.println(file.getFileExtension());
//					System.out.printf("%s (%s)\n", file.getName(), file.getId());
//				}
//			}
//
//		} catch (Exception e) {
//			// Handle exceptions
//			e.printStackTrace();
//		}
//	}

}
