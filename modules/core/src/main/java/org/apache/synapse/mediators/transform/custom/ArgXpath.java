package org.apache.synapse.mediators.transform.custom;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMText;
import org.apache.commons.lang.StringUtils;
import org.apache.synapse.MessageContext;
import org.apache.synapse.config.xml.enums.PropertyTypes;
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

    private PropertyTypes type;

    public PropertyTypes getType() {
        return type;
    }

    public void setType(PropertyTypes type) {
        this.type = type;
    }

    public Object getFormattedResult(MessageContext messageContext) throws JaxenException {
        Object evaluate = this.evaluate(messageContext);
        Object finalResult=null;

        PropertyTypes argType = Optional.ofNullable(this.type).orElse(PropertyTypes.string);

        //TODO: handle other types and custom types

        if(argType==PropertyTypes.string){
            if (evaluate instanceof ArrayList) {
                ArrayList tmpList = (ArrayList) evaluate;

                if (tmpList.size() == 1) {
                        finalResult= getString(((List) evaluate).get(0));
                } else if (tmpList.size() > 1) {
                    finalResult = tmpList.stream().map(o -> getString(o)).collect(Collectors.toList());
                }else{
                    finalResult= "";
                }
            }
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
            this.type=PropertyTypes.valueOf(type);
            //what if is not string or om, what is type is custom ojo
        }

    }

}
