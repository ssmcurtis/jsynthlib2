package synthdrivers.YamahaMotif;
import core.*;
import javax.swing.JInternalFrame;

/** Driver for Yamaha Motif Normal Voices */
public class YamahaMotifNormalVoiceDriver extends YamahaMotifSingleDriver
{

   public YamahaMotifNormalVoiceDriver() {
     // This is a hack until getting built in voices is supported.
     authors = "Rib Rdb (ribrdb at yahoo.com)";
     bankNumbers =new String[] {"User","User"};
     patchNumbers = new String[128];
     defaults_filename = "normal.syx";
     patchType="Normal Voice";
     base_address = "08";
     edit_buffer_base_address = "0F";
     parameter_base_address = "40";
     patchSize = 1250;
     trimSize = 1250;
     numSysexMsgs = 35;
     yamaha_init();
   }
  public JInternalFrame editPatch(Patch p) {
    return new YamahaMotifNormalVoiceEditor(p);
  }
  public Patch createNewPatch() { return super.createNewPatch(); }

}
