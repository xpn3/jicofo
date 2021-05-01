package org.jitsi.jicofo;

import org.jitsi.impl.protocol.xmpp.ChatMemberImpl;
import org.jitsi.impl.protocol.xmpp.ChatRoomImpl;
import org.jitsi.jicofo.schisming.SchismingHub;
import org.jitsi.osgi.ServiceUtils2;
import org.jitsi.protocol.xmpp.XmppChatMember;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.jid.parts.Resourcepart;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JitsiMeetConferenceImplTest {
    JitsiMeetConferenceImpl conference;

    @Before
    public void setup() {
        EntityBareJid roomName = mock(EntityBareJid.class);
        Resourcepart focusUserName = mock(Resourcepart.class);
        ProtocolProviderHandler protocolProviderHandler = mock(ProtocolProviderHandler.class);
        ProtocolProviderHandler jvbXmppConnection = mock(ProtocolProviderHandler.class);
        JitsiMeetConferenceImpl.ConferenceListener listener = mock(JitsiMeetConferenceImpl.ConferenceListener.class);
        JitsiMeetConfig config = mock(JitsiMeetConfig.class);
        long gid = 1;

        Localpart localpart = mock(Localpart.class);
        when(roomName.getLocalpart()).thenReturn(localpart);

        conference = new JitsiMeetConferenceImpl(roomName, focusUserName, protocolProviderHandler, jvbXmppConnection,
                listener, config, null, gid);
    }

    @Test
    public void onMemberJoined_onSecondChatMember_registersBothParticipants() {
        SchismingHub hub = mock(SchismingHub.class);
        conference.setSchismingHub(hub);
        XmppChatMember member1 = new ChatMemberImpl(mock(EntityFullJid.class), mock(ChatRoomImpl.class),1);
        XmppChatMember member2 = new ChatMemberImpl(mock(EntityFullJid.class), mock(ChatRoomImpl.class),2);
        when(ServiceUtils2.getService(FocusBundleActivator.bundleContext, FocusManager.class)).thenReturn(mock(FocusManager.class));

        conference.onMemberJoined(member1);
        conference.onMemberJoined(member2);

        verify(hub).register(new Participant(conference, member1, 10));
        verify(hub).register(new Participant(conference, member2, 10));
    }
}
