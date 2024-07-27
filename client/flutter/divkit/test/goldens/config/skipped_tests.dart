class _SkipTest {
  final String path;
  final _Reason reason;
  final String? stackTraceUrl;

  const _SkipTest(
    this.path,
    this.reason, [
    this.stackTraceUrl,
  ]);
}

enum _Reason {
  divNotSupported,
  haveToLoadNetworkImage,
  crashesOnImageDimensions,
  crashesOnAspectRatio,
  crashesOnRenderFlexOverflow,
  crashesOnRenderFlexLayout,
  // Didn't read through
  unknown,
}

final skippedTestsMap = {
  for (final test in skippedTests) test.path: test,
};

final skippedTests = [
  // # div-background
  ...const [
    'example/assets/test_data/snapshot_test_data/div-background/nine-patch-rhombs-horizontal-insets.json',
    'example/assets/test_data/snapshot_test_data/div-background/nine-patch-rhombs-large-all-insets.json',
    'example/assets/test_data/snapshot_test_data/div-background/nine-patch-rhombs-large-bottom-inset.json',
    'example/assets/test_data/snapshot_test_data/div-background/nine-patch-rhombs-large-bottom-left-inset.json',
    'example/assets/test_data/snapshot_test_data/div-background/nine-patch-rhombs-large-bottom-left-right-inset.json',
    'example/assets/test_data/snapshot_test_data/div-background/nine-patch-rhombs-vertical-insets.json',
    'example/assets/test_data/snapshot_test_data/div-background/nine-patch-rhombs.json',
    'example/assets/test_data/snapshot_test_data/div-background/nine-patch-shape.json',
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.divNotSupported,
      null,
    ),
  ),

  ...const [
    'example/assets/test_data/snapshot_test_data/div-container/horizontal-orientation-with-between-separators-using-paddings.json',
    'example/assets/test_data/snapshot_test_data/div-container/horizontal-orientation-with-between-separators.json',
    'example/assets/test_data/snapshot_test_data/div-container/horizontal-orientation-with-all-separators-using-paddings.json',
    'example/assets/test_data/snapshot_test_data/div-container/horizontal-orientation-with-all-separators.json',
    'example/assets/test_data/snapshot_test_data/div-container/vertical-orientation-with-between-separators-using-paddings.json',
    'example/assets/test_data/snapshot_test_data/div-container/vertical-orientation-with-between-separators.json',
    'example/assets/test_data/snapshot_test_data/div-container/vertical-orientation-with-all-separators-using-paddings.json',
    'example/assets/test_data/snapshot_test_data/div-container/vertical-orientation-with-all-separators.json',
    'example/assets/test_data/snapshot_test_data/div-container/wrap/horizontal-orientation-with-separators.json',
    'example/assets/test_data/snapshot_test_data/div-container/wrap/horizontal-orientation-with-separators-space-around.json',
    'example/assets/test_data/snapshot_test_data/div-container/wrap/horizontal-orientation-with-separators-space-evenly.json',
    'example/assets/test_data/snapshot_test_data/div-container/wrap/horizontal-orientation-with-separators-using-paddings.json',
    'example/assets/test_data/snapshot_test_data/div-container/wrap/horizontal-orientation-with-separators-using-paddings-rtl.json',
    'example/assets/test_data/snapshot_test_data/div-container/wrap/vertical-orientation-with-separators.json',
    'example/assets/test_data/snapshot_test_data/div-container/wrap/vertical-orientation-with-separators-space-around.json',
    'example/assets/test_data/snapshot_test_data/div-container/wrap/vertical-orientation-with-separators-space-evenly.json',
    'example/assets/test_data/snapshot_test_data/div-container/wrap/vertical-orientation-with-separators-using-paddings.json',
    'example/assets/test_data/snapshot_test_data/div-container/wrap/vertical-orientation-with-separators-using-paddings-rtl.json',
    'example/assets/test_data/snapshot_test_data/div-container/block-paddings.json',
    'example/assets/test_data/snapshot_test_data/div-container/match-parent-with-big-content.json',
    'example/assets/test_data/snapshot_test_data/div-container/fixed-size-with-paddings.json',
    'example/assets/test_data/snapshot_test_data/div-container/corners_radius.json'
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.divNotSupported,
      'Separators are not supported yet',
    ),
  ),

  ...const [
    'example/assets/test_data/snapshot_test_data/div-container/baseline.json',
    'example/assets/test_data/snapshot_test_data/div-container/baseline-with-vertical-containers.json',
    'example/assets/test_data/snapshot_test_data/div-container/wrap/baseline.json',
    'example/assets/test_data/snapshot_test_data/div-container/baseline-with-images.json',
    'example/assets/test_data/snapshot_test_data/div-input/text-alignment-property.json',
    'example/assets/test_data/snapshot_test_data/div-input/text-alignment-property-single-line.json',
    'example/assets/test_data/snapshot_test_data/div-input/all-attributes.json'
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.divNotSupported,
      'Baseline alignment not supported yet',
    ),
  ),

  ...const [
    /// TODO: pass border (stroke) to inside of container
    'example/assets/test_data/snapshot_test_data/div-container/border-box-model.json',
    'example/assets/test_data/snapshot_test_data/div-text/border-with-stoke.json',
    'example/assets/test_data/snapshot_test_data/div-container/wrap/horizontal-orientation-child-alignments.json',
    'example/assets/test_data/snapshot_test_data/div-container/wrap/vertical-orientation-child-alignments.json',
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.divNotSupported,
      'Known cases',
    ),
  ),

// # div-container/aspect

  const _SkipTest(
    'example/assets/test_data/snapshot_test_data/div-container/aspect/wrap_content-width-horizontal.json',
    _Reason.crashesOnAspectRatio,
    'https://paste.yandex-team.ru/bd749f41-872d-42f2-a740-8a44e989e41f',
  ),
  const _SkipTest(
    'example/assets/test_data/snapshot_test_data/div-container/aspect/wrap_content-width-overlap.json',
    _Reason.crashesOnAspectRatio,
    'https://paste.yandex-team.ru/585ce2b6-50b9-4a67-9851-984e2a211284',
  ),
  const _SkipTest(
    'example/assets/test_data/snapshot_test_data/div-container/aspect/wrap_content-width-overlap-match_parent-height.json',
    _Reason.crashesOnRenderFlexOverflow,
    'https://paste.yandex-team.ru/690a312d-b5aa-4cc3-b182-3d2efcca81a0',
  ),
  const _SkipTest(
    'example/assets/test_data/snapshot_test_data/div-container/aspect/wrap_content-width-vertical-constrained-child-not-fit.json',
    _Reason.crashesOnAspectRatio,
    'https://paste.yandex-team.ru/221414c0-8858-4e3b-818f-791f55ce0f32',
  ),
  const _SkipTest(
    'example/assets/test_data/snapshot_test_data/div-container/aspect/wrap_content-width-vertical-match_parent-child-not-fit.json',
    _Reason.crashesOnAspectRatio,
    'https://paste.yandex-team.ru/dc501869-a777-48cc-8f81-683d5bd13dfe',
  ),
  const _SkipTest(
    'example/assets/test_data/snapshot_test_data/div-container/aspect/wrap_content-width-vertical-match_parent-height.json',
    _Reason.crashesOnRenderFlexOverflow,
    'https://paste.yandex-team.ru/a796ab09-dab0-4835-8db3-a87a561ab0bf',
  ),
  const _SkipTest(
    'example/assets/test_data/snapshot_test_data/div-container/aspect/wrap_content-width-overlap-fixed-height.json',
    _Reason.crashesOnImageDimensions,
  ),
  const _SkipTest(
    'example/assets/test_data/snapshot_test_data/div-container/aspect/wrap-layout_mode-horizontal-fixed-height.json',
    _Reason.crashesOnImageDimensions,
  ),
  const _SkipTest(
    'example/assets/test_data/snapshot_test_data/div-text/ellipsis.json',
    _Reason.divNotSupported,
    'Custom ellipsize is not supported yet, requires custom Text widget',
  ),
  const _SkipTest(
    'example/assets/test_data/snapshot_test_data/div-container/aspect/wrap_content-width-vertical-fixed-height.json',
    _Reason.crashesOnImageDimensions,
  ),

