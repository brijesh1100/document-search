package com.hevodata.core.search;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.hevodata.core.messaging.Document;
import com.hevodata.core.messaging.Message;
import com.hevodata.core.messaging.MessageHeaders;
import com.hevodata.core.messaging.ResponceDoc;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.Time;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;

/**
 * This class consume Message {@link HevoDataMessage}
 * and build index request, save index, raad data from index {@link ResponceDoc}
 * */
public class ElasticSearchConsumer {

	/**
	 * Message header for the Index id.
	 */
	public static final String INDEX_ID_HEADER = "INDEX_ID";

	/**
	 * Message header for the Index name.
	 */
	public static final String INDEX_NAME_HEADER = "INDEX_NAME";

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public IndexRequest buildIndexRequest(Message<?> message, ElasticsearchConsumerProperties consumerProperties)
			throws Exception {
		IndexRequest.Builder requestBuilder = new IndexRequest.Builder();

		requestBuilder.index(consumerProperties.getIndex());

		requestBuilder.id(consumerProperties.getId());

		if (message.getPayload() instanceof String) {
			requestBuilder.withJson(new StringReader((String) message.getPayload()));
		} else {
			System.out.println(message.getPayload());
			requestBuilder.document(message.getPayload());
		}

		if (consumerProperties.getTimeoutSeconds() > 0) {
			requestBuilder.timeout(new Time.Builder().time(consumerProperties.getTimeoutSeconds() + "s").build());
		}

		return requestBuilder.build();
	}

	@SuppressWarnings("unchecked")
	public void index(ElasticsearchClient elasticsearchClient, @SuppressWarnings("rawtypes") IndexRequest request,
			boolean isAsync) {
		try {
			if (isAsync) {
				ElasticsearchAsyncClient elasticsearchAsyncClient = new ElasticsearchAsyncClient(
						elasticsearchClient._transport());
				CompletableFuture<IndexResponse> responseCompletableFuture = elasticsearchAsyncClient.index(request);
				responseCompletableFuture.whenComplete((indexResponse, x) -> {
					if (x != null) {
						System.err.println("Async indexing failed: " + x.getMessage());
						x.printStackTrace();
					} else {
						handleResponse(indexResponse);
					}
				});
			} else {
				IndexResponse response = elasticsearchClient.index(request);
				handleResponse(response);
			}
		} catch (IOException e) {
			System.err.println("Error occurred while indexing document synchronously: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void handleResponse(IndexResponse response) {
		System.out.println(String.format(
				"Index operation [index=%s] succeeded: document [id=%s, version=%d] was written on shard %s.",
				response.index(), response.id(), response.version(), response.shards().toString()));
	}
	
	/**
	 * This is just search based on file content as of now.
	 * */
	public List<Message<Document>> searchRequest(ElasticsearchClient elasticsearchClient, String index, String query) {
		SearchResponse<ResponceDoc> response=null;
		List<Message<Document>> messages = new ArrayList<>();
		try {
			response = elasticsearchClient.search(s -> s
				    .index(index) 
				    .query(q -> q      
				        .match(t -> t
				        	.field("content")	
				            .query(query)
				        )
				    ),
				    ResponceDoc.class      
				);
		} catch (ElasticsearchException | IOException e) {
			e.printStackTrace();
		}
		
		List<Hit<ResponceDoc>> hits = response.hits().hits();
		
		for (Hit<ResponceDoc> hit : hits) {
			ResponceDoc responceDoc = hit.source();
			HevoDataMessage message = new HevoDataMessage();
			Document doc = new Document.DocumentBuilder()
					.name(responceDoc.getName())
					.type(responceDoc.getType())
					.build();
			message.setPayload(doc);
			Map<String, Object> map = new HashMap<>();
			map.put(MessageHeaders.ID, hit.id());
			MessageHeaders headers = new MessageHeaders(map);
			message.setHeader(headers);
			System.out.println(hit.id());
			System.out.println(responceDoc);
			messages.add(message);
		}
		return messages;
	}

	
}
