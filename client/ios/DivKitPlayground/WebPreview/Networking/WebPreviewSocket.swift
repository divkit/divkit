import Combine
import Foundation

final class WebPreviewSocket: NSObject {
  private var session: URLSession!
  private var task: URLSessionWebSocketTask?

  private let responseSubject = CurrentValueSubject<[String: Any], Never>([:])

  var responsePublisher: JsonPublisher {
    responseSubject.eraseToAnyPublisher()
  }

  override init() {
    super.init()

    session = URLSession(
      configuration: .default,
      delegate: self,
      delegateQueue: .main
    )
  }

  deinit {
    disconnect()
  }

  func connect(httpUrl: URL) {
    guard let source = URLComponents(url: httpUrl, resolvingAgainstBaseURL: false),
          let uuid = source.queryItems?.first(where: { $0.name == "uuid" })?.value else {
      AppLogger.error("Invalid web preview URL: \(httpUrl.absoluteString)")
      return
    }

    var result = URLComponents()
    result.scheme = "wss"
    result.host = source.host
    result.path = source.path
    guard let url = result.url else {
      return
    }

    return connect(url: url, uuid: uuid)
  }

  func connect(url: URL, uuid: String) {
    disconnect()

    let task = session.webSocketTask(with: url)
    task.resume()
    task.send(ListenPayload(uuid: uuid)) { [weak self] in
      if let error = $0 {
        AppLogger.error("Failed to connect to server: \(error)")
        self?.disconnect()
        return
      }
      self?.listen()
    }
    self.task = task
  }

  func send(state: UIStatePayload) {
    task?.send(state) {
      $0.map { AppLogger.error("Failed to send UI state with \($0)") }
    }
  }

  func disconnect() {
    guard task != nil else { return }
    task?.cancel(with: .goingAway, reason: nil)
    task = nil
  }

  private func listen() {
    task?.receive { [weak self] in
      switch $0 {
      case let .success(message):
        let dictionary = try! message.data.asJsonDictionary()
        let json = try! JSONPayload(dictionary: dictionary).message.json
        self?.responseSubject.send(json)
      case let .failure(error):
        AppLogger.error("Failed to receive message with \(error)")
      }
      self?.listen()
    }
  }
}

extension WebPreviewSocket: URLSessionDelegate {
  func urlSession(
    _: URLSession,
    didReceive challenge: URLAuthenticationChallenge,
    completionHandler: @escaping (URLSession.AuthChallengeDisposition, URLCredential?) -> Void
  ) {
    if let trust = challenge.protectionSpace.serverTrust {
      completionHandler(.useCredential, URLCredential(trust: trust))
    } else {
      completionHandler(.useCredential, nil)
    }
  }
}

extension WebPreviewSocket: URLSessionWebSocketDelegate {
  func urlSession(
    _: URLSession,
    webSocketTask: URLSessionWebSocketTask,
    didCloseWith _: URLSessionWebSocketTask.CloseCode,
    reason _: Data?
  ) {
    guard webSocketTask == task else { return }
    disconnect()
  }
}

extension URLSessionWebSocketTask {
  fileprivate func send(
    _ payload: some Encodable,
    completion: @escaping (Error?) -> Void
  ) {
    do {
      let data = try JSONEncoder().encode(payload)
      send(.data(data), completionHandler: completion)
    } catch {
      completion(error)
    }
  }
}

extension URLSessionWebSocketTask.Message {
  var data: Data {
    switch self {
    case let .data(data):
      return data
    case let .string(string):
      return Data(string.utf8)
    @unknown default:
      return Data()
    }
  }
}
