package org.jitsi.jicofo.schisming;

import org.jitsi.jicofo.Participant;

public interface SchismingHub {
    void register(Participant participant) throws ParticipantAlreadyRegisteredException;
    SchismingGroup getSchismingGroup(Participant participant);
}
