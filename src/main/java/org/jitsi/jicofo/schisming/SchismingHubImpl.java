package org.jitsi.jicofo.schisming;

import org.jetbrains.annotations.NotNull;
import org.jitsi.jicofo.Participant;
import org.jitsi.utils.logging2.Logger;
import org.jitsi.utils.logging2.LoggerImpl;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.IQ;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class SchismingHubImpl implements SchismingHub {
    @NotNull
    private final Logger logger;
    private final List<SchismingGroup> schismingGroups;
    private Integer latestGroupId;

    public SchismingHubImpl() {
        logger = new LoggerImpl(SchismingHubImpl.class.getName(), Level.INFO);
        schismingGroups = new ArrayList<>();
        latestGroupId = 0;
    }

    private Integer createGroupId() throws SchismingGroupLimitReachedException {
        if(latestGroupId == Integer.MAX_VALUE) {
            throw new SchismingGroupLimitReachedException(
                    "Unable to create new SchismingGroup. Reached maximum number of SchismingGroups.");
        }
        return ++latestGroupId;
    }

    @Override
    public List<SchismingGroup> getSchismingGroups() {
        return new ArrayList<>(schismingGroups);
    }

    @Override
    public void register(Participant participant) throws ParticipantAlreadyRegisteredException, SchismingGroupLimitReachedException {
        if(participant == null) {
            throw new InvalidParameterException("Participant cannot be null.");
        }
        logger.info("register participant: " + participant.toString());
        if(getSchismingGroup(participant) != null) {
            throw new ParticipantAlreadyRegisteredException(
                    "Unable to register Participant " + participant.toString() + ". Already registered.");
        }
        SchismingGroup newGroup = new SchismingGroup(createGroupId());
        newGroup.add(participant);
        schismingGroups.add(newGroup);
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
        for(SchismingGroup group : schismingGroups) {
            for(Participant participant : group.getParticipants()) {
                SchismingIq state = new SchismingIq();
                state.setTo(participant.getMucJid());
                state.setType(IQ.Type.set);
                state.setHub(this);
                logger.info("Sending SchismingHub state: " + state.toXML().toString());
                connection.sendStanza(state);
            }
        }
    }
}
