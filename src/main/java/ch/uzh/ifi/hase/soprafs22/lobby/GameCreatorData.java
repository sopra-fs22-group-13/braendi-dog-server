package ch.uzh.ifi.hase.soprafs22.lobby;

import ch.uzh.ifi.hase.soprafs22.voicechat.entities.VoiceRoom;

public class GameCreatorData {
    public VoiceRoom vr;
    public String gameToken;

    public GameCreatorData(VoiceRoom vr, String gt)
    {
        this.vr = vr;
        this.gameToken = gt;
    }
}
