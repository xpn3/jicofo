package org.jitsi.jicofo.schisming;

import org.jetbrains.annotations.NotNull;
import org.jitsi.jicofo.Participant;
import org.jitsi.utils.logging2.Logger;
import org.jitsi.utils.logging2.LoggerImpl;

import java.util.logging.Level;

public class SchismingHubImpl implements SchismingHub {
    @NotNull
    private final Logger logger;
    private SchismingGroup schismingGroup;

    public SchismingHubImpl() {
        logger = new LoggerImpl(SchismingHubImpl.class.getName(), Level.INFO);
    }

    @Override
    public void register(Participant participant) {
        if(participant == null) {
            return;
        }
        logger.info("register participant: " + participant.toString());
        schismingGroup = new SchismingGroup(participant);
    }

    @Override
    public SchismingGroup getSchismingGroup(Participant participant) {
        return schismingGroup;
    }
}
