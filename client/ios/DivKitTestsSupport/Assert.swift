import XCTest

public func assertEqual<T: Equatable>(_ actual: T, _ expected: T) {
  if actual != expected {
    XCTFail("Objects are not equal: \n" + diffBetweenDumps(actual, expected))
  }
}

private struct ElementWithAttachment<Element: Hashable, Attachment>: Hashable {
  let element: Element
  let attachment: Attachment

  func hash(into hasher: inout Hasher) {
    hasher.combine(element)
  }
}

private func == <T, U>(
  lhs: ElementWithAttachment<T, U>,
  rhs: ElementWithAttachment<T, U>
) -> Bool {
  lhs.element == rhs.element
}

private func diffBetween<T: Collection>(
  _ a: T,
  _ b: T
) -> String where T.Iterator.Element: Hashable {
  var aIndex = a.startIndex
  var bIndex = b.startIndex
  var diff = ""

  while aIndex != a.endIndex, bIndex != b.endIndex {
    let hexAddressRegex = "0x[\\dA-Za-z]{8}"
    let mirrorElementNumberRegex = "#[\\d]+$"
    let aStr = "\(a[aIndex])".filteringRegexes(hexAddressRegex, mirrorElementNumberRegex)
    let bStr = "\(b[bIndex])".filteringRegexes(hexAddressRegex, mirrorElementNumberRegex)

    if aStr == bStr {
      aIndex = a.index(after: aIndex)
      bIndex = b.index(after: bIndex)
      continue
    }

    let aSlice = a[aIndex..<a.endIndex]
    let bSlice = b[bIndex..<b.endIndex]
    let remainingAElements = Set(aSlice)
    let remainingBElements = Set(bSlice)
    let commonElements = remainingAElements.intersection(remainingBElements)

    let currentAIndex: T.Index
    let currentBIndex: T.Index

    if !commonElements.isEmpty {
      let elementsWithDistanceInA = Set(aSlice.enumerated().compactMap {
        commonElements
          .contains($0.element) ?
          ElementWithAttachment(element: $0.element, attachment: $0.offset) :
          nil
      })

      let elementsWithDistance: [(index: Int, element: T.Iterator.Element)] = bSlice.enumerated()
        .compactMap {
          if let index = elementsWithDistanceInA
            .firstIndex(of: ElementWithAttachment(element: $0.element, attachment: 0)) {
            return (
              index: max(elementsWithDistanceInA[index].attachment, $0.offset),
              element: $0.element
            )
          }

          return nil
        }
      let closestElement = elementsWithDistance.min { $0.index < $1.index }!.element

      currentAIndex = aSlice.firstIndex(of: closestElement)!
      currentBIndex = bSlice.firstIndex(of: closestElement)!
    } else {
      currentAIndex = a.endIndex
      currentBIndex = b.endIndex
    }

    diff += "\(aIndex)c\(bIndex)\n"

    let contextLength = 10

    for element in
      a[(
        a.index(aIndex, offsetBy: -contextLength, limitedBy: a.startIndex) ?? a
          .startIndex
      )..<aIndex] {
      diff += "\t\(element)".trimmedToLength(100) + "\n"
    }

    for element in a[aIndex..<currentAIndex] {
      diff += "-\t\(element)\n"
    }

    for element in b[bIndex..<currentBIndex] {
      diff += "+\t\(element)\n"
    }

    for element in
      a[currentAIndex..<(
        a.index(currentAIndex, offsetBy: contextLength, limitedBy: a.endIndex) ?? a
          .endIndex
      )] {
      diff += "\t\(element)".trimmedToLength(100) + "\n"
    }

    aIndex = currentAIndex
    bIndex = currentBIndex
  }

  for element in a[aIndex..<a.endIndex] {
    diff += "-\t\(element)\n"
  }

  for element in b[bIndex..<b.endIndex] {
    diff += "+\t\(element)\n"
  }

  return diff
}

private func diffBetweenDumps<T>(_ a: T, _ b: T) -> String {
  var aDump = ""
  dump(a, to: &aDump)
  var bDump = ""
  dump(b, to: &bDump)
  let aRows = aDump.components(separatedBy: "\n")
  let bRows = bDump.components(separatedBy: "\n")
  return diffBetween(aRows, bRows)
}

extension String {
  fileprivate func trimmedToLength(_ length: Int) -> String {
    let result = self[0..<min(length, count)]
    return result + ((result.count < count) ? "..." : "")
  }

  fileprivate func filteringRegexes(_ regexes: String...) -> String {
    var result = self
    for regex in regexes {
      result = result.replacingOccurrences(of: regex, with: "", options: [.regularExpression])
    }
    return result
  }
}
