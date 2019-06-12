package com.billy.bbk.arpg.compiler.gut;

/**
 * @author lei.liu
 * @since 2019-06-12 13:55
 */
public final class GutCommandResource {
    private GutCommandResource() {}

    static final String GUTEVENT = "GUTEVENT";

    /**
     * 命令文本
     */
    public static final String[] COMMAND_TEXT = {
            "Music",
            "LoadMap",
            "CreateActor",
            "DeleteNpc",
            "MapEvent",
            "ActorEvent",
            "Move",
            "ActorMove",
            "ActorSpeed",
            "Callback",
            "Goto",
            "If",
            "Set",
            "Say",
            "StartChapter",
            "ScreenR",
            "ScreenS",
            "ScreenA",
            "Event",
            "Money",
            "Gameover",
            "IfCmp",
            "Add",
            "Sub",
            "SetControlId",
            "GutEvent",
            "SetEvent",
            "ClrEvent",
            "Buy",
            "FaceToFace",
            "Movie",
            "Choice",
            "CreateBox",
            "DeleteBox",
            "GainGoods",
            "InitFight",
            "FightEnable",
            "FightDisenable",
            "CreateNpc",
            "EnterFight",
            "DeleteActor",
            "GainMoney",
            "UseMoney",
            "SetMoney",
            "LearnMagic",
            "Sale",
            "NpcMoveMod",
            "Message",
            "DeleteGoods",
            "ResumeActorHp",
            "ActorLayerUp",
            "BoxOpen",
            "DelAllNpc",
            "NpcStep",
            "SetSceneName",
            "ShowSceneName",
            "ShowScreen",
            "UseGoods",
            "AttribTest",
            "AttribSet",
            "AttribAdd",
            "ShowGut",
            "UseGoodsNum",
            "Randrade",
            "Menu",
            "TestMoney",
            "CallChapter",
            "DisCmp",
            "Return",
            "TimeMsg",
            "DisableSave",
            "EnableSave",
            "GameSave",
            "SetEventTimer",
            "EnableShowPos",
            "DisableShowPos",
            "SetTo",
            "TestGoodsNum"
    };

    /**
     * 命令参数
     */
    public static final String[] COMMAND_PARAM = {
            "NN",
            "NNNN",
            "NNN",
            "N",
            "NNNNNN",
            "NA",
            "NNN",
            "NNNNNN",
            "NN",
            "",
            "A",
            "NA",
            "NN",
            "NC",
            "NN",
            "N",
            "NN",
            "N",
            "NA",
            "N",
            "",
            "NNA",
            "NN",
            "NN",
            "N",
            "NE",
            "N",
            "N",
            "U",
            "NN",
            "NNNNN",
            "CCA",
            "NNNN",
            "N",
            "NN",
            "NNNNNNNNNNN",
            "",
            "",
            "NNNN",
            "NNNNNNNNNNNNNAA",
            "N",
            "L",
            "L",
            "L",
            "NNN",
            "",
            "NN",
            "C",
            "NNA",
            "NN",
            "NN",
            "N",
            "",
            "NNN",
            "C",
            "",
            "",
            "NNA",
            "NNNAA",
            "NNN",
            "NNN",
            "NNC",
            "NNNA",
            "NA",
            "NC",
            "LA",
            "NN",
            "NNAA",
            "",
            "NC",
            "",
            "",
            "",
            "NN",
            "",
            "",
            "NN",
            "NNNAA"
    };

 /* 以下为标签定义, 和代码无关

 @ A 直接地址
 @ C 字符串
 @ E 间接地址
 @ L 4字节数
 @ N 2字节数
 @ U 多个N

 0x00 Music NN
 0x01 LoadMap NNNN
 0x02 CreateActor NNN
 0x03 DeleteNpc N
 0x04 MapEvent NNNNNN T
 0x05 ActorEvent NA T
 0x06 Move  NNN
 0x07 ActorMove NNNNNN T
 0x08 ActorSpeed NN T
 0x09 Callback
 0x0A Goto A
 0x0B If NA
 0x0C Set NN
 0x0D Say NC
 0x0E StartChapter NN
 0x0F ScreenR N T
 0x10 ScreenS NN
 0x11 ScreenA N T
 0x12 Event NA T
 0x13 Money N T
 0x14 Gameover
 0x15 IfCmp NNA
 0x16 Add NN
 0x17 Sub NN
 0x18 SetControlId N
 0x19 GutEvent NE
 0x1A SetEvent N
 0x1B ClrEvent N
 0x1C Buy U
 0x1D FaceToFace NN
 0x1E Movie NNNNN
 0x1F Choice CCA
 0x20 CreateBox NNNN
 0x21 DeleteBox N
 0x22 GainGoods NN
 0x23 InitFight NNNNNNNNNNN
 0x24 FightEnable
 0x25 FightDisenable
 0x26 CreateNpc NNNN
 0x27 EnterFight NNNNNNNNNNNNNAA
 0x28 DeleteActor N
 0x29 GainMoney L
 0x2A UseMoney L
 0x2B SetMoney L
 0x2C LearnMagic NNN
 0x2D Sale
 0x2E NpcMoveMod NN
 0x2F Message C
 0x30 DeleteGoods NNA
 0x31 ResumeActorHp NN
 0x32 ActorLayerUp NN
 0x33 BoxOpen N
 0x34 DelAllNpc
 0x35 NpcStep NNN
 0x36 SetSceneName C
 0x37 ShowSceneName
 0x38 ShowScreen
 0x39 UseGoods NNA
 0x3A AttribTest NNNAA
 0x3B AttribSet NNN
 0x3C AttribAdd NNN
 0x3D ShowGut NNC
 0x3E UseGoodsNum NNNA
 0x3F Randrade NA
 0x40 Menu NC
 0x41 TestMoney LA
 0x42 CallChapter NN
 0x43 DisCmp NNAA
 0x44 Return
 0x45 TimeMsg NC
 0x46 DisableSave
 0x47 EnableSave
 0x48 GameSave
 0x49 SetEventTimer NN
 0x4A EnableShowPos
 0x4B DisableShowPos
 0x4C SetTo NN
 0x4D TestGoodsNum NNNAA
 */
}
