import java.util.ArrayList;


public class Usercart extends Inventory{
    static ArrayList<Usercart> cart = new ArrayList<>();
    private int id;
    private int quantity;


    public int getId() {return id;}

    public int getQuantity() {return quantity;}


    public Usercart(int productId, int quantity) {

        this.id = productId;
        this.quantity = quantity;
    }

    public static void viewCart() {
        boolean flag = false;
        for (Usercart cartItem : cart) {
            System.out.println("Product Id: " + cartItem.getId());
            System.out.println("Product Name: " + mongoOperations.getProdcutName(cartItem.getId()));
            System.out.println("Product Quantity: " + cartItem.getQuantity());
            System.out.println("Total Price: " + cartItem.getQuantity() * mongoOperations.getPrice(cartItem.getId()));
            System.out.println("\n");
            flag = true;
        }
        if(!flag){
            System.out.println("No items in the cart");
        }
    }

    public String getProductName(int id) {return mongoOperations.getProdcutName(id);
    }

}
