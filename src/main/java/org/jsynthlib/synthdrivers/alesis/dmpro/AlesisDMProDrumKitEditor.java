/*
 * Copyright 2004 Peter Hageus
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
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
package org.jsynthlib.synthdrivers.alesis.dmpro;

import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import org.jsynthlib.menu.widgets.CheckBoxWidget;
import org.jsynthlib.menu.widgets.ComboBoxWidget;
import org.jsynthlib.menu.widgets.IParamModel;
import org.jsynthlib.menu.widgets.PatchNameWidget;
import org.jsynthlib.menu.widgets.ScrollBarLookupWidget;
import org.jsynthlib.menu.widgets.ScrollBarWidget;
import org.jsynthlib.menu.widgets.SysexSender;
import org.jsynthlib.menu.window.PatchEditorFrame;
import org.jsynthlib.model.patch.PatchDataImpl;

/**
 * @author Peter Hageus (peter.hageus@comhem.se)
 */
class AlesisDMProDrumKitEditor extends PatchEditorFrame {

	public AlesisDMProDrumKitEditor(PatchDataImpl patch) {
		super("Alesis DM Pro Drumkit Editor", patch);

		// All widgets need a ref to parser, it handles access to patchsysex
		AlesisDMProParser oParser = new AlesisDMProParser(patch);
		String[] arType = new String[] { " AKk ", " EKk ", " ASn ", " ESn ", " TOM ", " HAT ", " CYM ", " AP1 ",
				" AP2 ", " EPc ", " SFx ", " CHR ", " USR " };
		String[] arDrum = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13",
				"14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30",
				"31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47",
				"48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64",
				"65", "66", "67", "68", "69", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "80", "81",
				"82", "83", "84", "85", "86", "87", "88", "89", "90", "91", "92", "93", "94", "95", "96", "97", "98",
				"99", "100", "101", "102", "103", "104", "105", "106", "107", "108", "109", "110", "111", "112", "113",
				"114", "115", "116", "117", "118", "119", "120", "121", "122", "123", "124", "125", "126", "127" };

		String[] arOutput = new String[] { "Main L/R", "Aux 1/2", "Aux 3", "Aux 4", "FX Only" };
		String[] arFXBus = new String[] { "Reverb", "OD->DL->PCH" };
		String[] arMuteGroup = new String[] { "OFF", "Group 1", "Group 2", "Group 3", "Group 4" };
		String[] arMIDIChannel = new String[] { "Basic", "ch 1", "ch 2", "ch 3", "ch 4", "ch 5", "ch 6", "ch 7",
				"ch 8", "ch 9", "ch 10", "ch 11", "ch 12", "ch 13", "ch 14", "ch 15", "ch 16" };
		String[] arDrumLink = new String[65];
		String[] arPan = new String[] { "<3", "<2", "<1", "<>", "1>", "2>", "3>", "PRG" };

		// Common Pane
		JPanel cmnPane = new JPanel();
		cmnPane.setLayout(new FlowLayout());
		addWidget(cmnPane, new PatchNameWidget(" Name  ", patch), 0, 0, 18, 1, 0);
		gbc.gridx = 0;
		gbc.gridy = 0;
		scrollPane.add(cmnPane, gbc);

		arDrumLink[0] = "Off";

		gbc.weightx = 0;
		gbc.weighty = 0;

		int nStartByte = 44;

		for (int i = 0; i < 64; i++) {

			// For each drum
			JPanel pnlDrum = new JPanel();
			JLabel lblHeader = new JLabel("Drum " + (i + 33));
			pnlDrum.add(lblHeader, gbc);

			pnlDrum.setLayout(new GridBagLayout());
			pnlDrum.setBorder(new EtchedBorder(EtchedBorder.LOWERED));

			ComboBoxWidget cboDrum = new ComboBoxWidget("Drum", patch, new AlesisDMProModel(oParser, nStartByte + 0, 0,
					7), new AlesisDMProSender(i, 0, 1), arDrum);

			// The drums list depends on the category combo...
			BuddyCombo cboCat = new BuddyCombo("Category", patch, new AlesisDMProModel(oParser, nStartByte + 0, 7, 5),
					new AlesisDMProSender(i, 0, 0), arType, cboDrum.cb);

			addWidget(pnlDrum, cboCat, 1, 0, 2, 1, i);
			addWidget(pnlDrum, cboDrum, 1, 1, 2, 1, 0);

			addWidget(pnlDrum, new ComboBoxWidget("Output", patch, new AlesisDMProModel(oParser, nStartByte + 2, 7, 3),
					new AlesisDMProSender(i, 2, 2), arOutput), 1, 2, 2, 1, 0);

			addWidget(pnlDrum, new ComboBoxWidget("Effect bus", patch, new AlesisDMProModel(oParser, nStartByte + 4, 1,
					1), new AlesisDMProSender(i, 2, 4), arFXBus), 3, 0, 2, 1, 0);

			addWidget(pnlDrum, new ComboBoxWidget("Mute group", patch, new AlesisDMProModel(oParser, nStartByte + 5, 2,
					3), new AlesisDMProSender(i, 2, 5), arMuteGroup), 3, 1, 2, 1, 0);

			addWidget(pnlDrum, new ScrollBarLookupWidget("Pan", patch, 0, 7, new AlesisDMProModel(oParser,
					nStartByte + 2, 4, 3), new AlesisDMProSender(i, 2, 1), arPan), 3, 2, 2, 1, 0);

			addWidget(pnlDrum, new ScrollBarWidget("Effect level", patch, 0, 99, 0, new AlesisDMProModel(oParser,
					nStartByte + 3, 2, 7), new AlesisDMProSender(i, 2, 3)), 5, 0, 3, 1, 0);

			addWidget(pnlDrum, new ScrollBarWidget("Volume", patch, 0, 99, 0, new AlesisDMProModel(oParser,
					nStartByte + 1, 5, 7), new AlesisDMProSender(i, 2, 0)), 5, 1, 3, 1, 0);

			addWidget(pnlDrum, new ScrollBarWidget("Pitch", patch, -96, 96, 0, new AlesisDMProModel(oParser,
					nStartByte + 4, 2, 8), new AlesisDMProSender(i, 1, 0)), 5, 2, 3, 1, 0);

			addWidget(pnlDrum, new CheckBoxWidget("MIDI In", patch,
					new AlesisDMProModel(oParser, nStartByte + 5, 5, 1), new AlesisDMProSender(i, 3, 14)), 8, 0, 1, 1,
					0);

			addWidget(pnlDrum, new CheckBoxWidget("MIDI Out", patch,
					new AlesisDMProModel(oParser, nStartByte + 5, 6, 1), new AlesisDMProSender(i, 3, 15)), 8, 1, 1, 1,
					0);

			addWidget(pnlDrum, new CheckBoxWidget("Enable", patch, new AlesisDMProModel(oParser, nStartByte + 1, 4, 1),
					new AlesisDMProSender(i, 2, 7)), 8, 2, 1, 1, 0);

			addWidget(pnlDrum, new ComboBoxWidget("MIDI Channel", patch, new AlesisDMProModel(oParser, nStartByte + 5,
					7, 5), new AlesisDMProSender(i, 3, 13), arMIDIChannel), 9, 0, 2, 1, 0);

			addWidget(pnlDrum, new ComboBoxWidget("Drum link", patch, new AlesisDMProModel(oParser, nStartByte + 6, 4,
					7), new AlesisDMProSender(i, 2, 6), arDrumLink), 9, 1, 2, 1, 0);

			gbc.gridx = 0;
			gbc.gridy = i + 1;

			scrollPane.add(pnlDrum, gbc);
			nStartByte += 8;
		}

