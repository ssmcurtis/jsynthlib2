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
 * Timbre Temp Driver for Roland MT32.
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

public class RolandMT32TimbreTempDriver extends SynthDriverPatchImpl {
	/** Header Size of the Data set DT1 message. */
	private static final int HSIZE = 8;
	/** Single Patch size */
	private static final int SSIZE = 247;
	/** Definition of the Request message RQ1 */
	private static final SysexHandler SYS_REQ = new SysexHandler(
			"F0 41 10 16 11 04 *partAddrM* *partAddrL* 00 01 76 *checkSum* F7");

	public RolandMT32TimbreTempDriver() {
		super("Timbre Temp", "Fred Jan Kraan");
		sysexID = "F041**16";
		patchSize = HSIZE + SSIZE + 1;
		patchNameStart = HSIZE;
		patchNameSize = 10;
		deviceIDoffset = 2;
		checksumStart = 5;
		checksumEnd = 10;
		checksumOffset = 0;
		bankNumbers = new String[] { "" };
		patchNumbers = new String[] { "TTA-1", "TTA-2", "TTA-3", "TTA-4", "TTA-5", "TTA-6", "TTA-7", "TTA-8" };
	}

	/*
	 * Send a patch (bulk dump system exclusive message) to MIDI device. The message format here is Data set DT1
	 */
	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		setBankNum(bankNum); // Control change
		sendProgramChange(patchNum); // Program change
		try {
			Thread.sleep(100);
		} catch (Exception e) {
			ErrorMsgUtil.reportStatus(e);
		}

		// int timbreAddr = patchNum * (SSIZE - 1);
		int timbreAddr = patchNum * SSIZE; // Timbre offset is here same as size
		int timAddrM = (timbreAddr / 0x80) & 0x7F;
		int timAddrL = timbreAddr & 0x7F;
		p.getSysex()[0] = (byte) 0xF0;

		p.getSysex()[5] = (byte) 0x04; // point to Timbre Memory
		p.getSysex()[6] = (byte) timAddrM;
		p.getSysex()[7] = (byte) timAddrL;
		// calculateChecksum(p);

		ErrorMsgUtil.reportStatus("Store patchNum " + patchNum + " to timAddrM/L " + timAddrM + " / " + timAddrL);
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
		sum += 0xA5;
		p.getSysex()[ofs] = (byte) (sum % 128);
	}

	// not used
	public PatchDataImpl createNewPatch() {
		byte[] sysex = new byte[HSIZE + SSIZE + 1];
		sysex[0] = (byte) 0xF0;
		sysex[1] = (byte) 0x41;
		sysex[2] = (byte) 0x10;
		sysex[3] = (byte) 0x16;
		sysex[4] = (byte) 0x12;
		sysex[5] = (byte) 0x04;
		sysex[6] = (byte) 0x0;
		sysex[HSIZE + SSIZE] = (byte) 0xF7;
		PatchDataImpl p = new PatchDataImpl(sysex, this);
		setPatchName(p, "New Timbre");
		calculateChecksum(p);
		return p;
	}

	public JSLFrame editPatch(PatchDataImpl p) {
		return new RolandMT32TimbreTempEditor(p);
	}

	public void requestPatchDump(int bankNum, int timNum) {
		// timNum misuses patchNum
		// The message format here is Request RQ1
		int timbreAddr = timNum * (SSIZE - 1); // 0xF6;
		int timAddrH = 0x04; // Always Timbre Temp Area
		int timAddrM = timbreAddr / 0x80; // timbreAddr >> 8
		int timAddrL = timbreAddr & 0x7F;
		int timSizeH = 0x00;
		int timSizeM = 0x01;
		int timSizeL = 0x76;
		int checkSum = (0 - (timAddrH + timAddrM + timAddrL + timSizeH + timSizeM + timSizeL)) & 0x7F;
		NameValue nVs[] = new NameValue[3];
		nVs[0] = new NameValue("partAddrM", timAddrM);
		nVs[1] = new NameValue("partAddrL", timAddrL);
		nVs[2] = new NameValue("checkSum", checkSum);

		send(SYS_REQ.toSysexMessage(getChannel(), nVs));
	}
}
