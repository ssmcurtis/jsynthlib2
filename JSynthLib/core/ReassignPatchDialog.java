/*
 * ReassignPatchDialog.java  
 * $Id$
 */

package core;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;


/**
 *
 * ReassignPatchDialog.java - If more than two devices are loaded which supports the given patch,
 * show this Dialog to choose a new Device/Driver combination for the patch.
 * The internal patch assignment is used to send/play a patch.
 * @author  Torsten Tittmann
 * @version v 1.0, 2003-02-03 
 */
public class ReassignPatchDialog extends JDialog {

  //===== Instance variables
  private boolean driverMatched=false;
  private Driver driver;
  private Device device;
  private int deviceNum;
  private int driverNum;
  private Patch p;
  private StringBuffer patchString;

  private ArrayList deviceAssignmentList = new ArrayList();

  JLabel myLabel;

  JComboBox deviceComboBox;
  JComboBox driverComboBox;

 /**
  * Constructor
  * @param patch The Patch to reassign
  */
  public ReassignPatchDialog (Patch patch) {
    super(PatchEdit.instance, "Reassign Patch to another Device/Driver", true);

    p           = patch;
    patchString = p.getPatchHeader();
    // now the panel
    JPanel dialogPanel = new JPanel(new BorderLayout(5, 5));

    myLabel=new JLabel("Please select a Device/Driver to Reassign.",JLabel.CENTER);
    dialogPanel.add(myLabel, BorderLayout.NORTH);

    //=================================== Combo Panel ==================================
    //----- Create the combo boxes
    deviceComboBox = new JComboBox();
    deviceComboBox.addActionListener(new DeviceActionListener());
    deviceComboBox.setRenderer(new DeviceCellRenderer());

    driverComboBox = new JComboBox();
    driverComboBox.setRenderer(new DriverCellRenderer());

    boolean newDevice=true;
    //----- First Populate the Device/Driver List with Device/Driver. which supports the patch
    for (int i=0, n=0; i<PatchEdit.appConfig.deviceCount();i++)
    {
      device=(Device)PatchEdit.appConfig.getDevice(i);

      for (int j=0, m=0; j<device.driverList.size();j++)
      {
	if ( (driver=(Driver)device.driverList.get(j)).supportsPatch(patchString,p) )
	{
	  if (newDevice)	// only one entry for each supporting device
	  {
	    deviceAssignmentList.add(new deviceAssignment( i, device) );	// the original deviceNum/device
	    newDevice = false;
            n++;		// How many deviceAssignment?
	  }
	  ((deviceAssignment)deviceAssignmentList.get(n-1)).add(j, driver);	// the original driverNum/driver

	  if ( i == p.deviceNum && j == p.driverNum)	// default is patch internal deviceNum & driverNum
	  {
	    deviceNum = n-1;
            driverNum = m;
          }
	  driverMatched=true;	// Hipp, Hipp, Hurra - at least one driver was found
          m++;
        }
	newDevice = true;
      }
    }

    //----- Populate the combo boxes with the entries of the deviceAssignmentList
    for (int i=0; i<deviceAssignmentList.size();i++)
    {
      deviceComboBox.addItem( deviceAssignmentList.get(i) );
    }
    deviceComboBox.setSelectedIndex(deviceNum);		// This was the original device

    //----- Layout the labels in a panel.
    JPanel labelPanel = new JPanel(new GridLayout(0, 1, 5, 5));
    labelPanel.add(new JLabel("Device:", JLabel.LEFT));
    labelPanel.add(new JLabel("Driver:", JLabel.LEFT));

    //----- Layout the fields in a panel
    JPanel fieldPanel = new JPanel(new GridLayout(0, 1));
    fieldPanel.add(deviceComboBox);
    fieldPanel.add(driverComboBox);

   //----- Create the comboPanel, labels on left, fields on right
    JPanel comboPanel = new JPanel(new BorderLayout());
    comboPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    comboPanel.add(labelPanel, BorderLayout.CENTER);
    comboPanel.add(fieldPanel, BorderLayout.EAST);
    dialogPanel.add(comboPanel, BorderLayout.CENTER);

    //=================================== Button Panel ==================================
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout ( new FlowLayout (FlowLayout.CENTER) );

    JButton reassign = new JButton("Reassign");
    reassign.addActionListener(new ReassignActionListener());
    buttonPanel.add(reassign);

    JButton cancel = new JButton ("Cancel");
    cancel.addActionListener (
      new ActionListener () {
        public void actionPerformed (ActionEvent e) {
          setVisible(false);
	  dispose();
        }
      }
    );
    buttonPanel.add(cancel);
    getRootPane().setDefaultButton(reassign);
    dialogPanel.add(buttonPanel, BorderLayout.SOUTH);

    //===== Final initialisation of dialog box
    getContentPane().add(dialogPanel);
    pack();
    centerDialog();

    // show() or not to show(), that's the question!
    if (driverMatched==true)
    {
      if (deviceComboBox.getItemCount()>1)
	this.show();
      else
      {
        JOptionPane.showMessageDialog(null, "Only one driver was found, which support this patch! Nothing will happen", "Error while reassigning a patch", JOptionPane.INFORMATION_MESSAGE);
	dispose();
      }
    }
    else
    {
      JOptionPane.showMessageDialog(null, "Oops, No driver was found, which support this patch! Nothing will happen", "Error while reassigning a patch", JOptionPane.WARNING_MESSAGE);
      dispose();
    }
  }


