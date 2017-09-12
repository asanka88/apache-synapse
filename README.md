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

With the Velocity Template Mediator , it supports body,property,soap header,envelope as the targets. You can put the generated output to anyof these. And it supports both xml and json formats



Await for more documentation .....