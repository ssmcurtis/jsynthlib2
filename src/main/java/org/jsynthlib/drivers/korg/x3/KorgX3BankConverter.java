package org.jsynthlib.drivers.korg.x3;

import org.jsynthlib.core.Converter;
import org.jsynthlib.core.Patch;

/**
 * Converts Korg X3 Program Bank data from 37486 bytes to 32800 bytes (164
 * bytes*200 patches). The actual patch sizes in JSynthLib differ because of the
 * header information.
 * 
 * @author Juha Tukkinen
 */
public class KorgX3BankConverter extends Converter {

	/**
	 * Default constructor. Notice that patchSize must be 37493.
	 */
	public KorgX3BankConverter() {
		super("Bank Dump Converter", "Juha Tukkinen");
		sysexID = "F042**35";
		patchSize = 37493;
	}

	/**
	 * The actual conversion. In MIDI data, every 7th byte has the next seven
	 * bytes' uppest bits.
	 * 
	 * @param p
	 *            Patch to be converted
	 * @return Converted patch
	 */
	public Patch[] extractPatch(Patch p) {
		byte[] sysex = p.getByteArray();
		byte[] pd;
		pd = new byte[37600 + KorgX3BankDriver.EXTRA_HEADER];
		// 37600 = 188*200
		System.arraycopy(sysex, 0, pd, 0, KorgX3BankDriver.EXTRA_HEADER);

		int j = KorgX3BankDriver.EXTRA_HEADER;
		byte b7 = 0; // bit 7
		for (int i = 0; i < 37486; i++) {
			if (i % 8 == 0) {
				b7 = sysex[i + KorgX3BankDriver.EXTRA_HEADER];
			} else {
				// byte b8 = (byte)(getBit( b7, (byte)( ((i%8)-1) ) ) << 7);
				byte b8 = (byte) (((b7 & ((byte) 0x01 << (((i % 8) - 1)))) >> (((i % 8) - 1))) << 7);

				pd[j] = (byte) ((byte) sysex[i + KorgX3BankDriver.EXTRA_HEADER] + (byte) b8);
				j++;
			}
		}

		Patch[] pf = new Patch[1];
		pf[0] = new Patch(pd);
		return pf;
	}
}
