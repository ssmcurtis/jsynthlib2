/*
 * Copyright 2006 Roger Westerlund
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
package org.jsynthlib.synthdrivers.roland.d10;

import static org.jsynthlib.synthdrivers.roland.d10.D10Constants.BASE_TIMBRE_MEMORY;
import static org.jsynthlib.synthdrivers.roland.d10.D10Constants.OFS_ADDRESS;
import static org.jsynthlib.synthdrivers.roland.d10.D10Constants.OFS_DEVICE_ID;
import static org.jsynthlib.synthdrivers.roland.d10.D10Constants.SIZE_HEADER_DT1;
import static org.jsynthlib.synthdrivers.roland.d10.D10Constants.SIZE_TRAILER;
import static org.jsynthlib.synthdrivers.roland.d10.D10Constants.TIMBRE_COUNT;
import static org.jsynthlib.synthdrivers.roland.d10.D10Constants.TIMBRE_SIZE;

import org.jsynthlib.model.driver.SynthDriverBank;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.synthdrivers.roland.d10.message.D10DataSetMessage;
import org.jsynthlib.synthdrivers.roland.d10.message.D10RequestMessage;
import org.jsynthlib.synthdrivers.roland.d10.message.D10TransferMessage;

public class RolandD10TimbreBankDriver extends SynthDriverBank {

	private RolandD10TimbreDriver timbreDriver;

	/**
	 * @param patchType
	 * @param authors
	 * @param numPatches
	 * @param numColumns
	 */
	public RolandD10TimbreBankDriver(RolandD10TimbreDriver timbreDriver) {
		super("Timbre Bank", "Roger Westerlund", TIMBRE_COUNT, 4);
		this.timbreDriver = timbreDriver;
		sysexID = "F041**1612";

		singleSysexID = "F041**1612";
		patchSize = 4 * (SIZE_HEADER_DT1 + SIZE_TRAILER) + TIMBRE_COUNT * TIMBRE_SIZE.getIntValue();
		deviceIDoffset = OFS_DEVICE_ID;
		checksumOffset = patchSize - SIZE_TRAILER;
		checksumStart = OFS_ADDRESS;
		checksumEnd = checksumOffset - 1;
		bankNumbers = new String[] {};
		patchNumbers = RolandD10Support.createPatchNumbers();
	}

	public PatchDataImpl createNewPatch() {
		D10TransferMessage message = new D10DataSetMessage(patchSize - (SIZE_HEADER_DT1 + SIZE_TRAILER),
				BASE_TIMBRE_MEMORY.getDataValue());
		PatchDataImpl bank = new PatchDataImpl(message.getBytes(), this);
		for (int patchNumber = 0; patchNumber < TIMBRE_COUNT; patchNumber++) {
			putPatch(bank, timbreDriver.createNewPatch(), patchNumber);
		}
		return bank;
	}

	public void requestPatchDump(int bankNumber, int patchNumber) {
		D10RequestMessage requestMessage = new D10RequestMessage(BASE_TIMBRE_MEMORY, Entity.createFromIntValue(
				TIMBRE_COUNT).multiply(TIMBRE_SIZE));
		send(requestMessage.getBytes());
	}

	public PatchDataImpl extractPatch(PatchDataImpl bank, int patchNum) {
		PatchDataImpl patch = timbreDriver.createNewPatch();
		RolandD10Support.copyPatchFromBank(patchNum, bank.getSysex(), patch.getSysex());
		return patch;
	}

	public String getPatchName(PatchDataImpl bank, int patchNum) {
		return timbreDriver.getPatchName(extractPatch(bank, patchNum));
	}

	public void setPatchName(PatchDataImpl bank, int patchNum, String name) {
		// Patch has no name in data.
	}

	public void putPatch(PatchDataImpl bank, PatchDataImpl patch, int patchNum) {
		RolandD10Support.copyPatchFromBank(patchNum, bank.getSysex(), patch.getSysex());
	}

}