// # div-container/wrap

  const _SkipTest(
    'example/assets/test_data/snapshot_test_data/div-container/wrap/horizontal-orientation-match-parent-cross.json',
    _Reason.crashesOnRenderFlexLayout,
    'https://paste.yandex-team.ru/04a5c12e-825c-4e24-812d-5ae62624ee98',
  ),
  const _SkipTest(
    'example/assets/test_data/snapshot_test_data/div-container/wrap/horizontal-orientation-wrap-content-constrained.json',
    _Reason.crashesOnRenderFlexOverflow,
    'https://paste.yandex-team.ru/4ba25713-9819-4603-b882-66c6229b2c68',
  ),
  const _SkipTest(
    'example/assets/test_data/snapshot_test_data/div-container/wrap/vertical-orientation-match-parent-cross.json',
    _Reason.crashesOnRenderFlexLayout,
    'https://paste.yandex-team.ru/d5043446-c4e5-4023-bd5c-edad95365fb4',
  ),
  const _SkipTest(
    'example/assets/test_data/snapshot_test_data/div-container/wrap/vertical-orientation-wrap-content-constrained.json',
    _Reason.crashesOnRenderFlexOverflow,
    'https://paste.yandex-team.ru/d9c38ec6-a59c-48ae-a825-6c12f8e08314',
  ),
  const _SkipTest(
    'example/assets/test_data/snapshot_test_data/div-container/wrap/horizontal-orientation-paddings.json',
    _Reason.crashesOnRenderFlexOverflow,
    'https://paste.yandex-team.ru/e46599f4-0fdd-434c-b8cc-8f4ccd455284',
  ),
  const _SkipTest(
    'example/assets/test_data/snapshot_test_data/div-container/wrap/horizontal-orientation-with-wrap-content-constrained-children.json',
    _Reason.crashesOnRenderFlexOverflow,
    'https://paste.yandex-team.ru/1ded4255-bb11-4ca8-b43b-b31d4daaf098',
  ),
  const _SkipTest(
    'example/assets/test_data/snapshot_test_data/div-container/wrap/horizontal-orientation-wrap-content.json',
    _Reason.crashesOnRenderFlexOverflow,
    'https://paste.yandex-team.ru/7963e7be-08d4-4525-9c71-e9fa8749134c',
  ),
  const _SkipTest(
    'example/assets/test_data/snapshot_test_data/div-container/wrap/horizontal-orientation-wrap-content.json',
    _Reason.crashesOnRenderFlexOverflow,
    'https://paste.yandex-team.ru/7963e7be-08d4-4525-9c71-e9fa8749134c',
  ),
  const _SkipTest(
    'example/assets/test_data/snapshot_test_data/div-container/wrap/vertical-orientation-paddings.json',
    _Reason.crashesOnRenderFlexOverflow,
    'https://paste.yandex-team.ru/e454bb3b-fec6-4b78-8a92-498f1ff9747c',
  ),
  const _SkipTest(
    'example/assets/test_data/snapshot_test_data/div-container/wrap/vertical-orientation-with-wrap-content-constrained-children.json',
    _Reason.crashesOnRenderFlexOverflow,
    'https://paste.yandex-team.ru/4f2ca615-af39-4763-a508-d8398e99bc5f',
  ),
  const _SkipTest(
    'example/assets/test_data/snapshot_test_data/div-container/wrap/vertical-orientation-wrap-content.json',
    _Reason.crashesOnRenderFlexOverflow,
    'https://paste.yandex-team.ru/62ddb122-ef5f-4ed3-ab21-998b44e03d7d',
  ),

// # div-container/item_builder

  ...[
    'example/assets/test_data/snapshot_test_data/div-container/item_builder/item-builder.json',
    'example/assets/test_data/snapshot_test_data/div-container/item_builder/non-unique-matched-selectors.json',
    'example/assets/test_data/snapshot_test_data/div-container/item_builder/nested-builders.json',
    'example/assets/test_data/snapshot_test_data/div-container/item_builder/index.json'
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.divNotSupported,
      'item builder is not supported yet',
    ),
  ),

