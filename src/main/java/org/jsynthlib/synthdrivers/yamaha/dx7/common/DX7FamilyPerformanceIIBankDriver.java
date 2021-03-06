/*
 * JSynthlib -	generic "PerformanceII" Bank Driver for DX7 Family
 *		(used by DX7-II, DX7s)
 * ===============================================================
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

import java.io.UnsupportedEncodingException;

import org.jsynthlib.model.driver.SynthDriverBank;
import org.jsynthlib.model.driver.SysexHandler;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.ErrorMsgUtil;

public class DX7FamilyPerformanceIIBankDriver extends SynthDriverBank {
	byte[] initSysex;
	String[] dxPatchNumbers;
	String[] dxBankNumbers;

	private static final int dxPatchNameSize = 20; // size of patchname of single patch
	private static final int dxPatchNameOffset = 31; // offset in packed bank format
	private static final int dxSinglePackedSize = 51; // size of single patch in packed bank format
	private static final int dxSysexHeaderSize = 16; // length of sysex header

	public DX7FamilyPerformanceIIBankDriver(byte[] initSysex, String[] dxPatchNumbers, String[] dxBankNumbers) {
		super("Performance Bank", "Torsten Tittmann", dxPatchNumbers.length, 4);

		this.initSysex = initSysex;
		this.dxPatchNumbers = dxPatchNumbers;
		this.dxBankNumbers = dxBankNumbers;

		sysexID = "f0430*7e0c6a4c4d202038393733504d";
		sysexRequestDump = new SysexHandler("f0 43 @@ 7e 4c 4d 20 20 38 39 37 33 50 4d f7");
		deviceIDoffset = 2;
		patchNameStart = 0;
		patchNameSize = 0;
		patchNumbers = dxPatchNumbers;
		bankNumbers = dxBankNumbers;
		singleSysexID = "F0430*7E003d4c4d2020383937335045";
		;
		singleSize = 69;
		checksumOffset = 1648;
		checksumStart = 6;
		checksumEnd = 1647;
		numSysexMsgs = 1;
		patchSize = 1650;
		trimSize = patchSize;
	}

	public int getPatchStart(int patchNum) {
		return (dxSinglePackedSize * patchNum) + dxSysexHeaderSize;
	}

	public int getPatchNameStart(int patchNum) {
		return getPatchStart(patchNum) + dxPatchNameOffset;
	}

	public String getPatchName(PatchDataImpl p, int patchNum) {
		int nameStart = getPatchNameStart(patchNum);

		try {
			StringBuffer s = new StringBuffer(new String(((PatchDataImpl) p).getSysex(), nameStart, dxPatchNameSize,
					"US-ASCII"));
			return s.toString();
		} catch (UnsupportedEncodingException ex) {
			return "-";
		}
	}

	public void setPatchName(PatchDataImpl p, int patchNum, String name) {
		int nameStart = getPatchNameStart(patchNum);

		while (name.length() < dxPatchNameSize)
			name = name + " ";

		byte[] namebytes = new byte[dxPatchNameSize];

		try {
			namebytes = name.getBytes("US-ASCII");
			for (int i = 0; i < dxPatchNameSize; i++)
				((PatchDataImpl) p).getSysex()[nameStart + i] = namebytes[i];

		} catch (UnsupportedEncodingException ex) {
			return;
		}
	}

	public void putPatch(PatchDataImpl bank, PatchDataImpl p, int patchNum) // puts a patch into the bank, converting it
																			// as needed
	{
		if (!canHoldPatch(p)) {
			DX7FamilyStrings.dxShowError(toString(), "This type of patch does not fit in to this type of bank.");
			return;
		}

		for (int i = 0; i < 51; i++) {
			((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + i] = (byte) (((PatchDataImpl) p).getSysex()[16 + i]);
		}

		calculateChecksum(bank);
	}

	public PatchDataImpl extractPatch(PatchDataImpl bank, int patchNum) // Gets a patch from the bank, converting it as needed
	{
		try {
			byte[] sysex = new byte[singleSize];

			// transform bulk-dump-packed-format to voice data
			sysex[0] = (byte) 0xf0;
			sysex[1] = (byte) 0x43;
			sysex[2] = (byte) 0x00;
			sysex[3] = (byte) 0x7e;
			sysex[4] = (byte) 0x00;
			sysex[5] = (byte) 0x3d;
			sysex[6] = (byte) 0x4c; // "L"
			sysex[7] = (byte) 0x4d; // "M"
			sysex[8] = (byte) 0x20; // " "
			sysex[9] = (byte) 0x20; // " "
			sysex[10] = (byte) 0x38; // "8"
			sysex[11] = (byte) 0x39; // "9"
			sysex[12] = (byte) 0x37; // "7"
			sysex[13] = (byte) 0x33; // "3"
			sysex[14] = (byte) 0x50; // "P"
			sysex[15] = (byte) 0x45; // "E"
			sysex[singleSize - 1] = (byte) 0xf7;

			for (int i = 0; i < 51; i++) {
				sysex[16 + i] = (byte) (((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + i]);
			}

			PatchDataImpl p = new PatchDataImpl(sysex, getDevice()); // single sysex
			p.calculateChecksum();

			return p;
		} catch (Exception e) {
			ErrorMsgUtil.reportError(getManufacturerName() + " " + getModelName(), "Error in " + toString(), e);
			return null;
		}
	}

	public PatchDataImpl createNewPatch() // create a bank with 32 performance patches
	{
		byte[] sysex = new byte[trimSize];

		sysex[0] = (byte) 0xf0;
		sysex[1] = (byte) 0x43;
		sysex[2] = (byte) 0x00;
		sysex[3] = (byte) 0x7e;
		sysex[4] = (byte) 0x0c;
		sysex[5] = (byte) 0x6a;
		sysex[6] = (byte) 0x4c;
		sysex[7] = (byte) 0x4d;
		sysex[8] = (byte) 0x20;
		sysex[9] = (byte) 0x20;
		sysex[10] = (byte) 0x38;
		sysex[11] = (byte) 0x39;
		sysex[12] = (byte) 0x37;
		sysex[13] = (byte) 0x33;
		sysex[14] = (byte) 0x50;
		sysex[15] = (byte) 0x4d;

		sysex[trimSize - 1] = (byte) 0xf7;

		PatchDataImpl v = new PatchDataImpl(initSysex, getDevice()); // single sysex
		PatchDataImpl p = new PatchDataImpl(sysex, this); // bank sysex

		for (int i = 0; i < getNumPatches(); i++)
			putPatch(p, v, i);

		calculateChecksum(p);

		return p;
	}

	public void requestPatchDump(int bankNum, int patchNum) {
		send(sysexRequestDump.toSysexMessage(getChannel() + 0x20));
	}
}
