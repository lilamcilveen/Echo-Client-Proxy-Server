/**
 * The client creates a DatagramSocket to both send packets to port 23 on the intermediate host and receive DatagramPackets from the intermediate host.
 * Features:
 * 	- Alternates between read an write requests (currently 5 each, ending with #11 an invalid request:)
 * 	- Prints out the information it has put in the packet
 * 	- Sends the packet to a well known port: 23 on the intermediate host
 *  - When it receives a DatagramPacket from the intermediate host, it prints out the information received, 
 *  including the byte array
 * 
 */
package basic.system;

import java.net.*;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author Lila McIlveen
 * @date February 8, 2020
 */
public class EchoClient {

	private DatagramPacket sendPacket, receivePacket;
	private DatagramSocket sendReceiveSocket;

	private final int REPEAT = 11;
	
	// filename to be sent through netascii
	private String file = "test.txt"; // file name to be converted from a string to bytes
	private String mode = "netascii";

	// bytes used to indicate a read or write request
	private byte zero = 0x00;
	private byte one = 0x01;
	private byte two = 0x02;

	public EchoClient() {
		try {
			sendReceiveSocket = new DatagramSocket();
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}
	}

	/*
	 * Helper function: Forms an array of bytes containing a request to be later
	 * sent to the intermediate host Input: packetCount - indicating which packet is
	 * being created Output: array of bytes
	 */
	private byte[] formByteArray(int packetCount) {
		// filename and mode converted from a string to bytes
		byte[] filenameBytes = file.getBytes();
		byte[] modeBytes = mode.getBytes();

		// forming the buffer
		byte[] buffer = new byte[filenameBytes.length + modeBytes.length + 4];
		buffer[0] = zero; // first byte is 0

		// second byte depends on:
		// alternating between read and write requests depending on packetCount
		if (packetCount % 2 == 1)
			buffer[1] = one;
		else
			buffer[1] = two;

		// copying filenameBytes into the buffer
		for (int i = 0; i < filenameBytes.length; i++)
			buffer[i + 2] = filenameBytes[i];

		// the 11th request is invalid
		if (packetCount != 11)
			buffer[filenameBytes.length + 2] = zero; // valid request
		else
			buffer[filenameBytes.length + 2] = one; // invalid request case (11th)

		// copying modeBytes into the buffer
		for (int i = 0; i < modeBytes.length; i++)
			buffer[i + filenameBytes.length + 3] = modeBytes[i];

		// finally, another 0 byte
		buffer[filenameBytes.length + modeBytes.length + 3] = zero;

		return buffer;
	}

	/*
	 * Helper function: Prints information relating to a send request
	 */
	private void processSend(DatagramPacket packet) {
		System.out.println("Client: Sending packet:");
		System.out.println("To host: " + packet.getAddress());
		System.out.println("Destination host port: " + packet.getPort());
		processStatus(packet);
	}

	/*
	 * Helper function: Prints information relating to a receive request
	 */
	private void processReceive(DatagramPacket packet) {
		System.out.println("Client: Packet received:");
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

	private void echoAndReceive() {
		byte[] buffer;

		for (int i = 0; i < REPEAT; i++) {
			buffer = formByteArray(i + 1);

			// DatagramPacket to be sent through port 23 on the intermediate host
			try {
				sendPacket = new DatagramPacket(buffer, buffer.length, InetAddress.getLocalHost(), 23);
			} catch (UnknownHostException uhe) {
				uhe.printStackTrace();
				System.exit(1);
			}
			processSend(sendPacket);

			try {
				sendReceiveSocket.send(sendPacket);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}

			System.out.println("Client: Packet sent. \n");

			// DatagramPacket to receive the response from the intermediate host
			byte[] responseBuffer = new byte[4];
			receivePacket = new DatagramPacket(responseBuffer, responseBuffer.length);

			//
			try {
				System.out.println("Waiting...");
				sendReceiveSocket.receive(receivePacket);
			} catch (IOException e) {
				System.out.print("IO Exception: likely:");
				System.out.println("Receive Socket Timed Out.\n" + e);
				e.printStackTrace();
				System.exit(1);
			}

			processReceive(receivePacket);
		}
	}

	public static void main(String[] args) {
		EchoClient c = new EchoClient();
		c.echoAndReceive();
	}

}
