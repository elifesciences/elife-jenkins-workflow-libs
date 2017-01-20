def call(trigger, Closure body) {
    if (trigger) {
        body()
    } else {
        echo "Nothing to do"
    }
}
