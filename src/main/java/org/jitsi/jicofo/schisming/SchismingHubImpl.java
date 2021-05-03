package org.jitsi.jicofo.schisming;

import org.jetbrains.annotations.NotNull;
import org.jitsi.jicofo.Participant;
import org.jitsi.utils.logging2.Logger;
import org.jitsi.utils.logging2.LoggerImpl;

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
}
