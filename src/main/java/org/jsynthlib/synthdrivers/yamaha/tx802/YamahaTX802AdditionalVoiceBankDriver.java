/*
 * JSynthlib - "Additional Voice" Bank Driver Yamaha TX802
 * =======================================================
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
import org.jsynthlib.synthdrivers.yamaha.dx7.common.DX7FamilyAdditionalVoiceBankDriver;
import org.jsynthlib.synthdrivers.yamaha.dx7.common.DX7FamilyDevice;

public class YamahaTX802AdditionalVoiceBankDriver extends DX7FamilyAdditionalVoiceBankDriver {
	public YamahaTX802AdditionalVoiceBankDriver() {
		super(YamahaTX802AdditionalVoiceConstants.INIT_ADDITIONAL_VOICE,
				YamahaTX802AdditionalVoiceConstants.BANK_ADDITIONAL_VOICE_PATCH_NUMBERS,
				YamahaTX802AdditionalVoiceConstants.BANK_ADDITIONAL_VOICE_BANK_NUMBERS);
	}

	public PatchDataImpl createNewPatch() {
		return super.createNewPatch();
	}

	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		if ((((DX7FamilyDevice) (getDevice())).getSwOffMemProtFlag() & 0x01) == 1) {
			// switch off memory protection
			YamahaTX802SysexHelpers.swOffMemProt(this, (byte) (getChannel() + 0x10));
		} else {
			if ((((DX7FamilyDevice) (getDevice())).getTipsMsgFlag() & 0x01) == 1)
				// show Information
				YamahaTX802Message.dxShowInformation(toString(), YamahaTX802Message.MEMORY_PROTECTION_STRING);
		}

		// choose the desired MIDI Receive block (internal (1-32), internal (33-64))
		// (the TX802 uses this message for both ReceiveBlock and TransmitBlock)
		YamahaTX802SysexHelpers.chBlock(this, (byte) (getChannel() + 0x10), (byte) (bankNum));

		sendPatchWorker(p);
	};

	public void requestPatchDump(int bankNum, int patchNum) {
		// choose the desired MIDI Receive block (internal (1-32), internal (33-64))
		// (the TX802 uses this message for both ReceiveBlock and TransmitBlock)
		YamahaTX802SysexHelpers.chBlock(this, (byte) (getChannel() + 0x10), (byte) (bankNum));

		send(sysexRequestDump.toSysexMessage(getChannel() + 0x20));
	}
}
