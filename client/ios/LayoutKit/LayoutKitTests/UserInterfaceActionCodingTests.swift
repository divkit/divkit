import Foundation
import LayoutKit
import Testing

@Suite
struct UserInterfaceActionCodingTests {
  @Test
  func divActionParams_roundTripPreservesPath() throws {
    let path = UIElementPath("card") + "anchor" + "tooltip#hint" + "0" + "button"
    let params = UserInterfaceAction.DivActionParams(
      action: .object(["log_id": .string("action")]),
      path: path,
      source: .tap,
      url: nil
    )
    let payload = UserInterfaceAction.Payload.divAction(params: params)

    let data = try JSONEncoder().encode(payload)
    let decodedPayload = try JSONDecoder().decode(
      UserInterfaceAction.Payload.self,
      from: data
    )

    #expect(decodedPayload.divActionParams?.path == path)
  }
}