// # div-container

  const _SkipTest(
    'example/assets/test_data/snapshot_test_data/div-container/clip-to-bounds-with-shadows.json',
    _Reason.divNotSupported,
  ),

  ...[
    'example/assets/test_data/snapshot_test_data/div-container/overlap-orientation-custom-alignments-bc.json',
    'example/assets/test_data/snapshot_test_data/div-container/overlap-orientation-custom-alignments-bl.json',
    'example/assets/test_data/snapshot_test_data/div-container/overlap-orientation-custom-alignments-br.json',
    'example/assets/test_data/snapshot_test_data/div-container/overlap-orientation-custom-alignments-cc.json',
    'example/assets/test_data/snapshot_test_data/div-container/overlap-orientation-custom-alignments-cl.json',
    'example/assets/test_data/snapshot_test_data/div-container/overlap-orientation-custom-alignments-cr.json',
    'example/assets/test_data/snapshot_test_data/div-container/overlap-orientation-custom-alignments-tc.json',
    'example/assets/test_data/snapshot_test_data/div-container/overlap-orientation-custom-alignments-tl.json',
    'example/assets/test_data/snapshot_test_data/div-container/overlap-orientation-custom-alignments-tr.json',
    'example/assets/test_data/snapshot_test_data/div-container/horizontal-orientation-alignments.json',
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.unknown,
      'Test cases use unsupported text symbols',
    ),
  ),

  ...[
    'example/assets/test_data/snapshot_test_data/div-container/aspect/wrap_content-width-vertical.json',
    'example/assets/test_data/snapshot_test_data/div-container/aspect/wrap_content-width-vertical-match_parent-child-fits.json',
    'example/assets/test_data/snapshot_test_data/div-container/known-match-parent-cross-axis.json',
    'example/assets/test_data/snapshot_test_data/div-container/unknown-match-parent-cross-axis.json',
    'example/assets/test_data/snapshot_test_data/div-text/auto-ellipsize-by-container-size.json',
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.unknown,
      'Current behavior not matches target',
    ),
  ),

  ...[
    'example/assets/test_data/snapshot_test_data/div-container/horizontal-orientation-constrained-not-fit.json',
    'example/assets/test_data/snapshot_test_data/div-container/horizontal-orientation-match-parent-not-fit-with-margin.json',
    'example/assets/test_data/snapshot_test_data/div-container/horizontal-orientation-match-parent-not-fit-with-padding.json',
    'example/assets/test_data/snapshot_test_data/div-container/horizontal-orientation-match-parent-not-fit.json',
    'example/assets/test_data/snapshot_test_data/div-container/horizontal-orientation-paddings.json',
    'example/assets/test_data/snapshot_test_data/div-container/horizontal-orientation-with-wrap-content-constrained-children.json',
    'example/assets/test_data/snapshot_test_data/div-container/overlap-orientation-paddings.json',
    'example/assets/test_data/snapshot_test_data/div-container/overlap-orientation-with-wrap-content-constrained-children.json',
    'example/assets/test_data/snapshot_test_data/div-container/vertical-orientation-paddings.json',
    'example/assets/test_data/snapshot_test_data/div-container/vertical-orientation-with-wrap-content-constrained-children.json',
    'example/assets/test_data/snapshot_test_data/div-container/container_with_wrap_content_constrained_child.json',
    'example/assets/test_data/snapshot_test_data/div-container/vertical-orientation-contsrained-not-fit.json',
    'example/assets/test_data/snapshot_test_data/div-container/vertical-orientation-match-parent-not-fit-with-margin.json',
    'example/assets/test_data/snapshot_test_data/div-container/vertical-orientation-match-parent-not-fit-with-padding.json',
    'example/assets/test_data/snapshot_test_data/div-container/vertical-orientation-match-parent-not-fit.json',
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.crashesOnRenderFlexOverflow,
      'Overflow should be normal behavior for these cases',
    ),
  ),
  ...[
    'example/assets/test_data/snapshot_test_data/div-container/horizontal-match-parent-inside-wrap-content-on-both-axis.json',
    'example/assets/test_data/snapshot_test_data/div-container/match-parent-inside-wrap-content.json',
    'example/assets/test_data/snapshot_test_data/div-container/overlap-match-parent-inside-wrap-content-on-both-axis.json',
    'example/assets/test_data/snapshot_test_data/div-container/vertical-match-parent-inside-wrap-content-on-both-axis.json',
    'example/assets/test_data/snapshot_test_data/div-container/wrap/vertical-orientation-match-parent-inside-wrap-content.json',
    'example/assets/test_data/snapshot_test_data/div-container/horizontal-match-parent-inside-wrap-content-on-cross-axis.json',
    'example/assets/test_data/snapshot_test_data/div-container/vertical-match-parent-inside-wrap-content-on-cross-axis.json',
    'example/assets/test_data/snapshot_test_data/div-container/wrap/horizontal-orientation-match-parent-inside-wrap-content.json',
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.unknown,
      'match_parent inside wrap_content should transform into the wrap_content with constrained',
    ),
  ),

  ...[
    'example/assets/test_data/snapshot_test_data/div-container/horizontal-orientation-bottom-horizontal-alignment.json',
    'example/assets/test_data/snapshot_test_data/div-container/horizontal-orientation-bottom-vertical-alignment.json',
    'example/assets/test_data/snapshot_test_data/div-container/horizontal-orientation-center-horizontal-alignment.json',
    'example/assets/test_data/snapshot_test_data/div-container/horizontal-orientation-center-vertical-alignment.json',
    'example/assets/test_data/snapshot_test_data/div-container/horizontal-orientation-space-around-alignment.json',
    'example/assets/test_data/snapshot_test_data/div-container/horizontal-orientation-space-between-alignment.json',
    'example/assets/test_data/snapshot_test_data/div-container/horizontal-orientation-space-evenly-alignment.json',
    'example/assets/test_data/snapshot_test_data/div-container/overlap-orientation.json',
    'example/assets/test_data/snapshot_test_data/div-container/overlap-with-match-parent-child.json',
    'example/assets/test_data/snapshot_test_data/div-container/size_unit.json',
    'example/assets/test_data/snapshot_test_data/div-container/vertical-orientation-center-alignment.json',
    'example/assets/test_data/snapshot_test_data/div-container/vertical-orientation-space-around-alignment.json',
    'example/assets/test_data/snapshot_test_data/div-container/vertical-orientation-space-between-alignment.json',
    'example/assets/test_data/snapshot_test_data/div-container/vertical-orientation-space-evenly-alignment.json',
    'example/assets/test_data/snapshot_test_data/div-background/scale_stretch.json',
    'example/assets/test_data/snapshot_test_data/div-container/no-scale-background.json',
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.haveToLoadNetworkImage,
      'Framework can not load load network images during testing',
    ),
  ),

  ...[
    'example/assets/test_data/snapshot_test_data/div-container/overlap-container-alignment.json',
    'example/assets/test_data/snapshot_test_data/div-container/overlap-orientation-default-alignments.json',
    'example/assets/test_data/snapshot_test_data/div-container/vertical-orientation-alignments.json',
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.crashesOnRenderFlexOverflow,
      'Device size is less than image',
    ),
  ),

