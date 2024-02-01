package com.hevodata.core.messaging;

public class Document {

	private String path;
	private String name;
	private String content;
	private FileType type;

	private Document(DocumentBuilder builder) {
		this.path = builder.path;
		this.name = builder.name;
		this.content = builder.content;
		this.type = builder.type;
	}

	public String getPath() {
		return path;
	}

	public String getName() {
		return name;
	}

	public String getContent() {
		return content;
	}

	public FileType getType() {
		return type;
	}

	public static class DocumentBuilder {

		private String path;
		private String name;
		private String content;
		private FileType type;

		public DocumentBuilder() {
		}

		public DocumentBuilder content(String content) {
			this.content = content;
			return this;
		}

		public DocumentBuilder path(String path) {
			this.path = path;
			return this;
		}

		public DocumentBuilder name(String name) {
			this.name = name;
			return this;
		}

		public DocumentBuilder type(String type) {
			if (type.toLowerCase().contains("pdf")) {
				this.type = FileType.PDF;
			} else if (type.toLowerCase().contains("office")) {
				this.type = FileType.DOCX;
			} else if (type.toLowerCase().contains("text/plain")) {
				this.type = FileType.TXT;
			} else {
				this.type = FileType.CSV;
			}
			return this;
		}

		public Document build() {
			return new Document(this);
		}
	}

	public enum FileType {
		PDF, CSV, IMAGE, TXT, DOCX
	}

}
