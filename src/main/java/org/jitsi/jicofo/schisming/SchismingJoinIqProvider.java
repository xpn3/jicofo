package org.jitsi.jicofo.schisming;

import org.jitsi.utils.logging.Logger;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.xmlpull.v1.XmlPullParser;

public class SchismingJoinIqProvider extends IQProvider<SchismingJoinIq>
{
    private final static Logger logger = Logger.getLogger(SchismingJoinIq.class);

    public static void registerSchismingJoinIqProvider()
    {
        ProviderManager.addIQProvider(
                SchismingJoinIq.ELEMENT_NAME,
                SchismingJoinIq.NAMESPACE,
                new SchismingJoinIqProvider());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SchismingJoinIq parse(XmlPullParser parser, int initialDepth)
    {
        String namespace = parser.getNamespace();
        if(!SchismingJoinIq.NAMESPACE.equals(namespace)) {
            logger.warn("Unable to parse SchismingJoinIq due to invalid namespace " + namespace);
            return null;
        }

        String rootElement = parser.getName();
        if(!SchismingJoinIq.ELEMENT_NAME.equals(rootElement)) {
            logger.warn("Unable to parse SchismingJoinIq due to invalid element name " + rootElement);
            return null;
        }

        SchismingJoinIq iq = new SchismingJoinIq();
        String participantId = parser.getAttributeValue("", "participantId");
        iq.setParticipantId(participantId);

        Integer groupId = null;
        if(parser.getAttributeValue("", "groupId") != null) {
            groupId = Integer.valueOf(parser.getAttributeValue("", "groupId"));
        }
        iq.setGroupId(groupId);

        return iq;
    }
}
