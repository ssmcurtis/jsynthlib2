package core;
import java.io.*;
import javax.swing.*;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.InvalidMidiDataException;
import java.text.*;
// import java.util.Set;
// import java.util.HashSet;
// import java.util.Arrays;

/**
 * This is the base class for all Drivers.<p>
 *
 * Compatibility Note: The following fields are now
 * <code>private</code>.  Use setter/getter method to access them.
 * <pre>
 *	device, patchType, authors
 * </pre>
 * Compatibility Note: The following fields are now obsoleted.  Use a
 * getter method to access them.  The getter method queries parent
 * Device object.
 * <pre>
 *	deviceNum, driverNum,
 *	channel, port, inPort, manufacturer, model, inquiryID, id
 * </pre>
 * Compatibility Note:
 *	SysexHandler.send(getPort(), sysex);
 * or
 *	PatchEdit.MidiOut.writeLongMessage(getPort(), sysex);
 * was replaced by
 *	send(sysex);
 *
 * @author Brian Klock
 * @version $Id$
 */
public class Driver extends Object /*implements Serializable, Storable*/ {
    /**
     * Which device does this driver go with?
     */
    private Device device;

    // deviceNum and driverNum are set by
    // PatchEdit.appConfig.reassignDeviceDriverNums method.
    /** Which deviceNum does the device of this driver goes with? */
    //private int deviceNum;
    /** Which driverNum does the device of this driver goes with? */
    //private int driverNum;

    /**
     * The patch type. eg. "Single", "Bank", "Drumkit", etc.
     */
    private String patchType;

    /**
     * The names of the authors of this driver.
     */
    private String authors;

    /*
     * The following fields are used by default methods defined in
     * this file.  If your extending driver can use a default method
     * as is, set the corresponding fields.  Otherwise override the
     * method.
     */
    // for default set/getPatchName methods
    /**
     * The offset in the patch where the patchname starts. '0' if
     * patch is not named -- remember all offsets are zero based.
     * @see #setPatchName
     * @see #getPatchName
     */
    protected int patchNameStart;
    /**
     * Number of characters in the patch name. (0 if no name)
     * @see #setPatchName
     * @see #getPatchName
     */
    protected int patchNameSize;

    // for default calculateCheckSum(Patch) method
    /**
     * Offset of checksum byte.<p>
     * Need to be set if default <code>calculateChecksum(Patch)</code>
     * method is used.
     * @see #calculateChecksum(Patch)
     */
    protected int checksumOffset;
    /**
     * Start of range that Checksum covers.<p>
     * Need to be set if default <code>calculateChecksum(Patch)</code>
     * method is used.
     * @see #calculateChecksum(Patch)
     */
    protected int checksumStart;
    /**
     * End of range that Checksum covers.<p>
     * Need to be set if default <code>calculateChecksum(Patch)</code>
     * method is used.
     * @see #calculateChecksum(Patch)
     */
    protected int checksumEnd;

    // for default trimSysex method
    /**
     * The size of the patch for trimming purposes.
     * @see #trimSysex
     */
    protected int trimSize = 0;

    // for default choosePatch methods
    /**
     * Array holding names/numbers/names for all banks.
     * @see #choosePatch(Patch,int,int)
     * @see #choosePatch(Patch)
     */
    protected String[] bankNumbers;
    /**
     * Array holding names/numbers for all patches.
     * @see #choosePatch(Patch,int,int)
     * @see #choosePatch(Patch)
     */
    protected String[] patchNumbers;

    // for default supportsPatch method
    /**
     * The size of the patch this Driver supports (or 0 for variable).
     * @see #supportsPatch
     */
    protected int patchSize;
    /**
     * The hex header that sysex files of the format this driver
     * supports will have.  The program will attempt to match loaded
     * sysex drivers with the sysexID of a loaded driver.  It can be
     * up to 16 bytes and have wildcards (<code>*</code>).
     * (ex. <code>"F041**003F12"</code>)
     * @see #supportsPatch
     */
    protected String sysexID;

    // for sendPatchWorker method
    /**
     * Offset of deviceID in sysex. Used by
     * <code>sendPatchWorker</code> method.
     * @see #sendPatchWorker
     */
    protected int deviceIDoffset;  // array index of device ID

