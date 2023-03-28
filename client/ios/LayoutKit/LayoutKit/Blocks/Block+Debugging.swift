import CoreGraphics
import Foundation

import CommonCorePublic

extension ContainerBlock.LayoutDirection: CustomDebugStringConvertible {
  public var debugDescription: String {
    switch self {
    case .horizontal:
      return "Horizontal"
    case .vertical:
      return "Vertical"
    }
  }
}

extension BlockAlignment2D: CustomDebugStringConvertible {
  public var debugDescription: String {
    if horizontal != vertical {
      return "V: \(vertical) H: \(horizontal)"
    } else {
      return horizontal.debugDescription
    }
  }
}

extension TextBlock: CustomDebugStringConvertible {
  public var debugDescription: String {
    """
    Text \(widthTrait) x \(heightTrait) {
    \(text.prettyDebugDescription.indented())
    }
    """
  }
}

extension ImageBlock: CustomDebugStringConvertible {
  public var debugDescription: String {
    """
    Image \(widthTrait) x \(height) {
      Content mode: \(contentMode)
      Holder: \(imageHolder)
    }
    """
  }
}

extension AnimatableImageBlock: CustomDebugStringConvertible {
  public var debugDescription: String {
    """
    Image \(widthTrait) x \(height) {
      Content mode: \(contentMode)
      Holder: \(imageHolder)
    }
    """
  }
}

extension SeparatorBlock: CustomDebugStringConvertible {
  public var debugDescription: String {
    "Separator \(widthTrait) x \(heightTrait) \(color)"
  }
}

extension SwipeContainerBlock: CustomDebugStringConvertible {
  public var debugDescription: String {
    """
    SwipeContainerBlock
    Actions:
    \(swipeOutActions.debugDescription)
    Content:
    \(child.debugDescription)
    """
  }
}

extension ContainerBlock: CustomDebugStringConvertible {
  public var debugDescription: String {
    var description = """
    \(layoutDirection) Container \(widthTrait) x \(heightTrait) {
      Axial alignment: \(axialAlignment)
      Cross alignment: \(crossAlignment)
    """
    if let animation = contentAnimation {
      description += "\n  Animation: \(animation)"
    }
    if anchorPoint != ContainerBlock.defaultAnchorPoint {
      description += "\n  Anchor:\(anchorPoint)"
    }

    description += "\n  Children: ["
    let lastGap = gaps.last!
    zip(children, gaps).enumerated().forEach { offset, tuple in
      let gap = tuple.1
      if !gap.isApproximatelyEqualTo(0) {
        description += "\n    Gap: \(gap),"
      }
      let child = tuple.0
      description += "\n    Cross alignment: \(child.crossAlignment)\n"
      description += child.content.debugDescription.indented(level: 2)
      if offset != children.count - 1 || !lastGap.isApproximatelyEqualTo(0) {
        description += ","
      }
    }
    if !lastGap.isApproximatelyEqualTo(0) {
      description += "\n    Gap: \(lastGap)"
    }
    description += "\n  ]"

    description += "\n}"
    return description
  }
}

extension ShadedBlock: CustomDebugStringConvertible {
  public var debugDescription: String {
    """
    Shaded {
      Shadow: \(shadow)
      Block: \(block)
    }
    """
  }
}

extension SwitchBlock: CustomDebugStringConvertible {
  public var debugDescription: String {
    var result = """
    Switch {
      On: \(on)
      Enabled: \(enabled)
    """

    if let action = action {
      result += "\n  URL: \(dbgStr(action.url))"
      result += "\n  Path: \(dbgStr(action.path))"
    }

    result += "\n}"
    return result
  }
}

extension PageControlBlock: CustomDebugStringConvertible {
  public var debugDescription: String {
    """
    PageControl {
      Pager ID: \(dbgStr(pagerPath?.pagerId))
      Card ID: \(dbgStr(pagerPath?.cardId))
      Number of pages: \(state.numberOfPages)
      Current page: \(state.currentPage)
    }
    """
  }
}

extension SwitchableContainerBlock: CustomDebugStringConvertible {
  public var debugDescription: String {
    """
    Switchable {
      Selector background color: \(backgroundColor)
      Selected item color:       \(selectedBackgroundColor)
      Title gaps:                \(titleGaps)
      Title to content gap:      \(titleContentGap)
      Left {
        Title: \(items.0.title)
    \(items.0.content.debugDescription.indented(level: 2))
      }
      Right {
        Title: \(items.1.title)
    \(items.1.content.debugDescription.indented(level: 2))
      }
    }
    """
  }
}

extension LayoutTrait: CustomDebugStringConvertible {
  public var debugDescription: String {
    switch self {
    case .intrinsic:
      return "I"
    case let .weighted(value):
      return "R(\(value.rawValue))"
    case let .fixed(value):
      return "\(value)"
    }
  }
}

