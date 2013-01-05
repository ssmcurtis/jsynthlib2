package org.jsynthlib.synthdrivers.yamaha.fs1r;

import java.io.UnsupportedEncodingException;

import org.jsynthlib.menu.JSLFrame;
import org.jsynthlib.model.driver.SynthDriverPatchImpl;
import org.jsynthlib.model.driver.SysexHandler;
import org.jsynthlib.model.patch.PatchDataImpl;

/**
 * Single driver for Yamaha FS1R voices.
 * 
 * @author Denis Queffeulou mailto:dqueffeulou@free.fr
 * @version $Id$
 */
public class YamahaFS1RVoiceDriver extends SynthDriverPatchImpl {
	/** size of patch without header */
	static final int PATCH_SIZE = 608;

	/** offset without sysex header */
	static final int PATCHNAME_OFFSET = 9;

	/** number of characters in patch name */
	static final int PATCHNAME_SIZE = 10;

	/** size of header begin + end */
	static final int HEADER_SIZE = 11;

	static final int COMMON_SIZE = 112;
	static final int COMMON_OFFSET = PATCHNAME_OFFSET;

	static final int VOICE_VOICE_SIZE = 35;
	static final int VOICE_UNVOICE_SIZE = 27;
	static final int VOICE_SIZE = VOICE_VOICE_SIZE + VOICE_UNVOICE_SIZE;

	static final int VOICE_VOICE_OFFSET = COMMON_OFFSET + COMMON_SIZE;

	static final int INTERNAL_PATCHNUM_OFFSET = 8;

	static final int BANK_NUM_INTERNAL = 0;
	static final int BANK_NUM_PERFORMANCE = 1;

	private int mCurrentBankNum;
	private int mCurrentPatchNum;

