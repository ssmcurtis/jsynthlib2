package org.jsynthlib.synthdrivers.studioelectronics.se1;

import org.jsynthlib.tools.DriverUtil;
import org.jsynthlib.tools.HexaUtil;

public class Se1 {

	public static final String VENDOR = "Studio Electronics";
	public static final String DEVICE = "SE1";

	public static final int HEADER_SIZE = 6;
	public static final int PROGRAM_SIZE_SYSEX = 135;
	public static final int PROGRAM_SIZE = PROGRAM_SIZE_SYSEX - HEADER_SIZE - 1;
	public static final int PROGRAM_COUNT_IN_BANK = 99;
	public static final int PROGRAM_COUNT_IN_SYNTH = PROGRAM_COUNT_IN_BANK * 2;
	public static final int BANK_SIZE_SYSEX = HEADER_SIZE + (PROGRAM_SIZE * PROGRAM_COUNT_IN_BANK) + 1;

	public static final int PATCH_NAME_START_AT = 117; // include header
	public static final int PATCH_NAME_LENGTH = 16;

	public static final String[] BANK_NAMES_PATCHES = new String[] { "Bank 1", "Bank 2" };

	public static final String DEVICE_SYSEX_ID = "F000004D01";
	
	public static final byte[] DEFAULT_BANK_HEADER = new byte[] { (byte) 0xF0, 0x00, 0x00, 0x4D, 0x01, 0x63, };
	public static final byte[] DEFAULT_PATCH_HEADER = new byte[] { (byte) 0xF0, 0x00, 0x00, 0x4D, 0x01, 0x00, };
	
