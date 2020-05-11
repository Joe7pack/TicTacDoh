package com.guzzardo.android.willyshmo.tictactoe4;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class SendMessageToRabbitMQTask extends AsyncTask<Object, Void, Void> {
	private ToastMessage mCallingActivity;
	private static Resources mResources;

	@Override
	protected Void doInBackground(Object... values) {
		try {
			
			String hostName = (String)values[0];
			String qName = (String)values[1];
			String exchangeName = (String)values[2];
			String message = (String)values[3];
			mCallingActivity = (ToastMessage)values[4];
			mResources = (Resources)values[5];

			ConnectionFactory connectionFactory = new ConnectionFactory();
			connectionFactory.setHost(hostName);
			connectionFactory.setUsername("JoeG");
			connectionFactory.setPassword("Reese");
			connectionFactory.setVirtualHost("test");
			int portNumber = Integer.valueOf(mResources.getString(R.string.RabbitMQPortNumber));
			connectionFactory.setPort(portNumber);
			Connection connection = connectionFactory.newConnection();
			Channel channel = connection.createChannel();
			
//			channel.exchangeDeclare(EXCHANGE_NAME, "fanout", true);
			channel.queueDeclare(qName, false, false, false, null);

//			channel.basicPublish(EXCHANGE_NAME, QUEUE_NAME, null, tempstr.getBytes());
			channel.basicPublish("", qName, null, message.getBytes());
			writeToLog("SendMessageToRabbitMQTask", "message: " + message + " queue: " + qName);
			channel.close();
			connection.close();
		} catch (Exception e) {
			writeToLog("SendMessageToRabbitMQTask", "Exception: "+e.getMessage()); 			
			//Log.e("SendMessageToRabbitMQTask", e.getMessage());
			mCallingActivity.sendToastMessage(e.getMessage());
		}
		return null;
	}
	
    private static void writeToLog(String filter, String msg) {
    	if ("true".equalsIgnoreCase(mResources.getString(R.string.debug))) {
    		Log.d(filter, msg);
    	}
    }
	

}
