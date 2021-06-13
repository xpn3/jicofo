package org.jitsi.jicofo;

import mock.MockParticipant;
import mock.MockProtocolProvider;
import mock.muc.MockMultiUserChat;
import mock.muc.MockMultiUserChatOpSet;
import mock.util.TestConference;
import org.jitsi.jicofo.schisming.ParticipantAlreadyRegisteredException;
import org.jitsi.jicofo.schisming.SchismingGroupLimitReachedException;
import org.jitsi.jicofo.schisming.SchismingHub;
import org.jitsi.xmpp.extensions.jitsimeet.SSRCInfoPacketExtension;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.mockito.ArgumentMatcher;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JitsiMeetConferenceImplTest {
    private static final OSGiHandler osgi = OSGiHandler.getInstance();
    private TestConference testConf;
    private MockMultiUserChat chat;

    @Before
    public void setup() throws Exception {
        osgi.init();

        EntityBareJid roomName = JidCreate.entityBareFrom("testSSRCs@conference.pawel.jitsi.net");
        String serverName = "test-server";
        testConf = TestConference.allocate(osgi.bc, serverName, roomName);

        MockProtocolProvider provider = testConf.getFocusProtocolProvider();
        MockMultiUserChatOpSet mucOpSet = provider.getMockChatOpSet();
        chat = (MockMultiUserChat) mucOpSet.findRoom(roomName.toString());
    }

    @After
    public void teardown() throws Exception {
        osgi.shutdown();
    }

    @Test
    public void onMemberJoined_onSecondChatMember_registersBothParticipants() throws ParticipantAlreadyRegisteredException, SchismingGroupLimitReachedException {
        SchismingHub hub = mock(SchismingHub.class);
        testConf.conference.setSchismingHub(hub);

        MockParticipant user1 = new MockParticipant("User1");
        user1.setSsrcVideoType(SSRCInfoPacketExtension.CAMERA_VIDEO_TYPE);
        MockParticipant user2 = new MockParticipant("User2");
        user2.setSsrcVideoType(SSRCInfoPacketExtension.SCREEN_VIDEO_TYPE);

        //ACT
        user1.join(chat);
        user2.join(chat);

        //ASSERT
        verify(hub).register(argThat(new ParticipantMatcher(
                new Participant(testConf.conference, user1.getChatMember(), 10))));
        verify(hub).register(argThat(new ParticipantMatcher(
                new Participant(testConf.conference, user2.getChatMember(), 10))));
    }

    @Test
    public void onMemberLeft_deregistersParticipant() {
        SchismingHub hub = mock(SchismingHub.class);
        testConf.conference.setSchismingHub(hub);

        MockParticipant user1 = new MockParticipant("User1");
        user1.setSsrcVideoType(SSRCInfoPacketExtension.CAMERA_VIDEO_TYPE);
        user1.join(chat);

        //ACT
        user1.leave();

        //ASSERT
        verify(hub).deregister(argThat(new ParticipantMatcher(
                new Participant(testConf.conference, user1.getChatMember(), 10))));
    }

    public class ParticipantMatcher implements ArgumentMatcher<Participant> {
        private final Participant left;

        public ParticipantMatcher(Participant left) {
            this.left = left;
        }

        @Override
        public boolean matches(Participant right) {
            return left.getChatMember().equals(right.getChatMember());
        }
    }
}