 /**
  *
  */
  private void centerDialog () {
    Dimension screenSize = this.getToolkit().getScreenSize();
    Dimension size = this.getSize ();
    this.setLocation((screenSize.width - size.width)/2, (screenSize.height - size.height)/2);
  }


 /**
  * Makes the actual work after pressing the 'Reassign' button
  */
  class ReassignActionListener implements ActionListener {
    public void actionPerformed (ActionEvent evt) {

      p.deviceNum = (int) ((deviceAssignment)deviceComboBox.getSelectedItem()).deviceNum;
      p.driverNum = (int) ((driverAssignment)driverComboBox.getSelectedItem()).driverNum;

      setVisible(false);
      dispose();
    }
  }


 /**
  * Repopulate the Driver ComboBox with valid drivers after a Device change 
  */
  class DeviceActionListener implements ActionListener {
    public void actionPerformed (ActionEvent evt) {

      deviceAssignment myDevAssign = (deviceAssignment)deviceComboBox.getSelectedItem();
      driverAssignment myDrvAssign;

      driverComboBox.removeAllItems();

      if (myDevAssign != null)
      {
        for (int j=0;j<myDevAssign.driverAssignmentList.size ();j++)
        {
	  myDrvAssign = (driverAssignment)myDevAssign.driverAssignmentList.get(j);

          if ( !(Converter.class.isInstance (myDrvAssign.driver) ) &&
		( myDrvAssign.driver.supportsPatch(patchString,p)) )  
              driverComboBox.addItem(myDrvAssign);
        }
      }
      driverComboBox.setSelectedIndex(driverNum);	// the original driver is the default

      driverComboBox.setEnabled(driverComboBox.getItemCount() > 1);
    }
  }


 /**
  * New standard renderer for ComboBoxes
  */
  class ComboCellRenderer extends JLabel implements ListCellRenderer {
    public ComboCellRenderer() {
      setOpaque(true);
    }

    public Component getListCellRendererComponent (
        JList list,
        Object value,
        int index,
        boolean isSelected,
        boolean cellHasFocus
      ) {

      setText(value == null ? "" : value.toString());
      setBackground(isSelected ? Color.red : Color.white);
      setForeground(isSelected ? Color.white : Color.black);
      return this;
    }
  }

 /**
  * Special renderer for Device ComboBox to display the valid DeviceName
  */
  class DeviceCellRenderer extends ComboCellRenderer {
    public Component getListCellRendererComponent (
        JList list,
        Object value,
        int index,
        boolean isSelected,
        boolean cellHasFocus
      ) {

      super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      setText(value == null ? "" : ((deviceAssignment)value).device.getDeviceName());
      return this;
    }
  }

 /**
  * Special renderer for Driver ComboBox to display the valid PatchType
  */
  class DriverCellRenderer extends ComboCellRenderer {
    public Component getListCellRendererComponent (
        JList list,
        Object value,
        int index,
        boolean isSelected,
        boolean cellHasFocus
      ) {

      super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      setText(value == null ? "" : ((driverAssignment)value).driver.getPatchType());
      return this;
    }
  }

 /**
  * We need to remember the original deviceNum variable of the Device and the Device itself.
  * Each deviceAssignment Object contains a List of driverAssignments which remembers
  * the original driverNum resp. driver. Maybe more than on driver supports the patch.
  *
  * @see driverAssignment
  */
  class deviceAssignment
  {
    protected int deviceNum;
    protected Device device;
    protected ArrayList driverAssignmentList = new ArrayList();

    deviceAssignment(int deviceNum, Device device)
    {
      this.deviceNum = deviceNum;
      this.device    = device;
    }

    void add(int driverNum, Driver driver)
    {
      this.driverAssignmentList.add(new driverAssignment(driverNum, driver));
    }
  }


 /**
  * We need to remember the original driverNum variable of the Driver.
  *
  * @see deviceAssignment
  */
  class driverAssignment
  {
    protected int driverNum;
    protected Driver driver;

    driverAssignment(int driverNum, Driver driver)
    {
      this.driverNum = driverNum;
      this.driver    = driver;
    }
  }

} 
