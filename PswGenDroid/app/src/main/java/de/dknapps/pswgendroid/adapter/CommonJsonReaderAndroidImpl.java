/************************************************************************************
 * PswGenDesktop - Manages your websites and repeatably generates passwords for them
 * PswGenDroid - Generates your passwords managed by PswGenDesktop on your mobile
 *
 *     Copyright (C) 2005-2018 Uwe Damken
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ************************************************************************************/
package de.dknapps.pswgendroid.adapter;

import android.util.JsonReader;
import android.util.JsonToken;
import de.dknapps.pswgencore.util.CommonJsonReader;

import java.io.IOException;
import java.io.InputStreamReader;

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
