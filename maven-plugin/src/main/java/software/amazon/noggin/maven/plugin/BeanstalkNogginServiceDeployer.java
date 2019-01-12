package software.amazon.noggin.maven.plugin;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import software.amazon.awssdk.services.cloudformation.model.Parameter;
import software.amazon.awssdk.services.cloudformation.model.Stack;

public class BeanstalkNogginServiceDeployer extends NogginServiceDeployer {
    public void deploy(Properties properties, Path jar) throws Exception {
        String stackName = properties.getProperty("application.name");
        String cloudFormationFileName = "beanstalk-cfn-template.yml";
        String sourceKey = "beanstalk-application-" + System.currentTimeMillis() + ".jar";

        String nogginBucket = getNogginBucket();

        uploadCloudFormationTemplate(cloudFormationFileName, nogginBucket);
        uploadSource(nogginBucket, sourceKey, jar);

        List<Parameter> parameters = Arrays.asList(parameter("BeanstalkRoutesClass", properties.getProperty("application.class")),
                                                   parameter("BeanstalkCodeS3Bucket", nogginBucket),
                                                   parameter("BeanstalkCodeS3Object", sourceKey));

        Stack stack = deploy(stackName, parameters, nogginBucket, cloudFormationFileName);

        System.out.println("Application URL: " + getOutput(stack, "BeanstalkInvokeURL"));
    }
}
