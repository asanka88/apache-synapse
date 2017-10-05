package org.apache.synapse.mediators.transform.custom;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.impl.llom.util.AXIOMUtil;
import org.apache.axis2.AxisFault;
import org.apache.axis2.databinding.utils.BeanUtil;
import org.apache.axis2.engine.DefaultObjectSupplier;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseException;
import org.apache.synapse.config.xml.enums.ArgType;
import org.apache.synapse.mediators.bean.BeanUtils;
import org.apache.synapse.util.AXIOMUtils;
import org.apache.synapse.util.xpath.SynapseXPath;
import org.jaxen.JaxenException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by asanka on 3/21/16.
 */
public class ArgXpath extends SynapseXPath {

    private static final Log LOG= LogFactory.getLog(ArgXpath.class);
    private ArgType type;
    private String customType;
    private Class customTypeClass;

    public ArgType getType() {
        return type;
    }

    public void setType(ArgType type) {
        this.type = type;
    }

    public Object getFormattedResult(MessageContext messageContext) throws JaxenException {
        Object evaluate = this.evaluate(messageContext);
        Object finalResult;

        ArgType argType = Optional.ofNullable(this.type).orElse(ArgType.string);

        //TODO: handle other types and custom types

        switch (argType) {
            case string:
                finalResult = handleString(evaluate);
                break;
            case om:
                finalResult = handleOM(evaluate);
                break;
            case custom:
                finalResult = handleCustom(evaluate);
                break;
            default:
                finalResult = handleString(evaluate);
        }

        return finalResult;
    }

    private Object handleCustom(Object result) {

        Object evaluate = handleOM(result);

        Object finalResult=null;
        if(evaluate instanceof ArrayList) {

            finalResult = ((ArrayList) evaluate).stream().filter(o -> o instanceof OMElement).map(o -> {
                try {
                    return BeanUtil.deserialize(customTypeClass, ((OMElement) o), new DefaultObjectSupplier(), "result");
                } catch (AxisFault axisFault) {
                    axisFault.printStackTrace();
                }
                return null;
            }).collect(Collectors.toList());


        }else{
            try {
                finalResult =BeanUtil.deserialize(customTypeClass, (OMElement) evaluate, new DefaultObjectSupplier(), "result");
            } catch (AxisFault axisFault) {
                axisFault.printStackTrace();
            }

        }

        return finalResult;
    }

    private Object handleString(Object result) {
        Object finalResult;
        if (result instanceof ArrayList) {
            ArrayList tmpList = (ArrayList) result;

            if (tmpList.size() == 1) {
                finalResult= getString(((List) result).get(0));
            } else if (tmpList.size() > 1) {
                finalResult = tmpList.stream().map(this::getString).collect(Collectors.toList());
            }else{
                finalResult= "";
            }
        }else{
            finalResult=result.toString();
        }
        return finalResult;
    }

    private Object handleOM(Object result) {
        Object finalResult = null;
        if (result instanceof ArrayList) {
            ArrayList tmpList = (ArrayList) result;

            if (tmpList.size() == 1) {
                finalResult= (OMElement)tmpList.get(0);
            } else if (tmpList.size() > 1) {
                finalResult = tmpList;
            }
        }else{
            finalResult=result;
        }
        return finalResult;
    }

    private String getString(Object o){
        if(o instanceof OMElement){
            return o.toString();
        }else if(o instanceof OMText){
            return ((OMText) o).getText();
        }else if(o instanceof OMAttribute){
            return ((OMAttribute) o).getAttributeValue();
        }else{
            return o.toString();
        }
    }

    public ArgXpath(String xpathString) throws JaxenException {
        super(xpathString);
    }

    public ArgXpath(String xpathString,String type) throws JaxenException {
        super(xpathString);
        if(StringUtils.isEmpty(type)){
            this.type=null;
        }else {
            this.type= ArgType.valueOf(type);
            //what if is not string or om, what is type is custom ojo
        }

    }

    public void setCustomType(String customType) {
        this.customType = customType;
        try {
            customTypeClass=Class.forName(customType);
        } catch (ClassNotFoundException e) {
            LOG.error(e);
            throw new SynapseException("Class "+customType+" cannot be found for custom type",e);
        }
    }

    public String getCustomType() {
        return customType;
    }

    public Object getDefault() throws IllegalAccessException, InstantiationException {
        if(this.type==ArgType.custom){
            return customTypeClass.newInstance();
        }
        return "";
    }
}
