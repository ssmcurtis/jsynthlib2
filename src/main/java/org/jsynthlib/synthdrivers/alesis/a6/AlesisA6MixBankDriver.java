// written by Kenneth L. Martinez
//
// @version $Id$

package org.jsynthlib.synthdrivers.alesis.a6;

import javax.swing.JOptionPane;

import org.jsynthlib.PatchBayApplication;
import org.jsynthlib.menu.patch.BankDriver;
import org.jsynthlib.menu.patch.Patch;
import org.jsynthlib.menu.patch.SysexHandler;
import org.jsynthlib.tools.ErrorMsg;

public class AlesisA6MixBankDriver extends BankDriver {

	public AlesisA6MixBankDriver() {
		super("Mix Bank", "Kenneth L. Martinez", AlesisA6PgmSingleDriver.patchList.length, 4);
		sysexID = "F000000E1D04**00";
		sysexRequestDump = new SysexHandler("F0 00 00 0E 1D 0B *bankNum* F7");
		patchSize = 151040;
		patchNameStart = 2; // does NOT include sysex header
		patchNameSize = 16;
		deviceIDoffset = -1;
		bankNumbers = AlesisA6PgmSingleDriver.bankList;
		patchNumbers = AlesisA6PgmSingleDriver.patchList;
		singleSize = 1180;
		singleSysexID = "F000000E1D04";
	}

	public void calculateChecksum(Patch p) {
		// A6 doesn't use checksum
	}

	// protected static void calculateChecksum(Patch p, int start, int end, int ofs)
	// {
	// // A6 doesn't use checksum
	// }

	public void storePatch(Patch p, int bankNum, int patchNum) {
		if (bankNum == 1 || bankNum == 2)
			JOptionPane.showMessageDialog(PatchBayApplication.getInstance(), "Cannot send to a preset bank", "Store Patch",
					JOptionPane.WARNING_MESSAGE);
		else
			sendPatchWorker((Patch) p, bankNum);
	}

	public void putPatch(Patch bank, Patch p, int patchNum) {
		if (!canHoldPatch(p)) {
			ErrorMsg.reportError("Error", "This type of patch does not fit in to this type of bank.");
			return;
		}

		System.arraycopy(((Patch) p).getSysex(), 0, ((Patch) bank).getSysex(), patchNum * 1180, 1180);
		((Patch) bank).getSysex()[patchNum * 1180 + 6] = 0; // user bank
		((Patch) bank).getSysex()[patchNum * 1180 + 7] = (byte) patchNum; // set mix #
	}

	public Patch getPatch(Patch bank, int patchNum) {
		byte sysex[] = new byte[1180];
		System.arraycopy(((Patch) bank).getSysex(), patchNum * 1180, sysex, 0, 1180);
		return new Patch(sysex, getDevice());
	}

	public String getPatchName(Patch p, int patchNum) {
		Patch Mix = (Patch) getPatch(p, patchNum);
		try {
			char c[] = new char[patchNameSize];
			for (int i = 0; i < patchNameSize; i++)
				c[i] = (char) (AlesisA6PgmSingleDriver.getA6PgmByte(Mix.getSysex(), i + patchNameStart));
			return new String(c);
		} catch (Exception ex) {
			return "-";
		}
	}

	public void setPatchName(Patch p, int patchNum, String name) {
		Patch Mix = (Patch) getPatch(p, patchNum);
		if (name.length() < patchNameSize + 4)
			name = name + "                ";
		byte nameByte[] = name.getBytes();
		for (int i = 0; i < patchNameSize; i++) {
			AlesisA6PgmSingleDriver.setA6PgmByte(nameByte[i], Mix.getSysex(), i + patchNameStart);
		}
		putPatch(p, Mix, patchNum);
	}

	// protected void sendPatch (Patch p)
	// {
	// sendPatchWorker((Patch)p, 0);
	// }

	protected void sendPatchWorker(Patch p, int bankNum) {
		byte tmp[] = new byte[1180]; // send in 128 single-mix messages
		try {
			PatchBayApplication.showWaitDialog();
			for (int i = 0; i < 128; i++) {
				System.arraycopy(p.getSysex(), i * 1180, tmp, 0, 1180);
				tmp[6] = (byte) bankNum;
				tmp[7] = (byte) i; // mix #
				send(tmp);
				Thread.sleep(15);
			}
			PatchBayApplication.hideWaitDialog();
		} catch (Exception e) {
			ErrorMsg.reportStatus(e);
			ErrorMsg.reportError("Error", "Unable to send Patch");
		}
	}

	public void requestPatchDump(int bankNum, int patchNum) {
		send(sysexRequestDump.toSysexMessage(((byte) getChannel()), new SysexHandler.NameValue[] {
				new SysexHandler.NameValue("bankNum", bankNum), new SysexHandler.NameValue("patchNum", patchNum) }));
	}
}