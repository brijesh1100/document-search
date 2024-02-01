package com.hevodata.core.search;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.tika.parser.AutoDetectParser;
import org.elasticsearch.client.RestClient;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;
import com.hevodata.core.messaging.Document;
import com.hevodata.core.messaging.Message;
import com.hevodata.core.messaging.MessageHeaders;
import com.hevodata.core.storage.google.GoogleDriveClient;
import com.hevodata.core.storage.google.GoogleDriveClientProvider;
import com.hevodata.core.storage.support.CloudStorageClient;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;

/**
 * We build our search with Google Drive This can improve further
 */
public class GoogleDriveSearchService {

	private static final String HEVODATA_SEARCH_INDEX = "hevodata_search_index";

	private static final String DRIVE_FILE_METADATA = "nextPageToken, files(id, name,driveId,contentHints,createdTime,description,fileExtension,parents)";

	private static final String FIXED_DRIVEID_FILTER = "'18Avwp7OrL3eNdaLuWNmF0hPJlWLCkEqv' in parents";

	private final ElasticSearchConsumer consumer;

	private final ESConnectorConfiguration connectors;

	private final ElasticsearchClient client;

	private CloudStorageClient<?> googleDriveClient;

	public GoogleDriveSearchService() {
		this.connectors = new ESConnectorConfiguration.Builder().withEndpoint("http://localhost:9200")
				.withUsername("elastic").withPassword("TTJmr5FTh8KU1XfCe1el").build();
		this.client = createESSearchClent();
		this.consumer = new ElasticSearchConsumer();
		this.googleDriveClient = getGoogleDriveClient();
	}

	private CloudStorageClient<?> getGoogleDriveClient() {
		GoogleDriveClientProvider provider = GoogleDriveClientProvider.getInstance();
		CloudStorageClient<?> googleDriveClient = null;
		try {
			googleDriveClient = provider.createClient();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (GoogleDriveClient) googleDriveClient;
	}

	private ElasticsearchClient createESSearchClent() {
		RestClient restClient = RestClient.builder(HttpHost.create(this.connectors.getEndpoint())).build();

		ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
		return new ElasticsearchClient(transport);
	}

	public List<Map<String, Object>> search(String content) {
		List<Message<Document>> messages = this.consumer.searchRequest(client, HEVODATA_SEARCH_INDEX, content);
		if (messages.size() > 0) {
			String query = prepareQueryGoogleDriveSearchQuery(messages);
			String finalQuery = "'18Avwp7OrL3eNdaLuWNmF0hPJlWLCkEqv' in parents and " + query;
			System.out.println(finalQuery);
			return googleDriveClient.listFiles(finalQuery);
		} else {
			Map<String, Object> map = new HashMap<>();
			List<Map<String, Object>> list = new ArrayList<>();
			map.put("message", "No Record Found");
			list.add(map);
			return list;
		}

	}

	private String prepareQueryGoogleDriveSearchQuery(List<Message<Document>> messages) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < messages.size(); i++) {
			HevoDataMessage message = (HevoDataMessage) messages.get(i);

			if (i > 0) {
				sb.append(" or ");
			}

			sb.append("name = '").append(message.getPayload().getName()).append("'");
		}

		return sb.toString();
	}

	/**
	 * This is entry for bootstrap load upload is not targeted so, this method will
	 * load the file into search use it once.
	 */
	public void sync() throws Exception {
		extractAndLoadFromResource();
	}

	/**
	 * This method will load the content and metadata of the google drive file Right
	 * now this is manual process but can be improve.
	 * 
	 * @throws Exception
	 */
	private void extractAndLoadFromResource() throws Exception {
		Drive service = getDrive();
		FileList result = service.files().list().setPageSize(10).setFields(DRIVE_FILE_METADATA)
				.setQ(FIXED_DRIVEID_FILTER).execute();
		List<com.google.api.services.drive.model.File> files = result.getFiles();
		if (files == null || files.isEmpty()) {
			System.out.println("No files found.");
		} else {
			System.out.println("Files:");
			for (com.google.api.services.drive.model.File file : files) {
				if (file.getFileExtension() != null) {
					ElasticsearchConsumerProperties consumerProperties = new ElasticsearchConsumerProperties();
					Map<String, Object> header = new HashMap<>();
					consumerProperties.setAsync(true);
					consumerProperties.setIndex(HEVODATA_SEARCH_INDEX);
					HevoDataMessage hevoMessage = new HevoDataMessage();
					URL uri = this.getClass().getClassLoader().getResource(file.getName());
					File localfile = new File(URLDecoder.decode(uri.getPath(), "UTF-8"));
					System.out.println(localfile.getName());
					System.out.println(localfile.getPath());
					FileInputStream stream = new FileInputStream(localfile);
					ContentExtractor.extractFromFile(new AutoDetectParser(), stream, hevoMessage, file.getName());
					header.put(MessageHeaders.CONTENT_TYPE, hevoMessage.getPayload().getType().name());
					header.put(MessageHeaders.FILE_EXT, file.getFileExtension());
					hevoMessage.setHeader(new MessageHeaders(header));
					consumerProperties.setId(file.getId());
					@SuppressWarnings("rawtypes")
					IndexRequest request = this.consumer.buildIndexRequest(hevoMessage, consumerProperties);
					System.out.println(request);
					this.consumer.index(client, request, consumerProperties.isAsync());
				}
			}
		}

	}

	private Drive getDrive() {
		GoogleDriveClient client = (GoogleDriveClient) this.googleDriveClient;
		Credential cred = null;
		try {
			cred = (Credential) client.getCredentials();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return client.getDrive(cred);
	}

//	public static void main(String[] args) throws Exception {
//
//		RestClient restClient = RestClient.builder(HttpHost.create("http://localhost:9200")).build();
//
//		ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
//		ElasticsearchClient client = new ElasticsearchClient(transport);
//
//		Map<String, Object> jsonMap = new HashMap<>();
//        jsonMap.put("field1", "Hi");
//        jsonMap.put("field2", "Hello Elasticsearch");
//		IndexResponse response = client
//				.index(i -> i.index("person").id("123_test").document(jsonMap));
//
//		System.out.println(response.index());
//
//		GoogleDriveSearchService service = new GoogleDriveSearchService();
////		service.sync();
//		List<Map<String,Object>> results = service.search("g");
//		System.out.println(results);
//	}

}
