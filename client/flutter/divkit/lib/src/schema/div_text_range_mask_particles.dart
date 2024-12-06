// Generated code. Do not modify.

import 'package:divkit/src/schema/div_fixed_size.dart';
import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// A mask to hide text (spoiler) that looks like randomly distributed particles (telegram alike).
class DivTextRangeMaskParticles with EquatableMixin {
  const DivTextRangeMaskParticles({
    required this.color,
    this.density = const ValueExpression(0.8),
    this.isAnimated = const ValueExpression(false),
    this.isEnabled = const ValueExpression(true),
    this.particleSize = const DivFixedSize(
      value: ValueExpression(
        1,
      ),
    ),
  });

  static const type = "particles";

  /// Color of particles on the mask.
  final Expression<Color> color;

  /// Density of particles on the mask, interpreted as a probability of a particle to spawn in a given point on the mask.
  // default value: 0.8
  final Expression<double> density;

  /// Defines whether particles on the mask will be animated or not. Animation looks like smooth random particle movements (telegram alike).
  // default value: false
  final Expression<bool> isAnimated;

  /// Controls mask state: if set to `true` mask will hide specified part of the text, otherwise the text will be shown.
  // default value: true
  final Expression<bool> isEnabled;

  /// Size of a single particle on a mask.
  // default value: const DivFixedSize(value: ValueExpression(1,),)
  final DivFixedSize particleSize;

  @override
  List<Object?> get props => [
        color,
        density,
        isAnimated,
        isEnabled,
        particleSize,
      ];

  DivTextRangeMaskParticles copyWith({
    Expression<Color>? color,
    Expression<double>? density,
    Expression<bool>? isAnimated,
    Expression<bool>? isEnabled,
    DivFixedSize? particleSize,
  }) =>
      DivTextRangeMaskParticles(
        color: color ?? this.color,
        density: density ?? this.density,
        isAnimated: isAnimated ?? this.isAnimated,
        isEnabled: isEnabled ?? this.isEnabled,
        particleSize: particleSize ?? this.particleSize,
      );

  static DivTextRangeMaskParticles? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivTextRangeMaskParticles(
        color: reqVProp<Color>(
          safeParseColorExpr(
            json['color'],
          ),
          name: 'color',
        ),
        density: reqVProp<double>(
          safeParseDoubleExpr(
            json['density'],
            fallback: 0.8,
          ),
          name: 'density',
        ),
        isAnimated: reqVProp<bool>(
          safeParseBoolExpr(
            json['is_animated'],
            fallback: false,
          ),
          name: 'is_animated',
        ),
        isEnabled: reqVProp<bool>(
          safeParseBoolExpr(
            json['is_enabled'],
            fallback: true,
          ),
          name: 'is_enabled',
        ),
        particleSize: reqProp<DivFixedSize>(
          safeParseObject(
            json['particle_size'],
            parse: DivFixedSize.fromJson,
            fallback: const DivFixedSize(
              value: ValueExpression(
                1,
              ),
            ),
          ),
          name: 'particle_size',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}
