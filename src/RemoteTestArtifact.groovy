public class RemoteTestArtifact implements Serializable
{
    private final String path

    public RemoteTestArtifact(path)
    {
        this.path = path
    }

    public String localTestArtifactFolder()
    {
        def localTestArtifact = ((this.path =~ /\/?(build\/.*)/)[0][1])
        def localSlash = localTestArtifact.lastIndexOf('/')
        def localTestArtifactFolder = localTestArtifact[0..localSlash]
        return localTestArtifactFolder
    }

    public String localTestArtifact()
    {
        return (this.path =~ /\/?(build\/.*)/)[0][1]
    }
}
