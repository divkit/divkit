#if INTERNAL_BUILD
import CoreGraphics
import Foundation

import BaseUI
import CommonCore
import LayoutKit

extension Block {
  func addingErrorsButton(
    errors: [ExpressionError],
    parentPath: UIElementPath,
    showDebugInfo: ((ViewType) -> Void)?
  ) -> Block {
    let errorsCount = errors.count
    guard errorsCount > 0 else {
      return self
    }

    let counterText = errorsCount > maxCount
      ? "\(maxCount)+"
      : "\(errorsCount)"

    let typo = Typo(size: FontSize(rawValue: 14), weight: .regular)
      .with(color: .white)

    let action = UserInterfaceAction(
      payload: .url(showOverlayURL),
      path: parentPath + "div_errors_indicator",
      accessibilityElement: nil
    )

    let counter = TextBlock(
      widthTrait: .intrinsic,
      text: counterText.with(typo: typo)
    )

    let indicator = counter
      .addingVerticalGaps(errorsButtonCounterGaps)
      .addingHorizontalGaps(calculateCounterHorizontalGaps(counter: counter))
      .addingDecorations(
        boundary: .clipCorner(radius: 10),
        backgroundColor: .red
      )
      .addingEdgeGaps(2)
      .addingDecorations(action: action)

    let buttonBlock: Block
    if let showDebugInfo = showDebugInfo {
      buttonBlock = DebugInfoBlock(
        child: indicator,
        showDebugInfo: {
          #if os(iOS)
          showDebugInfo(ErrorListView(errorList: errors.map { $0.description }))
          #else
          return
          #endif
        }
      )
    } else {
      buttonBlock = indicator
    }

    let block = LayeredBlock(
      widthTrait: calculatedWidthTrait,
      heightTrait: calculatedHeightTrait,
      children: [self, buttonBlock]
    )

    return block
  }
}

private func calculateCounterHorizontalGaps(counter: Block) -> CGFloat {
  let additionalGap = (counter.intrinsicSize.height - counter.intrinsicSize.width) / 2
  return max(
    errorsButtonCounterGaps,
    errorsButtonCounterGaps + additionalGap
  )
}

private let errorsButtonCounterGaps: CGFloat = 4
private let maxCount = 9999

private let showOverlayURL = URL(string: "debugInfo://show")!
#endif
