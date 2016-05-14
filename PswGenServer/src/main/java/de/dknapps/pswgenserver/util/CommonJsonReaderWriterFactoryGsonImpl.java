package de.dknapps.pswgenserver.util;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import de.dknapps.pswgencore.util.CommonJsonReader;
import de.dknapps.pswgencore.util.CommonJsonReaderWriterFactory;
import de.dknapps.pswgencore.util.CommonJsonWriter;

@SuppressWarnings("javadoc")
public class CommonJsonReaderWriterFactoryGsonImpl implements CommonJsonReaderWriterFactory {

	@Override
	public CommonJsonReader getJsonReader(final InputStreamReader inputStreamReader) {
		return new CommonJsonReaderGsonImpl(inputStreamReader);
	}

	@Override
	public CommonJsonWriter getJsonWriter(final OutputStreamWriter outputStreamWriter) {
		return new CommonJsonWriterGsonImpl(outputStreamWriter);
	}

}
