from ...schema.modeling.text import Text


def bold(content: str) -> str:
    return f'**{content}**'


def code(content: str) -> str:
    return f'`{content}`'


def content_in_quotes(prefix: str, content: str, suffix: str) -> Text:
    return Text(f'{prefix}"{content}"{suffix}')


def paragraph(content: str) -> str:
    return f'<p>{content}</p>'
