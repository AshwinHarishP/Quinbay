import com.mongodb.client.*;
import org.bson.Document;



public class Inventory {

    class mongoOperations {
        static MongoClient mongoClient = null;
        static MongoCollection<Document> collection;
        static MongoCollection<Document> collection1;

        static {
            try {
                mongoClient = MongoClients.create("mongodb://localhost:27017");
                MongoDatabase database = mongoClient.getDatabase("inventoryDB");
                collection = database.getCollection("products");
                collection1 = database.getCollection("category");
            } catch (Exception error) {
                System.out.println("Error in the database: " + error);
            }
        }

        public static void addProduct(String productName, double productPrice, int productStock) {
            try {

                Document filter = new Document("productName", productName);
                Document existingProduct = collection.find(filter).first();
                if (existingProduct == null) {
                    int newProductID = mongoOperations.genreateProductId();

                    Document newProduct = new Document()
                            .append("categoryId", getCategoryId())
                            .append("categoryName", getCategoryName(getCategoryId()))
                            .append("productId", newProductID + 1)
                            .append("productName", productName)
                            .append("productPrice", productPrice)
                            .append("productStock", productStock)
                            .append("status", true);

                    collection.insertOne(newProduct);
                    System.out.println("Product Added Successfully");
                } else {
                    // Product exists, update based on status
                    boolean status = existingProduct.getBoolean("status");

                    if (status) {
                        // Status is true, update price and stock
                        Document update = new Document("$set", new Document()
                                .append("productPrice", productPrice)
                                .append("productStock", productStock));
                        collection.updateOne(filter, update);
                        System.out.println("Product Updated Successfully");
                    } else {
                        Document update = new Document("$set", new Document()
                                .append("productPrice", productPrice)
                                .append("productStock", productStock)
                                .append("status", true));
                        collection.updateOne(filter, update);
                        System.out.println("Product Updated and Status Set to True");
                    }
                }
            } catch (Exception error) {
                System.out.println("Error in updating product: " + error);
            }
        }


        private static Object getCategoryName(int categoryId) {
            Document filterId = new Document("categoryId", categoryId);
            Document result = collection1.find(filterId).first();
            return result.getString("categoryName");
        }


        private static int genreateProductId() {
            long totalCount = collection.countDocuments();
            return (int) totalCount;
        }

        public static void updateStock(int productIdToUpdate, int newStockvalue) {
            try {
                Document stockFilter = new Document("productId", productIdToUpdate);
                Document stockUpdate = new Document("$set", new Document("productStock", newStockvalue));

                collection.updateOne(stockFilter, stockUpdate);
                System.out.println("Stock updated successfuly");

            } catch (Exception error) {System.out.println("Error in updating stock: " + error);}
        }

        public static boolean checkId(int productIdToUpdatePrice) {
            Document filter = new Document("productId", productIdToUpdatePrice).append("status", true);
            Document result = collection.find(filter).first();
            return result != null;
        }


        public static boolean checkStock(int productIdToCheck) {
            Document filter = new Document("productId", productIdToCheck);
            Document result = collection.find(filter).first();
            return (result.getInteger("productStock") == 0);
        }


        public static int getQuantity(int productIdToCheck) {
            Document filter = new Document("productId", productIdToCheck);
            Document result = collection.find(filter).first();
            return result.getInteger("productStock");
        }


        public static void updatePrice(int productIdToUpdatePrice, double newPrice) {
            Document Pricefilter = new Document("productId", productIdToUpdatePrice);
            Document Priceupdate = new Document("$set", new Document("productPrice", newPrice));
            collection.updateOne(Pricefilter, Priceupdate);
            System.out.println("Price updated !!!");
        }


        public static void deleteProduct(int productIdToDelete) {
            Document filter = new Document("productId", productIdToDelete);
            collection.updateOne(filter, new Document("$set", new Document("status", false)));
            System.out.println("Product Deleted !!!");
        }


        public static void findParticularProduct(int prodIdToFind) {
            Document filter = new Document("productId", prodIdToFind).append("status", true);
            Document result = collection.find(filter).first();
            if (result == null) {
                System.out.println("No product id found !!!");
            } else {
                System.out.println("Product Id: " + result.getInteger("productId"));
                System.out.println("Product Name: " + result.getString("productName"));
                System.out.println("Product Price: " + result.getDouble("productPrice"));
                System.out.println("Product Stock: " + result.getInteger("productStock"));
            }

        }

        public static String getProdcutName(int prodId) {
            Document filter = new Document("productId", prodId);
            Document result = collection.find(filter).first();
            return result.getString("productName");
        }


        public static double getPrice(int prodId) {
            Document filter = new Document("productId", prodId);
            Document result = collection.find(filter).first();
            return result.getDouble("productPrice");
        }

        public static boolean isParticularProduct(int prodIdToFind) {
            Document filter = new Document("productId", prodIdToFind).append("status", true);
            Document result = collection.find(filter).first();
            if (result == null) {
                return false;
            }
            return true;

        }

        public static void viewAllProducts() {
            Document filter = new Document("status", true);
            FindIterable<Document> results = collection.find(filter);
            boolean flag = false;
            for (Document doc : results) {
                System.out.println("Product Id: " + doc.getInteger("productId"));
                System.out.println("Product Name: " + doc.getString("productName"));
                System.out.println("Product Price: " + doc.getDouble("productPrice"));
                System.out.println("Product Stock: " + doc.getInteger("productStock"));
                System.out.println("\n");
                flag = true;
            }
            if (!flag) {
                System.out.println("No items available !!!");
            }
        }

        private static int getCategoryId() {
            long categoryId = collection1.countDocuments();
            return (int) categoryId;
        }

        public static void addCategory(String categoryName) {
            try {
                Document filter = new Document("categoryName", categoryName);
                Document existingCategory = collection1.find(filter).first();

                if (existingCategory == null) {
                    int categoryId = getCategoryId() + 1;
                    // Category does not exist, create new category and add product
                    Document newCategory = new Document()
                            .append("categoryId", categoryId)
                            .append("categoryName", categoryName);

                    collection1.insertOne(newCategory);
                    System.out.println("Category Created and Product Added Successfully");

                }
            } catch (Exception error) {
                System.out.println("Error in adding product to category: " + error);
            }
        }
    }
}

