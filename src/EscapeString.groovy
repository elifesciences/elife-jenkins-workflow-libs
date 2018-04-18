public class EscapeString implements Serializable {
    public static String forBashSingleQuotes(String original) {
        def closedQuote = "'"
        def quote = "\\\\'"
        def openQuote = "'"
        return original.replaceAll(/'/, closedQuote + quote + openQuote)
    }
}
