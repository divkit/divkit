import 'package:divkit/divkit.dart';
import 'package:flutter/services.dart';

class Box {
  final RootIsolateToken token;
  final List<String> data;
  final List<Map> metas;

  Box(this.data, this.metas) : token = ServicesBinding.rootIsolateToken!;
}

class Item {
  final String title;
  final Set<String> tags;
  final DivKitData src;

  const Item(this.title, this.src, this.tags);
}
