# Java Microservices Framework for AWS

Noggin is a framework for creating Java microservices on AWS. It allows
you to quickly create and deploy services to AWS Lambda. It provides:

* An annotation-based API for creating RESTful microservices.
* A maven plugin for deploying and updating your microservice.

You can create RESTful services:

```Java
@Application
class HelloWorldApplication {
    @Path("/")
    @Produces("text/plain")
    public String helloWorld() {
        return "Hello, World!";
    }
}
```

And deploy them to AWS:

```Bash
> mvn deploy
[INFO] Scanning for projects...
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] Building hello-world-application 1.0-SNAPSHOT
[INFO] ------------------------------------------------------------------------
[INFO]
[INFO] --- noggin-maven-plugin:0.1.0-SNAPSHOT:noggin-deploy (push-to-aws) @ noggin-demo ---
Updating stack: noggin-application
Status: UPDATE_IN_PROGRESS.............
Status: UPDATE_COMPLETE_CLEANUP_IN_PROGRESS
Status: UPDATE_COMPLETE
Application URL: https://uuid.execute-api.us-west-2.amazonaws.com/prod
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 30.993 s
[INFO] Finished at: 2019-01-18T12:07:09-08:00
[INFO] Final Memory: 34M/114M
[INFO] ------------------------------------------------------------------------
```

See the [examples](examples) module for an example application.