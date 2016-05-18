package network;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yashesh on 6/11/2015.
 */
public class SOAPRequest extends NetworkRequest{


            private String baseUrl,actionName,nameSpace;
            List<Parameter> soapParameters;

    public List<Parameter> getSoapParameters() {
        return soapParameters;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public void addSoapParameter(Parameter parameter) {
       if(soapParameters==null){
           soapParameters=new ArrayList<Parameter>();
       }

        soapParameters.add(parameter);
    }



    public static class Parameter{

        private String name,value;
        private DataType type;
        public enum  DataType{

            INT,FLOAT,STRING,BOOLEAN;
        }


        public Parameter(String name,String value,DataType type){

            setName(name);
            setValue(value);
            setType(type);
        }
        public Parameter(String name,String value){

            setName(name);
            setValue(value);
            setType(DataType.STRING);
        }
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public DataType getType() {
            return type;
        }

        public void setType(DataType type) {
            this.type = type;
        }
    }


}
