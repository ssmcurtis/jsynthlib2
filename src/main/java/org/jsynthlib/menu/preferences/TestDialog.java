/* $Id$ */

package org.jsynthlib.menu.preferences;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jsynthlib.advanced.style.TextComponentDemo;
import org.jsynthlib.menu.JSLDialog;
import org.jsynthlib.tools.UiUtil;

public class TestDialog extends JSLDialog {

	TextComponentDemo deviceSelectionTree;

	// DevicesConfig devConf = null;

	public TestDialog(JFrame parent, boolean isReadOnly) {
		super(parent, "Synthesizer Device Install", true);
		if (isReadOnly) {
			setTitle("Supported synthesizer");
		}
		
		JPanel container = new JPanel();
		container.setLayout(new BorderLayout());
		
		deviceSelectionTree = new TextComponentDemo();
		// JScrollPane scrollpane = new JScrollPane(deviceSelectionTree);
		container.add(deviceSelectionTree, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

//		// The following code catches double-clicks on leafs and treats them like pressing "OK"
//		MouseListener ml = new MouseAdapter() {
//			public void mousePressed(MouseEvent e) {
//				int selRow = deviceSelectionTree.getRowForLocation(e.getX(), e.getY());
//				TreePath tp = deviceSelectionTree.getPathForRow(selRow);
//				// Did they even click on a tree item
//				if (tp != null) {
//					if (e.getClickCount() == 2) {
//						// User double-clicked. What did they click on?
//						DefaultMutableTreeNode o = (DefaultMutableTreeNode) tp.getLastPathComponent();
//						if (o.isLeaf()) {
//							// User double-clicked on a leaf. Treat it like "OK"
//							okPressed();
//						}
//					}
//				}
//			}
//		};
//		deviceSelectionTree.addMouseListener(ml);
//		if (!isReadOnly) {
//			JButton ok = new JButton("OK");
//			ok.addActionListener(new ActionListener() {
//				public void actionPerformed(ActionEvent e) {
//					okPressed();
//				}
//			});
//			buttonPanel.add(ok);
//		}
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelPressed();
			}
		});
		buttonPanel.add(cancel);

		getRootPane().setDefaultButton(cancel);

		container.add(buttonPanel, BorderLayout.SOUTH);
		getContentPane().add(container);
		setSize(400, 600);

		UiUtil.centerDialog(this);
	}

	void okPressed() {
//		this.setVisible(false);
//		String s = (String) deviceSelectionTree.getSelectedValue();
//		if (s == null)
//			return;
//
//		String cls = PatchBayApplication.devConfig.getClassNameForDeviceName(s);
//		Device device = AppConfig.addDevice(cls);
//		if (device == null)
//			return;
//
//		String info = device.getInfoText();
//		if (info != null && info.length() > 0) {
//			JTextArea jta = new JTextArea(info, 15, 40);
//			jta.setEditable(false);
//			jta.setLineWrap(true);
//			jta.setWrapStyleWord(true);
//			jta.setCaretPosition(0);
//			JScrollPane jasp = new JScrollPane(jta);
//			JOptionPane.showMessageDialog(null, jasp, "Device Information", JOptionPane.INFORMATION_MESSAGE);
//		}
	}

	void cancelPressed() {
		this.setVisible(false);
	}

}
