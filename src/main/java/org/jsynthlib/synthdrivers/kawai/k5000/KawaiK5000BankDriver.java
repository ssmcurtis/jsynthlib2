/*
 * @version $Id$
 */
package org.jsynthlib.synthdrivers.kawai.k5000;

import java.io.UnsupportedEncodingException;

import javax.swing.JOptionPane;

import org.jsynthlib.PatchBayApplication;
import org.jsynthlib.model.driver.SynthDriverBank;
import org.jsynthlib.model.driver.SysexHandler;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.ErrorMsgUtil;
import org.jsynthlib.tools.HexaUtil;

public class KawaiK5000BankDriver extends SynthDriverBank {

	// phil@muqus.com - see K5000W/S Midi Implementation, p23
	final static SysexHandler SYSEX_REQUEST_A_DUMP = new SysexHandler("F0 40 @@ 01 00 0A 00 00 00 F7");
	// N.B. Following message specified incorrectly in K5000W/2 Midi Implementation
	final static SysexHandler SYSEX_REQUEST_D_DUMP = new SysexHandler("F0 40 @@ 01 00 0A 00 02 00 F7");

	public int[] patchIndex = new int[129];
	public PatchDataImpl indexedPatch;

	public KawaiK5000BankDriver() {
		super("Bank", "Brian Klock", 128, 4);
		sysexID = "F040**21000A000*";
		deviceIDoffset = 2;
		patchSize = 0;
		numSysexMsgs = 1; // phil@muqus.com
		bankNumbers = new String[] { "0-Bank A", "1-------", "2-------", "3-Bank D" };
		patchNumbers = new String[] { "01-", "02-", "03-", "04-", "05-", "06-", "07-", "08-", "09-", "10-", "11-",
				"12-", "13-", "14-", "15-", "16-", "17-", "18-", "19-", "20-", "21-", "22-", "23-", "24-", "25-",
				"26-", "27-", "28-", "29-", "30-", "31-", "32-", "33-", "34-", "35-", "36-", "37-", "38-", "39-",
				"40-", "41-", "42-", "43-", "44-", "45-", "46-", "47-", "48-", "49-", "50-", "51-", "52-", "53-",
				"54-", "55-", "56-", "57-", "58-", "59-", "60-", "61-", "62-", "63-", "64-", "65-", "66-", "67-",
				"68-", "69-", "70-", "71-", "72-", "73-", "74-", "75-", "76-", "77-", "78-", "79-", "80-", "81-",
				"82-", "83-", "84-", "85-", "86-", "87-", "88-", "89-", "90-", "91-", "92-", "93-", "94-", "95-",
				"96-", "97-", "98-", "99-", "100-", "101-", "102-", "103-", "104-", "105-", "106-", "107-", "108-",
				"109-", "110-", "111-", "112-", "113-", "114-", "115-", "116-", "117-", "118-", "119-", "120-", "121-",
				"122-", "123-", "124-", "125-", "126-", "127-", "128-" };

		singleSysexID = "F040**20000A000*";
		singleSize = 0;
	}

	// K5000 Banks have a variable number of patches from 1-128. This funtion
	// determines the number of patches in the bank
	public int numPatchesinBank(PatchDataImpl p) {
		int num = 0;
		for (int i = 8; i < 26; i++) {
			if ((p.getSysex()[i] & 1) > 0)
				num++;
			if ((p.getSysex()[i] & 2) > 0)
				num++;
			if ((p.getSysex()[i] & 4) > 0)
				num++;
			if ((p.getSysex()[i] & 8) > 0)
				num++;
			if ((p.getSysex()[i] & 16) > 0)
				num++;
			if ((p.getSysex()[i] & 32) > 0)
				num++;
			if ((p.getSysex()[i] & 64) > 0)
				num++;
		}
		return num;
	}

	public boolean patchExists(PatchDataImpl p, int num) {
		int sub = p.getSysex()[8 + (num / 7)];
		if (num % 7 == 0)
			return ((sub & 1) > 0);
		if (num % 7 == 1)
			return ((sub & 2) > 0);
		if (num % 7 == 2)
			return ((sub & 4) > 0);
		if (num % 7 == 3)
			return ((sub & 8) > 0);
		if (num % 7 == 4)
			return ((sub & 16) > 0);
		if (num % 7 == 5)
			return ((sub & 32) > 0);
		if (num % 7 == 6)
			return ((sub & 64) > 0);
		return false;
	}

