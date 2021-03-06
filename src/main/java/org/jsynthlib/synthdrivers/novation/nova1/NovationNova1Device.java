/*
 * $Id$
 * NovationNovaDevice.java
 *
 * Created on 10. Oktober 2001, 22:09
 *
 * @version $Id$
 *
 */

package org.jsynthlib.synthdrivers.novation.nova1;

import java.util.prefs.Preferences;

import org.jsynthlib.model.device.Device;

public class NovationNova1Device extends Device {
	/** Creates new NovationNovaDevice */
	public NovationNova1Device() {
		super("Novation", "Nova1", "F07E..06020020290100210020000000F7", null, "Yves Lefebvre");
	}

	/** Constructor for for actual work. */
	public NovationNova1Device(Preferences prefs) {
		this();
		this.prefs = prefs;

		addDriver(new NovationNova1BankDriver());
		addDriver(new NovationNova1SingleDriver());
		addDriver(new NovationNova1SinglePerformanceDriver());
	}
}
