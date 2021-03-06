package org.jsynthlib.model.patch;

import java.awt.datatransfer.Transferable;
import java.io.Serializable;

import javax.sound.midi.SysexMessage;

import org.jsynthlib.menu.JSLFrame;
import org.jsynthlib.model.device.Device;
import org.jsynthlib.model.driver.SynthDriver;
import org.jsynthlib.model.driver.SynthDriverPatch;
import org.jsynthlib.tools.DriverUtil;

/**
 * Common interface for a Single Patch and a Bank Patch.
 * 
 * @version $Id$
 */
public interface Patch extends Cloneable, Transferable, Serializable {

	/** Getter for date. */
	String getDate();

	/** Setter for date. */
	void setDate(String date);

	/** Getter for author of the patch. */
	String getAuthor();

	/** Setter for author of the patch. */
	void setAuthor(String author);

	/** Getter for comment. */
	String getComment();

	/** Setter for comment. */
	void setComment(String comment);

	/** Return Device for this patch. */
	Device getDevice();

	/** Return Driver for this patch. */
	SynthDriverPatch getDriver();

	/** Set driver. */
	void setDriver(SynthDriverPatch driver);

	/**
	 * Set driver which is for the sysex data.
	 * 
	 * @see DriverUtil#chooseDriver(byte[])
	 */
	void findDriver();

	/**
	 * Check if the Patch's driver is null driver (Generic driver).
	 */
	boolean hasNullDriver();

	/**
	 * Return a hexadecimal string for {@link SynthDriver#supportsPatch IDriver.suppportsPatch} at most 16 byte sysex data.
	 * 
	 * @see SynthDriver#supportsPatch
	 */
	String getPatchHeader();

	/**
	 * Returns the patch's name. This is not necessarily the name stored on the synth.
	 */
	String getName();

	/** Set the patch's name. */
	void setName(String name);

	/**
	 * Returns true if a Patch Editor Window is implemented.
	 * 
	 * @see #edit
	 */
	boolean hasEditor();

	/**
	 * Returns a Patch Editor Window for this Patch. Returns <code>null</code> if there is no editor. XXX throw an
	 * Exception?
	 */
	JSLFrame edit();

	/**
	 * Sends a patch to a set location on a synth.
	 */
	void send(int bankNum, int patchNum);

	/** Get an array of sysex messages representing this patch. */
	SysexMessage[] getMessages();

	/**
	 * Get a byte array representing this patch. The checksum is calculated.
	 * 
	 * @return a byte array of Sysex data.
	 * @see #getByteArray()
	 */
	byte[] export();

	/**
	 * Get a byte array representing this patch. The checksum may not be calculated.
	 * <p>
	 * According to the implementation of IPatch interface, this method may be expensive. Be careful to use this. You
	 * may want to add a new method to IPatch interface, as getSize() or lookupManufacturer().
	 * 
	 * @see #export()
	 */
	byte[] getByteArray();

	/** Get the size (number of byte) of Patch. */
	int getSize();

	/** Get type of Patch. */
	String getType();

	/**
	 * Get the maximum number of characters in the patch name. (0 if no name)
	 */
	int getNameSize();

	/** Look up manufacturer name from Sysex data. */
	String lookupManufacturer();

	/** Check if a Single Patch. */
	boolean isSinglePatch();

	/** Check if a Bank Patch. */
	boolean isBankPatch();

	/**
	 * Change this patch to contain the same data as p. Used for backing up edited patches.
	 * 
	 * @param p
	 *            Patch whose data we should use.
	 */
	void useSysexFromPatch(Patch p);

	/** create a clone of the patch. */
	Object clone();

	public String getFileName();

	public void setFileName(String fileName);

	public String getPatchId();

	/**
	 * Should be not changeable
	 * 
	 * @param patchId
	 */
	public void setPatchId(String patchId);

	public String getInfo();

	public void setInfo(String info);

	public int getPatchSize();

	public Integer getScore();
	
	public void setScore(int score);

	public void addScore(int score);
	
	public void setSelected(boolean selected);

	public boolean getSelected();

}