extension AnimationChanges: CustomDebugStringConvertible {
  public var debugDescription: String {
    switch self {
    case let .transform(values):
      return "transform\(values)"
    case let .opacity(values):
      return "opacity\(values)"
    }
  }
}

extension BlockAnimation: CustomDebugStringConvertible {
  public var debugDescription: String {
    "{ Animation changes:\(changes), KeyTimes: \(keyTimes.map { $0.value }), Duration: \(duration.value) }"
  }
}

extension BlockShadow: CustomDebugStringConvertible {
  public var debugDescription: String {
    "{ Corners: \(cornerRadii), Blur: \(blurRadius), Offset: \(offset), Opacity: \(opacity) }"
  }
}

extension GalleryViewModel.Item: CustomDebugStringConvertible {
  public var debugDescription: String {
    "[\(crossAlignment)] \(content)"
  }
}

extension GalleryBlock: CustomDebugStringConvertible {
  public var debugDescription: String {
    var description = """
    Gallery \(widthTrait) x \(heightTrait) {
    """

    zip(model.metrics.spacings, model.items).forEach { tuple in
      let item = tuple.1
      description += "\n" + item.debugDescription.indented() + ","
      let spacing = tuple.0
      if !spacing.isApproximatelyEqualTo(0) {
        description += "\n  Spacing \(spacing),"
      }
    }
    description += "\n" + model.items.last!.debugDescription.indented()
    description += "\n}"
    return description
  }
}

extension PagerBlock: CustomDebugStringConvertible {
  public var debugDescription: String {
    """
    PagerBlock {
      Pager ID: \(dbgStr(pagerPath?.pagerId))
      Card ID: \(dbgStr(pagerPath?.cardId))
      Number of pages: \(state.numberOfPages)
      Current page: \(state.currentPage)
    }
    """
  }
}

extension BackgroundBlock: CustomDebugStringConvertible {
  public var debugDescription: String {
    """
    Background {
    \(background.debugDescription.indented())
      Content:
    \(child.debugDescription.indented())
    }
    """
  }
}

extension BlockBorder: CustomDebugStringConvertible {
  public var debugDescription: String {
    "{ color: \(color), width: \(width) }"
  }
}

extension TabsBlock: CustomDebugStringConvertible {
  public var debugDescription: String {
    var result = """
    Tabs \(widthTrait) x \(heightTrait) {
      Selected: \(state.selectedPageIndex)
      Title style: \(model.listModel.titleStyle.debugDescription.indented().dropFirst(2))
    """

    if let separator = model.separatorStyle {
      result += "\n  Separator: color \(separator.color), insets \(separator.insets)"
    }

    result += "\n  Tabs:"
    result += "\n  ["
    zip(model.listModel.tabTitles, model.contentsModel.pages).forEach {
      let title = $0.0
      result += "\n    Title: \(title.text), path: \(title.path)"
      if let url = title.url {
        result += "\n    Title url: \(url)"
      }

      let page = $0.1
      result += "\n" + page.debugDescription.indented(level: 2)
    }
    result += "\n  ]"
    result += "\n}"

    return result
  }
}

extension LayeredBlock.Child {
  fileprivate var description: String {
    "[\(alignment)]\(content)"
  }
}

extension LayeredBlock: CustomDebugStringConvertible {
  public var debugDescription: String {
    var result = "Layers \(widthTrait) x \(heightTrait) {"
    children.forEach {
      result += "\n  Alignment: \($0.alignment)"
      result += "\n" + $0.content.debugDescription.indented()
    }
    result += "\n}"
    return result
  }
}

extension GridBlock.Span: CustomDebugStringConvertible {
  public var debugDescription: String {
    "(\(rows)x\(columns))"
  }
}

extension GridBlock.Item: CustomDebugStringConvertible {
  public var debugDescription: String {
    "\(span)\(contents)"
  }
}

extension GridBlock: CustomDebugStringConvertible {
  public var debugDescription: String {
    """
    Grid \(columnCount) Columns, \(widthTrait.debugDescription) x \(heightTrait.debugDescription) {
      Items map:
    \(formatTable(grid.itemsIndices).indented())
      Items:
    \(items.map { $0.debugDescription }.joined(separator: ",\n").indented())
    }
    """
  }
}

