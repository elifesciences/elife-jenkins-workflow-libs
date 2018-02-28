import Environment

public class DockerCompose implements Serializable {
    private String name
    private String file
    private Environment environment = new Environment()

    public static DockerCompose command(String name, String file = 'docker-compose.ci.yml')
    {
        return new DockerCompose(name, file);
    }

    public DockerCompose(String name, String file)
    {
        this.name = name
        this.file = file
    }

    public DockerCompose withEnvironment(String name, String value)
    {
        this.environment.set(name, value)
        return this
    }

    public String toString()
    {
        return "${environment.asPrefix()}docker-compose -f ${file} ${name}"
    }
}
