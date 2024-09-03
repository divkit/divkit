import LayoutKit
import Markdown
import UIKit
import VGSL

struct MarkdownStringBuilder: MarkupVisitor {
  private let typo: Typo
  private let path: UIElementPath

  init(typo: Typo, path: UIElementPath) {
    self.typo = typo
    self.path = path
  }

  mutating func attributedString(from document: Document) -> NSAttributedString {
    visit(document)
  }

  mutating func defaultVisit(_ markup: Markup) -> NSAttributedString {
    let result = NSMutableAttributedString()
    for child in markup.children {
      result.append(visit(child))
    }
    return result
  }

  mutating func visitText(_ text: Text) -> NSAttributedString {
    text.plainText.with(typo: typo)
  }

  mutating func visitEmphasis(_ emphasis: Emphasis) -> NSAttributedString {
    let result = NSMutableAttributedString()
    for child in emphasis.children {
      result.append(visit(child))
    }
    result.applyFontModifier { $0.apply(newTraits: .traitItalic) }

    return result
  }

  mutating func visitStrong(_ strong: Strong) -> NSAttributedString {
    let result = NSMutableAttributedString()

    for child in strong.children {
      result.append(visit(child))
    }

    return result.with(typo: typo.with(fontWeight: .bold))
  }

  mutating func visitParagraph(_ paragraph: Paragraph) -> NSAttributedString {
    let result = NSMutableAttributedString()

    for child in paragraph.children {
      result.append(visit(child))
    }

    if paragraph.hasSuccessor {
      if paragraph.isContainedInList {
        result.append(.singleNewline(typo.attributes))
      } else {
        result.append(.doubleNewline(typo.attributes))
      }
    }

    return result
  }

  mutating func visitHeading(_ heading: Heading) -> NSAttributedString {
    let result = NSMutableAttributedString()

    for child in heading.children {
      result.append(visit(child))
    }

    result.applyFontModifier {
      $0.apply(
        newTraits: .traitBold,
        newPointSize: 28.0 - CGFloat(heading.level * 2)
      )
    }

    if heading.hasSuccessor {
      result.append(.doubleNewline(typo.attributes))
    }

    return result
  }

  mutating func visitLink(_ link: Link) -> NSAttributedString {
    let result = NSMutableAttributedString()

    for child in link.children {
      result.append(visit(child))
    }

    guard let url = link.destination.flatMap(URL.init(string:)) else {
      return result
    }

    let action = ActionsAttribute(actions: [.init(url: url, path: path)])
    action.apply(to: result, at: CFRange(location: 0, length: result.length - 1))
    result.addAttribute(.foregroundColor, value: UIColor.systemBlue)

    return result
  }

  mutating func visitInlineCode(_ inlineCode: InlineCode) -> NSAttributedString {
    let monospacedFont = typo.attributes.safeFontAttribute.withMonospacedDigits()
    let typo = typo.with(font: monospacedFont)
    return inlineCode.code.with(typo: typo)
  }

  func visitCodeBlock(_ codeBlock: CodeBlock) -> NSAttributedString {
    let monospacedFont = typo.attributes.safeFontAttribute.withMonospacedDigits()
    let typo = typo.with(font: monospacedFont)
    let result = NSMutableAttributedString(attributedString: codeBlock.code.with(typo: typo))

    if codeBlock.hasSuccessor {
      result.append(.singleNewline(typo.attributes))
    }

    return result
  }

  mutating func visitStrikethrough(_ strikethrough: Strikethrough) -> NSAttributedString {
    let result = NSMutableAttributedString()

    for child in strikethrough.children {
      result.append(visit(child))
    }

    result.addAttribute(.strikethroughStyle, value: NSUnderlineStyle.single.rawValue)

    return result
  }

