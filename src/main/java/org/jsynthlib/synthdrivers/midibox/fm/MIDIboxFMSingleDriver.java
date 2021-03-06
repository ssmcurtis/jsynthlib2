/*
 * Generic Single Patch Driver for MIDIbox FM
 * =====================================================================
 * @author  Thorsten Klose
 * @version $Id$
 *
 * Copyright (C) 2005  Thorsten.Klose@gmx.de   
 *                     http://www.uCApps.de
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.jsynthlib.synthdrivers.midibox.fm;

import javax.swing.JOptionPane;

import org.jsynthlib.PatchBayApplication;
import org.jsynthlib.model.driver.NameValue;
import org.jsynthlib.model.driver.SynthDriverPatchImpl;
import org.jsynthlib.model.driver.SysexHandler;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.DriverUtil;

public class MIDIboxFMSingleDriver extends SynthDriverPatchImpl {
	private byte sysex_type;
	private int num_patches;

	public MIDIboxFMSingleDriver(String patch_name, int _num_patches, byte _sysex_type) {
		super(patch_name, "Thorsten Klose");

		num_patches = _num_patches;
		sysex_type = _sysex_type;

		String type_digit1 = Integer.toHexString(((int) sysex_type >> 4) & 0xf);
		String type_digit0 = Integer.toHexString(((int) sysex_type >> 0) & 0xf);

		sysexID = "F000007E49**02" + type_digit1 + "*";
		sysexRequestDump = new SysexHandler("F0 00 00 7E 49 @@ 01 " + type_digit1 + type_digit0
				+ " *bankNum* *patchNum* F7");

		bankNumbers = new String[] { "A", "B", "C", "D", "E", "F", "G", "H" };
		patchNumbers = new String[num_patches];
		System.arraycopy(DriverUtil.generateNumbers(1, num_patches, "000"), 0, patchNumbers, 0, num_patches);

		patchSize = 268;

		patchNameStart = sysex_type < 0x10 ? 10 : 0;
		patchNameSize = sysex_type < 0x10 ? 16 : 0;

		deviceIDoffset = 5;

		checksumStart = 10;
		checksumEnd = 265;
		checksumOffset = 266;
	}

	public void requestPatchDump(int bankNum, int patchNum) {
		// clearMidiInBuffer(); now done by SysexGetDialog.GetActionListener.
		// setBankNum(bankNum); // not for MBFM!
		// setPatchNum(patchNum); // not for MBFM!
		if (sysexRequestDump == null) {
			JOptionPane.showMessageDialog(PatchBayApplication.getInstance(), "The " + toString()
					+ " driver does not support patch getting.\n\n" + "Please start the patch dump manually...",
					"Get Patch", JOptionPane.WARNING_MESSAGE);
		} else
			send(sysexRequestDump.toSysexMessage(getDeviceID(), new NameValue("bankNum", bankNum),
					new NameValue("patchNum", patchNum)));
	}

	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		((PatchDataImpl) p).getSysex()[5] = (byte) ((getDeviceID() - 1) & 0x7f);
		((PatchDataImpl) p).getSysex()[6] = (byte) 0x02;
		((PatchDataImpl) p).getSysex()[7] = (byte) sysex_type;
		((PatchDataImpl) p).getSysex()[8] = (byte) (bankNum);
		((PatchDataImpl) p).getSysex()[9] = (byte) (patchNum);
		sendPatchWorker(p);
		try {
			Thread.sleep(100);
		} catch (Exception e) {
		}
	}

	public void sendPatch(PatchDataImpl p) {
		((PatchDataImpl) p).getSysex()[5] = (byte) ((getDeviceID() - 1) & 0x7f);
		((PatchDataImpl) p).getSysex()[6] = (byte) 0x02;
		((PatchDataImpl) p).getSysex()[7] = (byte) sysex_type;

		sendPatchWorker(p);
	}
}