	private static final byte[] mInitVoice = new byte[] { (byte) 0xF0, 0x43, 0x00, 0x5E, 0x04, 0x60, 0x41, 0x00, 0x00,
			0x49, 0x6E, 0x69, 0x74, 0x20, 0x56, 0x6F, 0x69, 0x63, 0x65, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x14,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x14, 0x00, 0x00, 0x00, 0x00, 0x18, 0x32, 0x32, 0x32, 0x32, 0x14,
			0x14, 0x14, 0x14, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x01, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00, 0x00, 0x32, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x40, 0x40, 0x40,
			0x40, 0x40, 0x00, 0x00, 0x00, 0x00, 0x00, 0x40, 0x40, 0x40, 0x40, 0x40, 0x00, 0x24, 0x07, 0x55, 0x07, 0x00,
			0x00, 0x45, 0x3C, 0x0C, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x40, 0x32, 0x64, 0x4B, 0x4B, 0x1E, 0x1E, 0x1E,
			0x63, 0x00, 0x00, 0x00, 0x18, 0x01, 0x00, 0x00, 0x38, 0x00, 0x14, 0x0F, 0x32, 0x32, 0x14, 0x14, 0x63, 0x63,
			0x63, 0x00, 0x00, 0x14, 0x14, 0x00, 0x00, 0x00, 0x63, 0x27, 0x00, 0x00, 0x03, 0x00, 0x00, 0x00, 0x00, 0x38,
			0x07, 0x07, 0x07, 0x18, 0x0E, 0x00, 0x00, 0x14, 0x07, 0x00, 0x32, 0x32, 0x14, 0x14, 0x00, 0x07, 0x63, 0x63,
			0x63, 0x00, 0x00, 0x14, 0x14, 0x00, 0x00, 0x00, 0x07, 0x07, 0x07, 0x07, 0x18, 0x01, 0x00, 0x00, 0x38, 0x01,
			0x14, 0x0F, 0x32, 0x32, 0x14, 0x14, 0x63, 0x63, 0x63, 0x00, 0x00, 0x14, 0x14, 0x00, 0x00, 0x00, 0x00, 0x27,
			0x00, 0x00, 0x03, 0x00, 0x00, 0x00, 0x00, 0x38, 0x07, 0x07, 0x07, 0x18, 0x0F, 0x00, 0x00, 0x14, 0x07, 0x00,
			0x32, 0x32, 0x14, 0x14, 0x00, 0x07, 0x63, 0x63, 0x63, 0x00, 0x00, 0x14, 0x14, 0x00, 0x00, 0x00, 0x07, 0x07,
			0x07, 0x07, 0x18, 0x01, 0x00, 0x00, 0x38, 0x02, 0x14, 0x0F, 0x32, 0x32, 0x14, 0x14, 0x63, 0x63, 0x63, 0x00,
			0x00, 0x14, 0x14, 0x00, 0x00, 0x00, 0x00, 0x27, 0x00, 0x00, 0x03, 0x00, 0x00, 0x00, 0x00, 0x38, 0x07, 0x07,
			0x07, 0x18, 0x0F, 0x4B, 0x00, 0x14, 0x07, 0x00, 0x32, 0x32, 0x14, 0x14, 0x00, 0x07, 0x63, 0x63, 0x63, 0x00,
			0x00, 0x14, 0x14, 0x00, 0x00, 0x00, 0x07, 0x07, 0x07, 0x07, 0x18, 0x01, 0x00, 0x00, 0x38, 0x03, 0x14, 0x0F,
			0x32, 0x32, 0x14, 0x14, 0x63, 0x63, 0x63, 0x00, 0x00, 0x14, 0x14, 0x00, 0x00, 0x00, 0x00, 0x27, 0x00, 0x00,
			0x03, 0x00, 0x00, 0x00, 0x00, 0x38, 0x07, 0x07, 0x07, 0x18, 0x10, 0x00, 0x00, 0x14, 0x07, 0x00, 0x32, 0x32,
			0x14, 0x14, 0x00, 0x07, 0x63, 0x63, 0x63, 0x00, 0x00, 0x14, 0x14, 0x00, 0x00, 0x00, 0x07, 0x07, 0x07, 0x07,
			0x18, 0x01, 0x00, 0x00, 0x38, 0x04, 0x14, 0x0F, 0x32, 0x32, 0x14, 0x14, 0x63, 0x63, 0x63, 0x00, 0x00, 0x14,
			0x14, 0x00, 0x00, 0x00, 0x00, 0x27, 0x00, 0x00, 0x03, 0x00, 0x00, 0x00, 0x00, 0x38, 0x07, 0x07, 0x07, 0x18,
			0x10, 0x2A, 0x00, 0x14, 0x07, 0x00, 0x32, 0x32, 0x14, 0x14, 0x00, 0x07, 0x63, 0x63, 0x63, 0x00, 0x00, 0x14,
			0x14, 0x00, 0x00, 0x00, 0x07, 0x07, 0x07, 0x07, 0x18, 0x01, 0x00, 0x00, 0x38, 0x05, 0x14, 0x0F, 0x32, 0x32,
			0x14, 0x14, 0x63, 0x63, 0x63, 0x00, 0x00, 0x14, 0x14, 0x00, 0x00, 0x00, 0x00, 0x27, 0x00, 0x00, 0x03, 0x00,
			0x00, 0x00, 0x00, 0x38, 0x07, 0x07, 0x07, 0x18, 0x10, 0x4B, 0x00, 0x14, 0x07, 0x00, 0x32, 0x32, 0x14, 0x14,
			0x00, 0x07, 0x63, 0x63, 0x63, 0x00, 0x00, 0x14, 0x14, 0x00, 0x00, 0x00, 0x07, 0x07, 0x07, 0x07, 0x18, 0x01,
			0x00, 0x00, 0x38, 0x06, 0x14, 0x0F, 0x32, 0x32, 0x14, 0x14, 0x63, 0x63, 0x63, 0x00, 0x00, 0x14, 0x14, 0x00,
			0x00, 0x00, 0x00, 0x27, 0x00, 0x00, 0x03, 0x00, 0x00, 0x00, 0x00, 0x38, 0x07, 0x07, 0x07, 0x18, 0x10, 0x68,
			0x00, 0x14, 0x07, 0x00, 0x32, 0x32, 0x14, 0x14, 0x00, 0x07, 0x63, 0x63, 0x63, 0x00, 0x00, 0x14, 0x14, 0x00,
			0x00, 0x00, 0x07, 0x07, 0x07, 0x07, 0x18, 0x01, 0x00, 0x00, 0x38, 0x07, 0x14, 0x0F, 0x32, 0x32, 0x14, 0x14,
			0x63, 0x63, 0x63, 0x00, 0x00, 0x14, 0x14, 0x00, 0x00, 0x00, 0x00, 0x27, 0x00, 0x00, 0x03, 0x00, 0x00, 0x00,
			0x00, 0x38, 0x07, 0x07, 0x07, 0x18, 0x11, 0x00, 0x00, 0x14, 0x07, 0x00, 0x32, 0x32, 0x14, 0x14, 0x00, 0x07,
			0x63, 0x63, 0x63, 0x00, 0x00, 0x14, 0x14, 0x00, 0x00, 0x00, 0x07, 0x07, 0x07, 0x07, 0x6B, (byte) 0xF7 };

