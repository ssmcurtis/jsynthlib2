package org.jsynthlib.menu.patch;


/**
 * Internal frame (other than PatchEditorFrame) have to implement this interface in order to treat MIDI input when frame
 * is displayed. Otherwise it could result in nullptrexception in PatchEdit.
 * 
 * @author denis queffeulou mailto:dqueffeulou@free.fr
 */
public interface PatchContainer {
	IPatch getPatch();
}
