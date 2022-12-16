package home.oehler.res;

public class OrderItem {
    private int bookId;
    private int amount;


    public OrderItem() {}
    public OrderItem(int bookId) {this.bookId = bookId; this.amount=1;}
    public OrderItem(int bookId, int amount) {this.bookId = bookId; this.amount = amount;}

    public int getBookId() {return bookId;}
    public void setBookId(int bookId) {this.bookId = bookId;}
    public int getAmount() {return amount;}
    public void setAmount(int amount) {this.amount = amount;}
/*
    public JSONObject toJsonObject() {
        return new JSONObject().createObjectBuilder()
                .add("bookId", bookId)
                .add("amount", amount)
                .build();
    }*/
}
