from abc import abstractmethod
from typing import cast, Dict, List, Union

from .utils import code, content_in_quotes
from ...schema.modeling.entities import (
    PropertyType,
    Property,
    Declarable,
    Entity,
    EntityEnumeration,
    StringEnumeration,
    Array,
    Bool,
    BoolInt,
    Color,
    Dictionary,
    Double,
    Int,
    Object,
    ObjectFormat,
    StaticString,
    String,
    Url
)
from ...schema.modeling.text import Text


class DocumentationPropertyType(PropertyType):

    @property
    @abstractmethod
    def description(self) -> str:
        pass

    def json_description(self, obj_stack: List[Declarable], prefix: str, suffix: str) -> Text:
        return content_in_quotes(prefix, self.description, suffix)

    @abstractmethod
    def constraints(self, dictionary: Dict[str, str]) -> str:
        pass

    def details(self, dictionary: Dict[str, str]) -> str:
        return ''


class DocumentationProperty(Property):

    def update_base(self):
        update_property_type_base(self.property_type)

    @property
    def property_type_doc(self) -> DocumentationPropertyType:
        return cast(DocumentationPropertyType, self.property_type)

    def json_description_prefix(self, parent: Entity, property_type: PropertyType) -> str:
        if isinstance(property_type, Array):
            return self.json_description_prefix(parent, property_type.property_type)
        return f'{self.dict_field}{"" if self.optional else "*"}: '


class DocumentationDeclarable(Declarable):
    @abstractmethod
    def json_description(self, obj_stack: List[Declarable], prefix: str = '', suffix: str = '') -> Text:
        pass

    @property
    @abstractmethod
    def include_in_menu_file(self) -> bool:
        pass


def update_declarable_base(obj: Declarable) -> DocumentationDeclarable:
    if isinstance(obj, Entity):
        obj.__class__ = DocumentationEntity
        obj = cast(DocumentationEntity, obj)
        obj.update_base()
        return obj
    elif isinstance(obj, EntityEnumeration):
        obj.__class__ = DocumentationEntityEnumeration
        return cast(DocumentationEntityEnumeration, obj)
    elif isinstance(obj, StringEnumeration):
        obj.__class__ = DocumentationStringEnumeration
        return cast(DocumentationStringEnumeration, obj)
    else:
        raise NotImplementedError


def update_property_type_base(property_type: PropertyType):
    if isinstance(property_type, Array):
        property_type.__class__ = DocumentationArray
        property_type = cast(DocumentationArray, property_type)
        property_type.update_base()
    elif isinstance(property_type, Bool):
        property_type.__class__ = DocumentationBool
    elif isinstance(property_type, BoolInt):
        property_type.__class__ = DocumentationBoolInt
    elif isinstance(property_type, Color):
        property_type.__class__ = DocumentationColor
    elif isinstance(property_type, Dictionary):
        property_type.__class__ = DocumentationDictionary
    elif isinstance(property_type, Double):
        property_type.__class__ = DocumentationDouble
    elif isinstance(property_type, Int):
        property_type.__class__ = DocumentationInt
    elif isinstance(property_type, Object):
        property_type.__class__ = DocumentationObject
        property_type = cast(DocumentationObject, property_type)
        property_type.update_base()
    elif isinstance(property_type, StaticString):
        property_type.__class__ = DocumentationStaticString
    elif isinstance(property_type, String):
        property_type.__class__ = DocumentationString
    elif isinstance(property_type, Url):
        property_type.__class__ = DocumentationUrl
    else:
        raise NotImplementedError


class DocumentationEntity(Entity, DocumentationDeclarable):

    def update_base(self):
        for prop in self.properties:
            prop.__class__ = DocumentationProperty
            prop = cast(DocumentationProperty, prop)
            prop.update_base()

    @property
    def display_name(self) -> str:
        return self._display_name

    @property
    def is_deprecated(self) -> bool:
        return self._is_deprecated

    @property
    def properties_doc(self) -> List[DocumentationProperty]:
        return cast(List[DocumentationProperty], self.properties)

    def json_description(self, obj_stack: List[Declarable], prefix: str = '', suffix: str = '') -> Text:
        if self in obj_stack:
            return Text(prefix + self.display_name + suffix)

        result = Text(prefix + '{')
        last_index = len(self.properties_doc) - 1
        for index, prop in enumerate(sorted(
            self.properties_doc,
            key=lambda p: not isinstance(p.property_type, StaticString)
        )):
            result += prop.property_type_doc.json_description(
                obj_stack=obj_stack + [self],
                prefix=prop.json_description_prefix(parent=self, property_type=prop.property_type),
                suffix='' if index == last_index else ','
            ).indented()
        result += '}' + suffix
        return result

    def include_in_menu_file(self):
        return not self.generate_as_protocol


class DocumentationEntityEnumeration(EntityEnumeration, DocumentationDeclarable):

    def json_description(self, obj_stack: List[Declarable], prefix: str = '', suffix: str = '') -> Text:
        return Text(prefix + self.name + suffix)

    def include_in_menu_file(self) -> bool:
        return True


class DocumentationStringEnumeration(StringEnumeration, DocumentationDeclarable):

    def json_description(self, obj_stack: List[Declarable], prefix: str = '', suffix: str = '') -> Text:
        return content_in_quotes(prefix, 'string', suffix)

    def include_in_menu_file(self) -> bool:
        return False


