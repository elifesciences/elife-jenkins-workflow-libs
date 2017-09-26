def backslash(character, string) {
    // backslash the slash because it's a special character
    // backslash them again because replaceAll has special chars like \1
    return string.replaceAll(character, '\\\\' + character)
}

def call(cmd) {
    cmd = backslash('=', cmd)
    cmd = backslash(',', cmd)
    cmd = _escapeString(cmd)
}
