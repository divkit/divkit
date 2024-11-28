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
  // SVG images crash, or test stucks on forever
  // We need to support mocking them
  // https://st.yandex-team.ru/YXPRODELIVERY-4382
  svgImage,
  // One or more sides of the image happened to be 0
  invalidImageDimensions,
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
    'test_data/snapshot_test_data/div-background/nine-patch-rhombs-horizontal-insets.json',
    'test_data/snapshot_test_data/div-background/nine-patch-rhombs-large-all-insets.json',
    'test_data/snapshot_test_data/div-background/nine-patch-rhombs-large-bottom-inset.json',
    'test_data/snapshot_test_data/div-background/nine-patch-rhombs-large-bottom-left-inset.json',
    'test_data/snapshot_test_data/div-background/nine-patch-rhombs-large-bottom-left-right-inset.json',
    'test_data/snapshot_test_data/div-background/nine-patch-rhombs-vertical-insets.json',
    'test_data/snapshot_test_data/div-background/nine-patch-rhombs.json',
    'test_data/snapshot_test_data/div-background/nine-patch-shape.json',
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.divNotSupported,
      null,
    ),
  ),

  ...const [
    'test_data/snapshot_test_data/div-container/horizontal-orientation-with-between-separators-using-paddings.json',
    'test_data/snapshot_test_data/div-container/horizontal-orientation-with-between-separators.json',
    'test_data/snapshot_test_data/div-container/horizontal-orientation-with-all-separators-using-paddings.json',
    'test_data/snapshot_test_data/div-container/horizontal-orientation-with-all-separators.json',
    'test_data/snapshot_test_data/div-container/vertical-orientation-with-between-separators-using-paddings.json',
    'test_data/snapshot_test_data/div-container/vertical-orientation-with-between-separators.json',
    'test_data/snapshot_test_data/div-container/vertical-orientation-with-all-separators-using-paddings.json',
    'test_data/snapshot_test_data/div-container/vertical-orientation-with-all-separators.json',
    'test_data/snapshot_test_data/div-container/wrap/horizontal-orientation-with-separators.json',
    'test_data/snapshot_test_data/div-container/wrap/horizontal-orientation-with-separators-space-around.json',
    'test_data/snapshot_test_data/div-container/wrap/horizontal-orientation-with-separators-space-evenly.json',
    'test_data/snapshot_test_data/div-container/wrap/horizontal-orientation-with-separators-using-paddings.json',
    'test_data/snapshot_test_data/div-container/wrap/horizontal-orientation-with-separators-using-paddings-rtl.json',
    'test_data/snapshot_test_data/div-container/wrap/vertical-orientation-with-separators.json',
    'test_data/snapshot_test_data/div-container/wrap/vertical-orientation-with-separators-space-around.json',
    'test_data/snapshot_test_data/div-container/wrap/vertical-orientation-with-separators-space-evenly.json',
    'test_data/snapshot_test_data/div-container/wrap/vertical-orientation-with-separators-using-paddings.json',
    'test_data/snapshot_test_data/div-container/wrap/vertical-orientation-with-separators-using-paddings-rtl.json',
    'test_data/snapshot_test_data/div-container/block-paddings.json',
    'test_data/snapshot_test_data/div-container/match-parent-with-big-content.json',
    'test_data/snapshot_test_data/div-container/fixed-size-with-paddings.json',
    'test_data/snapshot_test_data/div-container/corners_radius.json',
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.divNotSupported,
      'Separators are not supported yet',
    ),
  ),

  ...const [
    'test_data/snapshot_test_data/div-container/baseline.json',
    'test_data/snapshot_test_data/div-container/baseline-with-vertical-containers.json',
    'test_data/snapshot_test_data/div-container/wrap/baseline.json',
    'test_data/snapshot_test_data/div-container/baseline-with-images.json',
    'test_data/snapshot_test_data/div-input/text-alignment-property.json',
    'test_data/snapshot_test_data/div-input/text-alignment-property-single-line.json',
    'test_data/snapshot_test_data/div-input/all-attributes.json',
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.divNotSupported,
      'Baseline alignment not supported yet',
    ),
  ),

  ...const [
    /// TODO: pass border (stroke) to inside of container
    'test_data/snapshot_test_data/div-container/border-box-model.json',
    'test_data/snapshot_test_data/div-text/border-with-stoke.json',
    'test_data/snapshot_test_data/div-container/wrap/horizontal-orientation-child-alignments.json',
    'test_data/snapshot_test_data/div-container/wrap/vertical-orientation-child-alignments.json',
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.divNotSupported,
      'Known cases',
    ),
  ),

