package org.apache.synapse.mediators.transform;

import junit.framework.TestCase;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.synapse.Mediator;
import org.apache.synapse.MessageContext;
import org.apache.synapse.config.xml.VelocityTemplateMediatorFactory;
import org.apache.synapse.core.SynapseEnvironment;
import org.apache.synapse.mediators.TestUtils;
import org.apache.synapse.util.xpath.SynapseXPath;
import org.jaxen.JaxenException;

/**
 * Created by asanka on 9/13/17.
 */
public class VelocityTemplateMediatorTest extends TestCase {
    private static String ONE_TO_ONE_SOURCE ="<people><person><name>user1</name><age>27</age></person></people>";
    private static String ONE_TO_ONE_CODE ="<velocityTemplate media-type=\"xml\" xmlns=\"http://ws.apache.org/ns/synapse\"><format><person xmlns=\"\"><fname>$name</fname><age>$age</age></person></format><args><arg name=\"name\" expression=\"//name/text()\" /><arg name=\"age\" expression=\"//age/text()\" /></args><target target-type=\"body\"/></velocityTemplate>";
    private static String ARRAY_CODE="<velocityTemplate media-type=\"xml\" xmlns=\"http://ws.apache.org/ns/synapse\"><format><students xmlns=\"\">        #foreach($student in $students)            <student><name>$student</name></student>        #end    </students></format><args><arg name=\"students\" expression=\"//name/text()\" type=\"string\" /></args><target target-type=\"body\"/></velocityTemplate>";
    private static String ARRAY_SOURCE="<people><person><name>asanka</name><age>27</age></person><person><name>nuwan</name><age>28</age></person><person><name>Eranda</name><age>30</age></person><person><name>Malith</name><age>27</age></person></people>";
    private static String CUSTOM_TYPE_CODE="<velocityTemplate media-type=\"xml\" xmlns=\"http://ws.apache.org/ns/synapse\"><format><students xmlns=\"\">        #foreach($student in $students)            <student><name>$student.getName()</name></student>        #end    </students></format><args><arg name=\"students\" expression=\"//person\" type=\"custom\" className=\"org.apache.synapse.mediators.test.beans.Person\" /></args><target target-type=\"body\"/></velocityTemplate>";



    public void testOutputToPropertySynapseScope() throws Exception{
        String codeString=" <velocityTemplate media-type=\"xml\" xmlns=\"http://ws.apache.org/ns/synapse\"><format><person xmlns=\"\"><name>$code</name></person></format><args><arg name=\"code\" expression=\"$ctx:code\"/></args><target target-type=\"property\" name=\"propBody\" scope=\"synapse\"/></velocityTemplate>";
        OMElement code = AXIOMUtil.stringToOM(codeString);
        VelocityTemplateMediatorFactory factory=new VelocityTemplateMediatorFactory();
        MessageContext synCtx = TestUtils.getTestContext(ONE_TO_ONE_SOURCE);
        synCtx.setProperty("code","testCode");
        Mediator mediator = factory.createMediator(code, null);
        SynapseEnvironment synapseEnvironment = TestUtils.getTestSynaseEnvironment();
        assertTrue(mediator instanceof VelocityTemplateMediator);
        ((VelocityTemplateMediator)mediator).init(synapseEnvironment);
        mediator.mediate(synCtx);
        Object array = synCtx.getProperty("propBody");
        assertTrue(array instanceof OMElement);
        compareXpathResults("testCode","$ctx:propBody//name",synCtx);
    }

    public void testOneToOneTransformation() throws Exception {

        OMElement code = AXIOMUtil.stringToOM(ONE_TO_ONE_CODE);
        VelocityTemplateMediatorFactory factory=new VelocityTemplateMediatorFactory();
        MessageContext synCtx = TestUtils.getTestContext(ONE_TO_ONE_SOURCE);
        Mediator mediator = factory.createMediator(code, null);
        SynapseEnvironment synapseEnvironment = TestUtils.getTestSynaseEnvironment();
        assertTrue(mediator instanceof VelocityTemplateMediator);
        ((VelocityTemplateMediator)mediator).init(synapseEnvironment);
        mediator.mediate(synCtx);
        compareXpathResults("user1","//fname",synCtx);
        compareXpathResults("27","//age",synCtx);

    }

    public void testArrayTransformation() throws Exception{

        OMElement code = AXIOMUtil.stringToOM(ARRAY_CODE);
        VelocityTemplateMediatorFactory factory=new VelocityTemplateMediatorFactory();
        MessageContext synCtx = TestUtils.getTestContext(ARRAY_SOURCE);
        Mediator mediator = factory.createMediator(code, null);
        SynapseEnvironment synapseEnvironment = TestUtils.getTestSynaseEnvironment();
        assertTrue(mediator instanceof VelocityTemplateMediator);
        ((VelocityTemplateMediator)mediator).init(synapseEnvironment);
        mediator.mediate(synCtx);
        System.out.println(synCtx.getEnvelope().getBody().toString());
        compareXpathResults("4","string(count(//name))",synCtx);

    }

    public void testCustomTypeTransformation() throws Exception{

        OMElement code = AXIOMUtil.stringToOM(CUSTOM_TYPE_CODE);
        VelocityTemplateMediatorFactory factory=new VelocityTemplateMediatorFactory();
        MessageContext synCtx = TestUtils.getTestContext(ARRAY_SOURCE);
        Mediator mediator = factory.createMediator(code, null);
        SynapseEnvironment synapseEnvironment = TestUtils.getTestSynaseEnvironment();
        assertTrue(mediator instanceof VelocityTemplateMediator);
        ((VelocityTemplateMediator)mediator).init(synapseEnvironment);
        mediator.mediate(synCtx);
        System.out.println(synCtx.getEnvelope().getBody().toString());
        //compareXpathResults("4","string(count(//name))",synCtx);

    }


    private void compareXpathResults(String result,String xpath,MessageContext ctx){
        try {
            SynapseXPath nameXpath = new SynapseXPath(xpath);
            String s = nameXpath.stringValueOf(ctx);
            assertEquals(result,s);
        } catch (JaxenException e) {
            fail();
            e.printStackTrace();
        }
    }


}
