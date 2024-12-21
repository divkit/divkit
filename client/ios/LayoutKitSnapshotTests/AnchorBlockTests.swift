import CoreGraphics
import LayoutKit
import VGSL

final class AnchorBlockTests: LayoutKitSnapshotTest {
  func test_OnlyCenter_Fits() {
    perform(.crossResizables(center: 100))
  }

  func test_WithLeading_Fits() {
    perform(.crossResizables(leading: 80, center: 100))
  }

  func test_WithTrailing_Fits() {
    perform(.crossResizables(center: 100, trailing: 80))
  }

  func test_AllContents_Fits() {
    perform(.crossResizables(leading: 80, center: 100, trailing: 80))
  }

  func test_OnlyCenter_DoesNotFit() {
    perform(.crossResizables(center: axialSize + 100))
  }

  func test_WithLeading_DoesNotFit() {
    perform(.crossResizables(leading: 120, center: 100))
  }

  func test_WithTrailing_DoesNotFit() {
    perform(.crossResizables(center: 100, trailing: 120))
  }

  func test_LeadingLarger_DoesNotFit() {
    perform(.crossResizables(leading: 120, center: 100, trailing: 40))
  }

  func test_TrailingLarger_DoesNotFit() {
    perform(.crossResizables(leading: 40, center: 100, trailing: 120))
  }

  func test_AllContents_DoesNotFit() {
    perform(.crossResizables(leading: 120, center: 100, trailing: 120))
  }

  func test_AlignmentLeading() {
    perform(.crossAlignment(.leading))
  }

  func test_AlignmentCenter() {
    perform(.crossAlignment(.center))
  }

  func test_AlignmentTrailing() {
    perform(.crossAlignment(.trailing))
  }

  func test_IntrinsicCross() {
    perform(Configuration(
      crossTrait: .intrinsic,
      leading: Configuration.Item(crossSize: .fixed(40), axialSize: 40),
      center: Configuration.Item(crossSize: .fixed(50), axialSize: 50),
      trailing: Configuration.Item(crossSize: .fixed(60), axialSize: 60)
    ))
  }

  private func perform(_ configuration: Configuration, name: String = #function) {
    for direction in ContainerBlock.LayoutDirection.allCases {
      let size: CGSize = switch direction {
      case .horizontal:
        .init(width: axialSize, height: crossSize)
      case .vertical:
        .init(width: crossSize, height: axialSize)
      }

      func makeCenterOverlay(content: Block, color: Color) -> Block {
        LayeredBlock(
          widthTrait: .intrinsic,
          heightTrait: .intrinsic,
          horizontalChildrenAlignment: .center,
          verticalChildrenAlignment: .center,
          children: [
            content,
            TextBlock(
              widthTrait: .intrinsic,
              text: "╳".with(typo: Typo(size: 20, weight: .light).with(color: color))
            ),
          ]
        )
      }

      let anchor = AnchorBlock(
        direction: direction,
        crossTrait: configuration.crossTrait,
        crossAlignment: configuration.crossAlignment,
        leading: configuration.leading?.makePayload(
          text: "◄",
          direction: direction
        ).addingDecorations(border: .init(color: .blue)),
        center: makeCenterOverlay(
          content: configuration.center.makePayload(
            text: "■",
            direction: direction
          ),
          color: .green
        ).addingDecorations(border: .init(color: .green)),
        trailing: configuration.trailing?.makePayload(
          text: "►",
          direction: direction
        ).addingDecorations(border: .init(color: .blue))
      ).addingDecorations(border: .init(color: .red))

      perform(
        on: makeCenterOverlay(content: anchor, color: .red)
          .addingDecorations(border: .init(color: .gray)),
        size: size,
        name: "\(name)_\(direction)",
        mode: .verify
      )
    }
  }
}

private struct Configuration {
  struct Item {
    enum Size {
      case fixed(CGFloat)
      case weighted(LayoutTrait.Weight)
    }

    let crossSize: Size
    let axialSize: CGFloat
  }

  var crossTrait = LayoutTrait.resizable
  var crossAlignment = Alignment.center
  var leading: Item?
  var center: Item
  var trailing: Item?

  static func crossResizables(
    leading: CGFloat? = nil,
    center: CGFloat,
    trailing: CGFloat? = nil
  ) -> Self {
    Configuration(
      leading: leading.map {
        Item(crossSize: .weighted(.default), axialSize: $0)
      },
      center: Item(crossSize: .weighted(.default), axialSize: center),
      trailing: trailing.map {
        Item(crossSize: .weighted(.default), axialSize: $0)
      }
    )
  }

  static func crossAlignment(_ alignment: Alignment) -> Self {
    Self(
      crossAlignment: alignment,
      leading: Item(crossSize: .fixed(40), axialSize: 40),
      center: Item(crossSize: .fixed(50), axialSize: 50),
      trailing: Item(crossSize: .fixed(60), axialSize: 60)
    )
  }
}

extension Configuration.Item {
  fileprivate func makePayload(
    text: String,
    direction: ContainerBlock.LayoutDirection
  ) -> Block {
    let widthTrait: LayoutTrait
    let heightTrait: LayoutTrait

    let crossTrait: LayoutTrait = switch crossSize {
    case let .fixed(size):
      .fixed(size)
    case let .weighted(weight):
      .weighted(weight)
    }

    switch direction {
    case .horizontal:
      widthTrait = .fixed(axialSize)
      heightTrait = crossTrait
    case .vertical:
      heightTrait = .fixed(axialSize)
      widthTrait = crossTrait
    }

    return try! ContainerBlock(
      layoutDirection: .horizontal,
      widthTrait: widthTrait,
      heightTrait: heightTrait,
      horizontalChildrenAlignment: .center,
      verticalChildrenAlignment: .center,
      children: [
        TextBlock(
          widthTrait: .intrinsic,
          text: text.with(typo: Typo(size: 20, weight: .medium).with(color: .white))
        ),
      ]
    ).addingDecorations(backgroundColor: Color(white: 0.7, alpha: 1))
  }
}

private let axialSize: CGFloat = 300
private let crossSize: CGFloat = 100
