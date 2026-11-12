@testable import DivKit
import DivKitTestsSupport
@testable import LayoutKit
import VGSL
import XCTest

final class DivInputExtensionsTests: XCTestCase {
  private let variableStorage = DivVariableStorage()
  private var context: DivBlockModelingContext!

  override func setUp() {
    variableStorage.put(name: "input_variable", value: .string("Hello!"))
    context = DivBlockModelingContext(variableStorage: variableStorage)
  }

  func test_WithTextVariable() {
    let block = makeBlock(
      divInput(textVariable: "input_variable"),
      context: context
    )

    let expectedBlock = makeExpectedBlockBlock(
      accessibilityElement: accessibility(label: "Hello!"),
      context: context
    )

    assertEqual(block, expectedBlock)
  }

  func test_WithAccessibility() {
    let block = makeBlock(
      divInput(
        accessibility: DivAccessibility(description: .value("Description")),
        textVariable: "input_variable"
      ),
      context: context
    )

    let expectedBlock = makeExpectedBlockBlock(
      accessibilityElement: accessibility(label: "Description"),
      context: context
    )

    assertEqual(block, expectedBlock)
  }

  func test_WithPasswordType() {
    let block = makeBlock(
      divInput(
        keyboardType: .password,
        textVariable: "input_variable"
      ),
      context: context
    )

    let expectedBlock = makeExpectedBlockBlock(
      accessibilityElement: accessibility(),
      autocorrection: false,
      isSecure: true,
      multilineMode: false,
      context: context
    )

    assertEqual(block, expectedBlock)
  }

  func test_WithPasswordTypeAndAccessibility() {
    let block = makeBlock(
      divInput(
        accessibility: DivAccessibility(description: .value("Description")),
        keyboardType: .password,
        textVariable: "input_variable"
      ),
      context: context
    )

    let expectedBlock = makeExpectedBlockBlock(
      accessibilityElement: accessibility(label: "Description"),
      autocorrection: false,
      isSecure: true,
      multilineMode: false,
      context: context
    )

    assertEqual(block, expectedBlock)
  }

  func test_WithPasswordType_ForwardsAccessibilityMetadataWithoutDescription() throws {
    for text in ["", "secret"] {
      variableStorage.put(name: "input_variable", value: .string(text))
      for description in [nil, "", "Password"] as [String?] {
        let block = makeBlock(
          divInput(
            accessibility: DivAccessibility(
              description: description.map { .value($0) },
              hint: .value("At least eight characters"),
              stateDescription: .value("Required"),
              type: .editText
            ),
            keyboardType: .password,
            textVariable: "input_variable"
          ),
          context: context
        )
        let wrapper = try XCTUnwrap(block.child as? DecoratingBlock)
        let input = try XCTUnwrap(wrapper.child as? TextInputBlock)
        let usesNativeAccessibility = description != "Password"

        XCTAssertEqual(
          input.accessibilityHint,
          usesNativeAccessibility ? "At least eight characters" : nil
        )
        XCTAssertEqual(
          input.accessibilityStateDescription,
          usesNativeAccessibility ? "Required" : nil
        )
        XCTAssertEqual(
          wrapper.accessibilityElement?.strings.label,
          usesNativeAccessibility ? nil : "Password"
        )
      }
    }
  }

  func test_WithNonSecureType_DoesNotForwardAccessibilityMetadata() throws {
    let block = makeBlock(
      divInput(
        accessibility: DivAccessibility(
          hint: .value("Hint"),
          stateDescription: .value("Required")
        ),
        textVariable: "input_variable"
      ),
      context: context
    )
    let wrapper = try XCTUnwrap(block.child as? DecoratingBlock)
    let input = try XCTUnwrap(wrapper.child as? TextInputBlock)

    XCTAssertNil(input.accessibilityHint)
    XCTAssertNil(input.accessibilityStateDescription)
    XCTAssertEqual(wrapper.accessibilityElement?.strings.hint, "Hint")
    XCTAssertEqual(wrapper.accessibilityElement?.strings.value, "Required")
  }

  func test_WithPasswordTypeAndEmptyDescription() {
    let block = makeBlock(
      divInput(
        accessibility: DivAccessibility(description: .value("")),
        keyboardType: .password,
        textVariable: "input_variable"
      ),
      context: context
    )

    let expectedBlock = makeExpectedBlockBlock(
      accessibilityElement: accessibility(),
      autocorrection: false,
      isSecure: true,
      multilineMode: false,
      context: context
    )

    assertEqual(block, expectedBlock)
  }