    /**
     * SysexHandler object to request dump.  You don't have to use
     * this field if you override <code>requestPatchDump</code>
     * method.
     * @see #requestPatchDump
     * @see SysexHandler
     */				// - phil@muqus.com
    protected SysexHandler sysexRequestDump = null;

    /** Number of sysex messages in patch dump.  Not used now. */
    protected int numSysexMsgs;

    /*
     * The following fields are obsoleted when the Device class was
     * introduced.  Use getter functions to access them.
     */
    /**
     * The channel the user assigns to this driver.
     */
    //private int channel = 1;

    /**
     * The MIDI Out port the user assigns to this driver.
     */
    //private int port;
    /**
     * The MIDI In port the user assigns to this driver.
     */
    //private int inPort;	// phil@muqus.com
    /**
     * The company which made the Synthesizer
     */
    //private String manufacturer;
    /**
     * The models supported by this driver eg TG33/SY22
     */
    //private String model;
    /**
     * The response to the Universal Inquiry Message.  It can have
     * wildcards (*). It can be up to 16 bytes
     */
    // ADDED BY GERRIT GEHNEN
    //private String inquiryID;
    /**
     * A Shorthand alias for the Synth this driver supports (eg TG33,K5k)
     */
    //private String id;

    /**
     * Creates a new <code>Driver</code> instance.
     *
     * @param patchType The patch type. eg. "Single", "Bank",
     * "Drumkit", etc.
     * @param authors The names of the authors of this driver.
     */
    public Driver(String patchType, String authors) {
	this.patchType = patchType;
	this.authors = authors;
    }

    /**
     * Constructs a generic Driver.
     * @deprecated Use Driver(String, String).
     */
    public Driver() {
	/*
        sysexID = "MATCHNONE";
	inquiryID = "NONE";
        authors = "Brian Klock";
	manufacturer = "Generic";
	model = "";
        patchType = "Sysex";
// 	id = "???";
        patchNameSize = 0;
        patchNumbers = new String[128];
        for (int i = 0; i < 128; i++)
	    patchNumbers[i] = String.valueOf(i);
        bankNumbers = new String[] {"0"};
	*/
    }

    //
    // Setters and Getters
    //
    /** Setter for property <code>device</code>. */
    void setDevice(Device d) {
	device = d;
    }
    /** Getter for property <code>device</code>. */
    protected Device getDevice() {
	return device;
    }
    /**
     * Setter for property <code>deviceNum</code>.
     * Don't use this. Only for backward compatibility.
     */
    /*
    void setDeviceNum(int deviceNum) {
	this.deviceNum = deviceNum;
    }
    */
    /**
     * Getter for property <code>deviceNum</code>.<p>
     * This method will be deprecated.
     * Use <code>getDevice()</code> instead of
     * <code>PatchEdit.appConfig.getDevice(getDeviceNum())</code>.
     * Use <code>new Patch(sysex, getDevice())</code> instead of
     * <code>new Patch(getDeviceNum(), sysex)</code>.
     */
    protected int getDeviceNum() {
// 	return this.deviceNum;
	return device.getDeviceNum();
    }
    /**
     * Setter for property <code>driverNum</code>.
     * Don't use this. Only for backward compatibility.
     */
    /*
    void setDriverNum(int driverNum) {
	this.driverNum = driverNum;
    }
    */
    /**
     * Getter for property <code>driverNum</code>.<p>
     * This method will be deprecated.
     * Use <code>new Patch(sysex, this)</code> instead of
     * <code>new Patch(sysex, getDeviceNum(), getDriverNum())</code>.
     */
    protected int getDriverNum() {
 	return device.driverList.indexOf(this);
    }
    /** Getter for property <code>patchType</code>. */
    protected String getPatchType() {
	return patchType;
    }
    /** Getter for property <code>patchSize</code>. */
    protected int getPatchSize() {
	return patchSize;
    }
    /** Getter for property <code>getAuthors</code>. */
    protected String getAuthors() {
	return authors;
    }
    /** Setter for property <code>port</code>. */
    // remove when 'port' becomes 'private'.
    /*
    public void setPort(int port) { // 'public' for storable interface
        this.port = port;
	//device.setPort(port);
    }
    */
    /** Getter for property <code>port</code>. */
    public int getPort() {	// called by bank driver
	return device.getPort();
    }
    /** Setter for property <code>inPort</code>. */
    // remove this method when 'inPort' becomes 'private'.
    /*
    public void setInPort(int inPort) { // 'public' for storable interface
        this.inPort = inPort;
	//device.setInPort(inPort);
    }
    */
    /** Getter for property <code>inPort</code>. */
    protected int getInPort() {	// was 'public' for storable interface
	return device.getInPort();
    }
    /** Getter for property <code>device.manufacturerName</code>. */
    protected String getManufacturerName() {
	return device.getManufacturerName();
    }
    /** Getter for property <code>device.modelName</code>. */
    protected String getModelName() {
	return device.getModelName();
    }
    /** Getter for property <code>device.synthName</code>. */
    protected String getSynthName() {
	return device.getSynthName();
    }
    /** Getter for property <code>device.channel</code>. */
    public int getChannel() { // called by bank driver
        return device.getChannel();
    }
    /** Setter for property <code>device.channel</code>. */
    // remove this method when 'channel' becomes 'private'.
    /*
    public void setChannel(int channel) { // called by Device and for storable interface
        this.channel = channel;
    }
    */
    /** Getter for property <code>device.deviceID</code>. */
    protected int getDeviceID() {
	return device.getDeviceID();
    }
//     protected void setSynthName(String s) {
//  	id = s;
//     }