// # div-container/aspect

  const _SkipTest(
    'test_data/snapshot_test_data/div-container/aspect/wrap_content-width-horizontal.json',
    _Reason.crashesOnAspectRatio,
    'https://paste.yandex-team.ru/bd749f41-872d-42f2-a740-8a44e989e41f',
  ),
  const _SkipTest(
    'test_data/snapshot_test_data/div-container/aspect/wrap_content-width-overlap.json',
    _Reason.crashesOnAspectRatio,
    'https://paste.yandex-team.ru/585ce2b6-50b9-4a67-9851-984e2a211284',
  ),
  const _SkipTest(
    'test_data/snapshot_test_data/div-container/aspect/wrap_content-width-overlap-match_parent-height.json',
    _Reason.crashesOnRenderFlexOverflow,
    'https://paste.yandex-team.ru/690a312d-b5aa-4cc3-b182-3d2efcca81a0',
  ),
  const _SkipTest(
    'test_data/snapshot_test_data/div-container/aspect/wrap_content-width-vertical-constrained-child-not-fit.json',
    _Reason.crashesOnAspectRatio,
    'https://paste.yandex-team.ru/221414c0-8858-4e3b-818f-791f55ce0f32',
  ),
  const _SkipTest(
    'test_data/snapshot_test_data/div-container/aspect/wrap_content-width-vertical-match_parent-child-not-fit.json',
    _Reason.crashesOnAspectRatio,
    'https://paste.yandex-team.ru/dc501869-a777-48cc-8f81-683d5bd13dfe',
  ),
  const _SkipTest(
    'test_data/snapshot_test_data/div-container/aspect/wrap_content-width-vertical-match_parent-height.json',
    _Reason.crashesOnRenderFlexOverflow,
    'https://paste.yandex-team.ru/a796ab09-dab0-4835-8db3-a87a561ab0bf',
  ),
  const _SkipTest(
    'test_data/snapshot_test_data/div-container/aspect/wrap_content-width-overlap-fixed-height.json',
    _Reason.crashesOnImageDimensions,
  ),
  const _SkipTest(
    'test_data/snapshot_test_data/div-container/aspect/wrap-layout_mode-horizontal-fixed-height.json',
    _Reason.crashesOnImageDimensions,
  ),
  const _SkipTest(
    'test_data/snapshot_test_data/div-text/ellipsis.json',
    _Reason.divNotSupported,
    'Custom ellipsize is not supported yet, requires custom Text widget',
  ),
  const _SkipTest(
    'test_data/snapshot_test_data/div-container/aspect/wrap_content-width-vertical-fixed-height.json',
    _Reason.crashesOnImageDimensions,
  ),

// # div-container/wrap

  const _SkipTest(
    'test_data/snapshot_test_data/div-container/wrap/horizontal-orientation-match-parent-cross.json',
    _Reason.crashesOnRenderFlexLayout,
    'https://paste.yandex-team.ru/04a5c12e-825c-4e24-812d-5ae62624ee98',
  ),
  const _SkipTest(
    'test_data/snapshot_test_data/div-container/wrap/horizontal-orientation-wrap-content-constrained.json',
    _Reason.crashesOnRenderFlexOverflow,
    'https://paste.yandex-team.ru/4ba25713-9819-4603-b882-66c6229b2c68',
  ),
  const _SkipTest(
    'test_data/snapshot_test_data/div-container/wrap/vertical-orientation-match-parent-cross.json',
    _Reason.crashesOnRenderFlexLayout,
    'https://paste.yandex-team.ru/d5043446-c4e5-4023-bd5c-edad95365fb4',
  ),
  const _SkipTest(
    'test_data/snapshot_test_data/div-container/wrap/vertical-orientation-wrap-content-constrained.json',
    _Reason.crashesOnRenderFlexOverflow,
    'https://paste.yandex-team.ru/d9c38ec6-a59c-48ae-a825-6c12f8e08314',
  ),
  const _SkipTest(
    'test_data/snapshot_test_data/div-container/wrap/horizontal-orientation-paddings.json',
    _Reason.crashesOnRenderFlexOverflow,
    'https://paste.yandex-team.ru/e46599f4-0fdd-434c-b8cc-8f4ccd455284',
  ),
  const _SkipTest(
    'test_data/snapshot_test_data/div-container/wrap/horizontal-orientation-with-wrap-content-constrained-children.json',
    _Reason.crashesOnRenderFlexOverflow,
    'https://paste.yandex-team.ru/1ded4255-bb11-4ca8-b43b-b31d4daaf098',
  ),
  const _SkipTest(
    'test_data/snapshot_test_data/div-container/wrap/horizontal-orientation-wrap-content.json',
    _Reason.crashesOnRenderFlexOverflow,
    'https://paste.yandex-team.ru/7963e7be-08d4-4525-9c71-e9fa8749134c',
  ),
  const _SkipTest(
    'test_data/snapshot_test_data/div-container/wrap/horizontal-orientation-wrap-content.json',
    _Reason.crashesOnRenderFlexOverflow,
    'https://paste.yandex-team.ru/7963e7be-08d4-4525-9c71-e9fa8749134c',
  ),
  const _SkipTest(
    'test_data/snapshot_test_data/div-container/wrap/vertical-orientation-paddings.json',
    _Reason.crashesOnRenderFlexOverflow,
    'https://paste.yandex-team.ru/e454bb3b-fec6-4b78-8a92-498f1ff9747c',
  ),
  const _SkipTest(
    'test_data/snapshot_test_data/div-container/wrap/vertical-orientation-with-wrap-content-constrained-children.json',
    _Reason.crashesOnRenderFlexOverflow,
    'https://paste.yandex-team.ru/4f2ca615-af39-4763-a508-d8398e99bc5f',
  ),
  const _SkipTest(
    'test_data/snapshot_test_data/div-container/wrap/vertical-orientation-wrap-content.json',
    _Reason.crashesOnRenderFlexOverflow,
    'https://paste.yandex-team.ru/62ddb122-ef5f-4ed3-ab21-998b44e03d7d',
  ),

