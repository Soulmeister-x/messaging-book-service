package home.oehler.res;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class Books {

    private List<Book> bookList = new ArrayList<Book>();
 
    private Books() {
        bookList.add(new Book(0, "Book of Fire", "Max Maus"));
        bookList.add(new Book(1, "Book of Wind", "Peter Mueller"));
        bookList.add(new Book(2, "Book of Water", "Maria Morales"));
        bookList.add(new Book(3, "Book of Earth", "Simone Namos"));
        bookList.add(new Book(4, "Book of Books", "Alfredo Adlero"));
	}

    // Bill Pugh Thread Safe Singelton
    private static class SingletonHelper {
        private static final Books INSTANCE = new Books();
    }

    public static Books getInstance() {
        return SingletonHelper.INSTANCE;
    }

	public List<Book> getBookList() {
        return bookList;
    }

    public void setBookList(List<Book> bookList) {
        this.bookList = bookList;
    }
    
    public Book getBookById(int id) {
        for (Book b : Books.getInstance().getBookList()) {
            if (b.getBookId() == id)
                return b;
        }
        return null;
    }

    public JSONArray toJsonArray() {
        JSONArray arr = new JSONArray();
        for (Book b : bookList) {
            arr.put(b);
        }

        return arr;
    }

}
