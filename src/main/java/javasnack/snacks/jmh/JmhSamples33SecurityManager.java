package javasnack.snacks.jmh;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.Policy;
import java.security.URIParameter;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

/* copied from:
 * https://hg.openjdk.java.net/code-tools/jmh/file/tip/jmh-samples/src/main/java/org/openjdk/jmh/samples/
 * 
 * jmh args: JmhSamples33SecurityManager -w 3s -wi 5 -f 1 -t 2 -i 5 -r 3s
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class JmhSamples33SecurityManager {
    /*
     * Some targeted tests may care about SecurityManager being installed.
     * Since JMH itself needs to do privileged actions, it is not enough
     * to blindly install the SecurityManager, as JMH infrastructure will fail.
     */

    /*
     * In this example, we want to measure the performance of System.getProperty
     * with SecurityManager installed or not. To do this, we have two state classes
     * with helper methods. One that reads the default JMH security policy (we ship one
     * with JMH), and installs the security manager; another one that makes sure
     * the SecurityManager is not installed.
     *
     * If you need a restricted security policy for the tests, you are advised to
     * get /jmh-security-minimal.policy, that contains the minimal permissions
     * required for JMH benchmark to run, merge the new permissions there, produce new
     * policy file in a temporary location, and load that policy file instead.
     * There is also /jmh-security-minimal-runner.policy, that contains the minimal
     * permissions for the JMH harness to run, if you want to use JVM args to arm
     * the SecurityManager.
     */

    @State(Scope.Benchmark)
    public static class SecurityManagerInstalled {
        @Setup
        public void setup() throws IOException, NoSuchAlgorithmException, URISyntaxException {
            URI policyFile = JmhSamples33SecurityManager.class.getResource("/jmh-security.policy").toURI();
            Policy.setPolicy(Policy.getInstance("JavaPolicy", new URIParameter(policyFile)));
            System.setSecurityManager(new SecurityManager());
        }

        @TearDown
        public void tearDown() {
            System.setSecurityManager(null);
        }
    }

    @State(Scope.Benchmark)
    public static class SecurityManagerEmpty {
        @Setup
        public void setup() throws IOException, NoSuchAlgorithmException, URISyntaxException {
            System.setSecurityManager(null);
        }
    }

    @Benchmark
    public String testWithSM(SecurityManagerInstalled s) throws InterruptedException {
        return System.getProperty("java.home");
    }

    @Benchmark
    public String testWithoutSM(SecurityManagerEmpty s) throws InterruptedException {
        return System.getProperty("java.home");
    }
}
