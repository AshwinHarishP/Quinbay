
import com.mongodb.client.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Inventory {
    private int productId;
    private String prodcutName;
    private double productPrice;
    private int productStock;
    private boolean flag = true;

    static ArrayList<Inventory> products = new ArrayList<>();
    static ArrayList<Integer> deleteItems = new ArrayList<Integer>();
    static MongoClient mongoClient = null;



    public void readProductsFromFile(String fileName) throws FileNotFoundException {
        Scanner fileReader = new Scanner(new File(fileName));
        while (fileReader.hasNextLine()) {
            String line = fileReader.nextLine();
            String[] productData = line.split(",");

            int prodId = Integer.parseInt(productData[0]);
            String productName = productData[1];
            double productPrice = Double.parseDouble(productData[2]);

            int productStock = Integer.parseInt(productData[3]);
            boolean productFlag = Boolean.parseBoolean(productData[4]);

            Inventory prod = new Inventory(prodId, productName, productPrice, productStock, productFlag);
            prod.setFlag(productFlag);
            productId += 1;
            products.add(prod);
        }

        fileReader.close();
    }
    public static void writeProductsToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("ShopingApplication/src/sellerChoice.txt"))) {
            for (Inventory prod : products) {
                String line = prod.getProductId() + "," +
                        prod.getProductName() + "," +
                        prod.getProductPrice() + "," +
                        prod.getProductStock() + "," +
                        prod.getFlag();
                writer.println(line);
            }
            System.out.println("Products data written to file: " + "ShopingApplication/src/sellerChoice.txt");
        } catch (IOException error) {
            System.err.println("Error writing products file: " + error.getMessage());
        }

    }



    public Inventory(int productId, String prodcutName, double productPrice, int productStock, boolean flag) {
        this.productId = productId;
        this.prodcutName = prodcutName;
        this.productPrice = productPrice;
        this.productStock = productStock;
        this.flag = flag;

    }

    public Inventory() {

    }


    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId){
        this.productId = productId;
    }


    public boolean isParticularid(int id) {
        for (Inventory prod : products) {
            if (prod.getProductId() == id) {
                return true;
            }
        }
        return false;
    }

    public String getProductName(int id) {
        for (Inventory prod : products) {
            if (prod.getProductId() == id) {
                return prod.getProductName();
            }
        }
        return "Product not found!!!";
    }

    public String getProdcutName(int prodId) {
        for (Inventory prod : products) {
            if (prod.getProductId() == prodId){
                return prod.getProductName();
            }
        }
        return "No Product Found";
    }

    public boolean getProductName(String name) {
        for (Inventory prod : products) {
            if (prod.getProductName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }


    public static double getPrice(int id) {
        for (Inventory prod : products) {
            if (prod.getProductId() == id) {
                System.out.println("Product Price: " + prod.getProductPrice());
                return prod.getProductPrice();
            }
        }
        return 1;
    }


    public double getProductPrice() {
        return productPrice;
    }


    public double getProductPrice(int id) {
        for (Inventory prod : products) {
            if (prod.getProductId() == id) {
                return prod.getProductPrice();
            }
        }
        return 1;
    }



    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public int getProductStock() {
        return productStock;
    }

    public static int getProductStock(int id) {
        for (Inventory prod : products) {
            if (prod.getProductId() == id && prod.getFlag()) {
                return prod.getProductStock();
            }
        }
        System.out.println("Product not found");
        return 0;
    }


    public static boolean getParticular(int id) {
        for (Inventory prod : products) {
            if (prod.getProductId() == id && prod.getFlag() == true) {
                System.out.println("Product Name: " + prod.getProductName(id));
                System.out.println("Product Id: " + prod.getProductId());
                System.out.println("Product Price: " + prod.getProductPrice());
                System.out.println("Product Stock: " + prod.getProductStock());
                return true;
            }
        }
        System.out.println("Product Id not found");
        return false;
    }

    public static void viewAllproducts() {
        boolean flag = false;
        for (Inventory prod : products) {
            if (prod.getFlag()) {
                flag = true;
                System.out.println("\n");
                System.out.println("Product Id: " + prod.getProductId());
                System.out.println("Product Name: " + prod.getProductName());
                System.out.println("Product Price: " + prod.getProductPrice());
                System.out.println("Product Stock: " + prod.getProductStock());
            }
        }
        if (!flag) System.out.println("No items");
    }

    public String getProductName() {
        return prodcutName;
    }

    public void setProductStock(int productStock) {
        this.productStock = productStock;
    }



    public static boolean updateStock(int id, int newStockvalue) {
        for (Inventory prod : products) {
            if (prod.getProductId() == id) {
                prod.setProductStock(newStockvalue);
                System.out.println("Stock Updated Successfully");
                Seller.writeProductsToFile();
                return true;
            }
        }
        System.out.println("Id dosent Exist");
        return false;

    }

    public static boolean updatePrice(int id, int newPrice) {
        if (newPrice < 1) {
            System.out.println("Enter a valid Price!!!");
            return false;
        }
        for (Inventory prod : products) {
            if (prod.getProductId() == id) {
                prod.setProductPrice(newPrice);
                System.out.println("Price Updated Successfully");
                Seller.writeProductsToFile();
                return true;
            }
        }
        System.out.println("Id cannot be found");
        return false;
    }

    public boolean getFlag() {
        return flag;
    }

    public static boolean getFlag(int id) {
        for (Inventory prod : products) {
            if (prod.getProductId() == id) {
                return prod.getFlag();
            }
        }
        return false;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public void setStockupdate(int productStock){
        this.productStock = productStock;
    }

    public int getStockupdate(){
        return productStock;
    }

    public static boolean deleteProduct(int id) {
        for (Inventory prod : products) {
            if (prod.getProductId() == id) {
                if(prod.getFlag() == false){
                    System.out.println("Product already deleted");
                    return false;
                }
                prod.setFlag(false);
                System.out.println("Deleted Sucessfully");
                Seller.writeProductsToFile();
                return true;
            }
        }
        System.out.println("Product not found!!!");
        return false;
    }
}
