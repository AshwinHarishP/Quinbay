import java.io.*;
import java.util.Scanner;

public class Seller extends Inventory {

    static  Scanner sc = new Scanner(System.in);
    public Seller() throws IOException {
        super();

        System.out.println("Welcome to Seller DashBoard!!!");
        Seller.options();
    }



    public static void options() {
        boolean flag = true;

        do {
            System.out.println("Enter Your choice: \n 1. Add Product \n 2. Update stock \n 3. Update Price \n 4. Delete Product \n 5. Search Product \n 6. View All Product \n 7. Exit");
            int sellerOptions = sc.nextInt();

            switch (sellerOptions) {
                case 1:
                    System.out.println("-----------------------------");
                    System.out.println("Adding Product");
                    System.out.println("Enter Total Number of Products you need to add: ");
                    int totProd = sc.nextInt();
                    if (totProd < 1) {
                        System.out.println("Enter a valid number");
                        break;
                    }

                    boolean checkFlag = true;

                    for (int i = 1; i <= totProd; i++) {

                        System.out.println("Enter Product Category: ");
                        String categroyName = sc.next().toLowerCase();
                        System.out.println("Enter Product name: ");
                        String productName = sc.next().toLowerCase();

                        System.out.println("Enter Product Price: ");
                        double productPrice = sc.nextDouble();
                        if (productPrice < 1) {
                            checkFlag = false;
                            System.out.println("Enter a valid price");
                            break;
                        }

                        System.out.println("Enter Product Stock: ");
                        int productStock = sc.nextInt();
                        if (productStock < 1) {
                            checkFlag = false;
                            System.out.println("Enter a valid stock");
                            break;
                        }

                        boolean nameFlag = false;
                        mongoOperations.addCategory(categroyName);
                        mongoOperations.addProduct(productName, productPrice, productStock);
                        System.out.println("-----------------------------");
                    }
                    break;

                case 2:
                    System.out.println("-----------------------------");
                    System.out.println("Update Stock");
                    System.out.println("Enter Product ID: ");
                    int productIdToUpdate = sc.nextInt();
                    if (productIdToUpdate < 1) {
                        System.out.println("Enter a valid id!!!!");
                        break;
                    }

                    System.out.println("Enter new stock value: ");
                    int newStockvalue = sc.nextInt();
                    if (newStockvalue < 0) {
                        System.out.println("Stock could'nt be negative");
                        break;
                    }

                    mongoOperations.updateStock(productIdToUpdate, newStockvalue);
                    System.out.println("-----------------------------");
                    break;

                case 3:
                    System.out.println("-----------------------------");
                    System.out.println("Updating Price");
                    System.out.println("Enter Product ID: ");
                    int productIdToUpdatePrice = sc.nextInt();
                    if(productIdToUpdatePrice < 1) {
                        System.out.println("Invalid Id");
                        break;
                    }

                    if(!mongoOperations.checkId(productIdToUpdatePrice)){
                        System.out.println("Invalid product ID !!!");
                        break;
                    }

                    System.out.println("Enter new price: ");
                    int newPrice = sc.nextInt();
                    if (newPrice < 1) {
                        System.out.println("Enter a valid price!!!");
                        break;
                    }

                    mongoOperations.updatePrice(productIdToUpdatePrice, newPrice);

                    System.out.println("-----------------------------");
                    break;

                case 4:

                    System.out.println("-----------------------------");
                    System.out.println("Deleting a Product");
                    System.out.println("Enter Product ID: ");
                    int productIdToDelete = sc.nextInt();
                    if (productIdToDelete < 1) {
                        System.out.println("Enter a valid id!!!");
                        break;
                    }

                    mongoOperations.deleteProduct(productIdToDelete);

                    System.out.println("-----------------------------");
                    break;

                case 5:
                    System.out.println("-----------------------------");
                    System.out.println("View Product");
                    System.out.println("Enter product ID: ");
                    int prodId = sc.nextInt();
                    if(prodId < 1) {
                        System.out.println("Invalid Id");
                        break;
                    }

                    mongoOperations.findParticularProduct(prodId);

                    System.out.println("-----------------------------");
                    break;

                case 6:
                    System.out.println("-----------------------------");
                    System.out.println("View All Products");
                    mongoOperations.viewAllProducts();
                    System.out.println("-----------------------------");
                    break;

                case 7:
                    flag = false;
                    System.out.println("Exiting Seller Dashboard");
                    break;

                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
                    break;
            }
        } while (flag);

    }
}