# Echo Client/Proxy/Server

## Purpose:
The purpose of this software is to simulate a basic three part system consisting of a client, intermediate host, and server. The intermediate host does not change the packets, it just sends them.

The system involves:
- Internetworking via UDP/IP
- Java’s DatagramPacket and DatagramSocket classes
- Conversion between Strings and arrays of bytes
- Running multiple systems concurrently (demonstrated as 3 separate processes with specific networking rules)

## Set up instructions:
  1) Open **Eclipse** and import the project
  2) Right click on **EchoServer.java** and select `Run As` **>>** `Java Application`
  3) Right click on **IntermediateHost.java** and select `Run As` **>>** `Java Application`
  4) Right click on **EchoClient.java** and select `Run As` **>>** `Java Application`
  
  One of the buttons in Eclipse near the console tab is "Display Selected Console".
  When you choose this option you can select from any of the three running applications.

## Software organization:
There are 3 .java files under basic.system consisting of:
- EchoServer.java
- IntermediateHost.java
- EchoClient.java

## Limitations:
- The client must run after the server.
- The system is currently configured to do 5 reads, 5 writes, and 1 invalid packet
  - This can be changed through `echoAndReceive()` in **EchoClient.java** 
- The intermediate host could be updated to change packets and thus become an error simulator for the system

---

Created on February 9, 2020.
