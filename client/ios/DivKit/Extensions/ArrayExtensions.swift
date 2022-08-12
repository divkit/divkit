// Copyright 2018 Yandex LLC. All rights reserved.

import CommonCore
import LayoutKit

extension Array where Element == Div {
  func makeBlocks<T>(
    context: DivBlockModelingContext,
    mappedBy modificator: (Div, Block) throws -> T
  ) throws -> [T] {
    try iterativeFlatMap { div, index in
      let divContext = modified(context) { $0.parentPath += index }
      let block = try? div.value.makeBlock(context: divContext)
      return try block.map { try modificator(div, $0) }
    }
  }

  func makeBlocks(context: DivBlockModelingContext) throws -> [Block] {
    try makeBlocks(context: context, mappedBy: { $1 })
  }
}
