package org.jitsi.jicofo.schisming;

import org.jitsi.jicofo.Participant;

import java.util.Arrays;
import java.util.List;

public class SchismingGroup {
    private Participant participant;

    public SchismingGroup(Participant participant) {
        this.participant = participant;
    }

    public List<Participant> getParticipants() {
        return Arrays.asList(participant);
    }
}