// # div-container/item_builder

  ...[
    'test_data/snapshot_test_data/div-container/item_builder/item-builder.json',
    'test_data/snapshot_test_data/div-container/item_builder/non-unique-matched-selectors.json',
    'test_data/snapshot_test_data/div-container/item_builder/nested-builders.json',
    'test_data/snapshot_test_data/div-container/item_builder/index.json',
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.divNotSupported,
      'item builder is not supported yet',
    ),
  ),

// # div-container

  const _SkipTest(
    'test_data/snapshot_test_data/div-container/clip-to-bounds-with-shadows.json',
    _Reason.divNotSupported,
  ),

  ...[
    'test_data/snapshot_test_data/div-container/overlap-orientation-custom-alignments-bc.json',
    'test_data/snapshot_test_data/div-container/overlap-orientation-custom-alignments-bl.json',
    'test_data/snapshot_test_data/div-container/overlap-orientation-custom-alignments-br.json',
    'test_data/snapshot_test_data/div-container/overlap-orientation-custom-alignments-cc.json',
    'test_data/snapshot_test_data/div-container/overlap-orientation-custom-alignments-cl.json',
    'test_data/snapshot_test_data/div-container/overlap-orientation-custom-alignments-cr.json',
    'test_data/snapshot_test_data/div-container/overlap-orientation-custom-alignments-tc.json',
    'test_data/snapshot_test_data/div-container/overlap-orientation-custom-alignments-tl.json',
    'test_data/snapshot_test_data/div-container/overlap-orientation-custom-alignments-tr.json',
    'test_data/snapshot_test_data/div-container/horizontal-orientation-alignments.json',
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.unknown,
      'Test cases use unsupported text symbols',
    ),
  ),

  ...[
    'test_data/snapshot_test_data/div-container/aspect/wrap_content-width-vertical.json',
    'test_data/snapshot_test_data/div-container/aspect/wrap_content-width-vertical-match_parent-child-fits.json',
    'test_data/snapshot_test_data/div-container/known-match-parent-cross-axis.json',
    'test_data/snapshot_test_data/div-container/unknown-match-parent-cross-axis.json',
    'test_data/snapshot_test_data/div-text/auto-ellipsize-by-container-size.json',
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.unknown,
      'Current behavior not matches target',
    ),
  ),

  ...[
    'test_data/snapshot_test_data/div-container/horizontal-orientation-constrained-not-fit.json',
    'test_data/snapshot_test_data/div-container/horizontal-orientation-match-parent-not-fit-with-margin.json',
    'test_data/snapshot_test_data/div-container/horizontal-orientation-match-parent-not-fit-with-padding.json',
    'test_data/snapshot_test_data/div-container/horizontal-orientation-match-parent-not-fit.json',
    'test_data/snapshot_test_data/div-container/horizontal-orientation-paddings.json',
    'test_data/snapshot_test_data/div-container/horizontal-orientation-with-wrap-content-constrained-children.json',
    'test_data/snapshot_test_data/div-container/overlap-orientation-paddings.json',
    'test_data/snapshot_test_data/div-container/overlap-orientation-with-wrap-content-constrained-children.json',
    'test_data/snapshot_test_data/div-container/vertical-orientation-paddings.json',
    'test_data/snapshot_test_data/div-container/vertical-orientation-with-wrap-content-constrained-children.json',
    'test_data/snapshot_test_data/div-container/container_with_wrap_content_constrained_child.json',
    'test_data/snapshot_test_data/div-container/vertical-orientation-contsrained-not-fit.json',
    'test_data/snapshot_test_data/div-container/vertical-orientation-match-parent-not-fit-with-margin.json',
    'test_data/snapshot_test_data/div-container/vertical-orientation-match-parent-not-fit-with-padding.json',
    'test_data/snapshot_test_data/div-container/vertical-orientation-match-parent-not-fit.json',
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.crashesOnRenderFlexOverflow,
      'Overflow should be normal behavior for these cases',
    ),
  ),
  ...[
    'test_data/snapshot_test_data/div-container/horizontal-match-parent-inside-wrap-content-on-both-axis.json',
    'test_data/snapshot_test_data/div-container/match-parent-inside-wrap-content.json',
    'test_data/snapshot_test_data/div-container/overlap-match-parent-inside-wrap-content-on-both-axis.json',
    'test_data/snapshot_test_data/div-container/vertical-match-parent-inside-wrap-content-on-both-axis.json',
    'test_data/snapshot_test_data/div-container/wrap/vertical-orientation-match-parent-inside-wrap-content.json',
    'test_data/snapshot_test_data/div-container/horizontal-match-parent-inside-wrap-content-on-cross-axis.json',
    'test_data/snapshot_test_data/div-container/vertical-match-parent-inside-wrap-content-on-cross-axis.json',
    'test_data/snapshot_test_data/div-container/wrap/horizontal-orientation-match-parent-inside-wrap-content.json',
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.unknown,
      'match_parent inside wrap_content should transform into the wrap_content with constrained',
    ),
  ),

  ...[
    'test_data/snapshot_test_data/div-container/overlap-container-alignment.json',
    'test_data/snapshot_test_data/div-container/overlap-orientation-default-alignments.json',
    'test_data/snapshot_test_data/div-container/vertical-orientation-alignments.json',
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.crashesOnRenderFlexOverflow,
      'Device size is less than image',
    ),
  ),