	private static String mVoicesLabels128[] = new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11",
			"12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29",
			"30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47",
			"48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65",
			"66", "67", "68", "69", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "80", "81", "82", "83",
			"84", "85", "86", "87", "88", "89", "90", "91", "92", "93", "94", "95", "96", "97", "98", "99", "100",
			"101", "102", "103", "104", "105", "106", "107", "108", "109", "110", "111", "112", "113", "114", "115",
			"116", "117", "118", "119", "120", "121", "122", "123", "124", "125", "126", "127", "128" };

	/** size of all */
	static final int PATCH_AND_HEADER_SIZE = PATCH_SIZE + HEADER_SIZE;

	private static YamahaFS1RVoiceDriver mSingleton;

	/**
	 * Constructor for the YamahaFS1RSingleDriver object
	 */
	public YamahaFS1RVoiceDriver() {
		super("Voice", "Denis Queffeulou");
		mSingleton = this;
		sysexID = "F043005E0460**00";

		// inquiryID="F07E**06020F0200*************F7";
		patchSize = PATCH_AND_HEADER_SIZE;
		patchNameStart = PATCHNAME_OFFSET;
		patchNameSize = PATCHNAME_SIZE;
		// deviceIDoffset est l'emplacement du canal midi si besoin
		deviceIDoffset = -1;
		checksumStart = 4;
		checksumEnd = PATCH_AND_HEADER_SIZE - 3;
		checksumOffset = PATCH_AND_HEADER_SIZE - 2;
		sysexRequestDump = new SysexHandler("F0 43 20 5E 51 00 *patchNum* F7");
		bankNumbers = new String[] { "Internal", "Current performance voices (1..4)" };
		patchNumbers = mVoicesLabels128;
	}

	public static YamahaFS1RVoiceDriver getInstance() {
		return mSingleton;
	}

	public void setBankNum(int bankNum) {
		mCurrentBankNum = bankNum;
		// System.out.println("setBankNum = "+ bankNum);
		updateSysexRequest();
	}

	public void setPatchNum(int patchNum) {
		mCurrentPatchNum = patchNum;
		if (mCurrentBankNum == BANK_NUM_PERFORMANCE) {
			if (mCurrentPatchNum > 3) {
				mCurrentPatchNum = 3;
			}
		}
		// System.out.println("setPatchNum = "+patchNum);
		updateSysexRequest();
	}

	/**
	 * Send a voice in current performance part.
	 * 
	 * @param aPart
	 *            1..4
	 */
	public void sendPatch(PatchDataImpl p, int aPart) {
		p.getSysex()[6] = (byte) (0x40 + aPart - 1);
		p.getSysex()[7] = (byte) 0;
		p.getSysex()[8] = (byte) 0;
		calculateChecksum(p);
		sendPatch(p);
	}

	/** Sends a patch to a set location on a synth. */
	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		// change the address to internal voice
		((PatchDataImpl) p).getSysex()[6] = (byte) 0x51;
		((PatchDataImpl) p).getSysex()[7] = (byte) 0;
		((PatchDataImpl) p).getSysex()[8] = (byte) patchNum;
		calculateChecksum(p);
		sendPatch(p);
	}

	/**
	 * Met a jour la requete selon le type de banque.
	 */
	private void updateSysexRequest() {
		if (mCurrentBankNum == BANK_NUM_PERFORMANCE) {
			sysexRequestDump = new SysexHandler("F0 43 20 5E 4" + mCurrentPatchNum + " 00 00 F7");
		} else {
			sysexRequestDump = new SysexHandler("F0 43 20 5E 51 00 *patchNum* F7");
		}
	}

	/**
	 * @param p
	 *            a bank patch
	 * @param aPatchOffset
	 *            offset of voice in patch sysex
	 */
	String getPatchName(PatchDataImpl p, int aPatchOffset) {
		if (patchNameSize == 0)
			return ("-");
		try {
			String s = new String(p.getSysex(), aPatchOffset + patchNameStart, patchNameSize, "US-ASCII");
			return s;
		} catch (UnsupportedEncodingException ex) {
			return "-";
		}
	}

	/**
	 * @param p
	 *            a bank patch
	 * @param aPatchOffset
	 *            offset of voice in patch sysex
	 */
	public void setPatchName(PatchDataImpl p, String name, int aPatchOffset) {
		if (name.length() < patchNameSize)
			name = name + "            ";
		byte[] namebytes = new byte[64];
		try {
			namebytes = name.getBytes("US-ASCII");
			for (int i = 0; i < patchNameSize; i++)
				p.getSysex()[aPatchOffset + patchNameStart + i] = namebytes[i];

		} catch (UnsupportedEncodingException ex) {
			return;
		}
	}

	/**
	 * Description of the Method
	 * 
	 * @return an Init Voice copy
	 */
	public PatchDataImpl createNewPatch() {
		byte[] sysex = new byte[PATCH_AND_HEADER_SIZE];
		initPatch(sysex, 0);
		return new PatchDataImpl(sysex, this);
	}

	static void initPatch(byte[] sysex, int aOffset) {
		for (int i = 0; i < PATCH_AND_HEADER_SIZE; i++) {
			sysex[aOffset + i] = mInitVoice[i];
		}
	}

	/**
	 * Return voice editor window.
	 * 
	 * @param p
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 */
	public JSLFrame editPatch(PatchDataImpl p) {
		return new YamahaFS1RVoiceEditor((PatchDataImpl) p);
	}

	/**
	 * @param aPart
	 *            part number in performance
	 */
	public JSLFrame editPatch(PatchDataImpl p, int aPart, int aBankNumber) {
		return new YamahaFS1RVoiceEditor(p, aPart, aBankNumber);
	}
}
