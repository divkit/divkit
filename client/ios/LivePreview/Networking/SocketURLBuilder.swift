import Foundation

func buildWebSocketURL(from urlString: String) -> (URL, String)? {
  guard let url = URL(string: urlString),
        let source = URLComponents(url: url, resolvingAgainstBaseURL: false),
        let uuid = source.queryItems?.first(where: { $0.name == uuidName })?.value else {
    return nil
  }
  var result = URLComponents()
  result.scheme = scheme
  result.host = source.host
  result.path = source.path
  guard let resultURL = result.url else {
    return nil
  }
  return (resultURL, uuid)
}

private let scheme = "wss"
private let uuidName = "uuid"
