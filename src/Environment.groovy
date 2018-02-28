public class Environment implements Serializable {
    private Map contents = [:]

    public Environment set(String name, value) {
        this.contents[name] = value
        return this
    }

    public String asPrefix() {
        def prefixList = []
        this.contents.each({ n, v ->
            prefixList.add("${n}=${v}")
        })
        return prefixList.join(' ')
    }
}
