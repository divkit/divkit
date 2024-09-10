// Generated code. Do not modify.

import 'package:divkit/src/schema/div_fixed_size.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Fixed width value of the visible part of a neighbouring page.
class DivNeighbourPageSize extends Preloadable with EquatableMixin {
  const DivNeighbourPageSize({
    required this.neighbourPageWidth,
  });

  static const type = "fixed";

  /// Width of the visible part of a neighbouring page.
  final DivFixedSize neighbourPageWidth;

  @override
  List<Object?> get props => [
        neighbourPageWidth,
      ];

  DivNeighbourPageSize copyWith({
    DivFixedSize? neighbourPageWidth,
  }) =>
      DivNeighbourPageSize(
        neighbourPageWidth: neighbourPageWidth ?? this.neighbourPageWidth,
      );

  static DivNeighbourPageSize? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivNeighbourPageSize(
        neighbourPageWidth: safeParseObj(
          DivFixedSize.fromJson(json['neighbour_page_width']),
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivNeighbourPageSize?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivNeighbourPageSize(
        neighbourPageWidth: (await safeParseObjAsync(
          DivFixedSize.fromJson(json['neighbour_page_width']),
        ))!,
      );
    } catch (e) {
      return null;
    }
  }

  @override
  Future<void> preload(
    Map<String, dynamic> context,
  ) async {
    try {
      await neighbourPageWidth.preload(context);
    } catch (e) {
      return;
    }
  }
}
