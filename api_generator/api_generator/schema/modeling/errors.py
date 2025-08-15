from ..preprocessing.entities import ElementLocation


class InvalidFieldRepresentationError(Exception):
    def __init__(self, location: ElementLocation, value: any):
        self.location = location
        self.value = value
        message = f'Error {location}: invalid value "{value}"'
        super(InvalidFieldRepresentationError, self).__init__(message)


class GenericError(Exception):
    def __init__(self, location: ElementLocation, text: str):
        self.location = location
        self.text = text
        message = f'Error {location}: "{text}"'
        super(GenericError, self).__init__(message)


class UnsupportedFormatTypeError(Exception):
    def __init__(self):
        message = 'Only object type is supported as a complex string format'
        super(UnsupportedFormatTypeError, self).__init__(message)
