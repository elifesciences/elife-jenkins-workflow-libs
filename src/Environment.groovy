public class Environment implements Serializable {
    private Map contents = [:]

    public Environment set(String name, value) {
        this.contents[name] = value
        return this
    }

    public String asPrefix() {
        String prefix = ''
        this.contents.each({ n, v ->
            prefix = "${prefix}${n}=${v} "
        })
        return prefix
    }
}
