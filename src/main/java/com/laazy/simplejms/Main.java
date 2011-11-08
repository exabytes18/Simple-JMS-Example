package com.laazy.simplejms;

import com.laazy.simplejms.messages.XYZ;
import com.laazy.simplejms.messages.XYZFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.codehaus.jackson.map.ObjectMapper;

import javax.jms.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

public class Main {
	private static final String USERNAME = "guest";
	private static final String PASSWORD = "guest";
	private static final String TCP_SERVER = "tcp://localhost:61616/";
	private static final String TOPIC_PREFIX = "nodes";
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private static String getHostname() throws UnknownHostException {
		InetAddress addr = InetAddress.getLocalHost();
		return addr.getHostName();
	}

	private static void sender(final Session aSession) throws JMSException, UnknownHostException {
		final Topic myTopic = aSession.createTopic(TOPIC_PREFIX + "." + getHostname());
		final MessageProducer myMessageProducer = aSession.createProducer(myTopic);
		myMessageProducer.setTimeToLive(5000);
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				final XYZ mySystemInformation = XYZFactory.createMessage(1, 2, 3);
				try {
					final String myMessageBody = OBJECT_MAPPER.writeValueAsString(mySystemInformation);
					final Message myMessage = aSession.createTextMessage(myMessageBody);
					myMessageProducer.send(myMessage);
					System.out.println("Sent object: " + mySystemInformation);
				} catch (JMSException | IOException myException) {
					myException.printStackTrace();
				}
			}
		}, 0, 500);
	}

	private static void receiver(final Session aSession) throws JMSException {
		final Topic myTopic = aSession.createTopic(TOPIC_PREFIX + ".*");
		final MessageConsumer myMessageConsumer = aSession.createConsumer(myTopic);
		myMessageConsumer.setMessageListener(new MessageListener() {
			@Override
			public void onMessage(Message aMessage) {
				if (aMessage instanceof TextMessage) {
					try {
						final XYZ myXYZ = OBJECT_MAPPER.readValue(((TextMessage) aMessage).getText(), XYZ.class);
						System.out.println("Received object: " + myXYZ);
					} catch (JMSException | IOException myException) {
						myException.printStackTrace();
					}
				}
			}
		});
	}

	public static void main(String[] args) throws JMSException, UnknownHostException {
		final ConnectionFactory myConnectionFactory = new ActiveMQConnectionFactory(TCP_SERVER);
		final Connection myConnection = myConnectionFactory.createConnection(USERNAME, PASSWORD);
		final Session mySession = myConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		myConnection.start();

		receiver(mySession);
		sender(mySession);
	}
}
