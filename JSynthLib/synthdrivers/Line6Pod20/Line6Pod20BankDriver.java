//
//  Line6Pod20BankDriver.java
//  JSynthLib
//
//  Created by Jeff Weber on Sat Jun 26 2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package synthdrivers.Line6Pod20;
import core.*;
import java.io.*;
import javax.swing.*;

public class Line6Pod20BankDriver extends BankDriver
{
    private static final SysexHandler SYS_REQ = new SysexHandler(Constants.BANK_DUMP_REQ_ID); //Program Bank Dump Request
    
    public Line6Pod20BankDriver()
    {
        super(Constants.BANK_PATCH_TYP_STR, Constants.AUTHOR, Constants.PATCHES_PER_BANK, 1);        
        sysexID = Constants.BANK_SYSEX_MATCH_ID;
        
        deviceIDoffset = Constants.DEVICE_ID_OFFSET;
        
        bankNumbers  = Constants.BANK_BANK_LIST;
        patchNumbers = Constants.BANK_PATCH_LIST;  
        
        singleSysexID = Constants.SIGL_SYSEX_MATCH_ID;
        singleSize = Constants.SIGL_SIZE + Constants.PDMP_HDR_SIZE + 1;
        patchSize=Constants.PATCHES_PER_BANK * Constants.SIGL_SIZE + Constants.BDMP_HDR_SIZE + 1;
        patchNameSize = Constants.PATCH_NAME_SIZE;
    }
    
    /** Returns the offset of the start of the patch in nibblized (non-native) bytes.*/
    public int getPatchStart(int patchNum)
    {
        int start=(Constants.SIGL_SIZE / 2 * patchNum);
        start+=Constants.BDMP_HDR_SIZE;  //sysex header
        return start;
    }
    
    public String getPatchName(Patch p,int patchNum) {
        int nameStart=getPatchStart(patchNum);
        nameStart+=Constants.PATCH_NAME_START; //offset of name in patch data
        char c[] = new char[patchNameSize];
        for (int i = 0; i < patchNameSize; i++) {
            c[i] = (char)PatchBytes.getSysexByte(p.sysex, Constants.BDMP_HDR_SIZE, i + nameStart);
        }
        
        return new String(c);
    }
    
    public void setPatchName(Patch p,int patchNum, String name)
    {
        patchNameStart=getPatchStart(patchNum);
        patchNameStart+=Constants.PATCH_NAME_START; //offset of name in patch data
        if (name.length()<patchNameSize) name=name+"                ";
        byte [] namebytes = new byte [64];
        try {
            namebytes=name.getBytes("US-ASCII");
            for (int i=0;i<patchNameSize;i++) {
                PatchBytes.setSysexByte(p, Constants.BDMP_HDR_SIZE, i + patchNameStart, namebytes[i]);
            }
        } catch (UnsupportedEncodingException ex) {return;}
    }
    
    public void putPatch(Patch bank, Patch p, int patchNum)  // Tested??  // Retest with new version of Core.*
    { 
        if (!canHoldPatch(p)) {
            JOptionPane.showMessageDialog
            (null,
             "This type of patch does not fit in to this type of bank.",
             "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        System.arraycopy(p.sysex, Constants.PDMP_HDR_SIZE, bank.sysex, getSysexStart(patchNum),Constants.SIGL_SIZE);
    }
    
    public Patch getPatch(Patch bank, int patchNum)
    {
        byte [] sysex=new byte[Constants.SIGL_SIZE + Constants.PDMP_HDR_SIZE + 1];
        System.arraycopy(Constants.SIGL_DUMP_HDR_BYTES, 0, sysex, 0, Constants.PDMP_HDR_SIZE);
        sysex[7]=(byte)patchNum;  
        sysex[Constants.SIGL_SIZE + Constants.BDMP_HDR_SIZE+1]=(byte)0xF7;    
        System.arraycopy(bank.sysex, getSysexStart(patchNum), sysex, Constants.PDMP_HDR_SIZE, Constants.SIGL_SIZE);
        try{
            Patch p = new Patch(sysex, getDevice());
            return p;
        }catch (Exception e) {
            ErrorMsg.reportError("Error","Error in Bass Pod Bank Driver",e);
            return null;
        }
    }
    
    /** Returns the offset of the start of the patch in nibblized (native) bytes.*/
    public int getSysexStart(int patchNum)
    {
        int start=(Constants.SIGL_SIZE * patchNum);
        start+=Constants.BDMP_HDR_SIZE;  //sysex header
        return start;
    }
    
    /** Creates a new bank patch..*/
    public Patch createNewPatch()
    {
        byte [] sysex = new byte[Constants.BDMP_HDR_SIZE + (Constants.SIGL_SIZE * Constants.PATCHES_PER_BANK) + 1];
        System.arraycopy(Constants.BANK_DUMP_HDR_BYTES, 0, sysex, 0, Constants.BDMP_HDR_SIZE);
        sysex[Constants.BDMP_HDR_SIZE + (Constants.SIGL_SIZE * Constants.PATCHES_PER_BANK)]=(byte)0xF7;
        Patch p = new Patch(sysex, this);
        for (int i=0;i<Constants.PATCHES_PER_BANK;i++) {
            System.arraycopy(Constants.NEW_SYSEX, Constants.PDMP_HDR_SIZE, p.sysex, getSysexStart(i), Constants.SIGL_SIZE);
            setPatchName(p,i,"New Patch");
        }
        return p;
    }
    
    public void requestPatchDump(int bankNum, int patchNum) {
        send(SYS_REQ.toSysexMessage(getChannel(),
                                    new SysexHandler.NameValue("bankNum", bankNum << 1)));
    }
    
    public void storePatch (Patch p, int bankNum,int patchNum)
    {
        Patch[] thisPatch = new Patch[Constants.PATCHES_PER_BANK];
        for (int progNbr=0; progNbr<Constants.PATCHES_PER_BANK; progNbr++) {
            thisPatch[progNbr] = getPatch(p, progNbr);
        }
        for (int progNbr=0; progNbr<Constants.PATCHES_PER_BANK; progNbr++) {
            int bankNbr = progNbr / 4;
            int ptchNbr = progNbr % 4;
            ((Line6Pod20SingleDriver)thisPatch[progNbr].getDriver()).storePatch(thisPatch[progNbr], bankNbr, ptchNbr);
        }
    }
}