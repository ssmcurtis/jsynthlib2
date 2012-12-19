package org.jsynthlib.menu;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.jsynthlib.Constants;
import org.jsynthlib.DeviceList;
import org.jsynthlib.menu.action.Actions;
import org.jsynthlib.menu.action.MenuDesktop;
import org.jsynthlib.menu.patch.Device;
import org.jsynthlib.menu.preferences.AppConfig;
import org.jsynthlib.menu.preferences.PrefsDialog;
import org.jsynthlib.menu.ui.JSLDesktop;
import org.jsynthlib.menu.ui.window.WaitDialog;
import org.jsynthlib.model.DevicesConfig;
import org.jsynthlib.tools.ErrorMsg;
import org.jsynthlib.tools.Utility;
import org.jsynthlib.tools.midi.MidiUtil;

/**
 * This is the main JSynthLib application object. It's called PatchEdit, which is probably ambiguous, but probably to
 * late to change now.
 * 
 * @version $Id$
 */
public final class PatchBayApplication {
	public static DevicesConfig devConfig;

	private static MenuDesktop desktop;
	private static PrefsDialog prefsDialog;
	private static WaitDialog waitDialog;

	/**
	 * Initialize Application:
	 * 
	 * @param debugLevel
	 *            debug level
	 * @param fileList
	 *            list of file name
	 */
	public PatchBayApplication(List<String> fileList, int debugLevel) {

		ErrorMsg.setDebugLevel(debugLevel);

		// for bug report
		ErrorMsg.reportStatus("JSynthLib: " + Constants.VERSION + ", Java: " + Utility.getJavaVersion() + ", OS: "
				+ Utility.getOSName() + ", " + Utility.getOSVersion());

		// Load synth database (synthdrivers.properties)
		devConfig = new DevicesConfig();

		// info
		List<Class<?>> classes = DeviceList.getClasses("org.jsynthlib.synthdrivers", Device.class);
		for (Class<?> c : classes) {
			// ErrorMsg.reportStatus(c.getSimpleName());
		}

		// Load config file (JSynthLib.properties).
		boolean loadPrefsSuccessfull = AppConfig.loadPrefs();

		// Check if all MIDI ports are available wirski@op.pl
		for (int i = 0; i < AppConfig.deviceCount(); i++) {
			try {
				MidiUtil.getInputMidiDeviceInfo(AppConfig.getDevice(i).getInPort());
				MidiUtil.getOutputMidiDeviceInfo(AppConfig.getDevice(i).getPort());
			} catch (Exception ex) {
				int ans = JOptionPane.showConfirmDialog(null, "At last one device's MIDI port is unavailable.\n"
						+ "You might verify your setup and restart the application.\n"
						+ "Press CANCEL to quit or OK to continue.", "Unavailable MIDI ports",
						JOptionPane.OK_CANCEL_OPTION);
				if (ans == JOptionPane.CANCEL_OPTION) {
					System.exit(0);
				} else {
					break;
				}
			}
		}
		// define event actions
		Actions.createAllActions();
		// Set up the GUI.
		JSLDesktop.setGUIMode(AppConfig.getGuiStyle() == AppConfig.GUI_MDI);
		desktop = new MenuDesktop("JSynthLib2", Actions.createMenuBar(), Actions.createToolBar(),
				Actions.exitAction);

		// Show dialog for the 1st invokation.
		// This is no longer normal. Maybe we shouldn't save prefs if this
		// happens (could be difficult)
		if (!loadPrefsSuccessfull)
			ErrorMsg.reportError("Error", "Unable to load user preferences. Defaults loaded instead.");

		// popup menu for Library window, etc.
		Actions.createPopupMenu();

		// set up Preference Dialog Window
		prefsDialog = new PrefsDialog(getRootFrame());

		// Set up a silly little dialog we can pop up for the user to
		// gawk at while we do time consuming work later on.
		waitDialog = new WaitDialog(getRootFrame());

		// Start pumping MIDI information from Input --> Output so the
		// user can play a MIDI Keyboard and make pretty music
		masterInEnable(AppConfig.getMasterInEnable());

		// open default library frame.
		String fname = AppConfig.getDefaultLibrary();
		if (!fname.equals("")) {
			ErrorMsg.reportStatus("default lib: " + fname);
			Actions.openFrame(new File(fname));
		}

		// open library frame specified on the command line argument.
		Iterator<String> it = fileList.iterator();
		while (it.hasNext()) {
			fname = (String) it.next();
			ErrorMsg.reportStatus("file name: " + fname);
			Actions.openFrame(new File(fname));
		}
	}

