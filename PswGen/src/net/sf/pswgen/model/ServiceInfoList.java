package net.sf.pswgen.model;

import java.util.Collection;
import java.util.TreeMap;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p>
 * Hält eine Zuordnung von Dienstekürzeln zu zugehörigen Informationen, die zum Generieren eines Passworts
 * nötig sind.
 * </p>
 * <p>
 * (c) 2005-2012, by Uwe Damken
 * </p>
 */
@XmlRootElement(name = "ServiceList")
public class ServiceInfoList {

	/** Die Werte aus der zu Grunde liegenden Map als Collection, JAXB kann "nur" Collections */
	@XmlElementWrapper(name = "Services")
	@XmlElement(name = "Service")
	private Collection<ServiceInfo> servicesAsCollection;

	/** Die hinter dieser ServiceInfoList liegende HashMap */
	private TreeMap<String, ServiceInfo> services = new TreeMap<String, ServiceInfo>();

	/**
	 * Ein Dienstekürzel und die zugehörigen Informationen zufügen.
	 */
	public ServiceInfo putServiceInfo(final ServiceInfo serviceInfo) {
		return services.put(serviceInfo.getServiceAbbreviation(), serviceInfo);
	}

	/**
	 * Liefert die Informationen zu einem Dienstekürzel.
	 */
	public ServiceInfo getServiceInfo(final String serviceAbbreviation) {
		return services.get(serviceAbbreviation);
	}

	/**
	 * Löscht die Informationen zu einem Dienstekürzel.
	 */
	public ServiceInfo removeServiceInfo(final String serviceAbbreviation) {
		return services.remove(serviceAbbreviation);
	}

	/**
	 * Liefert die Informationen zu allen Dienstekürzeln.
	 */
	public Collection<ServiceInfo> getServices() {
		return services.values();
	}

	/**
	 * Bereitet das Speichern dieser ServiceInfoList in eine XML-Datei vor, indem die Werte aus der Map in
	 * eine Collection gestellt werden. Der Grund dafür ist, dass JAXB nicht mit Maps, sondern "nur" mit
	 * Collections umgehen kann.
	 */
	public void prepareSave() {
		servicesAsCollection = services.values();
	}

	/**
	 * Bereitet das Laden dieser ServiceInfoList aus einer XML-Datei nach, indem die Werte aus der Collection
	 * wieder in die Map gestellt werden. Der Umweg ist nötig, weil JAXB nur mit Collections, nicht aber mit
	 * Maps umgehen kann.
	 */
	public void reinforceLoad() {
		services = new TreeMap<String, ServiceInfo>();
		for (ServiceInfo serviceInfo : servicesAsCollection) {
			putServiceInfo(serviceInfo);
		}
	}

}