// # div-gallery

  ...[
    'test_data/snapshot_test_data/div-gallery/corners_radius.json',
    'test_data/snapshot_test_data/div-gallery/horizontal-gallery-content-alignment.json',
    'test_data/snapshot_test_data/div-gallery/horizontal-gallery-cross-spacing.json',
    'test_data/snapshot_test_data/div-gallery/horizontal-gallery-custom-item-spacing.json',
    'test_data/snapshot_test_data/div-gallery/horizontal-gallery-custom-margins.json',
    'test_data/snapshot_test_data/div-gallery/horizontal-gallery-custom-paddings.json',
    'test_data/snapshot_test_data/div-gallery/horizontal-gallery-fixed-width.json',
    'test_data/snapshot_test_data/div-gallery/horizontal-gallery-horizontal-shrinking.json',
    'test_data/snapshot_test_data/div-gallery/horizontal-gallery-resizable-width.json',
    'test_data/snapshot_test_data/div-gallery/horizontal-grid-gallery-item-spacing.json',
    'test_data/snapshot_test_data/div-gallery/horizontal-grid-gallery-padding.json',
    'test_data/snapshot_test_data/div-gallery/vertical-gallery-content-alignment.json',
    'test_data/snapshot_test_data/div-gallery/vertical-gallery-cross-spacing.json',
    'test_data/snapshot_test_data/div-gallery/vertical-gallery-custom-item-spacing.json',
    'test_data/snapshot_test_data/div-gallery/vertical-gallery-custom-margins.json',
    'test_data/snapshot_test_data/div-gallery/vertical-gallery-custom-paddings.json',
    'test_data/snapshot_test_data/div-gallery/vertical-gallery-fixed-width.json',
    'test_data/snapshot_test_data/div-gallery/vertical-gallery-horizontal-shrinking.json',
    'test_data/snapshot_test_data/div-gallery/vertical-gallery-resizable-width.json',
    'test_data/snapshot_test_data/div-gallery/vertical-grid-gallery-item-spacing.json',
    'test_data/snapshot_test_data/div-gallery/vertical-grid-gallery-padding.json',
    'test_data/snapshot_test_data/div-gallery/empty.json',
    'test_data/snapshot_test_data/div-gallery/gallery-default-item-position.json',
    'test_data/snapshot_test_data/div-gallery/gallery-default-item-with-states.json',
    'test_data/snapshot_test_data/div-gallery/horizontal-gallery-default-item.json',
    'test_data/snapshot_test_data/div-gallery/horizontal-gallery-fixed-height.json',
    'test_data/snapshot_test_data/div-gallery/horizontal-gallery-items-not-fit.json',
    'test_data/snapshot_test_data/div-gallery/horizontal-gallery-items-resizable-height.json',
    'test_data/snapshot_test_data/div-gallery/horizontal-gallery-resizable-height.json',
    'test_data/snapshot_test_data/div-gallery/horizontal-gallery-scrollbar.json',
    'test_data/snapshot_test_data/div-gallery/horizontal-gallery-vertical-shrinking.json',
    'test_data/snapshot_test_data/div-gallery/horizontal-gallery-with-columns.json',
    'test_data/snapshot_test_data/div-gallery/vertical-gallery-fixed-height.json',
    'test_data/snapshot_test_data/div-gallery/vertical-gallery-items-not-fit.json',
    'test_data/snapshot_test_data/div-gallery/vertical-gallery-items-resizable-height.json',
    'test_data/snapshot_test_data/div-gallery/vertical-gallery-resizable-height.json',
    'test_data/snapshot_test_data/div-gallery/vertical-gallery-scrollbar.json',
    'test_data/snapshot_test_data/div-gallery/vertical-gallery-with-columns.json',
    'test_data/snapshot_test_data/div-gallery/vertical-grid-gallery-wrapped-item-spacing.json',
    'test_data/snapshot_test_data/div-gallery/item-builder/item-builder.json',
    'test_data/snapshot_test_data/div-gallery/item-builder/nested-builders.json',
    'test_data/snapshot_test_data/div-gallery/item-builder/non-unique-matched-selectors.json',
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.unknown,
    ),
  ),

// # div-gif-image

  ...[
    'test_data/snapshot_test_data/div-gif-image/placeholder-color.json',
    'test_data/snapshot_test_data/div-gif-image/preview.json',
    'test_data/snapshot_test_data/div-gif-image/aspect-fixed.json',
    'test_data/snapshot_test_data/div-gif-image/aspect-match_parent.json',
    'test_data/snapshot_test_data/div-gif-image/aspect-wrap_content.json',
    'test_data/snapshot_test_data/div-gif-image/border-with-stroke.json',
    'test_data/snapshot_test_data/div-gif-image/corner-radius.json',
    'test_data/snapshot_test_data/div-gif-image/corners_radius.json',
    'test_data/snapshot_test_data/div-gif-image/custom-alpha.json',
    'test_data/snapshot_test_data/div-gif-image/custom-height.json',
    'test_data/snapshot_test_data/div-gif-image/custom-margins.json',
    'test_data/snapshot_test_data/div-gif-image/custom-paddings.json',
    'test_data/snapshot_test_data/div-gif-image/custom-width.json',
    'test_data/snapshot_test_data/div-gif-image/no_scale.json',
    'test_data/snapshot_test_data/div-gif-image/no_scale_bottom_right.json',
    'test_data/snapshot_test_data/div-gif-image/no_scale_top_left.json',
    'test_data/snapshot_test_data/div-gif-image/scale_fill.json',
    'test_data/snapshot_test_data/div-gif-image/scale_fill_bottom.json',
    'test_data/snapshot_test_data/div-gif-image/scale_fill_right.json',
    'test_data/snapshot_test_data/div-gif-image/scale_fit.json',
    'test_data/snapshot_test_data/div-gif-image/scale_fit_bottom.json',
    'test_data/snapshot_test_data/div-gif-image/scale_fit_left.json',
    'test_data/snapshot_test_data/div-gif-image/scale_fit_right.json',
    'test_data/snapshot_test_data/div-gif-image/scale_fit_top.json',
    'test_data/snapshot_test_data/div-gif-image/scale_stretch.json',
    'test_data/snapshot_test_data/div-gif-image/shadow.json',
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.divNotSupported,
    ),
  ),

