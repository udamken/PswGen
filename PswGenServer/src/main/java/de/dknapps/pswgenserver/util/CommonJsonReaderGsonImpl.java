package de.dknapps.pswgenserver.util;

import java.io.IOException;
import java.io.InputStreamReader;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import de.dknapps.pswgencore.util.CommonJsonReader;

@SuppressWarnings("javadoc")
public class CommonJsonReaderGsonImpl implements CommonJsonReader {

	private final JsonReader reader;

	public CommonJsonReaderGsonImpl(final InputStreamReader inputStreamReader) {
		this.reader = new JsonReader(inputStreamReader);
	}

	@Override
	public boolean peekReturnsEndObject() throws IOException {
		return this.reader.peek() == JsonToken.END_OBJECT;
	}

	@Override
	public void beginArray() throws IOException {
		this.reader.beginArray();
	}

	@Override
	public void beginObject() throws IOException {
		this.reader.beginObject();
	}

	@Override
	public void close() throws IOException {
		this.reader.close();
	}

	@Override
	public void endArray() throws IOException {
		this.reader.endArray();
	}

	@Override
	public void endObject() throws IOException {
		this.reader.endObject();
	}

	@Override
	public boolean hasNext() throws IOException {
		return this.reader.hasNext();
	}

	@Override
	public int hashCode() {
		return this.reader.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return this.reader.equals(obj);
	}

	@Override
	public boolean nextBoolean() throws IOException {
		return this.reader.nextBoolean();
	}

	@Override
	public int nextInt() throws IOException {
		return this.reader.nextInt();
	}

	@Override
	public String nextName() throws IOException {
		return this.reader.nextName();
	}

	@Override
	public String nextString() throws IOException {
		return this.reader.nextString();
	}

}
