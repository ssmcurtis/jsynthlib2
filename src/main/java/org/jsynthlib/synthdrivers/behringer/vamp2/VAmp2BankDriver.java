/*
 * Copyright 2005 Jeff Weber
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

package org.jsynthlib.synthdrivers.behringer.vamp2;

import java.io.UnsupportedEncodingException;

import javax.swing.JOptionPane;

import org.jsynthlib.model.driver.NameValue;
import org.jsynthlib.model.driver.SynthDriverBank;
import org.jsynthlib.model.driver.SysexHandler;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.ErrorMsgUtil;

/**
 * V-Amp 2 Bank Driver. Used for V-Amp 2 bank patch. The V-Amp 2 appears to the user to have 25 banks consisting of five
 * patch locations apiece. The banks are numbered 1 through 25 and the patches are designated by the letters A, B, C, D,
 * and E. The actual physical layout is a single bank consisting of 125 patch locations numbered 0 to 124.
 * 
 * @author Jeff Weber
 */
public class VAmp2BankDriver extends SynthDriverBank {
	/**
	 * Bank Dump Request
	 */
	private static final SysexHandler SYS_REQ = new SysexHandler(Constants.VAMP2_BANK_DUMP_REQ_ID); // Bank Dump Request

	/**
	 * Constructs a VAmp2BankDriver
	 */
	public VAmp2BankDriver() {
		super(Constants.VAMP2_BANK_TYP_STR, Constants.AUTHOR, Constants.PATCHES_PER_BANK, 1);
		sysexID = Constants.VAMP2_BANK_MATCH_ID;

		deviceIDoffset = Constants.DEVICE_ID_OFFSET;

		bankNumbers = Constants.BANK_BANK_LIST;
		patchNumbers = Constants.BANK_PATCH_LIST;

		singleSysexID = Constants.VAMP2_SINGLE_MATCH_ID;
		singleSize = Constants.SINGLE_PATCH_SIZE + Constants.HDR_SIZE + 1;
		patchSize = Constants.PATCHES_PER_BANK * Constants.SINGLE_PATCH_SIZE + Constants.HDR_SIZE + 1;
		patchNameStart = Constants.HDR_SIZE + Constants.PATCH_NAME_START; // DOES
																			// include
																			// sysex
																			// header
		patchNameSize = Constants.PATCH_NAME_SIZE;
	}

	/**
	 * Returns the index of the selected patch
	 * 
	 * @param patchNum
	 *            Patch number
	 * @return Index of the patch
	 */
	public int getPatchStart(int patchNum) {
		int start = (Constants.SINGLE_PATCH_SIZE * patchNum);
		start += Constants.HDR_SIZE; // sysex header
		return start;
	}

