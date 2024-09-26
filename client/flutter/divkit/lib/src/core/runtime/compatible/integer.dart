export 'package:divkit/src/core/runtime/compatible/integer_native.dart'
    // This is not a full and incorrect support for a 64-bit integer for the Web,
    // so if we want to fully use the library, we need to replace int with BigInt in all
    // places. This has not been done, since the main target is for native platforms that
    // the 64-bit integer supports and work faster with it.
    if (dart.library.js) 'package:divkit/src/core/runtime/compatible/integer_web.dart';
