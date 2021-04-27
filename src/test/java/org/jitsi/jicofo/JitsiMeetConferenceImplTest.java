package org.jitsi.jicofo;

import org.jitsi.impl.protocol.xmpp.ChatMemberImpl;
import org.jitsi.impl.protocol.xmpp.ChatRoomImpl;
import org.jitsi.jicofo.schisming.SchismingHub;
import org.jitsi.osgi.OSGIServiceRef;
import org.jitsi.osgi.ServiceUtils2;
import org.jitsi.protocol.xmpp.XmppChatMember;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.jid.parts.Part;
import org.jxmpp.jid.parts.Resourcepart;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.logging.Level;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JitsiMeetConferenceImplTest {
    @Test
    public void onMemberJoined_onSecondChatMember_registersBothParticipants() throws Exception {
        //EntityBareJid roomName = JidCreate.entityBareFrom("testSSRCs@conference.pawel.jitsi.net");
        EntityBareJid roomName = mock(EntityBareJid.class);
        Resourcepart focusUserName = mock(Resourcepart.class);
        ProtocolProviderHandler protocolProviderHandler = mock(ProtocolProviderHandler.class);
        ProtocolProviderHandler jvbXmppConnection = mock(ProtocolProviderHandler.class);
        JitsiMeetConferenceImpl.ConferenceListener listener = mock(JitsiMeetConferenceImpl.ConferenceListener.class);
        JitsiMeetConfig config = mock(JitsiMeetConfig.class);
        long gid = 1;

        assert roomName != null;
        assert focusUserName != null;
        assert protocolProviderHandler != null;
        assert jvbXmppConnection != null;
        assert listener != null;
        assert config != null;

        // prevent NullPointerException when calling new JitsiMeetConferenceImpl()
        Localpart localpart = mock(Localpart.class);
        when(roomName.getLocalpart()).thenReturn(localpart);

        /*OSGiHandler osgi = OSGiHandler.getInstance();
        OSGIServiceRef<FocusManager> focusManagerRef = new OSGIServiceRef<>(osgi.bc, FocusManager.class);
        focusManagerRef.get().conferenceRequest(roomName, new HashMap<>());
        JitsiMeetConferenceImpl conference = focusManagerRef.get().getConference(roomName);*/

        JitsiMeetConferenceImpl conference = new JitsiMeetConferenceImpl(roomName, focusUserName, protocolProviderHandler, jvbXmppConnection, listener, config, Level.INFO, gid);
        SchismingHub hub = mock(SchismingHub.class);
        conference.setSchismingHub(hub);

        XmppChatMember member1 = new ChatMemberImpl(mock(EntityFullJid.class), mock(ChatRoomImpl.class),1);
        XmppChatMember member2 = new ChatMemberImpl(mock(EntityFullJid.class), mock(ChatRoomImpl.class),2);

        // prevent NullPointerExceptions when calling conference.onMemberJoined()
        //when(member1.getContactAddress()).thenReturn("test");
        //when(member2.getContactAddress()).thenReturn("test");
        //when(conference.getFocusManager()).thenReturn(ServiceUtils2.getService(FocusBundleActivator.bundleContext, FocusManager.class));

        conference.onMemberJoined(member1);
        conference.onMemberJoined(member2);

        verify(hub).register(new Participant(conference, member1, 10)); // TODO check if maxSourceCount=10 is okay
        verify(hub).register(new Participant(conference, member2, 10)); // TODO check if maxSourceCount=10 is okay
    }
}
