package com.hevodata.core.messaging;

import java.util.UUID;

public class SimpleIdGenerator implements IdGenerator {

	@Override
	public UUID generateId() {
		return UUID.randomUUID();
	}

}
