package org.jitsi.jicofo.schisming;

import org.jetbrains.annotations.NotNull;
import org.jitsi.jicofo.JitsiMeetConference;
import org.jitsi.jicofo.Participant;
import org.jitsi.protocol.xmpp.XmppChatMember;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class SchismingHubImplTest {
    private SchismingHub sut;

    @Before
    public void setup() {
        sut = new SchismingHubImpl();
    }

    @Test
    public void register() throws ParticipantAlreadyRegisteredException {
        Participant participant = createParticipant();
        //ACT
        sut.register(participant);
        //ASSERT
        assertSchismingGroup(participant);
    }

    @Test
    public void register_twoParticipants() throws ParticipantAlreadyRegisteredException {
        Participant participant1 = createParticipant();
        Participant participant2 = createParticipant();
        //ACT
        sut.register(participant1);
        sut.register(participant2);
        //ASSERT
        assertSchismingGroup(participant1);
        assertSchismingGroup(participant2);
    }

    @Test(expected = InvalidParameterException.class)
    public void register_participantIsNull_throws() throws ParticipantAlreadyRegisteredException {
        //ACT
        sut.register(null);
    }

    @Test
    public void register_sameParticipantTwice_throws() throws ParticipantAlreadyRegisteredException {
        Participant participant = createParticipant();
        sut.register(participant);
        //ACT
        ThrowingRunnable act = () -> {
            sut.register(participant);
        };
        //ASSERT
        Exception exception = assertThrows(ParticipantAlreadyRegisteredException.class, act);
        assertTrue(exception.getMessage().contains("Unable to register Participant"));
    }

    @NotNull
    private Participant createParticipant() {
        return new Participant(mock(JitsiMeetConference.class), mock(XmppChatMember.class), 10);
    }

    private void assertSchismingGroup(Participant participant) {
        SchismingGroup result = sut.getSchismingGroup(participant);
        assertNotNull(result);
        List<Participant> participants = result.getParticipants();
        assertTrue(participants.contains(participant));
    }
}
