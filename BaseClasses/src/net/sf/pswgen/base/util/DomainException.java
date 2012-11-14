package net.sf.pswgen.base.util;

/**
 * <p>
 * Zur möglichst einfachen Realisierung von Fachfehlern werden diese als Instanzen dieser Klasse und damit als
 * RuntimeException geworfen. Einziger Zweck ist es, einen Fehlertext zur Oberfläche durchzureichen. Ein
 * tolles Konzept ist das nicht, funktioniert aber.
 * </p>
 * <p>
 * (c) 2010-2012, by Uwe Damken
 * </p>
 */
public class DomainException extends RuntimeException {

	private static final long serialVersionUID = 7931546922671727841L;

	public DomainException(String arg0) {
		super(arg0);
	}

}
