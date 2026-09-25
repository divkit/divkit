import Network
import VGSL
import XCTest

@MainActor
final class UITestConnectionTests: XCTestCase {
  func testConnectsWithoutFeatureFlags() async throws {
    let connection = try await UITestConnection()
    defer { connection.close() }
    let app = appTask(port: connection.port) { appConnection in
      _ = try await appConnection.receive()
      try await appConnection.send(.success)
    }
    defer { app.cancel() }

    try await connection.perform(.divAction([:]))
    try await app.value
  }

  func testPropagatesActionFailureWithoutClosingConnection() async throws {
    let connection = try await UITestConnection()
    defer { connection.close() }
    let app = appTask(port: connection.port) { appConnection in
      _ = try await appConnection.receive()
      try await appConnection.send(.failure("Invalid action"))
      _ = try await appConnection.receive()
      try await appConnection.send(.success)
    }
    defer { app.cancel() }

    do {
      try await connection.perform(.divAction([:]))
      XCTFail("Expected the action error")
    } catch {
      XCTAssertEqual(error.localizedDescription, "Invalid action")
    }
    try await connection.perform(.divAction([:]))
    try await app.value
  }

  func testFailsWhenAppDisconnects() async throws {
    let connection = try await UITestConnection()
    defer { connection.close() }
    let app = appTask(port: connection.port) { appConnection in
      _ = try await appConnection.receive()
    }
    defer { app.cancel() }

    do {
      try await connection.perform(.divAction([:]))
      XCTFail("Expected a connection error")
    } catch {
      XCTAssertFalse(error.localizedDescription.contains("timed out"))
    }
    try await app.value
  }

  func testTimesOutWithoutResponse() async throws {
    let connection = try await UITestConnection()
    defer { connection.close() }
    let app = appTask(port: connection.port) { appConnection in
      _ = try await appConnection.receive()
      _ = try await appConnection.receive()
    }
    defer { app.cancel() }

    do {
      try await connection.perform(.divAction([:]))
      XCTFail("Expected a timeout")
    } catch {
      XCTAssertEqual(error.localizedDescription, "UI test connection timed out after 10 seconds")
    }
  }

  private func appTask(
    port: UInt16,
    body: @escaping @MainActor (AppConnection) async throws -> Void
  ) -> Task<Void, Error> {
    Task {
      let connection = NetworkConnection(to: .hostPort(
        host: .ipv4(.loopback),
        port: .init(rawValue: port)!
      )) {
        Coder(sending: UITestResponse.self, receiving: UITestRequest.self, using: .json) {
          TCP()
        }
      }
      try await body(connection)
    }
  }
}

private typealias AppConnection = NetworkConnection<Coder<
  UITestResponse,
  UITestRequest,
  NetworkJSONCoder
>>
