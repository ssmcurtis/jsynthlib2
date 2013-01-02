/*
 * Copyright 2005 Ton Holsink
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

package org.jsynthlib.synthdrivers.yamaha.ub99;

import java.io.UnsupportedEncodingException;

import org.jsynthlib.menu.helper.SysexHandler;
import org.jsynthlib.model.driver.SynthDriverBank;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.DriverUtil;

public class YamahaUB99BankDriver extends SynthDriverBank {

	private static final SysexHandler BANKDUMP_REQ = new SysexHandler("F0 43 7D 50 55 42 30 01 ** F7");

	private final YamahaUB99Driver singleDriver;

	public YamahaUB99BankDriver(YamahaUB99Driver singleDriver) {
		super("Bank", "Ton Holsink <a.j.m.holsink@chello.nl>", YamahaUB99Const.NUM_PATCH, YamahaUB99Const.NUM_COLUMNS);

		this.singleDriver = singleDriver;
		patchNameSize = YamahaUB99Const.NAME_SIZE;
		bankNumbers = new String[] { "User" };
		patchNumbers = new String[YamahaUB99Const.NUM_PATCH];
		System.arraycopy(DriverUtil.generateNumbers(1, YamahaUB99Const.NUM_PATCH, "##"), 0, patchNumbers, 0,
				YamahaUB99Const.NUM_PATCH);
		patchSize = YamahaUB99Const.BANK_SIZE;

		sysexID = "F04239392056312E3030";
		singleSysexID = null;
		singleSize = YamahaUB99Const.SINGLE_SIZE;
	}

	public String getPatchName(PatchDataImpl p, int patchNum) {
		int nameOfst = YamahaUB99Const.NAME_SIZE * patchNum + YamahaUB99Const.BANK_NAME_OFFSET;
		try {
			return new String(((PatchDataImpl) p).getSysex(), nameOfst, patchNameSize, "US-ASCII");
		} catch (UnsupportedEncodingException e) {
			return "---";
		}
	}

	public void setPatchName(PatchDataImpl p, int patchNum, String name) {
		int banknameOfst = YamahaUB99Const.NAME_SIZE * patchNum + YamahaUB99Const.BANK_NAME_OFFSET;
		int nameOfst = YamahaUB99Const.BANK_PATCH_OFFSET + singleSize * patchNum + YamahaUB99Const.NAME_OFFSET;

		while (name.length() < patchNameSize)
			name = name + " ";

		byte[] namebytes = name.getBytes();

		for (int i = 0; i < patchNameSize; i++) {
			p.getSysex()[banknameOfst + i] = namebytes[i];
			p.getSysex()[nameOfst + i] = namebytes[i];
		}
	}

	public void calculateChecksum(PatchDataImpl p) {
	}

	public boolean canHoldPatch(PatchDataImpl p) {
		if ((singleSize != p.getSysex().length) && (singleSize != 0))
			return false;
		return true;
	}

	public void putPatch(PatchDataImpl bank, PatchDataImpl p, int patchNum) {
		System.arraycopy(((PatchDataImpl) p).getSysex(), 0, ((PatchDataImpl) bank).getSysex(), YamahaUB99Const.BANK_PATCH_OFFSET
				+ singleSize * patchNum, singleSize);
	}

	public PatchDataImpl getPatch(PatchDataImpl bank, int patchNum) {
		byte[] sysex = new byte[singleSize];
		System.arraycopy(((PatchDataImpl) bank).getSysex(), YamahaUB99Const.BANK_PATCH_OFFSET + singleSize * patchNum, sysex,
				0, singleSize);

		return new PatchDataImpl(sysex, singleDriver);
	}

	public PatchDataImpl createNewPatch() {
		byte[] header = { 85, 66, 57, 57, 32, 86, 49, 46, 48, 48 };
		byte[] sysex = new byte[YamahaUB99Const.BANK_SIZE];

		System.arraycopy(header, 0, sysex, 0, header.length);
		System.arraycopy(header, 0, sysex, 64, header.length);

		byte[] b = singleDriver.createNewPatchArray();
		for (int i = 0; i < YamahaUB99Const.NUM_PATCH; i++) {
			System.arraycopy(YamahaUB99Const.NEW_PATCH_NAME, 0, sysex, YamahaUB99Const.BANK_NAME_OFFSET + i
					* YamahaUB99Const.NAME_SIZE, YamahaUB99Const.NAME_SIZE);
			System.arraycopy(b, 0, sysex, YamahaUB99Const.BANK_PATCH_OFFSET + i * YamahaUB99Const.SINGLE_SIZE,
					YamahaUB99Const.SINGLE_SIZE);
		}

		PatchDataImpl bank = new PatchDataImpl(sysex, this);
		return bank;
	}

	public void requestPatchDump(int bankNum, int patchNum) {
		for (int i = 0; i < YamahaUB99Const.NUM_PATCH; i++) {
			send(BANKDUMP_REQ.toSysexMessage(-1, i));
			try {
				Thread.sleep(100);
			} catch (Exception e) {
			}
		}
	}

	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		PatchDataImpl single;
		byte[] buf = new byte[] { (byte) 0xF0, (byte) 0x43, (byte) (0x7D), (byte) 0x30, (byte) 0x55, (byte) 0x42,
				(byte) 0x39, (byte) 0x39, (byte) 0x00, (byte) 0x00, (byte) 0x30, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0xF7 };
		DriverUtil.calculateChecksum(buf, YamahaUB99Const.CHECKSUMSTART, buf.length - 3, buf.length - 2);
		send(buf);
		try {
			Thread.sleep(100);
		} catch (Exception e) {
		}

		for (int i = 0; i < YamahaUB99Const.NUM_PATCH; i++) {
			single = getPatch(p, i);
			singleDriver.sendThisPatch(single, i, 0x01);
		}
		try {
			Thread.sleep(100);
		} catch (Exception e) {
		}
		buf[11] = 0x10;
		DriverUtil.calculateChecksum(buf, YamahaUB99Const.CHECKSUMSTART, buf.length - 3, buf.length - 2);
		send(buf);
	}
}