package de.dknapps.pswgencore.util;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public interface CommonJsonReaderWriterFactory {

	CommonJsonReader getJsonReader(InputStreamReader inputStreamReader);

	CommonJsonWriter getJsonWriter(OutputStreamWriter outputStreamWriter);

}
