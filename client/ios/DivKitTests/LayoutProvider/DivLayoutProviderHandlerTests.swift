@testable @_spi(Internal) import DivKit
import LayoutKit
import Testing
import VGSL

@Suite(.serialized)
struct DivLayoutProviderHandlerTests {
  private let variablesStorage = DivVariablesStorage()
  private let cardId: DivCardID = "test_card"
  private let path: UIElementPath = "test_card"

  init() {
    variablesStorage.set(
      cardId: cardId,
      variables: [
        "var_a": .integer(0),
        "var_b": .integer(0),
      ]
    )
  }

  // MARK: - Circular dependency termination

  @Test
  func circularDependency_ABcycle_Terminates() {
    let handler = DivLayoutProviderHandler(variablesStorage: variablesStorage)
    let varA = DivVariableName(rawValue: "var_a")
    let varB = DivVariableName(rawValue: "var_b")

    let blockA = handler.apply(
      block: EmptyBlock.zeroSized,
      path: path,
      widthVariableName: varA,
      heightVariableName: nil
    )
    let blockB = handler.apply(
      block: EmptyBlock.zeroSized,
      path: path,
      widthVariableName: varB,
      heightVariableName: nil
    )

    guard let providerA = blockA as? SizeProviderBlock,
          let providerB = blockB as? SizeProviderBlock else {
      Issue.record("Expected SizeProviderBlocks")
      return
    }

    for _ in 1...20 {
      let currentVarB: Int = variablesStorage.getVariableValue(
        cardId: cardId,
        name: varB
      ) ?? 0
      providerA.widthUpdater?(currentVarB + 50)

      let currentVarA: Int = variablesStorage.getVariableValue(
        cardId: cardId,
        name: varA
      ) ?? 0
      providerB.widthUpdater?(currentVarA + 50)
    }

    let varAAfter: Int? = variablesStorage.getVariableValue(cardId: cardId, name: varA)
    let varBAfter: Int? = variablesStorage.getVariableValue(cardId: cardId, name: varB)
    #expect(varAAfter == 150)
    #expect(varBAfter == 200)

    providerA.widthUpdater?((varBAfter ?? 0) + 50)
    providerB.widthUpdater?((varAAfter ?? 0) + 50)
    #expect(
      variablesStorage.getVariableValue(cardId: cardId, name: varA) == varAAfter
    )
    #expect(
      variablesStorage.getVariableValue(cardId: cardId, name: varB) == varBAfter
    )
  }

  @Test
  func afterReset_NewCycleCanUpdate() {
    let handler = DivLayoutProviderHandler(variablesStorage: variablesStorage)
    let widthVariableName = DivVariableName(rawValue: "var_a")

    let block = handler.apply(
      block: EmptyBlock.zeroSized,
      path: path,
      widthVariableName: widthVariableName,
      heightVariableName: nil
    )

    guard let sizeProviderBlock = block as? SizeProviderBlock else {
      Issue.record("Expected SizeProviderBlock but got \(type(of: block))")
      return
    }

    sizeProviderBlock.widthUpdater?(100)
    sizeProviderBlock.widthUpdater?(200)
    sizeProviderBlock.widthUpdater?(300)
    sizeProviderBlock.widthUpdater?(400) // blocked

    let widthBeforeReset: Int? = variablesStorage.getVariableValue(
      cardId: cardId,
      name: widthVariableName
    )
    #expect(widthBeforeReset == 300)

    handler.resetUpdatedVariables()

    sizeProviderBlock.widthUpdater?(500)

    let widthAfterReset: Int? = variablesStorage.getVariableValue(
      cardId: cardId,
      name: widthVariableName
    )
    #expect(widthAfterReset == 500)
  }
}
