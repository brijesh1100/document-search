package com.hevodata.core.search;

import com.hevodata.core.messaging.Document;
import com.hevodata.core.messaging.Message;
import com.hevodata.core.messaging.MessageHeaders;

/**
 * This model use for internal communication from elastcsearch and Google Drive search
 * */
public class HevoDataMessage implements Message<Document> {

	private Document document;
	private MessageHeaders header;

	@Override
	public Document getPayload() {
		return this.document;
	}

	@Override
	public MessageHeaders getHeaders() {
		return this.header;
	}

	public Document setPayload(Document document) {
		return this.document = document;
	}

	public MessageHeaders setHeader(MessageHeaders messageHeaders) {
		return this.header = messageHeaders;
	}

}
