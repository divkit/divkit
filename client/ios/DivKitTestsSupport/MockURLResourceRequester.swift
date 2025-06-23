import Foundation
import VGSLFundamentals

public final class MockURLResourceRequester: URLResourceRequesting {
  public var requestedURLs: [URL] = []
  public var shouldSucceed = true
  public var customBehavior: ((URL) -> Bool)?
  
  public init() {}

  @MainActor
  public func getDataWithSource(
    from url: URL,
    completion: @escaping CompletionHandlerWithSource
  ) -> Cancellable? {
    requestedURLs.append(url)

    if let customBehavior {
      if customBehavior(url) {
        completion(.success(URLRequestResult(data: Data(), source: .network)))
      } else {
        completion(.failure(NSError(domain: "test", code: 0)))
      }
    } else {
      if shouldSucceed {
        completion(.success(URLRequestResult(data: Data(), source: .network)))
      } else {
        completion(.failure(NSError(domain: "test", code: 0)))
      }
    }

    return CancellableImpl {}
  }
}

private struct CancellableImpl: Cancellable {
  private let closure: @Sendable () -> Void

  init(_ closure: @escaping @Sendable () -> Void = {}) {
    self.closure = closure
  }

  func cancel() {
    closure()
  }
}