// # div-gallery

  ...[
    'example/assets/test_data/snapshot_test_data/div-gallery/corners_radius.json',
    'example/assets/test_data/snapshot_test_data/div-gallery/horizontal-gallery-content-alignment.json',
    'example/assets/test_data/snapshot_test_data/div-gallery/horizontal-gallery-cross-spacing.json',
    'example/assets/test_data/snapshot_test_data/div-gallery/horizontal-gallery-custom-item-spacing.json',
    'example/assets/test_data/snapshot_test_data/div-gallery/horizontal-gallery-custom-margins.json',
    'example/assets/test_data/snapshot_test_data/div-gallery/horizontal-gallery-custom-paddings.json',
    'example/assets/test_data/snapshot_test_data/div-gallery/horizontal-gallery-fixed-width.json',
    'example/assets/test_data/snapshot_test_data/div-gallery/horizontal-gallery-horizontal-shrinking.json',
    'example/assets/test_data/snapshot_test_data/div-gallery/horizontal-gallery-resizable-width.json',
    'example/assets/test_data/snapshot_test_data/div-gallery/horizontal-grid-gallery-item-spacing.json',
    'example/assets/test_data/snapshot_test_data/div-gallery/horizontal-grid-gallery-padding.json',
    'example/assets/test_data/snapshot_test_data/div-gallery/vertical-gallery-content-alignment.json',
    'example/assets/test_data/snapshot_test_data/div-gallery/vertical-gallery-cross-spacing.json',
    'example/assets/test_data/snapshot_test_data/div-gallery/vertical-gallery-custom-item-spacing.json',
    'example/assets/test_data/snapshot_test_data/div-gallery/vertical-gallery-custom-margins.json',
    'example/assets/test_data/snapshot_test_data/div-gallery/vertical-gallery-custom-paddings.json',
    'example/assets/test_data/snapshot_test_data/div-gallery/vertical-gallery-fixed-width.json',
    'example/assets/test_data/snapshot_test_data/div-gallery/vertical-gallery-horizontal-shrinking.json',
    'example/assets/test_data/snapshot_test_data/div-gallery/vertical-gallery-resizable-width.json',
    'example/assets/test_data/snapshot_test_data/div-gallery/vertical-grid-gallery-item-spacing.json',
    'example/assets/test_data/snapshot_test_data/div-gallery/vertical-grid-gallery-padding.json',
    'example/assets/test_data/snapshot_test_data/div-gallery/empty.json',
    'example/assets/test_data/snapshot_test_data/div-gallery/gallery-default-item-position.json',
    'example/assets/test_data/snapshot_test_data/div-gallery/gallery-default-item-with-states.json',
    'example/assets/test_data/snapshot_test_data/div-gallery/horizontal-gallery-default-item.json',
    'example/assets/test_data/snapshot_test_data/div-gallery/horizontal-gallery-fixed-height.json',
    'example/assets/test_data/snapshot_test_data/div-gallery/horizontal-gallery-items-not-fit.json',
    'example/assets/test_data/snapshot_test_data/div-gallery/horizontal-gallery-items-resizable-height.json',
    'example/assets/test_data/snapshot_test_data/div-gallery/horizontal-gallery-resizable-height.json',
    'example/assets/test_data/snapshot_test_data/div-gallery/horizontal-gallery-scrollbar.json',
    'example/assets/test_data/snapshot_test_data/div-gallery/horizontal-gallery-vertical-shrinking.json',
    'example/assets/test_data/snapshot_test_data/div-gallery/horizontal-gallery-with-columns.json',
    'example/assets/test_data/snapshot_test_data/div-gallery/vertical-gallery-fixed-height.json',
    'example/assets/test_data/snapshot_test_data/div-gallery/vertical-gallery-items-not-fit.json',
    'example/assets/test_data/snapshot_test_data/div-gallery/vertical-gallery-items-resizable-height.json',
    'example/assets/test_data/snapshot_test_data/div-gallery/vertical-gallery-resizable-height.json',
    'example/assets/test_data/snapshot_test_data/div-gallery/vertical-gallery-scrollbar.json',
    'example/assets/test_data/snapshot_test_data/div-gallery/vertical-gallery-with-columns.json',
    'example/assets/test_data/snapshot_test_data/div-gallery/vertical-grid-gallery-wrapped-item-spacing.json',
    'example/assets/test_data/snapshot_test_data/div-gallery/item-builder/item-builder.json',
    'example/assets/test_data/snapshot_test_data/div-gallery/item-builder/nested-builders.json',
    'example/assets/test_data/snapshot_test_data/div-gallery/item-builder/non-unique-matched-selectors.json'
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.unknown,
    ),
  ),

// # div-gif-image

  ...[
    'example/assets/test_data/snapshot_test_data/div-gif-image/placeholder-color.json',
    'example/assets/test_data/snapshot_test_data/div-gif-image/preview.json',
    'example/assets/test_data/snapshot_test_data/div-gif-image/aspect-fixed.json',
    'example/assets/test_data/snapshot_test_data/div-gif-image/aspect-match_parent.json',
    'example/assets/test_data/snapshot_test_data/div-gif-image/aspect-wrap_content.json',
    'example/assets/test_data/snapshot_test_data/div-gif-image/border-with-stroke.json',
    'example/assets/test_data/snapshot_test_data/div-gif-image/corner-radius.json',
    'example/assets/test_data/snapshot_test_data/div-gif-image/corners_radius.json',
    'example/assets/test_data/snapshot_test_data/div-gif-image/custom-alpha.json',
    'example/assets/test_data/snapshot_test_data/div-gif-image/custom-height.json',
    'example/assets/test_data/snapshot_test_data/div-gif-image/custom-margins.json',
    'example/assets/test_data/snapshot_test_data/div-gif-image/custom-paddings.json',
    'example/assets/test_data/snapshot_test_data/div-gif-image/custom-width.json',
    'example/assets/test_data/snapshot_test_data/div-gif-image/no_scale.json',
    'example/assets/test_data/snapshot_test_data/div-gif-image/no_scale_bottom_right.json',
    'example/assets/test_data/snapshot_test_data/div-gif-image/no_scale_top_left.json',
    'example/assets/test_data/snapshot_test_data/div-gif-image/scale_fill.json',
    'example/assets/test_data/snapshot_test_data/div-gif-image/scale_fill_bottom.json',
    'example/assets/test_data/snapshot_test_data/div-gif-image/scale_fill_right.json',
    'example/assets/test_data/snapshot_test_data/div-gif-image/scale_fit.json',
    'example/assets/test_data/snapshot_test_data/div-gif-image/scale_fit_bottom.json',
    'example/assets/test_data/snapshot_test_data/div-gif-image/scale_fit_left.json',
    'example/assets/test_data/snapshot_test_data/div-gif-image/scale_fit_right.json',
    'example/assets/test_data/snapshot_test_data/div-gif-image/scale_fit_top.json',
    'example/assets/test_data/snapshot_test_data/div-gif-image/scale_stretch.json',
    'example/assets/test_data/snapshot_test_data/div-gif-image/shadow.json',
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.divNotSupported,
    ),
  ),

