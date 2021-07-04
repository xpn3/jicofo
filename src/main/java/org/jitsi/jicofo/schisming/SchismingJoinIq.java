package org.jitsi.jicofo.schisming;

import org.jitsi.jicofo.Participant;
import org.jitsi.utils.logging.Logger;
import org.jivesoftware.smack.packet.IQ;

public class SchismingJoinIq extends IQ {
    private final static Logger logger = Logger.getLogger(SchismingJoinIq.class);

    public static final String ELEMENT_NAME = "join";
    public static final String NAMESPACE = "http://jitsi.org/jitmeet/schisming";

    private String participantId;
    private Integer groupId;

    public SchismingJoinIq()
    {
        super(ELEMENT_NAME, NAMESPACE);
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml)
    {
        xml.attribute("participantId", participantId);
        xml.attribute("groupId", groupId);
        xml.rightAngleBracket();
        xml.closeElement("join");

        logger.info(xml.toString());
        return xml;
    }

    public String getParticipantId() {
        return participantId;
    }

    public void setParticipantId(String participantId) {
        this.participantId = participantId;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }
}