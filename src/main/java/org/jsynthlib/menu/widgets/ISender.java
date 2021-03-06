package org.jsynthlib.menu.widgets;

import org.jsynthlib.model.driver.SynthDriverPatch;

/**
 * Interface for Sender.
 * 
 * Sender sends MidiMessage[s] by using <code>send(IPatchDriver, int)</code> method. Every time a widget moves, its
 * Sender gets told. The MidiMessage will be sent to the synth informing it of the change. Usually a Single Editor
 * will have one or more Sender. Sometimes more than one is used because a synth uses more than one method to
 * transfer the data.
 */
public interface ISender {
	/**
	 * Send MIDI message[s] for <code>value</code>.
	 * 
	 * @param value
	 *            an <code>int</code> value
	 */
	void send(SynthDriverPatch driver, int value);
}