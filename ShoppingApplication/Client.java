import java.sql.*;
import java.util.Scanner;

public class Client extends Inventory{
    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/Shoping";
    private static final String JDBC_USER = "postgres";
    private static final String JDBC_PASSWORD = "1234";
    static  Scanner sc = new Scanner(System.in);

    public Client() throws SQLException {
        super();
        System.out.println("Welcome to Client Dashboard");
        Client.options();
    }

    private static void deleteAllData() {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            String deleteCartSql = "DELETE FROM Cart";
            try (PreparedStatement deleteCartStatement = conn.prepareStatement(deleteCartSql)) {
                int deletedCartRows = deleteCartStatement.executeUpdate();
            }

        } catch (SQLException e) {
            System.out.println("Error deleting data: " + e.getMessage());
        }
    }


    public static void options() throws SQLException {
        boolean clientFlag = true;
        do{
            System.out.println("Enter your choice: \n 1. Search Product \n 2. View All Products \n 3. Purchase Product \n 4. Checkout \n 5. Exit");
            int userChoice = sc.nextInt();

            switch (userChoice){
                case 1:
                    System.out.println("Search Product");
                    System.out.println("Enter Product id: ");
                    int productId = sc.nextInt();
                    if(productId < 1){
                        System.out.println("Enter a valid product id !!!");
                        break;
                    }
                    mongoOperations.findParticularProduct(productId);
                    break;

                case 2:
                    System.out.println("View All Products");
                    mongoOperations.viewAllProducts();
                    break;

                case 3:
                    System.out.println("Purchase Product");
                    System.out.println("Enter Product id: ");
                    int prodId = sc.nextInt();
                    if(prodId < 1){
                        System.out.println("Enter a valid id");
                        break;
                    }
                    if(!mongoOperations.checkId(prodId)){
                        System.out.println("Id not found");
                        break;
                    }

                    if(mongoOperations.checkStock(prodId)){
                        System.out.println("Out of Stock!!!");
                        break;
                    }

                    System.out.println("Enter Quantity: ");
                    int prodQuanity = sc.nextInt();
                    if(prodQuanity < 1){
                        System.out.println("Enter valid quantity");
                        break;
                    }

                    if(mongoOperations.isParticularProduct(prodId)){
                        int Quantity = mongoOperations.getQuantity(prodId);
                        if(Quantity == 0){
                            System.out.println("Out of Stock!!!");
                            break;
                        }
                        if(prodQuanity > Quantity){
                            System.out.println("Quanity is high\n Limit: " + Quantity);
                            break;
                        }

                        mongoOperations.updateStock(prodId, Quantity - prodQuanity);
                        Usercart cart1 = new Usercart(prodId, prodQuanity);

                        Usercart.cart.add(cart1);
                        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
                            String sql = "INSERT INTO Cart(productId, productName, Quantity, Price, totalPrice) VALUES (?, ?, ?, ?, ?)";
                            PreparedStatement statement = conn.prepareStatement(sql);
                            statement.setString(1, "Pid: " + prodId);
                            statement.setString(2, mongoOperations.getProdcutName(prodId));
                            statement.setInt(3, prodQuanity);
                            statement.setDouble(4, mongoOperations.getQuantity(prodId));
                            statement.setDouble(5, mongoOperations.getPrice(prodId) * prodQuanity);
                            int rowsInserted = statement.executeUpdate();
                            if (rowsInserted > 0) {
                                System.out.println("Purchase stored in Cart table successfully.");
                            }
                        } catch (SQLException e) {
                            System.out.println("Error storing purchase in Cart table: " + e.getMessage());
                        }

                        System.out.println("Items added Successfully");
                    }
                    break;


                case 4:
                    System.out.println("Checkout Details: ");
                    Usercart.viewCart();
                    confirmCheckout();
                    break;

                case 5:
                    System.out.println("Exiting dashboard");
                    deleteAllData();
                    clientFlag = false;
                    break;

                default:
                    System.out.println("Enter a valid choice!!!");
            }

        }while(clientFlag);
    }

    private static void confirmCheckout() throws SQLException {
        if (Usercart.cart.size() > 0) {
            System.out.println("Confirm Checkout (y/n)?");
            String confirmation = sc.next().toLowerCase();

            if (confirmation.equalsIgnoreCase("y")) {
                int totalQuantity = 0;
                double totalAmount = 0.0;
                try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
                    String selectSql = "SELECT productId, productname, Quantity, Price FROM Cart";


                    try (
                            PreparedStatement selectStatement = conn.prepareStatement(selectSql);
                            ResultSet resultSet = selectStatement.executeQuery();
                            PreparedStatement insertPurchaseStatement = conn.prepareStatement("INSERT INTO purchaseHistory(orderId, productId, productName, Quantity, Price) VALUES (?, ?, ?, ?, ?)");
                            PreparedStatement insertOrderStatement = conn.prepareStatement("INSERT INTO order_id(orderId, Quantity, totalPrice) VALUES (?, ?, ?)");
                            PreparedStatement deleteStatement = conn.prepareStatement("DELETE FROM Cart")
                    ) {
                        int orderOrderId = 0;
                        while (resultSet.next()) {
                            String productId = resultSet.getString("productId");
                            String productName = resultSet.getString("productName");
                            int quantity = resultSet.getInt("quantity");
                            double price = resultSet.getDouble("Price");

                            totalQuantity += quantity;
                            totalAmount += price;
                            orderOrderId = (getOrderorderID(conn))+1;
                            // Insert into purchaseHistory
                            insertPurchaseStatement.setString(1, "Oid" + orderOrderId);
                            insertPurchaseStatement.setString(2, productId);
                            insertPurchaseStatement.setString(3, productName);
                            insertPurchaseStatement.setInt(4, quantity);
                            insertPurchaseStatement.setDouble(5, price);
                            insertPurchaseStatement.executeUpdate();
                        }
                        // Insert into order_id
                        insertOrderStatement.setString(1, "Oid" + orderOrderId);
                        insertOrderStatement.setInt(2, totalQuantity);
                        insertOrderStatement.setDouble(3, totalAmount);
                        insertOrderStatement.executeUpdate();

                        System.out.println("Data moved from Cart to purchaseHistory and order_id successfully.");

                        deleteStatement.executeUpdate();
                        Usercart.cart.clear();

                    } catch (SQLException e) {
                        System.out.println("Error moving data from Cart to purchaseHistory and order_id: " + e.getMessage());
                    }

                } catch (SQLException e) {
                    System.out.println("Database connection error: " + e.getMessage());
                }

            } else {

                System.out.println("Checkout cancelled.");
            }
        } else {
            System.out.println("Cart is empty!!!");
        }
    }

    public static int getOrderorderID(Connection conn) throws SQLException {
        int totalOrders = 0;

        try (PreparedStatement countTotal = conn.prepareStatement("SELECT COUNT(*) FROM order_id")) {
            try (ResultSet resultSet = countTotal.executeQuery()) {
                if (resultSet.next()) {
                    totalOrders = resultSet.getInt(1);
                } else {
                    System.out.println("No orders found in the order_id table.");
                }
            } catch (SQLException e) {
                System.out.println("Error executing query: " + e.getMessage());
            }
        }

        return totalOrders;
    }

}