	protected void finalize() { // ???
		ErrorMsg.reportStatus("JSynthLib finalizing...");
		masterInEnable(false);
	}

	public static void showPrefsDialog() {
		prefsDialog.setVisible(true);
	}

	/**
	 * Returns the current active JFrame. Used for the <code>owner</code> parameter for <code>JDialog</code>
	 * constructor. Use this for a dialog window which depends on a frame.
	 * 
	 * @see #getRootFrame()
	 */
	public static JFrame getInstance() {
		return desktop == null ? null : desktop.getSelectedWindow();
	}

	/**
	 * Returns the root JFrame for the <code>owner</code> parameter for <code>JDialog</code> constructor. Use this for a
	 * dialog window which does not depend on a frame.
	 * 
	 * @see #getInstance()
	 */
	public static JFrame getRootFrame() {
		return desktop == null ? null : desktop.getRootFrame();
	}

	/**
	 * Returns JSLDesktop object.
	 */
	public static MenuDesktop getDesktop() {
		return desktop;
	}

	// //////////////////////////////////////////////////////////////////////
	// This allows icons to be loaded even if they are inside a Jar file
	private static ImageIcon loadIcon(String name) { // not used now
		Object icon;
		String jarName = null;
		icon = new ImageIcon(name);
		if (((ImageIcon) icon).getIconWidth() == -1) {
			jarName = new String("/");
			jarName = jarName.concat(name);
			try {
				// icon = new ImageIcon(this.getClass().getResource(jarName));
				icon = new ImageIcon(PatchBayApplication.class.getClass().getResource(jarName));
			} catch (java.lang.NullPointerException e) {
				ErrorMsg.reportStatus("ImageIcon:LoadIcon Could not find: " + name);
			}
		}
		return (ImageIcon) icon;
	}

	// //////////////////////////////////////////////////////////////////////
	public static void showWaitDialog() {
		waitDialog.showDialog("Please wait until the operation is completed.");
	}

	public static void showWaitDialog(String s) {
		waitDialog.showDialog(s);
	}

	public static void hideWaitDialog() {
		waitDialog.hideDialog();
	}

	// //////////////////////////////////////////////////////////////////////
	// MIDI Master Input
	// masterInTrans (trns) -> MasterReceiver (rcvr1) -> initPortOut(rcvr)
	private static class MasterReceiver implements Receiver {
		private Receiver rcvr;

		MasterReceiver(Receiver rcvr) {
			this.rcvr = rcvr;
		}

		// Receiver interface
		public void close() {
			// don't close a shared Receiver
			// if (rcvr != null) rcvr.close();
		}

		public void send(MidiMessage message, long timeStamp) {
			int status = message.getStatus();
			if ((0x80 <= status) && (status < 0xF0)) { // MIDI channel Voice
														// Message
				// I believe Sysex message must be ignored.
				// || status == SysexMessage.SYSTEM_EXCLUSIVE)
				ErrorMsg.reportStatus("MasterReceiver: " + message);
				this.rcvr.send(message, timeStamp);
				MidiUtil.log("RECV: ", message);
			}
		}
	}

	private static Transmitter trns;
	private static Receiver rcvr1;

	public static void masterInEnable(boolean enable) {
		if (enable) {
			// disable previous master in port if enabled.
			masterInEnable(false);
			// get transmitter
			trns = MidiUtil.getTransmitter(AppConfig.getMasterController());
			// create output receiver
			try {
				Receiver rcvr = MidiUtil.getReceiver(AppConfig.getInitPortOut());
				rcvr1 = new MasterReceiver(rcvr);
				trns.setReceiver(rcvr1);
				// ErrorMsg.reportStatus("masterInEnable.rcvr: " + rcvr);
				// ErrorMsg.reportStatus("masterInEnable.rcvr1: " + rcvr1);
				// ErrorMsg.reportStatus("masterInEnable.trns: " + trns);
			} catch (MidiUnavailableException e) {
				ErrorMsg.reportStatus(e);
			}
		} else {
			if (trns != null)
				trns.close();
			if (rcvr1 != null)
				rcvr1.close();
		}
	}
}
