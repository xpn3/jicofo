package org.jitsi.jicofo.schisming;
import org.jitsi.jicofo.Participant;
import org.jitsi.utils.logging.Logger;
import org.jivesoftware.smack.packet.*;

public class SchismingStateIq extends IQ {
    private final static Logger logger = Logger.getLogger(SchismingStateIq.class);

    public static final String ELEMENT_NAME = "schisminghub";
    public static final String NAMESPACE = "http://jitsi.org/jitmeet/schisming";

    public SchismingHub hub;

    public SchismingStateIq()
    {
        super(ELEMENT_NAME, NAMESPACE);
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml)
    {
        xml.rightAngleBracket();
        if(hub != null) {
            for(SchismingGroup group : hub.getSchismingGroups()) {
                xml.halfOpenElement("group");
                xml.attribute("id", group.getId());
                xml.rightAngleBracket();
                for(Participant participant : group.getParticipants()) {
                    xml.halfOpenElement("participant");
                    xml.attribute("id", participant.getEndpointId());
                    xml.rightAngleBracket();
                    xml.closeElement("participant");
                }
                xml.closeElement("group");
            }
        }
        logger.debug("IQChildElementXml: " + xml.toString());
        return xml;
    }

    public void setHub(SchismingHub hub) {
        this.hub = hub;
    }
}