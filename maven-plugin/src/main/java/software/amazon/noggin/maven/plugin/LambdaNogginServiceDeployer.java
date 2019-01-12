package software.amazon.noggin.maven.plugin;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import software.amazon.awssdk.services.apigateway.ApiGatewayClient;
import software.amazon.awssdk.services.cloudformation.model.Parameter;
import software.amazon.awssdk.services.cloudformation.model.Stack;

public class LambdaNogginServiceDeployer extends NogginServiceDeployer {
    private final ApiGatewayClient apiGateway = ApiGatewayClient.create();

    public void deploy(Properties properties, Path jar) throws Exception {
        String stackName = properties.getProperty("application.name");
        String cloudFormationFileName = "lambda-cfn-template.yml";
        String sourceKey = "lambda-application-" + System.currentTimeMillis() + ".jar";

        String nogginBucket = getNogginBucket();

        uploadCloudFormationTemplate(cloudFormationFileName, nogginBucket);
        uploadSource(nogginBucket, sourceKey, jar);

        List<Parameter> parameters = Arrays.asList(parameter("ApiGatewayStageName", "prod"),
                                                   parameter("ApiGatewayApiName", stackName + "-api"),
                                                   parameter("LambdaRoutesClass", properties.getProperty("application.class")),
                                                   parameter("LambdaFunctionName", stackName + "-function"),
                                                   parameter("LambdaCodeS3Bucket", nogginBucket),
                                                   parameter("LambdaCodeS3Object", sourceKey));

        Stack stack = deploy(stackName, parameters, nogginBucket, cloudFormationFileName);

        apiGateway.createDeployment(r -> r.restApiId(getOutput(stack, "ApiGatewayRestApiId"))
                                          .stageName(getOutput(stack, "ApiGatewayStageName")));

        System.out.println("Application URL: " + getOutput(stack, "ApiGatewayInvokeURL"));
    }
}
