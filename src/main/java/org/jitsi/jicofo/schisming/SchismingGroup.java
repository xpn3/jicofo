package org.jitsi.jicofo.schisming;

import org.jitsi.jicofo.Participant;

import java.util.Arrays;
import java.util.List;

public class SchismingGroup {
    private final Integer id;
    private Participant participant;

    public SchismingGroup(Integer id, Participant participant) {
        this.participant = participant;
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public List<Participant> getParticipants() {
        return Arrays.asList(participant);
    }
}
