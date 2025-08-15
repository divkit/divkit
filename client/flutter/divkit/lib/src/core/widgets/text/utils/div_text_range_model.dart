import 'package:divkit/divkit.dart';
import 'package:equatable/equatable.dart';
import 'package:flutter/widgets.dart';

class DivTextRangeModel with EquatableMixin {
  final String text;

  final DivTextRangeOptionModel optionModel;

  const DivTextRangeModel({
    required this.text,
    required this.optionModel,
  });

  @override
  List<Object?> get props => [
        text,
        optionModel,
      ];
}

class DivTextRangeOptionModel with EquatableMixin {
  final TextStyle? style;

  final List<DivActionModel> actions;

  final int? topOffset;

  const DivTextRangeOptionModel({
    this.style,
    this.actions = const [],
    this.topOffset,
  });

  @override
  List<Object?> get props => [
        style,
        actions,
        topOffset,
      ];

  bool get isDefault => this == const DivTextRangeOptionModel();

  static DivTextRangeOptionModel mergeAll(
    List<DivTextRangeOptionModel> listData,
  ) {
    DivTextRangeOptionModel mergedData = const DivTextRangeOptionModel();
    for (var item in listData) {
      mergedData = item._merge(
        mergedData,
      );
    }
    return mergedData;
  }

  DivTextRangeOptionModel _merge(DivTextRangeOptionModel model) =>
      DivTextRangeOptionModel(
        style: model.style?.merge(style) ?? style,
        actions: model.actions.isNotEmpty ? model.actions : actions,
        topOffset: model.topOffset ?? topOffset,
      );
}

class DivTextRangeInterval with EquatableMixin {
  final int start;
  final int end;

  const DivTextRangeInterval(
    this.start,
    this.end,
  );

  @override
  List<Object?> get props => [
        start,
        end,
      ];
}
