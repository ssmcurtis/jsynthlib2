/*
 * Copyright 2004 Jeff Weber
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or(at your option) any later version.
 *
 * JSynthLib is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSynthLib; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */

package org.jsynthlib.synthdrivers.alesis.dm5;

import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.jsynthlib.menu.window.PatchEditorFrame;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.widgets.CheckBoxWidget;
import org.jsynthlib.widgets.ComboBoxWidget;
import org.jsynthlib.widgets.ParamModel;
import org.jsynthlib.widgets.SysexSender;

/**
 * Alesis DM5 System Info Editor. Edits the system info patch which contains global parameters for the DM5. These
 * parameters are Omni Enable, MIDI Thru Enable, Program Change Enable, Controllers Enable, Drumset Edited, Footswitch
 * Mode, Note Chase Enable, and Currently Selected Drumset.
 * 
 * @author Jeff Weber
 */
public class AlesisDM5SysInfoEditor extends PatchEditorFrame {
	/**
	 * Size of program patch header--7 bytes.
	 */
	static final int headerSize = Constants.HDR_SIZE;

	/**
	 * Constructs a AlesisDM5SysInfoEditor for the selected patch.
	 */
	AlesisDM5SysInfoEditor(PatchDataImpl patch) {
		super("Alesis DM5 System Info Editor", patch);
		scrollPane.setLayout(new GridLayout(0, 1));
		addTrigPane(patch);
		pack();
	}

	/**
	 * Adds the System Info pane.
	 */
	private void addTrigPane(PatchDataImpl patch) {
		JPanel dm5EditPanel = new JPanel();
		dm5EditPanel.setLayout(new BoxLayout(dm5EditPanel, BoxLayout.Y_AXIS));
		dm5EditPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED), "System Info",
				TitledBorder.CENTER, TitledBorder.CENTER));
		addWidgets(dm5EditPanel, patch);
		scrollPane.add(dm5EditPanel, gbc);
	}

	/**
	 * Adds the the widgets to the System Info pane.
	 */
	private void addWidgets(JPanel panel, PatchDataImpl patch) {
		addWidget(panel, new CheckBoxWidget("Omni Enable", patch, new DM5SysInfoModel(patch, headerSize + 0, 1, true),
				new DM5SysInfoSender(patch, 1)), 0, 0, 1, 1, -1);

		addWidget(panel, new CheckBoxWidget("MIDI Thru Enable", patch, new DM5SysInfoModel(patch, headerSize + 0, 2,
				false), new DM5SysInfoSender(patch, 1)), 0, 0, 1, 1, -2);

		addWidget(panel, new CheckBoxWidget("Program Change Enable", patch, new DM5SysInfoModel(patch, headerSize + 0,
				4, false), new DM5SysInfoSender(patch, 1)), 0, 0, 1, 1, -3);

		addWidget(panel, new CheckBoxWidget("Controllers Enable", patch, new DM5SysInfoModel(patch, headerSize + 0, 8,
				false), new DM5SysInfoSender(patch, 1)), 0, 0, 1, 1, -4);

		addWidget(panel, new CheckBoxWidget("Drumset Edited", patch, new DM5SysInfoModel(patch, headerSize + 0, 16,
				false), new DM5SysInfoSender(patch, 1)), 0, 0, 1, 1, -5);

		addWidget(panel, new ComboBoxWidget("Footswitch Mode", patch, new DM5SysInfoModel(patch, headerSize + 0, 32,
				false), new DM5SysInfoSender(patch, 1), new String[] { "Hi Hat", "Drumset Advance" }), 0, 0, 1, 1, 1);

		addWidget(panel, new CheckBoxWidget("Note Chase Enable", patch, new DM5SysInfoModel(patch, headerSize + 0, 64,
				false), new DM5SysInfoSender(patch, 1)), 0, 0, 1, 1, -6);

		addWidget(panel, new ComboBoxWidget("Currently Selected Drumset", patch, new ParamModel(patch, headerSize + 2),
				new DM5SysInfoSender(patch, 1), new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
						"11", "12", "13", "14", "15", "16", "17", "18", "19", "20" }), 0, 0, 1, 1, 2);
	}

	/**
	 * Model for all parameters of the system info patch
	 */
	class DM5SysInfoModel extends ParamModel {
		public int bitmask;
		public int mult;
		public boolean reverse;

		/**
		 * Creates a new DM5SysInfoModel instance given the patch, the offset of the data within the patch, the selected
		 * bit of the data, represented by a power of two, and a boolean representing whether the values are reversed
		 * (low to high).
		 */
		public DM5SysInfoModel(PatchDataImpl p, int o, int b, boolean r) {
			super(p, o);
			bitmask = b;
			reverse = r;
			if ((bitmask & 1) == 1)
				mult = 1;
			else if ((bitmask & 2) == 2)
				mult = 2;
			else if ((bitmask & 4) == 4)
				mult = 4;
			else if ((bitmask & 8) == 8)
				mult = 8;
			else if ((bitmask & 16) == 16)
				mult = 16;
			else if ((bitmask & 32) == 32)
				mult = 32;
			else if ((bitmask & 64) == 64)
				mult = 64;
			else if ((bitmask & 128) == 128)
				mult = 128;
		}

		/**
		 * Overrides the standard set method to reverse the parameter if reverse = true and then set the selected bit to
		 * the value.
		 */
		public void set(int i) {
			if (reverse) {
				i = (i - 1) * -1;
			}
			patch.getSysex()[ofs] = (byte) ((i * mult) + (patch.getSysex()[ofs] & (~bitmask)));
		}

		/**
		 * Overrides the standard get method to get the value from the selected bit and then reverse the value if
		 * reverse = true .
		 */
		public int get() {
			int returnVal = (patch.getSysex()[ofs] & bitmask) / mult;
			if (reverse) {
				returnVal = (returnVal - 1) * -1;
			}
			return returnVal;
		}
	}

	/**
	 * A subclass of SysexSender used to send the system info patch. Since the patch is only 11 bytes, this sender just
	 * sends the whole patch.
	 */
	private class DM5SysInfoSender extends SysexSender {
		int parameter;
		PatchDataImpl patch;

		/**
		 * Constructs a DM5SysInfoSender.
		 */
		public DM5SysInfoSender(PatchDataImpl p, int param) {
			parameter = param;
			patch = p;
		}

		/**
		 * Generate method for sending entire patch.
		 */
		public byte[] generate(int value) {
			return patch.getSysex();
		}
	}
}