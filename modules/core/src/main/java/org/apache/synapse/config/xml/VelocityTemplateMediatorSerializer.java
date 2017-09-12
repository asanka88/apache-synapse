package org.apache.synapse.config.xml;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.synapse.Mediator;
import org.apache.synapse.mediators.transform.VelocityTemplateMediator;
import org.apache.synapse.mediators.transform.custom.ArgXpath;
import org.apache.synapse.config.xml.enums.*;

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
        OMElement mediatorRoot = fac.createOMElement(VelocityTemplateMediatorFactory.propertyTemplateElement);
        OMElement formatOmElement = fac.createOMElement(VelocityTemplateMediatorFactory.formatElement);
        OMElement formatBody=null;
        if(StringUtils.equals(velocityTemplateMediator.getMediaType(),"xml")){
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
        OMElement argsListElement = fac.createOMElement(VelocityTemplateMediatorFactory.argumentListElement);
        Iterator<Map.Entry<String,ArgXpath>> iterator = velocityTemplateMediator.getxPathExpressions().entrySet().iterator();

        while (iterator.hasNext()){
            Map.Entry<String, ArgXpath> next = iterator.next();
            OMElement arg = fac.createOMElement(VelocityTemplateMediatorFactory.argumentElement);
            arg.addAttribute(VelocityTemplateMediatorFactory.nameAttribute.getLocalPart(),next.getKey(),null);
            arg.addAttribute(VelocityTemplateMediatorFactory.expressionAttribute.getLocalPart(),next.getValue().getRootExpr().getText(),null);
            if(next.getValue().getType()!=null){
                arg.addAttribute(VelocityTemplateMediatorFactory.argTypeAttribute.getLocalPart(),next.getValue().getType().toString(),null);
            }

            argsListElement.addChild(arg);
        }



        mediatorRoot.addChild(argsListElement);


        OMElement targetElement = fac.createOMElement(VelocityTemplateMediatorFactory.targetElement);
        targetElement.addAttribute(VelocityTemplateMediatorFactory.targetType.getLocalPart(), velocityTemplateMediator.
                getTargetType(),null);

        if(StringUtils.equals(velocityTemplateMediator.getTargetType(),"property")){
            targetElement.addAttribute(VelocityTemplateMediatorFactory.nameAttribute.getLocalPart(), velocityTemplateMediator.
                    getPropertyName(),null);
            targetElement.addAttribute(VelocityTemplateMediatorFactory.scopeAttribute.getLocalPart(), velocityTemplateMediator.
                    getScope(),null);
            targetElement.addAttribute(VelocityTemplateMediatorFactory.propertyTypeAttribute.getLocalPart(), velocityTemplateMediator
                    .getPropertyType(),null);

        }

        mediatorRoot.addChild(targetElement);
        return mediatorRoot;
    }

    public String getMediatorClassName() {
        return VelocityTemplateMediator.class.getName();
    }
}