		pack();
	}

	// Inner class
	class BuddyCombo extends ComboBoxWidget {

		JComboBox m_cboOther = null;

		public BuddyCombo(String l, PatchDataImpl p, IParamModel ofs, SysexSender s, String[] o,
				JComboBox cboOther) {
			super(l, p, ofs, s, o);
			m_cboOther = cboOther;

			cb.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						JComboBox cbo = (JComboBox) e.getSource();
						updateDrumNames(cbo);

					}
				}
			});

		}

		public void setValue(int v) {
			super.setValue(v);
			updateDrumNames(this.cb);

		}

		private void updateDrumNames(JComboBox cbo) {

			int i = cbo.getSelectedIndex();
			int nCurrent = m_cboOther.getSelectedIndex();

			m_cboOther.removeAllItems();

			for (int n = 0; n < 128; n++)
				m_cboOther.addItem(DrumName.DrumName[i][n]);

			m_cboOther.setSelectedIndex(nCurrent);
		}

	}
}

class AlesisDMProSender extends SysexSender {

	private int m_nChannel; // Drum
	private int m_nFunc; // Function
	private int m_nPage; // Pagenumber

	public AlesisDMProSender(int nChannel, int nFunc, int nPage) {
		m_nChannel = nChannel;
		m_nFunc = nFunc;
		m_nPage = nPage;
	}

	public byte[] generate(int value) {
		byte[] sysex = new byte[11];

		sysex[0] = (byte) 0xF0;
		sysex[1] = (byte) 0x00;
		sysex[2] = (byte) 0x00;
		sysex[3] = (byte) 0x0E;
		sysex[4] = (byte) 0x19;
		sysex[5] = (byte) 0x10;
		sysex[6] = (byte) (16 | (m_nFunc & 15)); // 1 << 4 = DrumKitedit, low nibble = func
		sysex[7] = (byte) (((m_nFunc & 16) << 6) | (m_nPage & 15)); // MSB of func at bit 6, low nibble = page
		sysex[8] = (byte) (((m_nChannel << 1) & 127) | ((value >>> 7) & 1)); // Channel/drum, MSB of value
		sysex[9] = (byte) (value & 127); // Reamining bits of value
		sysex[10] = (byte) 0xF7;

		return sysex;
	}

}

class AlesisDMProModel implements IParamModel {
	private AlesisDMProParser m_oParser;
	private int m_nByte;
	private int m_nBit;
	private int m_nBits;

	public AlesisDMProModel(AlesisDMProParser oParser, int nByte, int nBit, int nBits) {
		m_oParser = oParser;
		m_nByte = nByte;
		m_nBit = nBit;
		m_nBits = nBits;
	}

	public void set(int i) {
		m_oParser.setValue(m_nByte, m_nBit, m_nBits, i);
	}

	public int get() {
		return m_oParser.getValue(m_nByte, m_nBit, m_nBits);
	}
}

/**
 * Just to hold a static array of the drumnames, don't wanna mess up the other code with it...
 */
