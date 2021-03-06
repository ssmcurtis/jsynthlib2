/*
 * Copyright 2004 Joachim Backhaus
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
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

package org.jsynthlib.synthdrivers.yamaha.tg100;

import org.jsynthlib.model.driver.ConverterImpl;
import org.jsynthlib.model.patch.PatchDataImpl;

/**
 * Converts temporary parameter SysEx data (644 Bytes) to a "Single Performance" (223 Bytes)
 * 
 * @author Joachim Backhaus
 * @version $Id$
 */
public class YamahaTG100AllConverter extends ConverterImpl {

	public YamahaTG100AllConverter() {
		super("All Dump Converter", "Joachim Backhaus");

		this.sysexID = TG100Constants.SYSEX_ID;
		this.patchSize = TG100Constants.ALL_DUMP_SIZE;
		/* This doesn't seem to be used */
		// this.deviceIDoffset = 0;
	}

	/**
	 * Converts 8266 Byte sysex files to the 6720 Bytes of a Voice Bank
	 */
	public PatchDataImpl[] extractPatch(PatchDataImpl p) {
		byte[] sysex = p.getByteArray();
		PatchDataImpl[] newPatchArray = new PatchDataImpl[1];
		byte[] temporarySysex = new byte[TG100Constants.PATCH_SIZE * TG100Constants.PATCH_NUMBER_LENGTH];

		System.arraycopy(sysex, TG100Constants.ALL_DUMP_OFFSET, temporarySysex, 0, TG100Constants.PATCH_SIZE
				* TG100Constants.PATCH_NUMBER_LENGTH);

		newPatchArray[0] = new PatchDataImpl(temporarySysex, getDevice());

		return newPatchArray;
	}
}
