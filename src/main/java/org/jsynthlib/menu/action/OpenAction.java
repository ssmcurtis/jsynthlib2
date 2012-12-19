package org.jsynthlib.menu.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.Serializable;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.jsynthlib.menu.PatchBayApplication;
import org.jsynthlib.menu.preferences.AppConfig;
import org.jsynthlib.menu.ui.ExtensionFilter;
import org.jsynthlib.menu.ui.window.CompatibleFileDialog;
import org.jsynthlib.menu.ui.window.LibraryFrame;
import org.jsynthlib.menu.ui.window.SceneFrame;

public class OpenAction extends AbstractAction {
	static final FileFilter filter;
	static {
		String lext = LibraryFrame.FILE_EXTENSION;
		String sext = SceneFrame.FILE_EXTENSION;
		filter = new ExtensionFilter("JSynthLib Library/Scene Files (*" + lext + ", *" + sext + ")", new String[] {
				lext, sext });
	}

	public OpenAction(Map<Serializable, Integer> mnemonics) {
		super("Open...", null);
		mnemonics.put(this, new Integer('O'));
	}

	public void actionPerformed(ActionEvent e) {
		CompatibleFileDialog fc = new CompatibleFileDialog();
		fc.setCurrentDirectory(new File(AppConfig.getLibPath()));
		fc.addChoosableFileFilter(filter);
		fc.setFileFilter(filter);

		if (fc.showOpenDialog(PatchBayApplication.getInstance()) == JFileChooser.APPROVE_OPTION)
			Actions.openFrame(fc.getSelectedFile());
	}
}