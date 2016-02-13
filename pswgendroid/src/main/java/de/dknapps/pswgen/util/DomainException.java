/*
 * #%L
 * PswGen
 * %%
 * Copyright (C) 2005 - 2016 Uwe Damken
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package de.dknapps.pswgen.util;

/**
 * <p>
 * Zur möglichst einfachen Realisierung von Fachfehlern werden diese als Instanzen dieser Klasse und damit als
 * RuntimeException geworfen. Einziger Zweck ist es, einen Fehlertext zur Oberfläche durchzureichen. Ein
 * tolles Konzept ist das nicht, funktioniert aber.
 * </p>
 * <p>
 * ACHTUNG: Diese Klasse ist für PswGen und PswGenDroid identisch, sprich kopiert.
 * </p>
 */
public class DomainException extends RuntimeException {

	private static final long serialVersionUID = 7931546922671727841L;

	public DomainException(String arg0) {
		super(arg0);
	}

}