// # div-grid

  ...[
    'test_data/snapshot_test_data/div-grid/empty.json',
    'test_data/snapshot_test_data/div-grid/non-rectangular-grid.json',
    'test_data/snapshot_test_data/div-grid/non-rectangular-spanned-grid.json',
    'test_data/snapshot_test_data/div-grid/all-fixed.json',
    'test_data/snapshot_test_data/div-grid/ambiguous-span.json',
    'test_data/snapshot_test_data/div-grid/content-not-fit-with-paddings.json',
    'test_data/snapshot_test_data/div-grid/corners_radius.json',
    'test_data/snapshot_test_data/div-grid/examples/example_1.json',
    'test_data/snapshot_test_data/div-grid/examples/example_2.json',
    'test_data/snapshot_test_data/div-grid/examples/example_3.json',
    'test_data/snapshot_test_data/div-grid/examples/example_4.json',
    'test_data/snapshot_test_data/div-grid/examples/example_5.json',
    'test_data/snapshot_test_data/div-grid/examples/example_6.json',
    'test_data/snapshot_test_data/div-grid/examples/example_7.json',
    'test_data/snapshot_test_data/div-grid/fixed-size-with-paddings.json',
    'test_data/snapshot_test_data/div-grid/fixed-width-distribution.json',
    'test_data/snapshot_test_data/div-grid/intersected-fixed-spans.json',
    'test_data/snapshot_test_data/div-grid/intrinsic-width.json',
    'test_data/snapshot_test_data/div-grid/item-alignments.json',
    'test_data/snapshot_test_data/div-grid/resizable-height.json',
    'test_data/snapshot_test_data/div-grid/resizable-width.json',
    'test_data/snapshot_test_data/div-grid/spans.json',
    'test_data/snapshot_test_data/div-grid/weighted-calculation.json',
    'test_data/snapshot_test_data/div-grid/weighted-greater-than-fixed.json',
    'test_data/snapshot_test_data/div-grid/weighted-less-than-fixed.json',
    'test_data/snapshot_test_data/div-grid/weighted-plus-match-parent.json',
    'test_data/snapshot_test_data/div-grid/without-weight.json',
    'test_data/snapshot_test_data/div-grid/wrap-content-constrained-greater-than-parent.json',
    'test_data/snapshot_test_data/div-grid/wrap-content-constrained-height.json',
    'test_data/snapshot_test_data/div-grid/wrap-content-constrained-items.json',
    'test_data/snapshot_test_data/div-grid/wrap-content-items.json',
    'test_data/snapshot_test_data/div-grid/wrap-content-weighted-items.json',
    'test_data/snapshot_test_data/div-grid/content-alignment.json',
    'test_data/snapshot_test_data/div-grid/wrap-content-greater-than-parent.json',
    'test_data/snapshot_test_data/div-grid/match-parent-items-with-margins.json',
    'test_data/snapshot_test_data/div-grid/overlapped-items.json',
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.divNotSupported,
    ),
  ),

// # div-image

  ...[
    'test_data/snapshot_test_data/div-image/custom-preview.json',
    'test_data/snapshot_test_data/div-image/placeholder-color.json',
    'test_data/snapshot_test_data/div-image/preview.json',
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.haveToLoadNetworkImage,
    ),
  ),

  ...[
    'test_data/snapshot_test_data/div-image/tint-mode.json',
    'test_data/snapshot_test_data/div-image/blur-with-big-radius.json'
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.invalidImageDimensions,
    ),
  ),

// # div-indicator

  ...[
    'test_data/snapshot_test_data/div-indicator/stroke.json',
    'test_data/snapshot_test_data/div-indicator/active_size.json',
    'test_data/snapshot_test_data/div-indicator/corners_radius.json',
    'test_data/snapshot_test_data/div-indicator/fixed-height.json',
    'test_data/snapshot_test_data/div-indicator/fixed-width-max_items_circle.json',
    'test_data/snapshot_test_data/div-indicator/fixed-width-max_items_rectangle.json',
    'test_data/snapshot_test_data/div-indicator/fixed-width-max_items_rectangle_slider.json',
    'test_data/snapshot_test_data/div-indicator/fixed-width-max_items_rectangle_worm.json',
    'test_data/snapshot_test_data/div-indicator/margins.json',
    'test_data/snapshot_test_data/div-indicator/match_parent-width-max_items.json',
    'test_data/snapshot_test_data/div-indicator/minimum_size.json',
    'test_data/snapshot_test_data/div-indicator/paddings.json',
    'test_data/snapshot_test_data/div-indicator/shapes.json',
    'test_data/snapshot_test_data/div-indicator/stretch_items.json',
    'test_data/snapshot_test_data/div-indicator/stretch_items_max_items_constraint.json',
    'test_data/snapshot_test_data/div-indicator/wrap_content-height.json',
    'test_data/snapshot_test_data/div-indicator/wrap_content-width-max_items.json',
    'test_data/snapshot_test_data/div-indicator/rtl_div_indicator.json',
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.divNotSupported,
    ),
  ),

