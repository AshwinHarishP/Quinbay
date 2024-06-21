
import java.util.ArrayList;
import java.util.List;

public class Usercart extends Inventory{
    static ArrayList<Usercart> cart = new ArrayList<>();
    private int id;
    private int quantity;

    public static List<Usercart> getCart() {
        return cart;
    }

    public int getId() {
        return id;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getQuantity(int id) {
        for (Usercart cartItem : cart) {
            if (cartItem.getId() == id) {
                return cartItem.getQuantity();
            }
        }
        return 0;
    }

    public Usercart(int productId, int quantity) {

        this.id = productId;
        this.quantity = quantity;
    }

    public static void viewCart() {
        boolean flag = false;
        for (Usercart cartItem : cart) {
            System.out.println("Product Id: " + cartItem.getId());
            System.out.println("Product Name: " + cartItem.getProductName(cartItem.getId()));
            System.out.println("Product Quantity: " + cartItem.getQuantity());
            System.out.println("Total Price: " + cartItem.getQuantity() * Inventory.getPrice(cartItem.getId()));
            flag = true;
        }
        if(flag == false){
            System.out.println("No items in the cart");
        }
    }

    public String getProductName(int id) {
        for (Inventory prod : products) {
            if (prod.getProductId() == id) {
                return prod.getProductName();
            }
        }
        return "Not found";
    }

}
