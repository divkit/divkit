import Network

@MainActor
final class UITestAppClient {
  private let task: Task<Void, Error>

  init(
    port: UInt16,
    handleRequest: @escaping @MainActor (UITestRequest) throws -> UITestResponse
  ) {
    task = Task {
      let connection = NetworkConnection(to: .hostPort(
        host: .ipv4(.loopback),
        port: .init(rawValue: port)!
      )) {
        Coder(sending: UITestResponse.self, receiving: UITestRequest.self, using: .json) {
          TCP()
        }
      }
      for try await (request, _) in connection.messages {
        let response: UITestResponse
        do {
          response = try handleRequest(request)
        } catch {
          response = .failure(error.localizedDescription)
        }
        try await connection.send(response)
      }
    }
  }

  deinit {
    task.cancel()
  }
}
