package org.jsynthlib.synthdrivers.yamaha.fs1r;

import java.awt.FlowLayout;

import javax.swing.Box;
import javax.swing.JPanel;

import org.jsynthlib.menu.widgets.CheckBoxWidget;
import org.jsynthlib.menu.widgets.ComboBoxWidget;
import org.jsynthlib.menu.widgets.KnobWidget;
import org.jsynthlib.menu.widgets.SpinnerWidget;
import org.jsynthlib.model.patch.PatchDataImpl;

/**
 * Formant sequence parameters.
 * 
 * @author denis queffeulou mailto:dqueffeulou@free.fr
 */
class FseqWindow extends JPanel {

	private PatchDataImpl p;

	public PatchDataImpl getPatch() {
		return p;
	}

	private static final String[] mRatios = { "MIDI 1/4", "MIDI 1/2", "MIDI 1/1", "MIDI 2/1", "MIDI 4/1" };

	FseqWindow(PatchDataImpl aPatch) {
		p = aPatch;
		Box oPanel = Box.createVerticalBox();

		JPanel oPanel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		oPanel1.add(new ComboBoxWidget("Part", p, new YamahaFS1RPerformanceDriver.Model(p, 0x15),
				new YamahaFS1RPerformanceDriver.Sender(0x15), new String[] { "Off", "1", "2", "3", "4" }));
		oPanel1.add(new ComboBoxWidget("Bank", p, new YamahaFS1RPerformanceDriver.Model(p, 0x16),
				new YamahaFS1RPerformanceDriver.Sender(0x16), new String[] { "Int", "Pre" }));
		// TODO afficher les noms
		oPanel1.add(new SpinnerWidget("Program", p, 0, 89, 0, new YamahaFS1RPerformanceDriver.Model(p, 0x17),
				new YamahaFS1RPerformanceDriver.Sender(0x17)));
		// TODO 0..4 64..127
		oPanel1.add(new ComboBoxWidget("Speed ratio", p, new YamahaFS1RPerformanceDriver.Model(p, 0x18),
				new YamahaFS1RPerformanceDriver.Sender(0x18), mRatios));
		oPanel1.add(new KnobWidget("10-500%", p, 0x64, 5000, 0, new YamahaFS1RPerformanceDriver.Model(p, 0x18),
				new YamahaFS1RPerformanceDriver.Sender(0x18)));
		oPanel.add(oPanel1);

		JPanel oPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		oPanel2.add(new KnobWidget("Start offset", p, 0, 0x3FF, 0, new YamahaFS1RPerformanceDriver.Model(p, 0x1A),
				new YamahaFS1RPerformanceDriver.Sender(0x1A)));
		oPanel2.add(new KnobWidget("Loop start", p, 0, 0x3FF, 0, new YamahaFS1RPerformanceDriver.Model(p, 0x1C),
				new YamahaFS1RPerformanceDriver.Sender(0x1C)));
		oPanel2.add(new KnobWidget("Loop end", p, 0, 0x3FF, 0, new YamahaFS1RPerformanceDriver.Model(p, 0x1E),
				new YamahaFS1RPerformanceDriver.Sender(0x1E)));
		oPanel2.add(new ComboBoxWidget("Loop mode", p, new YamahaFS1RPerformanceDriver.Model(p, 0x20),
				new YamahaFS1RPerformanceDriver.Sender(0x20), new String[] { "One way", "Round" }));
		oPanel2.add(new ComboBoxWidget("Play", p, new YamahaFS1RPerformanceDriver.Model(p, 0x21),
				new YamahaFS1RPerformanceDriver.Sender(0x21), new String[] { "Scratch", "Fseq" }));
		oPanel.add(oPanel2);

		JPanel oPanel3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		oPanel3.add(new KnobWidget("Tempo vel sens", p, 0, 7, 0, new YamahaFS1RPerformanceDriver.Model(p, 0x22),
				new YamahaFS1RPerformanceDriver.Sender(0x22)));
		oPanel3.add(new KnobWidget("Level vel sens", p, 0, 127, -64, new YamahaFS1RPerformanceDriver.Model(p, 0x27),
				new YamahaFS1RPerformanceDriver.Sender(0x27)));
		oPanel3.add(new CheckBoxWidget("Formant pitch", p, new YamahaFS1RPerformanceDriver.Model(p, 0x23),
				new YamahaFS1RPerformanceDriver.Sender(0x23)));
		oPanel3.add(new ComboBoxWidget("Key on trig", p, new YamahaFS1RPerformanceDriver.Model(p, 0x24),
				new YamahaFS1RPerformanceDriver.Sender(0x24), new String[] { "First", "All" }));
		oPanel3.add(new KnobWidget("Delay", p, 0, 0x63, 0, new YamahaFS1RPerformanceDriver.Model(p, 0x26),
				new YamahaFS1RPerformanceDriver.Sender(0x26)));
		oPanel.add(oPanel3);

		add(oPanel);
	}
}
