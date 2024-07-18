import Foundation
import VGSL

public final class PhoneMaskFormatter: MaskFormatter {
  private let masksByCountryCode: JSONDictionary
  private let extraSymbols: String

  public init(masksByCountryCode: JSONDictionary, extraSymbols: String) {
    self.masksByCountryCode = masksByCountryCode
    self.extraSymbols = extraSymbols
  }

  public func formatted(rawText: String, rawCursorPosition: CursorData? = nil) -> InputData {
    var text = [Character]()
    var rawData = [InputData.RawCharacter]()
    var stringIndex = rawText.startIndex
    var newCursorPosition: CursorPosition?
    let mask = findMask(for: rawText)
    maskLoop: for element in mask {
      if element.isPlaceHolder {
        guard stringIndex < rawText.endIndex else {
          break
        }
        while !rawText[stringIndex].isNumber {
          stringIndex = rawText.index(after: stringIndex)
          if stringIndex == rawText.endIndex {
            break maskLoop
          }
        }
        text.append(rawText[stringIndex])
        let textString = String(text)
        rawData
          .append(.init(
            char: rawText[stringIndex],
            index: textString.index(before: textString.endIndex)
          ))
        stringIndex = rawText.index(after: stringIndex)
      } else {
        text.append(element)
      }
    }
    let textString = String(text)
    if let rawCursorPosition {
      let cursorIndex = rawCursorPosition.cursorPosition.rawValue
      if cursorIndex < rawText.endIndex {
        let pos = rawText.distance(from: rawText.startIndex, to: cursorIndex)
        if pos >= rawData.count {
          newCursorPosition = .init(rawValue: textString.endIndex)
        } else if rawCursorPosition.afterNonDecodingSymbols || pos == 0 {
          newCursorPosition = .init(rawValue: rawData[pos].index)
        } else {
          newCursorPosition = .init(rawValue: textString.index(after: rawData[pos - 1].index))
        }
      } else {
        newCursorPosition = .init(rawValue: textString.endIndex)
      }
    }
    return InputData(text: textString, cursorPosition: newCursorPosition, rawData: rawData)
  }

  private func findMask(for rawText: String) -> String {
    guard !rawText.isEmpty else {
      return ""
    }
    var resultMask = ""
    var masksByCountryCode = masksByCountryCode
    rawTextLoop: for ch in rawText {
      guard let innerObject = masksByCountryCode[String(ch)] else {
        break
      }
      switch innerObject {
      case let .object(innerDict):
        if let mask = innerDict.maskValue {
          resultMask = mask
          break rawTextLoop
        } else {
          masksByCountryCode = innerDict
        }
      case .array, .bool, .null, .number, .string:
        assertionFailure("Unexpected mask value for \(rawText)")
      }
    }
    if resultMask.isEmpty {
      guard let defaultMask = masksByCountryCode.defaultMask else {
        assertionFailure("No default mask for \(rawText)")
        return ""
      }
      resultMask = defaultMask
    }

    return resultMask + extraSymbols
  }

  public func equals(_ other: MaskFormatter) -> Bool {
    guard let other = other as? PhoneMaskFormatter else {
      return false
    }
    return self.masksByCountryCode == other.masksByCountryCode
  }
}

extension Character {
  fileprivate var isPlaceHolder: Bool {
    self == "0"
  }
}

extension JSONDictionary {
  fileprivate var maskValue: String? {
    if case let .string(mask) = self["value"] {
      mask
    } else {
      nil
    }
  }

  fileprivate var defaultMask: String? {
    guard case let .object(defaultMaskDict) = self["*"],
          case let .string(defaultMask) = defaultMaskDict["value"] else {
      return nil
    }
    return defaultMask
  }
}
