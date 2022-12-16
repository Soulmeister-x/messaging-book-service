package home.oehler.app;

import home.oehler.res.Order;
import home.oehler.res.OrderItem;
import home.oehler.res.Orders;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import home.oehler.res.Books;
import org.json.JSONArray;
import org.json.JSONObject;


import javax.jms.*;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
    public static void main(String[] args) {
        // send POST request to existing queue to create message
        // connect to ActiveMQ service
        Connection conn = null;
        try {
            conn = new ActiveMQConnectionFactory(url).createConnection("admin", "admin");
            conn.start();

            //Creating a non-transactional session to send/receive JMS message.
            Session session = conn.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);

            // Destination represents the queue, we want to send to
            // queue will be created automatically
            Destination destination = session.createQueue("bookshop");

            // create consumer for reading requests
            // create producer for producing responses
            MessageConsumer consumer = session.createConsumer(destination);
            MessageProducer producer = session.createProducer(null);

            Message msg = session.createMessage();


            // receive request
            while ((msg = consumer.receive()) != null) {
                // evaluate request
                String type = msg.getJMSType();

                int reqId = ("request".equals(type.split(":")[0])) ? Integer.parseInt(type.split(":")[1]) : 0;

                /*
                Vorgehensweise Server:
                - empfange Request von Client
                - lese Request-Id von JMSType
                - ggf.
                  - singlerequest: lese id von RequestProperty
                  - multirequest: lese request-data von Message Body ... create Orders

                - baue Response
                - sende Response
                 */


                // create response object
                JSONObject jsonResponse = new JSONObject();

                switch (reqId) {
                    case 0: // getAllBooks
                        // create JSON String
                        jsonResponse.put("books", Books.getInstance().toJsonArray());
                        break;
                    case 1: // getBookById
                        try {
                            jsonResponse.put("book", Books.getInstance()
                                    .getBookById(msg.getIntProperty("bookId"))
                                    .toJsonObject());
                        } catch (NullPointerException e) {
                            jsonResponse.put("error", "404: not found book #"+msg.getIntProperty("bookId"));
                        }
                        break;
                    case 2: // getAllOrders
                        jsonResponse.put("orders", Orders.getInstance().toJsonArray());
                        break;
                    case 3: // getOrderById
                        try {
                            jsonResponse.put("order",Orders.getInstance()
                                    .getOrderById(msg.getIntProperty("orderId"))
                                    .toJsonObject());
                        } catch (NullPointerException e) {
                            jsonResponse.put("error", "404: not found order #"+msg.getIntProperty("orderId"));
                        }

                        break;
                    case 4: // orderBooks
                        if (msg instanceof TextMessage) {
                            TextMessage jsonRequest = (TextMessage) msg;

                            // add order to database
                            int id = Orders.getInstance().addOrder(
                                    new Order(
                                            Orders.getInstance().getNewId(),
                                            new JSONArray(jsonRequest.getText())));

                            jsonResponse.put("orderId", id);
                        }
                        break;
                    default:
                        jsonResponse.put("error","incorrent requestId");
                        // exit client
                        break;
                }

                // send response to reply queue
                Message responseMsg = session.createTextMessage(jsonResponse.toString());

                Destination d = msg.getJMSReplyTo();
                producer.send(d, responseMsg);



            }





        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (JMSException eJms) {
                    eJms.printStackTrace();
                }
            }
        }

    }
}
