// Copyright 2018 Yandex LLC. All rights reserved.

import Serialization

public protocol TemplateDeserializable {
  init(
    dictionary: [String: Any],
    templateToType: TemplateToType
  ) throws
}
