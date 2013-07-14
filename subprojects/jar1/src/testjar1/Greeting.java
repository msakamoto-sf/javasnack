package testjar1;

public class Greeting {
    public Greeting() {
    }

    public String morning(String to) {
        return "Grood Morning, " + to + ".";
    }

    public String afternoon(String to) {
        return "Good Afternoon, " + to + ".";
    }

    public static void main(String[] args) {
        Greeting g = new Greeting();
        System.out.println(g.morning("ABC"));
        System.out.println(g.afternoon("DEF"));
    }
}