    /**
     * Gets the name of the patch from the sysex. If the patch uses
     * some weird format or encoding, this needs to be overidden in
     * the particular driver.
     */
    public String getPatchName(Patch p) { // called by bank driver
        if (patchNameSize == 0)
	    return ("-");
        try {
 	    return new String(p.sysex, patchNameStart, patchNameSize, "US-ASCII");
	} catch (UnsupportedEncodingException ex) {
	    return "-";
	}
    }

    /**
     * Set the name of the patch in the sysex. If the patch uses some
     * weird format or encoding, this needs to be overidden in the
     * particular driver.
     */
    public void setPatchName(Patch p, String name) { // called by bank driver
        if (patchNameSize == 0) {
	    ErrorMsg.reportError("Error", "The Driver for this patch does not support Patch Name Editing.");
	    return;
	}

	while (name.length() < patchNameSize)
		name = name + " ";

        byte[] namebytes = new byte[patchNameSize];
        try {
	    namebytes = name.getBytes("US-ASCII");
	    for (int i = 0; i < patchNameSize; i++)
		p.sysex[patchNameStart + i] = namebytes[i];
	} catch (UnsupportedEncodingException ex) {
	    return;
	}
        calculateChecksum(p);	// Is this required here?
    }

    /**
     * Caluculate check sum of a <code>Patch</code>.<p>
     *
     * This method is called by extended classes.
     * Override this for different checksum calculation method.
     *
     * @param p a <code>Patch</code> value
     * @param start start offset
     * @param end end offset
     * @param ofs offset of the checksum data
     */
    protected void calculateChecksum(Patch p, int start, int end, int ofs) {
// 	ErrorMsg.reportStatus("Driver:calcChecksum:1st byte is " + p.sysex[start]);
// 	ErrorMsg.reportStatus("Last byte is " + p.sysex[end]);
// 	ErrorMsg.reportStatus("Checksum was " + p.sysex[ofs]);
        int sum = 0;
        for (int i = start; i <= end; i++)
            sum += p.sysex[i];
	// Here is an example of checksum calculation (for Roland, etc.)
	/*
	p.sysex[ofs] = (byte) (sum & 0x7f);
	p.sysex[ofs] = (byte) (p.sysex[ofs] ^ 0x7f);
	p.sysex[ofs] = (byte) (p.sysex[ofs] + 1);
	p.sysex[ofs] = (byte) (p.sysex[ofs] & 0x7f);   //to ensure that checksum is in range 0-127;
	*/
	//p.sysex[ofs] = (byte) ((~sum + 1) & 0x7f);
	p.sysex[ofs] = (byte) (-sum & 0x7f);
//      ErrorMsg.reportStatus("Checksum is now " + p.sysex[ofs]);
    }

