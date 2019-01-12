package software.amazon.noggin.maven.plugin;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import software.amazon.noggin.NogginRuntime;

@Mojo(name = "noggin-deploy")
public class DeployNogginService extends AbstractMojo {
    @Parameter(property = "jar")
    private String jar;

    public void execute() {
        try {
            Path jar = Paths.get(this.jar);
            Properties properties = loadApplicationProperties(jar);

            NogginRuntime generator = NogginRuntime.valueOf(properties.getProperty("application.type"));
            switch (generator) {
                case LAMBDA:
                    new LambdaNogginServiceDeployer().deploy(properties, jar);
                    break;
                case BEANSTALK:
                    new BeanstalkNogginServiceDeployer().deploy(properties, jar);
                    break;
                default:
                    throw new IllegalStateException("Unsupported generator type: " + generator);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Properties loadApplicationProperties(Path jar) {
        try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(jar))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (entry.getName().equals("software/amazon/noggin/application.properties")) {
                    Properties result = new Properties();
                    result.load(zipInputStream);
                    return result;
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        throw new IllegalStateException("Unable to find software/amazon/noggin/lambda-noggin-route in jar.");
    }
}
