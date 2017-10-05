package org.apache.synapse.config.xml;

import org.apache.axiom.om.OMElement;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.Mediator;
import org.apache.synapse.deployers.SynapseArtifactDeploymentException;
import org.apache.synapse.mediators.transform.VelocityTemplateMediator;
import org.apache.synapse.mediators.transform.custom.ArgXpath;
import org.jaxen.JaxenException;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * Created by asanka on 3/7/16.
 */
public class VelocityTemplateMediatorFactory extends AbstractMediatorFactory {
    public static final QName propertyTemplateElement=new QName(XMLConfigConstants.SYNAPSE_NAMESPACE,"velocityTemplate");
    public static final QName formatElement=new QName(XMLConfigConstants.SYNAPSE_NAMESPACE,"format");
    public static final QName argumentListElement =new QName(XMLConfigConstants.SYNAPSE_NAMESPACE,"args");
    public static final QName argumentElement =new QName(XMLConfigConstants.SYNAPSE_NAMESPACE,"arg");
    public static final QName targetElement =new QName(XMLConfigConstants.SYNAPSE_NAMESPACE,"target");
    public static final QName expressionAttribute=new QName("expression");
    public static final QName nameAttribute =new QName("name");
    public static final QName argTypeAttribute=new QName("type");
    public static final QName classNameAttribute =new QName("className");
    public static final QName scopeAttribute=new QName("scope");
    public static final QName mediaTypeAttribute=new QName("media-type");
    public static final QName targetType=new QName("target-type");
    private static final Log LOG= LogFactory.getLog(VelocityTemplateMediatorFactory.class);

    @Override
    protected Mediator createSpecificMediator(OMElement omElement, Properties properties) {
        if(LOG.isDebugEnabled()){
            LOG.debug("Creating VelocityTemplateMediator out of "+omElement.toString());
        }
        VelocityTemplateMediator mediator=new VelocityTemplateMediator();
        String mediaTypeAttrValue = omElement.getAttributeValue(mediaTypeAttribute);//reading media type
        String mediaType = StringUtils.isEmpty(mediaTypeAttrValue)?"xml":mediaTypeAttrValue;//if null default media type is xml
        mediator.setMediaType(mediaType);//setting media type
        OMElement format = omElement.getFirstChildWithName(formatElement);
        if(format == null || (StringUtils.equals("xml",mediaType)&& format.getFirstElement()==null) ||
                (StringUtils.equals("json",mediaType)&& StringUtils.isEmpty(format.getText()))){
            //meets failure condition
            //format element is null or
            //if xml this doesn't have xml template body or
            //if json this doesn't have json string
            throw new SynapseArtifactDeploymentException("Template format is empty in PropertyTemplate Mediator");
        }
        //if media type is xml then the template body is first element of the format element
        //other wise it is json, then it is json string wrapped by format element
        String templateBody=(StringUtils.equals("xml",mediaType))?format.getFirstElement().toString():format.getText();
        mediator.setTemplate(templateBody);

        OMElement argumentList = omElement.getFirstChildWithName(argumentListElement);
        Iterator<OMElement> argumentsIterator = argumentList.getChildrenWithName(argumentElement);
        Map<String,ArgXpath> synXpathMap= new HashMap<String, ArgXpath>();
        while(argumentsIterator.hasNext()){
            OMElement argument = argumentsIterator.next();
            String name = argument.getAttributeValue(nameAttribute);
            String xpathExpression = argument.getAttributeValue(expressionAttribute);
            String argType = argument.getAttributeValue(argTypeAttribute);
            String customType= argument.getAttributeValue(classNameAttribute);
            if(StringUtils.isEmpty(xpathExpression) || StringUtils.isEmpty(name)){
                String msg = "expression or name attribute is missing in the arg element";
                LOG.error(msg);
                throw new SynapseArtifactDeploymentException(msg);
            }
            try {
                ArgXpath value = new ArgXpath(xpathExpression, argType);
                if(StringUtils.equalsIgnoreCase("custom",argType)){
                    if(StringUtils.isEmpty(customType)){
                        String msg = "className must not be present if type=\"custom\"";
                        LOG.error(msg);
                        throw new SynapseArtifactDeploymentException(msg);
                    }
                    value.setCustomType(customType);
                }
                synXpathMap.put(name, value);
            } catch (JaxenException e) {
                String msg = "Error while constructing xpath from argument " + xpathExpression;
                LOG.error(msg,e);
                handleException(msg);
            }
        }

        mediator.setxPathExpressions(synXpathMap);

        OMElement targetEle = omElement.getFirstChildWithName(targetElement);
        if(targetElement==null){
            throw new SynapseArtifactDeploymentException("Target element is missing in the Template Mediator");
        }
        String targetTypeValue =targetEle.getAttributeValue(targetType);
        targetTypeValue=(StringUtils.isEmpty(targetTypeValue))?"body":targetTypeValue;
        mediator.setTargetType(targetTypeValue);
        if(StringUtils.equalsIgnoreCase("property",targetTypeValue)){
            //if the target type is property then property name is mandotary
            String propertyName = targetEle.getAttributeValue(nameAttribute);

            if(StringUtils.isEmpty(propertyName)){
                String msg = "property name attribute is required in Template Mediator," +
                        " when the type is property";
                LOG.error(msg);
                throw new SynapseArtifactDeploymentException(msg);
            }
            String scope = targetEle.getAttributeValue(scopeAttribute);
            scope=(StringUtils.isEmpty(scope))?"synapse":scope;
            mediator.setPropertyName(propertyName);
            mediator.setScope(scope);
        }
        return mediator;
    }

    public QName getTagQName() {
        return propertyTemplateElement;
    }
}