// # div-grid

  ...[
    'example/assets/test_data/snapshot_test_data/div-grid/empty.json',
    'example/assets/test_data/snapshot_test_data/div-grid/non-rectangular-grid.json',
    'example/assets/test_data/snapshot_test_data/div-grid/non-rectangular-spanned-grid.json',
    'example/assets/test_data/snapshot_test_data/div-grid/all-fixed.json',
    'example/assets/test_data/snapshot_test_data/div-grid/ambiguous-span.json',
    'example/assets/test_data/snapshot_test_data/div-grid/content-not-fit-with-paddings.json',
    'example/assets/test_data/snapshot_test_data/div-grid/corners_radius.json',
    'example/assets/test_data/snapshot_test_data/div-grid/examples/example_1.json',
    'example/assets/test_data/snapshot_test_data/div-grid/examples/example_2.json',
    'example/assets/test_data/snapshot_test_data/div-grid/examples/example_3.json',
    'example/assets/test_data/snapshot_test_data/div-grid/examples/example_4.json',
    'example/assets/test_data/snapshot_test_data/div-grid/examples/example_5.json',
    'example/assets/test_data/snapshot_test_data/div-grid/examples/example_6.json',
    'example/assets/test_data/snapshot_test_data/div-grid/examples/example_7.json',
    'example/assets/test_data/snapshot_test_data/div-grid/fixed-size-with-paddings.json',
    'example/assets/test_data/snapshot_test_data/div-grid/fixed-width-distribution.json',
    'example/assets/test_data/snapshot_test_data/div-grid/intersected-fixed-spans.json',
    'example/assets/test_data/snapshot_test_data/div-grid/intrinsic-width.json',
    'example/assets/test_data/snapshot_test_data/div-grid/item-alignments.json',
    'example/assets/test_data/snapshot_test_data/div-grid/resizable-height.json',
    'example/assets/test_data/snapshot_test_data/div-grid/resizable-width.json',
    'example/assets/test_data/snapshot_test_data/div-grid/spans.json',
    'example/assets/test_data/snapshot_test_data/div-grid/weighted-calculation.json',
    'example/assets/test_data/snapshot_test_data/div-grid/weighted-greater-than-fixed.json',
    'example/assets/test_data/snapshot_test_data/div-grid/weighted-less-than-fixed.json',
    'example/assets/test_data/snapshot_test_data/div-grid/weighted-plus-match-parent.json',
    'example/assets/test_data/snapshot_test_data/div-grid/without-weight.json',
    'example/assets/test_data/snapshot_test_data/div-grid/wrap-content-constrained-greater-than-parent.json',
    'example/assets/test_data/snapshot_test_data/div-grid/wrap-content-constrained-height.json',
    'example/assets/test_data/snapshot_test_data/div-grid/wrap-content-constrained-items.json',
    'example/assets/test_data/snapshot_test_data/div-grid/wrap-content-items.json',
    'example/assets/test_data/snapshot_test_data/div-grid/wrap-content-weighted-items.json',
    'example/assets/test_data/snapshot_test_data/div-grid/content-alignment.json',
    'example/assets/test_data/snapshot_test_data/div-grid/wrap-content-greater-than-parent.json',
    'example/assets/test_data/snapshot_test_data/div-grid/match-parent-items-with-margins.json'
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.divNotSupported,
    ),
  ),

// # div-image

  ...[
    'example/assets/test_data/snapshot_test_data/div-image/custom-preview.json',
    'example/assets/test_data/snapshot_test_data/div-image/custom-tint-color.json',
    'example/assets/test_data/snapshot_test_data/div-image/placeholder-color.json',
    'example/assets/test_data/snapshot_test_data/div-image/preview.json',
    'example/assets/test_data/snapshot_test_data/div-image/tint-mode.json',
    'example/assets/test_data/snapshot_test_data/div-image/aspect-fixed.json',
    'example/assets/test_data/snapshot_test_data/div-image/aspect-match_parent.json',
    'example/assets/test_data/snapshot_test_data/div-image/aspect-wrap_content.json',
    'example/assets/test_data/snapshot_test_data/div-image/blur-with-big-radius.json',
    'example/assets/test_data/snapshot_test_data/div-image/blur.json',
    'example/assets/test_data/snapshot_test_data/div-image/border-with-stroke.json',
    'example/assets/test_data/snapshot_test_data/div-image/composite-background.json',
    'example/assets/test_data/snapshot_test_data/div-image/content-horizontal-alignment-end.json',
    'example/assets/test_data/snapshot_test_data/div-image/content-horizontal-alignment-start.json',
    'example/assets/test_data/snapshot_test_data/div-image/corner-radius.json',
    'example/assets/test_data/snapshot_test_data/div-image/corners_radius.json',
    'example/assets/test_data/snapshot_test_data/div-image/custom-alpha.json',
    'example/assets/test_data/snapshot_test_data/div-image/custom-height.json',
    'example/assets/test_data/snapshot_test_data/div-image/custom-margins.json',
    'example/assets/test_data/snapshot_test_data/div-image/custom-paddings.json',
    'example/assets/test_data/snapshot_test_data/div-image/custom-width.json',
    'example/assets/test_data/snapshot_test_data/div-image/no_scale.json',
    'example/assets/test_data/snapshot_test_data/div-image/no_scale_bottom_right.json',
    'example/assets/test_data/snapshot_test_data/div-image/no_scale_top_left.json',
    'example/assets/test_data/snapshot_test_data/div-image/rtl-filter.json',
    'example/assets/test_data/snapshot_test_data/div-image/rtl-image-content-horizontal-alignment-end.json',
    'example/assets/test_data/snapshot_test_data/div-image/rtl-image-content-horizontal-alignment-start.json',
    'example/assets/test_data/snapshot_test_data/div-image/scale_fill.json',
    'example/assets/test_data/snapshot_test_data/div-image/scale_fill_bottom.json',
    'example/assets/test_data/snapshot_test_data/div-image/scale_fill_right.json',
    'example/assets/test_data/snapshot_test_data/div-image/scale_fit.json',
    'example/assets/test_data/snapshot_test_data/div-image/scale_fit_bottom.json',
    'example/assets/test_data/snapshot_test_data/div-image/scale_fit_left.json',
    'example/assets/test_data/snapshot_test_data/div-image/scale_fit_right.json',
    'example/assets/test_data/snapshot_test_data/div-image/scale_fit_top.json',
    'example/assets/test_data/snapshot_test_data/div-image/scale_stretch.json',
    'example/assets/test_data/snapshot_test_data/div-image/shadow.json',
    'example/assets/test_data/snapshot_test_data/div-image/wrap-content-width-by-height.json',
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.haveToLoadNetworkImage,
    ),
  ),

// # div-indicator

  ...[
    'example/assets/test_data/snapshot_test_data/div-indicator/stroke.json',
    'example/assets/test_data/snapshot_test_data/div-indicator/active_size.json',
    'example/assets/test_data/snapshot_test_data/div-indicator/corners_radius.json',
    'example/assets/test_data/snapshot_test_data/div-indicator/fixed-height.json',
    'example/assets/test_data/snapshot_test_data/div-indicator/fixed-width-max_items_circle.json',
    'example/assets/test_data/snapshot_test_data/div-indicator/fixed-width-max_items_rectangle.json',
    'example/assets/test_data/snapshot_test_data/div-indicator/fixed-width-max_items_rectangle_slider.json',
    'example/assets/test_data/snapshot_test_data/div-indicator/fixed-width-max_items_rectangle_worm.json',
    'example/assets/test_data/snapshot_test_data/div-indicator/margins.json',
    'example/assets/test_data/snapshot_test_data/div-indicator/match_parent-width-max_items.json',
    'example/assets/test_data/snapshot_test_data/div-indicator/minimum_size.json',
    'example/assets/test_data/snapshot_test_data/div-indicator/paddings.json',
    'example/assets/test_data/snapshot_test_data/div-indicator/shapes.json',
    'example/assets/test_data/snapshot_test_data/div-indicator/stretch_items.json',
    'example/assets/test_data/snapshot_test_data/div-indicator/stretch_items_max_items_constraint.json',
    'example/assets/test_data/snapshot_test_data/div-indicator/wrap_content-height.json',
    'example/assets/test_data/snapshot_test_data/div-indicator/wrap_content-width-max_items.json',
    'example/assets/test_data/snapshot_test_data/div-indicator/rtl_div_indicator.json',
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.divNotSupported,
    ),
  ),

