/*
 * PatchGridModel.java
 *
 * Created on 16. Mai 2001, 21:58
 */


package core;

import javax.swing.table.AbstractTableModel;

class PatchGridModel extends AbstractTableModel
{

    public Patch bankData;
    public BankDriver bankDriver;

    /**
     * @param p
     * @param d  */
    public PatchGridModel (Patch p,BankDriver d)
    {super();
ErrorMsg.reportStatus("PatchGridModel");
     bankData=p;
     bankDriver=d;
    }


    /**
     * @return  */
    public int getColumnCount ()
    {
        return bankDriver.numColumns;
    }

    /**
     * @return  */
    public int getRowCount ()
    {
        return bankDriver.numPatches/bankDriver.numColumns;
    }

    /**
     * @param col
     * @return  */
    public String getColumnName (int col)
    {
        return "";
    }

    /**
     * @param row
     * @param col
     * @return  */
    public Object getValueAt (int row, int col)
    {
        return (bankDriver.patchNumbers[         col*bankDriver.numPatches/bankDriver.numColumns+row]+" "+
        bankDriver.getPatchName (bankData,col*bankDriver.numPatches/bankDriver.numColumns+row));
    }
    /**
     * @param row
     * @param col
     * @return  */
    public Patch getPatchAt(int row, int col)
    {
        return bankDriver.getPatch(bankData,col*bankDriver.numPatches/bankDriver.numColumns+row);
    }
    /**
     * @param c
     * @return  */
    public Class getColumnClass (int c)
    {
        return getValueAt (0, c).getClass ();
    }
    /**
     * @param row
     * @param col
     * @return  */
    public boolean isCellEditable (int row, int col)
    {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.

//----- Start phil@muqus.com (allow patch name editing from a bank edit window)
      //return false;
      return true;
//----- End phil@muqus.com

    }
    /**
     * @param p
     * @param row
     * @param col  */
    public void setPatchAt(Patch p,int row,int col)
    {
        bankDriver.putPatch(bankData,p,col*bankDriver.numPatches/bankDriver.numColumns+row);
        fireTableCellUpdated (row, col);
    }
    /**
     * @param value
     * @param row
     * @param col  */
    public void setValueAt (Object value, int row, int col)
    {
//----- Start phil@muqus.com (allow patch name editing from a bank edit window)
      int patchNum = col * bankDriver.numPatches / bankDriver.numColumns + row;
      bankDriver.setPatchName(bankData, patchNum, ((String)value).substring((bankDriver.patchNumbers[patchNum] + " ").length()));
//----- End phil@muqus.com
      fireTableCellUpdated (row, col);
    }
}
