import CoreGraphics
import LayoutKit
import VGSL

final class DecoratingBlockTests: LayoutKitSnapshotTest {
  func test_Wrapped_NoClip_Boundary() {
    perform(
      on: block,
      size: CGSize(width: 140, height: 70),
      mode: .verify
    )
  }
}

private let block = try! ContainerBlock(
  layoutDirection: .horizontal,
  widthTrait: .intrinsic,
  children: [
    TextBlock(
      widthTrait: .intrinsic,
      text: "Child 1".with(typo: Typo(
        font: Font.with(weight: .regular, size: 16)
      ))
    ),
    TextBlock(
      widthTrait: .intrinsic,
      text: "Child 2".with(typo: Typo(
        font: Font.with(weight: .regular, size: 16)
      ))
    ),
  ]
)
.addingEdgeGaps(8)
.addingDecorations(
  boundary: .cornerRadius(34),
  border: BlockBorder(color: .black),
  backgroundColor: .gray,
)
.addingDecorations(
  boundary: .noClip,
  backgroundColor: .red
)
