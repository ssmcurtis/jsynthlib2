package org.jsynthlib.menu.action;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JTable;

import org.jsynthlib.PatchBayApplication;
import org.jsynthlib.menu.window.LibraryFrame;
import org.jsynthlib.model.patch.Patch;
import org.jsynthlib.model.tablemodel.PatchTableModel;

public class UpdateCommentAction extends AbstractAction {
	public UpdateCommentAction(Map<Serializable, Integer> mnemonics) {
		super("Update comment *", null);
		this.setEnabled(false);
	}

	public void actionPerformed(ActionEvent e) {

		LibraryFrame libraryFrame = (LibraryFrame) PatchBayApplication.getDesktop().getSelectedFrame();
		JTable table = libraryFrame.getTable();
		PatchTableModel pm = (PatchTableModel) libraryFrame.getTable().getModel();

		int maxPatchesinTable = libraryFrame.getPatchCollection().size();
		int maxPatchNames = -1;

		for (int i = 0; i < maxPatchesinTable; i++) {

			Patch p = pm.getPatchAt(table.convertRowIndexToModel(i));
			
			if (maxPatchNames == -1) {
				// set max
				maxPatchNames = p.getDriver().getPatchNumbers().length;
			}

			if (i < maxPatchNames) {
				p.setComment(p.getDriver().getPatchNumbers()[i]);
			} else {
				p.setComment("Patch " + (i + 1));
			}
		}

		libraryFrame.getMyModel().fireTableDataChanged();
	}
}