    /**
     * Caluculate check sum of a <code>Patch</code>.<p>
     *
     * This method is called by main program.  Need to be overridden
     * if a patch is consist from multiple SysEX messages.
     *
     * @param p a <code>Patch</code> value
     */
    public void calculateChecksum(Patch p) { // called by bank driver
	calculateChecksum(p, checksumStart, checksumEnd, checksumOffset);
    }

    /**
     * Create a new Patch.
     *
     * Don't override this method unless your driver supports this.
     * The caller checks if your driver support this by using
     * getDeclaredMethod.
     */
    protected Patch createNewPatch() {
	return null;
    }

    /**
     * This method trims a patch, containing more than one real
     * patch to a correct size. Useful for files containg more than one
     * bank for example. Some drivers are incompatible with this method
     * so it reqires explicit activation with the trimSize variable.
     * @param p the patch, which should be trimmed to the right size
     * @return the size of the (modified) patch
     */
    protected int trimSysex(Patch p) { // no driver overrides this now.
        if (trimSize > 0 && p.sysex.length > trimSize
	    && p.sysex[trimSize - 1] == (byte) 0xf7) {
	    byte[] sysex = new byte[trimSize];
	    System.arraycopy(p.sysex, 0, sysex, 0, trimSize);
	    p.sysex = sysex;
        }
	return p.sysex.length;	// == trimSize
    }

    /**
     * Returns an Editor Window for this Patch. Overwrite this to
     * invoke your Patch Editor if made it.
     */
    protected JSLFrame editPatch(Patch p) {
	ErrorMsg.reportError("Error", "The Driver for this patch does not support Patch Editing.");
	return null;
    }

    /**
     * Prompts the user for the location to store the patch and stores
     * it.
     *
     * @param p a <code>Patch</code> value
     * @param bankNum initially selected bank number.
     * @param patchNum initially selected patch number.
     */
    // Nobody extends this method now.
    protected void choosePatch(Patch p, int bankNum, int patchNum) {
	int bank = 0;
	int patch = 0;
	String bankstr;
	String patchstr;
	try {
	    // choose bank number
	    if (bankNumbers.length > 1) {
		bankstr = (String) JOptionPane.showInputDialog
		    (null, "Please Choose a Bank", "Storing Patch",
		     JOptionPane.QUESTION_MESSAGE, null,
		     bankNumbers, bankNumbers[bankNum]);
		if (bankstr == null) // canceled
		    return;
		for (int i = 0; i < bankNumbers.length; i++)
		    if (bankstr.equals(bankNumbers[i])) {
			bank = i;
			break;
		    }
	    }
	    // choose patch number
	    if (patchNumbers.length > 1) {
		patchstr = (String) JOptionPane.showInputDialog
		    (null, "Please Choose a Patch Location", "Storing Patch",
		     JOptionPane.QUESTION_MESSAGE, null,
		     patchNumbers, patchNumbers[patchNum]); // phil@muqus.com
		if (patchstr == null) // canceled
		    return;
		for (int i = 0; i < patchNumbers.length; i++)
		    if (patchstr.equals(patchNumbers[i])) {
			patch = i;
			break;
		    }
	    }
	} catch (Exception e) {
	    ErrorMsg.reportStatus(e);
	}
	ErrorMsg.reportStatus("Driver:ChoosePatch  Bank = " + bank
			      + "  Patch = " + patch);
	// send a Patch to selected bank & patch number
	storePatch(p, bank, patch);
    }

    /**
     * Prompts the user for the location to store the patch and stores
     * it.  Initially bank number '0' and patch number '0' are selected.
     *
     * @param p a <code>Patch</code> value
     */
    protected void choosePatch(Patch p) {
	choosePatch(p, 0, 0);
    }

