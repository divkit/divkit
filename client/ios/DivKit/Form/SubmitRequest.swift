import Foundation

public struct SubmitRequest: Equatable {
  public let url: URL
  public let method: String
  public let headers: [String: String]

  init(url: URL, method: String, headers: [String: String]) {
    self.url = url
    self.method = method
    self.headers = headers
  }
}