// # div-pager

  ...[
    'example/assets/test_data/snapshot_test_data/div-pager/corners_radius.json',
    'example/assets/test_data/snapshot_test_data/div-pager/horizontal-pager-constrained-height-with-paddings.json',
    'example/assets/test_data/snapshot_test_data/div-pager/horizontal-pager-custom-item-spacing.json',
    'example/assets/test_data/snapshot_test_data/div-pager/horizontal-pager-custom-neighbour-page-width-with-paddings.json',
    'example/assets/test_data/snapshot_test_data/div-pager/horizontal-pager-custom-neighbour-page-width.json',
    'example/assets/test_data/snapshot_test_data/div-pager/horizontal-pager-custom-page-width-with-paddings.json',
    'example/assets/test_data/snapshot_test_data/div-pager/horizontal-pager-custom-page-width.json',
    'example/assets/test_data/snapshot_test_data/div-pager/horizontal-pager-fixed-height-with-paddings.json',
    'example/assets/test_data/snapshot_test_data/div-pager/horizontal-pager-fixed-width.json',
    'example/assets/test_data/snapshot_test_data/div-pager/horizontal-pager-match-parent-height-with-paddings.json',
    'example/assets/test_data/snapshot_test_data/div-pager/horizontal-pager-same-paddings.json',
    'example/assets/test_data/snapshot_test_data/div-pager/vertical-pager-constrained-width-with-paddings.json',
    'example/assets/test_data/snapshot_test_data/div-pager/vertical-pager-custom-item-spacing.json',
    'example/assets/test_data/snapshot_test_data/div-pager/vertical-pager-custom-margins.json',
    'example/assets/test_data/snapshot_test_data/div-pager/vertical-pager-custom-neighbour-page-width-with-paddings.json',
    'example/assets/test_data/snapshot_test_data/div-pager/vertical-pager-custom-neighbour-page-width.json',
    'example/assets/test_data/snapshot_test_data/div-pager/vertical-pager-custom-page-width-with-paddings.json',
    'example/assets/test_data/snapshot_test_data/div-pager/vertical-pager-custom-page-width.json',
    'example/assets/test_data/snapshot_test_data/div-pager/vertical-pager-fixed-width.json',
    'example/assets/test_data/snapshot_test_data/div-pager/vertical-pager-match-parent-width-with-paddings.json',
    'example/assets/test_data/snapshot_test_data/div-pager/empty.json',
    'example/assets/test_data/snapshot_test_data/div-pager/horizontal-pager-fixed-height.json',
    'example/assets/test_data/snapshot_test_data/div-pager/horizontal-pager-items-resizable-height.json',
    'example/assets/test_data/snapshot_test_data/div-pager/horizontal-pager-resizable-height.json',
    'example/assets/test_data/snapshot_test_data/div-pager/horizontal-pager-resizable-width.json',
    'example/assets/test_data/snapshot_test_data/div-pager/horizontal-pager-wrap-content-height-with-paddings.json',
    'example/assets/test_data/snapshot_test_data/div-pager/pager-default-item-with-states.json',
    'example/assets/test_data/snapshot_test_data/div-pager/rtl-pager.json',
    'example/assets/test_data/snapshot_test_data/div-pager/vertical-pager-fixed-height.json',
    'example/assets/test_data/snapshot_test_data/div-pager/vertical-pager-fixed-width-with-paddings.json',
    'example/assets/test_data/snapshot_test_data/div-pager/vertical-pager-resizable-height.json',
    'example/assets/test_data/snapshot_test_data/div-pager/vertical-pager-resizable-width.json',
    'example/assets/test_data/snapshot_test_data/div-pager/vertical-pager-wrap-content-width-with-paddings.json',
    'example/assets/test_data/snapshot_test_data/div-pager/item-builder/item-builder.json',
    'example/assets/test_data/snapshot_test_data/div-pager/item-builder/nested-builders.json',
    'example/assets/test_data/snapshot_test_data/div-pager/item-builder/non-unique-matched-selectors.json',
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.divNotSupported,
    ),
  ),

// # div-radial-gradient

  ...[
    'example/assets/test_data/snapshot_test_data/div-radial-gradient/fixed-center-negative.json',
    'example/assets/test_data/snapshot_test_data/div-radial-gradient/fixed-center-positive.json',
    'example/assets/test_data/snapshot_test_data/div-radial-gradient/relative-center-positive.json',
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.crashesOnRenderFlexOverflow,
      'https://paste.yandex-team.ru/25bfbf31-e08b-46a7-b372-31c45909d5e5',
    ),
  ),

  ...[
    'example/assets/test_data/snapshot_test_data/div-radial-gradient/relative-radius-farthest-corner.json',
    'example/assets/test_data/snapshot_test_data/div-radial-gradient/relative-radius-farthest-side.json',
    'example/assets/test_data/snapshot_test_data/div-radial-gradient/relative-radius-nearest-corner.json',
    'example/assets/test_data/snapshot_test_data/div-radial-gradient/relative-radius-nearest-side.json',
    'example/assets/test_data/snapshot_test_data/div-radial-gradient/zero-radius.json',
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.crashesOnImageDimensions,
      'https://paste.yandex-team.ru/b3cb8900-6dea-49c8-9460-605bb8188e96',
    ),
  ),

// # div-select

  ...[
    'example/assets/test_data/snapshot_test_data/div-select/all-attributes.json',
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.divNotSupported,
    ),
  ),

// # div-separator

  ...[
    'example/assets/test_data/snapshot_test_data/div-separator/big_corner_radius.json',
    'example/assets/test_data/snapshot_test_data/div-separator/custom-color.json',
    'example/assets/test_data/snapshot_test_data/div-separator/custom-paddings.json',
    'example/assets/test_data/snapshot_test_data/div-separator/default-values.json',
    'example/assets/test_data/snapshot_test_data/div-separator/fixed-size-with-paddings.json',
    'example/assets/test_data/snapshot_test_data/div-separator/horizontal-fixed-height.json',
    'example/assets/test_data/snapshot_test_data/div-separator/horizontal-fixed-width.json',
    'example/assets/test_data/snapshot_test_data/div-separator/vertical-fixed-height.json',
    'example/assets/test_data/snapshot_test_data/div-separator/vertical-fixed-width.json',
    'example/assets/test_data/snapshot_test_data/div-separator/vertical-intrinsic-width.json',
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.divNotSupported,
    ),
  ),

