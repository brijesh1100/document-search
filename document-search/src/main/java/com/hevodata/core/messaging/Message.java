package com.hevodata.core.messaging;

import com.hevodata.core.search.HevoDataMessage;

/**
 * This is generic message type 
 * {@link HevoDataMessage}
 * */
public interface Message<T> {
	/**
	 * Return the message payload.
	 */
	T getPayload();

	/**
	 * Return message headers for the message.
	 */
	MessageHeaders getHeaders();

}
