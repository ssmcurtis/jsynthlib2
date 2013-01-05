package org.jsynthlib.menu.action;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Map;

import javax.swing.AbstractAction;

import org.jsynthlib.PatchBayApplication;
import org.jsynthlib.menu.Actions;
import org.jsynthlib.menu.window.AbstractLibraryFrame;
import org.jsynthlib.menu.window.LibraryFrame;
import org.jsynthlib.menu.window.SortDialog;
import org.jsynthlib.tools.ErrorMsgUtil;

public class DeleteDuplicateAction extends AbstractAction {
	public DeleteDuplicateAction(Map<Serializable, Integer> mnemonics) {
		super("Delete duplicates", null);
		this.setEnabled(false);
		mnemonics.put(this, new Integer('R'));
	}

	public void actionPerformed(ActionEvent e) {
		try {
			((LibraryFrame)Actions.getSelectedFrame()).deleteDuplicates();
		} catch (Exception ex) {
			ErrorMsgUtil.reportError("Error", "Library must be focused "+ getClass().getSimpleName(), ex);
		}
	}
}