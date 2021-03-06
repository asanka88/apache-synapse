package org.apache.synapse.config.xml;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.synapse.Mediator;
import org.apache.synapse.mediators.transform.VelocityTemplateMediator;
import org.apache.synapse.mediators.transform.custom.ArgXpath;
import org.apache.synapse.config.xml.enums.*;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by asanka on 3/7/16.
 */
public class VelocityTemplateMediatorSerializer extends AbstractMediatorSerializer {

    @Override
    protected OMElement serializeSpecificMediator(Mediator mediator) {
        if(!(mediator instanceof VelocityTemplateMediator)){
            handleException("Unsupported mediator passed in for serialization : "
                    + mediator.getType());
        }

        VelocityTemplateMediator velocityTemplateMediator =(VelocityTemplateMediator)mediator;
        OMElement mediatorRoot = fac.createOMElement(VelocityTemplateMediatorFactory.propertyTemplateElement.getLocalPart(),synNS);
        mediatorRoot.addAttribute(VelocityTemplateMediatorFactory.mediaTypeAttribute.getLocalPart(),velocityTemplateMediator.getMediaType(),null);
        OMElement formatOmElement = fac.createOMElement(VelocityTemplateMediatorFactory.formatElement.getLocalPart(),synNS);
        OMElement formatBody=null;
        if(StringUtils.equals(velocityTemplateMediator.getMediaType(),MediaTypes.xml.toString())){
            try {
                formatBody = AXIOMUtil.stringToOM(velocityTemplateMediator.getTemplate());
                formatOmElement.addChild(formatBody);
            } catch (XMLStreamException e) {
                handleException("Failed to serialize template format");
            }
        }else {
            formatOmElement.setText(velocityTemplateMediator.getTemplate());
        }

        mediatorRoot.addChild(formatOmElement);
        OMElement argsListElement = fac.createOMElement(VelocityTemplateMediatorFactory.argumentListElement.getLocalPart(),synNS);
        velocityTemplateMediator.getxPathExpressions().entrySet().stream().forEach(entry -> {

            OMElement arg = fac.createOMElement(VelocityTemplateMediatorFactory.argumentElement.getLocalPart(),synNS);
            arg.addAttribute(VelocityTemplateMediatorFactory.nameAttribute.getLocalPart(),entry.getKey(),null);
            ArgXpath value = entry.getValue();
            arg.addAttribute(VelocityTemplateMediatorFactory.expressionAttribute.getLocalPart(), value.toString(),null);
            if(value.getType()!=null){
                arg.addAttribute(VelocityTemplateMediatorFactory.argTypeAttribute.getLocalPart(), value.getType().toString(),null);

                String customType = value.getCustomType();
                if(StringUtils.isNotEmpty(customType)){
                    arg.addAttribute(VelocityTemplateMediatorFactory.classNameAttribute.getLocalPart(), customType.toString(),null);

                }
            }

            argsListElement.addChild(arg);

        });

        mediatorRoot.addChild(argsListElement);

        OMElement targetElement = fac.createOMElement(VelocityTemplateMediatorFactory.targetElement.getLocalPart(),synNS);
        targetElement.addAttribute(VelocityTemplateMediatorFactory.targetType.getLocalPart(), velocityTemplateMediator.
                getTargetType(),null);

        if(StringUtils.equals(velocityTemplateMediator.getTargetType(),TargetType.property.toString())){
            targetElement.addAttribute(VelocityTemplateMediatorFactory.nameAttribute.getLocalPart(), velocityTemplateMediator.
                    getPropertyName(),null);
            targetElement.addAttribute(VelocityTemplateMediatorFactory.scopeAttribute.getLocalPart(), velocityTemplateMediator.
                    getScope(),null);
        }

        mediatorRoot.addChild(targetElement);
        return mediatorRoot;
    }

    public String getMediatorClassName() {
        return VelocityTemplateMediator.class.getName();
    }
}