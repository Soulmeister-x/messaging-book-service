package home.oehler.res;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Order {

    private int orderId;
    private List<OrderItem> orders = new ArrayList<>();

    
    /*** Constructors ***/
    public Order() {
    }
    public Order(int orderId, List<OrderItem> orders) {
        this.orderId = orderId;
        this.orders = orders;
    }
    public Order(int orderId, JSONArray items) {
        this.orderId = orderId;
        for (int i=0; i<items.length(); i++) {
            JSONObject o = items.getJSONObject(i);
            orders.add(new OrderItem(o.getInt("bookId"), o.getInt("amount")));
        }
    }

    /*** Functions ***/

    @Override
    public String toString() {
        String s =
                "Order{" +
                "orderID=" + orderId;
        for (OrderItem i : orders) {
            s += ",{" +
                    "bookId=" + i.getBookId() +
                    ",amount=" + i.getAmount() +
                    "}";
        }
        return s+"}";
    }

    public JSONObject toJsonObject() {
        JSONObject o = new JSONObject();

        o.put("orderId", orderId);
        o.put("orders", new JSONArray(orders));


        return o;
    }

    /*** GETTERS & SETTERS ***/
    public int getOrderId() {
        return orderId;
    }
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
    public List<OrderItem> getOrders() {return orders;}
    public void setOrders(List<OrderItem> orders) {this.orders = orders;}
    /*** ***/

    
}
