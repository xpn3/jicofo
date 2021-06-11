package org.jitsi.jicofo.schisming;

import org.jitsi.jicofo.Participant;

import java.util.*;

public class SchismingGroup {
    private final Integer id;
    private final Set<Participant> participants;

    public SchismingGroup(Integer id) {
        this.participants = new HashSet<>();
        this.id = id;
    }

    public void add(Participant participant) {
        if(participant == null) {
            return;
        }
        participants.add(participant);
    }

    public Integer getId() {
        return id;
    }

    public Set<Participant> getParticipants() {
        return new HashSet<>(participants);
    }
}
