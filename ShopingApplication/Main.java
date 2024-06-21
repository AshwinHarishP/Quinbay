import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, SQLException, NullPointerException {
        Scanner sc = new Scanner(System.in);
        boolean optionFlag = true;
        int accessChoice;

        do {
            System.out.println("Enter your Access: \n 1. Seller \n 2. Customer \n 3. Exit");
            accessChoice = sc.nextInt();

            switch (accessChoice) {
                case 1:
                    Seller seller = new Seller();
                    break;
                case 2:
                    Client client = new Client();
                    break;
                case 3:
                    System.out.println("Process exiting!!!");
                    optionFlag = false;
                    break;
                default:
                    System.out.println("Enter a valid choice");
                    break;
            }
        } while(optionFlag);
    }
}