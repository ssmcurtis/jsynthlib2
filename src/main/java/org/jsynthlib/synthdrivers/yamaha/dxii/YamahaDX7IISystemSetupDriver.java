/*
 * JSynthlib - "System Setup" Driver for Yamaha DX7-II
 * ===================================================
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

import org.jsynthlib.menu.JSLFrame;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.synthdrivers.yamaha.dx7.common.DX7FamilyDevice;
import org.jsynthlib.synthdrivers.yamaha.dx7.common.DX7FamilySystemSetupIIDriver;

public class YamahaDX7IISystemSetupDriver extends DX7FamilySystemSetupIIDriver {
	public YamahaDX7IISystemSetupDriver() {
		super(YamahaDX7IISystemSetupConstants.INIT_SYSTEM_SETUP,
				YamahaDX7IISystemSetupConstants.SINGLE_SYSTEM_SETUP_PATCH_NUMBERS,
				YamahaDX7IISystemSetupConstants.SINGLE_SYSTEM_SETUP_BANK_NUMBERS);

	}

	public PatchDataImpl createNewPatch() {
		return super.createNewPatch();
	}

	public JSLFrame editPatch(PatchDataImpl p) {
		return super.editPatch(p);
	}

	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		if ((((DX7FamilyDevice) (getDevice())).getSwOffMemProtFlag() & 0x01) == 1) {
			// switch off memory protection (internal+cartridge!)
			YamahaDX7IISysexHelpers.swOffMemProt(this, (byte) (getChannel() + 0x10), (byte) 0);
		} else {
			if ((((DX7FamilyDevice) (getDevice())).getTipsMsgFlag() & 0x01) == 1)
				// show Information
				YamahaDX7IIStrings.dxShowInformation(toString(), YamahaDX7IIStrings.MEMORY_PROTECTION_STRING);
		}

		sendPatchWorker(p);
	}
}
