package org.jsynthlib.synthdrivers.alesis.qs;

import org.jsynthlib.model.driver.NameValue;
import org.jsynthlib.model.driver.SynthDriverPatchImpl;
import org.jsynthlib.model.driver.SysexHandler;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.synthdrivers.alesis.SysexRoutines;
import org.jsynthlib.tools.ErrorMsgUtil;

/**
 * AlesisQSMixDriver.java
 * 
 * Mix program driver for Alesis QS series synths Feb 2002
 * 
 * @author Zellyn Hunter (zellyn@bigfoot.com, zjh)
 * @version $Id$ GPL v2
 */

public class AlesisQSMixDriver extends SynthDriverPatchImpl {
	public AlesisQSMixDriver() {
		super("Mix", "Zellyn Hunter");
		sysexID = "F000000E0E**";
		sysexRequestDump = new SysexHandler("F0 00 00 0E 0E *opcode* *patchNum* F7");
		// patchSize=350/7*8;
		patchSize = QSConstants.PATCH_SIZE_MIX;
		// zjh - I think this should be 0 so sendPatchWorker doesn't use it
		deviceIDoffset = 0;
		;
		checksumStart = 0;
		checksumEnd = 0;
		checksumOffset = 0;
		// bankNumbers =new String[] {"Internal 1", "Internal 2", "Internal 3", "GenMIDI", "User"};
		bankNumbers = QSConstants.WRITEABLE_BANK_NAMES;
		patchNumbers = QSConstants.PATCH_NUMBERS_MIX_WITH_EDIT_BUFFER;
	}

	/**
	 * Print a byte in binary, for debugging packing/unpacking code
	 **/
	public String toBinaryStr(byte b) {
		String output = new String();
		for (int i = 7; i >= 0; i--) {
			output += ((b >> i) & 1);
		}
		return output;
	}

	/**
	 * Get patch name from sysex buffer
	 * 
	 * @param ip
	 *            the patch to get the name from
	 * @return the name of the patch
	 */
	public String getPatchName(PatchDataImpl ip) {
		// ErrorMsg.reportStatus("Alesis getPatchName ", p.sysex);
		return SysexRoutines.getChars(((PatchDataImpl) ip).getSysex(), QSConstants.HEADER, QSConstants.MIX_NAME_START,
				QSConstants.MIX_NAME_LENGTH);
	}

	/**
	 * Set patch name in sysex buffer
	 * 
	 * @param p
	 *            the patch to set the name in
	 * @param name
	 *            the string to set the name to
	 */
	public void setPatchName(PatchDataImpl p, String name) {
		// ErrorMsg.reportStatus("Alesis setPatchName ", p.sysex);
		SysexRoutines.setChars(name, ((PatchDataImpl) p).getSysex(), QSConstants.HEADER, QSConstants.MIX_NAME_START,
				QSConstants.MIX_NAME_LENGTH);
	}

	/**
	 * Override the checksum and do nothing - the Alesis does not use checksums
	 * 
	 * @param p
	 *            the ignored
	 * @param start
	 *            ignored
	 * @param end
	 *            ignored
	 * @param ofs
	 *            ignored
	 */
	protected void calculateChecksum(PatchDataImpl p, int start, int end, int ofs) {
		// This synth does not use a checksum
	}

	/**
	 * Create a new mix patch
	 * 
	 * @return the new Patch
	 */
	public PatchDataImpl createNewPatch() {
		// Copy over the Alesis QS header
		byte[] sysex = new byte[patchSize];
		for (int i = 0; i < QSConstants.GENERIC_HEADER.length; i++) {
			sysex[i] = QSConstants.GENERIC_HEADER[i];
		}
		// Set it to be a mix, at position 0
		sysex[QSConstants.POSITION_OPCODE] = QSConstants.OPCODE_MIDI_USER_MIX_DUMP;
		sysex[QSConstants.POSITION_LOCATION] = 0;

		// Create the patch, and set the name
		PatchDataImpl p = new PatchDataImpl(sysex, this);
		setPatchName(p, QSConstants.DEFAULT_NAME_MIX);
		return p;
	}

	// public JSLFrame editPatch(Patch p)
	// {
	// return new AlesisQSMixEditor((Patch)p);
	// }

	/**
	 * Copied from Driver.java by zjh. Requests a patch dump. Use opcode 0F - MIDI User Program Dump Request. 100
	 * corresponds to the Mix mode edit buffer
	 * 
	 * @param bankNum
	 *            not used
	 * @param patchNum
	 *            the patch number, 0-100
	 */
	public void requestPatchDump(int bankNum, int patchNum) {
		// setBankNum(bankNum);
		// setPatchNum(patchNum);

		// default to simple case - get specified patch from the User bank
		int location = patchNum;
		int opcode = QSConstants.OPCODE_MIDI_USER_MIX_DUMP_REQ;

		send(sysexRequestDump.toSysexMessage(getChannel(), new NameValue("opcode", opcode),
				new NameValue("patchNum", location)));
	}

	/**
	 * Sends a patch to the synth's mix edit buffer.
	 * 
	 * @param p
	 *            the patch to send to the edit buffer
	 */
	public void sendPatch(PatchDataImpl p) {
		storePatch(p, 0, QSConstants.MAX_LOCATION_MIX + 1);
	}

	/**
	 * Sends a patch to a set location on a synth. See comment for requestPatchDump for explanation of patch numbers. We
	 * save the old values, then set the opcode and target location, then send it, then restore the old values
	 * 
	 * @param p
	 *            the patch to send
	 * @param bankNum
	 *            ignored - you can only send to the User bank on Alesis QS synths
	 * @param patchNum
	 *            the patch number to send it to
	 */
	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		// set specified patch in the User bank
		byte location = (byte) patchNum;
		byte opcode = QSConstants.OPCODE_MIDI_USER_MIX_DUMP;
		byte oldOpcode = ((PatchDataImpl) p).getSysex()[QSConstants.POSITION_OPCODE];
		byte oldLocation = ((PatchDataImpl) p).getSysex()[QSConstants.POSITION_LOCATION];

		// set the opcode and target location
		((PatchDataImpl) p).getSysex()[QSConstants.POSITION_OPCODE] = opcode;
		((PatchDataImpl) p).getSysex()[QSConstants.POSITION_LOCATION] = location;

		ErrorMsgUtil.reportStatus("foo", ((PatchDataImpl) p).getSysex());
		// setBankNum (bankNum);
		// setPatchNum (patchNum);

		// actually send the patch
		sendPatchWorker(p);

		// restore the old values
		((PatchDataImpl) p).getSysex()[QSConstants.POSITION_OPCODE] = oldOpcode;
		((PatchDataImpl) p).getSysex()[QSConstants.POSITION_LOCATION] = oldLocation;
	}
}
