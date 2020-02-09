Purpose: The purpose of this software is to  build a very basic three part system consisting of a client, an
intermediate host, and a server. The intermediate host does not change the packets, it just sends them on.

Developer & Development Date: Lila McIlveen - February 8, 2020

The learning purpose:
	Internetworking via UDP/IP
	Java’s DatagramPacket and DatagramSocket classes
	Conversion between Strings and arrays of bytes
	Running multiple main programs (projects) concurrently, acting as 3 separate systems

Software organization:
	There are 3 .java files under basic.system consisting of:
		- EchoServer.java
		- IntermediateHost.java
		- EchoClient.java

Set Up Instructions:
  1) Open Eclipse and import the project
  2) Right click on EchoServer.java and select Run As -> Java Application
  3) Right click on IntermediateHost.java and select Run As -> Java Application
  4) Right click on EchoClient.java and select Run As -> Java Application

Usage:
  One of your other buttons near your console tab is "Display Selected Console".
  When you choose this option you can select from any of the three running applications.

Limitations:
  - The intermediate host could be updated to change packets and thus become an error simulator for the system.
