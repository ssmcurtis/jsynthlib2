/*
 * Copyright 2004,2005 Fred Jan Kraan
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
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
/**
 * Patch Memory Driver for Roland MT32.
 *
 * @version $Id$
 */
package org.jsynthlib.synthdrivers.roland.mt32;

import org.jsynthlib.menu.JSLFrame;
import org.jsynthlib.model.driver.NameValue;
import org.jsynthlib.model.driver.SynthDriverPatchImpl;
import org.jsynthlib.model.driver.SysexHandler;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.ErrorMsgUtil;

public class RolandMT32PatchMemoryDriver extends SynthDriverPatchImpl {
	/** Header Size of the Data set DT1 message. */
	private static final int HSIZE = 5;
	/** Single Patch size */
	private static final int SSIZE = 0x0B;
	/** Definition of the Request message RQ1 */
	private static final SysexHandler SYS_REQ = new SysexHandler(
			"F0 41 10 16 11 05 *partAddrM* *partAddrL* 00 00 07 *checkSum* F7");

	public RolandMT32PatchMemoryDriver() {
		super("Patch Memory", "Fred Jan Kraan");
		sysexID = "F041**16";
		patchSize = HSIZE + SSIZE + 1;
		patchNameStart = 0;
		patchNameSize = 0;
		deviceIDoffset = 0;
		checksumStart = 5;
		checksumEnd = 10;
		checksumOffset = 0;
		// checksumStart = HSIZE;
		// checksumEnd = HSIZE + SSIZE - 2;
		// checksumOffset = HSIZE + SSIZE - 1;
		bankNumbers = new String[] { "" };
		int PMCount = 128;
		patchNumbers = new String[PMCount];
		for (int i = 0; i < PMCount; i++) {
			patchNumbers[i] = ("PMA-" + (i + 1));
		}

	}

	/*
	 * Send a patch (bulk dump system exclusive message) to MIDI device. Target should be Timbre Memory 1 - 64. TTA will
	 * do for now. The message format here is Data set DT1
	 */
	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		setBankNum(bankNum); // Control change
		sendProgramChange(patchNum); // Program change
		try {
			Thread.sleep(100);
		} catch (Exception e) {
			ErrorMsgUtil.reportStatus(e);
		}

		int patchAddr = patchNum * 0x08;
		int patAddrM = patchAddr / 0x80;
		int patAddrL = patchAddr & 0x7F;

		p.getSysex()[0] = (byte) 0xF0;
		// p.sysex[2] = (byte) 0x10;
		p.getSysex()[5] = (byte) 0x05; // point to Patch Temp Area
		p.getSysex()[6] = (byte) patAddrM;
		p.getSysex()[7] = (byte) patAddrL;

		calculateChecksum(p, HSIZE, HSIZE + SSIZE - 2, HSIZE + SSIZE - 1);

		ErrorMsgUtil.reportStatus("Store patchNum " + patchNum + " to patAddrM/L " + patAddrM + " / " + patAddrL);
		// sendPatchWorker(p);
		// send(p.sysex);
		try {
			sendPatchWorker(p);
			Thread.sleep(100);
		} catch (Exception e) {
			ErrorMsgUtil.reportStatus(e);
		}
		sendProgramChange(patchNum); // Program change
	}

	/*
	 * Send a Patch (bulk dump system exclusive message) to an edit buffer of MIDI device. Target should be Timbre Temp
	 * Area 1 - 8. The message format here is Data set DT1
	 */
	public void sendPatch(PatchDataImpl p) {

		sendPatchWorker(p);
	}

	// not used
	protected void calculateChecksum(PatchDataImpl p, int start, int end, int ofs) {
		int sum = 0;
		for (int i = start; i <= end; i++) {
			sum += p.getSysex()[i];
		}
		sum = (0 - sum) & 0x7F;
		p.getSysex()[ofs] = (byte) (sum % 128);
	}

	// New Patch has Data set format DT1
	public PatchDataImpl createNewPatch() {
		byte[] sysex = new byte[HSIZE + SSIZE + 1];
		sysex[0] = (byte) 0xF0;
		sysex[1] = (byte) 0x41;
		sysex[2] = (byte) 0x10;
		sysex[3] = (byte) 0x16;
		sysex[4] = (byte) 0x12;
		sysex[5] = (byte) 0x05;
		sysex[6] = (byte) 0x0;
		sysex[HSIZE + SSIZE] = (byte) 0xF7;
		PatchDataImpl p = new PatchDataImpl(sysex, this);
		// setPatchName(p, "New PatchM");
		calculateChecksum(p);
		return p;
	}

	public JSLFrame editPatch(PatchDataImpl p) {
		return new RolandMT32PatchMemoryEditor(p);
	}

	public void requestPatchDump(int bankNum, int patNum) {
		// The message format here is Request RQ1
		int patchAddr = patNum * 0x08;
		int patAddrH = 0x05; // Always Patch Temp Area
		int patAddrM = patchAddr / 0x80; // patchAddr >> 8
		int patAddrL = patchAddr & 0x7F;
		int patSizeH = 0x00;
		int patSizeM = 0x00;
		int patSizeL = 0x07;
		int checkSum = (0 - (patAddrH + patAddrM + patAddrL + patSizeH + patSizeM + patSizeL)) & 0x7F;
		NameValue nVs[] = new NameValue[3];
		nVs[0] = new NameValue("partAddrM", patAddrM);
		nVs[1] = new NameValue("partAddrL", patAddrL);
		nVs[2] = new NameValue("checkSum", checkSum);

		send(SYS_REQ.toSysexMessage(getChannel(), nVs));
	}
}
