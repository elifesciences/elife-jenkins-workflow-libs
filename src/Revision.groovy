public class Revision {
    private String label

    public Revision(String label) {
        this.label = label
    }

    public Boolean isBranch() {
        def isCommit = this.label ==~ /[0-9a-f]{40}/
        return !isCommit
    }

    public String toString() {
        return label
    }
}
