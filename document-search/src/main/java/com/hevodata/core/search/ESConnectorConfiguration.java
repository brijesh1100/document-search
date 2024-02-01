package com.hevodata.core.search;

/**
 * This class to hold the ElasticSearch Connection Configuration
 * uses {@link ElasticSearchConsumer}
 * */
public class ESConnectorConfiguration {
	private String endpoint;
	private String username;
	private String password;
	private String apiKeyId;
	private String apiKeySecret;

	public String getEndpoint() {
		return endpoint;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getApiKeyId() {
		return apiKeyId;
	}

	public String getApiKeySecret() {
		return apiKeySecret;
	}

	// Private constructor to enforce the use of the builder
	private ESConnectorConfiguration() {
	}

	// Builder class
	public static class Builder {
		private final ESConnectorConfiguration config;

		public Builder() {
			this.config = new ESConnectorConfiguration();
		}

		public Builder withEndpoint(String endpoint) {
			config.endpoint = endpoint;
			return this;
		}

		public Builder withUsername(String username) {
			config.username = username;
			return this;
		}

		public Builder withPassword(String password) {
			config.password = password;
			return this;
		}

		public Builder withApiKeyId(String apiKeyId) {
			config.apiKeyId = apiKeyId;
			return this;
		}

		public Builder withApiKeySecret(String apiKeySecret) {
			config.apiKeySecret = apiKeySecret;
			return this;
		}

		public ESConnectorConfiguration build() {
			return config;
		}
	}

	// Usage example:
//	public static void main(String[] args) {
//		ESConnectorConfiguration configuration = new ESConnectorConfiguration.Builder()
//				.withEndpoint("https://your-elasticsearch-endpoint").withUsername("your-username")
//				.withPassword("your-password").withApiKeyId("your-api-key-id").withApiKeySecret("your-api-key-secret")
//				.build();
//	}
}
