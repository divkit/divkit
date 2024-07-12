import Foundation

import VGSL

public class DivPatchDownloader: DivPatchProvider {
  private let requestPerformer: URLRequestPerforming

  private var requests: [NetworkTask] = []

  public init(requestPerformer: URLRequestPerforming) {
    self.requestPerformer = requestPerformer
  }

  public func getPatch(
    url: URL,
    completion: @escaping DivPatchProviderCompletion
  ) {
    var request: NetworkTask?
    request = requestPerformer.performRequest(
      URLRequest(url: url),
      completion: { [weak self] result in
        guard let self else {
          return
        }
        self.requests.removeAll { $0 === request }
        parseResult(result, completion: completion)
      }
    )
    requests.append(request!)
  }

  public func cancelRequests() {
    requests.forEach { $0.cancel() }
    requests.removeAll()
  }

  deinit {
    cancelRequests()
  }
}

private func parseResult(
  _ result: Result<(Data, HTTPURLResponse), NSError>,
  completion: DivPatchProviderCompletion
) {
  switch result {
  case let .success((data, _)):
    do {
      let patch = try parseDivPatch(data)
      completion(.success(patch))
    } catch {
      completion(.failure(error))
    }
  case let .failure(error):
    completion(.failure(error))
  }
}
