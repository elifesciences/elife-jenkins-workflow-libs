def call(string) {
    def closedQuote = "'"
    def quote = "\\\\'"
    def openQuote = "'"
    return string.replaceAll(/'/, closedQuote + quote + openQuote)
}