  mutating func visitUnorderedList(_ unorderedList: UnorderedList) -> NSAttributedString {
    let result = NSMutableAttributedString()

    let font = typo.attributes.safeFontAttribute

    for listItem in unorderedList.listItems {
      var listItemAttributes: [NSAttributedString.Key: Any] = [:]

      let listItemParagraphStyle = NSMutableParagraphStyle()

      let baseLeftMargin: CGFloat = 15.0
      let leftMarginOffset = baseLeftMargin +
        (20.0 * CGFloat(unorderedList.elementDepth(ListItemContainer.self)))
      let spacingFromIndex: CGFloat = 8.0
      let bulletWidth = ceil(
        NSAttributedString(string: "•", attributes: [.font: font]).size()
          .width
      )
      let firstTabLocation = leftMarginOffset + bulletWidth
      let secondTabLocation = firstTabLocation + spacingFromIndex

      listItemParagraphStyle.tabStops = [
        NSTextTab(textAlignment: .right, location: firstTabLocation),
        NSTextTab(textAlignment: .left, location: secondTabLocation),
      ]

      listItemParagraphStyle.headIndent = secondTabLocation

      listItemAttributes[.paragraphStyle] = listItemParagraphStyle
      listItemAttributes[.font] = UIFont.systemFont(ofSize: 4)

      let listItemAttributedString = visit(listItem).mutableCopy() as! NSMutableAttributedString
      listItemAttributedString.insert(
        NSAttributedString(string: "\t•\t", attributes: listItemAttributes),
        at: 0
      )

      result.append(listItemAttributedString)
    }

    if unorderedList.hasSuccessor {
      result.append(.doubleNewline(typo.attributes))
    }

    return result
  }

  mutating func visitListItem(_ listItem: ListItem) -> NSAttributedString {
    let result = NSMutableAttributedString()

    for child in listItem.children {
      result.append(visit(child))
    }

    if listItem.hasSuccessor {
      result.append(.singleNewline(typo.attributes))
    }

    return result
  }

  mutating func visitOrderedList(_ orderedList: OrderedList) -> NSAttributedString {
    let result = NSMutableAttributedString()

    for (index, listItem) in orderedList.listItems.enumerated() {
      var listItemAttributes: [NSAttributedString.Key: Any] = [:]

      let font = typo.attributes.safeFontAttribute
      let numeralFont = font.withMonospacedDigits()

      let listItemParagraphStyle = NSMutableParagraphStyle()

      let baseLeftMargin: CGFloat = 15.0
      let leftMarginOffset = baseLeftMargin +
        (20.0 * CGFloat(orderedList.elementDepth(ListItemContainer.self)))

      let highestNumberInList = orderedList.childCount
      let numeralColumnWidth =
        ceil(
          NSAttributedString(string: "\(highestNumberInList).", attributes: [.font: numeralFont])
            .size().width
        )

      let spacingFromIndex: CGFloat = 8.0
      let firstTabLocation = leftMarginOffset + numeralColumnWidth
      let secondTabLocation = firstTabLocation + spacingFromIndex

      listItemParagraphStyle.tabStops = [
        NSTextTab(textAlignment: .right, location: firstTabLocation),
        NSTextTab(textAlignment: .left, location: secondTabLocation),
      ]

      listItemParagraphStyle.headIndent = secondTabLocation

      listItemAttributes[.paragraphStyle] = listItemParagraphStyle
      listItemAttributes[.font] = font

      let listItemAttributedString = visit(listItem).mutableCopy() as! NSMutableAttributedString

      var numberAttributes = listItemAttributes
      numberAttributes[.font] = numeralFont

      let numberAttributedString = NSAttributedString(
        string: "\t\(index + 1).\t",
        attributes: numberAttributes
      )
      listItemAttributedString.insert(numberAttributedString, at: 0)

      result.append(listItemAttributedString)
    }

    if orderedList.hasSuccessor {
      if orderedList.isContainedInList {
        result.append(.singleNewline(typo.attributes))
      } else {
        result.append(.doubleNewline(typo.attributes))
      }
    }

    return result
  }