    /**
     * Compares the header & size of a Patch to this driver to see if
     * this driver is the correct one to support the patch.
     *
     * @param patchString the result of <code>p.getPatchHeader()</code>.
     * @param p a <code>Patch</code> value
     * @return <code>true</code> if this driver supports the Patch.
     * @see #patchSize
     * @see #sysexID
     */
    protected boolean supportsPatch(StringBuffer patchString, Patch p) {
	// check the length of Patch
        if ((patchSize != p.sysex.length) && (patchSize != 0))
	    return false;

        if (sysexID == null || patchString.length() < sysexID.length())
	    return false;

        StringBuffer compareString = new StringBuffer();
        for (int i = 0; i < sysexID.length(); i++) {
	    switch (sysexID.charAt(i)) {
	    case '*':
		compareString.append(patchString.charAt(i));
		break;
	    default:
		compareString.append(sysexID.charAt(i));
            }
        }
// 	System.out.println(toString());
// 	System.out.println("Comp.String: " + compareString);
// 	System.out.println("DriverString:" + driverString);
// 	System.out.println("PatchString: " + patchString);
        return (compareString.toString().equalsIgnoreCase
		(patchString.toString().substring(0, sysexID.length())));
    }

    //
    // MIDI methods
    //
    /** Send Program Change MIDI message. */
    protected void setPatchNum(int patchNum) {
        try {
	    ShortMessage msg = new ShortMessage();
	    msg.setMessage(ShortMessage.PROGRAM_CHANGE, getChannel() - 1,
			   patchNum, 0); // Program Number
	    send(msg);
	} catch (Exception e) {
	}
    }

    /** Send Control Change (Bank Select) MIDI message. */
    protected void setBankNum(int bankNum) {
        try {
	    ShortMessage msg = new ShortMessage();
	    msg.setMessage(ShortMessage.CONTROL_CHANGE, getChannel() - 1,
			   0x00, //  Bank Select (MSB)
			   bankNum / 128); // Bank Number (MSB)
	    send(msg);
	    msg.setMessage(ShortMessage.CONTROL_CHANGE, getChannel() - 1,
			   0x20, //  Bank Select (LSB)
			   bankNum % 128); // Bank Number (MSB)
	    send(msg);
	} catch (Exception e) {
	}
    }

    /**
     * Sends a patch to a set location on a synth.<p>
     * Override this if required.
     */
    // sendPatch(Patch) may be better name.
    protected void storePatch(Patch p, int bankNum, int patchNum) {
        setBankNum(bankNum);
        setPatchNum(patchNum);
        sendPatch(p);
    }

    /**
     * Sends a patch to the synth's edit buffer.<p>
     *
     * Override this in the subclass if parameters or warnings need to
     * be sent to the user (aka if the particular synth does not have
     * a edit buffer or it is not MIDI accessable.
     */
    protected void sendPatch(Patch p) {
	sendPatchWorker(p);
    }

    /**
     * Set Device ID and send the sysex data to MIDI output.
     */
    protected final void sendPatchWorker(Patch p) {
        if (deviceIDoffset > 0)
	    p.sysex[deviceIDoffset] = (byte) (getDeviceID() - 1);
        try {
	    send(p.sysex);
	} catch (Exception e) {
	    ErrorMsg.reportStatus(e);
	}
    }

    //----- Start phil@muqus.com
    /**
     * Request MIDI synth to send a patch dump.  If
     * <code>sysexRequestDump</code> is not <code>null</code>, a
     * request dump message is sent.  Otherwise a dialog
     * window will prompt users.
     * @see SysexHandler
     */
    protected void requestPatchDump(int bankNum, int patchNum) {
	clearMidiInBuffer();
	setBankNum(bankNum);
	setPatchNum(patchNum);
	if (sysexRequestDump == null) {
	    JOptionPane.showMessageDialog
		(PatchEdit.getInstance(),
		 "The " + toString()
		 + " driver does not support patch getting.\n\n"
		 + "Please start the patch dump manually...",
		 "Get Patch", JOptionPane.WARNING_MESSAGE);
	} else
	    send(sysexRequestDump.toSysexMessage(getDeviceID(),
						 new NameValue("bankNum", bankNum),
						 new NameValue("patchNum", patchNum)));
    }
    //----- End phil@muqus.com

