@testable @_spi(Internal) import DivKit
import DivKitTestsSupport
import Foundation
import LayoutKit
import Testing

@Suite
struct SubmitActionHandlerTests {
  private let submitter = MockSubmitter()
  private let variablesStorage = DivVariablesStorage()
  private let handler: DivActionHandler

  init() {
    handler = DivActionHandler(
      submitter: submitter,
      variablesStorage: variablesStorage
    )
  }

  @Test
  func submit_ContainerWithDifferentVariables() {
    variablesStorage.initializeIfNeeded(
      path: cardId.path + containerId,
      variables: [
        "string_var": .string("string_value"),
        "int_var": .integer(123),
        "number_var": .number(123.0),
        "bool_var": .bool(true),
        "color_var": .color(.color(withHexString: "#233223FF")!),
        "url_var": .url(testUrl),
        "dict_var": .dict(["key": "value"]),
        "array_var": .array(["value_1", "value_2"]),
      ]
    )

    handler.handleSubmit()

    #expect(submitter.lastData?["string_var"] == "string_value")
    #expect(submitter.lastData?["int_var"] == "123")
    #expect(submitter.lastData?["number_var"] == "123.0")
    #expect(submitter.lastData?["bool_var"] == "true")
    #expect(submitter.lastData?["color_var"] == "#233223FF")
    #expect(submitter.lastData?["url_var"] == testUrl.absoluteString)
    #expect(submitter.lastData?["dict_var"] == "{\"key\":\"value\"}")
    #expect(submitter.lastData?["array_var"] == "[\"value_1\",\"value_2\"]")
  }

  @Test
  func submit_ContainerWithoutVariables() {
    variablesStorage.initializeIfNeeded(
      path: cardId.path,
      variables: ["some_var": .string("some_value")]
    )
    variablesStorage.initializeIfNeeded(
      path: cardId.path + containerId,
      variables: [:]
    )

    handler.handleSubmit()

    #expect(submitter.lastRequest != nil)
    #expect(submitter.lastData?.isEmpty == true)
  }

  @Test
  func submit_ContainerWithIncorrectId() {
    variablesStorage.initializeIfNeeded(
      path: cardId.path + containerId,
      variables: [:]
    )

    handler.handleSubmit(containerId: "wrong_id")

    #expect(submitter.lastRequest == nil)
    #expect(submitter.lastData == nil)
  }

  @Test
  func submit_ContainerWithDuplicatedId() {
    variablesStorage.initializeIfNeeded(
      path: cardId.path + "container_1" + containerId,
      variables: ["var_1": .string("value_1")]
    )
    variablesStorage.initializeIfNeeded(
      path: cardId.path + "container_2" + containerId,
      variables: ["var_2": .string("value_2")]
    )

    handler.handleSubmit(containerId: containerId)

    #expect(submitter.lastData == nil)
  }

  @Test
  func submit_PassesRequestParams() {
    variablesStorage.initializeIfNeeded(
      path: cardId.path + containerId,
      variables: ["var_1": .string("value_1")]
    )

    handler.handleSubmit(
      containerId: containerId,
      request: DivActionSubmit.Request(
        headers: [.init(name: .value("header_1"), value: .value("value_1"))],
        method: .value(.get),
        url: .value(testUrl)
      )
    )

    let expectedRequest = SubmitRequest(
      url: testUrl,
      method: "GET",
      headers: ["header_1": "value_1"]
    )
    #expect(submitter.lastRequest == expectedRequest)
  }
}

extension DivActionHandler {
  fileprivate func handleSubmit(
    containerId: String = containerId,
    request: DivActionSubmit.Request = .init(url: .value(testUrl))
  ) {
    handle(
      divAction(
        logId: "action_id",
        typed: .divActionSubmit(
          DivActionSubmit(
            containerId: .value(containerId),
            request: request
          )
        )
      ),
      path: cardId.path,
      source: .callback,
      sender: nil
    )
  }
}

private final class MockSubmitter: DivSubmitter {
  private(set) var lastRequest: SubmitRequest?
  private(set) var lastData: [String: String]?

  func submit(
    request: SubmitRequest,
    data: [String: String],
    completion _: @escaping DivSubmitterCompletion
  ) {
    lastRequest = request
    lastData = data
  }

  func cancelRequests() {}
}

private let testUrl = URL(string: "https://example.com")!
private let cardId = DivBlockModelingContext.testCardId
private let containerId = "container_id"
