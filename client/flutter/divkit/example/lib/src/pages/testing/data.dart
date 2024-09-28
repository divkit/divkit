import 'package:divkit/divkit.dart';

class Box {
  final List<String> data;
  final List<Map> metas;

  const Box(this.data, this.metas);
}

class Item {
  final String title;
  final Set<String> tags;
  final DivKitData src;

  const Item(this.title, this.src, this.tags);
}
