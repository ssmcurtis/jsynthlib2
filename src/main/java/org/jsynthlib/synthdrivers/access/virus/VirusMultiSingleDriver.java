// written by Kenneth L. Martinez

package org.jsynthlib.synthdrivers.access.virus;

import org.jsynthlib.model.driver.NameValue;
import org.jsynthlib.model.driver.SynthDriverPatchImpl;
import org.jsynthlib.model.driver.SysexHandler;
import org.jsynthlib.model.patch.PatchDataImpl;

/**
 * @version $Id$
 * @author Kenneth L. Martinez
 */
public class VirusMultiSingleDriver extends SynthDriverPatchImpl {
//	static final String BANK_LIST[] = new String[] { "User" };
//	static final String PATCH_LIST[] = new String[] { "000", "001", "002", "003", "004", "005", "006", "007", "008", "009", "010", "011",
//			"012", "013", "014", "015", "016", "017", "018", "019", "020", "021", "022", "023", "024", "025", "026", "027", "028", "029",
//			"030", "031", "032", "033", "034", "035", "036", "037", "038", "039", "040", "041", "042", "043", "044", "045", "046", "047",
//			"048", "049", "050", "051", "052", "053", "054", "055", "056", "057", "058", "059", "060", "061", "062", "063", "064", "065",
//			"066", "067", "068", "069", "070", "071", "072", "073", "074", "075", "076", "077", "078", "079", "080", "081", "082", "083",
//			"084", "085", "086", "087", "088", "089", "090", "091", "092", "093", "094", "095", "096", "097", "098", "099", "100", "101",
//			"102", "103", "104", "105", "106", "107", "108", "109", "110", "111", "112", "113", "114", "115", "116", "117", "118", "119",
//			"120", "121", "122", "123", "124", "125", "126", "127" };
	static final int BANK_NUM_OFFSET = 7;
	static final int PATCH_NUM_OFFSET = 8;

	public VirusMultiSingleDriver() {
		super("Multi Single", "Kenneth L. Martinez");
		sysexID = "F000203301**11";
		sysexRequestDump = new SysexHandler("F0 00 20 33 01 10 31 01 *patchNum* F7");

		patchSize = 267;
		patchNameStart = 13;
		patchNameSize = 10;
		deviceIDoffset = 5;
		checksumOffset = 265;
		checksumStart = 5;
		checksumEnd = 264;
		bankNumbers = Virus.BANK_NAMES_MULTI;
		patchNumbers = Virus.createPatchNumbers();
	}

	@Override
	protected void calculateChecksum(PatchDataImpl p, int start, int end, int ofs) {
		int sum = 0;
		for (int i = start; i <= end; i++) {
			sum += p.getSysex()[i];
		}
		p.getSysex()[ofs] = (byte) (sum & 0x7F);
	}

	@Override
	public void sendPatch(PatchDataImpl p) {
		sendPatch((PatchDataImpl) p, 0, 0); // using single mode edit buffer
	}

	private void sendPatch(PatchDataImpl p, int bankNum, int patchNum) {
		PatchDataImpl p2 = new PatchDataImpl(p.getSysex());
		p2.getSysex()[deviceIDoffset] = (byte) (getDeviceID() - 1);
		p2.getSysex()[BANK_NUM_OFFSET] = (byte) bankNum;
		p2.getSysex()[PATCH_NUM_OFFSET] = (byte) patchNum;
		calculateChecksum(p2);
		sendPatchWorker(p2);
	}

	@Override
	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		sendPatch((PatchDataImpl) p, 1, patchNum);
	}

	@Override
	public void playPatch(PatchDataImpl p) {
		PatchDataImpl p2 = new PatchDataImpl(((PatchDataImpl) p).getSysex());
		p2.getSysex()[deviceIDoffset] = (byte) (getDeviceID() - 1);
		p2.getSysex()[BANK_NUM_OFFSET] = 0; // edit buffer
		p2.getSysex()[PATCH_NUM_OFFSET] = 0; // single mode
		calculateChecksum(p2);
		super.playPatch(p2);
	}

	@Override
	public PatchDataImpl createNewPatch() {
		return new PatchDataImpl(Virus.NEW_MULTI_PATCH, this);
	}

	@Override
	public void requestPatchDump(int bankNum, int patchNum) {
		send(sysexRequestDump.toSysexMessage(getDeviceID(), new NameValue("bankNum", bankNum), new NameValue(
				"patchNum", patchNum)));
	}
}
