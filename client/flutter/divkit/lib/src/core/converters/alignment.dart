import 'package:divkit/divkit.dart';
import 'package:flutter/widgets.dart';

class DivAlignmentConverter {
  final Expression<DivAlignmentVertical?>? vertical;
  final Expression<DivAlignmentHorizontal?>? horizontal;

  const DivAlignmentConverter(this.vertical, this.horizontal);

  AlignmentGeometry? convert() {
    final vertical = this.vertical?.value;
    final horizontal = this.horizontal?.value;

    if (vertical != null && horizontal != null) {
      return vertical.map(
        top: () => horizontal.map(
          left: () => Alignment.topLeft,
          center: () => Alignment.topCenter,
          right: () => Alignment.topRight,
          start: () => AlignmentDirectional.topStart,
          end: () => AlignmentDirectional.topEnd,
        ),
        center: () => horizontal.map(
          left: () => Alignment.centerLeft,
          center: () => Alignment.center,
          right: () => Alignment.centerRight,
          start: () => AlignmentDirectional.centerStart,
          end: () => AlignmentDirectional.centerEnd,
        ),
        bottom: () => horizontal.map(
          left: () => Alignment.bottomLeft,
          center: () => Alignment.bottomCenter,
          right: () => Alignment.bottomRight,
          start: () => AlignmentDirectional.bottomStart,
          end: () => AlignmentDirectional.bottomEnd,
        ),
        // TODO: support baseline alignment in flutter
        baseline: () => horizontal.map(
          left: () => Alignment.centerLeft,
          center: () => Alignment.center,
          right: () => Alignment.centerRight,
          start: () => AlignmentDirectional.centerStart,
          end: () => AlignmentDirectional.centerEnd,
        ),
      );
    } else if (vertical != null) {
      return vertical.map(
        top: () => Alignment.topLeft,
        center: () => Alignment.centerLeft,
        bottom: () => Alignment.bottomLeft,
        // TODO: support baseline alignment in flutter
        baseline: () => Alignment.centerLeft,
      );
    } else if (horizontal != null) {
      return horizontal.map(
        left: () => Alignment.topLeft,
        center: () => Alignment.topCenter,
        right: () => Alignment.topRight,
        start: () => AlignmentDirectional.topStart,
        end: () => AlignmentDirectional.topEnd,
      );
    }
    return null;
  }
}

class DivTextAlignmentConverter {
  final Expression<DivAlignmentVertical> vertical;
  final Expression<DivAlignmentHorizontal> horizontal;

  const DivTextAlignmentConverter(this.vertical, this.horizontal);

  TextAlign convertHorizontal() {
    switch (horizontal.value) {
      case DivAlignmentHorizontal.left:
        return TextAlign.left;
      case DivAlignmentHorizontal.center:
        return TextAlign.center;
      case DivAlignmentHorizontal.right:
        return TextAlign.right;
      case DivAlignmentHorizontal.start:
        return TextAlign.start;
      case DivAlignmentHorizontal.end:
        return TextAlign.end;
    }
  }

  TextAlignVertical convertVertical() {
    switch (vertical.value) {
      case DivAlignmentVertical.top:
        return TextAlignVertical.top;
      case DivAlignmentVertical.center:
        return TextAlignVertical.center;
      case DivAlignmentVertical.bottom:
        return TextAlignVertical.bottom;
      // TODO: support baseline alignment in flutter
      case DivAlignmentVertical.baseline:
        return TextAlignVertical.center;
    }
  }
}
