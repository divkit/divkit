import Foundation

import CommonCore

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

  private let responsePipe = SignalPipe<[String: Any]>()
  var response: Signal<[String: Any]> { responsePipe.signal }

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
      guard $0 == nil else {
        DemoAppLogger.error("Failed to connect to server \(url) with \($0!)")
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
        guard let dictionary = try? JSONSerialization.jsonObject(
          with: message.data,
          options: []
        ) as? [String: Any],
          let payload = try? JSONPayload(dictionary: dictionary) else {
          DemoAppLogger.error("Failed to parse valid server message from response \($0)")
          self?.responsePipe.send([:])
          break
        }
        self?.responsePipe.send(payload.message.json)
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