extension DecoratingBlock: CustomDebugStringConvertible {
  var debugDescription: String {
    var result = "Decorated {"
    var decorations: [String] = []

    if !backgroundColor.alpha.isApproximatelyEqualTo(0) {
      decorations.append("Background color: \(backgroundColor)")
    }

    if let actions = actions {
      var actionDescription = "Actions:\n"
      for action in actions {
        actionDescription += "   UIAction:\n"
        actionDescription += "      path: \(action.path)\n"
        actionDescription +=
          "      payload: \(action.payload.debugDescription.indented())\n"
      }
      decorations.append(actionDescription)
    }

    if let actionAnimation = actionAnimation {
      var description = "Action animation:\n"
      for animation in actionAnimation.touchDown {
        description += "   TouchDown animation:\n"
        description += "      name: \(animation.kind.rawValue)\n"
      }
      for animation in actionAnimation.touchUp {
        description += "   TouchUp animation:\n"
        description += "      name: \(animation.kind.rawValue)\n"
      }
      if actionAnimation.touchDown.isEmpty, actionAnimation.touchUp.isEmpty {
        description += "   empty\n"
      }
      decorations.append(description)
    }

    if let actions = longTapActions {
      decorations.append(actions.debugDescription)
    }

    if let url = analyticsURL {
      decorations.append("Analytics: \(url.absoluteString)")
    }

    if boundary != DecoratingBlock.defaultBoundary {
      decorations.append("Boundary: \(boundary)")
    }

    if let border = border {
      decorations.append("Border: \(border)")
    }

    if !childAlpha.isApproximatelyEqualTo(1) {
      decorations.append("Alpha: \(childAlpha)")
    }

    if !visibilityActions.isEmpty {
      var visibilityActionDescription = "Visibility actions:\n"
      for action in visibilityActions {
        let uiAction = action.uiAction
        visibilityActionDescription += "   UIAction:\n"
        visibilityActionDescription += "      path: \(uiAction.path)\n"
        visibilityActionDescription +=
          "      payload: \(uiAction.payload.debugDescription.indented())\n"
        visibilityActionDescription +=
          "   Required visibility duration: \(action.requiredVisibilityDuration)\n"
        visibilityActionDescription += "   Target percentage: \(action.targetPercentage)\n"
      }
      decorations.append(visibilityActionDescription)
    }

    if !tooltips.isEmpty {
      var tooltipsDescription = "Tooltips:\n"
      for tooltip in tooltips {
        tooltipsDescription += "   \(tooltip.debugDescription.indented())\n"
      }
      decorations.append(tooltipsDescription)
    }

    if paddings != .zero {
      var paddingsDescription = "Padded:"
      func append(_ what: String, _ value: CGFloat) {
        if !value.isApproximatelyEqualTo(0) {
          paddingsDescription += " \(what) \(value)"
        }
      }
      append("top", paddings.top)
      append("left", paddings.left)
      append("bottom", paddings.bottom)
      append("right", paddings.right)

      decorations.append(paddingsDescription)
    }

    result += decorations.map { "\n  " + $0 }.joined()
    result += "\n" + child.debugDescription.indented()
    result += "\n}"

    return result
  }
}

#if INTERNAL_BUILD
extension AccessibilityBlock: CustomDebugStringConvertible {
  var debugDescription: String {
    """
    Accessibility {
      accessibilityID: \(accessibilityID)
      child: \(child.debugDescription.indented())
    }
    """
  }
}
#endif

extension TransitioningBlock: CustomDebugStringConvertible {
  public var debugDescription: String {
    """
    Transitioning {
      from: \(dbgStr(from?.debugDescription.indented()))
      to: \(to.debugDescription.indented())
    }
    """
  }
}

extension BlockTooltip: CustomDebugStringConvertible {
  public var debugDescription: String {
    """
    BlockTooltip {
      id: \(id)
      duration: \(duration.value)
      offset: \(offset.x) x \(offset.y)
      position: \(position.rawValue)
      block: \(block.debugDescription.indented())
    }
    """
  }
}

extension MaskedBlock: CustomDebugStringConvertible {
  public var debugDescription: String {
    """
    MaskedBlock {
      maskBlock: \(maskBlock.debugDescription.indented())
      maskedBlock: \(maskedBlock.debugDescription.indented())
    }
    """
  }
}

extension EmptyBlock: CustomDebugStringConvertible {
  public var debugDescription: String {
    "Empty \(widthTrait) x \(heightTrait)"
  }
}

private func formatTable<T>(_ rawTable: [[T]]) -> String {
  let table = rawTable.map { $0.map(String.init(describing:)) }
  guard !table.isEmpty else { return "Empty table" }
  let columnCounts = table.map { $0.count }
  guard columnCounts.min() == columnCounts.max() else {
    return "Malformed table"
  }
  let columnCount = columnCounts.min()!
  guard columnCount != 0 else {
    return "Empty table"
  }
  let columnWidths = (0..<columnCount).map { column in
    table.map { $0[column].count }.max()!
  }
  return table.map {
    $0.enumerated().map { offset, element in
      String(repeating: " ", times: try! UInt(value: columnWidths[offset] - element.count)) +
        element
    }.joined(separator: " ")
  }.joined(separator: "\n")
}
