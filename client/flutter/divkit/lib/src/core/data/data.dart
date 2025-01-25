import 'package:divkit/divkit.dart';
import 'package:divkit/src/utils/trace.dart';
import 'package:equatable/equatable.dart';

@Deprecated('Use DefaultDivKitData')
typedef DefaultLazyDivKitData = DefaultDivKitData;

class DefaultDivKitData extends DivKitData with EquatableMixin {
  final Map<String, dynamic> _card;
  final Map<String, dynamic>? _templates;

  DefaultDivKitData.fromJson(
    Map<String, dynamic> json,
  )   : _card = json['card']!,
        _templates = json['templates'];

  DefaultDivKitData.fromScheme({
    required Map<String, dynamic> card,
    Map<String, dynamic>? templates,
  })  : _card = card,
        _templates = templates;

  DivData? _source;

  @override
  DivData? get source => _source;

  @override
  bool get hasSource => _source != null;

  @override
  DivKitData build() {
    _source ??= traceFunc(
      'DivData.fromJson',
      () => DivData.fromJson(
        traceFunc(
          'TemplatesResolver.merge',
          () => TemplatesResolver(
            layout: _card,
            templates: _templates,
          ).merge(),
        ),
      ),
    );
    return this;
  }

  @override
  List<Object?> get props => [_source];
}
