import java.util.Base64;

public class test {
    public static void main(String[] args) {
        String[] arr = {"1", "2"};
        String[] arrTemp = encryption(arr);
        System.out.println(arr[0]);
        System.out.println(arrTemp[0]);
    }
    public static String[] encryption(String[]  data) {
        String[] encrypted = new String[data.length];
        for (int i = 0; i < data.length; i++) { // get each element of data
            String encodedString = Base64.getEncoder().encodeToString(data[i].getBytes()); // encryption
            encrypted[i] = encodedString; // reassign
        }
        return encrypted;
    }
    public static String[] decryption(String[] data) {
        String[] decrypted = new String[data.length];
        for (int i = 0; i < data.length; i++) {
            byte[] decodedBytes = Base64.getDecoder().decode(data[i].getBytes()); /* get bytes of string and decode it into readable one */
            String decodedString = new String(decodedBytes); // bytes to string
            decrypted[i] = decodedString;
        }
        return decrypted;
    }
}
