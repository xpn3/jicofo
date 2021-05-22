package org.jitsi.jicofo.schisming;

import org.jetbrains.annotations.NotNull;
import org.jitsi.jicofo.Participant;
import org.jitsi.protocol.xmpp.XmppConnection;
import org.jitsi.utils.logging2.Logger;
import org.jitsi.utils.logging2.LoggerImpl;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.id.StanzaIdUtil;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class SchismingHubImpl implements SchismingHub {
    @NotNull
    private final Logger logger;
    private final List<SchismingGroup> schismingGroups;

    public SchismingHubImpl() {
        logger = new LoggerImpl(SchismingHubImpl.class.getName(), Level.INFO);
        schismingGroups = new ArrayList<>();
    }

    @Override
    public void register(Participant participant) throws ParticipantAlreadyRegisteredException {
        if(participant == null) {
            throw new InvalidParameterException("Participant cannot be null.");
        }
        logger.info("register participant: " + participant.toString());
        if(getSchismingGroup(participant) != null) {
            throw new ParticipantAlreadyRegisteredException(
                    "Unable to register Participant " + participant.toString() + ". Already registered.");
        }
        schismingGroups.add(new SchismingGroup(participant));
    }

    @Override
    public SchismingGroup getSchismingGroup(Participant participant) {
        if(participant == null) {
            return null;
        }
        for(SchismingGroup group : schismingGroups) {
            if(group.getParticipants().contains(participant)) {
                return group;
            }
        }
        return null;
    }

    @Override
    public void sendState(XMPPConnection connection) throws SmackException.NotConnectedException, InterruptedException {
        if(connection == null) {
            throw new InvalidParameterException("Connection cannot be null.");
        }
        SchismingIq state = new SchismingIq();
        state.setStanzaId(StanzaIdUtil.newStanzaId());
        state.setType(IQ.Type.set);
        // TODO add state to iq
        logger.info("Sending SchismingHub state: " + state.toXML());
        connection.sendStanza(state);
    }
}
