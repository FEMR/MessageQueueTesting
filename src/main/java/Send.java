import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.concurrent.TimeoutException;


public class Send {

    private final static String QUEUE_NAME = "hello";
    private final static String HOST_NAME = "ec2-54-91-104-166.compute-1.amazonaws.com";

    public static void main(String[] argv)
            throws java.io.IOException, TimeoutException {

            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(HOST_NAME);
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            String message = "Hello World!";

            Message object = new Message();
            object.setClassName( "UserRepository" );
            object.setMethodName( "createUser" );
            object.setParameters( "First Name,Last Name,email@gmail.com" );

            Gson gson = new Gson();
            String json = gson.toJson( object );

            channel.basicPublish("", QUEUE_NAME, null, json.getBytes("UTF-8"));
            System.out.println(" [x] Sent '" + json + "'");

            channel.close();
            connection.close();

    }
}