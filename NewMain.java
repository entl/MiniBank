//written by Vorobyov Maksym
//Saturday April 02 2022

/*	The NewMain class implements an application that 
 *  allow user to register a bank account and perform
 * different operations such deposit, withdraw, transfer,
 * check history, view balance and change account credits.
 * All information is stored in multiple data files.
 * */
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

public class NewMain {
    static String[] thisAccount;
    static String[] allEmails;
    static String[] allAccountNumbers;
    static String[][] allReceipts = new String[50][5]; // declared for 50 pos. Hope it will be enough
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

    }//end main method

    public static void registration() throws IOException {
        boolean menu = true;
        Random random = new Random();
        String[] registrationData = new String[7];

        while (menu) {

            System.out.print("\nEnter first name: ");
            registrationData[0] = sc.nextLine(); // adds first name

            System.out.print("\nEnter second name: ");
            registrationData[1] = sc.nextLine(); // adds second name

            System.out.print("\nEnter your birthdate(ex. 19.02.2000): ");
            registrationData[2] = sc.nextLine(); // adds age
            try {
                if (Calendar.getInstance().get(Calendar.YEAR)
                        - Integer.parseInt(registrationData[2].substring(registrationData[2].length() - 4)) < 16) { // checks
                                                                                                                    // if
                                                                                                                    // person
                                                                                                                    // older
                                                                                                                    // 16
                    System.out.println("Registration of the bank account is allowed only for people older 16");
                    break;
                }
            } catch (Exception e) {
                System.out.println("Incorrect birthdate");
                break;
            }
            while (true) {
                System.out.print("\nEnter your email(only gmail is allowed): ");
                registrationData[3] = sc.nextLine();
                if (!registrationData[3].contains("@gmail.com")) { // check for gmail email
                    System.out.println("Use gmail email");
                    continue;
                }
                for (String string : allEmails) { // checks if emails already used
                    if (string.compareTo(registrationData[3]) == 0) {
                        System.out.println("Email is already registrated");
                        menu = false; /* change to false to break registration loop and go to main method */
                        break;
                    }
                }
                break;
            }

            if (!menu) { // if false break loop
                break;
            }

            while (true) { // creating password
                System.out.print("\nCreate password: ");
                registrationData[4] = sc.nextLine();

                System.out.print("\nRepeat password: ");
                if (registrationData[4].compareTo(sc.nextLine()) != 0) { /*
                                                                          * if doesn't match skip loop iteration to
                                                                          * enter password one more time
                                                                          */
                    System.out.println("Password does not match");
                    continue;
                }
                else break;
            }
            boolean Number = true;
            while (Number) { // loop is necessary to exclude chance of account number coincidences
                registrationData[5] = ""; /* subsitute null with "" */
                for (int i = 0; i < 5; i++) { // generating account number that consists of 5 numbers
                    int rand = random.nextInt(9);
                    registrationData[5] = registrationData[5] + rand;
                }
                if (allAccountNumbers.length!=0) {
                    for (String string : allAccountNumbers) {
                        if (string.compareTo(registrationData[5]) == 0) {
                            System.out.println("if");
                            break;
                        } 
                        else {
                            Number = false;
                            break;
                        }
                    }
                } else break;
                if (!Number) {
                    break;
                }
            }

            registrationData[6] = "0"; // balance

            System.out.println(Arrays.toString(registrationData));

            String[] encryptedRegistrationData = encryption(registrationData); // encryptes registration data

            writeFile(encryptedRegistrationData, encryptedRegistrationData[5] + ".txt", ""); /*
                                                                                              * creates an encrypted
                                                                                              * file with registrated
                                                                                              * data file name is an
                                                                                              * encrypted email
                                                                                              */
            File receiptFile = new File(encryptedRegistrationData[5] + "_receipts.txt");
            receiptFile.createNewFile();
            writeFile(encryption(allEmails), "emails.txt", encryptedRegistrationData[3]); /*
                                                                                           * adds new emails to the file
                                                                                           * with all emails
                                                                                           */
            writeFile(encryption(allAccountNumbers), "accountNumbers.txt", encryptedRegistrationData[5]); /*
                                                                                                           * adds new
                                                                                                           * account
                                                                                                           * number to
                                                                                                           * the file
                                                                                                           * with all
                                                                                                           * account
                                                                                                           * numbers
                                                                                                           */
            System.out.println("Account successfully registered");
            break;
        }
    }//end registration method

    public static void login() throws IOException {
        try {
            System.out.print("\nEnter email: ");
            String tempEmail = sc.nextLine();
            int flag = 0;
            for (int i = 0; i < allEmails.length; i++) {
                if(allEmails[i]==null){
                    continue;
                }
                if (allEmails[i].compareTo(tempEmail)==0) {
                    flag = i;
                }
            }
            thisAccount = decryption(readFile(Base64.getEncoder().encodeToString(allAccountNumbers[flag].getBytes())
                    + ".txt")); /*
                                 * encrypts inputted email, reads file, decrypts data stored and adds to the
                                 * array
                                 */

            System.out.print("\nEnter password: ");
            if (sc.nextLine().compareTo(thisAccount[4]) == 0) {
                try {
                    menu();
                } catch (IOException e) {
                    // For some reason throws doesn't work properly
                }
            } else
                System.out.println("Incorrect password");
        } catch (FileNotFoundException e) {
            System.out.println("Email not found");
        }
    }//end login method

    public static void menu() throws IOException, FileNotFoundException {
        boolean menu = true;
        while (menu) {
            try {
                thisAccount = decryption(readFile(encryption(thisAccount)[5] + ".txt")); /*
                                                                                          * updates inforomation in
                                                                                          * array
                                                                                          */
            } catch (Exception e) {
                System.out.println("menu err");
            }
            System.out.print("""
                    \n----------------------------------------
                    1. View balance
                    2. View account details
                    3. Change email/password
                    4. Deposit
                    5. Withdraw
                    6. Transfer
                    7. History
                    8. Logout
                    """);
            System.out.print("What would you like to do?: ");
            switch (sc.nextLine()) {
                case "1":
                    viewBalance();
                    break;
                case "2":
                    viewAccountCredits();
                    break;
                case "3":
                    changeAccountCredits();
                    break;
                case "4":
                    deposit();
                    break;
                case "5":
                    withdraw();
                    break;
                case "6":
                    transfer();
                    break;
                case "7":
                    history();
                    break;
                case "8":
                    menu = false;
                    break;
                default:
                    System.out.println("");
                    break;
            }
        }
    }//end menu method

    // outputs balance of account
    public static void viewBalance() {
        System.out.println("\n\n----------------------------------------");
        System.out.println("Account balance: " + thisAccount[6]);
        System.out.println("Press enter to continue... ");
        sc.nextLine();
    }//end viewBalance method

    public static void viewAccountCredits() {
        System.out.println("\n----------------------------------------");
        System.out.println("First name: " + thisAccount[0]);
        System.out.println("Second name: " + thisAccount[1]);
        System.out.println("Birthdate: " + thisAccount[2]);
        System.out.println("Email: " + thisAccount[3]);
        System.out.println("Password: " + "*******");
        System.out.println("Account number: " + thisAccount[5]);
        System.out.println("Press enter to continue... ");
        sc.nextLine();
    }//end viewAccountCredits method

    public static void changeAccountCredits() throws IOException {
        System.out.println("\n----------------------------------------");
        System.out.println("1. Change email\n2. Change password");
        System.out.print("\nChoose option: ");
        switch (sc.nextLine()) {
            case "1":
                Boolean isFound = false;
                System.out.print("\nEnter password: ");
                if (sc.nextLine().compareTo(thisAccount[4]) == 0) { // to change info password is necessary
                    System.out.print("\nEnter new email: ");
                    String newEmail = sc.nextLine();
                    System.out.print("\nEnter email again: ");
                    for (String string : allEmails) { // checks if email is free
                        if (string == null) {
                            continue;
                        } else {
                            if (newEmail.compareTo(string) == 0) {
                                isFound = true;
                                break;
                            }
                        }
                    }
                    if (isFound) {
                        System.out.println("Email already taken");
                        break;
                    }
                    if (!newEmail.contains("@gmail.com")) { // emails have to be gmail
                        System.out.println("Only @gmail.com mails allowed");
                        break;
                    }
                    if (sc.nextLine().compareTo(newEmail) == 0) {
                        int flag = 0;
                        for(int i = 0; i < allEmails.length; i++){
                            if (allEmails[i]==null) {
                                continue;
                            }
                            if (allEmails[i].compareTo(thisAccount[3])==0) {
                                flag=i;
                                break;
                            }
                        }
                        thisAccount[3] = newEmail;
                        allEmails[flag] = newEmail;
                        writeFile(encryption(thisAccount), encryption(thisAccount)[5] + ".txt", ""); // updates account
                                                                                                     // information
                        writeFile(encryption(allEmails), "emails.txt", "");
                        System.out.println("Your account credits were successfully changed");
                    } else {
                        System.out.println("Emails do not match");
                        break;
                    }
                } else {
                    System.out.println("Passwords do not match");
                    break;
                }
                break;
            case "2":
                System.out.print("\nEnter password: ");
                if (sc.nextLine().compareTo(thisAccount[4]) == 0) { // to change info password is necessary
                    System.out.print("\nEnter new password: ");
                    String newPassword = sc.nextLine();
                    System.out.print("\n Enter password again: ");
                    if (sc.nextLine().compareTo(newPassword) == 0) { // check for pwd matching
                        thisAccount[4] = newPassword; // updates info
                        writeFile(encryption(thisAccount), encryption(thisAccount)[5] + ".txt", ""); // saves update
                        System.out.println("Your account credits were successfully changed");
                    } else {
                        System.out.println("Passwords do not match");
                        break;
                    }
                } else {
                    System.out.println("Passwords do not match");
                    break;
                }
                break;
            default:
                break;
        }
    }//change changeAccountCredits method

    public static void deposit() throws IOException {
        System.out.println("\n\n----------------------------------------");
        System.out.print("Enter an amount to deposit: ");
        try {
            double amount = sc.nextDouble();
            thisAccount[6] = String.valueOf(Double.parseDouble(thisAccount[6]) + amount); // add to balance
            sc.nextLine();// blank line
            writeFile(encryption(thisAccount), encryption(thisAccount)[5] + ".txt", "");// update information
            receipts(String.valueOf(amount), "Deposit ", thisAccount[5]);// create receipt and output it
            System.out.println("Press enter to continue... ");
            sc.nextLine();
        } catch (Exception e) {
            System.out.println("Incorrect input");
        }
    }//end deposit method

    public static void withdraw() throws IOException {
        System.out.println("\n\n----------------------------------------");
        System.out.print("Enter an amount to withdraw: ");
        try {
            double amount = sc.nextDouble();
            sc.nextLine(); // blank line
            if (Double.parseDouble(thisAccount[6]) - amount >= 0) { // check if balance sufficient
                thisAccount[6] = String.valueOf(Double.parseDouble(thisAccount[6]) - amount); // subtract amount
                writeFile(encryption(thisAccount), encryption(thisAccount)[5] + ".txt", ""); // update information
                receipts(String.valueOf(amount), "Withdraw", thisAccount[5]); // create receipt and output it
                System.out.println("Press enter to continue... ");
                sc.nextLine();
            } else
                System.out.println("Insufficient balance");
        } catch (Exception e) {
            System.out.println("Incorrect input");
        }
    } //end withdraw method

    public static void transfer() throws IOException {
        System.out.println("\n\n----------------------------------------");
        System.out.print("Enter an account number: ");
        String accountNumber = sc.nextLine();
        int flag = 0; // position of account number
        if (accountNumber.compareTo(thisAccount[5]) != 0) {
            for (int i = 0; i < allAccountNumbers.length; i++) {
                try {
                    if (allAccountNumbers[i].compareTo(accountNumber) == 0) {
                        flag = i;
                        System.out.print("Enter amount to transfer: ");
                        try {
                            double amount = sc.nextDouble();
                            sc.nextLine(); // blank line

                            String[] accountToTransfer = readFile((encryption(allAccountNumbers)[flag]) + ".txt"); /*
                                                                                                            * reads data
                                                                                                            * of
                                                                                                            * account to
                                                                                                            * transfer
                                                                                                            */
                            accountToTransfer = decryption(accountToTransfer); // decrypts

                            if (Double.parseDouble(thisAccount[6]) - amount >= 0) { // check if it is enough funds
                                thisAccount[6] = String.valueOf(Double.parseDouble(thisAccount[6]) - amount); // subtract
                                                                                                              // from
                                                                                                              // one acc
                                accountToTransfer[6] = String
                                        .valueOf(Double.parseDouble(accountToTransfer[6]) + amount); // add
                                                                                                     // to
                                                                                                     // another
                                receipts(String.valueOf(amount), "Transfer", accountToTransfer[5]);
                                writeFile(encryption(thisAccount), encryption(thisAccount)[5] + ".txt", ""); // save and
                                                                                                             // encrypt
                                writeFile(encryption(accountToTransfer), encryption(accountToTransfer)[5] + ".txt", ""); // save
                                                                                                                         // and
                                                                                                                         // encrypt
                                System.out.println("Operation successful");
                                System.out.println("Press enter to continue...");
                                sc.nextLine();
                            } else
                                System.out.println("Insufficient balance");
                        } catch (Exception e) {
                            System.out.println("Incorrect input");
                            sc.nextInt(); // blank line
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Incorrect input");
                    sc.nextInt(); // blank line
                }
            }
            if (flag == 0) {
                System.out.println("Account number not found");
            }
        } else {
            System.out.println("You cannot transfer money to own account");
        }
    }// end transfer method

    public static void history() throws FileNotFoundException {
        String[] allReceiptsTemp = decryption(readFile(encryption(thisAccount)[5] + "_receipts.txt"));
        int j2 = 0;
        for (int i = 0; i < allReceipts.length; i++) {
            for (int j = 0; j < 5; j++) {
                try {
                    allReceipts[i][j] = allReceiptsTemp[j2];
                    j2++;
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        }
        System.out.println("----------------------------------------");
        System.out.print("""
                \n
                1. View all history
                2. Search for transaction
                3. Sort transations
                """);
        System.out.print("\nChoose option: ");
        switch (sc.nextLine()) {
            case "1":
                viewAll(true);
                break;
            case "2":
                System.out.println("----------------------------------------");
                System.out.println(
                        "1. Search by date \n2. Search by transaction type\n3. Search by account No. \n4. Search by transaction No.");
                System.out.print("\nChoose searching criteria: ");
                switch (sc.nextLine()) {
                    case "1":
                        System.out.print("Enter date (ex. 30/01/2000): ");
                        search(sc.nextLine(), 0);
                        break;
                    case "2":
                        System.out.print("Enter transaction type (Deposit/Withdraw/Transfer): ");
                        search(sc.nextLine(), 2);
                        break;
                    case "3":
                        System.out.print("Enter account No: ");
                        search(sc.nextLine(), 3);
                        break;
                    case "4":
                        System.out.print("Enter transtaction No: ");
                        search(sc.nextLine(), 1);
                        break;
                    default:
                        break;
                }
                break;
            case "3":
                System.out.println("----------------------------------------");
                System.out.println("\n1. Sort by date \n2. Sort by amount");
                System.out.print("\nChoose option: ");
                switch (sc.nextLine()) {
                    case "1":
                        sorting(1);
                        break;
                    case "2":
                        sorting(4);
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }// end history method

    public static void viewAll(boolean direction) {
        System.out.println("\n----------------------------------------");
        System.out.println("\tDate \t      Trans. No     Type \tAccount No\tAmount");
        if (direction) {// if true display from first element to last
            for (int i = 0; i < allReceipts.length; i++) {
                if (allReceipts[i][0] == null) {
                    continue;
                }
                for (int j = 0; j < allReceipts[0].length; j++) {
                    if (j != 4) {
                        System.out.print(allReceipts[i][j] + "\t  ");
                    } else
                    System.out.println("\t" + allReceipts[i][j] + "\t  ");
                }
                System.out.println("");
            }
        } else {// if false backwards. This block simplifies sorting data. You don't need to
        // create 2 sorting methods which with different order
        for (int i = allReceipts.length - 1; i > 0; i--) {
            if (allReceipts[i][0] == null) {
                continue;
            }
            for (int j = 0; j < allReceipts[0].length; j++) {
                if (j != 4) {
                    System.out.print(allReceipts[i][j] + "\t  ");
                } else
                System.out.println("\t" + allReceipts[i][j] + "\t  ");
            }
            System.out.println("");
        }
    }
} //end viewAll method

// ------------------------backend methods------------------------------------

public static void search(String criteria, int ArrPos) {
    String[][] filtetedReceipts = new String[allReceipts.length][5];
        int j3 = 0;
        for (int i = 0; i < allReceipts.length; i++) {
            if (allReceipts[i][ArrPos] == null) {
                continue;
            }
            if (allReceipts[i][ArrPos].contains(criteria)) {
                filtetedReceipts[j3] = allReceipts[i];
                j3++;
            }
        }
        if (filtetedReceipts[0][0] != null) {
            System.out.println("\n----------------------------------------");
            System.out.println("\tDate \t      Trans. No     Type \tAccount No\tAmount");
            for (int i = 0; i < filtetedReceipts.length; i++) {
                if (filtetedReceipts[i][0] == null) {
                    continue;
                }
                for (int j = 0; j < filtetedReceipts[0].length; j++) {
                    if (j != 4) {
                        System.out.print(filtetedReceipts[i][j] + "\t  ");
                    } else
                        System.out.println("\t" + filtetedReceipts[i][j] + "\t  ");
                }
                System.out.println("");
            }
        } else {
            System.out.println("\nNo transactions found");
        }
    }//end seacrch method

    public static void sorting(int criteria) {
        for (int i = 0; i < allReceipts.length; i++) {
            for (int j = 0; j < allReceipts.length - i - 1; j++) {
                if (allReceipts[j + 1][0] == null) {
                    continue;
                }
                if (Double.parseDouble(allReceipts[j][criteria]) > Double
                        .parseDouble(allReceipts[j + 1][criteria])) { /*
                                                                       * comparing
                                                                       * Transaction
                                                                       * number not date
                                                                       * because it is
                                                                       * easier. The
                                                                       * result will be
                                                                       * the same because
                                                                       * the less number
                                                                       * the earlier
                                                                       * transaction was
                                                                       * made
                                                                       */
                    String[] temp = allReceipts[j];
                    allReceipts[j] = allReceipts[j + 1];
                    allReceipts[j + 1] = temp;
                }
            }
        }
        if (criteria == 1) { // display from earliest to latest
            viewAll(true);
        } else {// display from biggest to smallest
            viewAll(false);
        }
    }//end sorting methid

    public static void receipts(String amount, String transactionType, String toAccount)
            throws NumberFormatException, FileNotFoundException, IOException {
        String now = new SimpleDateFormat("dd/MM/yyyy HH.mm").format(new Date()); // current date
        String[] transactionNum = { String.valueOf(Integer.parseInt(readFile("transactionNumber.txt")[0]) + 1) }; /*
                                                                                                                   * array
                                                                                                                   * because
                                                                                                                   * read
                                                                                                                   * file
                                                                                                                   * requires
                                                                                                                   * arrays
                                                                                                                   */
        String[] allReceiptsTemp = decryption(
                readFile(Base64.getEncoder().encodeToString(thisAccount[5].getBytes()) + "_receipts.txt")); /*
                                                                                                             * read
                                                                                                             * data
                                                                                                             * from
                                                                                                             * file
                                                                                                             */

        String[] receipt = { now, transactionNum[0], transactionType, toAccount, amount }; /*
                                                                                            * if transfer we
                                                                                            * need
                                                                                            * accountNumber
                                                                                            */

        String[] updatedReceipts = new String[allReceiptsTemp.length + receipt.length]; /*
                                                                                         * new array to
                                                                                         * contanate old
                                                                                         * receipts and new one
                                                                                         */

        System.arraycopy(allReceiptsTemp, 0, updatedReceipts, 0, allReceiptsTemp.length); // copy old receipts
        System.arraycopy(receipt, 0, updatedReceipts, allReceiptsTemp.length, receipt.length); // copy new

        writeFile(encryption(updatedReceipts),
                Base64.getEncoder().encodeToString(thisAccount[5].getBytes()) + "_receipts.txt", ""); /*
                                                                                                       * update
                                                                                                       * receipts in
                                                                                                       * the filem
                                                                                                       */
        writeFile(transactionNum, "transactionNumber.txt", ""); // saves transaction number (all transactions have
                                                                // different number)
        System.out.println("   \n" + receipt[0] + " " + receipt[1] + " " + receipt[2] + " " + receipt[3] + " "
                + receipt[4] + " \n");

    }//end receipts method

    // method is created to encrypt data arrays using base64 encryption
    public static String[] encryption(String[] data) {
        String[] encrypted = new String[data.length];
        for (int i = 0; i < data.length; i++) { // get each element of data
            String encodedString = Base64.getEncoder().encodeToString(data[i].getBytes()); // encryption
            encrypted[i] = encodedString; // reassign
        }
        return encrypted;
    }//end encryption

    // method is used for decrypting arrays
    public static String[] decryption(String[] data) {
        String[] decrypted = new String[data.length];
        for (int i = 0; i < data.length; i++) {
            byte[] decodedBytes = Base64.getDecoder().decode(data[i].getBytes()); /*
                                                                                   * get bytes of string and decode it
                                                                                   * into readable one
                                                                                   */
            String decodedString = new String(decodedBytes); // bytes to string
            decrypted[i] = decodedString;
        }
        return decrypted;
    }//end decryption

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
    }//end readFile

    /*
     * method is used to save information to the file
     * extraData is used to add one more line to file
     * simplifies updating file, because instead of creation of an array
     * which length is bigger by 1 you can write new info to the file straightaway.
     */
    public static void writeFile(String[] data, String name, String extraData) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(name));
        for (int i = 0; i < data.length; i++) {
            writer.write(data[i] + "\n"); // writes each element of array into a file
        }
        if (extraData != "") { // added to write a new email to email data base
            writer.write(extraData + "\n");
        }
        writer.close();
    } //end writeFile
} //end class
