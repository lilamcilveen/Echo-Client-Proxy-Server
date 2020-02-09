Purpose: The purpose of this software is to  build a very basic three part system consisting of a client, an
intermediate host, and a server. The intermediate host does not change the packets, it just sends them on.

Developer & Development Date: Lila McIlveen - February 8, 2020

The learning purpose:
	Internetworking via UDP/IP
	Java’s DatagramPacket and DatagramSocket classes
	Conversion between Strings and arrays of bytes
	Running multiple main programs (projects) concurrently, acting as 3 separate systems

Software organization:
	There are 3 .java files consisting of:
	- EchoServer.java
		Runs forever until an invalid packet is recieved (throws exception and quits)
		Parses packets to confirm valid formats ("read request" or "write request")
		Prints out received information and response packet information
		Closes the response socket upon sending the response DatagramPacket

	- IntermediateHost.java
		Runs forever, waiting to receive a request or response
		Prints out the information it sends and receives (both as a String and as bytes)
		Sends the request and response:
		Received a request? Create a DatagramSocket & form a DatagramPacket to port 69 on its send/receive socket, containing exactly what it received
		Received a response? Form a packet to send back to the host sending the request

	- EchoClient.java
		Alternates between read an write requests (currently 5 each, ending with #11 an invalid request:)
		Prints out the information it has put in the packet
		Sends the packet to a well known port: 23 on the intermediate host
		When it receives a DatagramPacket from the intermediate host, it prints out the information received, including the byte array

Set Up Instructions:
  1) Open Eclipse and import the directory '101038709_Assignment2'
  2) Right click on EchoServer.java and select Run As -> Java Application
  3) Right click on IntermediateHost.java and select Run As -> Java Application
  4) Right click on EchoClient.java and select Run As -> Java Application

Usage:
  One of your other buttons near your console tab is "Display Selected Console".
  When you choose this option you can select from any of the three running applications.

Limitations:
  - The intermediate host could be updated to change packets and thus become an error simulator for the system.
