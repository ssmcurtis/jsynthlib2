/*
 * @version $Id: NovationXioSynthSingleDriver.java,v 1.9 2008/12/16 $
 */
package org.jsynthlib.synthdrivers.novation.xiosynth;

import java.io.UnsupportedEncodingException;

import org.jsynthlib.menu.JSLFrame;
import org.jsynthlib.model.driver.SynthDriverPatchImpl;
import org.jsynthlib.model.patch.PatchDataImpl;

public class NovationXioSynthSingleDriver extends SynthDriverPatchImpl {
	public NovationXioSynthSingleDriver() {
		super("Single", "Nicolas Boulicault");
		sysexID = "F000202901427F0";
		patchNameStart = 0xA4;
		patchNameSize = 16;
		deviceIDoffset = 0;
	}

	public void setPatchName(PatchDataImpl p, String name) {
		while (name.length() < patchNameSize)
			name = name + " ";

		byte[] namebytes = new byte[patchNameSize];

		try {
			namebytes = name.getBytes("US-ASCII");
			for (int i = 0; i < patchNameSize; i++)
				p.getSysex()[patchNameStart + i] = namebytes[i];
		} catch (UnsupportedEncodingException ex) {
			return;
		}
	}

	public String getPatchName(PatchDataImpl p, int patchNum) {
		int nameStart = 164;

		try {
			StringBuffer s = new StringBuffer(new String(((PatchDataImpl) p).getSysex(), nameStart, 16, "US-ASCII"));
			return s.toString();
		} catch (UnsupportedEncodingException ex) {
			return "-";
		}
	}

	public boolean supportsPatch(String patchString, byte[] sysex) {
		if (sysex.length != 270) {
			return false;
		}

		if ((patchSize != sysex.length) && (patchSize != 0))
			return false;

		if (sysexID == null || patchString.length() < sysexID.length())
			return false;

		StringBuffer compareString = new StringBuffer();
		for (int i = 0; i < sysexID.length(); i++) {
			switch (sysexID.charAt(i)) {
			case '*':
				compareString.append(patchString.charAt(i));
				break;
			default:
				compareString.append(sysexID.charAt(i));
			}
		}

		return (compareString.toString().equalsIgnoreCase(patchString.substring(0, sysexID.length())));
	}

	public void calculateChecksum(PatchDataImpl p) {
		/* I think there's no checksum on xio.. */
	}

	protected void sendProgramChange(int patchNum) {
	}

	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		sendPatch(p);
	}

	/* I took new patch from InitProgram in Xio */

	public PatchDataImpl createNewPatch() {
		byte[] sysex = new byte[] { (byte) 0xF0, (byte) 0x00, (byte) 0x20, (byte) 0x29, (byte) 0x01, (byte) 0x42,
				(byte) 0x7F, (byte) 0x00, (byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x38,
				(byte) 0x19, (byte) 0x00, (byte) 0x00, (byte) 0x40, (byte) 0x00, (byte) 0x15, (byte) 0x40, (byte) 0x40,
				(byte) 0x42, (byte) 0x40, (byte) 0x40, (byte) 0x40, (byte) 0x40, (byte) 0x40, (byte) 0x40, (byte) 0x40,
				(byte) 0x42, (byte) 0x40, (byte) 0x40, (byte) 0x40, (byte) 0x40, (byte) 0x40, (byte) 0x40, (byte) 0x40,
				(byte) 0x42, (byte) 0x40, (byte) 0x40, (byte) 0x40, (byte) 0x40, (byte) 0x40, (byte) 0x40, (byte) 0x40,
				(byte) 0x40, (byte) 0x49, (byte) 0x40, (byte) 0x40, (byte) 0x7F, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40, (byte) 0x7F, (byte) 0x7F, (byte) 0x40,
				(byte) 0x40, (byte) 0x40, (byte) 0x40, (byte) 0x40, (byte) 0x40, (byte) 0x40, (byte) 0x40, (byte) 0x00,
				(byte) 0x40, (byte) 0x40, (byte) 0x00, (byte) 0x3C, (byte) 0x40, (byte) 0x02, (byte) 0x5A, (byte) 0x7F,
				(byte) 0x28, (byte) 0x40, (byte) 0x02, (byte) 0x41, (byte) 0x00, (byte) 0x41, (byte) 0x00, (byte) 0x44,
				(byte) 0x00, (byte) 0x32, (byte) 0x44, (byte) 0x00, (byte) 0x32, (byte) 0x00, (byte) 0x00, (byte) 0x3F,
				(byte) 0x40, (byte) 0x40, (byte) 0x40, (byte) 0x38, (byte) 0x03, (byte) 0x40, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x7F, (byte) 0x40, (byte) 0x40, (byte) 0x40, (byte) 0x0A, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x40, (byte) 0x64, (byte) 0x00, (byte) 0x40, (byte) 0x40, (byte) 0x00, (byte) 0x40,
				(byte) 0x7F, (byte) 0x00, (byte) 0x00, (byte) 0x40, (byte) 0x5A, (byte) 0x00, (byte) 0x40, (byte) 0x14,
				(byte) 0x00, (byte) 0x4A, (byte) 0x40, (byte) 0x40, (byte) 0x40, (byte) 0x14, (byte) 0x00, (byte) 0x00,
				(byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02,
				(byte) 0x02, (byte) 0x02, (byte) 0x01, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40, (byte) 0x40, (byte) 0x40, (byte) 0x40,
				(byte) 0x40, (byte) 0x40, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40, (byte) 0x49, (byte) 0x6E,
				(byte) 0x69, (byte) 0x74, (byte) 0x20, (byte) 0x50, (byte) 0x72, (byte) 0x6F, (byte) 0x67, (byte) 0x72,
				(byte) 0x61, (byte) 0x6D, (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x00, (byte) 0x00,
				(byte) 0x40, (byte) 0x40, (byte) 0x40, (byte) 0x40, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x07,
				(byte) 0x02, (byte) 0x00, (byte) 0x7F, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x7F, (byte) 0x40,
				(byte) 0x40, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x7F, (byte) 0x40, (byte) 0x40, (byte) 0x40, (byte) 0x40, (byte) 0x00, (byte) 0x2C, (byte) 0x00,
				(byte) 0x40, (byte) 0x20, (byte) 0x00, (byte) 0x7F, (byte) 0x07, (byte) 0x00, (byte) 0x00, (byte) 0x05,
				(byte) 0x06, (byte) 0x00, (byte) 0x02, (byte) 0x07, (byte) 0x00, (byte) 0x7F, (byte) 0x10, (byte) 0x40,
				(byte) 0x40, (byte) 0x3F, (byte) 0x07, (byte) 0x07, (byte) 0x38, (byte) 0x07, (byte) 0x07, (byte) 0x07,
				(byte) 0x07, (byte) 0x07, (byte) 0x00, (byte) 0x3F, (byte) 0x07, (byte) 0x3F, (byte) 0x00, (byte) 0x3F,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xF7 };

		PatchDataImpl p = new PatchDataImpl(sysex, this);
		calculateChecksum(p);
		return p;
	}

	public JSLFrame editPatch(PatchDataImpl p) {
		return new NovationXioSynthSingleEditor((PatchDataImpl) p);
	}
}
