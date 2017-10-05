/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *   * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.apache.synapse.config.xml;

public class VelocityTemplateMediatorSerializationTest extends AbstractTestCase {

    private VelocityTemplateMediatorFactory velocityTemplateMediatorFactory;
    private VelocityTemplateMediatorSerializer velocityTemplateMediatorSerializer;

    public VelocityTemplateMediatorSerializationTest() {
        super(AbstractTestCase.class.getName());
        velocityTemplateMediatorFactory = new VelocityTemplateMediatorFactory();
        velocityTemplateMediatorSerializer = new VelocityTemplateMediatorSerializer();
    }

    public void testVelocityTemplateMediatorSingleValueSerialization() throws Exception {

        String inputXml = "<velocityTemplate     xmlns=\"http://ws.apache.org/ns/synapse\" media-type=\"xml\"><format><person             xmlns=\"\"><name>$name.getText()</name>\n" +
                "            <age>$age.getText()</age></person></format><args><arg name=\"name\" expression=\"//name\"/><arg name=\"age\" expression=\"//age\"/></args><target target-type=\"body\"/></velocityTemplate>";

        assertTrue(serialization(inputXml, velocityTemplateMediatorFactory, velocityTemplateMediatorSerializer));
        assertTrue(serialization(inputXml, velocityTemplateMediatorSerializer));
    }

    public void testVelocityTemplateMediatorArraySerialization() throws Exception {

        String inputXml = "<velocityTemplate media-type=\"xml\" xmlns=\"http://ws.apache.org/ns/synapse\"><format><students>        #foreach($student in $students)            <student><name>$student.getText()</name></student>        #end    </students></format><args><arg name=\"students\" expression=\"//name/text()\" type=\"string\" /></args><target target-type=\"body\"/></velocityTemplate>";

        assertTrue(serialization(inputXml, velocityTemplateMediatorFactory, velocityTemplateMediatorSerializer));
        assertTrue(serialization(inputXml, velocityTemplateMediatorSerializer));
    }

    //
    public void testVelocityTemplateMediatorHeaderOutputSerialization() throws Exception {

        String inputXml = "<velocityTemplate media-type=\"xml\" xmlns=\"http://ws.apache.org/ns/synapse\"><format><person xmlns=\"\"><name>$code</name></person></format><args><arg name=\"code\" expression=\"$ctx:code\"/></args><target target-type=\"header\"/>" +
                "</velocityTemplate>";

        assertTrue(serialization(inputXml, velocityTemplateMediatorFactory, velocityTemplateMediatorSerializer));
        assertTrue(serialization(inputXml, velocityTemplateMediatorSerializer));
    }

    public void testVelocityTemplateMediatorCustomTypeSerialization() throws Exception {

        String inputXml = "<velocityTemplate media-type=\"xml\" xmlns=\"http://ws.apache.org/ns/synapse\"><format><students>        #foreach($student in $students)            <student><name>$student.getText()</name></student>        #end    </students></format><args><arg name=\"students\" expression=\"//person\" type=\"custom\" className=\"org.apache.synapse.mediators.test.beans.Person\" /></args><target target-type=\"body\"/></velocityTemplate>";

        assertTrue(serialization(inputXml, velocityTemplateMediatorFactory, velocityTemplateMediatorSerializer));
        assertTrue(serialization(inputXml, velocityTemplateMediatorSerializer));
    }

}
