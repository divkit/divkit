#if os(iOS)
import Foundation
import UIKit
import VGSL

public class MaskedInputViewModel {
  public enum Action: Equatable {
    case insert(string: String, range: Range<String.Index>)
    case clear(pos: String.Index)
    case clearRange(range: Range<String.Index>)
  }

  @ObservableVariable var text: String
  @ObservableVariable var cursorPosition: NSRange?
  @ObservableProperty var rawText: String
  @ObservableProperty var maskValidator: MaskValidator
  @ObservableProperty var typo: Typo?

  @ObservableProperty private var rawCursorPosition: CursorData?
  private let disposePool = AutodisposePool()

  init(
    rawText: String,
    maskValidator: MaskValidator,
    typo: Typo? = nil,
    signal: Signal<Action>
  ) {
    let rawText = ObservableProperty(initialValue: rawText)
    let maskValidator = ObservableProperty(initialValue: maskValidator)
    let typo = ObservableProperty(initialValue: typo)
    let rawCursorData: ObservableProperty<CursorData?> =
      ObservableProperty(initialValue: nil)
    self._rawText = rawText
    self._maskValidator = maskValidator
    self._typo = typo
    self._rawCursorPosition = rawCursorData
    let resultSignal = Signal.combineLatest(
      rawText.currentAndNewValues.skipRepeats(),
      maskValidator.currentAndNewValues.skipRepeats(),
      rawCursorData.currentAndNewValues.skipRepeats(),
      typo.currentAndNewValues.skipRepeats()
    ).map { binding, maskValidator, cursor, _ in
      let res = maskValidator.formatted(rawText: binding, rawCursorPosition: cursor)
      return (
        res.text,
        res.cursorPosition.flatMap { NSRange($0.rawValue..<$0.rawValue, in: res.text) }
      )
    }
    _text = ObservableVariable(initialValue: "", newValues: resultSignal.map(\.0))
    _cursorPosition = ObservableVariable<NSRange?>(
      initialValue: nil,
      newValues: resultSignal.map(\.1)
    )

    maskValidator.newValues.skipRepeats().addObserver { [weak self] maskValidator in
      guard let self else { return }
      self.rawText = maskValidator.formatted(rawText: self.rawText, rawCursorPosition: nil).rawText
    }.dispose(in: disposePool)

    signal.addObserver { [weak self] action in
      guard let self else { return }
      let newString: String
      let newCursorPosition: CursorData?
      switch action {
      case let .clear(pos: pos):
        (newString, newCursorPosition) = self.maskValidator.removeSymbols(
          at: pos,
          data: self.maskValidator.formatted(rawText: self.rawText)
        )
      case let .clearRange(range: range):
        (newString, newCursorPosition) = self.maskValidator.removeSymbols(
          at: range,
          data: self.maskValidator.formatted(rawText: self.rawText)
        )
      case let .insert(string: string, range: range):
        (newString, newCursorPosition) = self.maskValidator.addSymbols(
          at: range,
          data: self.maskValidator.formatted(rawText: self.rawText),
          string: string
        )
      }
      self.rawText = self.maskValidator.formatted(rawText: newString).rawText
      self.rawCursorPosition = newCursorPosition
    }.dispose(in: disposePool)
  }
}
#endif
