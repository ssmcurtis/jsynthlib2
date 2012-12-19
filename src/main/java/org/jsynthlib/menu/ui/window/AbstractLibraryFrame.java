/*
 * AbstractLibraryFrame.java
 *
 * Created on 24. September 2002, 10:52
 */
package org.jsynthlib.menu.ui.window;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.MaskFormatter;

import org.jsynthlib.menu.PatchBayApplication;
import org.jsynthlib.menu.action.Actions;
import org.jsynthlib.menu.patch.IPatch;
import org.jsynthlib.menu.patch.PatchBank;
import org.jsynthlib.menu.patch.PatchBasket;
import org.jsynthlib.menu.patch.PatchSingle;
import org.jsynthlib.menu.ui.JSLFrame;
import org.jsynthlib.menu.ui.JSLFrameEvent;
import org.jsynthlib.menu.ui.JSLFrameListener;
import org.jsynthlib.menu.ui.PatchTransferHandler;
import org.jsynthlib.menu.ui.ProxyImportHandler;
import org.jsynthlib.tools.DriverUtil;
import org.jsynthlib.tools.ErrorMsg;
import org.jsynthlib.tools.midi.MidiFileImport;

/**
 * Abstract class for unified handling of Library and Scene frames.
 * 
 * @author Gerrit.Gehnen
 * @version $Id$
 */
public abstract class AbstractLibraryFrame extends MenuFrame implements PatchBasket {
	protected JTable table;
	protected PatchTableModel myModel;

	private final String TYPE;
	private PatchTransferHandler pth;
	/** Has the library been altered since it was last saved? */
	protected boolean changed = false; // wirski@op.pl
	private JLabel statusBar;
	private File filename;

	AbstractLibraryFrame(String title, String type, PatchTransferHandler pth) {
		super(PatchBayApplication.getDesktop(), title);
		TYPE = type;
		this.pth = pth;

		// ...Create the GUI and put it in the window...
		addJSLFrameListener(new MyFrameListener());

		// create Table
		myModel = createTableModel();
		createTable();

		// Create the scroll pane and add the table to it.
		final JScrollPane scrollPane = new JScrollPane(table);
		// Enable drop on scrollpane
		scrollPane.getViewport().setTransferHandler(new ProxyImportHandler(table, pth));
		// commented out by Hiroo
		// scrollPane.getVerticalScrollBar().addMouseListener(new MouseAdapter() {
		// public void mousePressed(MouseEvent e) {
		// }
		//
		// public void mouseReleased(MouseEvent e) {
		// //myModel.fireTableDataChanged();
		// }
		// });

		// Add the scroll pane to this window.
		JPanel statusPanel = new JPanel();
		statusBar = new JLabel(myModel.getRowCount() + " Patches");
		statusPanel.add(statusBar);

		// getContentPane().setLayout(new BorderLayout());
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		getContentPane().add(statusPanel, BorderLayout.SOUTH);

		// ...Then set the window size or call pack...
		setSize(800, 300); // wirski@op.pl
	}

	abstract PatchTableModel createTableModel();

	/** Before calling this method, table and myModel is setup. */
	abstract void setupColumns();

	abstract void frameActivated();

	abstract void enableActions();

