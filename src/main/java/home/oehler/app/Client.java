package home.oehler.app;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

/*
 *
 * best way to implement request-response over JMS  
 * 1. create a temporary queue and consumer per client on startup, 
 * 2. set JMSReplyTo property on each message to the temporary queue
 * 3. and then use a correlationID on each message to correlate request messages to response messages
 */

public class Client {
    // URL of JMS Server
    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;


    public static void main(String[] args) {

        try {
            Scanner stdIn = new Scanner(System.in);

            // get JMS connection from server
            Connection connection = new ActiveMQConnectionFactory(url).createConnection();
            connection.start();

            // create session for receiving messages
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // get the queue
            Destination destination = session.createQueue("bookshop");
            Destination retDest = session.createTemporaryQueue();

            // create producer to send requests
            // create consumer to consume responses
            MessageProducer producer = session.createProducer(destination);
            MessageConsumer consumer = session.createConsumer(retDest);

            // create request
            Message msg = session.createMessage();
            msg.setJMSReplyTo(retDest);
            int requestId=0;

            while(true) {
                System.out.println("**********************************\n"
                        + "Which request do you want to send?\n"
                        + "0 - get all books\n"
                        + "1 - get book by id\n"
                        + "2 - get all orders\n"
                        + "3 - get order by id\n"
                        + "4 - order books\n"
                        + "  - exit\n");
                requestId = stdIn.nextInt();

                msg.setJMSType("request:" + requestId);
                switch (requestId) {
                    case 0: // getAllBooks
                        break;
                    case 1: // getBookById
                        System.out.print("Enter Book ID: ");
                        int bookId = stdIn.nextInt();
                        msg.setIntProperty("bookId", bookId);
                        break;

                    case 2: // getAllOrders
                        break;

                    case 3: // getOrderById
                        System.out.print("Enter Order ID: ");
                        int orderId = stdIn.nextInt();
                        msg.setIntProperty("orderId", orderId);
                        break;
                        
                    case 4: // orderBooks
                        JSONArray orderRequest = new JSONArray();
                        do {
                            JSONObject o = new JSONObject();
                            System.out.print("Enter Book ID: ");
                            o.put("bookId", stdIn.nextInt());
                            System.out.print("Enter amount: ");
                            o.put("amount", stdIn.nextInt());
                            orderRequest.put(o);

                            System.out.println("Order another book? [y|n]");
                        } while (stdIn.next().equals("y"));

                        msg = session.createTextMessage(orderRequest.toString());
                        msg.setJMSReplyTo(retDest);
                        msg.setJMSType("request:" + requestId);


                        break;
                    default:
                        connection.close();
                        System.exit(0);
                        break;
                }


                // send request
                producer.send(msg);
                System.out.println("Sent " + msg.getJMSType());

                // receive response [as JSONString]
                Message response = consumer.receive();


                // parse response
                if (response instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) response;
                    System.out.println("Received response:\n'"+textMessage.getText()+"'");
                } else {
                    System.out.println("Received response of type: "+response.getJMSType());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }



    }
}
