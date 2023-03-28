import CommonCorePublic

struct Grid {
  typealias GridItem = GridBlock.Item
  let itemsIndices: [[Int]]

  var rowCount: Int { itemsIndices.count }
  var columnCount: Int { itemsIndices[0].count }

  // swiftformat:disable:next redundantParens
  func forEach(_ block: ((row: Int, column: Int)) -> Void) {
    for row in itemsIndices.indices {
      for column in itemsIndices[0].indices {
        block((row, column))
      }
    }
  }

  init(spans: [GridBlock.Span], columnCount: Int) throws {
    guard !spans.isEmpty else { throw Error.emptyItems }
    guard columnCount > 0 else { throw Error.invalidColumnCount(columnCount) }

    let resultInitialRow = [Int?](repeating: nil, times: try! UInt(value: columnCount))
    var result = [resultInitialRow]

    var iterator = GridIterator(columnCount: columnCount)

    func reserveRowsUpTo(_ lastIndex: Int) {
      while lastIndex >= result.count {
        result.append(resultInitialRow)
      }
    }

    for (index, span) in spans.enumerated() {
      while result[iterator.current] != nil {
        reserveRowsUpTo(iterator.next().row)
      }

      guard iterator.current.column + span.columns <= columnCount else {
        throw Error.unableToFormGrid(.noSpaceForItem(at: index))
      }

      reserveRowsUpTo(iterator.current.row + span.rows - 1)
      for rowOffset in 0..<span.rows {
        for columnOffset in 0..<span.columns {
          let location = iterator.current + (rowOffset, columnOffset)
          guard result[location] == nil else {
            throw Error.unableToFormGrid(.noSpaceForItem(at: index))
          }

          result[location] = index
        }
      }
    }

    itemsIndices = try result.enumerated().map { rowIdx, row in
      try row.enumerated().map { columnIdx, value in
        if let result = value {
          return result
        } else {
          throw Error.unableToFormGrid(.emptyCell(at: .init(row: rowIdx, column: columnIdx)))
        }
      }
    }
  }
}

private struct GridIterator {
  typealias Element = (row: Int, column: Int)

  private let columnCount: Int
  private(set) var current: Element = (0, 0)

  init(columnCount: Int) {
    self.columnCount = columnCount
  }

  mutating func next() -> Element {
    current.column += 1
    if current.column >= columnCount {
      current.row += 1
      current.column = 0
    }
    return current
  }
}

private typealias Error = GridBlock.Error.Payload
