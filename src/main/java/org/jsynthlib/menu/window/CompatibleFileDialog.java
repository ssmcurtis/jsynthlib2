package org.jsynthlib.menu.window;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

import org.jsynthlib.JSynthConstants;
import org.jsynthlib.tools.MacUtil;

public class CompatibleFileDialog extends JFileChooser {

	protected static final Frame dummyframe = new Frame();

	public CompatibleFileDialog() {
		super();
		setPreferredSize(new Dimension(JSynthConstants.widthFileChooser, JSynthConstants.heightFileChooser));
		// UiUtil.showDetailsViewAsDefault(getComponents());
	}

	public CompatibleFileDialog(File dir) {
		super(dir);
	}

	public CompatibleFileDialog(File dir, FileSystemView fsv) {
		super(dir, fsv);
	}

	public CompatibleFileDialog(FileSystemView fsv) {
		super(fsv);
	}

	public CompatibleFileDialog(String path) {
		super(path);
	}

	public CompatibleFileDialog(String path, FileSystemView fsv) {
		super(path, fsv);
	}

	protected boolean useAWT() {
		return MacUtil.isMac();
	}

	public int showOpenDialog(Component parent) {
		if (!useAWT()) {
			return super.showOpenDialog(parent);
		} else {
			setDialogType(OPEN_DIALOG);
			return showFileDialog(parent);
		}
	}

	public int showSaveDialog(Component parent) {
		if (!useAWT()) {
			return super.showSaveDialog(parent);
		} else {
			setDialogType(SAVE_DIALOG);
			return showFileDialog(parent);
		}
	}

	public int showDialog(Component parent, String text) {
		if (!useAWT()) {
			return super.showDialog(parent, text);
		} else {
			setApproveButtonText(text);
			return showFileDialog(parent);
		}
	}

	protected int showFileDialog(Component parent) {
		try {

			if (parent == null || !(parent instanceof Frame))
				parent = dummyframe;

			String prop = "apple.awt.fileDialogForDirectories";
			Properties props = System.getProperties();
			Object oldValue = props.get(prop);

			int mode;
			if (getDialogType() == SAVE_DIALOG)
				mode = FileDialog.SAVE;
			else
				mode = FileDialog.LOAD;

			FileDialog dialog = new FileDialog((Frame) parent, getDialogTitle(), mode);

			dialog.setDirectory(getCurrentDirectory().getCanonicalPath());
			dialog.setFilenameFilter(new FilterConverter());
			if (isDirectorySelectionEnabled()) {
				props.put(prop, "true");
			}
			dialog.setVisible(true);

			if (isDirectorySelectionEnabled()) {
				// Reset the system property
				if (oldValue == null)
					props.remove(prop);
				else
					props.put(prop, oldValue);
			}

			if (dialog.getDirectory() == null || dialog.getFile() == null)
				return CANCEL_OPTION;

			setSelectedFile(new File(dialog.getDirectory(), dialog.getFile()));

			return APPROVE_OPTION;
		} catch (Throwable th) { // return ERROR_OPTION; }
			th.printStackTrace();
			// TODO ssmCurtis - system exit
			System.exit(274);
			return ERROR_OPTION;
		}
	}

	protected class FilterConverter implements FilenameFilter {
		public boolean accept(File dir, String file) {
			return CompatibleFileDialog.this.accept(new File(dir, file));
		}
	}
}
