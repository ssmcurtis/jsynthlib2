/*
 * JSynthlib - "Fractional Scaling" Bank Driver for Yamaha TX802
 * =============================================================
 * @version $Id$
 * @author  Torsten Tittmann
 *
 * Copyright (C) 2002-2004 Torsten.Tittmann@gmx.de
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
 *
 */
package org.jsynthlib.synthdrivers.yamaha.tx802;

import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.synthdrivers.yamaha.dx7.common.DX7FamilyDevice;
import org.jsynthlib.synthdrivers.yamaha.dx7.common.DX7FamilyFractionalScalingBankDriver;

public class YamahaTX802FractionalScalingBankDriver extends DX7FamilyFractionalScalingBankDriver {
	public YamahaTX802FractionalScalingBankDriver() {
		super(YamahaTX802FractionalScalingConstants.INIT_FRACTIONAL_SCALING,
				YamahaTX802FractionalScalingConstants.BANK_FRACTIONAL_SCALING_PATCH_NUMBERS,
				YamahaTX802FractionalScalingConstants.BANK_FRACTIONAL_SCALING_BANK_NUMBERS);
	}

	public PatchDataImpl createNewPatch() {
		return super.createNewPatch();
	}

	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		if ((((DX7FamilyDevice) (getDevice())).getTipsMsgFlag() & 0x01) == 1)
			// show Information
			YamahaTX802Message.dxShowInformation(toString(), YamahaTX802Message.FRACTIONAL_SCALING_CARTRIDGE_STRING);

		if ((((DX7FamilyDevice) (getDevice())).getSwOffMemProtFlag() & 0x01) == 1) {
			// switch off memory protection
			YamahaTX802SysexHelpers.swOffMemProt(this, (byte) (getChannel() + 0x10));
		} else {
			if ((((DX7FamilyDevice) (getDevice())).getTipsMsgFlag() & 0x01) == 1)
				// show Information
				YamahaTX802Message.dxShowInformation(toString(), YamahaTX802Message.MEMORY_PROTECTION_STRING);
		}

		// choose the desired MIDI Receive block ((1-32), (33-64))
		YamahaTX802SysexHelpers.chBlock(this, (byte) (getChannel() + 0x10), (byte) (bankNum));

		sendPatchWorker(p);
	};

	public void requestPatchDump(int bankNum, int patchNum) {
		if ((((DX7FamilyDevice) (getDevice())).getTipsMsgFlag() & 0x01) == 1)
			// show Information
			YamahaTX802Message.dxShowInformation(toString(), YamahaTX802Message.FRACTIONAL_SCALING_CARTRIDGE_STRING);

		// choose the desired MIDI transmit block (internal (1-32), internal (33-64))
		YamahaTX802SysexHelpers.chBlock(this, (byte) (getChannel() + 0x10), (byte) (bankNum));

		send(sysexRequestDump.toSysexMessage(getChannel() + 0x20));
	}
}
