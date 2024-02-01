package com.hevodata.core.messaging;

import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hevodata.core.search.ElasticSearchConsumer;

/**
 * This class map the elasticsearch result
 * {@link ElasticSearchConsumer#searchRequest(co.elastic.clients.elasticsearch.ElasticsearchClient, String, String)}
 */
public class ResponceDoc {

	private String name;
	private String content;
	private String type;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public static ResponceDoc fromJson(String json) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.readValue(json, ResponceDoc.class);
		} catch (Exception e) {
			// Handle exceptions, e.g., log the error or throw a custom exception
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(content, name, type);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResponceDoc other = (ResponceDoc) obj;
		return Objects.equals(content, other.content) && Objects.equals(name, other.name)
				&& Objects.equals(type, other.type);
	}

	@Override
	public String toString() {
		return "ResponceDoc [name=" + name + ", content=" + content + ", type=" + type + "]";
	}

}