	/**
	 * Puts a single program patch into a bank. The target bank is given by the bank parameter. The target location
	 * within the bank is given by patchNum, where patchNum is in the range 0 through 124. The header and trailer bytes
	 * are stripped from the sysex data and the target location within the bank is overwritten.
	 * 
	 * @param bank
	 *            The target bank where the patch will be placed.
	 * @param p
	 *            The patch to be placed into the target bank.
	 * @param patchNum
	 *            The location within the bank where the patch will be placed.
	 */
	public void putPatch(PatchDataImpl bank, PatchDataImpl p, int patchNum) {
		if (!canHoldPatch(p)) {
			JOptionPane.showMessageDialog(null, "This type of patch does not fit in to this type of bank.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		System.arraycopy(p.getSysex(), Constants.HDR_SIZE, bank.getSysex(), getPatchStart(patchNum), Constants.SINGLE_PATCH_SIZE);
	}

	/**
	 * Returns a single program patch from a bank. The source bank is given by the bank parameter. The patch location
	 * within the bank is given by patchNum, where patchNum is in the range 0 through 124. The patch is extracted from
	 * the bank and a valid V-Amp 2 preset patch header is appended at the beginning and 0xF7 is appended at the end.
	 * 
	 * @param bank
	 *            The bank from which the patch will be retrieved.
	 * @param patchNum
	 *            The location of the requested patch within the bank.
	 * @return A reference to the requested patch.
	 */
	public PatchDataImpl extractPatch(PatchDataImpl bank, int patchNum) {
		byte[] sysex = new byte[singleSize];
		System.arraycopy(Constants.VAMP2_DUMP_HDR_BYTES, 0, sysex, 0, Constants.HDR_SIZE);
		sysex[4] = (byte) getChannel();
		sysex[7] = (byte) patchNum;
		sysex[singleSize - 1] = (byte) 0xF7;
		System.arraycopy(bank.getSysex(), getPatchStart(patchNum), sysex, Constants.HDR_SIZE, Constants.SINGLE_PATCH_SIZE);
		try {
			PatchDataImpl p = new PatchDataImpl(sysex, getDevice());
			return p;
		} catch (Exception e) {
			ErrorMsgUtil.reportError("Error", "Error in Bass Pod Bank Driver", e);
			return null;
		}
	}

	/**
	 * Gets the name of a patch within the bank. Patch p is the bank patch. int patchNum represents the location of the
	 * single patch within the bank, designated by a number between 0 and 124.
	 * 
	 * @param p
	 *            The target bank patch containing the single patch whose name is to be retrieved.
	 * @param patchNum
	 *            The location within the bank of the single patch whose name is to be retrieved.
	 * @return A String containing the patch name.
	 */
	public String getPatchName(PatchDataImpl p, int patchNum) {
		int nameStart = getPatchStart(patchNum) + Constants.PATCH_NAME_START;
		try {
			StringBuffer s = new StringBuffer(new String(p.getSysex(), nameStart, patchNameSize, "US-ASCII"));
			return s.toString();
		} catch (UnsupportedEncodingException ex) {
			return "-";
		}
	}

	/**
	 * Sets the name of a patch within the bank. Patch p is the bank patch. int patchNum represents the location of the
	 * single patch within the bank, designated by a number between 0 and 124. String name contains the name to be
	 * assigned to the patch.
	 * 
	 * @param p
	 *            The target bank patch containing the single patch whose name is to be changed.
	 * @param patchNum
	 *            The location within the bank of the single patch whose name is to be changed.
	 * @param name
	 *            A String containing the new patch name.
	 */
	public void setPatchName(PatchDataImpl p, int patchNum, String name) {
		int nameStart = getPatchStart(patchNum) + Constants.PATCH_NAME_START;

		if (name.length() < patchNameSize) {
			name = name + "                ";
		}

		byte[] namebytes = new byte[patchNameSize];

		try {
			namebytes = name.getBytes("US-ASCII");
			for (int i = 0; i < patchNameSize; i++) {
				p.getSysex()[nameStart + i] = namebytes[i];
			}
		} catch (UnsupportedEncodingException ex) {
			return;
		}
	}

	/**
	 * Creates a new bank patch.
	 * 
	 * @return A reference to the new bank patch.
	 */
	protected PatchDataImpl createNewPatch() {
		byte[] sysex = new byte[Constants.HDR_SIZE + (Constants.SINGLE_PATCH_SIZE * Constants.PATCHES_PER_BANK) + 1];
		System.arraycopy(Constants.BANK_DUMP_HDR_BYTES, 0, sysex, 0, Constants.HDR_SIZE);
		sysex[4] = (byte) getChannel();
		sysex[Constants.HDR_SIZE + (Constants.SINGLE_PATCH_SIZE * Constants.PATCHES_PER_BANK)] = (byte) 0xF7;
		PatchDataImpl p = new PatchDataImpl(sysex, this);
		for (int i = 0; i < Constants.PATCHES_PER_BANK; i++) {
			System.arraycopy(Constants.NEW_SINGLE_SYSEX, Constants.HDR_SIZE, p.getSysex(), getPatchStart(i),
					Constants.SINGLE_PATCH_SIZE);
			setPatchName(p, i, "New Patch");
		}
		return p;
	}

	/**
	 * Requests a dump of a V-Amp 2 bank consisting of 125 patches. The bankNum and patchNum parameters are ignored.
	 * 
	 * @param bankNum
	 *            Parameter provided to match the method in the superclass. Values pased in this parameter are ignored.
	 * @param patchNum
	 *            Parameter provided to match the method in the superclass. Values pased in this parameter are ignored.
	 */
	public void requestPatchDump(int bankNum, int patchNum) {
		int channel = getChannel();
		send(SYS_REQ.toSysexMessage(channel, new NameValue("channel", channel)));
	}

	/**
	 * Extracts all 125 preset patches from a bank patch and sends all to the device. Patch p represents the bank patch
	 * to be stored. For the V-Amp 2, the bankNum and patchNum parameters are ignored (since the V-Amp 2 only has one
	 * bank).
	 * 
	 * @param p
	 *            The patch to be stored.
	 * @param bankNum
	 *            Parameter provided to match the method in the superclass. Values pased in this parameter are ignored.
	 * @param patchNum
	 *            Parameter provided to match the method in the superclass. Values pased in this parameter are ignored.
	 */
	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		PatchDataImpl[] thisPatch = new PatchDataImpl[Constants.PATCHES_PER_BANK];
		for (int progNbr = 0; progNbr < Constants.PATCHES_PER_BANK; progNbr++) {
			thisPatch[progNbr] = extractPatch(p, progNbr);
		}
		for (int progNbr = 0; progNbr < Constants.PATCHES_PER_BANK; progNbr++) {
			int bankNbr = progNbr / 5;
			int ptchNbr = progNbr % 5;
			((VAmp2SingleDriver) thisPatch[progNbr].getDriver()).storePatch(thisPatch[progNbr], bankNbr, ptchNbr);
		}
	}
}