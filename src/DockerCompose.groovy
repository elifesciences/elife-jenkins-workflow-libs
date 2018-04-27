import Environment

public class DockerCompose implements Serializable {
    private String name
    private List files
    private Environment environment = new Environment()
    private Map options = [:]
    private List arguments = []

    public static DockerCompose command(String name, List files = ['docker-compose.ci.yml'])
    {
        return new DockerCompose(name, files);
    }

    public DockerCompose(String name, List files)
    {
        this.name = name
        this.files = files
    }

    public DockerCompose withEnvironment(String name, String value)
    {
        this.environment.set(name, value)
        return this
    }

    public DockerCompose withOption(String name, value = null)
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
        def pieces = []
        if (environment.asPrefix()) {
            pieces.add(environment.asPrefix())
        }
        def filesArgument = ''
        files.each({ f -> 
            filesArgument = filesArgument + "-f ${f} "
        })
        pieces.add("docker-compose ${filesArgument}${name}")
        if (options) {
            def optionsList = []
            options.each({ n, v ->
                def dashes
                if (n.size() == 1) {
                    dashes = '-'
                } else {
                    dashes = '--'
                }
                def value = ''
                if (v != null) {
                    value = " ${v}"
                }
                optionsList.add("${dashes}${n}${value}")
            })
            pieces.add(optionsList.join(' '))
        }
        if (arguments) {
            pieces.add(arguments.join(' '))
        }
        return pieces.join(' ')
    }
}
