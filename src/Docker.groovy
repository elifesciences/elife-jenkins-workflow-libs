import Environment

public class Docker implements Serializable {
    private String name
    private Map generalOptions = [:]
    private Map options = [:]
    private List optionsList = []
    private List arguments = []

    public static Docker command(String name)
    {
        return new Docker(name);
    }

    public Docker(String name)
    {
        this.name = name
    }

    public Docker withOption(String name, String value = null)
    {
        def String dashes
        if (name.size() == 1) {
            dashes = '-'
        } else {
            dashes = '--'
        }
        def String maybeValue = ''
        if (value != null) {
            maybeValue = " ${value}"
        }
        optionsList.add("${dashes}${name}${maybeValue}")
        return this
    }

    public Docker withOption(String name, Map map)
    {
        for (each in map) {
            this.withOption(name, "${each.key}=${each.value}")
        }
        return this
    }

    public Docker withArgument(value)
    {
        this.arguments.add(value)
        return this
    }

    public String toString()
    {
        def pieces = []
        pieces.add("docker ${name}")
        if (optionsList) {
            pieces.add(optionsList.join(' '))
        }
        if (arguments) {
            pieces.add(arguments.join(' '))
        }
        return pieces.join(' ')
    }
}
