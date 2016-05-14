package de.dknapps.pswgenserver.util;

import java.io.IOException;
import java.io.OutputStreamWriter;

import com.google.gson.stream.JsonWriter;

import de.dknapps.pswgencore.util.CommonJsonWriter;

@SuppressWarnings("javadoc")
public class CommonJsonWriterGsonImpl implements CommonJsonWriter {

	private final JsonWriter writer;

	public CommonJsonWriterGsonImpl(final OutputStreamWriter outputStreamWriter) {
		this.writer = new JsonWriter(outputStreamWriter);
	}

	@Override
	public CommonJsonWriter beginArray() throws IOException {
		this.writer.beginArray();
		return this;
	}

	@Override
	public CommonJsonWriter beginObject() throws IOException {
		this.writer.beginObject();
		return this;
	}

	@Override
	public void close() throws IOException {
		this.writer.close();
	}

	@Override
	public CommonJsonWriter endArray() throws IOException {
		this.writer.endArray();
		return this;
	}

	@Override
	public CommonJsonWriter endObject() throws IOException {
		this.writer.endObject();
		return this;
	}

	@Override
	public CommonJsonWriter name(final String name) throws IOException {
		this.writer.name(name);
		return this;
	}

	@Override
	public CommonJsonWriter value(final boolean value) throws IOException {
		this.writer.value(value);
		return this;
	}

	@Override
	public CommonJsonWriter value(final long value) throws IOException {
		this.writer.value(value);
		return this;
	}

	@Override
	public CommonJsonWriter value(final String value) throws IOException {
		this.writer.value(value);
		return this;
	}

	@Override
	public void setIndent(final String indent) throws IOException {
		this.writer.setIndent(indent);
	}

}