// # div-pager

  ...[
    'test_data/snapshot_test_data/div-pager/corners_radius.json',
    'test_data/snapshot_test_data/div-pager/horizontal-pager-constrained-height-with-paddings.json',
    'test_data/snapshot_test_data/div-pager/horizontal-pager-custom-item-spacing.json',
    'test_data/snapshot_test_data/div-pager/horizontal-pager-custom-neighbour-page-width-with-paddings.json',
    'test_data/snapshot_test_data/div-pager/horizontal-pager-custom-neighbour-page-width.json',
    'test_data/snapshot_test_data/div-pager/horizontal-pager-custom-page-width-with-paddings.json',
    'test_data/snapshot_test_data/div-pager/horizontal-pager-custom-page-width.json',
    'test_data/snapshot_test_data/div-pager/horizontal-pager-fixed-height-with-paddings.json',
    'test_data/snapshot_test_data/div-pager/horizontal-pager-fixed-width.json',
    'test_data/snapshot_test_data/div-pager/horizontal-pager-same-paddings.json',
    'test_data/snapshot_test_data/div-pager/vertical-pager-fixed-width.json',
    'test_data/snapshot_test_data/div-pager/vertical-pager-match-parent-width-with-paddings.json',
    'test_data/snapshot_test_data/div-pager/horizontal-pager-fixed-height.json',
    'test_data/snapshot_test_data/div-pager/horizontal-pager-resizable-height.json',
    'test_data/snapshot_test_data/div-pager/horizontal-pager-resizable-width.json',
    'test_data/snapshot_test_data/div-pager/horizontal-pager-wrap-content-height-with-paddings.json',
    'test_data/snapshot_test_data/div-pager/pager-default-item-with-states.json',
    'test_data/snapshot_test_data/div-pager/rtl-pager.json',
    'test_data/snapshot_test_data/div-pager/vertical-pager-fixed-width-with-paddings.json',
    'test_data/snapshot_test_data/div-pager/vertical-pager-resizable-width.json',
    'test_data/snapshot_test_data/div-pager/item-builder/item-builder.json',
    'test_data/snapshot_test_data/div-pager/item-builder/non-unique-matched-selectors.json',
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.divNotSupported,
    ),
  ),

// # div-radial-gradient

  ...[
    'test_data/snapshot_test_data/div-radial-gradient/fixed-center-negative.json',
    'test_data/snapshot_test_data/div-radial-gradient/fixed-center-positive.json',
    'test_data/snapshot_test_data/div-radial-gradient/relative-center-positive.json',
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.crashesOnRenderFlexOverflow,
      'https://paste.yandex-team.ru/25bfbf31-e08b-46a7-b372-31c45909d5e5',
    ),
  ),

  ...[
    'test_data/snapshot_test_data/div-radial-gradient/relative-radius-farthest-corner.json',
    'test_data/snapshot_test_data/div-radial-gradient/relative-radius-farthest-side.json',
    'test_data/snapshot_test_data/div-radial-gradient/relative-radius-nearest-corner.json',
    'test_data/snapshot_test_data/div-radial-gradient/relative-radius-nearest-side.json',
    'test_data/snapshot_test_data/div-radial-gradient/zero-radius.json',
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.crashesOnImageDimensions,
      'https://paste.yandex-team.ru/b3cb8900-6dea-49c8-9460-605bb8188e96',
    ),
  ),

// # div-select

  ...[
    'test_data/snapshot_test_data/div-select/all-attributes.json',
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.divNotSupported,
    ),
  ),

// # div-separator

  ...[
    'test_data/snapshot_test_data/div-separator/big_corner_radius.json',
    'test_data/snapshot_test_data/div-separator/custom-color.json',
    'test_data/snapshot_test_data/div-separator/custom-paddings.json',
    'test_data/snapshot_test_data/div-separator/default-values.json',
    'test_data/snapshot_test_data/div-separator/fixed-size-with-paddings.json',
    'test_data/snapshot_test_data/div-separator/horizontal-fixed-height.json',
    'test_data/snapshot_test_data/div-separator/horizontal-fixed-width.json',
    'test_data/snapshot_test_data/div-separator/vertical-fixed-height.json',
    'test_data/snapshot_test_data/div-separator/vertical-fixed-width.json',
    'test_data/snapshot_test_data/div-separator/vertical-intrinsic-width.json',
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.divNotSupported,
    ),
  ),

