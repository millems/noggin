package software.amazon.noggin;

public enum NogginRuntime {
    LAMBDA("software.amazon.noggin.core.LambdaNogginRouteGenerator"), // TODO: This should probably be defined in the lambda package, to prevent classnotfound errors.
    BEANSTALK("software.amazon.noggin.core.BeanstalkNogginRouteGenerator");

    private final String generatorClass;

    NogginRuntime(String generatorClass) {
        this.generatorClass = generatorClass;
    }

    public String generatorClass() {
        return generatorClass;
    }
}
