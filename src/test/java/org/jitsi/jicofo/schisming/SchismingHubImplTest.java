package org.jitsi.jicofo.schisming;

import org.jitsi.jicofo.JitsiMeetConference;
import org.jitsi.jicofo.Participant;
import org.jitsi.protocol.xmpp.XmppChatMember;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class SchismingHubImplTest {
    @Test
    public void register() {
        SchismingHub sut = new SchismingHubImpl();
        Participant participant = new Participant(mock(JitsiMeetConference.class), mock(XmppChatMember.class), 10);
        //ACT
        sut.register(participant);
        //ASSERT
        SchismingGroup result = sut.getSchismingGroup(participant);
        assert result != null;
        List<Participant> participants = result.getParticipants();
        Assert.assertEquals(Arrays.asList(participant), participants);
    }
}