// # div-size

  ...[
    'test_data/snapshot_test_data/div-size/wrap-content/constrained-true-margins-and-paddings-horizontal.json',
    'test_data/snapshot_test_data/div-size/wrap-content/constrained-true-margins-and-paddings-overlap.json',
    'test_data/snapshot_test_data/div-size/wrap-content/constrained-true-margins-and-paddings-vertical.json',
    'test_data/snapshot_test_data/div-size/wrap-content/constrained-true-margins-and-paddings-wrap-horizontal.json',
    'test_data/snapshot_test_data/div-size/wrap-content/constrained-true-margins-and-paddings-wrap-vertical.json',
    'test_data/snapshot_test_data/div-size/wrap-content/min-max-constrained-true-horizontal-weigted.json',
    'test_data/snapshot_test_data/div-size/wrap-content/min-max-constrained-true-horizontal.json',
    'test_data/snapshot_test_data/div-size/wrap-content/min-max-constrained-true-overlap.json',
    'test_data/snapshot_test_data/div-size/wrap-content/min-max-constrained-true-vertical-weighted.json',
    'test_data/snapshot_test_data/div-size/wrap-content/min-max-constrained-true-vertical.json',
    'test_data/snapshot_test_data/div-size/wrap-content/min-max-constrained-true-wrap-horizontal.json',
    'test_data/snapshot_test_data/div-size/wrap-content/min-max-constrained-true-wrap-vertical.json',
    'test_data/snapshot_test_data/div-size/wrap-content/min-max-constrained-false-horizontal.json',
    'test_data/snapshot_test_data/div-size/wrap-content/min-max-constrained-false-overlap.json',
    'test_data/snapshot_test_data/div-size/wrap-content/min-max-constrained-false-vertical.json',
    'test_data/snapshot_test_data/div-size/wrap-content/min-max-constrained-false-wrap-horizontal.json',
    'test_data/snapshot_test_data/div-size/wrap-content/min-max-constrained-false-wrap-vertical.json',
    'test_data/snapshot_test_data/div-size/wrap-content/text-min-max-constrained-false-horizontal.json',
    'test_data/snapshot_test_data/div-size/wrap-content/text-min-max-constrained-false-overlap.json',
    'test_data/snapshot_test_data/div-size/padding/padding-bigger-than-box.json',
    'test_data/snapshot_test_data/div-size/padding/padding-smaller-than-box.json',
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.unknown,
      'Current behavior not matches target',
    ),
  ),

// # div-slider

  ...[
    'test_data/snapshot_test_data/div-slider/base.json',
    'test_data/snapshot_test_data/div-slider/custom_active_track.json',
    'test_data/snapshot_test_data/div-slider/custom_inactive_track.json',
    'test_data/snapshot_test_data/div-slider/custom_text_bottom.json',
    'test_data/snapshot_test_data/div-slider/custom_text_left.json',
    'test_data/snapshot_test_data/div-slider/custom_text_left_top.json',
    'test_data/snapshot_test_data/div-slider/custom_text_right.json',
    'test_data/snapshot_test_data/div-slider/custom_text_top.json',
    'test_data/snapshot_test_data/div-slider/custom_text_top_bottom.json',
    'test_data/snapshot_test_data/div-slider/custom_thumb.json',
    'test_data/snapshot_test_data/div-slider/custom_thumb_secondary.json',
    'test_data/snapshot_test_data/div-slider/custom_thumb_text.json',
    'test_data/snapshot_test_data/div-slider/custom_thumb_text_secondary.json',
    'test_data/snapshot_test_data/div-slider/custom_tick_marks.json',
    'test_data/snapshot_test_data/div-slider/fixed_size.json',
    'test_data/snapshot_test_data/div-slider/match_parent_size.json',
    'test_data/snapshot_test_data/div-slider/secondary_thumb_on_left.json',
    'test_data/snapshot_test_data/div-slider/shapes_tick.json',
    'test_data/snapshot_test_data/div-slider/slider_nps.json',
    'test_data/snapshot_test_data/div-slider/with_active_tick_marks.json',
    'test_data/snapshot_test_data/div-slider/with_inactive_tick_marks.json',
    'test_data/snapshot_test_data/div-slider/with_paddings.json',
    'test_data/snapshot_test_data/div-slider/with_stroke.json',
    'test_data/snapshot_test_data/div-slider/wrap_content_size.json',
    'test_data/snapshot_test_data/div-slider/ranges.json',
    'test_data/snapshot_test_data/div-slider/rtl_slider.json',
    'test_data/snapshot_test_data/div-slider/shapes_thumb.json',
    'test_data/snapshot_test_data/div-slider/slider_with_error_and_warning.json',
    'test_data/snapshot_test_data/div-slider/slider_with_warning.json',
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.divNotSupported,
    ),
  ),

