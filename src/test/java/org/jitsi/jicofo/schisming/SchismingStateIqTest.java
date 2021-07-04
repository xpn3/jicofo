package org.jitsi.jicofo.schisming;

import org.custommonkey.xmlunit.Diff;
import org.jetbrains.annotations.NotNull;
import org.jitsi.jicofo.JitsiMeetConference;
import org.jitsi.jicofo.Participant;
import org.jitsi.protocol.xmpp.XmppChatMember;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.id.StanzaIdUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.xml.sax.SAXException;
import java.io.IOException;
import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SchismingStateIqTest {
    private SchismingStateIq sut;

    @Before
    public void setup() {
        sut = new SchismingStateIq();
    }

    @Test
    public void toXml() throws ParticipantAlreadyRegisteredException, IOException, SAXException, SchismingGroupLimitReachedException, SmackException.NotConnectedException, InterruptedException {
        sut.setStanzaId(StanzaIdUtil.newStanzaId());
        sut.setType(IQ.Type.set);
        SchismingHub hub = spy(SchismingHubImpl.class);
        Participant participant1 = createParticipant(hub);
        Participant participant2 = createParticipant(hub);
        Participant participant3 = createParticipant(hub);
        when(participant1.getEndpointId()).thenReturn("participant1");
        when(participant2.getEndpointId()).thenReturn("participant2");
        when(participant3.getEndpointId()).thenReturn("participant3");
        hub.register(participant1);
        hub.register(participant2);
        hub.register(participant3);
        sut.setHub(hub);
        //ACT
        String result = sut.toXML().toString();
        //ASSERT
        assertXMLEqual(new Diff(
            "<iq id='" + sut.getStanzaId() + "' type='set'>" +
                "<schisminghub xmlns='http://jitsi.org/jitmeet/schisming'>" +
                    "<group id='1'>" +
                        "<participant id='" + participant1.getEndpointId() + "'></participant>" +
                    "</group>" +
                    "<group id='2'>" +
                        "<participant id='" + participant2.getEndpointId() + "'></participant>" +
                    "</group>" +
                    "<group id='3'>" +
                        "<participant id='" + participant3.getEndpointId() + "'></participant>" +
                    "</group>" +
                "</schisminghub>" +
            "</iq>",
            result), true);
    }

    @NotNull
    private Participant createParticipant(SchismingHub hub) {
        Participant participant = new Participant(mock(JitsiMeetConference.class), mock(XmppChatMember.class), 10);

        try {
            doNothing().when(hub).sendState(participant);
        } catch (SmackException.NotConnectedException | InterruptedException e) {
            e.printStackTrace();
        }

        return participant;
    }
}