    /** Play note. */
    // the argument 'p' is not used.!!!FIXIT!!!
    public void playPatch(Patch p) { // called by core and some Editors
        try {
// 	    sendPatch(p);
	    Thread.sleep(100);
	    ShortMessage msg = new ShortMessage();
	    msg.setMessage(ShortMessage.NOTE_ON, getChannel() - 1,
			   PatchEdit.appConfig.getNote(),
			   PatchEdit.appConfig.getVelocity());
	    send(msg);

	    Thread.sleep(PatchEdit.appConfig.getDelay());

	    msg.setMessage(ShortMessage.NOTE_ON, getChannel() - 1,
			   PatchEdit.appConfig.getNote(),
			   0);	// expecting running status
	    send(msg);
	} catch (Exception e) {
	    ErrorMsg.reportStatus(e);
	}
    }

    // MIDI in/out mothods to encapsulate lower MIDI layer
    /** Send MidiMessage to MIDI outport. */
    protected final void send(MidiMessage msg) {
	try {
	    if (PatchEdit.newMidiAPI)
		device.send(msg);
	    else
		PatchEdit.MidiOut.send(getPort(), msg);
	} catch (Exception e) {
	    ErrorMsg.reportStatus(e);
	}
    }

    /** Send Sysex byte array data to MIDI outport. */
    protected final void send(byte[] sysex) {
	try {
	    if (PatchEdit.newMidiAPI) {
		SysexMessage[] a = MidiUtil.byteArrayToSysexMessages(sysex);
		for (int i = 0; i < a.length; i++)
		    device.send(a[i]);
	    } else
		PatchEdit.MidiOut.writeLongMessage(getPort(), sysex);
	} catch (Exception e) {
	    ErrorMsg.reportStatus(e);
	}
    }

    /** Send ShortMessage to MIDI outport. */
    protected final void send(int status, int d1, int d2) {
	ShortMessage msg = new ShortMessage();
	try {
	    msg.setMessage(status, d1, d2);
	} catch (InvalidMidiDataException e) {
	    ErrorMsg.reportStatus(e);
	}
	send(msg);
    }

    /** Send ShortMessage to MIDI outport. */
    protected final void send(int status, int d1) {
	send(status, d1, 0);
    }

    /**
     * clear MIDI input buffer.  Only used by YamahaDX7 now. Is this
     * really required?
     */
    protected final void clearMidiInBuffer() {
	try {
	    if (PatchEdit.newMidiAPI)
		MidiUtil.clearSysexInputQueue(getInPort());
	    else
		PatchEdit.MidiIn.clearMidiInBuffer(getInPort());
	} catch (Exception e) {
	    ErrorMsg.reportStatus(e);
	}
    }

    // For storable interface
    /**
     * Get the names of properties that should be stored and loaded.
     * @return a Set of field names
     */
    /*
    public Set storedProperties() {
	final String[] storedPropertyNames = {
	    "deviceNum", "driverNum", "port", "inPort", "channel"
	};
	HashSet set = new HashSet();
	set.addAll(Arrays.asList(storedPropertyNames));
	return set;
    }
    */
    /** Method that will be called after loading. */
    /*
    public void afterRestore() {
	// do nothing
    }
    */
    // end of storable interface

    //
    // For debugging.
    //
    /*
     * Returns String .. full name for referring to this patch for
     * debugging purposes
     */
    protected String getFullPatchName(Patch p) {
	return getManufacturerName() + " | " + getModelName() + " | "
	    + getPatchType() + " | " + getSynthName() + " | " + getPatchName(p);
    }

    /*
     * Returns String .. full name for referring to this Driver for
     * debugging purposes
     */
    public String toString() {
	return getManufacturerName() + " " + getModelName() + " "
	    + getPatchType();
    }

    /*
     * Returns String[] list of patch numbers for writable patches.
     * This can be overridden if some patch locations are read only.
     * e.g. the Waldorf Pulse has 100 patches, but only 0 to 39 are writable.
     * Currently writable patches are assumed to start at patch location 0
     * (This has nothing to with the "Storable" class in JSynthLib)
     */
    public String[] getPatchNumbersForStore()
    {
        // All patches assumed to be writable by default
        return patchNumbers;
    }

    /*
     * Generates arrays of formatted numbers
     */
    protected static String[] generateNumbers(int min, int max, String format){
        String retval[] = new String[max - min + 1];
        DecimalFormat df = (DecimalFormat)NumberFormat.getInstance().clone();
        df.applyPattern(format);
        while (max >= min)
            retval[max - min] = df.format(max--);
        return retval;
    }
}
