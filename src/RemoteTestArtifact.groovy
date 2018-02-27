public class RemoteTestArtifact implements Serializable
{
    private final String path

    public RemoteTestArtifact(path)
    {
        this.path = path
    }
    
    public String path()
    {
        return this.path
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

    public String remoteTestArtifactFolder()
    {
        def remoteSlash = this.path.lastIndexOf('/')
        return this.path[0..remoteSlash]
    }

    public String remoteTestArtifactFolderBasename()
    {
        return (this.remoteTestArtifactFolder() =~ /\/build\/(.*)/)[0][1]
    }
}
