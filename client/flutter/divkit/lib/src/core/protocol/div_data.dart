import 'package:divkit/src/core/template/templates_resolver.dart';
import 'package:divkit/src/generated_sources/div_data.dart';
import 'package:equatable/equatable.dart';

abstract class DivKitData {
  /// The layout described by dto entities.
  DivData? get source;
}

class DefaultDivKitData extends DivKitData with EquatableMixin {
  @override
  final DivData? source;

  DefaultDivKitData.fromJson(
    Map<String, dynamic> json,
  ) : source = DivData.fromJson(
          TemplatesResolver.fromTemplates(
            layout: json['card']!,
            templates: json['templates'],
          ).merge().toGenericMap(),
        );

  DefaultDivKitData.fromScheme({
    required Map<String, dynamic> card,
    Map<String, dynamic>? templates,
  }) : source = DivData.fromJson(
          TemplatesResolver.fromTemplates(
            layout: card,
            templates: templates,
          ).merge().toGenericMap(),
        );

  @override
  List<Object?> get props => [source];
}

class DefaultLazyDivKitData extends DivKitData with EquatableMixin {
  DivData? _source;

  @override
  DivData? get source => _source ??= DivData.fromJson(
        TemplatesResolver.fromTemplates(
          layout: card,
          templates: templates,
        ).merge().toGenericMap(),
      );

  final Map<String, dynamic> card;
  final Map<String, dynamic>? templates;

  DefaultLazyDivKitData.fromJson(
    Map<String, dynamic> json,
  )   : card = json['card']!,
        templates = json['templates'];

  DefaultLazyDivKitData.fromScheme({
    required this.card,
    this.templates,
  });

  @override
  List<Object?> get props => [source];
}
