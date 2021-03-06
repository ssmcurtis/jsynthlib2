package org.jsynthlib.synthdrivers.korg.x3;

import org.jsynthlib.model.driver.ConverterImpl;
import org.jsynthlib.model.patch.PatchDataImpl;

/**
 * Converts Korg X3 Single Bank data from 188 bytes to 164 bytes. The actual patch sizes in JSynthLib are 212 and 187
 * bytes because of the header information.
 * 
 * @author Juha Tukkinen
 * @version $Id$
 */
public class KorgX3SingleConverter extends ConverterImpl {

	/**
	 * Default constructor. Note that patchsize must be 212.
	 */
	public KorgX3SingleConverter() {
		super("Single Dump Converter", "Juha Tukkinen");
		sysexID = "F042**35";
		patchSize = 212;
	}

	/*
	 * public static byte getBit(byte b, byte n) { return (byte)((b & ( (byte)0x01 << n )) >> n); }
	 */

	/**
	 * The actual conversion. The MIDI data consists of 188 bytes which are transformed to 164 bytes. In MIDI data,
	 * every 7th byte has the next seven bytes' uppest bits.
	 * 
	 * @param p
	 *            Patch to be converted
	 * @return Converted patch
	 */
	public PatchDataImpl[] extractPatch(PatchDataImpl p) {
		byte[] sysex = p.getByteArray();
		byte[] ps; // source
		byte[] pd; // destination

		ps = new byte[188 + KorgX3SingleDriver.EXTRA_HEADER]; // patch with 7-bit bytes
		pd = new byte[164 + KorgX3SingleDriver.EXTRA_HEADER]; // patch as normal bytearray

		System.arraycopy(sysex, 0, ps, 0, 188 + KorgX3SingleDriver.EXTRA_HEADER);
		System.arraycopy(sysex, 0, pd, 0, KorgX3SingleDriver.EXTRA_HEADER);
		int j = KorgX3SingleDriver.EXTRA_HEADER;
		byte b7 = 0; // bit 7
		for (int i = 0; i < 188; i++) {
			if (i % 8 == 0) {
				b7 = ps[i + KorgX3SingleDriver.EXTRA_HEADER];
			} else {
				// actually this is done: byte b8 = (byte)(getBit( b7, (byte)( ((i%8)-1) ) ) << 7);
				byte b8 = (byte) (((b7 & ((byte) 0x01 << (((i % 8) - 1)))) >> (((i % 8) - 1))) << 7);

				pd[j] = (byte) ((byte) ps[i + KorgX3SingleDriver.EXTRA_HEADER] + (byte) b8);
				j++;
			}
		}

		PatchDataImpl[] pf = new PatchDataImpl[1];
		pf[0] = new PatchDataImpl(pd);
		return pf;
	}
}
