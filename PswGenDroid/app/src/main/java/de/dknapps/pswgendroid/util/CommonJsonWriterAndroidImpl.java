package de.dknapps.pswgendroid.util;

import java.io.IOException;
import java.io.OutputStreamWriter;

import android.util.JsonWriter;
import de.dknapps.pswgencore.util.CommonJsonWriter;

public class CommonJsonWriterAndroidImpl implements CommonJsonWriter {

	private JsonWriter writer;

	public CommonJsonWriterAndroidImpl(OutputStreamWriter outputStreamWriter) {
		writer = new JsonWriter(outputStreamWriter);
	}

	@Override
	public CommonJsonWriter beginArray() throws IOException {
		writer.beginArray();
		return this;
	}

	@Override
	public CommonJsonWriter beginObject() throws IOException {
		writer.beginObject();
		return this;
	}

	@Override
	public void close() throws IOException {
		writer.close();
	}

	@Override
	public CommonJsonWriter endArray() throws IOException {
		writer.endArray();
		return this;
	}

	@Override
	public CommonJsonWriter endObject() throws IOException {
		writer.endObject();
		return this;
	}

	@Override
	public CommonJsonWriter name(String name) throws IOException {
		writer.name(name);
		return this;
	}

	@Override
	public CommonJsonWriter value(boolean value) throws IOException {
		writer.value(value);
		return this;
	}

	@Override
	public CommonJsonWriter value(long value) throws IOException {
		writer.value(value);
		return this;
	}

	@Override
	public CommonJsonWriter value(String value) throws IOException {
		writer.value(value);
		return this;
	}

	@Override
	public void setIndent(String indent) throws IOException {
		writer.setIndent(indent);
	}

}
