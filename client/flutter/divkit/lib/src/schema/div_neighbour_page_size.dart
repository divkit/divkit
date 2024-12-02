// Generated code. Do not modify.

import 'package:divkit/src/schema/div_fixed_size.dart';
import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Fixed width value of the visible part of a neighbouring page.
class DivNeighbourPageSize extends Resolvable with EquatableMixin {
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
        neighbourPageWidth: reqProp<DivFixedSize>(
          safeParseObject(
            json['neighbour_page_width'],
            parse: DivFixedSize.fromJson,
          ),
          name: 'neighbour_page_width',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }

  @override
  DivNeighbourPageSize resolve(DivVariableContext context) {
    neighbourPageWidth.resolve(context);
    return this;
  }
}
