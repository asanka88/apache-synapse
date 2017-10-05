# Apache Synapse with VelocityTemplateMediator
Synapse Mediator for creating properties out of templates

## Why Velocity Template Mediator ?

Curruntly synapse comes with built in payloadFactory mediator. But there are several drawbacks in it.

1.  Doesn’t support arrays :
   You cannot iterate though an array/collection and add those elements to the payload.
   
2.  Doesn’t support conditional expressions:
   In case you want to add elements based on conditions payloadFactory doesn’t support.
   
3.  Readability is less since numbered placeholders like $1,$2 are used


## How Velocity Template Mediator solve them?

Velocity template mediator uses well known Apache Velocity as the template engine. You can do whatever you can do with Velocity in side this. Iterate through collections, check conditions etc. In addition to that since we are using place holders like $name,$age for the variables ,so it improves the readability as well.

On the other hand, there was no such a thing in Synapse to create a property out of a template.

When you call a soap back end, suppose you have to send a user name token in SOAP Header.
Suppose there are several back end calls you need to make and in every call you need to pass the user name token.

With the payload factory , you will have to create that part every time. Instead if you can create a property one time using a template and save it in the message context , you can reuse it without generating again and again.

With the Velocity Template Mediator , it supports body,property,soap header,envelope as the targets. You can put the generated output to anyof these. And it supports xml, but json support will be there soon.

On top of that, you can use POJOs in transformations. You can create a POJO that maps to a xml segment and us that as shown in [Example #4](#4using-pojos)

## Examples

### 1. One to One Mapping

```xml
<velocityTemplate media-type="xml" 
    xmlns="http://ws.apache.org/ns/synapse">
    <format>
        <person 
            xmlns="">
            <name>$name</name>
            <age>$age</age>
        </person>
    </format>
    <args>
        <arg name="name" expression="//name1/text()" type="string" />
        <arg name="age" expression="//age1/text()" type="string" />
    </args>
    <target target-type="body"/>
</velocityTemplate>

```

### 2. Arrays

```xml
<velocityTemplate media-type="xml" 
    xmlns="http://ws.apache.org/ns/synapse">
    <format>
        <students 
            xmlns="">                        
            #foreach($student in $students)  
            <student>
                <name>$student</name>
                <test>$name</test>
            </student>                       
            #end                        
        </students>
    </format>
    <args>
        <arg name="students" expression="//name/text()" type="string" />
        <arg name="name" expression="$ctx:p" type="string" />
    </args>
    <target target-type="body"/>
</velocityTemplate>

```

### 3. Conditional Transformation

```xml
<velocityTemplate media-type="xml" 
    xmlns="http://ws.apache.org/ns/synapse">
    <format>
        <people 
            xmlns="">                                                    
            #foreach($student in $students) 
                #if($role=='student')  
                <student>
                    <name>$student</name>
                </student>                             
                #else                            
                <teacher>
                    <name>$student</name>
                </teacher>                            
                #end      
            #end                           
        </people>
    </format>
    <args>
        <arg name="students" expression="//name/text()" type="string" />
        <arg name="filter" expression="//role/text()" type="string" />
    </args>
    <target target-type="body"/>
</velocityTemplate>

```

### 4.Using Pojos

```xml   
<velocityTemplate media-type="xml" 
    xmlns="http://ws.apache.org/ns/synapse">
    <format>
        <people xmlns="">               
            #foreach($student in $students) 
                #if($student.getRole()=='student')  
                <student>
                    <name>$student.getName()</name>
                </student>                            
                #end  
            #end                    
        </people>
    </format>
    <args>
        <arg name="students" expression="//person" type="custom" className="org.asanka.synapse.test.beans.Person"/>
    </args>
    <target target-type="body"/>
</velocityTemplate>
```

Pojo Class

```java
package org.apache.synapse.mediators.test.beans;

public class Person {

    String name;
    int age;
    String role;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

```

