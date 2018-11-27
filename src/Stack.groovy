public class Stack {
    public static Stack fromName(String name)
    {
        def (project, environment) = name.split("--")
        return new Stack(project, environment)
    }

    private String project
    private String environment

    public Stack(String project, String environment) {
        this.project = project
        this.environment = environment
    }

    public String project() {
        return this.project
    }

    public String environment() {
        return this.environment
    }

    public String toString() {
        return "${project}--${environment}"
    }
}
