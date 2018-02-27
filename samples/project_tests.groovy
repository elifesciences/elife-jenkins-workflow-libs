//builderCmd 'journal--ci', 'ls -l'
//builderProjectTests 'journal--ci', '/srv/journal'
//sh(script:'ls -l')
def execute() {
    println(sayHello)
    sayHello 'Giorgio'
}

return this