// # div-tabs

  ...[
    'test_data/snapshot_test_data/div-tabs/title-delimiters.json',
    'test_data/snapshot_test_data/div-tabs/too-big-constrained-tabs.json',
    'test_data/snapshot_test_data/div-tabs/spaces-in-title.json',
    'test_data/snapshot_test_data/div-tabs/corners_radius.json',
    'test_data/snapshot_test_data/div-tabs/custom-background-border.json',
    'test_data/snapshot_test_data/div-tabs/custom-margins.json',
    'test_data/snapshot_test_data/div-tabs/custom-paddings.json',
    'test_data/snapshot_test_data/div-tabs/custom-title-style.json',
    'test_data/snapshot_test_data/div-tabs/different-child-height.json',
    'test_data/snapshot_test_data/div-tabs/fixed-height.json',
    'test_data/snapshot_test_data/div-tabs/fixed-width.json',
    'test_data/snapshot_test_data/div-tabs/match-parent-pages.json',
    'test_data/snapshot_test_data/div-tabs/selected-tab-above-range.json',
    'test_data/snapshot_test_data/div-tabs/selected-tab-broken-content.json',
    'test_data/snapshot_test_data/div-tabs/selected-tab.json',
    'test_data/snapshot_test_data/div-tabs/separator-paddings.json',
    'test_data/snapshot_test_data/div-tabs/separator-resizing.json',
    'test_data/snapshot_test_data/div-tabs/tabs-dynamic-height.json',
    'test_data/snapshot_test_data/div-tabs/tabs-max-height.json',
    'test_data/snapshot_test_data/div-tabs/too-big-constrained-height-with-paddings.json',
    'test_data/snapshot_test_data/div-tabs/wrap_content-width.json',
    'test_data/snapshot_test_data/div-tabs/match-parent-height-with-paddings.json',
    'test_data/snapshot_test_data/div-tabs/rtl-tabs.json',
    'test_data/snapshot_test_data/div-tabs/tabs-and-gallery.json',
    'test_data/snapshot_test_data/div-tabs/title-with-expression.json',
    'test_data/snapshot_test_data/div-tabs/too-big-fixed-height-with-paddings.json',
    'test_data/snapshot_test_data/div-tabs/too-big-wrap-content-height-with-paddings.json',
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.divNotSupported,
    ),
  ),

  const _SkipTest(
    'test_data/snapshot_test_data/div-container/match-parent-with-margins.json',
    _Reason.unknown,
    'Not supported by platform',
  ),

  const _SkipTest(
    'test_data/snapshot_test_data/div-text/hyphenation.json',
    _Reason.unknown,
    'Not supported by platform',
  ),

  const _SkipTest(
    'test_data/snapshot_test_data/div-input/with-native-interface-no-background.json',
    _Reason.unknown,
    'Not supported by platform',
  ),

  ...[
    'test_data/snapshot_test_data/div-text/ranges-line-height-top-offset.json',
    'test_data/snapshot_test_data/div-text/ranges.json',
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.crashesOnRenderFlexOverflow,
      'Overflow in wrap content size',
    ),
  ),

  ...[
    'test_data/snapshot_test_data/div-text/ranges-line-height-top-offset.json',
    'test_data/snapshot_test_data/div-text/ranges-background-text.json',
    'test_data/snapshot_test_data/div-text/gradient-color-with-ranges.json',
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.divNotSupported,
      'Not supported by platform',
    ),
  ),

  const _SkipTest(
    'test_data/snapshot_test_data/div-text/text-shadow-alpha.json',
    _Reason.divNotSupported,
    'https://paste.yandex-team.ru/4132d91f-115f-481a-a581-97af4242af76',
  ),
  const _SkipTest(
    'test_data/snapshot_test_data/div-text/text-shadow.json',
    _Reason.divNotSupported,
    'https://paste.yandex-team.ru/4132d91f-115f-481a-a581-97af4242af76',
  ),
  const _SkipTest(
    'test_data/snapshot_test_data/div-text/blur-background.json',
    _Reason.invalidImageDimensions,
    'https://paste.yandex-team.ru/3e965301-f3ac-4ff3-ba62-c81665108399',
  ),
  const _SkipTest(
    'test_data/snapshot_test_data/div-text/images.json',
    _Reason.crashesOnRenderFlexLayout,
    'https://paste.yandex-team.ru/66b81483-d996-4a76-9b50-0fa88924b0a1',
  ),
  const _SkipTest(
    'test_data/snapshot_test_data/div-text/variables-rendering.json',
    _Reason.unknown,
    'Native calls are not supported, can be fixed after dart expression resolver',
  ),

  const _SkipTest(
    'test_data/snapshot_test_data/div-text/text_image_vertical_alignment.json',
    _Reason.unknown,
    'Not supported by platform now',
  ),

// # div-transform

  ...[
    'test_data/snapshot_test_data/div-transform/transform_container.json',
    'test_data/snapshot_test_data/div-transform/transform_container_pisa.json',
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.divNotSupported,
    ),
  ),
  _SkipTest(
    'test_data/snapshot_test_data/div-text/custom-image-tint-color.json',
    _Reason.divNotSupported,
    'https://a.yandex-team.ru/review/6756853/files/5#comment-9971090',
  ),
  _SkipTest(
    'test_data/snapshot_test_data/div-text/custom-text-alignment-with-attachment.json',
    _Reason.divNotSupported,
    'https://a.yandex-team.ru/review/6756853/files/5#comment-9971092',
  ),
  _SkipTest(
    'test_data/snapshot_test_data/div-text/ellipsis-with-image.json',
    _Reason.divNotSupported,
    'https://a.yandex-team.ru/review/6756853/details#comment-9971095',
  ),
  _SkipTest(
    'test_data/snapshot_test_data/div-text/all_attributes.json',
    _Reason.divNotSupported,
    'https://a.yandex-team.ru/review/6756853/details#comment-9971087',
  ),

// # div-svg

  ...[
    'test_data/snapshot_test_data/div-svg/svg_background.json',
    'test_data/snapshot_test_data/div-svg/svg_image_effects.json',
    'test_data/snapshot_test_data/div-svg/svg_images.json',
    'test_data/snapshot_test_data/div-svg/svg_images_invalid_values.json',
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.svgImage,
      'https://paste.yandex-team.ru/db093d74-d606-40ab-9649-94ec071e12eb',
    ),
  ),

// # div-video
  ...[
    'test_data/snapshot_test_data/div-video/video-preview-scale-fill.json',
    'test_data/snapshot_test_data/div-video/video-preview-scale-fit.json',
    'test_data/snapshot_test_data/div-video/video-preview-scale-no-scale.json'
  ].map(
    (e) => _SkipTest(
      e,
      _Reason.divNotSupported,
    ),
  ),
];
