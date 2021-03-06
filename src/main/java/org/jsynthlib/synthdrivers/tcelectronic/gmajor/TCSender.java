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

package org.jsynthlib.synthdrivers.tcelectronic.gmajor;

import org.jsynthlib.menu.widgets.SysexSender;
import org.jsynthlib.model.patch.PatchDataImpl;

class TCSender extends SysexSender {
	int offs, delta;
	PatchDataImpl patch;

	public TCSender(PatchDataImpl iPatch, int iParam) {
		patch = iPatch;
		offs = iParam;
	}

	public TCSender(PatchDataImpl iPatch, int iParam, int idelta) {
		this(iPatch, iParam);
		delta = idelta;
	}

	public byte[] generate(int value) {
		// TODO: EEN SEND METHODE BEDENKEN WAARBIJ DIT AUTOMATISCH WORDT GEREGELD, ZONDER DE STORE FUNCTIE TE
		// ONTREGELEN.
		// TODO: STORE ZET PATCH- EN BANKNUMMER EN GEBRUIKT SEND.
		patch.getSysex()[7] = (byte) 0x00;
		patch.getSysex()[8] = (byte) 0x00;

		value = value + delta;

		TCElectronicGMajorUtil.setValue(patch.getSysex(), value, offs);
		patch.getSysex()[TCElectronicGMajorConst.CHECKSUMOFFSET] = TCElectronicGMajorUtil.calcChecksum(patch.getSysex());
		return patch.getSysex();
	}
}