	public void setPatchExists(PatchDataImpl p, int num, boolean exists) {
		if (exists == patchExists(p, num))
			return;
		int sub = p.getSysex()[8 + (num / 7)];
		ErrorMsgUtil.reportStatus("bitmask was " + sub);
		if (num % 7 == 0)
			sub = sub ^ 1;
		if (num % 7 == 1)
			sub = sub ^ 2;
		if (num % 7 == 2)
			sub = sub ^ 4;
		if (num % 7 == 3)
			sub = sub ^ 8;
		if (num % 7 == 4)
			sub = sub ^ 16;
		if (num % 7 == 5)
			sub = sub ^ 32;
		if (num % 7 == 6)
			sub = sub ^ 64;
		ErrorMsgUtil.reportStatus("bitmask is " + sub);

		p.getSysex()[8 + (num / 7)] = (byte) sub;
	}

	public void generateIndex(PatchDataImpl p) {
		int currentPatchStart = 27;
		if (indexedPatch == p)
			return;
		for (int i = 0; i < 128; i++) {
			if (patchExists(p, i)) {
				patchIndex[i] = currentPatchStart;
				int newPatchStart = currentPatchStart + (82 + 86 * p.getSysex()[currentPatchStart + 51]);
				int numWaveData = 0;
				for (int j = 0; j < p.getSysex()[currentPatchStart + 51]; j++)
					if (((p.getSysex()[81 + currentPatchStart + 29 + (86 * j)] & 7) * 128 + p.getSysex()[81 + currentPatchStart
							+ 30 + (86 * j)]) == 512)
						numWaveData++;

				newPatchStart += (numWaveData * 806);
				currentPatchStart = newPatchStart;
			} else
				patchIndex[i] = 0;

		}
		indexedPatch = p;
		patchIndex[128] = p.getSysex().length - 1;
	}

	public int getPatchStart(int patchNum) {
		ErrorMsgUtil.reportStatus("K5kBankDriver:Calling old getPatchStart-- redirecting");
		return getPatchStart(indexedPatch, patchNum);
	}

	public int getPatchStart(PatchDataImpl p, int patchNum) {
		generateIndex(p);
		return patchIndex[patchNum];
	}

	public String getPatchName(PatchDataImpl ip) {
		return (((PatchDataImpl) ip).getSysex().length / 1024) + " Kilobytes";
	}

	public String getPatchName(PatchDataImpl ip, int patchNum) {
		PatchDataImpl p = (PatchDataImpl) ip;
		int nameStart = getPatchStart(p, patchNum);
		if (nameStart == 0)
			return "[empty]";
		nameStart += 40; // offset of name in patch data
		try {
			StringBuffer s = new StringBuffer(new String(p.getSysex(), nameStart, 8, "US-ASCII"));
			return s.toString();
		} catch (UnsupportedEncodingException ex) {
			return "-";
		}

	}

	// ----- Start phil@muqus.com

	// ----------------------------------------------------------------------------------------------------------------------
	// KawaiK5000BankDriver->setPatchName(Patch, int, String)
	// ----------------------------------------------------------------------------------------------------------------------

	public void setPatchName(PatchDataImpl bank, int patchNum, String name) {
		PatchDataImpl p = extractPatch(bank, patchNum);
		p.setName(name);
		p.calculateChecksum();
		putPatch(bank, p, patchNum);
	}

	// ----- End phil@muqus.com

	// protected static void calculateChecksum(Patch p,int start,int end,int ofs)
	// {
	//
	// }

	public void calculateChecksum(PatchDataImpl p) {
	}

	// ----- Start phil@muqus.com

	// ----------------------------------------------------------------------------------------------------------------------
	// KawaiK5000BankDriver->putPatch
	// ----------------------------------------------------------------------------------------------------------------------

