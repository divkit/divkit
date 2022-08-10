from . import entities


class FileAtInputPathError(Exception):
    def __init__(self, path: str):
        self.path = path
        message = f'Found file at input path {path}'
        super(FileAtInputPathError, self).__init__(message)


class PathNotFoundError(Exception):
    def __init__(self, path: str):
        self.path = path
        message = f'Could not find file or directory at path {path}'
        super(PathNotFoundError, self).__init__(message)


class NonDictionaryContentError(Exception):
    def __init__(self, path: str):
        self.path = path
        message = f'Root JSON object in file at {path} is not an object'
        super(NonDictionaryContentError, self).__init__(message)


class InvalidReferenceError(Exception):
    def __init__(self):
        message = 'Could not resolve some reference'
        super(InvalidReferenceError, self).__init__(message)


class InvalidFileReferenceError(Exception):
    def __init__(self, location: entities.ElementLocation, reference: str):
        self.location: entities.ElementLocation = location
        self.reference: str = reference
        message = f'Could not resolve reference {reference} {location}'
        super(InvalidFileReferenceError, self).__init__(message)


class UnsupportedCircularReferenceError(Exception):
    def __init__(self, description: str):
        self.description: str = description
        message = f'Unsupported circular reference: {description}\nOnly circular references to classes are allowed'
        super(UnsupportedCircularReferenceError, self).__init__(message)


class AmbiguousMergeError(Exception):
    def __init__(self, location: entities.ElementLocation):
        self.location: entities.ElementLocation = location
        message = f'Could not merge objects {location}'
        super(AmbiguousMergeError, self).__init__(message)


class InvalidAllOfError(Exception):
    def __init__(self, location: entities.ElementLocation):
        self.location: entities.ElementLocation = location
        message = f'Value of allOf key is not an array of objects {location}'
        super(InvalidAllOfError, self).__init__(message)


class UnresolvedReferenceError(Exception):
    def __init__(self, location: entities.ElementLocation, object_name: str, field_name: str, unresolved_typename: str):
        self.location: entities.ElementLocation = location
        self.object_name: str = object_name
        self.field_name: str = field_name
        self.unresolved_typename: str = unresolved_typename
        message = f'Property of "{object_name}" with name "{field_name}" with type "{unresolved_typename}" ' \
                  f'is not defined {location}. Did you make a reference to non-root complex type?'
        super(UnresolvedReferenceError, self).__init__(message)
