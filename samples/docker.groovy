def execute() {
    def image = new DockerImage(this, 'elifesciences/php_cli', 'latest')
    image.push()
    image.tag('20180125').push()
}

return this
