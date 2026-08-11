@testable import LayoutKit
import VGSL
import XCTest

/// Regression: after autocomplete-style range replace in a masked input,
/// cursor must land at the end of the inserted text (not at the start of the replaced range).
final class MaskedInputViewModelTests: XCTestCase {
  func test_insertReplacingPrefix_cursorAtEndOfInsertedText() {
    let pipe = SignalPipe<MaskedInputViewModel.Action>()
    let viewModel = MaskedInputViewModel(
      rawText: "Hel",
      maskValidator: makePassthroughMaskValidator(),
      signal: pipe.signal
    )

    waitUntil(viewModel) { $0.formattedText == "Hel" }

    // QuickType replacing "Hel" (0..<3) with "Help".
    pipe.send(.insert(string: "Help", range: 0..<3))

    waitUntil(viewModel) {
      $0.formattedText == "Help" && $0.cursorPosition?.location == 4
    }

    XCTAssertEqual(viewModel.formattedText, "Help")
    XCTAssertEqual(viewModel.cursorPosition?.location, 4)
    XCTAssertEqual(viewModel.cursorPosition?.length, 0)
  }

  func test_insertReplacingMidRange_cursorAfterInsertedText() {
    let pipe = SignalPipe<MaskedInputViewModel.Action>()
    let viewModel = MaskedInputViewModel(
      rawText: "abcdef",
      maskValidator: makePassthroughMaskValidator(),
      signal: pipe.signal
    )

    waitUntil(viewModel) { $0.formattedText == "abcdef" }

    // Replacing "cd" (2..<4) with "XY" → "abXYef", cursor after "XY" at 4.
    pipe.send(.insert(string: "XY", range: 2..<4))

    waitUntil(viewModel) {
      $0.formattedText == "abXYef" && $0.cursorPosition?.location == 4
    }

    XCTAssertEqual(viewModel.formattedText, "abXYef")
    XCTAssertEqual(viewModel.cursorPosition?.location, 4)
    XCTAssertEqual(viewModel.cursorPosition?.length, 0)
  }
}

private func waitUntil(
  _ viewModel: MaskedInputViewModel,
  timeout: TimeInterval = 1,
  file: StaticString = #filePath,
  line: UInt = #line,
  predicate: @escaping (MaskedInputViewModel) -> Bool
) {
  let expectation = XCTestExpectation(description: "view model state")
  expectation.assertForOverFulfill = false
  let disposePool = AutodisposePool()
  Signal.combineLatest(
    viewModel.$formattedText.currentAndNewValues,
    viewModel.$cursorPosition.currentAndNewValues
  ).addObserver { _, _ in
    if predicate(viewModel) {
      expectation.fulfill()
    }
  }.dispose(in: disposePool)

  if predicate(viewModel) {
    expectation.fulfill()
  }

  let result = XCTWaiter.wait(for: [expectation], timeout: timeout)
  XCTAssertEqual(result, .completed, file: file, line: line)
}

private func makePassthroughMaskValidator() -> MaskValidator {
  // Ticket-like mask: every character is accepted; formatted text mirrors raw input.
  MaskValidator(
    formatter: FixedLengthMaskFormatter(
      pattern: String(repeating: "#", count: 64),
      alwaysVisible: false,
      patternElements: [
        PatternElement(
          key: "#",
          regex: try! NSRegularExpression(pattern: ".*"),
          placeholder: "_"
        ),
      ]
    )
  )
}