	public static final String DEFAULT_PROGRAM_STRING = "4022010E183018410E433F210F39344A3C571C2E3D5F00001700006E01150B0A01020000413F0000000002017E000008090E0B00000001010001027E7A14000041140100010300220400000B00020700300000000000000076400212000400537E0100004602000877000000000000414E414C4F47204953204F4E202120202E";
	public static final String DEFAULT_BANK_STRING = "F000004D01634022010E183018410E433F210F39344A3C571C2E3D5F00001700006E01150B0A01020000413F0000000002017E000008090E0B00000001010001027E7A14000041140100010300220400000B00020700300000000000000076400212000400537E0100004602000877000000000000414E414C4F47204953204F4E202120202E000023000019183033442F5D15260047240200407F0A007F1700010100000B0201020000423F00013001006C000F0020000B0E00000001020001017E6A0E00004001017F0002000E0801000F00000E01300000000000000076400212360400537E01000046020008770000000000005345434F4E442032204E4F4E452020202E65221F001900184D56271B2D2B7700444B0400435C11007F1700090104160A01010200003C420001000000000000006001020C00000001000001016C4A520000544D670000040011020D000F00000E012B0000000000000076400212000400537E010000460200087700000000000032362048554E4452454420424C4F434B2E380220000000004748231D56640100000013006C551600005F0008070000020A01020000404000000000000000000050000E0E000000010200010168683A340000010100010300170A00000500000700300000000000000076400212000400537E01000046020008770000000000004241534943205245434F5244202020202E61321C00180019683E3628452C6738690B170D267F0B00001701022D09020E0400020000423F160044000F0027000010010B030000000102370101425E280000416001000103000F0319000F00020E01300000000000000076400212000400537E010000460200087700000000000057415443482048494D202020202020202E60121A000018007152222E0A3D310000005A0048506000001700000003140B0E00020000404000010000000000000070000E0C000000010000010172726834004D010100000400140600000B00000C01300000000000000076400212000400537E0100004602000877000000000000424F4F4F4D494E2720202020202020202E60121A000018007152222E523D310000005A004850600000170000000314030E00020000404000010000000000000070000E0C000000014000010172726834004D010100000400140600000B00000C01300000000000000076400212000400537E0100004602000877000000000000474C4F4F4D494E2720202020202020202E7F6E1E03007A564159263D4E2B570000030E0074190E7F7F1701120013000B0E000200003C410001000000007F00002001090600000001400001017644280000472D00000800000E0200000400020901300000000000000076400212000400537E0100004602000877000000000000424153532D4C494E45532020202020202E5E1D1800192A1800071F3C373E2100380C0F007A030F0001170011000115090A01020000403F0000000002017E00003001090B00000001020001027E50260000411401000103000B011D000B00020700300000000000000076400212000400537E0100004602000877000000000000434F4D494E2720434F525245435420202E7F1E1C0018301915622C423B292306290002067D061900001700010300060B040102000042410001000000000000003002090B00000001000001026064543E004D143A00020400110B00000D00000601300000000000000076400212000400537E01000046020008770000000000004556454E5446554C4C2053484F434B202E7F0019001801180A70713A1D26100010060200722B0000541700020009000E060002000040430001000000680000001003060500000001400000017E7E3400007A54007E000400100D00000E00000501300000000000000076400212000400537E0100004602000877000000000000464143452054484520424153532020202E7F221B001847186D421E290E2C2A00005600006F1C2100001701114000060F0A01020000443D0001000000000000003800050800000001010001025A1A5200005E4100000004000B0918000E00000D01300000000000000076400212000400537E1100004602000877000000000000534D4F4F54484544204F5554202020202E7F002D0019190141727F354A432F007F28190025001C1A7F170100130000090500020000404000343500000000000018010B0E00000001020000017E7E780000415A4E7F060208410900000F00000E00300000000000000076400212000400537E01000046020008770000000000004E4555424C452C204A414D45532020202E7F0017001919307F727F34434329004E2800001D3F001A001700002D22000B0501020000404000343500000000000010060B0300000001020000027E7E7626006D1C4E000604020E0B00000F00000E00300000000000000076400212000400537E010000460200087700000000000042524F54484552204F46204D494B45202E660017001918547F727F274B4329054E2808007D050B16001700002D22000A0101020000404000343500000000000000010B0300000001400001027E7E300000470707000604000E0B00000F00000E00300000000000000076400212000400537E010000460200087700000000000057452052454D454D42455220544F4E592E3F1D1A000016132F457C365242582C277F18007200117F000E00050C0935030A010200004042006E00220000001F006001060B00000001420101014A28001E007E007F6C0204000F0800000F00020E012C0000000000000076400212000400537E01000046020008770000000000004341505245545441204554484F5320202E7F181A0018371973247F204E4C464F78682400004D261E00170018000008090E0002000040402B727320000000000010090B0500000001023701027E3E7E000042000E000804001B0C62000F00000E013E0000000000000076400212000400537E010000460200087700000000000055204645454C20544841542032203F202E7F081E00000018417F33125A533C07513C00000063170000170016000A000B040102060040420001000000000000001009040C00000001420001017E144A000041010000000400140800000F00000E01300000000000000076400212000400537E0100004602000877000000000000434C4946464F524427532031302510202E60113200180000675951325944230041002D06003C1A7F001700000001000F0401020000403E00017F00000000000030000A0C00000001420001017E7C041A005A017F00020400160E00000000000024300000000000000076400212000400537E0100004602000877000000000000534352495054454420202020202020202E5D001800001800237F7F323C2C19277B6D1C00276E14000017001E0005000E0A000200003C42160044000F002700000802010E0000000142010101622C7E000C2D070100010308170042000F00020E013E0000000076000000400212000400537E01000046020008770000000000004341505249434F524E203120202020202E3D001B00192D1867595134414B230034001506007F1631000300000702190D0A00020000404700010000000000000010000A0C00000001020000027E541E00004D010100000400080800000A00020E002B0000000000000076400212000400537E01000046020008770000000000004341524C4F27532057415920202020202E3000180000000041426A2744435A001F24290078000E210017000816011D0B010002000040400034350000000000002809060C0000000102000101647E7E000027014E00060400230800000F00000E00300000000000000076400212000400537E0100004602000877000000000000415155414452454144202020202020202E4F2918001819185765302A512C30075F252B003F6B000E0E1701060A507F0B040002000042410C3500000F3A001B0020000C0E00000001400001026E60480000602D6D000809082D000E000F00000E01300000000000000076400212000400537E010000460200087700000000000041524341444941275320515545454E202E7F001F00191900776D262D3F432F004E1104001D7A041A00170000000000090500020000404000343500000000000008000E0E00000001400001017E7E48000041014E00060400140800000F00000E00300000000000000076400212000400537E01000046020008770000000000004D41524E4927532042455354202020202E38321500000014470F2328624107005D00130176001600005F00000800000204010200003F4000240000000000000058000B0E00000001420001017E7E3E0000000101000103000D0A00000500000700300000000000000076400212000400537E0100004602000877000000000000444953434F4E454354202020202020202E7F42200000190000002233273D130A4C291E00442B210000740005020011090B000200003C42032C6D2900236D000048000908000000014200010178466C0600000100000004000B0800000B00000C01340000000000000076400212000400537E01000046020008770000000000004A414E414348592020202020202020202E62173200180000673720325438230034002D0676001A7F001700001401070A0401020000403C000A0000000000000020000A0C0000000140000101647E000A0041017F00020400160E00000000000024300000000000000076400212000400537E0100004602000877000000000000383725205752414954482020202020202E1200160019182E571309310D434400373927003F4F28220E17001B0900000A04000200003D41004900000F24001B0048000B0E00000001400001025416200000602D6D000809000E0800000F00000E01300000000000000076400212000400537E0100004602000877000000000000534345505041434552515541202020202E12001600193100576509310D434400372327003F4F280E0E1700050900000902000200004241002600000F3A001B0008020B0E0000000140000102545A0E0000602D6D00080900140800000F00000E01300000000000000076400212000400537E0100004602000877000000000000564943544F52275320574156452020202E5542200000190700637F232D3F130A480C170000531B00007400050200110902000200003B42032C6D2900236D00006800090800000001020001017E567E0200000100000004000B0800000B00020C01320000000000000076400212000400537E0100004602000877000000000000524550524553454E54274E20532E452E2E4D00230030181641423E3C174100003C4200003B410051001700090006090A030002000040430E374D00000000000060000D0C00000000020000027E7E3A000041212700060400110C01000F00000E01300000000000000076400212000400537E01000046020008770000000000005741482057414820574148545A554E202E7F002200191878614F3E2C4B447F00062B4E0028005219281701001A00000D0500020000414000000000000000000048000B0E00000000420001027A6A0E00003401000005040B5E0700000F00000E01300000000000000076400212000400537E01000046020008770000000000004C4F4E472653484F52544F46495420202E7F00190018003122421616493741005B1D21001C74190000170007000A010A0A010201003D404040000000007F000010010D0900000000420001027E6E260000360E00000004000D017F000F00020E01300000000000000076400212000400367E013400460230087700000000000052494F4E45524F275320534F4E2753202E00111C001831195D453635182C420000261A007C001C7F001700062D00000B0301020000414116007F000F002700001001060E0000000100000102684E5400006E347F00020300140800000F00000E01300000000000000076400212000400537E01000046020008770000000000005341464520262053414E4520202020202E502119001830184E700625533B39003E2D250000772301000900040900000D06010200004140002B4200002C4300001001090E00000001000001027E5A4E0001411A0000010300120700000E00000F0130000100000000444D02000001000019200400000000002F4400000000000045524954275320484F4D4520202020207E7F0018001919007772262D3F432F004E1122001D51132F001700000105000105000200003C470034352000000025200809010200000001420000027E7E3A0000414041000604001D0C31000F00020E00300000000000000076400212000400537E010000460200087700000000000046555A5A2D46414345442020202020202E7F081C0018301915172C2855292300690013007B091800011700010300060A0401020000424100010C0000780000000001090B00000001010001027E1400000001010100020400110B00000D00000601300000000000000076400212000400537E0100004602000877000000000000564F43414C49545920202020202020202E4D3517000034000075351D00570B0048001F007F00204E6B5F00001400000A0C000200004040005F5A25005B5C240048000C0400000001420001017E00003400000101000002001D0A00000500020700300000000000000076400212000400537E01000046020008770000000000005345525649434520544F2048494D20202E693118000000142D2C3B2B583B25002000190078001D00001700000402150B0B0102000040402A3D0000000000000060010C0300000001420001017E2C380000400E0000080400110800000600020700280000000000000076400212000400537E01000046020008770000000000004741494E205245434F564552592020202E3D002000192D1867595134414B230034001506007F1631000300000702190D0B00020000404100010000000000000010000A0C00000001020000027E7E7000004D010100000400140800000A00000E002B0000000000000076400212000400537E01000046020008770000000000004A4F484E20424C41434B204A522E20202E621718000000125B321231483E180034002D0674061A00001700050001070A0201020500403C00017F0000000000000001020C00000000400001017E3E2600004D0100000204000C0E00000000000024300000000000000076400212000400537E01000046020008770000000000005052494F5259204F46205A2E203C20302E62001600192D18675951364A4B23002A001106007F1531000300000002190D0A00020000404100010000000000000018000E0C000000014000010272726626004D1A0000000400140800000A00000E00300000000000000076400212000400537E0100004602000877000000000000544845205245564552454E44202020202E53371B0019181E234C093D442C280034000D017404100000170004170200090201020400404000000000000000000060000B0C00000001400001027E7E1200002D2000000601000F0906000F00000E012A0000000000000076400212000400537E0100004602000877000000000000464F554E444154494F4E2020202020202E7F381F001948303A4443301C7F7F01197F43003F580B3D341700021C05070D0501020000404000340000000000000028030B0D0000000102000102402044000001014E00060400100800000400000C01300000000000000076400212000400537E0100004602000877000000000000274E20534F554C20494E20542E20482E2E43321A0000190000692E2E45005300392F1C0040471D7F013800010801170E0601020418403E0701753000000000300800090C0000000102000101744B770A006E147F01000100090801000600020100343300000000000076330210000400007E010032460200087700000000000054484552494F542042524F53272010202E7F131B0019181925351835312B170000000B017D190804001700012005030A0501020400404000360000000000000008000B0900000001020001027E7E34000000201A00060100140906000F00000E01300000000000000076400212000400537E0100004602000877000000000000424153532053544F50532048455245202E0D111900001231576565264E392A52407F1B0003491E0E0E1701011101190A0D0002000041410C3500000F3A001B0010010B0200000001000101017074480000602D6D000809000A0800000F00000E01300000000000000076400212000400537E01000046020008770000000000005055544E414D2050524F424C454D20202E6D321C001971007152002D315940000000000071142056001700000D0101090C010200003B4100010000000000000038010C0300000001000001017A406C42006E540100000400120800000F00000E01300000000000000076400212000400537E01000046020008770000000000005741524D2026204E49434520202020202E5830180000190000002233273D130A48072A00002B1F00007400050200110903000200003C42032C6D2900236D00004800090800000001000001017E466C1400000100000004000B0800000B00000C01350000000000000076400212000400537E0100004602000877000000000000555020524947485420202020202020202E7F301D0031603541323C3E562E03154C4C441B315F1800001700000801190901010200003F400034350000007F00004801090C00000001400001017F5A220000010101000604000B0200000000000024300000000000000076400212000400537E0100004602000877000000000000424C5545204E4F544520434F4F4C20202E2F0111003132312A773A474F424E163A67202D6F051724001700000A0815040A000200003F4300580000000000000020010A090000000040010101364A3400007E7C7E6C060400140F00000E00000301300000000000000076400212000400537E0100004602000877000000000000564152494F55532046554E4B20562E312E58081D003130303F327556512A290A4E7F441D4D7F1800001700000809050D01010200003F400034350000007F00000001090A0000000100000101333C10000001010100060400070200000000000024300000000000000076400212000400537E01000046020008770000000000004C494E444127532057494E47532020202E330522001819195D453649542C7F000063000040580000001701050A05020903010200004143160044000F00270000080204010000000140000001724C6C00007F410100010300141000000F00000E01300000000000000076400212000400537E010000460200087700000000000046554E43204C4541442F53494E4520202E420521001830195D451A2B602C7F000063000040580000001701020A087F0C04010200004143160044000F0027000020030B0E00000001000001016E486800007F4101000103001A0700000F00000E01300000000000000076400212000400537E010000460200087700000000000046554E43204C4541442F50554C5345202E330523001819195D453617602C7F000063000040580000001701080200020A02010200004143160044000F002700001001030200000001000000016E486800007F4101000103001A0800000F00000E01300000000000000076400212000400537E010000460200087700000000000046554E43204C4541442F5341572020202E7F0316001930191B6F7F28003D5200740022005300220000170006001D000602010200003E4000010000000000002010020A0D00000001000001017E5C5A38007E7C006C000400070800000F00000E012A0000000000000076400212000400537E01000046020008770000000000004241434B20494E2054484520444159202E7F0125003131007F7F2A1A26647F003E2E000048080000005F017D007F000C040002000040403D7F7D767D792F3F3800000405000000000100000134352B0001414061610001087F002102017F000E7F305F79766F773B7E497F7E7F7C7F7F637F5E7A7F3B7E7F7F4F2B7A6A373F2A53414E464F5244495A454420202020207544071A0018481A6E192440552C40387F69110F263F013A001700020A1602040401020000433D006F00000F0027000050010B0200000001000101017E1800000040007F000203101E0007000F00020E01300000000000000076400212000400537E0100004602000877000000000000534841444553204F46204D59204436202E6F1D1A001830182F4571354F425830277F180A72001100000E00050C0935020601020000404220087F220000001F005001060B00000001000101006A285E1C007E7E006C000400080800000F00000E012C0000000000000076400212000400537E0100004602000877000000000000534552494F55532044594E414D4943532E212F230031313154003E453527131B554B17264A581900004F0000020000090C01020000674100010E0000000000000800040E000000010000010068081C0000002001000504000E0800000000000001300000000000000076400212000400537E010000460200087700000000000044522E20574F524D20202020202020202E542A1D0048484824113E2542382A1463704A3C363D717F0017000008147F0902010200004240001E260000000000004801090D0000000102000101481C7E000001010000020409600A5A000F00000E01300000000000000076400212000400537E01000046020008770000000000005649425241434849205245532E2020202E432C19050000007F7F7F39003D0E00000E002228521E6A001700090113020707000200004538003435000000000040380102030000000100000100545454210041014E000604097E0800000F00020E01300000000000000076400212000400537E01000046020008770000000000005245534F4E414E5420455850455254202E51351A003131312F71754F42301D1F487E241A36651A1A001700090000000904000200003F400034350000000000000800090E0000000100010100612022000041014E00060400090200000000000024300000000000000076400212000400537E0100004602000877000000000000455448455245414C204D414E202020202E44351A003131182F71754C2F43000C3E281A17436F151A001700090000000901010200003F400034350000000000000000090E00000001020001006D2A5C000041014E00060400090200000000000024300000000000000076400212000400537E01000046020008770000000000002E2E2E574F4D414E20202020202020202E7F301D0048600641323C3E562E03154C4C441B315F1800001700000801190901010200003F400034350000007F00004800090C00000001400001017F5A320000010101000604000B0200000000000024300000000000000076400212000400537E01000046020008770000000000002E2E2E4348494C4420202020202020202E44001D003131184142752542432F0C41281800362C1C1A0017000008000009010002000043400034350000000000000800090E00000001000001003B705C000041014E000604000B0200000000000024300000000000000076400212000400537E010000460200087700000000000046414E544153544943203730275320202E42060E02483F533650431A0E346B0A3F37231261170F3900170115150A150901000505004040097F000000000000000800060A00000001420000013A3E3224001F717F53060408480614000F00000E00300000000000000076400212000400537E010000460200087700000000000050524F46554E44495320494D504F52542E7F0032006019192073333B2555530027000000003208000017010B4806260D02000300003C4400007F00000000000018020A0D00000001000000025A3A4000007F140100010408290200000F00000E01300000000000000076400212000400537E01000046020008770000000000003420552032204B4E4F572020202020202E202B0E0248716529373E40604E00004E28534236367F0000170015320A15060B00001800404000017F0000000000001800060A00000001000001026A7E7E1600407C015305040C7B0851000F00000E00300000000000000076400212000400537E01000046020008770000000000004D522E2048414D45525449413C474B3E2E7F3218010029007F217A39472E3D102F7F2419194029012D170009190803050900020000443D0007000000007F000010020A0300000001400000007A7A0400006D6100610102002E0801000F00000E00300000000000000076400212000400537E0100004602000877000000000000224D592042524F5448414822542F41202E6805180018181B241B3D42342A000019001417752027057F4000030502080D01010200003D4500007F00007F7F000060010C090000000142000100486470001041777F7F0308000B0700000F00000E01300000000076000000400212000400537E010000460200087700000000000048495320594F4B4520495320454153592E36001A001931294142433136431A0C20281420362C167F7F170002080A0B09010002000042403E3E3500007F7F00006000090D00000001017E0100674A220000412B7F7F0902000D0800000F00000E01300000000000000076400212000400537E0100004602000877000000000000262048495342555244454E4953204C542E5A0E1600191B193C2C35262C436716093B53136C461A32001700010F0206050901020000434035513D00001B001B0028010C0900000001000101005A5A2E000028797F000106000C0800000F00000E01300000000000000076400212000400537E0100004602000877000000000000444545502054484F55474854532020202E2209180049494C4A277107002F771A726F291A31644F00000E00000802680105010200003F420001000000000000004801090B00000001000001016430721800137E006C0004000A0800000F00000E01330000000000000076400212000400537E0100004602000877000000000000524F4F4D2057495448204120564945572E540F1B0549483161393E4C3D3E00000000142B29581F0000170002050011010D00020000403E00010E00000000000008050A0800000001420001017E3E0C000001010100050400410514000F00000E01290000000000000076400212000400537E010000460200087700000000000043414D494C4C452753204441202020202E5D012200494D4100004A5915307F003B5400002900000001170103020709090D000200007F35003E310000090E00006001090D0000000142000101362A1A000A4101507F000100140800000F00000E012D0000000000000076400212000400537E0100004602000877000000000000424C41434B57454C4C275320545544452E3C261E003118183F5C2F555021661F4C1D00013B5A1B000017000411013C0A0401020000404100000000000000000008000B0600000001420001013C524A0000010100000104000E0600000F00000E00300000000000000076400212000400537E0100004602000877000000000000524F434B464F5244205649414C5320202E7F001900301A377D3049264A7E19004600240700613B0040170014140803040C010205003E4100010000000000000060010D0B000000010001010066366C00003F680000000500070800000F00000E01300000000000000076400212000400537E010000460200087700000000000050554E4354554154494F4E20552E4B2E2E4D00150019192A71253E3F24551C054D001035643F12000017000E190A020101000205003E4000017F00535800000060020C0A00000001400000007C64460000604058000004000C0C00000000000024300000000000000076400212000400537E0100004602000877000000000000534F554E4420534541534F4E414C20202E612F1D00313F48414275254243110C4E281A0036622F1A0017000008141301010002000041400034350000000000205802090D01000001000001004D7E24000041014E000604000B02000000000000243A0000000000000076400212000400537E01000046020008770000000000004F5249454E54454420202020202020202E60231E003130305D45362F00216E0C1F741C00267F1E0000170000000000020200020000463F160044000F0027000010000E0E0000000100000100600C0A00102D790100010300070E00000F00000D00290000000000000076400212000400537E0100004602000877000000000000484F524E204F4620504C454E545920202E10251C003100195D453643002C65387F691700267F11000017000A0E0002020200020000433F160044000F0027000010010D0900000001423701016C2A28000041750100010300081000000F00000E01300000000000000076400212000400537E010000460200087700000000000053544556494520484F524E20202020202E48021A001930294142432550432F0C4B54511B3630221A7F1700020814490109000200004540003435000C007E00005001090D0000000102000100434A100000413B4E7F0602000B0800000F00000E01300000000000000076400212000400537E01000046020008770000000000004D495353494F4E202E2E2E20202020202E7F041900315030771069521F0043005C0021006B00390F00170112000000040C000201004044003C00200000002D0020000C0E000000010100000244241E0000007C00000104000D0A00000F00000E00300000000000000076400212000400537E01000046020008770000000000005245454420414C4552542120202020202E7F0019003131372F0D314C3142221A3C420001424A0038001700013C04040404000200003D4200540000005400004060030B0C00000000000001027E22580000417C7F00030400430800000F00000E01300000000028000076400212000400537E0100004602000877000000000000524545444F4E204741532120202020202E48001A001961294142432536432F0C4E45510036302A1A7F170002080A0B0909000200004340003435000C007E00006001090D00000001400000005E30280000412B4E7F0602000C0800000F00000E01300000000000000076400212000400537E010000460200087700000000000057452052454D454D424552205A2E20202E7F00190019607841423E3300604800332725007000232120170105191201010900020000404000220014001F0010400801080300000000400001016A00020000010100000200000E0600000E00000D01300000000000000076400212000400537E0100004602000877000000000000454C454354524F2056494245202020202E0000320019006161423E43354430004501330020082E26671701000000000103000200003D4000000000000000004010000E0E0000000042000000766E1018007F47147F05040C450800000F00000E01300000000000000076400212000400537E010000460200087700000000000054484520534F554E4420495320414C4C2E7F0112003139652F69714430421A1A3C420A00121D000000170000290B24040900020000443E00010000000000000050020A0D00000000000000007E7E7E7E00417C0000000400340800000F00000E01300000000000000076400212000400537E01000008770046020000000000004F4E20324E442054484F5547485420202E4D00230019780041423E4D003B003F3C42000016007F4E7F1701537F00000B0400020000404000352800390000000008000B0E0000000042000001766A7E00004141006C060808390C00090D7F000E01300000000000000076400212000400537E0100004602000877000000000000484F4F4B4544204F4E20534F4E4943532E4D001A1D4650482F22335A003600005A007F0418007F007F17010B4003220707000700004143000100000E33007540380D0A0C00000000400001017E7E7E0000342F7A7F00020C6E0800000F00000E01300000000000000076400212000400537E0100004602000877000000000000544845204D414A4553544943203132202E7F0F14003260536D58217B002B44000039500618324B00001701030E0D020101000200004040000100000000000000080206030000000002000002707068000070010000000400070700000F00000E00300000000000000076400212000400537E010000460200087700000000000043415357454C4C5A2047454E495553202E4D001D001938194142383A144100003C4200003B4100557F17011D0014130B0200020000404000352800043E2B000020000E0C0000000042000101767E0400004120007F060200140C00000F00000E01300000000000000076400212000400537E0100004602000877000000000000454E474C4953482047415244454E20202E3E28140000000041722F2451437F78792275007F007F1A7F1700001100090F07000C000035490034350000007F000038090B0C00000001420000016E6E6E000000543A7F06020D740800000F00000E01300000000000000076400212000400537E01000046020008770000000000004D494E442053574545504552202020202E56760A053139345D4536693C0018760B6911383A4A7F00001700013502220404001800004642002B007F0F00277F002000080D00000001000000015E5E5E0000747A0000050309460800000F00000E01300000000000000076400212000400537E01000046020008770000000000004F55544552204E494D49545A202020202E7F001A0119191957515D575F434300710174007B007F000E1701011601050B0600020000394C0C3500000F3A00000018000B09000000010100000260603400004101000008090907107D000B00000F00300000000000000076400212000400537E010000460200087700000000000043414E594F4E2753204F20535445454C2E4729187F1829341A7F0C252F237F4B7F154F7F5B277F7F0017000247067F0405000700003D42007F7E003000000000200A080C05000501420001014C4C04000054617F000204041E0A00000500000700300000000000000076400212000400537E01000046020008770000000000004F4E434520414741494E2020202020202E3A320B7F24321A7F450242470042002E1B5F000070000043170111000400010000021800403E00302D000F4B6066004803090D000500010100010056545600001A41010005030E4D0800000000000001300000000000000076400212000400537E010000460200087700000000000053554354494F4E2050495045202020202E472C100619271D580C7F4E0D4300004E0A00196F0A001A00170000000000010100020000403800343500000000000008000E0E00000001000000005E5A56010041014E0006040C270800000F00000E01300000000000000076400212000400537E010000460200087700000000000053494E45204F4E20544845202E2E2E2E2EF7";

	public static String[] createProgrammNumbers() {
		String[] retarr = new String[PROGRAM_COUNT_IN_BANK];
		String[] names = DriverUtil.generateNumbers(1, PROGRAM_COUNT_IN_BANK, "Patch #00");
		System.arraycopy(names, 0, retarr, 0, PROGRAM_COUNT_IN_BANK);
		return retarr;
	}

	public static byte[] getDefaultSinglePatch() {
		byte sysex[] = new byte[PROGRAM_SIZE_SYSEX];

		System.arraycopy(Se1.DEFAULT_PATCH_HEADER, 0, sysex, 0, Se1.HEADER_SIZE);

		System.arraycopy(HexaUtil.convertStringToSyex(DEFAULT_PROGRAM_STRING), 0, sysex, Se1.HEADER_SIZE, Se1.PROGRAM_SIZE);
		sysex[PROGRAM_SIZE_SYSEX - 1] = (byte) 0xF7;
		return sysex;
	}

	public static byte[] getDefautltBankPatch() {
		return HexaUtil.convertStringToSyex(DEFAULT_BANK_STRING);
	}

}