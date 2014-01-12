package myapi;

public class GreetingImpl implements GreetingInterface {
    protected String myFirstname;
    protected String myLastname;
    public GreetingImpl(String _myFirstname, String _myLastname) {
        this.myFirstname = _myFirstname;
        this.myLastname = _myLastname;
    }

    public String morning(String to) {
        return "Good Morning, I am " + to + ". You are " + this.myFirstname + " " + this.myLastname + ".";
    }

    public String afternoon(String to) {
        return "Good Afternoon, I am " + to + ". You are " + this.myFirstname + " " + this.myLastname + ".";
    }
}
