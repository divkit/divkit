import 'package:divkit/src/core/runtime/compatible/integer.dart';
import 'package:divkit/src/core/runtime/core.dart';

const baseMathFunctions = <String, RuntimeFunction>{
  'sum': sum,
  'sub': sub,
  'div': div,
  'mul': mul,
  'mod': mod,
  'abs': abs,
  'signum': signum,
  'copySign': copySign,
  'round': round,
  'floor': floor,
  'ceil': ceil,
  'maxInteger': maxInteger,
  'minInteger': minInteger,
  'maxNumber': maxNumber,
  'minNumber': minNumber,
  'max': max,
  'min': min,
};

Object sum(List args) {
  if (args.length != 2) {
    throw 'Exactly 2 argument(s) expected';
  }
  return sumOp(args[0], args[1]);
}

num sub(List args) {
  if (args.length != 2) {
    throw 'Exactly 2 argument(s) expected';
  }
  return subOp(args[0], args[1]);
}

num div(List args) {
  if (args.length != 2) {
    throw 'Exactly 2 argument(s) expected';
  }
  return divOp(args[0], args[1]);
}

num mul(List args) {
  if (args.length != 2) {
    throw 'Exactly 2 argument(s) expected';
  }
  return mulOp(args[0], args[1]);
}

num mod(List args) {
  if (args.length != 2) {
    throw 'Exactly 2 argument(s) expected';
  }
  return modOp(args[0], args[1]);
}

num abs(List args) {
  if (args.length != 1) {
    throw 'Exactly 1 argument(s) expected';
  }
  if (args[0] is int) {
    return trueInt(BigInt.from(args[0]).abs());
  }
  return trueNum(args[0].abs());
}

num signum(List args) {
  if (args.length != 1) {
    throw 'Exactly 1 argument(s) expected';
  }
  return args[0].sign;
}

num copySign(List args) {
  if (args.length != 2) {
    throw 'Exactly 2 argument(s) expected';
  }
  if (args.every((e) => e is int)) {
    return trueInt(copySignInt(args[0], args[1]));
  }
  return trueNum(copySignNum(args[0], args[1]));
}

double round(List args) {
  if (args.length != 1) {
    throw 'Exactly 1 argument(s) expected';
  }
  return trueNum(args[0]).round().toDouble();
}

double floor(List args) {
  if (args.length != 1) {
    throw 'Exactly 1 argument(s) expected';
  }
  return args[0].floor().toDouble();
}

double ceil(List args) {
  if (args.length != 1) {
    throw 'Exactly 1 argument(s) expected';
  }
  return args[0].ceil().toDouble();
}

int maxInteger(List args) {
  if (args.isNotEmpty) {
    throw 'Exactly 0 argument(s) expected';
  }
  return maxIntegerValue;
}

int minInteger(List args) {
  if (args.isNotEmpty) {
    throw 'Exactly 0 argument(s) expected';
  }
  return minIntegerValue;
}

double maxNumber(List args) {
  if (args.isNotEmpty) {
    throw 'Exactly 0 argument(s) expected';
  }
  return double.maxFinite;
}

double minNumber(List args) {
  if (args.isNotEmpty) {
    throw 'Exactly 0 argument(s) expected';
  }
  return double.minPositive;
}

Object max(List args) {
  if (args.isEmpty) {
    throw 'Function requires non empty argument list';
  }
  return args.reduce((c, n) => compare(c, n) > 0 ? c : n);
}

Object min(List args) {
  if (args.isEmpty) {
    throw 'Function requires non empty argument list';
  }
  return args.reduce((c, n) => c < n ? c : n);
}
