package javasnack.cli;

public class CliDemo1 {
    
    /*
     * mvn exec:java \
     *     -Dexec.mainClass=javasnack.cli.CliDemo1 \
     *     -Dexec.classpathScope=test \
     *     -Dexec.args=arg1,arg2,arg3 \
     *     -Dexec.args=arg4,arg5,arg6 \
     *     -Dexec.arguments=123,456 \
     *     -DCliDemo1.prop=abc
     * ->
     * <pre>
     * args.length = [1]
     * arg4,arg5,arg6
     * -DCliDemo1.prop=[abc]
     * </pre>
     */
    
    public static void main(String[] args) {
        System.out.println("args.length = [" + args.length + "]");
        for (String arg : args) {
            System.out.println(arg);
        }
        System.out.println("-DCliDemo1.prop=["
                + System.getProperty("CliDemo1.prop", "default") + "]");
    }
}
