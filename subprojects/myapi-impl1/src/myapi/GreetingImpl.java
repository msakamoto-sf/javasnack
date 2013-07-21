package myapi;

public class GreetingImpl implements GreetingInterface {
    protected String myFirstname;
    protected String myLastname;
    public GreetingImpl(String _myFirstname, String _myLastname) {
        this.myFirstname = _myFirstname;
        this.myLastname = _myLastname;
    }

    public String morning(String to) {
        return "GOOD MORNING, " + to + ". I am " + this.myFirstname + " " + this.myLastname + ".";
    }

    public String afternoon(String to) {
        return "GOOD AFTERNOON, " + to + ". I am " + this.myFirstname + " " + this.myLastname + ".";
    }
}
