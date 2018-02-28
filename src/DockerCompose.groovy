import Environment

public class DockerCompose implements Serializable {
    private String name
    private String file
    private Environment environment = new Environment()
    private Map options = [:]
    private List arguments = []

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

    public DockerCompose withOption(String name, value)
    {
        this.options[name] = value
        return this
    }

    public DockerCompose withArgument(value)
    {
        this.arguments.add(value)
        return this
    }

    public String toString()
    {
        def pieces = [
            environment.asPrefix(),
            "docker-compose -f ${file} ${name}",
        ]
        if (options) {
            def optionsList = []
            options.each({ n, v ->
                optionsList.add("--${n} ${v}")
            })
            pieces.add(optionsList.join(' '))
        }
        if (arguments) {
            pieces.add(arguments.join(' '))
        }
        return pieces.join(' ')
    }
}
