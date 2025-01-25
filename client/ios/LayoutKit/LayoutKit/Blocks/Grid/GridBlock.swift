import CoreGraphics

import VGSL

public final class GridBlock: BlockWithTraits, BlockWithLayout {
  public struct Span: Equatable {
    public let rows: Int
    public let columns: Int
    public static let `default` = Span()

    private init() {
      rows = 1
      columns = 1
    }

    public init(rows: Int = 1, columns: Int = 1) {
      self.rows = rows
      self.columns = columns
    }

    func validate() throws {
      if rows < 1 {
        throw BlockError("Grid block error: invalid rows span \(rows) < 1")
      }
      if columns < 1 {
        throw BlockError("Grid block error: invalid columns span \(columns) < 1")
      }
    }
  }

  public struct Item: Equatable {
    public struct Weight: Equatable {
      public let column: LayoutTrait.Weight?
      public let row: LayoutTrait.Weight?

      public init(column: LayoutTrait.Weight? = nil, row: LayoutTrait.Weight? = nil) {
        self.column = column
        self.row = row
      }
    }

    public let span: Span
    public let weight: Weight
    public var contents: Block
    public let alignment: BlockAlignment2D

    public init(
      span: Span = .default,
      weight: Weight,
      contents: Block,
      alignment: BlockAlignment2D = .default
    ) {
      self.span = span
      self.weight = weight
      self.contents = contents
      self.alignment = alignment
    }
  }

  public enum Direction: String {
    case horizontal
    case vertical
  }

  public struct Layout {
    public let size: CGSize
    public let itemFrames: [CGRect]
  }

  public let widthTrait: LayoutTrait
  public let heightTrait: LayoutTrait
  public let contentAlignment: BlockAlignment2D
  public let items: [Item]
  public let columnCount: Int
  let grid: Grid

  private var cachedIntrinsicWidth: CGFloat?
  private var cachedIntrinsicHeight: (width: CGFloat, height: CGFloat)?

  private init(
    widthTrait: LayoutTrait,
    heightTrait: LayoutTrait,
    contentAlignment: BlockAlignment2D,
    items: [Item],
    columnCount: Int,
    grid: Grid
  ) {
    self.widthTrait = widthTrait
    self.heightTrait = heightTrait
    self.contentAlignment = contentAlignment
    self.items = items
    self.columnCount = columnCount
    self.grid = grid
  }

  public convenience init(
    widthTrait: LayoutTrait,
    heightTrait: LayoutTrait,
    contentAlignment: BlockAlignment2D = .default,
    items: [Item],
    columnCount: Int
  ) throws {
    try self.init(
      widthTrait: widthTrait,
      heightTrait: heightTrait,
      contentAlignment: contentAlignment,
      items: items,
      columnCount: columnCount,
      grid: Grid(spans: items.map(\.span), columnCount: columnCount)
    )
    try validateLayoutTraits()
  }

  public func getImageHolders() -> [ImageHolder] {
    items.flatMap { $0.contents.getImageHolders() }
  }

  private func validateLayoutTraits() throws {
    if case .intrinsic = widthTrait, Layout.isGrid(
      grid,
      with: items,
      resizableAtDirection: .horizontal
    ) {
      throw BlockError(
        "Grid block error: cannot create horizontally resizable grid with intrinsic width trait"
      )
    }

    if case .intrinsic = heightTrait, Layout.isGrid(
      grid,
      with: items,
      resizableAtDirection: .vertical
    ) {
      throw BlockError(
        "Grid block error: cannot create vertically resizable grid with intrinsic height trait"
      )
    }
  }

  private func makeLayout(constrainedTo size: CGSize = .infinite) -> Layout {
    Layout(
      size: size,
      items: items,
      grid: grid,
      contentAlignment: contentAlignment
    )
  }

  public var intrinsicContentWidth: CGFloat {
    if case let .fixed(value) = widthTrait {
      return value
    }

    if let cached = cachedIntrinsicWidth {
      return cached
    }

    var result = Layout.calculateIntrinsicColumnWidths(items: items, grid: grid).reduce(0, +)

    if case let .intrinsic(_, minSize, maxSize) = widthTrait {
      result = clamp(result, min: minSize, max: maxSize)
    }

    cachedIntrinsicWidth = result
    return result
  }

  public func intrinsicContentHeight(forWidth width: CGFloat) -> CGFloat {
    if case let .fixed(value) = heightTrait {
      return value
    }

    if let cached = cachedIntrinsicHeight,
       cached.width.isApproximatelyEqualTo(width) {
      return cached.height
    }

    var result = makeLayout(constrainedTo: CGSize(width: width, height: .infinity)).size.height

    if case let .intrinsic(_, minSize, maxSize) = widthTrait {
      result = clamp(result, min: minSize, max: maxSize)
    }

    cachedIntrinsicHeight = (width: width, height: result)
    return result
  }

  public func equals(_ other: Block) -> Bool {
    guard let other = other as? GridBlock else {
      return false
    }

    return widthTrait == other.widthTrait
      && heightTrait == other.heightTrait
      && contentAlignment == other.contentAlignment
      && items == other.items
      && columnCount == other.columnCount
    // grid is calculated from items + columnCount, so no need to check it
  }

  func laidOutHierarchy(for size: CGSize) -> (GridBlock, GridBlock.Layout) {
    let layout = makeLayout(constrainedTo: size)
    let laidOutItems = zip(items, layout.itemFrames).map { item, frame in
      modified(item) { $0.contents = $0.contents.laidOut(for: frame.size) }
    }
    let block = GridBlock(
      widthTrait: .fixed(size.width),
      heightTrait: .fixed(size.height),
      contentAlignment: contentAlignment,
      items: laidOutItems,
      columnCount: columnCount,
      grid: grid
    )

    return (block, layout)
  }
}

extension GridBlock {
  public func updated(withStates states: BlocksState) throws -> GridBlock {
    let newItems = try items.map {
      try GridBlock.Item(
        span: $0.span,
        weight: $0.weight,
        contents: $0.contents.updated(withStates: states),
        alignment: $0.alignment
      )
    }

    let itemsChanged = zip(newItems, items).contains { $0.contents !== $1.contents }

    return itemsChanged
      ? GridBlock(
        widthTrait: widthTrait,
        heightTrait: heightTrait,
        contentAlignment: contentAlignment,
        items: newItems,
        columnCount: columnCount,
        grid: grid
      )
      : self
  }
}

extension GridBlock: ElementFocusUpdating {
  public func updated(path: UIElementPath, isFocused: Bool) throws -> GridBlock {
    let newItems = try items.map {
      try GridBlock.Item(
        span: $0.span,
        weight: $0.weight,
        contents: $0.contents.updated(path: path, isFocused: isFocused),
        alignment: $0.alignment
      )
    }

    let itemsChanged = zip(newItems, items).contains { $0.contents !== $1.contents }

    return itemsChanged
      ? GridBlock(
        widthTrait: widthTrait,
        heightTrait: heightTrait,
        contentAlignment: contentAlignment,
        items: newItems,
        columnCount: columnCount,
        grid: grid
      )
      : self
  }
}

extension GridBlock.Item {
  public static func ==(lhs: GridBlock.Item, rhs: GridBlock.Item) -> Bool {
    lhs.span == rhs.span
      && lhs.weight == rhs.weight
      && lhs.contents == rhs.contents
      && lhs.alignment == rhs.alignment
  }
}
