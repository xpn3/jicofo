package org.jitsi.jicofo.schisming;

import net.java.sip.communicator.service.protocol.ChatRoom;
import org.jetbrains.annotations.NotNull;
import org.jitsi.impl.protocol.xmpp.XmppProtocolProvider;
import org.jitsi.jicofo.Participant;
import org.jitsi.protocol.xmpp.XmppChatMember;
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
    public void register(Participant participant) throws ParticipantAlreadyRegisteredException, SchismingGroupLimitReachedException, SmackException.NotConnectedException, InterruptedException {
        if(participant == null) {
            throw new InvalidParameterException("Participant cannot be null.");
        }
        if(getSchismingGroup(participant) != null) {
            throw new ParticipantAlreadyRegisteredException(
                    "Unable to register Participant " + participant.toString() + ". Already registered.");
        }

        addParticipantToNewGroup(participant);

        sendState(participant);
    }

    @Override
    public void deregister(Participant participant) throws InvalidParameterException, SmackException.NotConnectedException, InterruptedException {
        if(participant == null) {
            throw new InvalidParameterException("Participant cannot be null.");
        }

        SchismingGroup group = getSchismingGroup(participant);
        logger.info("Deregistered participant " + participant.toString() + " from group " + group.getId());
        removeParticipantFromGroup(participant, group);

        sendState(participant);
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
    public void joinGroup(Participant participant, Integer groupId) throws SchismingGroupLimitReachedException, InvalidParameterException, SmackException.NotConnectedException, InterruptedException {
        if(participant == null) {
            throw new InvalidParameterException("Participant may not be null.");
        }
        SchismingGroup groupToJoin = null;
        for(SchismingGroup group : schismingGroups) {
            if(group.getId().equals(groupId)) {
                groupToJoin = group;
            }
        }

        SchismingGroup currentGroup = getSchismingGroup(participant);
        if(currentGroup.getId().equals(groupId)) {
            return;
        }

        if(groupToJoin == null) {
            addParticipantToNewGroup(participant);
        } else {
            groupToJoin.add(participant);
        }
        removeParticipantFromGroup(participant, currentGroup);

        sendState(participant);
    }

    private void addParticipantToNewGroup(Participant participant) throws SchismingGroupLimitReachedException {
        SchismingGroup newGroup = new SchismingGroup(createGroupId());
        newGroup.add(participant);
        schismingGroups.add(newGroup);
        logger.info("Registered participant " + participant.toString() + " in group " + newGroup.getId());
    }

    private void removeParticipantFromGroup(Participant participant, SchismingGroup group) {
        group.remove(participant);
        if(group.getNumberOfParticipants() == 0) {
            schismingGroups.remove(group);
            logger.info("Removed group " + group.getId() + " from SchismingHub");
        }
    }

    public void sendState(Participant participant) throws SmackException.NotConnectedException, InterruptedException {
        XmppChatMember chatMember = participant.getChatMember();
        ChatRoom chatRoom = chatMember.getChatRoom();
        XmppProtocolProvider xmppProtocolProvider = (XmppProtocolProvider) chatRoom.getParentProvider();
        XMPPConnection connection = xmppProtocolProvider.getConnection();

        if (connection == null) {
            logger.error("Failed to send state of SchismingHub - no XMPPConnection");
            return;
        }

        sendStanza(connection);
    }

    private void sendStanza(XMPPConnection connection) throws SmackException.NotConnectedException, InterruptedException {
        if(connection == null) {
            throw new InvalidParameterException("Connection cannot be null.");
        }
        for(SchismingGroup group : schismingGroups) {
            for(Participant participant : group.getParticipants()) {
                SchismingStateIq state = new SchismingStateIq();
                state.setTo(participant.getMucJid());
                state.setType(IQ.Type.set);
                state.setHub(this);
                logger.info("Sending SchismingHub state: " + state.toXML().toString());
                connection.sendStanza(state);
            }
        }
    }
}
