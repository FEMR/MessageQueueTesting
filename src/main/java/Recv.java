import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.rabbitmq.client.*;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeoutException;

public class Recv {

    private final static String QUEUE_NAME = "hello";
    private final static String HOST_NAME = "ec2-54-91-104-166.compute-1.amazonaws.com";
    private final static String SLACK_URL = "https://hooks.slack.com/services/";







    public static void main(String[] argv)
            throws java.io.IOException,
            java.lang.InterruptedException, TimeoutException {

            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost( HOST_NAME );
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                        throws IOException {

                    String message = new String(body, "UTF-8");

                    System.out.println(" [x] Received '" + message + "'");

                    JsonObject object = new JsonObject();
                    object.addProperty("text", message);
                    Gson gson = new Gson();
                    String payloadSTRING = gson.toJson(object);
                    sendToSlack(payloadSTRING);
                }
            };
            channel.basicConsume(QUEUE_NAME, true, consumer);

    }

    private static void sendToSlack(String payload){

        try {

            String webHookURL = "get me from configuration file";
            URL obj = new URL(SLACK_URL + webHookURL);
            HttpsURLConnection slackConnection = (HttpsURLConnection) obj.openConnection();
            slackConnection.setRequestMethod("POST");
            slackConnection.setRequestProperty("Content-type", "application/json");

            //START POST
            slackConnection.setDoOutput(true);
            OutputStream os = slackConnection.getOutputStream();
            os.write(payload.getBytes());
            os.flush();
            os.close();
            //END POST

            int httpResponseCode = slackConnection.getResponseCode();
            System.out.println("POST Response:" + httpResponseCode);




            if (httpResponseCode == HttpURLConnection.HTTP_OK) { //success
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        slackConnection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // print result
                System.out.println(response.toString());
            } else {
                System.out.println("POST request didn't fire");
            }

        }catch (Exception ex){

        }

    }
}