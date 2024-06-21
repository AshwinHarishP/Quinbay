import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import java.io.*;
import java.util.Scanner;

public class Seller extends Inventory {

    static  Scanner sc = new Scanner(System.in);


    public Seller() throws IOException {
        super();

        System.out.println("Welcome to Seller DashBoard!!!");
        readProductsFromFile("ShopingApplication/src/sellerChoice.txt");
        Seller.options();
    }

    private static int generateProductId() {
        int maxId = 0;
        for (Inventory prod : products) {
            if (prod.getProductId() > maxId) {
                maxId = prod.getProductId();
            }
        }
        return maxId + 1;
    }



    public static void options() {
        boolean flag = true;
        MongoClient mongoClient = null;
        try{
            mongoClient = MongoClients.create("mongodb://localhost:27017");
            MongoDatabase database = mongoClient.getDatabase("inventoryDB");
            MongoCollection<Document> collection = database.getCollection("products");

            do {
                System.out.println("Enter Your choice: \n 1. Add Product \n 2. Update stock \n 3. Update Price \n 4. Delete Product \n 5. Search Product \n 6. View All Product \n 7. Exit");
                int sellerOptions = sc.nextInt();
                int productId = generateProductId();

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
                                productId -= 1;
                                break;
                            }

                            System.out.println("Enter Product Stock: ");
                            int productStock = sc.nextInt();
                            if (productStock < 1) {
                                checkFlag = false;
                                System.out.println("Enter a valid stock");
                                productId -= 1;
                                break;
                            }



                            boolean nameFlag = false;
                            for (Inventory prod : products) {
                                if (prod.getProductName().equalsIgnoreCase(productName)) {
                                    prod.setFlag(true);
                                    prod.setProductStock(productStock);
                                    prod.setProductPrice(productPrice);
                                    nameFlag = true;

                                    Document filter = new Document("productName", productName);
                                    Document update = new Document("$set", new Document("status", true)
                                            .append("productStock", productStock)
                                            .append("productPrice", productPrice));

                                    collection.updateOne(filter, update);

                                    break;
                                }
                            }

                            if (!nameFlag) {
                                Inventory prod = new Inventory(productId, productName, productPrice, productStock, true);

                                Document productDocument = new Document();
                                productDocument.append("productId", productId)
                                        .append("productName", productName)
                                        .append("productPrice", productPrice)
                                        .append("productStock", productStock)
                                        .append("status", true);


                                collection.insertOne(productDocument);
                                addCategory(categroyName);
                                products.add(prod);
                            }
                            productId += 1;
                        }
                        if(checkFlag) {
                            Thread saveThread = new Thread(() -> writeProductsToFile());
                            saveThread.start();
                            System.out.println("Product(s) Added Successfully. Saving data asynchronously...");
                        }

                        System.out.println("-----------------------------");
                        break;

                    case 2:
                        System.out.println("-----------------------------");
                        System.out.println("Update Stock");
                        System.out.println("Enter Product ID: ");
                        int productIdToUpdate = sc.nextInt();
                        if(productIdToUpdate < 1){
                            System.out.println("Enter a valid id!!!!");
                            break;
                        }

                        System.out.println("Enter new stock value: ");
                        int newStockvalue = sc.nextInt();
                        if(newStockvalue < 0){
                            System.out.println("Stock could'nt be negative");
                            break;
                        }

                        Document stockFilter = new Document("productId", productIdToUpdate);
                        Document stockUpdate = new Document("$set", new Document("productStock", newStockvalue));

                        collection.updateOne(stockFilter, stockUpdate);

                        Inventory.updateStock(productIdToUpdate, newStockvalue);
                        System.out.println("-----------------------------");
                        break;

                    case 3:
                        System.out.println("-----------------------------");
                        System.out.println("Updating Price");
                        System.out.println("Enter Product ID: ");
                        int productIdToUpdatePrice = sc.nextInt();
                        if(productIdToUpdatePrice < 1 && Inventory.getFlag(productIdToUpdatePrice)){
                            System.out.println("Enter a valid id!!!");
                            break;
                        }

                        System.out.println("Enter new price: ");
                        int newPrice = sc.nextInt();
                        if(newPrice < 1){
                            System.out.println("Enter a valid price!!!");
                            break;
                        }
                        Document Pricefilter = new Document("productId", productIdToUpdatePrice);
                        Document Priceupdate = new Document("$set", new Document("productPrice", newPrice));

                        collection.updateOne(Pricefilter, Priceupdate);
                        Inventory.updatePrice(productIdToUpdatePrice, newPrice);
                        System.out.println("-----------------------------");
                        break;

                    case 4:
                        System.out.println("-----------------------------");
                        System.out.println("Deleting a Product");
                        System.out.println("Enter Product ID: ");
                        int productIdToDelete = sc.nextInt();
                        if(productIdToDelete < 1){
                            System.out.println("Enter a valid id!!!");
                            break;
                        }
                        Thread deleteThread = new Thread(() -> {
                            Inventory.deleteItems.add(productIdToDelete);
                            if(Inventory.deleteProduct(productIdToDelete)){
                                Document deleteFilter = new Document("productId", productIdToDelete);
                                Document deleteUpdate = new Document("$set", new Document("status", false));
                                collection.updateOne(deleteFilter, deleteUpdate);
                            }
                        });
                        deleteThread.start();
                        System.out.println("Product deletion initiated asynchronously...");
                        System.out.println("-----------------------------");
                        break;

                    case 5:
                        System.out.println("-----------------------------");
                        System.out.println("View Product");
                        System.out.println("Enter product ID: ");
                        int prodId = sc.nextInt();
                        Inventory.getParticular(prodId);
                        System.out.println("-----------------------------");
                        break;

                    case 6:
                        System.out.println("-----------------------------");
                        System.out.println("View All Products");
                        Inventory.viewAllproducts();
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
        } catch (Exception error) {
            System.out.println("Error in the database: " + error);
        }
        finally {
            if(mongoClient != null) mongoClient.close();
        }
        
        
    }

    private static void addCategory(String categoryName) {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("inventoryDB");
        MongoCollection<Document> categoriesCollection = database.getCollection("category");

        try {
            Document existingCategory = categoriesCollection.find(Filters.eq("categoryName", categoryName)).first();
            
            if (existingCategory == null) {
                Document categoryDocument = new Document();
                long catId = categoriesCollection.countDocuments();
                categoryDocument.append("categoryName", categoryName);
                categoryDocument.append("categoryId", catId+1);

                categoriesCollection.insertOne(categoryDocument);

                System.out.println("Category '" + categoryName + "' added successfully.");
            }
        } catch (Exception e) {
            System.err.println("Error adding category: " + e.getMessage());
            e.printStackTrace();
        } finally {
            mongoClient.close();
        }
    }

}