	private void createTable() {
		table = new JTable(myModel);

		table.setPreferredScrollableViewportSize(new Dimension(500, 70));
		table.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					Actions.showMenuPatchPopup(table, e.getX(), e.getY());
					table.setRowSelectionInterval(table.rowAtPoint(new Point(e.getX(), e.getY())),
							table.rowAtPoint(new Point(e.getX(), e.getY())));
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					Actions.showMenuPatchPopup(table, e.getX(), e.getY());
					table.setRowSelectionInterval(table.rowAtPoint(new Point(e.getX(), e.getY())),
							table.rowAtPoint(new Point(e.getX(), e.getY())));
				}
			}

			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					PatchSingle myPatch = (PatchSingle) getSelectedPatch();
					String name = myPatch.getName();
					int nameSize = myPatch.getNameSize();
					if (myPatch.hasEditor()) {
						Actions.EditActionProc();
						changed();
					} else if (nameSize != 0) {
						final JOptionPane optionPane;
						String maskStr = "";
						for (int i = 0; i < nameSize; i++) {
							maskStr += "*";
						}
						MaskFormatter Mask = new MaskFormatter();
						try {
							Mask.setMask(maskStr);
						} catch (Exception ex) {
							ErrorMsg.reportStatus(ex);
						}
						JFormattedTextField patchName = new JFormattedTextField(Mask);
						patchName.setValue(name);
						Object[] options = { new String("OK"), new String("Cancel") };
						optionPane = new JOptionPane(patchName, JOptionPane.PLAIN_MESSAGE, JOptionPane.YES_NO_OPTION,
								null, options, options[0]);
						JDialog dialog = optionPane.createDialog(table, "Edit patch name");
						dialog.setVisible(true);
						if (optionPane.getValue() == options[0]) {
							String newName = (String) patchName.getValue();
							myPatch.setName(newName);
							changed();
						}
					}
				}
			}
		});

		// table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setTransferHandler(pth);
		table.setDragEnabled(true);

		setupColumns();

		table.getModel().addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				changed = true;
				statusBar.setText(myModel.getRowCount() + " Patches");
				enableActions();
			}
		});

		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				enableActions();
			}
		});
	}

	private class MyFrameListener implements JSLFrameListener {
		public void JSLFrameClosing(JSLFrameEvent e) {
			if (!changed)
				return;

			// close Patch/Bank Editor editing a patch in this frame.
			JSLFrame[] jList = PatchBayApplication.getDesktop().getAllFrames();
			for (int j = 0; j < jList.length; j++) {
				if (jList[j] instanceof BankEditorFrame) {
					for (int i = 0; i < myModel.getRowCount(); i++)
						if (((BankEditorFrame) (jList[j])).bankData == myModel.getPatchAt(i)) {
							jList[j].moveToFront();
							try {
								jList[j].setSelected(true);
								jList[j].setClosed(true);
							} catch (Exception e1) {
								ErrorMsg.reportStatus(e1);
							}
							break;
						}
				}
				if (jList[j] instanceof PatchEditorFrame) {
					for (int i = 0; i < myModel.getRowCount(); i++)
						if (((PatchEditorFrame) (jList[j])).p == myModel.getPatchAt(i)) {
							jList[j].moveToFront();
							try {
								jList[j].setSelected(true);
								jList[j].setClosed(true);
							} catch (Exception e1) {
								ErrorMsg.reportStatus(e1);
							}
							break;
						}
				}
			}

			if (JOptionPane.showConfirmDialog(null,
					"This " + TYPE + " may contain unsaved data.\nSave before closing?", "Unsaved Data",
					JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
				return;

			moveToFront();
			Actions.saveFrame();
		}

		public void JSLFrameOpened(JSLFrameEvent e) {
		}

		public void JSLFrameActivated(JSLFrameEvent e) {
			frameActivated();
		}

		public void JSLFrameClosed(JSLFrameEvent e) {
		}

		public void JSLFrameDeactivated(JSLFrameEvent e) {
			Actions.setEnabled(false, Actions.EN_ALL);
		}

		public void JSLFrameDeiconified(JSLFrameEvent e) {
		}

		public void JSLFrameIconified(JSLFrameEvent e) {
		}
	}

	
	// TODO ssmcurtis - import patch
	// begin PatchBasket methods
	public void importPatch(File file) throws IOException, FileNotFoundException {
		if (MidiFileImport.doImport(file)) {
			return;
		}
		FileInputStream fileIn = new FileInputStream(file);
		byte[] buffer = new byte[(int) file.length()];
		fileIn.read(buffer);
		fileIn.close();

		// ErrorMsg.reportStatus("Buffer length:" + buffer.length);
		IPatch[] patarray = DriverUtil.createPatches(buffer);
		for (int j = 0; j < patarray.length; j++) {
			if (table.getSelectedRowCount() == 0)
				myModel.addPatch(patarray[j]);
			else
				myModel.setPatchAt(patarray[j], table.getSelectedRow());
		}

		changed();
	}

	protected void changed() {
		myModel.fireTableDataChanged();
		// This is done in tableChanged for the TableModelListener
		// changed = true;
	}

	public boolean isChanged() {
		return (changed);
	}

	public void exportPatch(File file) throws IOException, FileNotFoundException {
		if (table.getSelectedRowCount() == 0) {
			ErrorMsg.reportError("Error", "No Patch Selected.");
			return;
		}
		FileOutputStream fileOut = new FileOutputStream(file);
		fileOut.write(getSelectedPatch().export());
		fileOut.close();
	}

	public void deleteSelectedPatch() {
		ErrorMsg.reportStatus("delete patch : " + table.getSelectedRowCount());
		int[] ia = table.getSelectedRows();
		// Without this we cannot delete the patch at the bottom.
		table.clearSelection();
		// delete from bottom not to change indices to be removed
		for (int i = ia.length; i > 0; i--) {
			ErrorMsg.reportStatus("i = " + ia[i - 1]);
			myModel.removeAt(ia[i - 1]);
		}
		changed();
	}

	public void copySelectedPatch() {
		pth.exportToClipboard(table, Toolkit.getDefaultToolkit().getSystemClipboard(), TransferHandler.COPY);
	}

	public void pastePatch() {
		if (pth.importData(table, Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this))) {
			changed();
		} else {
			Actions.setEnabled(false, Actions.EN_PASTE);
		}
	}

	public void pastePatch(IPatch p) {
		myModel.addPatch(p);
		changed();
	}

	public void pastePatch(IPatch p, int bankNum, int patchNum) {// added by R. Wirski
		myModel.addPatch(p, bankNum, patchNum);
		changed();
	}

	public IPatch getSelectedPatch() {
		return myModel.getPatchAt(table.getSelectedRow());
	}

	public void sendSelectedPatch() {
		((PatchSingle) getSelectedPatch()).send();
	}

	public void sendToSelectedPatch() {
		new SysexSendToDialog(getSelectedPatch());
	}

	public void reassignSelectedPatch() {
		new ReassignPatchDialog(getSelectedPatch());
		changed();
	}

	public void playSelectedPatch() {
		PatchSingle myPatch = (PatchSingle) getSelectedPatch();
		myPatch.send();
		myPatch.play();
	}

	public void storeSelectedPatch() {
		new SysexStoreDialog(getSelectedPatch(), 0, 0); // wirski@op.pl
	}

	public JSLFrame editSelectedPatch() {
		// TODO: "changed" should only be set to true if the patch was modified.
		changed = false;
		return getSelectedPatch().edit();
	}

	public ArrayList getPatchCollection() {
		return myModel.getList();
	}

	// end of PatchBasket methods

	/**
	 * @return The abstractPatchListModel as unified source of patches in all types of Libraryframes
	 */
	public PatchTableModel getPatchTableModel() {
		return myModel;
	}

	/**
	 * @return The visual table component for this Frame.
	 */
	public JTable getTable() { // for SearchDialog
		return table;
	}

	public void extractSelectedPatch() {
		if (table.getSelectedRowCount() == 0) {
			ErrorMsg.reportError("Error", "No Patch Selected.");
			return;
		}
		PatchBank myPatch = (PatchBank) getSelectedPatch();
		for (int i = 0; i < myPatch.getNumPatches(); i++) {
			PatchSingle p = myPatch.get(i);
			if (p != null)
				myModel.addPatch(p);
		}
		changed();
	}

	// for open/save/save-as actions
	public void save() throws IOException {
		PatchBayApplication.showWaitDialog("Saving " + filename + "...");
		try {
			FileOutputStream f = new FileOutputStream(filename);
			ObjectOutputStream s = new ObjectOutputStream(f);
			List li = myModel.getList();

			System.out.println(li.getClass());

			s.writeObject(myModel.getList());
			s.flush();
			s.close();
			f.close();
			changed = false;

		} catch (IOException e) {
			throw e;
		} finally {
			PatchBayApplication.hideWaitDialog();
		}
	}

	public void save(File file) throws IOException {
		filename = file;
		setTitle(file.getName());
		save();
		changed = false;
	}

	public void open(File file) throws IOException, ClassNotFoundException {

		setTitle(file.getName());
		filename = file;

		FileInputStream f = new FileInputStream(file);
		ObjectInputStream s = new ObjectInputStream(f);
		myModel.setList((ArrayList) s.readObject());
		s.close();
		f.close();

		revalidateDrivers();
		myModel.fireTableDataChanged();
		changed = false;

	}

	public abstract FileFilter getFileFilter();

	public abstract String getFileExtension();

	/**
	 * Re-assigns drivers to all patches in libraryframe. Called after new drivers are added or or removed
	 */
	public void revalidateDrivers() {
		for (int i = 0; i < myModel.getRowCount(); i++)
			chooseDriver(myModel.getPatchAt(i));
		myModel.fireTableDataChanged();
	}

	private void chooseDriver(IPatch patch) {
		patch.setDriver();
		if (patch.hasNullDriver()) {
			// Unkown patch, try to guess at least the manufacturer
			patch.setComment("Probably a " + patch.lookupManufacturer() + " Patch, Size: " + patch.getSize());
		}
	}

	// JSLFrame method
	public boolean canImport(DataFlavor[] flavors) {
		return pth.canImport(table, flavors);
	}

	int getSelectedRowCount() { // not used now
		return table.getSelectedRowCount();
	}

}