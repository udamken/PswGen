package de.dknapps.pswgencore.util;

import java.io.IOException;

public interface CommonJsonWriter {

	void setIndent(String indent) throws IOException;

	CommonJsonWriter beginObject() throws IOException;

	CommonJsonWriter name(String name) throws IOException;

	CommonJsonWriter endObject() throws IOException;

	void close() throws IOException;

	CommonJsonWriter beginArray() throws IOException;

	CommonJsonWriter endArray() throws IOException;

	CommonJsonWriter value(String value) throws IOException;

	CommonJsonWriter value(boolean value) throws IOException;

	CommonJsonWriter value(long value) throws IOException;

}