  func test_WithPasswordTypeAndNonAutoAccessibilityType() {
    let block = makeBlock(
      divInput(
        accessibility: DivAccessibility(type: .editText),
        keyboardType: .password,
        textVariable: "input_variable"
      ),
      context: context
    )

    let expectedBlock = makeExpectedBlockBlock(
      accessibilityElement: accessibility(),
      autocorrection: false,
      isSecure: true,
      multilineMode: false,
      context: context
    )

    assertEqual(block, expectedBlock)
  }

  func test_WithEmptyDescription() {
    let block = makeBlock(
      divInput(
        accessibility: DivAccessibility(description: .value("")),
        textVariable: "input_variable"
      ),
      context: context
    )

    let expectedBlock = makeExpectedBlockBlock(
      accessibilityElement: accessibility(label: ""),
      context: context
    )

    assertEqual(block, expectedBlock)
  }

  func test_WithPasswordType_DoesNotExposeValueInMergedContainerLabel() throws {
    let container = divContainer(
      accessibility: DivAccessibility(mode: .value(.merge)),
      items: [
        divText(text: "Title"),
        divInput(
          keyboardType: .password,
          textVariable: "input_variable"
        ),
      ]
    )

    let block = try container.value.makeBlock(context: context)

    XCTAssertEqual(block.accessibilityElement?.strings.label, "Title")
  }

  func test_WithKeyboardTypeExpression() {
    variableStorage.put(name: "is_masked", value: .bool(true))
    let div = divInput(
      keyboardTypeExpression: "@{is_masked ? 'password' : 'single_line_text'}",
      textVariable: "input_variable"
    )

    let maskedBlock = makeBlock(div, context: context)
    XCTAssertNil(maskedBlock.inputAccessibilityLabel)

    variableStorage.put(name: "is_masked", value: .bool(false))
    let visibleBlock = makeBlock(div, context: context)
    XCTAssertEqual(visibleBlock.inputAccessibilityLabel, "Hello!")
  }

  func test_WithLocalKeyboardType_OnFirstBuild_DoesNotExposePassword() throws {
    variableStorage.put(name: "input_type", value: .string("single_line_text"))
    variableStorage.put(name: "password", value: .string("secret"))
    let div = Div.divInput(DivInput(
      keyboardType: expression("@{input_type}"),
      textVariable: "password",
      variables: [variable("input_type", "password")]
    ))

    // Build once with a fresh context, without preloading the input's local variables.
    let block = makeBlock(div, context: context)
    let wrapper = try XCTUnwrap(block.child as? DecoratingBlock)
    let input = try XCTUnwrap(wrapper.child as? TextInputBlock)

    XCTAssertTrue(input.isSecure)
    XCTAssertEqual(input.textValue.value, "secret")
    XCTAssertNil(wrapper.accessibilityElement?.strings.label)
  }

  func test_WithLocalPasswordAndEditTextAccessibility_OnFirstBuild_DoesNotExposePassword() throws {
    variableStorage.put(name: "input_type", value: .string("single_line_text"))
    variableStorage.put(name: "password", value: .string("outer"))
    let div = Div.divInput(DivInput(
      accessibility: DivAccessibility(type: .editText),
      keyboardType: expression("@{input_type}"),
      textVariable: "password",
      variables: [
        variable("input_type", "password"),
        variable("password", "local_secret"),
      ]
    ))

    let block = makeBlock(div, context: context)
    let wrapper = try XCTUnwrap(block.child as? DecoratingBlock)
    let input = try XCTUnwrap(wrapper.child as? TextInputBlock)

    XCTAssertTrue(input.isSecure)
    XCTAssertEqual(input.textValue.value, "local_secret")
    XCTAssertNil(wrapper.accessibilityElement?.strings.label)
  }

  func test_WithHideSuggestionsBar() {
    let block = makeBlock(
      divInput(
        allowSuggestionsBar: false,
        textVariable: "input_variable"
      ),
      context: context
    )

    let expectedBlock = makeExpectedBlockBlock(
      autocorrection: false,
      spellChecking: false,
      context: context
    )

    assertEqual(block, expectedBlock)
  }
}

extension StateBlock {
  fileprivate var inputAccessibilityLabel: String? {
    (child as? DecoratingBlock)?.accessibilityElement?.strings.label
  }
}

private func makeExpectedBlockBlock(
  accessibilityElement: AccessibilityElement? = nil,
  autocorrection: Bool = true,
  isSecure: Bool = false,
  multilineMode: Bool = true,
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
        multiLineMode: multilineMode,
        path: .root + "0" + "input",
        layoutDirection: .leftToRight,
        autocorrection: autocorrection,
        isSecure: isSecure,
        spellChecking: spellChecking
      ),
      accessibilityElement: accessibilityElement ?? accessibility(label: "Hello!"),
      path: .root + "0" + "input"
    ),
    ids: []
  )
}
