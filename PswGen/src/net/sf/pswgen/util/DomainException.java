package net.sf.pswgen.util;

/******************************************************************************
 PswGen - Manages your websites and repeatably generates passwords for them

 Copyright (C) 2005-2013 Uwe Damken

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License along
 with this program; if not, write to the Free Software Foundation, Inc.,
 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *****************************************************************************/

/**
 * <p>
 * Zur möglichst einfachen Realisierung von Fachfehlern werden diese als Instanzen dieser Klasse und damit als
 * RuntimeException geworfen. Einziger Zweck ist es, einen Fehlertext zur Oberfläche durchzureichen. Ein
 * tolles Konzept ist das nicht, funktioniert aber.
 * </p>
 * <p>
 * ACHTUNG: Diese Klasse ist für PswGen und PswGenDroid identisch, sprich kopiert.
 * </p>
 * <p>
 * Copyright (C) 2005-2014 Uwe Damken
 * </p>
 */
public class DomainException extends RuntimeException {

	private static final long serialVersionUID = 7931546922671727841L;

	public DomainException(String arg0) {
		super(arg0);
	}

}
