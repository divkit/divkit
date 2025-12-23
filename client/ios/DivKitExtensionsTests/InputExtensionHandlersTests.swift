@testable import DivKit
@testable import DivKitExtensions
import DivKitTestsSupport
import Foundation
@testable import LayoutKit
import Testing
import VGSL

@Suite
struct InputExtensionHandlersTests {
  private let variableStorage = DivVariableStorage()
  private let context: DivBlockModelingContext

  init() {
    context = DivBlockModelingContext(variableStorage: variableStorage)
    variableStorage.put(name: "input_variable", value: .string("Hello!"))
  }

  @Test
  func applyAllExtensionsToTextInput() throws {
    let contextWithExtensions = DivBlockModelingContext(
      extensionHandlers: [
        InputPropertiesExtensionHandler(),
        InputAutocorrectionExtensionHandler(),
      ],
      variableStorage: variableStorage
    )

    let block = makeBlock(
      divInput(
        extensions: [
          DivExtension(
            id: "input_properties",
            params: [
              "enables_return_key_automatically": true,
              "spell_checking": true,
            ]
          ),
          DivExtension(
            id: "input_autocorrection",
            params: [
              "enabled": true,
            ]
          ),
        ],
        textVariable: "input_variable"
      ),
      context: contextWithExtensions
    )

    let expectedBlock = makeExpectedBlock(
      autocorrection: true,
      enablesReturnKeyAutomatically: true,
      spellChecking: true,
      context: context
    )

    expectEqual(block, expectedBlock)
  }
}

private func makeExpectedBlock(
  autocorrection: Bool = true,
  enablesReturnKeyAutomatically: Bool = false,
  isSecure: Bool = false,
  spellChecking: Bool? = nil,
  context: DivBlockModelingContext
) -> StateBlock {
  StateBlock(
    child: DecoratingBlock(
      child: TextInputBlock(
        hint: NSAttributedString(string: ""),
        textValue: context.makeBinding(variableName: "input_variable", defaultValue: ""),
        textTypo: Typo(font: fontSpecifiers.text.font(weight: .regular, size: 12))
          .with(color: Color.colorWithARGBHexCode(0xFF_00_00_00)),
        path: .root + "0" + "input",
        layoutDirection: .leftToRight,
        autocorrection: autocorrection,
        isSecure: isSecure,
        enablesReturnKeyAutomatically: enablesReturnKeyAutomatically,
        spellChecking: spellChecking
      ),
      accessibilityElement: accessibility(label: "Hello!")
    ),
    ids: []
  )
}
