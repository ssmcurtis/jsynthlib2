/*
 * JSynthlib - "Micro Tuning" Editor for Yamaha DX7s
 * ===================================================================
 * @author  Torsten Tittmann
 * file:    YamahaDX7sMicroTuningEditor.java
 * date:    25.02.2003
 * @version 0.1
 *
 * Copyright (C) 2002-2003  Torsten.Tittmann@t-online.de
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 *
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * CAUTION: This is an experimental driver. It is not tested on a real device yet!
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 *
 * history:
 *         25.02.2003 v0.1: first published release
 *                          
 *
 */

package synthdrivers.YamahaDX7s;
import core.*;
import java.lang.String.*;
import java.text.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import javax.swing.event.*;

class YamahaDX7sMicroTuningEditor extends PatchEditorFrame 
{
  static final String [] SemiToneName		= new String [] {"C","C#","D","D#","E","F","F#","G","G#","A","A#","B"};

  static final String [] OctaveName		= new String [] {"-2","-1","0","1","2","3","4","5","6","7","8"};

  static final String [] CoarseStepName		= new String [] {
"-42","-41","-40","-39","-38","-37","-36","-35","-34","-33","-32","-31","-30","-29","-28","-27",
"-26","-25","-24","-23","-22","-21","-20","-19","-18","-17","-16","-15","-14","-13","-12","-11",
"-10","-9","-8","-7","-6","-5","-4","-3","-2","-1"," 0"," 1"," 2"," 3"," 4"," 5",
" 6"," 7"," 8"," 9","10","11","12","13","14","15","16","17","18","19","20","21",
"22","23","24","25","26","27","28","29","30","31","32","33","34","35","36","37",
"38","39","40","41","42"
                                                                };

  static final String [] FineStepName		= new String [] {
"  0","  1","  2","  3","  4","  5","  6","  7","  8","  9"," 10"," 11"," 12"," 13"," 14"," 15",
" 16"," 17"," 18"," 19"," 20"," 21"," 22"," 23"," 24"," 25"," 26"," 27"," 28"," 29"," 30"," 31",
" 32"," 33"," 34"," 35"," 36"," 37"," 38"," 39"," 40"," 41"," 42"," 43"," 44"," 45"," 46"," 47",
" 48"," 49"," 50"," 51"," 52"," 53"," 54"," 55"," 56"," 57"," 58"," 59"," 60"," 61"," 62"," 63",
" 64"," 65"," 66"," 67"," 68"," 69"," 70"," 71"," 72"," 73"," 74"," 75"," 76"," 77"," 78"," 79",
" 80"," 81"," 82"," 83"," 84"," 85"," 86"," 87"," 88"," 89"," 90"," 91"," 92"," 93"," 94"," 95",
" 96"," 97"," 98"," 99","100","101","102","103","104","105","106","107","108","109","110","111",
"112","113","114","115","116","117","118","119","120","121","122","123","124","125","126","127"
                                                                };



  public YamahaDX7sMicroTuningEditor(Patch patch)
  {
    super ("Yamaha DX7s \"Micro Tuning\" Editor",patch);


    PatchEdit.waitDialog.show();        // Because it needs some time to build up the editor frame


    int SemiTone, Octave, keyByte;

    JPanel microPane = new JPanel();
    microPane.setLayout(new GridBagLayout());gbc.weightx=1;

    for (Octave = 0 ; Octave < OctaveName.length ; Octave++)
    {
      for (SemiTone = 0; SemiTone < SemiToneName.length ; SemiTone++)
      {
	if ( (Octave == OctaveName.length-1) && (SemiTone == 8) ) break;

        keyByte = SemiTone+12*Octave;

        gbc.gridx=3*SemiTone;gbc.gridy=3*Octave+10;gbc.gridwidth=1;gbc.gridheight=1;
        microPane.add(new JLabel(SemiToneName[SemiTone]+OctaveName[Octave]),gbc);

        addWidget(microPane, new ComboBoxWidget(
						"Semitones",
						patch,				// coarse
			                        new ParamModel(patch,16+2*keyByte),
                        			new MicroTuningSender(patch, keyByte , true ),		// true -> coarse
			                        CoarseStepName),
						3*SemiTone+1, 3*Octave+10, 1, 1, 24*Octave+2*SemiTone   );

        addWidget(microPane, new ComboBoxWidget(
						"1.1719/Step",
						patch,				// fine
			                        new ParamModel(patch,16+2*keyByte+1),
                         			new MicroTuningSender(patch, keyByte , false),		// false -> fine
			                        FineStepName),
						3*SemiTone+1, 3*Octave+11, 1, 1, 24*Octave+2*SemiTone+1 );


        gbc.gridx=3*SemiTone+2;gbc.gridy=3*Octave+12;gbc.gridwidth=1;gbc.gridheight=1;
        microPane.add(new JLabel(" "),gbc);
      }
    }

    scrollPane.add(microPane,gbc);
    pack();
    show();


    PatchEdit.waitDialog.hide();        // Okay, the editor frame is ready
  }
}