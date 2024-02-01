package com.hevodata.core.search;

public class ElasticsearchConsumerProperties {

	/**
	 * Overrides the id property on a per-message basis if the INDEX_ID header value is set.
	 */
	String id;

	/**
	 * Overrides the index property on a per-message basis if the INDEX_NAME header value is set.
	 */
	String index;

	/**
	 * Specifies the shard to route to; defaults to a hash of the document id if not provided.
	 */
	String routing;

	/**
	 * Timeout (in seconds) for the shard to be available; defaults to 1 minute if not set.
	 */
	long timeoutSeconds;

	/**
	 * Indicates whether the indexing operation is asynchronous; defaults to synchronous.
	 */
	boolean async;

	/**
	 * Number of items to index for each request; defaults to 1. For values greater than 1, the bulk indexing API is used.
	 */
	int batchSize = 1;

	/**
	 * Timeout (in milliseconds) after which the message group is flushed when bulk indexing is active.
	 * Defaults to -1, meaning no automatic flush of idle message groups occurs.
	 */
	long groupTimeout = -1L;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getRouting() {
		return routing;
	}

	public void setRouting(String routing) {
		this.routing = routing;
	}

	public long getTimeoutSeconds() {
		return timeoutSeconds;
	}

	public void setTimeoutSeconds(long timeoutSeconds) {
		this.timeoutSeconds = timeoutSeconds;
	}

	public boolean isAsync() {
		return async;
	}

	public void setAsync(boolean async) {
		this.async = async;
	}

	public int getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	public long getGroupTimeout() {
		return groupTimeout;
	}

	public void setGroupTimeout(long groupTimeout) {
		this.groupTimeout = groupTimeout;
	}
}