class DrumName {
	public static final String[][] DrumName = new String[][] {
			// ACOUSTIC KICK
			// (AKk)
			{ "0 Cartman", "1 TwoHeads", "2 Klonk", "3 HardRock", "4 Wiggum", "5 PwrShoes", "6 Fat Boy", "7 Woodgivr",
					"8 Grounder", "9 Its Safe", "10 Mastered", "11 LowMetal", "12 LowFocus", "13 Bulbous",
					"14 BulboAmb", "15 Strukto", "16 R&R USA", "17 Are Ate!", "18 DarkRock", "19 Mid Bump",
					"20 HourWait", "21 MetlGate", "22 Doobie", "23 QS8 Kick", "24 AmbiShoe", "25 Bem", "26 CompRoom",
					"27 Subbo", "28 Pointy B", "29 BD Tippy", "30 Pum", "31 Sharpz", "32 SharpRok", "33 Igneous",
					"34 Granite", "35 Boks", "36 Tektonic", "37 Seismic", "38 Shale", "39 Blum", "40 BottmOut",
					"41 Razor", "42 Plumb", "43 Dancer", "44 Hip KiK", "45 DryPower", "46 Austrlia", "47 Skipper",
					"48 Big Soft", "49 WayTonal", "50 Dubly", "51 Straight", "52 Kick It!", "53 Robosan",
					"54 WoodAmbi", "55 Ginger", "56 Bathtub", "57 Speed-oh", "58 CyberMtl", "59 Whump", "60 Argh",
					"61 Klump", "62 Billie", "63 Metal EQ", "64 Earth", "65 TiteRoxy", "66 NiceNash", "67 Country",
					"68 Beanie", "69 Honest", "70 ThinSkin", "71 ThikSkin", "72 KatesIIx", "73 Sft&Boxi", "74 Classic",
					"75 Vintage", "76 BasHater", "77 DeepTone", "78 Beater", "79 Bimp", "80 Gray HR", "81 Black HR",
					"82 Trendy", "83 Energiz!", "84 Hollow", "85 E Bunny", "86 Agent 86", "87 Cygnia", "88 Bootery",
					"89 Big Gen", "90 Element", "91 Punter", "92 Bonhamy", "93 MrWhippl", "94 LiteJazz", "95 Soft&Low",
					"96 DryBllad", "97 Crusty", "98 Mid Foot", "99 Agent 99", "100 FatPllow", "101 WhupKick",
					"102 Thoomer", "103 LottaDub", "104 DeepSnap", "105 Woody", "106 Underdog", "107 Nelson",
					"108 Ovion", "109 Flop War", "110 Zippo", "111 WeirdHal", "112 NextRoom", "113 Pepto B",
					"114 Tonto", "115 D'oh", "116 VeloDub", "117 Brinflep", "118 TomSlick", "119 War Dub",
					"120 RockAmbi", "121 Kowalski", "122 Ethel", "123 RedShirt", "124 Simon B", "125 Sinister",
					"126 Dome", "127 Mallet" },
			// ELECTRIC KICK
			// (EKk)
			{ "0 08 Bump1", "1 08 Bump2", "2 08Whump1", "3 08Whump2", "4 08 Bump3", "5 08Whump3", "6 08 Bump4",
					"7 08Whump4", "8 08 Boom", "9 MW Boom1", "10 MW Boom2", "11 MW Boom3", "12 Tite/Drk", "13 Montel",
					"14 Rosie", "15 Jerry", "16 GutWrnch", "17 E Wood 1", "18 PhatButt", "19 E Wood 2", "20 Phat NV",
					"21 Meats", "22 Procreat", "23 Hi Aggro", "24 Kicker", "25 Loopy", "26 09 Kick1", "27 09 Kick2",
					"28 09 Kick3", "29 Thumper", "30 WarmBoot", "31 EuroKik", "32 Throomy", "33 KlubHaus",
					"34 Remix 09", "35 Pierced", "36 Manatee", "37 StrGarge", "38 ToonRude", "39 09RaveNV",
					"40 LoRaved", "41 RaveDown", "42 FzyLogic", "43 DistMach", "44 DistyPch", "45 Kill4U",
					"46 ClubFoot", "47 Kong", "48 Crusty", "49 Scalp", "50 PhatFuzz", "51 Squishy", "52 Face",
					"53 Captain", "54 Crunch", "55 Hip Hop", "56 Crackle", "57 SpritzNV", "58 HatTrkNV", "59 LoSweep",
					"60 Plastic", "61 Autobot", "62 HolowK", "63 Dark Kik", "64 LoBitBot", "65 LoPukFi", "66 Lo Fi NV",
					"67 Dark Hop", "68 Bang On", "69 Tank", "70 AirMovr", "71 Galosh K", "72 MrWilson", "73 Fat Code",
					"74 GeeX NV", "75 HipThoom", "76 PileDrvr", "77 Euro NV", "78 Popper", "79 8BitKick",
					"80 Warrior1", "81 Warrior2", "82 Simple", "83 DeepBlue", "84 Mini NV", "85 MiniDoor", "86 Rander",
					"87 ProdZpNV", "88 FatFiltr", "89 Big Byoo", "90 dZum", "91 Lowdown", "92 Thumpty", "93 Bottled",
					"94 Ghostkik", "95 Grouph", "96 TurboTek", "97 Boomer", "98 JunglKik", "99 JunglSwp", "100 FatMan",
					"101 Mobie", "102 E Face", "103 ChstPnch", "104 Roomie", "105 Tubs", "106 BD Trash", "107 Steel",
					"108 SpringKk", "109 Ripple", "110 Silo", "111 Chunk", "112 08 Rev", "113 Brutal", "114 DarkVKik",
					"115 Bond", "116 KickrVrb", "117 SciFi NV", "118 SadHappy", "119 MultKick", "120 WeidKick",
					"121 SctrKick", "122 Computer", "123 Eatie", "124 SantaKik", "125 Tubey", "126 Howp", "127 *SONAR*" },
			// ACOUSTIC SNARE
			// (ASn)
			{ "0 Backbeat", "1 WideOpen", "2 Tapehead", "3 Postmdrn", "4 Ringer", "5 Near&Far", "6 PaperSnr",
					"7 BrtPlate", "8 SoftGoth", "9 HardGoth", "10 Ranchero", "11 FlowrPwr", "12 NYPD Hit",
					"13 Gas Face", "14 U Masher", "15 RockHall", "16 PwrBalld", "17 Big Gate", "18 CompRoom",
					"19 Tightly", "20 Compresd", "21 CMS Pic", "22 Seltzer", "23 ModernRm", "24 Str Ambi",
					"25 RumpusRm", "26 Steamer", "27 MajHealy", "28 NeckRing", "29 ChorVerb", "30 Rocker", "31 Stuffy",
					"32 Radiqual", "33 Fireman", "34 SmakRoom", "35 StereoRm", "36 Studio54", "37 InDRoom",
					"38 BatChild", "39 EarlyRef", "40 Got Wood", "41 TiteWood", "42 Junkyard", "43 Cogswell",
					"44 Tralfaz", "45 High&Dry", "46 Pip", "47 PiccPete", "48 Bearings", "49 Disco", "50 DuctTape",
					"51 BrassRng", "52 Brass!", "53 Helmet", "54 ShakeYer", "55 Clyde", "56 Taoh", "57 Thubb",
					"58 Stuffed", "59 DynaSoul", "60 Prisoner", "61 Erector", "62 70'sFunk", "63 FishKiss",
					"64 RoboSnar", "65 PreampOD", "66 Klinger", "67 Hawkeye", "68 Skeeter", "69 Beethove",
					"70 Madame W", "71 Alleycat", "72 TinyRngr", "73 Kleine", "74 Compact", "75 Impact", "76 Restrain",
					"77 QuikComp", "78 VeloSnar", "79 Ringer!", "80 Chromium", "81 RoomRokr", "82 Piccolo",
					"83 Mountoon", "84 Control", "85 Chief", "86 MeanPicc", "87 Taos Snr", "88 Smacked", "89 Blasto",
					"90 Ugly Bob", "91 KilKenny", "92 ComboSn", "93 JudyJets", "94 Smacker", "95 Whackit",
					"96 Hi Punch", "97 Rm Punch", "98 RingPicc", "99 Sandpapr", "100 Snahpple", "101 Trashman",
					"102 Thwak SD", "103 Mettle", "104 BrasWhap", "105 Bargain!", "106 SnareOff", "107 RingSnar",
					"108 WideFlam", "109 Rick-O", "110 Natural", "111 Roll", "112 Roughs", "113 Orchestr",
					"114 Flutter", "115 Classicl", "116 BrushIt", "117 Brushed1", "118 BrushOff", "119 HrdBrush",
					"120 SftBrush", "121 Brushwak", "122 BriteStk", "123 SideStik", "124 RoomStik", "125 Dry Stik",
					"126 XstikAmb", "127 OvrhdStk" },
			// ELECTRIC SNARE
			// (ESn)
			{ "0 08 Snr 1", "1 08 Velo", "2 08 Snr 2", "3 08 Snr 3", "4 aR77 NV", "5 St. Popp", "6 EuroSnar",
					"7 55bee NV", "8 08 Snr 4", "9 W Snr NV", "10 09 Xpres", "11 09 Real", "12 09 Rave", "13 HexSnre",
					"14 Glick", "15 AnaSnare", "16 Nib", "17 Tite", "18 Looper", "19 IIx NV", "20 Smedley",
					"21 12BitSnr", "22 LoFiSnar", "23 Soul NV", "24 8BitSnr", "25 Prance", "26 SolidCav",
					"27 Solid NV", "28 DarkVSnr", "29 DrkVGate", "30 Flak", "31 Gate Me", "32 08 Verba", "33 Blip Box",
					"34 2 Litre", "35 Whipper", "36 YoQuiero", "37 Gorditas", "38 Imp", "39 BallPeen", "40 Barbrady",
					"41 Spleen", "42 FortyTwo", "43 Birf", "44 Snuh!", "45 Pipe NV", "46 HipKid", "47 StrHybrd",
					"48 Spank", "49 Looping", "50 BargWhap", "51 Sping", "52 Spang", "53 Spong", "54 Wolka",
					"55 KingKang", "56 Shred NV", "57 HeadBang", "58 Bash", "59 Klingon", "60 Ectoplsm", "61 MachSnr",
					"62 Trasher", "63 Mr. Bok", "64 Draop", "65 ComboSnr", "66 Sooweep", "67 Slam", "68 noK noK",
					"69 SynthSnr", "70 SonrWeez", "71 StrRapSD", "72 Looper", "73 Poppy SD", "74 Chick", "75 ClappySD",
					"76 Falcon", "77 Skrach U", "78 SkrachMe", "79 NoizSnar", "80 Smah!", "81 SemiBits", "82 HRB 1",
					"83 HRB 2", "84 Cap Gun", "85 DownEuro", "86 Dope", "87 Holo", "88 SnakeEye", "89 KongHead",
					"90 PoitNarf", "91 GxCombo", "92 ShredClp", "93 Florence", "94 Snapitup", "95 Squeeker",
					"96 Zeep Snr", "97 Screamer", "98 Junglize", "99 Girlfrnd", "100 Airhead", "101 GtSnare1",
					"102 GtSnare2", "103 GtSnare3", "104 GtSnare4", "105 CompdVrb", "106 CastProc", "107 Bope",
					"108 Circuit", "109 Capacitr", "110 SnapSnre", "111 Stop Dat", "112 Bammmm!", "113 Trace",
					"114 WhipZipp", "115 Noisarr", "116 ZapDoor", "117 Skreezap", "118 Tungsten", "119 Platinum",
					"120 CreepSnr", "121 SpipSnar", "122 Resistor", "123 Robot", "124 Chyoom", "125 Tyoom",
					"126 Werk 1", "127 Werk 2" },
			// TOMS
			// (TOM)
			{ "0 StudioHi", "1 StudioMd", "2 StudioLo", "3 StudioFl", "4 Sessn Hi", "5 SessnMid", "6 SessnLow",
					"7 SessnFlr", "8 PopTom H", "9 PopTom M", "10 PopTom L", "11 PopTom F", "12 FloppyHi",
					"13 FloppyMd", "14 FloppyLo", "15 FloppyFl", "16 Jazz Hi", "17 Jazz Mid", "18 Jazz Low",
					"19 Jazz Flr", "20 Brush Hi", "21 BrushMid", "22 BrushLow", "23 BrushFlr", "24 Bobo Hi",
					"25 Bobo Mid", "26 Bobo Low", "27 Bobo Flr", "28 KlasseHi", "29 KlasseMd", "30 KlasseLo",
					"31 KlasseFl", "32 SavvyHi", "33 SavvyMd", "34 SavvyLo", "35 SavvyFl", "36 St Tom H",
					"37 St Tom M", "38 St Tom L", "39 Koa Hi", "40 Koa Mid", "41 Koa Low", "42 Koa Flr", "43 Rocko Hi",
					"44 RockoMid", "45 RockoLow", "46 RockoFlr", "47 Live Hi", "48 Live Mid", "49 Live Low",
					"50 Live Flr", "51 Tunnel H", "52 Tunnel M", "53 Tunnel L", "54 GasCanHi", "55 GasCanMd",
					"56 GasCanLo", "57 Doom Hi", "58 Doom Mid", "59 Doom Low", "60 Doom Flr", "61 ThundrHi",
					"62 ThundrMd", "63 ThundrLo", "64 ThundrFl", "65 NYPD Hi", "66 NYPD Mid", "67 NYPD Low",
					"68 NYPD Flr", "69 GymineeH", "70 GymineeL", "71 Slam Hi", "72 Slam Low", "73 AllWetHi",
					"74 AllWetMd", "75 AllWetLo", "76 AllWetFl", "77 HexTom H", "78 HexTom M", "79 HexTom L",
					"80 RealHex1", "81 RealHex2", "82 Jam Hi", "83 Jam Mid", "84 Jam Low", "85 Jam Flr", "86 Raket Hi",
					"87 Raket Md", "88 Raket Lo", "89 Raket Fl", "90 Rapzo Hi", "91 Rapzo Md", "92 Rapzo Lo",
					"93 Rapzo Fl", "94 Blaster1", "95 Blaster2", "96 D Pipe 1", "97 D Pipe 2", "98 Pipe Up",
					"99 PipeDown", "100 OctaBnHi", "101 OctaBnLo", "102 Roto Hi", "103 Roto Lo", "104 WildRoto",
					"105 RoomVelo", "106 Cannon", "107 LoLectro", "108 Spacely", "109 Tom Cat", "110 Overlode",
					"111 Bowtom", "112 VelFlam1", "113 VelFlam2", "114 oh8 Tom", "115 1NoteRap", "116 E Pop",
					"117 Elroy", "118 RoloTom", "119 OctaRave", "120 RaveTomz", "121 SynSonic", "122 Jones",
					"123 Splot", "124 Espey 1", "125 Espey 2", "126 Kranner", "127 Rezotom", },
			// HI-HATS
			// (HAT)
			{ "0 ProTite1", "1 ProTite2", "2 ProClosd", "3 Pro Half", "4 Pro Open", "5 FootDnPr", "6 FootUpPr",
					"7 BrtTite1", "8 BrtTite2", "9 BrtTite3", "10 BrtClosd", "11 BrtLoose", "12 Brt Half",
					"13 Brt Skew", "14 Brt Open", "15 FootDnBr", "16 FootUpBr", "17 HRClosd1", "18 HRClosd2",
					"19 HR Loose", "20 HR Half", "21 HR Open1", "22 HR Open2", "23 FootDnHR", "24 FootUpHR",
					"25 SRClosed", "26 SR Half", "27 SR Open", "28 FootDnSR", "29 FootUpSR", "30 TrashyC1",
					"31 TrashyC2", "32 TrashyO1", "33 TrashyO2", "34 TrashyFt", "35 FootUpTr", "36 PopClsd1",
					"37 PopClsd2", "38 Pop Half", "39 Pop Open", "40 Pop Foot", "41 FootUpPp", "42 RockTite",
					"43 RokClsd1", "44 RokClsd2", "45 RokLoose", "46 RockOpen", "47 FootDnRk", "48 FootUpRK",
					"49 Mr.HatCl", "50 Mr.HatLs", "51 Mr.HatOp", "52 FootUpMr", "53 CrustyCl", "54 Sloopy",
					"55 Lp Hat 1", "56 Lp Hat 2", "57 15", "58 FlangeCl", "59 FlangeOp", "60 StBrytCl", "61 StBrytOp",
					"62 08HatCls", "63 08HatHlf", "64 08HatOp1", "65 08HatOp2", "66 08HatOp3", "67 08Hat2Cl",
					"68 08Op2Vel", "69 08Hat2Op", "70 Wyd08Cls", "71 Wyd08Opn", "72 LpHatCls", "73 LpHatHlf",
					"74 LpHatOpn", "75 GeeExCls", "76 GeeExOp1", "77 GeeExOp2", "78 09Closd", "79 09 Open1",
					"80 09 Open2", "81 09 OpnGt", "82 09 Dk Cl", "83 09 Dk Op", "84 EuroClsd", "85 EuroOpn1",
					"86 EuroOpn2", "87 StFiltCl", "88 StFiltOp", "89 MRJ Cls", "90 MRJ Op 1", "91 MRJ Op 2",
					"92 EssArCls", "93 U HatCls", "94 U HatOp1", "95 U HatOp2", "96 W HatCl1", "97 W HatCl2",
					"98 W Hat Op", "99 White Cl", "100 White Op", "101 Filt Cl", "102 Filt Op", "103 Wire Cl",
					"104 Wire Op", "105 Volt Cl", "106 Volt Op", "107 Night Cl", "108 Night Op", "109 DCrsh Cl",
					"110 DCrsh Op", "111 KCrsh Cl", "112 KCrsh Op", "113 Lite Cl", "114 Lite Op", "115 DKHt Cl",
					"116 DKHt Op", "117 HDHt Cl", "118 HDHt Op", "119 MadHattr", "120 DynaHat", "121 ToucHat1",
					"122 Top Hat1", "123 ToucHat2", "124 Top Hat2", "125 RandClsd", "126 RandOpen", "127 FootUpRn" },
			// CYMBALS
			// (CYM)
			{ "0 LongRock", "1 LongThin", "2 Big Rock", "3 SterRock", "4 DarkTurk", "5 SterTurk", "6 CrshRoom",
					"7 SterCrRm", "8 GaragCym", "9 SterGrge", "10 BrytShrt", "11 BrytLong", "12 Str Bryt",
					"13 PanCrsh1", "14 PanCrsh2", "15 Vintage", "16 Weenie", "17 Big&Shrt", "18 DrkCrash",
					"19 MedCrash", "20 VeloCrsh", "21 Exotica", "22 DrkFlanj", "23 SoftRoll", "24 4CymRolz",
					"25 CrshSwp", "26 CymChoke", "27 Str Cym", "28 StCrChok", "29 CrshStak", "30 Choker",
					"31 ChinaChe", "32 Grabbed", "33 TinyChok", "34 Splash 1", "35 Splash 2", "36 PanSplsh",
					"37 OilSplsh", "38 TrshCymb", "39 ChinaHrd", "40 StChiHrd", "41 ChinaSft", "42 StChiSht",
					"43 LowChina", "44 SoftRivs", "45 Rivitz", "46 LongRvtz", "47 St Rivet", "48 Riveting",
					"49 TinyRivt", "50 CrshRide", "51 SterCrRd", "52 TingCrsh", "53 RealRide", "54 RideCym1",
					"55 StrRide1", "56 Pan Ride", "57 Big Ride", "58 EasyRidr", "59 SoftCrak", "60 Fuzzo",
					"61 RideCym2", "62 Vel Ride", "63 StrRide2", "64 GlasRide", "65 RideBel1", "66 JazzRide",
					"67 StrBell1", "68 RideBel2", "69 FlangRid", "70 Vel Ping", "71 StrBell2", "72 UglyRide",
					"73 RideAliv", "74 Fat Ride", "75 Cain", "76 RngyRide", "77 MutantRd", "78 08Cymbal",
					"79 08Cym1NV", "80 08StCym1", "81 08FlngCm", "82 08StCym2", "83 08StCym3", "84 08StCym4",
					"85 08StCym5", "86 08StCym6", "87 Nice 08", "88 StNice08", "89 FakeRide", "90 Ice Ride",
					"91 ElectCym", "92 AllMetal", "93 CyberCym", "94 ShrtECym", "95 CrikRide", "96 CrikBell",
					"97 ShishRyd", "98 FiltBell", "99 SyFyBell", "100 SyFyRide", "101 InsectRd", "102 TinyRide",
					"103 8BitCrsh", "104 Sci-Fi", "105 Jungl Cr", "106 Goth", "107 SuzyCrsh", "108 Machine",
					"109 Ping Me", "110 12BitRev", "111 St12bRev", "112 Rev Cym", "113 Fat Orch", "114 SterOrch",
					"115 SmallOrc", "116 1812", "117 Gong", "118 StrGong1", "119 StrGong2", "120 KongGong",
					"121 ChowMein", "122 Gonger", "123 Ancient", "124 TinyGong", "125 PaleoRde", "126 Deep",
					"127 Warp 9" },
			// ACOUSTIC PERC. 1
			// (AP1)
			{ "0 BoLanGoo", "1 BataSmOp", "2 TuchBata", "3 Bongo 1", "4 Bongo 2", "5 Bongo 3", "6 Bongo 4",
					"7 Conga1Hi", "8 Conga1Lo", "9 Conga1Mt", "10 Conga1Sp", "11 Conga2Hi", "12 Conga2Lo",
					"13 Conga3Op", "14 Conga3Mt", "15 Conga3Sp", "16 Cng3Flam", "17 VelConga", "18 FuzzBhat",
					"19 BataBee", "20 Agrigo", "21 Tite", "22 Papz", "23 Rinz", "24 Critch", "25 Crictaos", "26 Caixa",
					"27 EthnoDrm", "28 Tribalin", "29 Ethno", "30 Triple", "31 Gbargian", "32 Tuboan", "33 HandDrum",
					"34 TakiDrum", "35 Ritual", "36 BataBoys", "37 E Skin", "38 Hybrid", "39 Gamma", "40 Skin",
					"41 Amazonia", "42 Ba Chomp", "43 Plastrum", "44 Skinner", "45 Pottery", "46 DublHead",
					"47 Dribble", "48 Breketa", "49 Taod", "50 TooPahh", "51 Indo", "52 Xena Rap", "53 H20 Botl",
					"54 Mugwump", "55 Cree", "56 SkinTite", "57 Tribe", "58 Thundrm", "59 DeepSkin", "60 Vel Skin",
					"61 Big Hit", "62 War Drum", "63 Hoodoo", "64 H20Stero", "65 Medi Man", "66 TaosDrum", "67 Desert",
					"68 Ghatams", "69 Ghatama", "70 Ghatam1", "71 Ghatam2", "72 Ghatam3", "73 DjembeHi", "74 Djimbo",
					"75 DjembeLo", "76 FatDjemb", "77 SubDjemb", "78 Djemverb", "79 Taiko", "80 TaikoVel",
					"81 TaikoRim", "82 AmUdu 1", "83 AmUdu 2", "84 Udu 1", "85 Udu 2", "86 Big Udu", "87 UduSLap",
					"88 Udu 7th", "89 Clay", "90 Cuica Hi", "91 CuicaLow", "92 Squirter", "93 BoyUp", "94 Gotham",
					"95 TalkDwn", "96 TalkUp", "97 Talker", "98 Perkish", "99 Mandala", "100 Tabla Na", "101 IndiaSlp",
					"102 TablaTin", "103 Tabla Te", "104 Tabla Ka", "105 HeadSlap", "106 Pah", "107 Iroquoi",
					"108 WeeWhack", "109 BrTambo", "110 Loqua", "111 BrazTamb", "112 Kingston", "113 Weedy",
					"114 TimbaleH", "115 TimbaleL", "116 Afrocord", "117 Ballast", "118 Big Oil", "119 PVCDrum",
					"120 AltTympn", "121 Rubble", "122 Tumbler", "123 ChokBlok", "124 Chordblx", "125 Chooka",
					"126 BoLan", "127 DrumTube", },
			// ACOUSTIC PERC. 2
			// (AP2)
			{ "0 SleighBl", "1 SlayChrs", "2 Tambo", "3 StTamb", "4 LiveTamb", "5 Tam Fine", "6 JingleFX",
					"7 BigJingl", "8 Hurting", "9 CastntRl", "10 Castanet", "11 Castas", "12 CastCrik", "13 Clave",
					"14 Ballade", "15 Sticket", "16 WoodBlok", "17 TmplBlok", "18 CricBloc", "19 Itchy", "20 ItchAgin",
					"21 Caxixi", "22 Cabasa 1", "23 Chicken", "24 Maracas", "25 Cabasa 2", "26 VeloShaq",
					"27 ShakerLo", "28 Scratch", "29 Spinner", "30 Shakeret", "31 CabaTwst", "32 Shaker",
					"33 RatShakr", "34 Rattle", "35 RapShakr", "36 3Shakers", "37 BeadShk1", "38 Shaquer",
					"39 Kockrico", "40 VelShake", "41 BeadShk2", "42 DryLeavs", "43 Ridley", "44 Krokus", "45 Guiro L",
					"46 Guiro S", "47 Croaker", "48 Buzzblok", "49 Clicker", "50 Clacking", "51 Insects", "52 Cheapy",
					"53 Quacker", "54 VbraLong", "55 VibraMte", "56 CaxiCr", "57 Crnchett", "58 Spittle",
					"59 ColdSnap", "60 PowrSnap", "61 DrumStix", "62 Jets", "63 Snaps", "64 Toy", "65 Stikz",
					"66 FngrSnap", "67 WoodChuk", "68 RubrBlok", "69 Tapping", "70 Heco Hit", "71 Junk", "72 Oil Can",
					"73 Hubcap", "74 Tuprware", "75 Aluminum", "76 Trivet", "77 Potlid", "78 BrassBit", "79 Rez Bell",
					"80 Panic", "81 PiePlate", "82 Wicket", "83 Ting", "84 Junkman", "85 Tub", "86 Junktom",
					"87 Junkdrum", "88 Scramb", "89 Boonda", "90 Ceremony", "91 AlarmDrm", "92 Snapper", "93 Plates",
					"94 VeloTink", "95 Banger", "96 Agogo 1", "97 Agogo 2", "98 VeloPing", "99 FlxBrake",
					"100 ClikTest", "101 DullMetl", "102 Triangl1", "103 Triangl2", "104 TrnglMte", "105 Cowbell",
					"106 AgoCow", "107 CowMess", "108 Flxatone", "109 FCymCls", "110 FCymMed", "111 FCymOpn",
					"112 Ringer", "113 RndChime", "114 Silvrwr", "115 St MarkT", "116 MarkTree", "117 BellTree",
					"118 Ble Bell", "119 Chime Lp", "120 WndChime", "121 Chime In", "122 Bend, OR", "123 BelChain",
					"124 Etherix", "125 SmbaLong", "126 SmbaShrt", "127 AcMetrnm" },
			// ELECTRONIC PERC.
			// (EPc)
			{ "0 WydClap1", "1 WydClap2", "2 09 Clap", "3 08 Clap", "4 CntField", "5 Bad Clap", "6 Clapp",
					"7 TheClaps", "8 Cricket", "9 Coins", "10 ZapCow", "11 V RezZip", "12 FiltrZip", "13 Zapper",
					"14 SharpZap", "15 Owlette", "16 Florian", "17 Dubl Zip", "18 Rez Attk", "19 V RezAtk",
					"20 Zap Tone", "21 Bump Zap", "22 KZap", "23 MWVoltaj", "24 Current", "25 Piz", "26 Poz",
					"27 Fizzip", "28 KissFlap", "29 ToneTun", "30 Goyle", "31 EuroCrik", "32 StCrickt", "33 Insects",
					"34 Quijada", "35 QujadaSt", "36 VelCrow", "37 Chuh", "38 Zippers", "39 ElectSnp", "40 CrickEur",
					"41 Droppin", "42 Dropped", "43 08 CgaHi", "44 08 CgaMd", "45 08 CgaLo", "46 SH Zap",
					"47 08 Clave", "48 55 Rim", "49 DynaRim", "50 E Click", "51 El Clave", "52 Syn Pop", "53 CodePerc",
					"54 UreyClik", "55 CodeStac", "56 Mtronome", "57 E Metal", "58 StrMetal", "59 Belly 1",
					"60 Chirper", "61 08Cwbell", "62 Gork", "63 HOG Talk", "64 Belly 2", "65 Oddy", "66 Button",
					"67 AlievZar", "68 Dank Mtl", "69 Pole", "70 HOG Mtl", "71 Prilftor", "72 Hiccough", "73 DirtyEye",
					"74 Grunch", "75 lofluttr", "76 IcyThrum", "77 VEX332", "78 QOB2020", "79 GAX.4NN", "80 AXPN4.3R",
					"81 AN599R", "82 GN335L", "83 Splashon", "84 Shish 1", "85 08MarcSm", "86 08MarcLg", "87 E Guiro",
					"88 Syn Whip", "89 Burstin", "90 Burst", "91 GeeTick", "92 Strike", "93 Noise.", "94 Noised",
					"95 CrckrJck", "96 Bloorp", "97 Wizip", "98 Zwack", "99 Guanko", "100 Shish 2", "101 Shash",
					"102 TingPok", "103 Bursting", "104 LofiShak", "105 SonrCong", "106 Skrchoid", "107 PingTok",
					"108 SonarHit", "109 ShapNoiz", "110 Slapper", "111 Martel", "112 DopNoiz", "113 WyteNoiz",
					"114 IronLung", "115 RazorDwn", "116 RezoPink", "117 ModuFlan", "118 Mod Zap", "119 Rez Zap",
					"120 Shapes", "121 Ranked", "122 VeloNut", "123 Ticker", "124 RapThing", "125 Vel Zizz",
					"126 LofiJngl", "127 Space 0" },
			// SPECIAL FX
			// (SFx)
			{ "0 Garbage", "1 Balls", "2 Bumper", "3 AC/DC", "4 Cyber", "5 SShowBob", "6 Perketen", "7 WhipSlap",
					"8 Dog Toy", "9 DeButton", "10 Red", "11 Ernie", "12 Kat", "13 Ghosts", "14 Sy Bee", "15 Mod Pan",
					"16 Screech", "17 DeadLoon", "18 Scr Pull", "19 Scr Push", "20 SkrchTin", "21 Pushy", "22 Anvil",
					"23 Clank", "24 Clanger", "25 Razor", "26 Bork", "27 Galley", "28 Tubed", "29 DeMetel",
					"30 PiperUp", "31 Bouy", "32 Staggerd", "33 Koanic", "34 Entrance", "35 Fingers", "36 Far East",
					"37 Tim", "38 Blocks", "39 ClavCord", "40 Toffler", "41 Jon Kage", "42 Comb", "43 Who Do?",
					"44 Bedrock", "45 Bones", "46 Horsey", "47 Static", "48 CastaDwn", "49 ClipClop", "50 Clicky",
					"51 Soda Up", "52 Wicker", "53 DoorShut", "54 DoorSlam", "55 Door", "56 DistDoor", "57 TrukDoor",
					"58 Bashfull", "59 Western", "60 TNT", "61 Deepy", "62 Smasher", "63 Dynamo", "64 Vi-Dow",
					"65 Big Rez", "66 Snave", "67 Cylon", "68 Berz", "69 Omni", "70 Twisted", "71 Toccata",
					"72 Gastown", "73 Ray Gun", "74 Danger", "75 Flutter", "76 Distance", "77 MPfennig", "78 Revkel",
					"79 RevRev", "80 RideRevz", "81 Zwack", "82 Whirly", "83 Bird", "84 Crickets", "85 F Noize",
					"86 Das Boat", "87 Minar", "88 Majar", "89 Susar", "90 Hydra", "91 Ping", "92 WatrTaxi",
					"93 Tchchch", "94 Syzygy", "95 Spokes", "96 Zip By", "97 MatchStk", "98 Doppel", "99 Xaos",
					"100 Astral", "101 Scissors", "102 Shire", "103 CrshVerb", "104 Pneumatc", "105 Watrhole",
					"106 Event", "107 Rebound", "108 Industry", "109 Rev Hit", "110 Pleides", "111 Pratfall",
					"112 Model T", "113 Ghostly", "114 Termintr", "115 Sonar", "116 Da Ocean", "117 NiteMare",
					"118 Moto X", "119 Gizmo", "120 3rd Kind", "121 Servo", "122 V2", "123 U Turn", "124 Machine",
					"125 InFlight", "126 Hyperbol", "127 Silence" },
			// CHROMATIC
			// (CHR)
			{ "0 TruTimpH", "1 TruTimpM", "2 TruTimpL", "3 FatTimpH", "4 FatTimpM", "5 FatTimpL", "6 SftTimpH",
					"7 SftTimpM", "8 SftTimpL", "9 DynTimpH", "10 DynTimpM", "11 DynTimpL", "12 Mars", "13 DeTymp",
					"14 Kettle", "15 Symfo Hi", "16 Symfo Lo", "17 Vibes Hi", "18 Vibes Lo", "19 Vibe 4th",
					"20 Vibe Lyd", "21 Vibe Alt", "22 Vibe11th", "23 GlasVibH", "24 GlasVibL", "25 Rubiks H",
					"26 Rubiks M", "27 Rubiks L", "28 FakeBell", "29 Vibrato", "30 AmbKalim", "31 Xylo Hi",
					"32 Xylo Lo", "33 MarimbaH", "34 MarimbaL", "35 SynClavz", "36 Mariachi", "37 Caribe H",
					"38 Caribe L", "39 StlDrumH", "40 StlDrumL", "41 Jersey", "42 CelesteH", "43 CelesteL",
					"44 Glock Hi", "45 Glock Lo", "46 Foreign", "47 T Bell H", "48 T Bell L", "49 OctoBell",
					"50 Tower", "51 FantasyH", "52 FantasyL", "53 NiceBelz", "54 Shiny", "55 Alloy Hi", "56 Alloy Lo",
					"57 Aggala H", "58 Aggala L", "59 LuminonH", "60 LuminonL", "61 MetlVibH", "62 MetlVibM",
					"63 MetlVibL", "64 ZenCoinH", "65 ZenCoinM", "66 ZenCoinL", "67 FantasmH", "68 FantasmL",
					"69 Morlocks", "70 Evo Wave", "71 Orchessa", "72 Infidel", "73 Riot", "74 DanzHit", "75 MegaHit",
					"76 FuturHit", "77 MCMXVIII", "78 Reverend", "79 Guit Hit", "80 GuitGate", "81 DATLimit",
					"82 LoudSlap", "83 HugeSlap", "84 Zap Slap", "85 Str Slap", "86 HammrBas", "87 ChrdBass",
					"88 Thumbin", "89 TiteBerH", "90 TiteBerL", "91 Berimba", "92 Distorto", "93 Acid Ind",
					"94 Punchy", "95 BassX", "96 Syn Bass", "97 TubeBass", "98 Jungled", "99 Cygnus", "100 Twanger",
					"101 BasSaw", "102 ToughBaz", "103 Quik Sqr", "104 Quik Saw", "105 M Rogue", "106 Taurus",
					"107 OB 4Ever", "108 Detuner", "109 Logan", "110 440V Arc", "111 Wonka", "112 DontWait",
					"113 SawTrptH", "114 SawTrptL", "115 TiteTptH", "116 TiteTptL", "117 SqrDot H", "118 SqrDot M",
					"119 SqrDot L", "120 OctoDot1", "121 OctoDot2", "122 OctoDot3", "123 Castle", "124 Tainted",
					"125 SineHitH", "126 SineHitL", "127 SloAtkPd" },
			// USER
			// (USR)
			{ "0 WoodAttk", "1 FatBoyAM", "2 Chorused", "3 Axecent", "4 Whap K", "5 Its Log", "6 YouAre#6",
					"7 Clipper", "8 Combat", "9 KickDrum", "10 Warlord", "11 Barnstrm", "12 Plumbers", "13 Adventur",
					"14 Tempest", "15 Puck", "16 DopeBeat", "17 EWoodK", "18 SynMini", "19 PipeBmpK", "20 Dirty K",
					"21 SpapFuzz", "22 Coder", "23 BD Boomr", "24 GatdMeth", "25 BatKick", "26 Code NV", "27 BumpJump",
					"28 Abyss", "29 Thoom NV", "30 Sub Boom", "31 MW Boom4", "32 Pipe NV", "33 SnarDrum", "34 Tickle",
					"35 Barney", "36 TrustNo1", "37 Toomsnr", "38 Stkit2U", "39 Mr. Funk", "40 Oh Yahh", "41 FrnkBlak",
					"42 PetrWats", "43 OkaySure", "44 Realism", "45 Globulin", "46 ZincCmBk", "47 Gen X", "48 Jenny",
					"49 A Snare", "50 Wetsuit", "51 I Quit", "52 Fwak !", "53 BalladRv", "54 Stereo 1", "55 Asteroid",
					"56 Randazap", "57 Blixo", "58 Shell", "59 CofeeCan", "60 Pinecone", "61 Pneumatc", "62 LoopSnrX",
					"63 Speng", "64 Spyng", "65 Ceramix", "66 Titehead", "67 Med Hit", "68 Pail", "69 PopBottl",
					"70 Dry Drum", "71 ThickDrm", "72 Ghatom", "73 MilkJug", "74 HardShkr", "75 Abacus", "76 Plastick",
					"77 NoisyDrm", "78 Resinblk", "79 Bumper", "80 BotlDrum", "81 Big Drum", "82 Deadhead",
					"83 Thumper", "84 PigRoast", "85 PhaseTom", "86 TomFlam1", "87 TomFlam2", "88 1Note #1",
					"89 1Note #2", "90 RokClsd1", "91 Rock 1/2", "92 Rock Opn", "93 Znap", "94 WydClap3",
					"95 BoxORubr", "96 Jaw", "97 Ultraman", "98 08 Rim", "99 BleepBas", "100 DeepRezo", "101 Turbolft",
					"102 Long Sqr", "103 Wait 4it", "104 Lingerer", "105 Bleep", "106 ChorTptH", "107 ChorTptL",
					"108 Barker", "109 ToyPno H", "110 ToyPno L", "111 TopTimpH", "112 TopTimpM", "113 TopTimpL",
					"114 SubTimpH", "115 SubTimpM", "116 SubTimpL", "117 VibeMin9", "118 Vibe13th", "119 Vibe 9th",
					"120 Vibe 6th", "121 VibeHmin", "122 VibeHdim", "123 VibeMaj7", "124 Vibe69", "125 Vibe Sus",
					"126 Chime", "127 OrchGate" } };

}