  mutating func visitBlockQuote(_ blockQuote: BlockQuote) -> NSAttributedString {
    let result = NSMutableAttributedString()

    for child in blockQuote.children {
      var quoteAttributes: [NSAttributedString.Key: Any] = [:]

      let quoteParagraphStyle = NSMutableParagraphStyle()

      let baseLeftMargin: CGFloat = 15.0
      let leftMarginOffset = baseLeftMargin +
        (20.0 * CGFloat(blockQuote.elementDepth(BlockQuote.self)))

      quoteParagraphStyle.tabStops = [NSTextTab(textAlignment: .left, location: leftMarginOffset)]

      quoteParagraphStyle.headIndent = leftMarginOffset

      quoteAttributes[.paragraphStyle] = quoteParagraphStyle
      quoteAttributes[.font] = typo.attributes.safeFontAttribute

      let quoteAttributedString = visit(child).mutableCopy() as! NSMutableAttributedString
      quoteAttributedString.insert(
        NSAttributedString(string: "\t", attributes: quoteAttributes),
        at: 0
      )

      quoteAttributedString.addAttribute(.foregroundColor, value: UIColor.systemGray)

      result.append(quoteAttributedString)
    }

    if blockQuote.hasSuccessor {
      result.append(.doubleNewline(typo.attributes))
    }

    return result
  }
}

extension NSMutableAttributedString {
  func applyFontModifier(_ modifier: (UIFont) -> UIFont) {
    enumerateAttribute(
      .font,
      in: NSRange(location: 0, length: length),
      options: []
    ) { value, range, _ in
      guard let font = value as? UIFont else { return }

      let newFont = modifier(font)
      addAttribute(.font, value: newFont, range: range)
    }
  }
}

extension UIFont {
  func apply(newTraits: UIFontDescriptor.SymbolicTraits, newPointSize: CGFloat? = nil) -> UIFont {
    var existingTraits = fontDescriptor.symbolicTraits
    existingTraits.insert(newTraits)

    guard let newFontDescriptor = fontDescriptor.withSymbolicTraits(existingTraits)
    else { return self }
    return UIFont(descriptor: newFontDescriptor, size: newPointSize ?? pointSize)
  }
}

extension Markup {
  func elementDepth<T>(_: T.Type) -> Int {
    var depth = 0
    var currentElement = parent
    while currentElement != nil {
      if currentElement is T {
        depth += 1
      }
      depth += 1
      currentElement = currentElement?.parent
    }
    return depth
  }
}

extension NSMutableAttributedString {
  func addAttribute(_ name: NSAttributedString.Key, value: Any) {
    addAttribute(name, value: value, range: NSRange(location: 0, length: length))
  }

  func addAttributes(_ attrs: [NSAttributedString.Key: Any]) {
    addAttributes(attrs, range: NSRange(location: 0, length: length))
  }
}

extension Markup {
  var hasSuccessor: Bool {
    parent?.childCount != indexInParent + 1
  }

  var isContainedInList: Bool {
    var currentElement = parent

    while currentElement != nil {
      if currentElement is ListItemContainer {
        return true
      }

      currentElement = currentElement?.parent
    }

    return false
  }
}

extension NSAttributedString {
  fileprivate static func singleNewline(_ attributes: [NSAttributedString.Key: Any])
    -> NSAttributedString {
    NSAttributedString(string: "\n", attributes: attributes)
  }

  fileprivate static func doubleNewline(_ attributes: [NSAttributedString.Key: Any])
    -> NSAttributedString {
    NSAttributedString(string: "\n\n", attributes: attributes)
  }
}

extension [NSAttributedString.Key: Any] {
  fileprivate var safeFontAttribute: UIFont {
    self[.font] as? UIFont ?? UIFont.systemFont(ofSize: UIFont.systemFontSize)
  }
}
