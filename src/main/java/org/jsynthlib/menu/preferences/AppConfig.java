/**
 * AppConfig.java - class to hold collect application configuration
 * variables in one place for easy saving and loading, and separation
 * of data from display code.  Persistent values are keeped by using
 * <code>java.util.prefs.Preferences</code>.
 * @author Zellyn Hunter (zellyn@zellyn.com)
 * @author Rib Rob
 * @author Hiroo Hayashi
 * @version $Id$
 */

package org.jsynthlib.menu.preferences;

import java.util.ArrayList;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;

import org.jsynthlib.JSynthConstants;
import org.jsynthlib.JSynthLib;
import org.jsynthlib.PatchBayApplication;
import org.jsynthlib.model.device.Device;
import org.jsynthlib.model.driver.SynthDriverPatch;
import org.jsynthlib.tools.ErrorMsgUtil;
import org.jsynthlib.tools.MacUtil;
import org.jsynthlib.tools.MidiUtil;

public class AppConfig {
	public static final int GUI_MDI = 0;
	static final int GUI_SDI = 1;

	private static ArrayList<Device> deviceList = new ArrayList<Device>();

	private static Preferences prefs = Preferences.userNodeForPackage(Object.class);
	private static Preferences prefsDev = prefs.node(JSynthLib.getStudio());

	private static int defaultLookANdFeel = 1;

	/**
	 * Initialize.
	 */
	static {
		try {
			prefs.sync();
		} catch (BackingStoreException e) {
			ErrorMsgUtil.reportStatus(e);
		}
		setLookAndFeel(getLookAndFeel());
	}

