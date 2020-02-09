/**
 * The server creates a DatagramSocket to recieve DatagramPackets through port 69
 * It also creates a DatagramSocket to send a response packet to the port it received a request from
 * Features:
 *  - Runs forever until an invalid packet is recieved (throws exception and quits)
 *  - Parses packets to confirm valid formats ("read request" or "write request")
 *  - Prints out received information and response packet information
 *  - Closes the response socket upon sending the response DatagramPacket
 */
package basic.system;

import java.net.*;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author Lila McIlveen
 * @date February 8, 2020
 */

public class EchoServer {

	// Parsing variables used to check the byte array
	
	private boolean readRequest;
	private boolean writeRequest;
	private boolean file;
	private boolean mode;
	private int zeroCount;

	private DatagramPacket sendPacket, receivePacket;
	private DatagramSocket sendSocket, receiveSocket;

	public EchoServer() {

		try {
			// Construct a DatagramSocket and bind it to port 69
			receiveSocket = new DatagramSocket(69); // Will be used to receive UDP Datagram packets

		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}
	}

	/*
	 * Helper function: Initializes variables used for parsing
	 */

	private void resetParsing() {
		zeroCount = 0;
		readRequest = false;
		writeRequest = false;
		file = false;
		mode = false;
	}

	/*
	 * Helper function: Parses a byte array and checks if it is a read request or
	 * write request
	 */
	private void parseBytes(byte[] data) throws Exception {
		resetParsing(); // initialize variables to default values
		for (int i = 0; i < data.length; i++) {
			if (data[i] == 0x00) {
				zeroCount++;
				if (zeroCount > 3)
					throw new Exception("Invalid Packet");
			} else if (data[i] == 0x01) {
				if (zeroCount == 1 && (!readRequest && !writeRequest))
					readRequest = true;
				else
					throw new Exception("Invalid Packet");
			} else if (data[i] == 0x02) {
				if (zeroCount == 1 && (!readRequest && !writeRequest))
					writeRequest = true;
				else
					throw new Exception("Invalid Packet");
			} else if (!file) {
				if (zeroCount == 1 && (readRequest ^ writeRequest))
					file = true;
				else if (zeroCount > 1 && !file)
					throw new Exception("Invalid Packet");
			} else if (!mode) {
				if (file && zeroCount == 2)
					mode = true;
				else if (!mode && zeroCount > 2)
					throw new Exception("Invalid Packet");
			}
		}
		if (zeroCount != 3)
			throw new Exception("Invalid Packet");
	}

	/*
	 * Helper function: creates a packets of 4 bytes to be sent to the intermediate
	 * host
	 */
	private byte[] createResponsePacket() {
		byte[] responseBytes = new byte[4];
		responseBytes[0] = 0x00;
		responseBytes[2] = 0x00;

		if (readRequest) {
			responseBytes[1] = 0x03;
			responseBytes[3] = 0x01;
		} else if (writeRequest) {
			responseBytes[1] = 0x04;
			responseBytes[3] = 0x00;
		}

		return responseBytes;
	}

	/*
	 * Helper function: Prints information relating to a send request
	 */

	private void processSend(DatagramPacket packet) {
		System.out.println("Server: Sending packet:");
		System.out.println("To host: " + packet.getAddress());
		System.out.println("Destination host port: " + packet.getPort());
		processStatus(packet);
	}

	/*
	 * Helper function: Prints information relating to a receive request
	 */

	private void processReceive(DatagramPacket packet) {
		System.out.println("Server: Packet received:");
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
	 * Receives a packet request from the intermediate host Sends a response packet
	 * back to the intermediate host
	 */
	private void receiveAndEcho() {

		// repeat the following "forever"
		while (true) {
			byte data[] = new byte[20]; // Constructs a DatagramPacket for receiving packets
			receivePacket = new DatagramPacket(data, data.length);
			System.out.println("Server: Waiting for Packet from the intermediate host\n");

			// Wait to receive a request from receiveSocket through the intermediate host
			try {
				System.out.println("Waiting..."); // status
				receiveSocket.receive(receivePacket);
			} catch (IOException e) {
				System.out.print("IO Exception: likely:");
				System.out.println("Receive Socket Timed Out.\n" + e);
				e.printStackTrace();
				System.exit(1); // error, terminate server
			}
			processReceive(receivePacket); // print information regarding the received packet

			// parse the received packet to check whether "read request" or "write &&
			// confirm that the format is valid
			try {
				parseBytes(receivePacket.getData());
			} catch (Exception e) {
				System.out.println("Invalid Packet");
				System.exit(1);
			}

			// create a DatagramSocket to be used for the 4 byte response from an available
			// port
			try {
				sendSocket = new DatagramSocket();
			} catch (SocketException se) {
				se.printStackTrace();
				System.exit(1);
			}

			byte responseBytes[] = createResponsePacket(); // 4 byte response

			// create a DatagramPacket to contain the 4 byte response, sending to the same
			// port as receivePacket
			try {
				sendPacket = new DatagramPacket(responseBytes, responseBytes.length, InetAddress.getLocalHost(),
						receivePacket.getPort());
			} catch (UnknownHostException uhe) {
				System.out.print("Unknown Host Exception: likely:");
				System.out.println("Host not found \n" + uhe);
				uhe.printStackTrace();
				System.exit(1);
			}

			processSend(sendPacket); // print status

			// send the 4 byte response packet to the intermediate host
			try {
				sendSocket.send(sendPacket);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}

			// We're finished, so close the socket!
			sendSocket.close();
		}
	}

	public static void main(String[] args) {
		EchoServer c = new EchoServer();
		c.receiveAndEcho();
	}
}
