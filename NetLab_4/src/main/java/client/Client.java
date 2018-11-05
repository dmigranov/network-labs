package client;



public class Client {
    public static void main(String[] args) {
        String s = "users/fsd";
        System.out.println(s.matches(("users/(.+)")));
    }
}