	/**
	 * Restore deviceList.
	 */
	public static boolean loadPrefs() {
		try {
			String[] devs;

			// clear studio configurations
			// for(String pd : prefs.childrenNames()) {
			// if(pd.equals("devices") || pd.equals("microkorg")) {
			// ErrorMsgUtil.reportStatus(">>>> " + pd);
			// } else {
			// ErrorMsgUtil.reportStatus("delete " + pd);
			// prefs.node(pd).removeNode();
			// }
			// }

			// debug prefs
			// devs = prefsDev.childrenNames();
			// for (int i = 0; i < devs.length; i++) {
			// if (prefsDev.nodeExists(devs[i])) {
			// Preferences.userRoot().node(devs[i]).removeNode();
			// prefsDev.removeNode();
			// }
			// }
			// prefsDev.flush();
			devs = prefsDev.childrenNames();

			// Some classes assume that the 1st driver is a Generic Driver.
			if (prefsDev.nodeExists("org.jsynthlib.synthdrivers.generic.GenericDevice#0")) {
				addDevice(JSynthConstants.SYNTLIB_CLASS_GENERIC, prefsDev.node("org.jsynthlib.synthdrivers.generic.GenericDevice#0"));
			} else {
				// create for the 1st time.
				addDevice(JSynthConstants.SYNTLIB_CLASS_GENERIC);
			}

			for (int i = 0; i < devs.length; i++) {
				if (devs[i].equals("org.jsynthlib.synthdrivers.generic.GenericDevice#0"))
					continue;
				if (devs[i].equals("Generic#0"))
					continue;

				// get class name from preferences node name
				// ErrorMsg.reportStatus("loadDevices: \"" + devs[i] + "\"");
				String className = devs[i].substring(0, devs[i].indexOf('#'));
				// ErrorMsg.reportStatus("loadDevices: -> " + s);
				// String className = PatchBayApplication.deviceConfig.getClassNameForShortName(s);
				// ErrorMsg.reportStatus("loadDevices: -> " + s);
				if (className != null) {
					addDevice(className, prefsDev.node(devs[i]));

				}
			}
			// ErrorMsg.reportStatus("deviceList: " + deviceList);
			return true;
		} catch (BackingStoreException e) {
			ErrorMsgUtil.reportStatus("loadPrefs: " + e);
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			ErrorMsgUtil.reportStatus("loadPrefs: " + e);
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * This routine just saves the current settings in the config file. Its called when the user quits the app.
	 */
	public static void savePrefs() {
		try {
			// Save the appconfig
			store();
		} catch (Exception e) {
			ErrorMsgUtil.reportError("Error", "Unable to Save Preferences", e);
		}
	}

	/**
	 * @throws BackingStoreException
	 */
	private static void store() throws BackingStoreException {
		// This shouldn't be necessary unless the jvm crashes.
		// Save prefs
		prefs.flush();
	}

	// Simple getters and setters

	/** Getter for libPath for library/scene file. */
	public static String getLibPath() {
		return prefs.get("libPath", ".");
	}

	/** Setter for libPath for library/scene file. */
	public static void setLibPath(String libPath) {
		prefs.put("libPath", libPath);
	}

	/** Getter for XML Path */
	public static String getXMLpaths() {
		return prefs.get("XMLpaths", "");
	}

	/** Setter for XML Path */
	public static void setXMLpaths(String libPath) {
		prefs.put("XMLpaths", libPath);
	}

	/** Getter for sysexPath for import/export Sysex Message. */
	public static String getSysexPath() {
		return prefs.get("sysexPath", ".");
	}

	/** Setter for sysexPath for import/export Sysex Message. */
	public static void setSysexPath(String sysexPath) {
		prefs.put("sysexPath", sysexPath);
	}

	/** Getter for default library which is open at start-up. */
	public static String getDefaultLibrary() {
		return prefs.get("defaultLib", "");
	}

	/** Setter for default library which is open at start-up. */
	public static void setDefaultLibrary(String file) {
		prefs.put("defaultLib", file);
	}

	/** Getter for sequencerEnable */
	public static boolean getSequencerEnable() {
		return prefs.getBoolean("sequencerEnable", false);
	}

	/** Setter for sequencerEnable */
	public static void setSequencerEnable(boolean sequencerEnable) {
		prefs.putBoolean("sequencerEnable", sequencerEnable);
	}

	@Deprecated
	/** Getter for midi file (Sequence) to play */
	public static String getSequencePath() {
		return prefs.get("sequencePath", "");
	}

	@Deprecated
	/** Setter for midi file (Sequence) to play */
	public static void setSequencePath(String sequencePath) {
		prefs.put("sequencePath", sequencePath);
	}

	/** Getter for note */
	public static int getNote() {
		return prefs.getInt("note", 0);
	}

	/** Setter for note */
	public static void setNote(int note) {
		prefs.putInt("note", note);
	}

	/** Getter for note */
	public static int getSequenceOrdinal() {
		return prefs.getInt("sequenceOrdinal", 0);
	}

	/** Setter for note */
	public static void setSequenceOrdinal(int sequence) {
		prefs.putInt("sequenceOrdinal", sequence);
	}

	/** Getter for note */
	public static int getBpmOrdinal() {
		return prefs.getInt("bpmOrdinal", 0);
	}

	/** Setter for note */
	public static void setBpmOrdinal(int bpm) {
		prefs.putInt("bpmOrdinal", bpm);
	}

	/** Getter for note */
	public static int getOctaveOrdinal() {
		return prefs.getInt("oactaveOrdinal", 0);
	}

	/** Setter for note */
	public static void setOctaveOrdinal(int octave) {
		prefs.putInt("oactaveOrdinal", octave);
	}

	/** Getter for note */
	public static int getLoopcount() {
		return prefs.getInt("loppCount", 0);
	}

	/** Setter for note */
	public static void setLoopcount(int count) {
		prefs.putInt("loppCount", count);
	}

	/** Getter for velocity */
	public static int getVelocity() {
		return prefs.getInt("velocity", 0);
	}

	/** Setter for velocity */
	public static void setVelocity(int velocity) {
		prefs.putInt("velocity", velocity);
	}

	/** Getter for delay */
	// TODO ssmCurtis - notelength ?
	public static int getDelay() {
		return prefs.getInt("delay", 0);
	}

	/** Setter for delay */
	public static void setDelay(int delay) {
		prefs.putInt("delay", delay);
	}

	/** Getter for RepositoryURL */
	public static String getRepositoryURL() {
		return prefs.get("repositoryURL", "http://www.jsynthlib.org");
	}

	/** Setter for RepositoryURL */
	public static void setRepositoryURL(String url) {
		prefs.put("repositoryURL", url);
	}

	/** Getter for RepositoryUser */
	public static String getRepositoryUser() {
		return prefs.get("repositoryUser", "");
	}

	/** Setter for RepositoryUser */
	public static void setRepositoryUser(String user) {
		prefs.put("repositoryUser", user);
	}

	/** Getter for RepositoryPass */
	public static String getRepositoryPass() {
		return prefs.get("repositoryPass", "");
	}

	/** Setter for RepositoryPass */
	public static void setRepositoryPass(String password) {
		prefs.put("repositoryPass", password);
	}

	/** Getter for lookAndFeel */
	public static int getLookAndFeel() {
		return prefs.getInt("lookAndFeel", defaultLookANdFeel);
	}

	/** Setter for lookAndFeel */
	public static void setLookAndFeel(int lookAndFeel) {
		// This causes dialogs and non-internal frames to be painted with the
		// look-and-feel. Emenaker 2005-06-08
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);

		prefs.putInt("lookAndFeel", lookAndFeel);
		UIManager.LookAndFeelInfo[] installedLF;
		installedLF = UIManager.getInstalledLookAndFeels();
		try {
			UIManager.setLookAndFeel(installedLF[lookAndFeel].getClassName());
		} catch (Exception e) {
			ErrorMsgUtil.reportStatus(e);
		}
	}

	/** Getter for guiStyle */
	public static int getGuiStyle() {
		return prefs.getInt("guiStyle", MacUtil.isMac() ? GUI_SDI : GUI_MDI);
	}

	/** Setter for guiStyle */
	public static void setGuiStyle(int guiStyle) {
		prefs.putInt("guiStyle", guiStyle);
	}

	/** Getter for tool bar */
	public static boolean getToolBar() {
		return prefs.getBoolean("toolBar", MacUtil.isMac());
	}

	/** Setter for tool bar */
	public static void setToolBar(boolean b) {
		prefs.putBoolean("toolBar", b);
	}

	/** Getter for tool bar */
	public static boolean getMakeTableSortable() {
		return prefs.getBoolean("makeTableSortable", false);
	}

	/** Setter for tool bar */
	public static void setMakeTableSortable(boolean b) {
		prefs.putBoolean("makeTableSortable", b);
	}

	/**
	 * Getter for midiEnable. Returns false if either MIDI input nor output is not available.
	 */
	public static boolean getMidiEnable() {
		return ((MidiUtil.isOutputAvailable() || MidiUtil.isInputAvailable()) && prefs.getBoolean("midiEnable", false));
	}

	/** Setter for midiEnable */
	public static void setMidiEnable(boolean midiEnable) {
		prefs.putBoolean("midiEnable", midiEnable);
		// ErrorMsg.reportStatus("setMidiEnable: " + midiEnable);
	}

	/** Getter for initPortIn */
	public static int getInitPortIn() {
		return prefs.getInt("initPortIn", 0);
	}

	/** Setter for initPortIn */
	public static void setInitPortIn(int initPortIn) {
		if (initPortIn < 0)
			initPortIn = 0;
		prefs.putInt("initPortIn", initPortIn);
	}

	/** Getter for initPortOut */
	public static int getInitPortOut() {
		return prefs.getInt("initPortOut", 0);
	}

	/** Setter for initPortOut */
	public static void setInitPortOut(int initPortOut) {
		if (initPortOut < 0)
			initPortOut = 0;
		prefs.putInt("initPortOut", initPortOut);
	}

	/**
	 * Getter for masterInEnable. Returns false if either MIDI input or output is unavailable.
	 */
	public static boolean getMasterInEnable() {
		return (MidiUtil.isOutputAvailable() && MidiUtil.isInputAvailable() && getMidiEnable() && prefs.getBoolean("masterInEnable", false));
	}

	/** Setter for masterInEnable */
	public static void setMasterInEnable(boolean masterInEnable) {
		PatchBayApplication.masterInEnable(masterInEnable);
		prefs.putBoolean("masterInEnable", masterInEnable);
	}

	/** Getter for masterController */
	public static int getMasterController() {
		return prefs.getInt("masterController", 0);
	}

	/** Setter for masterController */
	public static void setMasterController(int masterController) {
		prefs.putInt("masterController", masterController);
	}

	/** Getter for MIDI Output Buffer size. */
	public static int getMidiOutBufSize() {
		return prefs.getInt("midiOutBufSize", 0);
	}

	/** Setter for MIDI Output Buffer size. */
	public static void setMidiOutBufSize(int size) {
		prefs.putInt("midiOutBufSize", size);
	}

	/** Getter for MIDI Output delay time (msec). */
	public static int getMidiOutDelay() {
		return prefs.getInt("midiOutDelay", 0);
	}

	/** Setter for MIDI Output delay time (msec). */
	public static void setMidiOutDelay(int msec) {
		prefs.putInt("midiOutDelay", msec);
	}

	/**
	 * Getter for faderEnable. Returns false if MIDI input is unavailable.
	 */
	public static boolean getFaderEnable() {
		return (MidiUtil.isOutputAvailable() && getMidiEnable() && prefs.getBoolean("faderEnable", false));
	}

	/** Setter for faderEnable */
	public static void setFaderEnable(boolean faderEnable) {
		prefs.putBoolean("faderEnable", faderEnable);
	}

	/** Getter for faderPort */
	public static int getFaderPort() {
		return prefs.getInt("faderPort", 0);
	}

	/** Setter for faderPort */
	public static void setFaderPort(int faderPort) {
		prefs.putInt("faderPort", faderPort);
	}

	// int[] faderChannel (0 <= channel < 16, 16:off)
	/** Indexed getter for fader Channel number */
	public static int getFaderChannel(int i) {
		return prefs.getInt("faderChannel" + i, 0);
	}

	/** Indexed setter for fader Channel number */
	public static void setFaderChannel(int i, int faderChannel) {
		prefs.putInt("faderChannel" + i, faderChannel);
	}

	// int[] faderControl (0 <= controller < 120, 120:off)
	/** Indexed getter for fader Control number */
	public static int getFaderControl(int i) {
		int n = prefs.getInt("faderControl" + i, 0);
		return n > 120 ? 120 : n; // for old JSynthLib bug
	}

	/** Indexed setter for fader Control number. */
	public static void setFaderControl(int i, int faderControl) {
		prefs.putInt("faderControl" + i, faderControl);
	}

	/** Getter for Multiple MIDI Interface enable */
	public static boolean getMultiMIDI() {
		return prefs.getBoolean("multiMIDI", false);
	}

	/** Setter for midiEnable */
	public static void setMultiMIDI(boolean enable) {
		prefs.putBoolean("multiMIDI", enable);
	}

	public static boolean getSendPatchBeforePlay() {
		return prefs.getBoolean("sendPatchBeforePlay", false);
	}

	public static void setSendPatchBeforePlay(boolean enable) {
		prefs.putBoolean("sendPatchBeforePlay", enable);
	}

	public static boolean getImportDirectoryRecursive() {
		return prefs.getBoolean("importDirectoryRecursive", false);
	}

	public static void setImportDirectoryRecursive(boolean enable) {
		prefs.putBoolean("importDirectoryRecursive", enable);
	}

	public static boolean getAddParentDirectoryName() {
		return prefs.getBoolean("addParentDirectoryName", false);
	}

	public static void setAddParentDirectoryName(boolean enable) {
		prefs.putBoolean("addParentDirectoryName", enable);
	}

	/**
	 * Add Device into <code>deviceList</code>.
	 * 
	 * @param className
	 *            name of Device class (ex. "org.jsynthlib.drivers.KawaiK4.KawaiK4Device").
	 * @param prefs
	 *            <code>Preferences</code> node for the Device.
	 * @return a <code>Device</code> value created.
	 */
	private static Device addDevice(String className, Preferences prefs) {
		// ErrorMsgUtil.reportStatus("AppConfig add device " + className);
		
		Device device = PatchBayApplication.deviceConfig.createDevice(className, prefs);
		if (device != null) {
			device.setup();
			deviceList.add(device); // always returns true
		}
		return device;
	}

	/**
	 * Add Device into <code>deviceList</code>. A new Preferences node will be created for the Device.
	 * 
	 * @param className
	 *            name of Device class (ex. "org.jsynthlib.drivers.KawaiK4.KawaiK4Device").
	 * @return a <code>Device</code> value created.
	 */
	// Called by DeviceAddDialog and MidiScan.
	public static Device addDevice(String className) {
		return addDevice(className, getDeviceNode(className));
	}

	/** returns the 1st unused device node name for Preferences. */
	private static Preferences getDeviceNode(String s) {
		ErrorMsgUtil.reportStatus("getDeviceNode: " + s);
		// s = DevicesConfig.getShortNameForClassName(s);
		// ErrorMsg.reportStatus("getDeviceNode: -> " + s);
		int i;
		try {
			for (i = 0; prefsDev.nodeExists(s + "#" + i); i++)
				; // do nothing
			return prefsDev.node(s + "#" + i);
		} catch (BackingStoreException e) {
			ErrorMsgUtil.reportStatus(e);
			return null;
		}
	}

	/** Indexed getter for deviceList elements */
	public static Device getDevice(int i) {
		return (Device) deviceList.get(i);
	}

	/**
	 * Remover for deviceList elements. The caller must call reassignDeviceDriverNums and revalidateLibraries.
	 * 
	 * @return <code>Device</code> object removed.
	 */
	public static Device removeDevice(int i) {
		Device ret = (Device) deviceList.remove(i);
		try {
			ret.getPreferences().removeNode();
		} catch (BackingStoreException e) {
			ErrorMsgUtil.reportStatus(e);
		}
		return ret;
	}

	/** Size query for deviceList */
	public static int deviceCount() {
		return deviceList.size();
	}

	/** Getter for the index of <code>device</code>. */
	public static int getDeviceIndex(Device device) {
		return deviceList.indexOf(device);
	}

	/**
	 * Returns null driver of Generic Device. It is used when proper driver is not found.
	 */
	public static SynthDriverPatch getNullDriver() {
		return (SynthDriverPatch) getDevice(0).getDriver(0);
	}

	public static String[] getAvailableStudioSetups() {
		try {
			return prefs.childrenNames();
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new String[] {};
	}

}
