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

import android.util.JsonWriter;
import de.dknapps.pswgencore.util.CommonJsonWriter;

import java.io.IOException;
import java.io.OutputStreamWriter;

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