	public void putPatch(PatchDataImpl b, PatchDataImpl p, int patchNum) {
		PatchDataImpl bank = (PatchDataImpl) b;
		if (!canHoldPatch(p)) {
			JOptionPane.showMessageDialog(null, "This type of patch does not fit in to this type of bank.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Update index
		generateIndex(bank);

		// Find next existing patch in bank, this gives us the index at which the data needs to be inserted
		int nextPatchNum = patchNum;
		while (patchIndex[nextPatchNum] == 0)
			nextPatchNum++;
		ErrorMsgUtil.reportStatus("Insert at patchNum: " + nextPatchNum + " | index: " + patchIndex[nextPatchNum]);

		// p.sysex <DATA> starts at 9, ends just before trailing F7
		((PatchDataImpl) bank).setSysex(HexaUtil.byteArrayReplace(((PatchDataImpl) bank).getSysex(), patchIndex[nextPatchNum],
				patchSize(bank, patchNum), ((PatchDataImpl) p).getSysex(), 9, ((PatchDataImpl) p).getSysex().length - 10));

		// Update index and checksum
		indexedPatch = null;
		setPatchExists(bank, patchNum, true);
		generateIndex(bank);
		calculateChecksum(bank);
	}

	// ----- End phil@muqus.com

	public PatchDataImpl extractPatch(PatchDataImpl b, int patchNum) {
		PatchDataImpl bank = (PatchDataImpl) b;
		try {
			generateIndex(bank);
			int patchSize;
			if (patchIndex[patchNum] == 0)
				return null;
			int i = patchNum + 1;
			while ((i < 128) && (patchIndex[i] == 0))
				i++;
			patchSize = patchIndex[i] - patchIndex[patchNum];
			patchSize += 10;
			byte[] sysex = new byte[patchSize];
			sysex[00] = (byte) 0xF0;
			sysex[01] = (byte) 0x40;
			sysex[02] = (byte) 0x00;
			sysex[03] = (byte) 0x20;
			sysex[04] = (byte) 0x00;
			sysex[05] = (byte) 0x0A;
			sysex[06] = (byte) 0x00;
			sysex[07] = (byte) 0x00;
			sysex[8] = (byte) 0x00;
			sysex[patchSize - 1] = (byte) 0xF7;
			System.arraycopy(((PatchDataImpl) bank).getSysex(), getPatchStart(patchNum), sysex, 9, patchSize - 10);
			PatchDataImpl p = new PatchDataImpl(sysex, getDevice());
			p.calculateChecksum();
			return p;
		} catch (Exception e) {
			ErrorMsgUtil.reportError("Error", "Error in K5000 Bank Driver", e);
			return null;
		}
	}

	public PatchDataImpl createNewPatch() {
		byte[] sysex = new byte[28];
		sysex[0] = (byte) 0xF0;
		sysex[1] = (byte) 0x40;
		sysex[2] = (byte) 0x00;
		sysex[3] = (byte) 0x21;
		sysex[4] = (byte) 0x00;
		sysex[5] = (byte) 0x0a;
		sysex[6] = (byte) 0x00;
		sysex[27] = (byte) 0xF7;
		return new PatchDataImpl(sysex, this);
	}

	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		if (bankNum == 0)
			((PatchDataImpl) p).getSysex()[7] = 0; // bank a
		if (bankNum == 3)
			((PatchDataImpl) p).getSysex()[7] = 2; // bank d
		PatchBayApplication.showWaitDialog();
		setBankNum(bankNum);
		sendPatchWorker(p);
		PatchBayApplication.hideWaitDialog(); // phil@muqus.com

	};

	// ----- Start phil@muqus.com

	// ----------------------------------------------------------------------------------------------------------------------
	// KawaiK5000BankDriver->deletePatch
	// ----------------------------------------------------------------------------------------------------------------------

	public void deletePatch(PatchDataImpl b, int patchNum) {
		PatchDataImpl bank = (PatchDataImpl) b;
		if (!patchExists(bank, patchNum)) {
			JOptionPane.showMessageDialog(null, "Patch does not exist, so can not be deleted.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Update index
		generateIndex(bank);

		// Delete patch from sysex
		((PatchDataImpl) bank).setSysex(HexaUtil.byteArrayDelete(((PatchDataImpl) bank).getSysex(), patchIndex[patchNum],
				patchSize(bank, patchNum)));

		// Update index and checksum
		indexedPatch = null;
		setPatchExists(bank, patchNum, false);
		ErrorMsgUtil.reportStatus("NumPatches = " + numPatchesinBank(bank));
		generateIndex(bank);
		calculateChecksum(bank);
	}

	// ----------------------------------------------------------------------------------------------------------------------
	// KawaiK5000BankDriver->patchSize
	// Returns: Size of patch, patchNum ... or 0 if patch does not exist
	// ----------------------------------------------------------------------------------------------------------------------

	public int patchSize(PatchDataImpl bank, int patchNum) {
		if (patchExists(bank, patchNum)) {
			int i = patchNum + 1;
			while ((i < 128) && (patchIndex[i] == 0))
				i++;
			return patchIndex[i] - patchIndex[patchNum];
		} else {
			return 0;
		}
	}

	// ----------------------------------------------------------------------------------------------------------------------
	// KawaiK5000BankDriver->requestPatchDump
	// ----------------------------------------------------------------------------------------------------------------------

	public void requestPatchDump(int bankNum, int patchNum) {
		if (bankNum == 0)
			send(SYSEX_REQUEST_A_DUMP.toSysexMessage(getChannel()));
		else
			send(SYSEX_REQUEST_D_DUMP.toSysexMessage(getChannel()));
	}

	// ----- End phil@muqus.com
}
