/*
 * @version $Id$
 */
package org.jsynthlib.synthdrivers.yamaha.tx81z;

import org.jsynthlib.menu.JSLFrame;
import org.jsynthlib.model.driver.SynthDriverPatchImpl;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.ErrorMsgUtil;

public class YamahaTX81zSingleDriver extends SynthDriverPatchImpl {

	public YamahaTX81zSingleDriver() {
		super("Single", "Brian Klock");
		sysexID = "F043**7E00214C4D2020383937364145";
		patchNameStart = 124;
		patchNameSize = 10;
		deviceIDoffset = 2;
		bankNumbers = new String[] { "0-Internal" };
		patchNumbers = new String[] { "I01", "I02", "I03", "I04", "I05", "I06", "I07", "I08", "I09", "I10", "I11", "I12", "I13", "I14",
				"I15", "I16", "I17", "I18", "I19", "I20", "I21", "I22", "I23", "I24", "I25", "I26", "I27", "I28", "I29", "I30", "I31",
				"I32" };
	}

	public void calculateChecksum(PatchDataImpl p) {
		calculateChecksum(p, 6, 38, 39); // calculate ACED Checksum
		calculateChecksum(p, 47, 139, 140); // calculate VCED Checksum
		((PatchDataImpl) p).getSysex()[43] = ((byte) (getChannel() - 1));
	}

	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		// TODO Fix original error later
		if (bankNum<0) {
			bankNum = 0;
		}
		setBankNum(bankNum);
		sendProgramChange(patchNum);
		sendPatch(p);
		
		// TODO TX81z - store
		try {
			Thread.sleep(100);
			send(new byte[] { (byte) 0xF0, (byte) 0x43, (byte) (0x10 + getChannel() - 1), (byte) 0x13, (byte) 0x41, (byte) 0x7F,
					(byte) 0xF7 });
			Thread.sleep(100);
			send(new byte[] { (byte) 0xF0, (byte) 0x43, (byte) (0x10 + getChannel() - 1), (byte) 0x13, (byte) 0x48, (byte) 0x7F,
					(byte) 0xF7 });
			Thread.sleep(100);
			send(new byte[] { (byte) 0xF0, (byte) 0x43, (byte) (0x10 + getChannel() - 1), (byte) 0x13, (byte) 0x41, (byte) 0x00,
					(byte) 0xF7 });
			Thread.sleep(100);
			send(new byte[] { (byte) 0xF0, (byte) 0x43, (byte) (0x10 + getChannel() - 1), (byte) 0x13, (byte) 0x48, (byte) 0x7F,
					(byte) 0xF7 });
		} catch (Exception e) {
			ErrorMsgUtil.reportError("Error", "Unable to Play Patch", e);
		}
	}

	public PatchDataImpl createNewPatch() {
		byte[] sysex = new byte[142];
		sysex[00] = (byte) 0xF0;
		sysex[01] = (byte) 0x43;
		sysex[02] = (byte) 0x00;
		sysex[03] = (byte) 0x7E;
		sysex[04] = (byte) 0x00;
		sysex[05] = (byte) 0x21;
		sysex[06] = (byte) 0x4C;
		sysex[07] = (byte) 0x4D;
		sysex[8] = (byte) 0x20;
		sysex[9] = (byte) 0x20;
		sysex[10] = (byte) 0x38;
		sysex[11] = (byte) 0x39;
		sysex[12] = (byte) 0x37;
		sysex[13] = (byte) 0x36;
		sysex[14] = (byte) 0x41;
		sysex[15] = (byte) 0x45;
		sysex[40] = (byte) 0xF7;
		sysex[41] = (byte) 0xF0;
		sysex[42] = (byte) 0x43;
		sysex[43] = (byte) 0x00;
		sysex[44] = (byte) 0x03;
		sysex[45] = (byte) 0x00;
		sysex[46] = (byte) 0x5D;
		sysex[141] = (byte) 0xF7;

		PatchDataImpl p = new PatchDataImpl(sysex, this);
		setPatchName(p, "NewPatch");
		calculateChecksum(p);
		return p;
	}

	public JSLFrame editPatch(PatchDataImpl p) {
		return new YamahaTX81zSingleEditor((PatchDataImpl) p);
	}
}
