/*
 * JSynthlib - "Additional Voice" Single Driver for Yamaha DX7-II
 * ==============================================================
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
package org.jsynthlib.synthdrivers.yamaha.dxii;

import org.jsynthlib.menu.patch.Patch;
import org.jsynthlib.menu.ui.JSLFrame;
import org.jsynthlib.synthdrivers.yamaha.dx7.common.DX7FamilyAdditionalVoiceSingleDriver;
import org.jsynthlib.synthdrivers.yamaha.dx7.common.DX7FamilyDevice;

public class YamahaDX7IIAdditionalVoiceSingleDriver extends DX7FamilyAdditionalVoiceSingleDriver {
	public YamahaDX7IIAdditionalVoiceSingleDriver() {
		super(YamahaDX7IIAdditionalVoiceConstants.INIT_ADDITIONAL_VOICE,
				YamahaDX7IIAdditionalVoiceConstants.SINGLE_ADDITIONAL_VOICE_PATCH_NUMBERS,
				YamahaDX7IIAdditionalVoiceConstants.SINGLE_ADDITIONAL_VOICE_BANK_NUMBERS);
	}

	public Patch createNewPatch() {
		return super.createNewPatch();
	}

	public JSLFrame editPatch(Patch p) {
		return super.editPatch(p);
	}

	public void storePatch(Patch p, int bankNum, int patchNum) {
		sendPatchWorker(p);

		if ((((DX7FamilyDevice) (getDevice())).getTipsMsgFlag() & 0x01) == 1)
			// show Information
			YamahaDX7IIStrings.dxShowInformation(toString(), YamahaDX7IIStrings.STORE_SINGLE_ADDITIONAL_VOICE_STRING);
	}

	public void requestPatchDump(int bankNum, int patchNum) {
		// keyswitch to voice mode
		YamahaDX7IISysexHelpers.chVoiceMode(this, (byte) (getChannel() + 0x10));
		// 0-63 int voices, 64-127 cartridge voices
		setPatchNum(patchNum + 32 * bankNum);

		send(sysexRequestDump.toSysexMessage(getChannel() + 0x20));
	}
}