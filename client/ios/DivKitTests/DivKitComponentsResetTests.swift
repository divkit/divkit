@testable import DivKit
import DivKitTestsSupport
import LayoutKit
import Testing

@Suite
struct DivKitComponentsResetTests {
  private let components = DivKitComponents()
  private let cardId: DivCardID = "test_card"

  init() {
    components.persistentValuesStorage.clear()
  }

  @Test
  func reset_DoesNotClearStoredValues() {
    setVariable("x", value: .string("hello"))
    setStoredValue("key", value: "stored")

    components.reset()

    #expect(localVariable("x") == nil)
    let stored: String? = components.persistentValuesStorageInternal.get(name: "key")
    #expect(stored == "stored")
  }

  @Test
  func resetCardId_DoesNotClearStoredValues() {
    setStoredValue("key", value: "stored")

    components.reset(cardId: cardId)

    let stored: String? = components.persistentValuesStorageInternal.get(name: "key")
    #expect(stored == "stored")
  }
    

  @Test
  func сlear_ClearsStoredValues() {
    setStoredValue("key", value: "stored")

    components.persistentValuesStorage.clear()

    let stored: String? = components.persistentValuesStorageInternal.get(name: "key")
    #expect(stored == nil)
  }

  private func setVariable(_ name: String, value: DivVariableValue) {
    components.variablesStorage.set(
      cardId: cardId,
      variables: [DivVariableName(rawValue: name): value]
    )
  }

  private func localVariable(_ name: String) -> DivVariableValue? {
    components.variablesStorage.getVariableValue(cardId: cardId, name: DivVariableName(rawValue: name))
  }

  private func setStoredValue(_ name: String, value: String) {
    components.persistentValuesStorageInternal.set(
      value: DivStoredValue(name: name, value: value, type: .string, lifetimeInSec: 86400)
    )
  }
}
