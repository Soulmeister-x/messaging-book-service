package home.oehler.res;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class Orders {

    private List<Order> orderList = new ArrayList<>();
    private int newId;

    private Orders() {
        newId = 0;
    }
    
    private static class SingletonHelper {
        private static final Orders INSTANCE = new Orders();
    }

    public static Orders getInstance() {
        return SingletonHelper.INSTANCE;
    }

    public List<Order> getOrderList() {
        return orderList;
    }

    public int getNewId() {
        int maxId=0;
        for (Order o : orderList) {
            if (o.getOrderId()>maxId)
                maxId=o.getOrderId();
        }
        return orderList.isEmpty() ? 0 : maxId+1;
    }


    public int addOrder(Order newOrder) {
        orderList.add(newOrder);
        return newOrder.getOrderId();
    }

    public Order getOrderById(int id) {
        for (Order o : orderList) {
            if (o.getOrderId() == id)
                return o;
        }
        return null;
    }

    public JSONArray toJsonArray() {
        JSONArray arr = new JSONArray();
        for (Order order : orderList) {
            arr.put(order);
        }
        return arr;
    }
    
}
