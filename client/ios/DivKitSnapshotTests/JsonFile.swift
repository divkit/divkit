struct JsonFile {
  let absolutePath: String
  let relativePath: String

  var subdirectory: String {
    let index = relativePath.lastIndex(of: "/")!
    return String(relativePath[...index])
  }

  var name: String {
    let index = relativePath.lastIndex(of: "/")!
    return String(relativePath[relativePath.index(after: index)...])
  }
}
