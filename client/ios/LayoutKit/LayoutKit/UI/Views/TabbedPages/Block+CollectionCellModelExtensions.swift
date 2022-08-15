import Foundation

private protocol ReusableBlock: Block {}

extension BackgroundBlock: CollectionCellModel {}
extension ContainerBlock: CollectionCellModel {}
extension GalleryBlock: CollectionCellModel {}
extension ImageBlock: CollectionCellModel {}
extension AnimatableImageBlock: CollectionCellModel {}
extension SeparatorBlock: CollectionCellModel {}
extension ShadedBlock: CollectionCellModel {}
extension SwitchBlock: CollectionCellModel {}
extension TextBlock: CollectionCellModel {}
extension LaidOutBlock: CollectionCellModel where T: BlockWithLayout {}
extension DecoratingBlock: CollectionCellModel {}
extension LayeredBlock: CollectionCellModel {}
extension EmptyBlock: CollectionCellModel {}
#if INTERNAL_BUILD
extension AccessibilityBlock: CollectionCellModel {}
#endif
