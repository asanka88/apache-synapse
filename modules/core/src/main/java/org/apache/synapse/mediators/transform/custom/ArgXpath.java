package org.apache.synapse.mediators.transform.custom;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMText;
import org.apache.commons.lang.StringUtils;
import org.apache.synapse.MessageContext;
import org.apache.synapse.config.xml.enums.ArgType;
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

    private ArgType type;

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
            default:
                finalResult = handleString(evaluate);
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

}
