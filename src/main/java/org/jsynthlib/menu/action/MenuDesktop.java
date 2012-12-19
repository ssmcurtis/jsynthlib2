package org.jsynthlib.menu.action;

import java.util.Iterator;

import javax.swing.Action;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;

import org.jsynthlib.menu.ui.JSLDesktop;
import org.jsynthlib.menu.ui.JSLFrame;
import org.jsynthlib.menu.ui.JSLFrameEvent;
import org.jsynthlib.menu.ui.JSLWindowMenu;

/** JSLDesktop with Menu support. */
public class MenuDesktop extends JSLDesktop {

	public MenuDesktop(String title, JMenuBar mb, JToolBar tb, Action exitAction) {
		super(title, mb, tb, exitAction);
	}

	public void add(JSLFrame frame) {
		super.add(frame);
		Iterator<JSLWindowMenu> it = Actions.windowMenus.iterator();
		while (it.hasNext()) {
			((JSLWindowMenu) it.next()).add(frame);
		}
	}

	public void JSLFrameClosed(JSLFrameEvent e) {
		super.JSLFrameClosed(e);
		JSLFrame frame = e.getJSLFrame();
		Actions.windowMenus.remove(Actions.frames.get(frame));
		Actions.frames.remove(frame);
	}
}