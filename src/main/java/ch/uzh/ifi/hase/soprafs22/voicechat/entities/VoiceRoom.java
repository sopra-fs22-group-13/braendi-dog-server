package ch.uzh.ifi.hase.soprafs22.voicechat.entities;

/**
 * Information about the voiceRoom including its players.
 */
public class VoiceRoom {
    public String roomId;
    public String appId;

    public VoiceUser player1;
    public VoiceUser player2;
    public VoiceUser player3;
    public VoiceUser player4;

}