// # div-shadow

  ...[
    'example/assets/test_data/snapshot_test_data/div-shadow/shadow-with-alpha.json',
    'example/assets/test_data/snapshot_test_data/div-shadow/shadow-with-blur.json',
    'example/assets/test_data/snapshot_test_data/div-shadow/shadow-with-color.json',
    'example/assets/test_data/snapshot_test_data/div-shadow/shadow-with-offset.json',
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.haveToLoadNetworkImage,
    ),
  ),

// # div-size

  ...[
    'example/assets/test_data/snapshot_test_data/div-size/wrap-content/constrained-true-margins-and-paddings-horizontal.json',
    'example/assets/test_data/snapshot_test_data/div-size/wrap-content/constrained-true-margins-and-paddings-overlap.json',
    'example/assets/test_data/snapshot_test_data/div-size/wrap-content/constrained-true-margins-and-paddings-vertical.json',
    'example/assets/test_data/snapshot_test_data/div-size/wrap-content/constrained-true-margins-and-paddings-wrap-horizontal.json',
    'example/assets/test_data/snapshot_test_data/div-size/wrap-content/constrained-true-margins-and-paddings-wrap-vertical.json',
    'example/assets/test_data/snapshot_test_data/div-size/wrap-content/min-max-constrained-true-horizontal-weigted.json',
    'example/assets/test_data/snapshot_test_data/div-size/wrap-content/min-max-constrained-true-horizontal.json',
    'example/assets/test_data/snapshot_test_data/div-size/wrap-content/min-max-constrained-true-overlap.json',
    'example/assets/test_data/snapshot_test_data/div-size/wrap-content/min-max-constrained-true-vertical-weighted.json',
    'example/assets/test_data/snapshot_test_data/div-size/wrap-content/min-max-constrained-true-vertical.json',
    'example/assets/test_data/snapshot_test_data/div-size/wrap-content/min-max-constrained-true-wrap-horizontal.json',
    'example/assets/test_data/snapshot_test_data/div-size/wrap-content/min-max-constrained-true-wrap-vertical.json',
    'example/assets/test_data/snapshot_test_data/div-size/wrap-content/min-max-constrained-false-horizontal.json',
    'example/assets/test_data/snapshot_test_data/div-size/wrap-content/min-max-constrained-false-overlap.json',
    'example/assets/test_data/snapshot_test_data/div-size/wrap-content/min-max-constrained-false-vertical.json',
    'example/assets/test_data/snapshot_test_data/div-size/wrap-content/min-max-constrained-false-wrap-horizontal.json',
    'example/assets/test_data/snapshot_test_data/div-size/wrap-content/min-max-constrained-false-wrap-vertical.json',
    'example/assets/test_data/snapshot_test_data/div-size/wrap-content/text-min-max-constrained-false-horizontal.json',
    'example/assets/test_data/snapshot_test_data/div-size/wrap-content/text-min-max-constrained-false-overlap.json',
    'example/assets/test_data/snapshot_test_data/div-size/padding/padding-bigger-than-box.json',
    'example/assets/test_data/snapshot_test_data/div-size/padding/padding-smaller-than-box.json'
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.unknown,
      'Current behavior not matches target',
    ),
  ),

// # div-slider

  ...[
    'example/assets/test_data/snapshot_test_data/div-slider/base.json',
    'example/assets/test_data/snapshot_test_data/div-slider/custom_active_track.json',
    'example/assets/test_data/snapshot_test_data/div-slider/custom_inactive_track.json',
    'example/assets/test_data/snapshot_test_data/div-slider/custom_text_bottom.json',
    'example/assets/test_data/snapshot_test_data/div-slider/custom_text_left.json',
    'example/assets/test_data/snapshot_test_data/div-slider/custom_text_left_top.json',
    'example/assets/test_data/snapshot_test_data/div-slider/custom_text_right.json',
    'example/assets/test_data/snapshot_test_data/div-slider/custom_text_top.json',
    'example/assets/test_data/snapshot_test_data/div-slider/custom_text_top_bottom.json',
    'example/assets/test_data/snapshot_test_data/div-slider/custom_thumb.json',
    'example/assets/test_data/snapshot_test_data/div-slider/custom_thumb_secondary.json',
    'example/assets/test_data/snapshot_test_data/div-slider/custom_thumb_text.json',
    'example/assets/test_data/snapshot_test_data/div-slider/custom_thumb_text_secondary.json',
    'example/assets/test_data/snapshot_test_data/div-slider/custom_tick_marks.json',
    'example/assets/test_data/snapshot_test_data/div-slider/fixed_size.json',
    'example/assets/test_data/snapshot_test_data/div-slider/match_parent_size.json',
    'example/assets/test_data/snapshot_test_data/div-slider/secondary_thumb_on_left.json',
    'example/assets/test_data/snapshot_test_data/div-slider/shapes_tick.json',
    'example/assets/test_data/snapshot_test_data/div-slider/slider_nps.json',
    'example/assets/test_data/snapshot_test_data/div-slider/with_active_tick_marks.json',
    'example/assets/test_data/snapshot_test_data/div-slider/with_inactive_tick_marks.json',
    'example/assets/test_data/snapshot_test_data/div-slider/with_paddings.json',
    'example/assets/test_data/snapshot_test_data/div-slider/with_stroke.json',
    'example/assets/test_data/snapshot_test_data/div-slider/wrap_content_size.json',
    'example/assets/test_data/snapshot_test_data/div-slider/ranges.json',
    'example/assets/test_data/snapshot_test_data/div-slider/rtl_slider.json',
    'example/assets/test_data/snapshot_test_data/div-slider/shapes_thumb.json',
    'example/assets/test_data/snapshot_test_data/div-slider/slider_with_error_and_warning.json',
    'example/assets/test_data/snapshot_test_data/div-slider/slider_with_warning.json',
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.divNotSupported,
    ),
  ),

