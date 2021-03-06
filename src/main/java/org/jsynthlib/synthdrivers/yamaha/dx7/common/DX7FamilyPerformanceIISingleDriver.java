/*
 * JSynthlib -	generic "PerformanceII" Single Driver for Yamaha DX7 Family
 *		(used by DX7-II, DX7s)
 * =========================================================
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
package org.jsynthlib.synthdrivers.yamaha.dx7.common;

import org.jsynthlib.menu.JSLFrame;
import org.jsynthlib.model.driver.SynthDriverPatchImpl;
import org.jsynthlib.model.driver.SysexHandler;
import org.jsynthlib.model.patch.PatchDataImpl;

public class DX7FamilyPerformanceIISingleDriver extends SynthDriverPatchImpl {
	byte[] initSysex;
	String[] dxPatchNumbers;
	String[] dxBankNumbers;

	public DX7FamilyPerformanceIISingleDriver(byte[] initSysex, String[] dxPatchNumbers, String[] dxBankNumbers) {
		super("Single Performance", "Torsten Tittmann");

		this.initSysex = initSysex;
		this.dxPatchNumbers = dxPatchNumbers;
		this.dxBankNumbers = dxBankNumbers;

		sysexID = "F0430*7E003d4c4d2020383937335045";
		patchNameStart = 47;
		patchNameSize = 20;
		deviceIDoffset = 2;
		checksumOffset = 67;
		checksumStart = 6;
		checksumEnd = 66;
		patchNumbers = dxPatchNumbers;
		bankNumbers = dxBankNumbers;
		patchSize = 69;
		trimSize = 69;
		numSysexMsgs = 1;
		sysexRequestDump = new SysexHandler("f0 43 @@ 7e 4c 4d 20 20 38 39 37 33 50 45 f7");
	}

	public PatchDataImpl createNewPatch() {
		return new PatchDataImpl(initSysex, this);
	}

	public JSLFrame editPatch(PatchDataImpl p) {
		return new DX7FamilyPerformanceIIEditor(getManufacturerName() + " " + getModelName() + " \"" + getPatchType()
				+ "\" Editor", (PatchDataImpl) p);
	}

	public void requestPatchDump(int bankNum, int patchNum) {
		send(sysexRequestDump.toSysexMessage(getChannel() + 0x20));
	}
}
