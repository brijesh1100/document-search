package com.hevodata.core.messaging;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MessageHeaders implements Map<String, Object>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * UUID for none.
	 */
	public static final UUID ID_VALUE_NONE = new UUID(0, 0);

	/**
	 * The key for the Message ID. This is an automatically generated UUID.
	 */
	public static final String ID = "id";

	private final Map<String, Object> headers;

	private static volatile IdGenerator idGenerator;

	/**
	 * The key for the message timestamp.
	 */
	public static final String TIMESTAMP = "timestamp";

	/**
	 * The key for the message content type.
	 */
	public static final String CONTENT_TYPE = "Content-Type";
	public static final String FILE_EXT = "FILE_EXT";

	private static final IdGenerator defaultIdGenerator = new SimpleIdGenerator();
	
	/**
	 * Construct a {@link MessageHeaders} with the given headers. An {@link #ID} and
	 * {@link #TIMESTAMP} headers will also be added, overriding any existing values.
	 * @param headers a map with headers to add
	 */
	public MessageHeaders(Map<String, Object> headers) {
		this(headers, null, null);
	}

	protected MessageHeaders(Map<String, Object> headers, UUID id, Long timestamp) {
		this.headers = (headers != null ? new HashMap<>(headers) : new HashMap<>());

		if (id == null) {
			this.headers.put(ID, getIdGenerator().generateId());
		} else if (id == ID_VALUE_NONE) {
			this.headers.remove(ID);
		} else {
			this.headers.put(ID, id);
		}	
		

		if (timestamp == null) {
			this.headers.put(TIMESTAMP, System.currentTimeMillis());
		} else if (timestamp < 0) {
			this.headers.remove(TIMESTAMP);
		} else {
			this.headers.put(TIMESTAMP, timestamp);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private MessageHeaders(MessageHeaders original, Set<String> keysToIgnore) {
		this.headers = new HashMap(original.headers.size());
		original.headers.forEach((key, value) -> {
			if (!keysToIgnore.contains(key)) {
				this.headers.put(key, value);
			}
		});
	}
	
	protected Map<String, Object> getRawHeaders() {
		return this.headers;
	}

	private static IdGenerator getIdGenerator() {
		IdGenerator generator = idGenerator;
		return (generator != null ? generator : defaultIdGenerator);
	}

	@Override
	public Object get(Object key) {
		return this.headers.get(key);
	}

	@Override
	public boolean isEmpty() {
		return this.headers.isEmpty();
	}

	@Override
	public Set<String> keySet() {
		return Collections.unmodifiableSet(this.headers.keySet());
	}

	@Override
	public int size() {
		return this.headers.size();
	}

	@Override
	public Collection<Object> values() {
		return Collections.unmodifiableCollection(this.headers.values());
	}


	// Unsupported Map operations

	/**
	 * Since MessageHeaders are immutable, the call to this method
	 * will result in {@link UnsupportedOperationException}.
	 */
	@Override
	public Object put(String key, Object value) {
		throw new UnsupportedOperationException("MessageHeaders is immutable");
	}

	/**
	 * Since MessageHeaders are immutable, the call to this method
	 * will result in {@link UnsupportedOperationException}.
	 */
	@Override
	public void putAll(Map<? extends String, ? extends Object> map) {
		throw new UnsupportedOperationException("MessageHeaders is immutable");
	}

	/**
	 * Since MessageHeaders are immutable, the call to this method
	 * will result in {@link UnsupportedOperationException}.
	 */
	@Override
	public Object remove(Object key) {
		throw new UnsupportedOperationException("MessageHeaders is immutable");
	}

	/**
	 * Since MessageHeaders are immutable, the call to this method
	 * will result in {@link UnsupportedOperationException}.
	 */
	@Override
	public void clear() {
		throw new UnsupportedOperationException("MessageHeaders is immutable");
	}


	// Serialization methods

	private void writeObject(ObjectOutputStream out) throws IOException {
		Set<String> keysToIgnore = new HashSet<>();
		this.headers.forEach((key, value) -> {
			if (!(value instanceof Serializable)) {
				keysToIgnore.add(key);
			}
		});

		if (keysToIgnore.isEmpty()) {
			out.defaultWriteObject();
		}
		else {
			System.out.println("Ignoring non-serializable message headers");
			out.writeObject(new MessageHeaders(this, keysToIgnore));
		}
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
	}


	// equals, hashCode, toString

	@Override
	public boolean equals(Object other) {
		return (this == other || (other instanceof MessageHeaders that && this.headers.equals(that.headers)));
	}

	@Override
	public int hashCode() {
		return this.headers.hashCode();
	}

	@Override
	public String toString() {
		return this.headers.toString();
	}

	@Override
	public boolean containsKey(Object key) {
		return this.headers.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return this.headers.containsValue(value);
	}

	@Override
	public Set<Map.Entry<String, Object>> entrySet() {
		return Collections.unmodifiableMap(this.headers).entrySet();
	}

}
