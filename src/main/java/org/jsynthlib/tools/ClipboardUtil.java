package org.jsynthlib.tools;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

import org.jsynthlib.menu.patch.IPatch;
import org.jsynthlib.menu.ui.PatchTransferHandler;

public class ClipboardUtil implements ClipboardOwner {
	protected final static ClipboardUtil instance = new ClipboardUtil();

	protected final static Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();

	public static void storePatch(IPatch p) {
		try {
			c.setContents(p, instance);
		} catch (IllegalStateException e) {
			ErrorMsg.reportStatus(e);
		}
	}

	public static IPatch getPatch() { // not used
		try {
			Transferable t = c.getContents(instance);
			return (IPatch) t.getTransferData(PatchTransferHandler.PATCH_FLAVOR);
		} catch (IllegalStateException e) {
			ErrorMsg.reportStatus(e);
		} catch (ClassCastException e) {
			ErrorMsg.reportStatus(e);
		} catch (UnsupportedFlavorException e) {
			ErrorMsg.reportStatus(e);
		} catch (java.io.IOException e) {
			ErrorMsg.reportStatus(e);
		}
		return null;
	}

	// ClipboardOwner method
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
	}

}