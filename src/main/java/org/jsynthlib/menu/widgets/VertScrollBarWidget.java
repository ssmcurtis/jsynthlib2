package org.jsynthlib.menu.widgets;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jsynthlib.model.patch.Patch;

/**
 * Vertical scrollbar SysexWidget.
 * 
 * @version $Id$
 * @see ScrollBarWidget
 * @see ScrollBarLookupWidget
 */
public class VertScrollBarWidget extends ScrollBarWidget {
	public VertScrollBarWidget(Patch patch, IParameter param) {
		super(patch, param);
	}

	/**
	 * Constructor for setting up the VertScrollBarWidget.
	 * 
	 * @param label
	 *            Label for the Widget
	 * @param patch
	 *            The patch, which is edited
	 * @param min
	 *            Minimum value
	 * @param max
	 *            Maximum value
	 * @param base
	 *            base value. This value is added to the actual value for display purposes
	 * @param pmodel
	 *            a <code>ParamModel</code> instance.
	 * @param sender
	 *            sysexSender for transmitting the value at editing the parameter
	 */
	public VertScrollBarWidget(String label, Patch patch, int min, int max, int base, IParamModel pmodel, ISender sender) {
		super(label, patch, min, max, base, pmodel, sender);
	}

	protected void createWidgets() {
		slider = new JSlider(JSlider.VERTICAL, getValueMin(), getValueMax(), getValue());
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				eventListener(e);
			}
		});
		slider.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				eventListener(e);
			}
		});
		text = new JTextField(new Integer(getValue() + base).toString(), 4);
		text.setEditable(false);
	}

	protected void layoutWidgets() {
		setLayout(new BorderLayout());

		slider.setMinimumSize(new Dimension(25, 50));
		slider.setMaximumSize(new Dimension(25, 100));

		add(getJLabel(), BorderLayout.NORTH);
		add(slider, BorderLayout.CENTER);
		add(text, BorderLayout.SOUTH);
	}
}