class DocumentationArray(Array, DocumentationPropertyType):

    def update_base(self):
        if not isinstance(self.property_type, DocumentationPropertyType):
            update_property_type_base(self.property_type)

    @property
    def description(self) -> str:
        return 'array'

    @property
    def __property_type_doc(self) -> DocumentationPropertyType:
        return cast(DocumentationPropertyType, self.property_type)

    def json_description(self,
                         obj_stack: List[Declarable],
                         prefix: str,
                         suffix: str) -> Text:
        array_type_description = self.__property_type_doc.json_description(obj_stack=obj_stack, prefix='', suffix=',')
        if len(array_type_description.lines) > 1:
            result = Text(prefix + '[')
            result += array_type_description.indented()
            result += '  ...'
            result += ']' + suffix
            return result
        return self.__property_type_doc.json_description(
            obj_stack=obj_stack,
            prefix=prefix + '[ ',
            suffix=', ... ]' + suffix
        )

    def constraints(self, dictionary: Dict[str, str]) -> str:
        result = Text()
        min_items = self.min_items
        if min_items == 1:
            result += dictionary['div_generator_non_empty_array']
        elif min_items > 1:
            result += dictionary['div_generator_elements_min_number'].format(min_items)
        if self.strict_parsing:
            result += dictionary['div_generator_partial_parsing']
        return str(result)

    def details(self, dictionary: Dict[str, str]) -> str:
        return self.__property_type_doc.details(dictionary)


class DocumentationBool(Bool, DocumentationPropertyType):

    @property
    def description(self) -> str:
        return 'bool'

    def constraints(self, dictionary: Dict[str, str]) -> str:
        return ''


class DocumentationBoolInt(BoolInt, DocumentationPropertyType):

    @property
    def description(self) -> str:
        return 'bool_int'

    def constraints(self, dictionary: Dict[str, str]) -> str:
        return ''


class DocumentationColor(Color, DocumentationPropertyType):

    @property
    def description(self) -> str:
        return 'string'

    def constraints(self, dictionary: Dict[str, str]) -> str:
        return dictionary['div_generator_valid_formats_color']


class DocumentationDictionary(Dictionary, DocumentationPropertyType):

    @property
    def description(self) -> str:
        return 'object'

    def constraints(self, dictionary: Dict[str, str]) -> str:
        return ''


class DocumentationDouble(Double, DocumentationPropertyType):

    @property
    def description(self) -> str:
        return 'number'

    def constraints(self, dictionary: Dict[str, str]) -> str:
        return constraints_for_numbers(self, dictionary)


class DocumentationInt(Int, DocumentationPropertyType):

    @property
    def description(self) -> str:
        return 'int'

    def constraints(self, dictionary: Dict[str, str]) -> str:
        return constraints_for_numbers(self, dictionary)


def constraints_for_numbers(number: Union[Double, Int], dictionary: Dict[str, str]) -> str:
    if number.constraint is None:
        return ''
    constraint_str = number.constraint.replace('number', 'x')
    return dictionary['div_generator_value_restriction'].format(constraint_str)


class DocumentationObject(Object, DocumentationPropertyType):

    def update_base(self):
        if not isinstance(self.object, DocumentationDeclarable):
            update_declarable_base(self.object)

    @property
    def description(self) -> str:
        if isinstance(self.object, StringEnumeration):
            return 'string'
        return 'string' if self.format == ObjectFormat.JSON_STRING else 'object'

    @property
    def __object_doc(self) -> DocumentationDeclarable:
        return cast(DocumentationDeclarable, self.object)

    def json_description(self, obj_stack: List[DocumentationDeclarable], prefix: str, suffix: str) -> Text:
        return self.__object_doc.json_description(obj_stack, prefix, suffix)

    def constraints(self, dictionary: Dict[str, str]) -> str:
        obj = self.object
        if isinstance(obj, StringEnumeration):
            values = ', '.join(map(lambda case: code(case[0]), obj.cases))
            return dictionary['div_generator_possible_values'].format(values)
        result = Text(dictionary['div_generator_value_type'].format(obj.name))
        if self.format == ObjectFormat.JSON_STRING:
            result += dictionary['div_generator_value_json']
        return str(result)

    def details(self, dictionary: Dict[str, str]) -> str:
        if not isinstance(self.object, EntityEnumeration):
            return ''

        list_header = dictionary['div_generator_values_list']
        details = list(map(lambda obj: f'<li>[{obj[0]}]({obj[0]}.md#{obj[0]})</li>', self.object.entities))
        details.insert(0, list_header)
        return ''.join(details)


class DocumentationStaticString(StaticString, DocumentationPropertyType):

    @property
    def description(self) -> str:
        return 'string'

    def json_description(self, obj_stack: List[Declarable], prefix: str, suffix: str) -> Text:
        return content_in_quotes(prefix, self.value, suffix)

    def constraints(self, dictionary: Dict[str, str]) -> str:
        return dictionary['div_generator_value_must_be'].format(self.value)


class DocumentationString(String, DocumentationPropertyType):

    @property
    def description(self) -> str:
        return 'string'

    def constraints(self, dictionary: Dict[str, str]) -> str:
        result = Text()
        min_length = self.min_length
        if min_length == 1:
            result += dictionary['div_generator_non_empty_string']
        elif min_length > 1:
            result += dictionary['div_generator_min_length'].format(min_length)
        if self.formatted:
            result += dictionary['div_generator_html_formatting']
        if self.regex is not None:
            result += dictionary['div_generator_value_regex'].format(self.regex.pattern)
        return str(result)


class DocumentationUrl(Url, DocumentationPropertyType):

    @property
    def description(self) -> str:
        return 'string'

    def constraints(self, dictionary: Dict[str, str]) -> str:
        result = Text(dictionary['div_generator_value_url'])
        schemes = self.schemes
        if schemes is not None and len(schemes) > 0:
            schemes_str = ', '.join(map(lambda s: code(s), schemes))
            result += dictionary['div_generator_schemes'].format(schemes_str)
        return str(result)
