package com.hevodata.core.messaging;

import java.util.UUID;

@FunctionalInterface
public interface IdGenerator {
	/**
	 * Generate a new identifier.
	 * @return the generated identifier
	 */
	UUID generateId();
}
