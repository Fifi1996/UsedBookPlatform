package mjd.com.usedbookplatform.switchIndex.bookBean;

import java.io.Serializable;

/**
 * Created by IT之旅 on 2016-10-1.
 */
public class bookInfo implements Serializable {
    private String seller_sellbook_isbn;
    private String book_name;
    private String book_version;
    private String book_author;
    private String book_price;
    private String book_time;
    private String book_sell_declare;
    private String seller_register_name;
    private String book_amount;
    private String book_mark;
    private String bookImgUrl;

    public bookInfo() {
    }

    public bookInfo(String seller_sellbook_isbn, String book_name, String book_version, String book_author, String book_price, String book_time,
                    String book_sell_declare, String seller_register_name, String book_amount, String book_mark, String bookImgUrl) {
        this.seller_sellbook_isbn = seller_sellbook_isbn;
        this.book_name = book_name;
        this.book_version = book_version;
        this.book_author = book_author;
        this.book_price = book_price;
        this.book_time = book_time;
        this.book_sell_declare = book_sell_declare;
        this.seller_register_name = seller_register_name;
        this.book_amount = book_amount;
        this.book_mark = book_mark;
        this.bookImgUrl = bookImgUrl;
    }

    @Override
    public String toString() {
        return "bookInfo{" +
                "seller_sellbook_isbn='" + seller_sellbook_isbn + '\'' +
                ", book_name='" + book_name + '\'' +
                ", book_version='" + book_version + '\'' +
                ", book_author='" + book_author + '\'' +
                ", book_price='" + book_price + '\'' +
                ", book_time='" + book_time + '\'' +
                ", book_sell_declare='" + book_sell_declare + '\'' +
                ", seller_register_name='" + seller_register_name + '\'' +
                ", book_amount='" + book_amount + '\'' +
                ", book_mark='" + book_mark + '\'' +
                ", bookImgUrl='" + bookImgUrl + '\'' +
                '}';
    }

    public String getBookImgUrl() {
        return bookImgUrl;
    }

    public void setBookImgUrl(String bookImgUrl) {
        this.bookImgUrl = bookImgUrl;
    }

    public String getBook_mark() {
        return book_mark;
    }

    public void setBook_mark(String book_mark) {
        this.book_mark = book_mark;
    }

    public String getBook_amount() {
        return book_amount;
    }

    public void setBook_amount(String book_amount) {
        this.book_amount = book_amount;
    }

    public String getSeller_sellbook_isbn() {
        return seller_sellbook_isbn;
    }

    public void setSeller_sellbook_isbn(String seller_sellbook_isbn) {
        this.seller_sellbook_isbn = seller_sellbook_isbn;
    }

    public String getBook_time() {
        return book_time;
    }

    public void setBook_time(String book_time) {
        this.book_time = book_time;
    }
    public String getBook_name() {
        return book_name;
    }

    public void setBook_name(String book_name) {
        this.book_name = book_name;
    }

    public String getBook_version() {
        return book_version;
    }

    public void setBook_version(String book_version) {
        this.book_version = book_version;
    }

    public String getBook_author() {
        return book_author;
    }

    public void setBook_author(String book_author) {
        this.book_author = book_author;
    }

    public String getBook_price() {
        return book_price;
    }

    public void setBook_price(String book_price) {
        this.book_price = book_price;
    }

    public String getBook_sell_declare() {
        return book_sell_declare;
    }

    public void setBook_sell_declare(String book_sell_declare) {
        this.book_sell_declare = book_sell_declare;
    }

    public String getSeller_register_name() {
        return seller_register_name;
    }

    public void setSeller_register_name(String seller_register_name) {
        this.seller_register_name = seller_register_name;
    }
}
