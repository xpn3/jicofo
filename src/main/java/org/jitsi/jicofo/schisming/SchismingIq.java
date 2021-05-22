package org.jitsi.jicofo.schisming;
import org.jitsi.utils.logging.Logger;
import org.jivesoftware.smack.packet.*;
import org.jxmpp.jid.Jid;

public class SchismingIq extends IQ {
    private final static Logger logger = Logger.getLogger(SchismingIq.class);

    public static final String ELEMENT_NAME = "schisminghub";
    public static final String NAMESPACE = "http://jitsi.org/jitmeet/schisming";

    public SchismingIq()
    {
        super(ELEMENT_NAME, NAMESPACE);
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml)
    {
        xml.attribute("test", "testvalue");
        xml.rightAngleBracket()
                .append("test content");
        return xml;
    }
}