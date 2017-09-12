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

        PropertyTypes argType = Optional.ofNullable(this.type).orElse(PropertyTypes.string);

        //if list

//        if(evaluate instanceof ArrayList){
//            ArrayList tmpList=(ArrayList)evaluate;
//
//            if(tmpList.size()==1){
//                if(this.type==null){
//                    return tmpList.get(0);
//                }else{
//                   return getString(((List) evaluate).get(0));
//                }
//            }else if(tmpList.size()>1){
//                ArrayList<String> result=new ArrayList<String>(tmpList.size());
//
//               tmpList.stream().map(s -> getString(s)).collect(Collectors.toList());
//
//                return result;
//
//            }
//        }



        if(argType==PropertyTypes.string){
            if (evaluate instanceof ArrayList) {
                ArrayList tmpList = (ArrayList) evaluate;

                if (tmpList.size() == 1) {
                        return getString(((List) evaluate).get(0));

                } else if (tmpList.size() > 1) {

                    Object collect = tmpList.stream().map(o -> getString(o)).collect(Collectors.toList());

                    return collect;

                }else{
                    return "";
                }
            }
        }




//        if(this.type==null){
//            return evaluate;
//        }else if(type==PropertyTypes.string){
//            if(evaluate instanceof ArrayList){
//                ArrayList tmpList=(ArrayList)evaluate;
//                int size = tmpList.size();
//                if(size >1){
//                    ArrayList<String> result=new ArrayList<String>(tmpList.size());
//                    Iterator iterator = tmpList.iterator();
//                    while (iterator.hasNext()){
//                        Object next = iterator.next();
//                        result.add(getString(next));
//                    }
//                    return result;
//                }else if(size==1){
//                    Object o = tmpList.get(0);
//                    return getString(o);
//                }
//
//            }
//
//        }
        return evaluate;
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
        }

    }

}
