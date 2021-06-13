package org.jitsi.jicofo.schisming;

import org.jetbrains.annotations.NotNull;
import org.jitsi.jicofo.JitsiMeetConference;
import org.jitsi.jicofo.Participant;
import org.jitsi.protocol.xmpp.XmppChatMember;
import org.junit.Before;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Set;

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
    public void register() throws ParticipantAlreadyRegisteredException, SchismingGroupLimitReachedException {
        Participant participant = createParticipant();
        //ACT
        sut.register(participant);
        //ASSERT
        assertSchismingGroup(participant);
    }

    @Test
    public void register_twoParticipants() throws ParticipantAlreadyRegisteredException, SchismingGroupLimitReachedException {
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
    public void register_participantIsNull_throws() throws ParticipantAlreadyRegisteredException, SchismingGroupLimitReachedException {
        //ACT
        sut.register(null);
    }

    @Test
    public void register_sameParticipantTwice_throws() throws ParticipantAlreadyRegisteredException, SchismingGroupLimitReachedException {
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

    @Test
    public void deregister() throws ParticipantAlreadyRegisteredException, SchismingGroupLimitReachedException {
        Participant participant = createParticipant();
        sut.register(participant);
        assertSchismingGroup(participant);
        //ACT
        sut.deregister(participant);
        //ASSERT
        assertNoSchismingGroup(participant);
    }

    @Test
    public void deregister_otherParticipantsInSchisminGroup_keepsSchismingGroup() throws ParticipantAlreadyRegisteredException, SchismingGroupLimitReachedException {
        Participant participantToDeregister = createParticipant();
        sut.register(participantToDeregister);
        SchismingGroup group = sut.getSchismingGroup(participantToDeregister);
        Participant otherParticipant = createParticipant();
        group.add(otherParticipant);
        //ACT
        sut.deregister(participantToDeregister);
        //ASSERT
        assertEquals(1, sut.getSchismingGroups().size());
        assertSchismingGroup(otherParticipant);
    }

    @Test
    public void deregister_noParticipantInSchisminGroup_removesSchismingGroup() throws ParticipantAlreadyRegisteredException, SchismingGroupLimitReachedException {
        Participant participant = createParticipant();
        sut.register(participant);
        assertSchismingGroup(participant);
        //ACT
        sut.deregister(participant);
        //ASSERT
        assertEquals(0, sut.getSchismingGroups().size());
    }

    @NotNull
    public static Participant createParticipant() {
        return new Participant(mock(JitsiMeetConference.class), mock(XmppChatMember.class), 10);
    }

    private void assertSchismingGroup(Participant participant) {
        SchismingGroup result = sut.getSchismingGroup(participant);
        assertNotNull(result);
        Set<Participant> participants = result.getParticipants();
        assertTrue(participants.contains(participant));
    }

    private void assertNoSchismingGroup(Participant participant) {
        SchismingGroup result = sut.getSchismingGroup(participant);
        assertNull(result);
    }
}
