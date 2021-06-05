package org.jitsi.jicofo.schisming;

import org.custommonkey.xmlunit.Diff;
import org.jitsi.jicofo.Participant;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.id.StanzaIdUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;
import org.mockito.junit.MockitoJUnitRunner;
import org.xml.sax.SAXException;
import java.io.IOException;
import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.jitsi.jicofo.schisming.SchismingHubImplTest.createParticipant;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SchismingIqTest {
    private SchismingIq sut;

    @Before
    public void setup() {
        sut = new SchismingIq();
    }

    @Test
    public void toXml() throws ParticipantAlreadyRegisteredException, IOException, SAXException, SchismingGroupLimitReachedException {
        sut.setStanzaId(StanzaIdUtil.newStanzaId());
        sut.setType(IQ.Type.set);
        Participant participant1 = SchismingHubImplTest.createParticipant();
        Participant participant2 = SchismingHubImplTest.createParticipant();
        Participant participant3 = SchismingHubImplTest.createParticipant();
        when(participant1.getMucJid()).thenReturn(createAddressForName("participant1"));
        when(participant2.getMucJid()).thenReturn(createAddressForName("participant2"));
        when(participant3.getMucJid()).thenReturn(createAddressForName("participant3"));
        SchismingHub hub = new SchismingHubImpl();
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
                        "<participant id='" + participant1.getMucJid().toString() + "'></participant>" +
                    "</group>" +
                    "<group id='2'>" +
                        "<participant id='" + participant2.getMucJid().toString() + "'></participant>" +
                    "</group>" +
                    "<group id='3'>" +
                        "<participant id='" + participant3.getMucJid().toString() + "'></participant>" +
                    "</group>" +
                "</schisminghub>" +
            "</iq>",
            result), true);
    }

    private EntityFullJid createAddressForName(String nickname) throws XmppStringprepException {
        return JidCreate.entityFullFrom(JidCreate.entityBareFrom("testSSRCs@conference.pawel.jitsi.net"), Resourcepart.from(nickname));
    }
}
