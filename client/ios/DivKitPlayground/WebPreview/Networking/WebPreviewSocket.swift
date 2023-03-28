import Foundation

import CommonCorePublic

final class WebPreviewSocket: NSObject {
  enum State {
    enum Disconnected {
      case failed
      case ended
    }

    case disconnected(Disconnected)
    case connecting
    case connected
  }

  private var session: URLSession!
  private var task: URLSessionWebSocketTask?

  private let responsePipe = SignalPipe<JsonProvider>()
  var response: Signal<JsonProvider> { responsePipe.signal }

  @ObservableProperty
  private(set) var state: State = .disconnected(.ended)

  override init() {
    super.init()

    session = URLSession(
      configuration: .default,
      delegate: self,
      delegateQueue: .main
    )
  }

  deinit {
    disconnect(.ended)
  }

  func connect(httpUrl: URL) {
    guard let source = URLComponents(url: httpUrl, resolvingAgainstBaseURL: false),
          let uuid = source.queryItems?.first(where: { $0.name == "uuid" })?.value else {
      DemoAppLogger.error("Invalid web preview URL: \(httpUrl.absoluteString)")
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
    disconnect(.ended)
    state = .connecting
    let task = session.webSocketTask(with: url)
    task.resume()
    task.send(ListenPayload(uuid: uuid)) { [weak self] in
      if let error = $0 {
        DemoAppLogger.error("Failed to connect to server: \(error)")
        self?.disconnect(.failed)
        return
      }
      self?.listen()
      self?.state = .connected
    }
    self.task = task
  }

  func send(state: UIStatePayload) {
    task?.send(state) {
      $0.map { DemoAppLogger.error("Failed to send UI state with \($0)") }
    }
  }

  func endConnection() {
    disconnect(.ended)
  }

  private func disconnect(_ reason: State.Disconnected) {
    guard task != nil else { return }
    task?.cancel(with: .goingAway, reason: nil)
    task = nil
    state = .disconnected(reason)
  }

  private func listen() {
    task?.receive { [weak self] in
      switch $0 {
      case let .success(message):
        self?.responsePipe.send {
          let dictionary = try message.data.asJsonDictionary()
          return try JSONPayload(dictionary: dictionary).message.json
        }
      case let .failure(error):
        DemoAppLogger.error("Failed to receive message with \(error)")
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
    didCloseWith closeCode: URLSessionWebSocketTask.CloseCode,
    reason: Data?
  ) {
    guard webSocketTask == task else { return }
    let reason: State.Disconnected
    switch closeCode {
    case .goingAway, .normalClosure:
      reason = .ended
    case .abnormalClosure, .internalServerError, .invalid,
         .invalidFramePayloadData, .mandatoryExtensionMissing, .messageTooBig,
         .noStatusReceived, .policyViolation, .protocolError,
         .tlsHandshakeFailure, .unsupportedData:
      reason = .failed
    @unknown default:
      reason = .ended
    }
    disconnect(reason)
  }
}

extension URLSessionWebSocketTask {
  fileprivate func send<T>(
    _ payload: T,
    completion: @escaping (Error?) -> Void
  ) where T: Encodable {
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
