package synthdrivers.KawaiK4;
import core.*;
import java.io.*;
import javax.swing.*;

/** Driver for Kawai K4 Multi Bank
 *
 * @author Gerrit Gehnen
 * @version $Id$
 */

public class KawaiK4MultiBankDriver extends BankDriver
{
    
    public KawaiK4MultiBankDriver ()
    {
	super ("MultiBank","Gerrit Gehnen",64,4);
        sysexID="F040**210004**40";
       sysexRequestDump=new SysexHandler("F0 40 @@ 01 00 04 *bankNum* 40 F7");

        deviceIDoffset=2;
        bankNumbers =new String[]
        {"0-Internal","1-External"};
        patchNumbers=new String[]
        {"A-1","A-2","A-3","A-4","A-5","A-6","A-7","A-8",
         "A-9","A-10","A-11","A-12","A-13","A-14","A-15","A-16",
         "B-1","B-2","B-3","B-4","B-5","B-6","B-7","B-8",
         "B-9","B-10","B-11","B-12","B-13","B-14","B-15","B-16",
         "C-1","C-2","C-3","C-4","C-5","C-6","C-7","C-8",
         "C-9","C-10","C-11","C-12","C-13","C-14","C-15","C-16",
         "D-1","D-2","D-3","D-4","D-5","D-6","D-7","D-8",
         "D-9","D-10","D-11","D-12","D-13","D-14","D-15","D-16"};
         
         singleSysexID="F040**2*0004";
         singleSize=77+9;
         
    }
    
    public int getPatchStart (int patchNum)
    {
        int start=(77*patchNum);
        start+=8;  //sysex header
        return start;
    }
    public String getPatchName (Patch p,int patchNum)
    {
        int nameStart=getPatchStart (patchNum);
        nameStart+=0; //offset of name in patch data
        try
        {
            StringBuffer s= new StringBuffer (new String (p.sysex,nameStart,
            10,"US-ASCII"));
            return s.toString ();
        } catch (UnsupportedEncodingException ex)
        {return "-";}
        
    }
    
    public void setPatchName (Patch p,int patchNum, String name)
    {
        patchNameSize=10;
        patchNameStart=getPatchStart (patchNum);
        
        if (name.length ()<patchNameSize) name=name+"            ";
        byte [] namebytes = new byte [64];
        try
        {
            namebytes=name.getBytes ("US-ASCII");
            for (int i=0;i<patchNameSize;i++)
                p.sysex[patchNameStart+i]=namebytes[i];
            
        } catch (UnsupportedEncodingException ex)
        {return;}
    }
    
    
    public void calculateChecksum (Patch p,int start,int end,int ofs)
    {
        int i;
        int sum=0;
        
        for (i=start;i<=end;i++)
            sum+=p.sysex[i];
        sum+=0xA5;
        p.sysex[ofs]=(byte)(sum % 128);
        // p.sysex[ofs]=(byte)(p.sysex[ofs]^127);
        // p.sysex[ofs]=(byte)(p.sysex[ofs]+1);
    }
  
    
    public void calculateChecksum (Patch p)
    {
        for (int i=0;i<64;i++)
            calculateChecksum (p,8+(i*77),8+(i*77)+75,8+(i*77)+76);
    }
    
    public void putPatch (Patch bank,Patch p,int patchNum)
    {
        if (!canHoldPatch (p))
        {JOptionPane.showMessageDialog (null, "This type of patch does not fit in to this type of bank.","Error", JOptionPane.ERROR_MESSAGE); return;}
        
        System.arraycopy (p.sysex,8,bank.sysex,getPatchStart (patchNum),77);
        calculateChecksum (bank);
    }
    public Patch getPatch (Patch bank, int patchNum)
    {
        try
        {
            byte [] sysex=new byte[77+9];
            sysex[00]=(byte)0xF0;sysex[01]=(byte)0x40;sysex[02]=(byte)0x00;
            sysex[03]=(byte)0x20;sysex[04]=(byte)0x00;sysex[05]=(byte)0x04;
            sysex[06]=(byte)0x00;sysex[07]=(byte)(0x40+patchNum);
            sysex[77+8]=(byte)0xF7;
            System.arraycopy (bank.sysex,getPatchStart (patchNum),sysex,8,77);
            Patch p = new Patch (sysex, getDevice());
            p.getDriver().calculateChecksum (p);
            return p;
        }catch (Exception e)
        {ErrorMsg.reportError ("Error","Error in K4 MultiBank Driver",e);return null;}
    }
    public Patch createNewPatch ()
    {
        byte [] sysex = new byte[77*64+9];
        sysex[0]=(byte)0xF0; sysex[1]=(byte)0x40;sysex[2]=(byte)0x00;sysex[3]=(byte)0x21;sysex[4]=(byte)0x00;
        sysex[5]=(byte)0x04; sysex[6]=(byte)0x0;sysex[7]=0x40;sysex[77*64+8]=(byte)0xF7;
        Patch p = new Patch (sysex, this);
        for (int i=0;i<64;i++)
            setPatchName (p,i,"New Patch");
        calculateChecksum (p);
        return p;
    }

  public void requestPatchDump(int bankNum, int patchNum) {
        NameValue nv[]=new NameValue[1];
        nv[0]=new NameValue("bankNum",bankNum<<1);
        
        byte[] sysex = sysexRequestDump.toByteArray((byte)getChannel(),nv);
        
        SysexHandler.send(getPort(), sysex);
    }
  
     public void storePatch (Patch p, int bankNum,int patchNum)
    {
        try
        {Thread.sleep (100);}catch (Exception e)
        { }
        p.sysex[3]=(byte)0x21;
        p.sysex[6]=(byte)(bankNum<<1);
        p.sysex[7]=(byte)0x40;
        sendPatchWorker (p);
        try
        {Thread.sleep (100); } catch (Exception e)
        {}
    }
}
