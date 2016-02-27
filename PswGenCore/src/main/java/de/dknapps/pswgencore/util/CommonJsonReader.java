package de.dknapps.pswgencore.util;

import java.io.IOException;

/**
 * Common interface to CommonJsonReader for PswGenDesktop (com.google.gson.stream.JsonReader) and PswGenDroid
 * (android.util.JsonReader).
 */
public interface CommonJsonReader {

	boolean peekReturnsEndObject() throws IOException; // reader.peek() == JsonToken.END_OBJECT

	void beginObject() throws IOException;

	String nextString() throws IOException;

	void endObject() throws IOException;

	void close() throws IOException;

	void beginArray() throws IOException;

	boolean hasNext() throws IOException;

	void endArray() throws IOException;

	boolean nextBoolean() throws IOException;

	int nextInt() throws IOException;

	String nextName() throws IOException;

}
