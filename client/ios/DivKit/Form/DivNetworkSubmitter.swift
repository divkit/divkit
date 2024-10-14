import Foundation
import VGSL

@_spi(Internal)
final public class DivNetworkSubmitter: DivSubmitter {
  private let requestPerformer: URLRequestPerforming

  private var tasks: [NetworkTask] = []

  public init(requestPerformer: URLRequestPerforming) {
    self.requestPerformer = requestPerformer
  }

  public func submit(
    request: SubmitRequest,
    data: [String: String],
    completion: @escaping DivSubmitterCompletion
  ) {
    var urlRequest = URLRequest(url: request.url)
    request.headers.forEach { header in
      urlRequest.setValue(header.value, forHTTPHeaderField: header.key)
    }
    urlRequest.httpMethod = request.method

    var task: NetworkTask?
    task = requestPerformer.performRequest(urlRequest) { [weak self] in
      switch $0 {
      case .success: completion(.success(()))
      case let .failure(value): completion(.failure(value))
      }

      self?.tasks.removeAll(where: { $0 === task })
    }

    tasks.append(task!)
  }

  public func cancelRequests() {
    tasks.forEach { $0.cancel() }
    tasks.removeAll()
  }
}
