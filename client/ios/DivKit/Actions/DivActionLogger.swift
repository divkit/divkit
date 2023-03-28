import Foundation

import BasePublic
import NetworkingPublic
import Serialization

public protocol DivActionLogger {
  func log(url: URL, referer: URL?, payload: [String: Any]?)
}

public struct EmptyDivActionLogger: DivActionLogger {
  public init() {}

  public func log(url _: URL, referer _: URL?, payload _: [String: Any]?) {}
}

public struct DefaultDivActionLogger: DivActionLogger {
  private let requestPerformer: URLRequestPerforming

  public init(requestPerformer: URLRequestPerforming) {
    self.requestPerformer = requestPerformer
  }

  public func log(url: URL, referer: URL?, payload: [String: Any]?) {
    var request = URLRequest(url: url)
    if let referer = referer {
      request.setValue(referer.absoluteString, forHTTPHeaderField: "referer")
    }

    if let payload = payload {
      let json: JSONObject = .object(payload.typedJSON())
      guard let body = try? json.toJSONString().data(using: .utf8) else {
        assertionFailure("Can't serialize JSON payload")
        return
      }
      request.httpMethod = "POST"
      request.httpBody = body
    }

    requestPerformer.performRequest(request, completion: { _ in })
  }
}
