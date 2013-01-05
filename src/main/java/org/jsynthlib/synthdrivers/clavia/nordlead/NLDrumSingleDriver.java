// written by Kenneth L. Martinez
// $Id$
package org.jsynthlib.synthdrivers.clavia.nordlead;

import org.jsynthlib.model.driver.SynthDriverPatchImpl;
import org.jsynthlib.model.driver.SysexHandler;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.ErrorMsgUtil;

public class NLDrumSingleDriver extends SynthDriverPatchImpl {
	static final String BANK_LIST[] = new String[] { "ROM", "PCMCIA 1", "PCMCIA 2", "PCMCIA 3" };
	static final String PATCH_LIST[] = new String[] { "P0", "P1", "P2", "P3", "P4", "P5", "P6", "P7", "P8", "P9" };
	static final int BANK_NUM_OFFSET = 4;
	static final int PATCH_NUM_OFFSET = 5;
	static final byte NEW_PATCH[] = { (byte) 0xF0, (byte) 0x33, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x10,
			(byte) 0x0A, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x0F, (byte) 0x07, (byte) 0x0C, (byte) 0x01,
			(byte) 0x02, (byte) 0x01, (byte) 0x05, (byte) 0x06, (byte) 0x0F, (byte) 0x01, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x06, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x0C, (byte) 0x02,
			(byte) 0x00, (byte) 0x01, (byte) 0x08, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x0E, (byte) 0x02,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x0F, (byte) 0x01, (byte) 0x0B, (byte) 0x02,
			(byte) 0x08, (byte) 0x05, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x05,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x07, (byte) 0x05, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x02, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x03, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00,
			(byte) 0x02, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x08, (byte) 0x07, (byte) 0x0F, (byte) 0x07,
			(byte) 0x0B, (byte) 0x03, (byte) 0x0F, (byte) 0x07, (byte) 0x0C, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x06, (byte) 0x08, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01,
			(byte) 0x08, (byte) 0x07, (byte) 0x0A, (byte) 0x05, (byte) 0x0A, (byte) 0x00, (byte) 0x00, (byte) 0x03,
			(byte) 0x00, (byte) 0x00, (byte) 0x06, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x0C, (byte) 0x01,
			(byte) 0x00, (byte) 0x06, (byte) 0x00, (byte) 0x00, (byte) 0x0F, (byte) 0x07, (byte) 0x0F, (byte) 0x07,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x05, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x04, (byte) 0x0F, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x07, (byte) 0x06, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x02, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00,
			(byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x02, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x04, (byte) 0x00,
			(byte) 0x08, (byte) 0x07, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x07,
			(byte) 0x04, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x0F, (byte) 0x01, (byte) 0x01, (byte) 0x06,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x06, (byte) 0x00, (byte) 0x0E, (byte) 0x07,
			(byte) 0x0E, (byte) 0x00, (byte) 0x08, (byte) 0x02, (byte) 0x06, (byte) 0x00, (byte) 0x0E, (byte) 0x04,
			(byte) 0x00, (byte) 0x00, (byte) 0x0D, (byte) 0x01, (byte) 0x0F, (byte) 0x01, (byte) 0x0F, (byte) 0x07,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x05,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x09, (byte) 0x05, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x03, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x03, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x04, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x0A, (byte) 0x05, (byte) 0x0F, (byte) 0x07,
			(byte) 0x05, (byte) 0x01, (byte) 0x05, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x01, (byte) 0x04, (byte) 0x06, (byte) 0x05, (byte) 0x00, (byte) 0x00, (byte) 0x0A, (byte) 0x05,
			(byte) 0x00, (byte) 0x00, (byte) 0x0C, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x0C, (byte) 0x03,
			(byte) 0x00, (byte) 0x00, (byte) 0x0C, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x06, (byte) 0x02,
			(byte) 0x00, (byte) 0x00, (byte) 0x0B, (byte) 0x03, (byte) 0x0C, (byte) 0x06, (byte) 0x0F, (byte) 0x07,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x0C, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x02, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x02, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x03, (byte) 0x00,
			(byte) 0x0C, (byte) 0x02, (byte) 0x0F, (byte) 0x07, (byte) 0x04, (byte) 0x04, (byte) 0x0E, (byte) 0x02,
			(byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0x02, (byte) 0x01, (byte) 0x04, (byte) 0x06, (byte) 0x05,
			(byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x06, (byte) 0x02,
			(byte) 0x04, (byte) 0x02, (byte) 0x08, (byte) 0x05, (byte) 0x00, (byte) 0x00, (byte) 0x0E, (byte) 0x04,
			(byte) 0x00, (byte) 0x00, (byte) 0x0F, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x05,
			(byte) 0x04, (byte) 0x04, (byte) 0x0F, (byte) 0x07, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x05,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x0E, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x09, (byte) 0x02, (byte) 0x00, (byte) 0x00,
			(byte) 0x06, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x0A, (byte) 0x0E, (byte) 0x01, (byte) 0x0F, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0x05, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x02, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x03, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x03, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x06, (byte) 0x03, (byte) 0x0F, (byte) 0x07,
			(byte) 0x04, (byte) 0x04, (byte) 0x0E, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0x02,
			(byte) 0x01, (byte) 0x04, (byte) 0x06, (byte) 0x05, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x01,
			(byte) 0x00, (byte) 0x00, (byte) 0x06, (byte) 0x02, (byte) 0x04, (byte) 0x02, (byte) 0x08, (byte) 0x05,
			(byte) 0x00, (byte) 0x00, (byte) 0x0E, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x0F, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x05, (byte) 0x04, (byte) 0x04, (byte) 0x0F, (byte) 0x07,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x05, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0E, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x09, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x06, (byte) 0x01, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0A, (byte) 0x0E,
			(byte) 0x01, (byte) 0x0F, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x04, (byte) 0x05, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x01,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x02, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x02, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x04, (byte) 0x00,
			(byte) 0x0C, (byte) 0x03, (byte) 0x0F, (byte) 0x07, (byte) 0x04, (byte) 0x04, (byte) 0x0E, (byte) 0x02,
			(byte) 0x05, (byte) 0x01, (byte) 0x04, (byte) 0x02, (byte) 0x01, (byte) 0x04, (byte) 0x06, (byte) 0x05,
			(byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x06, (byte) 0x02,
			(byte) 0x04, (byte) 0x02, (byte) 0x08, (byte) 0x05, (byte) 0x00, (byte) 0x00, (byte) 0x0E, (byte) 0x04,
			(byte) 0x00, (byte) 0x00, (byte) 0x0F, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x05,
			(byte) 0x04, (byte) 0x04, (byte) 0x0F, (byte) 0x07, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x05,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x0E, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x09, (byte) 0x02, (byte) 0x00, (byte) 0x00,
			(byte) 0x06, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x0A, (byte) 0x0E, (byte) 0x01, (byte) 0x0F, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0x05, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x02, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x03, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x04, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x01, (byte) 0x04, (byte) 0x0F, (byte) 0x07,
			(byte) 0x00, (byte) 0x00, (byte) 0x05, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x01, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x02,
			(byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x0A, (byte) 0x03,
			(byte) 0x00, (byte) 0x00, (byte) 0x0E, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x0F, (byte) 0x01,
			(byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x03, (byte) 0x00, (byte) 0x07, (byte) 0x0F, (byte) 0x07,
			(byte) 0x00, (byte) 0x00, (byte) 0x07, (byte) 0x07, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x07, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x02, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x00,
			(byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x02, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x04, (byte) 0x00,
			(byte) 0xF7 };

	public NLDrumSingleDriver() {
		super("Drum Single", "Kenneth L. Martinez");
		sysexID = "F033**04**";
		sysexRequestDump = new SysexHandler("F0 33 @@ 04 *bankNum* *patchNum* F7");

		patchSize = 1063;
		patchNameStart = -1;
		patchNameSize = 0;
		deviceIDoffset = 2;
		bankNumbers = BANK_LIST;
		patchNumbers = PATCH_LIST;
	}

	public void calculateChecksum(PatchDataImpl p) {
		// doesn't use checksum
	}

	// protected static void calculateChecksum(Patch p, int start, int end, int ofs) {
	// // doesn't use checksum
	// }

	public String getPatchName(PatchDataImpl p) {
		PatchDataImpl ip = (PatchDataImpl) p;
		if (ip.getSysex()[PATCH_NUM_OFFSET] < 99) {
			return "drum Edit" + (ip.getSysex()[PATCH_NUM_OFFSET] - 16);
		} else {
			return "drum P" + (ip.getSysex()[PATCH_NUM_OFFSET] - 99);
		}
	}

	public void setPatchName(PatchDataImpl p, String name) {
	}

	public void sendPatch(PatchDataImpl p) {
		sendPatch((PatchDataImpl) p, 0, 16); // using edit buffer for slot A
	}

	public void sendPatch(PatchDataImpl p, int bankNum, int patchNum) {
		PatchDataImpl p2 = new PatchDataImpl(p.getSysex());
		p2.getSysex()[BANK_NUM_OFFSET] = (byte) bankNum;
		p2.getSysex()[PATCH_NUM_OFFSET] = (byte) patchNum;
		mySendPatch(p2);
	}

	// Sends a patch to a set location in the user bank
	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		setBankNum(bankNum); // must set bank - sysex patch dump always stored in current bank
		setPatchNum(patchNum); // must send program change to make bank change take effect
		sendPatch((PatchDataImpl) p, bankNum + 1, patchNum + 99);
		setPatchNum(patchNum); // send another program change to get new sound in edit buffer
	}

	public void playPatch(PatchDataImpl p) {
		byte sysex[] = new byte[patchSize];
		System.arraycopy(((PatchDataImpl) p).getSysex(), 0, sysex, 0, patchSize);
		sysex[BANK_NUM_OFFSET] = 0; // edit buffer
		sysex[PATCH_NUM_OFFSET] = 16; // slot A
		PatchDataImpl p2 = new PatchDataImpl(sysex);
		super.playPatch(p2);
	}

	public PatchDataImpl createNewPatch() {
		return new PatchDataImpl(NEW_PATCH, this);
	}

	protected void mySendPatch(PatchDataImpl p) {
		p.getSysex()[deviceIDoffset] = (byte) (((NordLeadDevice) getDevice()).getGlobalChannel() - 1);
		try {
			send(p.getSysex());
		} catch (Exception e) {
			ErrorMsgUtil.reportStatus(e);
		}
	}

	public void requestPatchDump(int bankNum, int patchNum) {
		setBankNum(bankNum); // kludge: drum dump request sends 1063 bytes of garbage -
		setPatchNum(patchNum + 99); // select drum sound, then get data from edit buffer
		send(sysexRequestDump.toSysexMessage(((NordLeadDevice) getDevice()).getGlobalChannel(),
				new SysexHandler.NameValue("bankNum", 10), new SysexHandler.NameValue("patchNum", 0)));
	}
}