// # div-tabs

  ...[
    'example/assets/test_data/snapshot_test_data/div-tabs/title-delimiters.json',
    'example/assets/test_data/snapshot_test_data/div-tabs/too-big-constrained-tabs.json',
    'example/assets/test_data/snapshot_test_data/div-tabs/spaces-in-title.json',
    'example/assets/test_data/snapshot_test_data/div-tabs/corners_radius.json',
    'example/assets/test_data/snapshot_test_data/div-tabs/custom-background-border.json',
    'example/assets/test_data/snapshot_test_data/div-tabs/custom-margins.json',
    'example/assets/test_data/snapshot_test_data/div-tabs/custom-paddings.json',
    'example/assets/test_data/snapshot_test_data/div-tabs/custom-title-style.json',
    'example/assets/test_data/snapshot_test_data/div-tabs/different-child-height.json',
    'example/assets/test_data/snapshot_test_data/div-tabs/fixed-height.json',
    'example/assets/test_data/snapshot_test_data/div-tabs/fixed-width.json',
    'example/assets/test_data/snapshot_test_data/div-tabs/match-parent-pages.json',
    'example/assets/test_data/snapshot_test_data/div-tabs/selected-tab-above-range.json',
    'example/assets/test_data/snapshot_test_data/div-tabs/selected-tab-broken-content.json',
    'example/assets/test_data/snapshot_test_data/div-tabs/selected-tab.json',
    'example/assets/test_data/snapshot_test_data/div-tabs/separator-paddings.json',
    'example/assets/test_data/snapshot_test_data/div-tabs/separator-resizing.json',
    'example/assets/test_data/snapshot_test_data/div-tabs/tabs-dynamic-height.json',
    'example/assets/test_data/snapshot_test_data/div-tabs/tabs-max-height.json',
    'example/assets/test_data/snapshot_test_data/div-tabs/too-big-constrained-height-with-paddings.json',
    'example/assets/test_data/snapshot_test_data/div-tabs/wrap_content-width.json',
    'example/assets/test_data/snapshot_test_data/div-tabs/match-parent-height-with-paddings.json',
    'example/assets/test_data/snapshot_test_data/div-tabs/rtl-tabs.json',
    'example/assets/test_data/snapshot_test_data/div-tabs/tabs-and-gallery.json',
    'example/assets/test_data/snapshot_test_data/div-tabs/title-with-expression.json',
    'example/assets/test_data/snapshot_test_data/div-tabs/too-big-fixed-height-with-paddings.json',
    'example/assets/test_data/snapshot_test_data/div-tabs/too-big-wrap-content-height-with-paddings.json',
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.divNotSupported,
    ),
  ),

  const _SkipTest(
    'example/assets/test_data/snapshot_test_data/div-container/match-parent-with-margins.json',
    _Reason.unknown,
    'Not supported by platform',
  ),

  const _SkipTest(
    'example/assets/test_data/snapshot_test_data/div-text/hyphenation.json',
    _Reason.unknown,
    'Not supported by platform',
  ),

  const _SkipTest(
    'example/assets/test_data/snapshot_test_data/div-input/with-native-interface-no-background.json',
    _Reason.unknown,
    'Not supported by platform',
  ),

  ...[
    'example/assets/test_data/snapshot_test_data/div-text/ranges-line-height-top-offset.json',
    'example/assets/test_data/snapshot_test_data/div-text/ranges.json',
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.crashesOnRenderFlexOverflow,
      'Overflow in wrap content size',
    ),
  ),

  ...[
    'example/assets/test_data/snapshot_test_data/div-text/ranges-line-height-top-offset.json',
    'example/assets/test_data/snapshot_test_data/div-text/ranges-background-text.json',
    'example/assets/test_data/snapshot_test_data/div-text/gradient-color-with-ranges.json',
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.divNotSupported,
      'Not supported by platform',
    ),
  ),

  const _SkipTest(
    'example/assets/test_data/snapshot_test_data/div-text/text-shadow-alpha.json',
    _Reason.divNotSupported,
    'https://paste.yandex-team.ru/4132d91f-115f-481a-a581-97af4242af76',
  ),
  const _SkipTest(
    'example/assets/test_data/snapshot_test_data/div-text/text-shadow.json',
    _Reason.divNotSupported,
    'https://paste.yandex-team.ru/4132d91f-115f-481a-a581-97af4242af76',
  ),
  const _SkipTest(
    'example/assets/test_data/snapshot_test_data/div-text/blur-background.json',
    _Reason.haveToLoadNetworkImage,
    'https://paste.yandex-team.ru/3e965301-f3ac-4ff3-ba62-c81665108399',
  ),
  const _SkipTest(
    'example/assets/test_data/snapshot_test_data/div-text/composite-background.json',
    _Reason.haveToLoadNetworkImage,
    'https://paste.yandex-team.ru/04b3d5a3-5e4f-49d6-89b5-f5544e6e7242',
  ),
  const _SkipTest(
    'example/assets/test_data/snapshot_test_data/div-text/image-background.json',
    _Reason.haveToLoadNetworkImage,
    'https://paste.yandex-team.ru/15650b63-7b14-4c08-9259-db6f5b3de7b6',
  ),
  const _SkipTest(
    'example/assets/test_data/snapshot_test_data/div-text/images.json',
    _Reason.crashesOnRenderFlexLayout,
    'https://paste.yandex-team.ru/66b81483-d996-4a76-9b50-0fa88924b0a1',
  ),
  const _SkipTest(
    'example/assets/test_data/snapshot_test_data/div-text/variables-rendering.json',
    _Reason.unknown,
    'Native calls are not supported, can be fixed after dart expression resolver',
  ),

// # div-transform

  ...[
    'example/assets/test_data/snapshot_test_data/div-transform/transform_container.json',
    'example/assets/test_data/snapshot_test_data/div-transform/transform_container_pisa.json',
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.divNotSupported,
    ),
  ),
  ...[
    'example/assets/test_data/snapshot_test_data/div-transform/transform_identity.json',
    'example/assets/test_data/snapshot_test_data/div-transform/transform_turn_45_around_bottom_center.json',
    'example/assets/test_data/snapshot_test_data/div-transform/transform_turn_45_around_bottom_left_corner.json',
    'example/assets/test_data/snapshot_test_data/div-transform/transform_turn_45_around_bottom_right_corner.json',
    'example/assets/test_data/snapshot_test_data/div-transform/transform_turn_45_around_center.json',
    'example/assets/test_data/snapshot_test_data/div-transform/transform_turn_45_around_left_center.json',
    'example/assets/test_data/snapshot_test_data/div-transform/transform_turn_45_around_right_center.json',
    'example/assets/test_data/snapshot_test_data/div-transform/transform_turn_45_around_top_center.json',
    'example/assets/test_data/snapshot_test_data/div-transform/transform_turn_45_around_top_left_corner.json',
    'example/assets/test_data/snapshot_test_data/div-transform/transform_turn_45_around_top_right_corner.json',
    'example/assets/test_data/snapshot_test_data/div-transform/transform_turn_90_around_center.json',
    'example/assets/test_data/snapshot_test_data/div-text/custom-image-tint-color.json',
    'example/assets/test_data/snapshot_test_data/div-text/custom-text-alignment-with-attachments.json',
    'example/assets/test_data/snapshot_test_data/div-text/ellipsis-with-image.json',
    'example/assets/test_data/snapshot_test_data/div-text/all_attributes.json',
    'example/assets/test_data/snapshot_test_data/div-container/vertical-composite-background.json',
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.haveToLoadNetworkImage,
    ),
  ),

  ...[
    'example/assets/test_data/snapshot_test_data/div-svg/svg_background.json',
    'example/assets/test_data/snapshot_test_data/div-svg/svg_image_effects.json',
    'example/assets/test_data/snapshot_test_data/div-svg/svg_images.json',
    'example/assets/test_data/snapshot_test_data/div-svg/svg_images_invalid_values.json',
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.haveToLoadNetworkImage,
    ),
  ),
];
