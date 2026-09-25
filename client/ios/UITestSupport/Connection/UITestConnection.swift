import Foundation
import Network

@MainActor
final class UITestConnection {
  let port: UInt16

  private var listenerTask: Task<Void, Never>?
  private var incoming: AsyncThrowingStream<Connection, Error>?
  private var connection: Connection?

  init() async throws {
    let ports = AsyncThrowingStream<UInt16, Error>.makeStream()
    let connections = AsyncThrowingStream<Connection, Error>.makeStream()
    incoming = connections.stream
    let listener = try NetworkListener(using: .parameters {
      Coder(sending: UITestRequest.self, receiving: UITestResponse.self, using: .json) {
        TCP()
      }
    }.localEndpoint(.hostPort(host: .ipv4(.loopback), port: .any)))
      .newConnectionLimit(1)
      .onStateUpdate { listener, state in
        switch state {
        case .ready:
          if let port = listener.port {
            ports.continuation.yield(port.rawValue)
            ports.continuation.finish()
          }
        case let .failed(error), let .waiting(error):
          ports.continuation.finish(throwing: error)
          connections.continuation.finish(throwing: error)
        default:
          break
        }
      }
    let task = Task {
      do {
        try await listener.run { connection in
          connections.continuation.yield(connection)
          connections.continuation.finish()
        }
      } catch {
        ports.continuation.finish(throwing: error)
        connections.continuation.finish(throwing: error)
      }
    }
    listenerTask = task
    do {
      port = try await withUITestTimeout {
        guard let port = try await ports.stream.first(where: { _ in true }) else {
          throw ConnectionError.listenerStopped
        }
        return port
      }
    } catch {
      task.cancel()
      throw error
    }
  }

  deinit {
    listenerTask?.cancel()
  }

  func close() {
    listenerTask?.cancel()
    listenerTask = nil
    connection = nil
    incoming = nil
  }

  func perform(_ request: UITestRequest) async throws {
    let response: UITestResponse
    do {
      response = try await withUITestTimeout { [self] in
        if connection == nil, let incoming {
          connection = try await incoming.first(where: { _ in true })
          self.incoming = nil
        }
        guard let connection else {
          throw ConnectionError.closed
        }
        try await connection.send(request)
        return try await connection.receive().content
      }
    } catch {
      close()
      throw error
    }
    switch response {
    case .success:
      break
    case let .failure(message):
      throw ConnectionError.requestFailed(message)
    }
  }
}

private typealias Connection = NetworkConnection<Coder<
  UITestRequest,
  UITestResponse,
  NetworkJSONCoder
>>

@MainActor
private func withUITestTimeout<Value: Sendable>(
  _ operation: @escaping @MainActor () async throws -> Value
) async throws -> Value {
  try await withThrowingTaskGroup(of: Value.self) { group in
    group.addTask { try await operation() }
    group.addTask {
      try await Task.sleep(for: .seconds(10))
      throw ConnectionError.timedOut
    }
    defer { group.cancelAll() }
    return try await group.next()!
  }
}

private enum ConnectionError: LocalizedError {
  case listenerStopped
  case closed
  case timedOut
  case requestFailed(String)

  var errorDescription: String? {
    switch self {
    case .listenerStopped:
      "UI test listener stopped before becoming ready"
    case .closed:
      "UI test connection is closed"
    case .timedOut:
      "UI test connection timed out after 10 seconds"
    case let .requestFailed(message):
      message
    }
  }
}
