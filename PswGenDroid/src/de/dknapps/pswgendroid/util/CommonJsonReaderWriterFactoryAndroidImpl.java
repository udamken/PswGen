package de.dknapps.pswgendroid.util;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import de.dknapps.pswgencore.util.CommonJsonReader;
import de.dknapps.pswgencore.util.CommonJsonReaderWriterFactory;
import de.dknapps.pswgencore.util.CommonJsonWriter;

public class CommonJsonReaderWriterFactoryAndroidImpl implements CommonJsonReaderWriterFactory {

	@Override
	public CommonJsonReader getJsonReader(InputStreamReader inputStreamReader) {
		return new CommonJsonReaderAndroidImpl(inputStreamReader);
	}

	@Override
	public CommonJsonWriter getJsonWriter(OutputStreamWriter outputStreamWriter) {
		return new CommonJsonWriterAndroidImpl(outputStreamWriter);
	}

}
