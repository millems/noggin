package software.amazon.noggin.maven.plugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.cloudformation.CloudFormationClient;
import software.amazon.awssdk.services.cloudformation.model.Capability;
import software.amazon.awssdk.services.cloudformation.model.DescribeStacksResponse;
import software.amazon.awssdk.services.cloudformation.model.Parameter;
import software.amazon.awssdk.services.cloudformation.model.Stack;
import software.amazon.awssdk.services.cloudformation.model.StackStatus;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.utils.IoUtils;
import software.amazon.noggin.runtime.util.Validate;

public abstract class NogginServiceDeployer {
    protected final CloudFormationClient cloudFormation = CloudFormationClient.create();
    protected final S3Client s3 = S3Client.create();

    protected String getNogginBucket() {
        return s3.listBuckets()
                 .buckets().stream()
                 .map(Bucket::name)
                 .filter(b -> b.startsWith("noggin-management-"))
                 .findFirst().orElseGet(() -> {
                     String nogginBucketName = "noggin-management-" + UUID.randomUUID();
                     s3.createBucket(r -> r.bucket(nogginBucketName));
                     return nogginBucketName;
                 });
    }

    protected void uploadCloudFormationTemplate(String cloudFormationFileName,
                                                String nogginBucket)
            throws IOException {
        Path temporaryFile = Files.createTempFile(LambdaNogginServiceDeployer.class.getSimpleName(), "");
        try (InputStream resourceInputStream = getClass().getResourceAsStream(cloudFormationFileName)) {
            try (OutputStream fileOutputStream = Files.newOutputStream(temporaryFile)) {
                IoUtils.copy(resourceInputStream, fileOutputStream);
            }
            s3.putObject(r -> r.bucket(nogginBucket).key(cloudFormationFileName), RequestBody.fromFile(temporaryFile));
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }

    protected void uploadSource(String nogginBucket, String key, Path file) {
        s3.putObject(r -> r.bucket(nogginBucket).key(key), RequestBody.fromFile(file));
    }

    protected Stack deploy(String stackName,
                           Collection<Parameter> parameters,
                           String nogginBucket,
                           String cloudFormationFileName) throws InterruptedException {
        String templateUrl = String.format("http://%s.s3.us-west-2.amazonaws.com/%s", nogginBucket, cloudFormationFileName);

        if (!stackExists(stackName)) {
            System.out.print("Creating stack: " + stackName);

            cloudFormation.createStack(r -> r.stackName(stackName)
                                             .parameters(parameters)
                                             .capabilities(Capability.CAPABILITY_IAM)
                                             .templateURL(templateUrl));

            while (!stackExists(stackName)) {
                System.out.print(".");
                Thread.sleep(1_000);
            }
        } else {
            System.out.print("Updating stack: " + stackName);
            cloudFormation.updateStack(r -> r.stackName(stackName)
                                             .parameters(parameters)
                                             .capabilities(Capability.CAPABILITY_IAM)
                                             .templateURL(templateUrl));
        }

        Set<StackStatus> successfulStatuses = new HashSet<>();
        successfulStatuses.add(StackStatus.CREATE_COMPLETE);
        successfulStatuses.add(StackStatus.UPDATE_COMPLETE);

        Set<StackStatus> pendingStatuses = new HashSet<>();
        pendingStatuses.add(StackStatus.CREATE_IN_PROGRESS);
        pendingStatuses.add(StackStatus.UPDATE_IN_PROGRESS);
        pendingStatuses.add(StackStatus.UPDATE_COMPLETE_CLEANUP_IN_PROGRESS);

        StackStatus lastReportedStatus = null;

        while (true) {
            DescribeStacksResponse stacks = cloudFormation.describeStacks(r -> r.stackName(stackName));

            Validate.isTrue(stacks.stacks().size() == 1, "Expected 1 stack, but found %s.", stacks.stacks()::size);

            Stack stack = stacks.stacks().get(0);

            StackStatus stackStatus = stack.stackStatus();

            if (lastReportedStatus != stackStatus) {
                System.out.println();
                System.out.print("Status: " + stackStatus);
                lastReportedStatus = stackStatus;
            } else {
                System.out.print(".");
            }
            System.out.flush();

            if (successfulStatuses.contains(stackStatus)) {
                System.out.println();
                return stack;
            }

            if (pendingStatuses.contains(stackStatus)) {
                Thread.sleep(1_000);
                continue;
            }

            throw new IllegalStateException("Update stack failed with status: " + stackStatus);
        }
    }

    protected String getOutput(Stack stack, String key) {
        return stack.outputs().stream()
                    .filter(o -> o.outputKey().equals(key))
                    .map(o -> o.outputValue())
                    .findFirst()
                    .orElse("");
    }

    protected boolean stackExists(String stackName) {
        return cloudFormation.listStacksPaginator()
                             .stackSummaries().stream()
                             .anyMatch(s -> s.stackName().equals(stackName));
    }

    protected Parameter parameter(String apiGatewayStageName, String prod) {
        return Parameter.builder().parameterKey(apiGatewayStageName).parameterValue(prod).build();
    }
}
