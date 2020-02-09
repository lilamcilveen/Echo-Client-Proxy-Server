/**
 * The intermediate host creates a DatagramSocket to receive data from port 23
 * It also creates a DatagramSocket used to send and receive
 * Features:
 * 	- Runs forever, waiting to receive a request or response
 * 	- Prints out the information it sends and receives (both as a String and as bytes)
 * 	- Sends the request and response:
 * 	- Received a request? Create a DatagramSocket & form a DatagramPacket to port 69 on 
 * its send/receive socket, containing exactly what it received
 * 	- Received a response? Form a packet to send back to the host sending the request 
 */
package basic.system;

import java.net.*;
import java.util.Arrays;
import java.io.IOException;

/**
 * @author Lila McIlveen
 * @date February 8, 2020
 */

public class IntermediateHost {

	private DatagramPacket sendPacket, receivePacket, sendReceivePacket;
	private DatagramSocket sendReceiveSocket, receiveSocket;

	public IntermediateHost() {
		try {
			// Construct a DatagramSocket bind it to port 23
			receiveSocket = new DatagramSocket(23);

			// Construct a DatagramSocket able to send packets from any available port
			sendReceiveSocket = new DatagramSocket();

		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}
	}

	/*
	 * Helper function: Prints information relating to a send request
	 */

	private void processSend(DatagramPacket packet) {
		System.out.println("IntermediateHost: Sending packet:");
		System.out.println("To host: " + packet.getAddress());
		System.out.println("Destination host port: " + packet.getPort());
		processStatus(packet);
	}

	/*
	 * Helper function: Prints information relating to a receive request
	 */
	private void processReceive(DatagramPacket packet) {
		System.out.println("IntermediateHost: Packet received:");
		System.out.println("From host: " + packet.getAddress());
		System.out.println("Host port: " + packet.getPort());
		processStatus(packet);
	}

	/*
	 * Helper function: Prints information relating to any request
	 */
	private void processStatus(DatagramPacket packet) {
		int len = packet.getLength();
		System.out.println("Packet length: " + len);
		System.out.println("Packet string:" + new String(packet.getData(), 0, len)); //print the packet as a string
		System.out.print("Packet bytes: ");
		System.out.println(Arrays.toString(packet.getData())); // prints the packet as bytes
		System.out.println();
	}

	/*
	 * Receives a request packet from the client and sends the request packet to the
	 * server Receives a response packet from the server and sends the response to
	 * the client
	 */
	private void receiveAndEcho() {
		// repeat the following "forever"
		while (true) {

			byte responseData[] = new byte[4]; // buffer for the 4 byte response packet
			byte requestData[] = new byte[20]; // buffer for receiving request packets

			// Create a datagram packet to receive packets of the buffer's size
			receivePacket = new DatagramPacket(requestData, requestData.length);
			System.out.println("IntermediateHost: Waiting for Packet from the client\n");

			// Wait for receiveSocket to receive a datagram packet from the client
			try {
				System.out.println("Waiting..."); // status
				receiveSocket.receive(receivePacket);
			} catch (IOException e) {
				System.out.print("IO Exception: likely:");
				System.out.println("Receive Socket Timed Out.\n" + e);
				e.printStackTrace();
				System.exit(1); // error, terminate server
			}

			processReceive(receivePacket);

			// Wait for receiveSocket to receive a datagram packet from the client
			try {
				sendReceivePacket = new DatagramPacket(receivePacket.getData(), receivePacket.getLength(),
						InetAddress.getLocalHost(), 69);
			} catch (UnknownHostException uhe) {
				uhe.printStackTrace();
				System.exit(1);
			}

			// Send the sendReceivePacket to the intermediate host
			try {
				sendReceiveSocket.send(sendReceivePacket);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}

			processSend(sendReceivePacket);

			// Create a DatagramPacket containing responseData (4 bytes), to be sent to port
			// 69
			try {
				sendReceivePacket = new DatagramPacket(responseData, responseData.length, InetAddress.getLocalHost(),
						69);
				System.out.println("IntermediateHost: Waiting for Packet from server\n");
			} catch (UnknownHostException uhe) {
				uhe.printStackTrace();
				System.exit(1);
			}

			// Wait until sendReceivePacket receives a packet from the server
			try {
				System.out.println("Waiting...");
				sendReceiveSocket.receive(sendReceivePacket);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}

			processReceive(sendReceivePacket);

			// Create a DatagramPacket that is ported to the same port as receivePacket
			sendPacket = new DatagramPacket(responseData, responseData.length, receivePacket.getAddress(),
					receivePacket.getPort());

			// Send the created DatagramPacked (sendPacket) to the client
			try {
				sendReceiveSocket.send(sendPacket);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(0);
			}

			processSend(sendPacket);
		}
	}

	public static void main(String[] args) {
		IntermediateHost h = new IntermediateHost();
		h.receiveAndEcho();
	}
}
