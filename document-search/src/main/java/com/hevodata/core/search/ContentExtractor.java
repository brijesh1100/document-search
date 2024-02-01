package com.hevodata.core.search;

import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;

import com.hevodata.core.messaging.Document;
import com.hevodata.core.messaging.Message;

/**
 * This class extract the content from Document 
 * right now we are only supporting PDF, TXT, DOCX, it can support CSV but I have not done here for demo.
 * */
public class ContentExtractor {
	
	public static void extractFromFile(final Parser parser, InputStream stream, Message<?> message, String fileName) throws Exception {

		HevoDataMessage hevoDataMessage = (HevoDataMessage) message;
		
		BodyContentHandler handler = new BodyContentHandler(100000000);
		Metadata metadata = new Metadata();

		try (FileInputStream content = (FileInputStream) stream) {
			ParseContext context = new ParseContext();
			parser.parse(content, handler, metadata, context);
		}

//		for (String name : metadata.names()) {
//			System.out.println(name + ":\t" + metadata.get(name));
//		}
		Document document = new Document.DocumentBuilder().name(fileName)
				.type(metadata.get("Content-Type"))
				.content(handler.toString())
				.build();
		hevoDataMessage.setPayload(document);
	}
    
//    public static void main(String[] args) {
//    	ContentExtractor operation = new ContentExtractor();
//		try {
//			File file = new File("C:\\Users\\Brijesh Singh\\Downloads\\file3.docx");
//			FileInputStream stream = new FileInputStream(file);
//			System.out.println(file.getName());
//			extractFromFile(new AutoDetectParser(), stream, new HevoDataMessage(), file.getName());
////			System.out.println(content);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}
