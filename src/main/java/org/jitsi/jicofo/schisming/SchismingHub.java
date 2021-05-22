package org.jitsi.jicofo.schisming;

import org.jitsi.jicofo.Participant;
import org.jitsi.protocol.xmpp.XmppConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;

public interface SchismingHub {
    void register(Participant participant) throws ParticipantAlreadyRegisteredException;
    SchismingGroup getSchismingGroup(Participant participant);
    void sendState(XMPPConnection connection) throws SmackException.NotConnectedException, InterruptedException;
}
