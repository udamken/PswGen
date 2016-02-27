package de.dknapps.pswgendroid.util;

import java.io.IOException;
import java.io.InputStreamReader;

import android.util.JsonReader;
import android.util.JsonToken;
import de.dknapps.pswgencore.util.CommonJsonReader;

public class CommonJsonReaderAndroidImpl implements CommonJsonReader {

	private JsonReader reader;

	public CommonJsonReaderAndroidImpl(InputStreamReader inputStreamReader) {
		reader = new JsonReader(inputStreamReader);
	}

	@Override
	public boolean peekReturnsEndObject() throws IOException {
		return reader.peek() == JsonToken.END_OBJECT;
	}

	@Override
	public void beginArray() throws IOException {
		reader.beginArray();
	}

	@Override
	public void beginObject() throws IOException {
		reader.beginObject();
	}

	@Override
	public void close() throws IOException {
		reader.close();
	}

	@Override
	public void endArray() throws IOException {
		reader.endArray();
	}

	@Override
	public void endObject() throws IOException {
		reader.endObject();
	}

	@Override
	public boolean hasNext() throws IOException {
		return reader.hasNext();
	}

	@Override
	public int hashCode() {
		return reader.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return reader.equals(obj);
	}

	@Override
	public boolean nextBoolean() throws IOException {
		return reader.nextBoolean();
	}

	@Override
	public int nextInt() throws IOException {
		return reader.nextInt();
	}

	@Override
	public String nextName() throws IOException {
		return reader.nextName();
	}

	@Override
	public String nextString() throws IOException {
		return reader.nextString();
	}

}
