package org.jsynthlib.menu.window;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.jsynthlib.menu.Actions;
import org.jsynthlib.menu.preferences.AppConfig;
import org.jsynthlib.model.device.Device;
import org.jsynthlib.model.driver.SynthDriver;
import org.jsynthlib.model.driver.SynthDriverBank;
import org.jsynthlib.model.driver.SynthDriverPatch;
import org.jsynthlib.model.driver.SynthDriverPatchImpl;
import org.jsynthlib.model.patch.Patch;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.MidiUtil;
import org.jsynthlib.tools.UiUtil;

public class StoreLibraryWindow {
	private JFrame f;
	private JTextArea ta;
	private JScrollPane sbrText;
	private JButton btnQuit;
	private JButton btnCopy;
	private JButton btnProcess;
	private JPanel buttons = new JPanel(new GridLayout(3, 1));

	private Map<SynthDriverPatchImpl, Integer> supportedDevices;

	private List<String> devices = new ArrayList<>();

	public StoreLibraryWindow() {
		f = new JFrame("Store non-generic patches from library (max. 16 Patches for device)");
		f.getContentPane().setLayout(new FlowLayout());

		ta = new JTextArea("", 20, 50);
		ta.setLineWrap(true);
		sbrText = new JScrollPane(ta);
		sbrText.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		btnProcess = new JButton("Store");
		buttons.add(btnProcess);
		btnProcess.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// appendText("Supported devices:");
				supportedDevices = new HashMap<SynthDriverPatchImpl, Integer>();
				btnQuit.setEnabled(false);
				btnCopy.setEnabled(false);

				Thread worker = new Thread() {

					public void run() {
						for (int i = 0; i < AppConfig.deviceCount(); i++) {
							Device device = AppConfig.getDevice(i);

							for (int j = 0; j < device.driverCount(); j++) {
								// System.out.println(device.driverCount() + " " +
								// device.getDriver(j).getClass().getSimpleName());

								if (device.getDriver(j) instanceof SynthDriverPatchImpl) {
									SynthDriverPatchImpl driver = (SynthDriverPatchImpl) device.getDriver(j);
									if (driver.isUseableForLibrary()) {
										String keyString = driver.getDevice().getManufacturerName() + " "
												+ driver.getDevice().getModelName() + "(" + driver.getClass().getSimpleName() + ")";

										if (!devices.contains(keyString)) {
											appendText(keyString);
											devices.add(keyString);
											supportedDevices.put(driver, 0);
										}
									}
								}
							}
						}
						appendText("");
						ArrayList<Patch> patches = Actions.getSelectedFrame().getPatchCollection();
						for (Map.Entry<SynthDriverPatchImpl, Integer> entry : supportedDevices.entrySet()) {

							SynthDriverPatchImpl driver = entry.getKey();

							for (Patch p : patches) {
								if (entry.getValue() < 16) {

									if (p instanceof PatchDataImpl) {
										System.out.println(p.getClass().getSimpleName());
										PatchDataImpl patchToSend = (PatchDataImpl) p;

										boolean sameDevice = driver.getDevice().equals(patchToSend.getDevice());
										boolean patchSupported = driver.supportsPatch(p.getPatchHeader(), p.getByteArray());

										System.out.println(driver.getDevice() + " " + patchToSend.getDevice());

										System.out.println("Device " + sameDevice + " Patch " + patchSupported);

										if (sameDevice && patchSupported) {

											supportedDevices.put(driver, (entry.getValue()));

											String keyString = driver.getDevice().getManufacturerName() + " "
													+ driver.getDevice().getModelName();
											if (p.getName().trim().isEmpty() || p.getName().equals("-")) {
												appendText(keyString + " Pos: " + (entry.getValue() + 1) + ": " + p.getFileName().trim()
														+ p.getComment());
											} else {
												appendText(keyString + " Pos: " + (entry.getValue() + 1) + ": " + p.getName().trim() + " "
														+ p.getComment());
											}

											if (driver.isBankDriver()) {
												((SynthDriverBank) driver).putPatch(null, patchToSend, entry.getValue());
											} else {
												driver.storePatch(patchToSend, 0, entry.getValue());
											}

											entry.setValue(entry.getValue() + 1);

										}

									}
								} else {
									System.out.println(">> instancetype .. ");
								}
							}
						}

						for (Map.Entry<SynthDriverPatchImpl, Integer> entry : supportedDevices.entrySet()) {

							SynthDriverPatchImpl driver = entry.getKey();

							if (driver.isBankDriver()) {
								((SynthDriverBank) driver).storePatch(null, 0, 0);
							}
						}
						btnQuit.setEnabled(true);
						btnCopy.setEnabled(true);

					}

				};
				worker.start();
			}
		});

		btnCopy = new JButton("Copy to clippboard");
		buttons.add(btnCopy);
		btnCopy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Toolkit toolkit = Toolkit.getDefaultToolkit();
				Clipboard clipboard = toolkit.getSystemClipboard();
				StringSelection selection = new StringSelection(ta.getText());
				clipboard.setContents(selection, null);
			}
		});

		btnQuit = new JButton("Quit");
		buttons.add(btnQuit);
		btnQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				f.setVisible(false);
			}
		});
	}

	public void launchFrame() {
		f.getContentPane().add(sbrText);
		f.getContentPane().add(buttons);

		f.pack();
		UiUtil.centerDialog(f);
		f.setVisible(true);
	}

	public void appendText(String textToAppend) {
		ta.append(textToAppend + "\n");
		ta.setCaretPosition(ta.getText().length() - 1);
	}
}