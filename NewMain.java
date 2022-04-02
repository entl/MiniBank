import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

public class NewMain {
    static String[] thisAccount;
    static String[] allEmails;
    static String[] allAccountNumbers;
    static String[][] allReceipts;
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        while (true) {
            allEmails = decryption(readFile("emails.txt"));
            allAccountNumbers = decryption(readFile("accountNumbers.txt"));

            System.out.println("\n//================Welcome to MiniBank================\\" + "\\" + "\n");

            System.out.println("\nWould you like to \n1. Register \n2. Login \n3. Exit the program");
            System.out.print("\nChoose option: ");
            switch (sc.nextLine()) {
                case "1":
                    registration();
                    break;
                case "2":
                    login();
                    break;
                case "3":
                    System.exit(0);
                default:
                    break;
            }
        }

    }

    public static void registration() throws IOException {
        boolean menu = true;
        Random random = new Random();
        String[] registrationData = new String[7];

        while (menu) {
            
            System.out.print("\nEnter first name: ");
            registrationData[0] = sc.nextLine(); //adds first name
            
            System.out.print("\nEnter second name: ");
            registrationData[1] = sc.nextLine(); //adds second name

            System.out.print("\nEnter your age: ");
            registrationData[2] = sc.nextLine(); //adds age

            if (Integer.parseInt(registrationData[2]) < 16) { //checks if person older 16
                System.out.println("Registration a bank account is allowed only for people older 16");
                break;
            }

            while (true) {
                System.out.print("\nEnter your email(only gmail is allowed): ");
                registrationData[3] = sc.nextLine();
                if (!registrationData[3].contains("@gmail.com")) { //check for gmail email
                    System.out.println("Use gmail email");
                    continue;
                }
                for (String string : allEmails) { //checks if emails already used
                    if (string.compareTo(registrationData[3])==0) {
                        System.out.println("Email is already registrated");
                        menu = false; /*change to false to break registration loop and go to main method */
                        break;
                    }
                }
                break;
            }

            if (!menu) { //if false break loop
                break;
            }

            while(true){ //creating password
                System.out.print("\nCreate password: ");
                registrationData[4] = sc.nextLine();

                System.out.print("\nRepeat password: ");
                if (registrationData[4].compareTo(sc.nextLine())!=0) { /* if doesn't match skip loop iteration to enter password one more time */
                    System.out.println("Password does not match");
                    continue;
                }
                break;
            }
            registrationData[5] = ""; /* subsitute null with "" */
            for (int i = 0; i < 5; i++) { // generating account number that consists of 5 numbers
                int rand = random.nextInt(9);
                registrationData[5] = registrationData[5] + rand;
            }

            registrationData[6] = "0"; //balance 

            System.out.println(Arrays.toString(registrationData));

            String[] encryptedRegistrationData = encryption(registrationData); //encryptes registration data

            writeFile(encryptedRegistrationData, encryptedRegistrationData[3]+".txt", ""); /* creates an encrypted file with registrated data file name is an encrypted email */
            writeFile(encryption(allEmails), "emails.txt", encryptedRegistrationData[3]); /* adds new emails to the file with all emails */
            writeFile(encryption(allAccountNumbers), "accountNumbers.txt", encryptedRegistrationData[5]); /*  adds new account number to the file with all account numbers*/
            break;
        }
    }

    public static void login() throws IOException {
        try {
            System.out.print("\nEnter email: ");
            thisAccount = decryption(readFile(Base64.getEncoder().encodeToString((sc.nextLine()).getBytes()) + ".txt")); /* encrypts inputted email, reads file, decrypts data stored and adds to the array */

            System.out.print("\nEnter password: ");
            if (sc.nextLine().compareTo(thisAccount[4]) == 0) {
                try {
                    menu();
                } catch (IOException e) {
                    //For some reason throws doesn't work properly
                }
            } else
                System.out.println("Incorrect password");
        } catch (FileNotFoundException e) {
            System.out.println("Email not found");
        }
    }

    public static void menu() throws IOException, FileNotFoundException {
        boolean menu = true;
        while (menu) {
            try {
                thisAccount = decryption(readFile(encryption(thisAccount)[3] + ".txt")); /* updates inforomation in array */
            } catch (Exception e) {
                System.out.println("menu err");
            }
            System.out.print("""
                    \n----------------------------------------
                    1. View balance
                    2. Change email/password
                    3. Deposit
                    4. Withdraw
                    5. Transfer
                    6. History
                    7. Logout
                    """);
            System.out.print("What would you like to do?: ");
            switch (sc.nextLine()) {
                case "1":
                    viewBalance();
                    break;
                case "2":
                    break;
                case "3":
                    deposit();
                    break;
                case "4":
                    withdraw();
                    break;
                case "5":
                    transfer();
                    break;
                case "6":
                    break;
                case "7":
                    menu=false;
                    break;
                default:
                    System.out.println("");
                    break;
            }
        }
    }
    
    // outputs balance of account
    public static void viewBalance() {
        System.out.println("\n\n----------------------");
        System.out.println("Account balance: " + thisAccount[6]);
        System.out.println("Press enter to continue... ");
        sc.nextLine();
    }

    public static void deposit() throws IOException {
        System.out.println("\n\n----------------------");
        System.out.print("Enter an amount to deposit: ");
        int amount = sc.nextInt();
        thisAccount[6] = String.valueOf(Integer.parseInt(thisAccount[6]) + amount);
        sc.nextLine();// blank line
        writeFile(encryption(thisAccount), encryption(thisAccount)[3] + ".txt", "");
        receipts(String.valueOf(amount), "Deposit", "");
        System.out.println("Press enter to continue... ");
        sc.nextLine();
    }

    public static void withdraw() throws IOException {
        System.out.println("\n\n----------------------");
        System.out.print("Enter an amount to withdraw: ");
        int amount = sc.nextInt();
        sc.nextLine(); //blank line
        if (Integer.parseInt(thisAccount[6]) - amount >= 0) {
            thisAccount[6] = String.valueOf(Integer.parseInt(thisAccount[6]) - amount);
            writeFile(encryption(thisAccount), encryption(thisAccount)[3] + ".txt", "");
            receipts(String.valueOf(amount), "Withdraw", "");
            System.out.println("Press enter to continue... ");
            sc.nextLine();
        } else
            System.out.println("Insufficient balance");
    }

    public static void transfer() throws IOException {
        System.out.println("\n\n----------------------");
        System.out.print("Enter an account number: ");
        String accountNumber = sc.nextLine();
        int flag = 0; //position of account number
        for (int i = 0; i < allAccountNumbers.length; i++) {
            try {
                if (allAccountNumbers[i].compareTo(accountNumber)==0) {
                    flag = i;
                    System.out.print("Enter amount to transfer: ");
                    int amount = sc.nextInt();
                    sc.nextLine(); //blank line

                    String[] accountToTransfer = readFile((encryption(allEmails)[flag])+".txt"); /* reads data of account to transfer */
                    accountToTransfer = decryption(accountToTransfer); //decrypts

                    if (Integer.parseInt(thisAccount[6]) - amount >= 0) { //check if it is enough funds
                        thisAccount[6] = String.valueOf(Integer.parseInt(thisAccount[6]) - amount); // subtract from one acc
                        accountToTransfer[6] = String.valueOf(Integer.parseInt(accountToTransfer[6]) + amount); //add to another
                        receipts(String.valueOf(amount), "Transfer", accountToTransfer[5]);
                        writeFile(encryption(thisAccount), encryption(thisAccount)[3]+".txt", ""); //save and encrypt
                        writeFile(encryption(accountToTransfer), encryption(accountToTransfer)[3]+".txt", ""); //save and encrypt
                        System.out.println("Operation successful");
                        System.out.println("Press enter to continue...");
                        sc.nextLine();
                    } else System.out.println("Insufficient balance");
                }
            } catch (InputMismatchException e) {
                System.out.println("Incorrect input");
                sc.nextInt(); //blank line
            }
        }
        if (flag==0) {
            System.out.println("Account number not found");
        }
    }

    public static void history() {
        
    }
        

    // ------------------------backend methods------------------------------------

    public static void receipts(String amount, String transactionType, String toAccount)
            throws NumberFormatException, FileNotFoundException, IOException {
        String now = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()); // current date
        String[] transactionNum = { String.valueOf(Integer.parseInt(readFile("transactionNumber.txt")[0]) + 1) }; /* array
                                                                                                                   because
                                                                                                                   read
                                                                                                                   file
                                                                                                                   requires
                                                                                                                   arrays */
        try { // try to find file with receipts
            String[] allReceiptsTemp = decryption(
                    readFile(Base64.getEncoder().encodeToString(thisAccount[3].getBytes()) + "_receipts.txt")); /* read
                                                                                                                 data
                                                                                                                 from
                                                                                                                 file */

            String[] receiptTemp;

            if (transactionType.compareTo("Transfer") == 0) {
                String[] receipt = { now, transactionNum[0], transactionType, toAccount, amount, "" }; /* if transfer we
                                                                                                        need
                                                                                                        accountNumber */
                receiptTemp = receipt; // can be seen from outer scope
            } else {
                String[] receipt = { now, transactionNum[0], transactionType, amount, "" };
                receiptTemp = receipt;
            }

            String[] updatedReceipts = new String[allReceiptsTemp.length + receiptTemp.length]; /* new array to
                                                                                                contanate old
                                                                                                 receipts and new one */

            System.arraycopy(allReceiptsTemp, 0, updatedReceipts, 0, allReceiptsTemp.length); // copy old receipts
            System.arraycopy(receiptTemp, 0, updatedReceipts, allReceiptsTemp.length, receiptTemp.length); // copy new

            writeFile(encryption(updatedReceipts),
                    Base64.getEncoder().encodeToString(thisAccount[3].getBytes()) + "_receipts.txt", ""); /* update
                                                                                                           receipts in
                                                                                                           the filem*/
            writeFile(transactionNum, "transactionNumber.txt", ""); // saves transaction number (all transactions have
                                                                    // different number)

        } catch (Exception e) { /*
                                 * if file wasn't found further code had same logic as in previous block the
                                 * only difference is in catch block we don't read file with receipts
                                 */
            String[] receiptTemp;
            if (transactionType.compareTo("Transfer") == 0) {
                String[] receipt = { now, transactionNum[0], transactionType, toAccount, amount, "" };
                receiptTemp = receipt;
                System.out.println(Arrays.toString(receipt));
            } else {
                String[] receipt = { now, transactionNum[0], transactionType, amount, "" };
                receiptTemp = receipt;
                System.out.println(Arrays.toString(receipt));
            }
            writeFile(encryption(receiptTemp),
                    Base64.getEncoder().encodeToString(thisAccount[3].getBytes()) + "_receipts.txt", "");
            writeFile(transactionNum, "transactionNumber.txt", "");
        }
    }

    // method is created to encrypt data arrays using base64 encryption
    public static String[] encryption(String[]  data) {
        String[] encrypted = new String[data.length];
        for (int i = 0; i < data.length; i++) { // get each element of data
            String encodedString = Base64.getEncoder().encodeToString(data[i].getBytes()); // encryption
            encrypted[i] = encodedString; // reassign
        }
        return encrypted;
    }

    // method is used for decrypting arrays
    public static String[] decryption(String[] data) {
        String[] decrypted = new String[data.length];
        for (int i = 0; i < data.length; i++) {
            byte[] decodedBytes = Base64.getDecoder().decode(data[i].getBytes()); /* get bytes of string and decode it into readable one */
            String decodedString = new String(decodedBytes); // bytes to string
            decrypted[i] = decodedString;
        }
        return decrypted;
    }

    // method is used to read information from data file and create array with 
    public static String[] readFile(String name) throws FileNotFoundException { 
        File file = new File(name);
        Scanner scanFileLines = new Scanner(file);
        int i = 0;
        while (scanFileLines.hasNextLine()) // checks number of lines to create an array
        {
            scanFileLines.nextLine();
            i++;
        }
        String data[] = new String[i];
        Scanner scanFile = new Scanner(file);
        i = 0;
        while (scanFile.hasNextLine()) {
            data[i] = (scanFile.nextLine()).replace("\n", "").replace("\r", ""); // clears string from trash and adds
            i++;
        }
        scanFile.close();
        scanFileLines.close();
        return data;
    }

    /* method is used to save information to the file
        extraData is used to add one more line to file
        simplifies updating file, because instead of creation of an array
        which length is bigger by 1 you can write new info to the file straightaway. */
    public static void writeFile(String[] data, String name, String extraData) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(name));
        for (int i = 0; i < data.length; i++) {
            writer.write(data[i] + "\n"); // writes each element of array into a file
        }
        if (extraData != "") { // added to write a new email to email data base
            writer.write(extraData + "\n");
        }
        writer.close();
    }
}
