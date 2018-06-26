public class DockerImage implements Serializable {
    private final def steps
    private final String repository
    private final String tag

    public static elifesciences(steps, String project, String tag) {
        return new DockerImage(steps, "elifesciences/${project}", tag)
    }

    /**
     * https://support.cloudbees.com/hc/en-us/articles/217736618-How-do-I-access-Pipeline-DSLs-from-inside-a-Groovy-class-
     */
    public DockerImage(steps, repository, tag) {
        this.steps = steps
        this.repository = repository
        this.tag = tag
    }

    public DockerImage pull()
    {
        this.steps.sh "docker pull ${repository}:${tag}"
        return this
    }

    public DockerImage push()
    {
        this.steps.sh "docker push ${repository}:${tag}"
        return this
    }

    public DockerImage tag(newTag)
    {
        this.steps.sh "docker tag ${repository}:${tag} ${repository}:${newTag}"
        return new DockerImage(steps, repository, newTag)
    }
}

