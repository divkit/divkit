{{
    import { MAX_INT, MIN_INT } from './const';
    import { toBigInt } from './bigint';

    function buildBinaryExpression(head, tail) {
        return tail.reduce((result, element) => {
            return {
                type: 'BinaryExpression',
                operator: element[1],
                left: result,
                right: element[3]
            };
        }, head);
    }

    function buildLogicalExpression(head, tail) {
        return tail.reduce((result, element) => {
            return {
                type: 'LogicalExpression',
                operator: element[1],
                left: result,
                right: element[3]
            };
        }, head);
    }

    function buildString(list) {
        if (list.every(it => typeof it === 'string')) {
            return {type: 'StringLiteral', value: list.join('')};
        }
        let res = list.reduce((result, element) => {
            if (typeof element === 'string' && typeof result[result.length - 1] === 'string') {
                result[result.length - 1] += element;
            } else {
                result.push(element);
            }
            return result;
        }, []).reduce((result, element) => {
            if (typeof element === 'string') {
                result.quasis.push({type: 'StringLiteral', value: element});
            } else {
                if (result.quasis.length === result.expressions.length) {
                    result.quasis.push({type: 'StringLiteral', value: ''});
                }
                result.expressions.push(element);
            }
            return result;
        }, {
            type: 'TemplateLiteral',
            quasis: [],
            expressions: []
        });

        if (res.quasis.length === res.expressions.length) {
            res.quasis.push({type: 'StringLiteral', value: ''});
        }

        return res;
    }

    function convertToInt64(str) {
        try {
            return toBigInt(str);
        } catch (_err) {
            throw new Error(`Value ${str} can't be converted to Integer type.`);
        }
    }

    function escapeChar(char) {
        if (char === "'" || char === '\\') {
            return char;
        }
        throw new Error('Incorrect string escape');
    }
}}

start
    = _ expr:Expression _ { return expr; }

JsonStringContents
    = list:JsonStringChar* { return buildString(list); }

JsonStringChar
    = '@{' _ expr:Expression _ '}' { return expr; }
    / '@{}' { return ''; }
    / !( '\\' / '@{' ) . { return text(); }
    / '@{' [^}]+ '}' { throw new Error("Error tokenizing '" + input + "'."); }
    / '@{' { throw new Error("Error tokenizing '" + input + "'."); }
    / '\\' str:'@{' { return str; }
    / '\\' char:. { return escapeChar(char); }
    / '\\' { throw new Error("Error tokenizing '" + input + "'."); }

Expression
    = expr:Or _ '?' _ first:Expression _ ':' _ second:Expression
        { return {type: 'ConditionalExpression', test: expr, consequent: first, alternate: second}; }
    / Or

Or
    = head:And tail:( _ '||' _ And )*
        { return buildLogicalExpression(head, tail); }

And
    = head:Equal tail:( _ '&&' _ Equal )*
        { return buildLogicalExpression(head, tail); }

Equal
    = head:Comparison tail:( _ ( '==' / '!=' ) _  Comparison )*
        { return buildBinaryExpression(head, tail); }

Comparison
    = head:Sum tail:( _ ( '>=' / '>' / '<=' / '<' ) _  Sum )*
        { return buildBinaryExpression(head, tail); }

Sum
    = head:Factor tail:( _ ( '+' / '-' ) _  Factor )*
        { return buildBinaryExpression(head, tail); }

Factor
    = head:Unary tail:( _ ( '/' / '*' / '%' ) _  Unary )*
        { return buildBinaryExpression(head, tail); }

Unary
    = Number
    / Integer
    / op:( '!' / '+' / '-' ) _  expr:Unary
        { return {type: 'UnaryExpression', operator: op, argument: expr}; }
    / Call

Call
    = id:Identifier _ '(' _ args:Arguments _ ')'
        { return {type: 'CallExpression', callee: id, arguments: args}; }
    / id:Identifier { return {type: 'Variable', id}; }
    / PrimaryExpression

Arguments
    = expr:Expression list:( _ ',' _ @Expression)* { return [expr, ...list]; }
    / _

PrimaryExpression
    = String
    / Number
    / Integer
    / Boolean
    / '(' expr:Expression ')' { return expr; }

String "string"
    = "'" str:StringContents "'" { return str; }

StringContents
    = list:StringChar* { return buildString(list); }

StringChar
    = '@{' _ expr:Expression _ '}' { return expr; }
    / '@{}' { return ''; }
    / !('\\' / "'" / '@{') . { return text(); }
    / '@{' [^'}]+ '}' { throw new Error("Error tokenizing '" + input + "'."); }
    / '@{' { throw new Error("Error tokenizing '" + input + "'."); }
    / '\\' str:'@{' { return str; }
    / '\\' char:. { return escapeChar(char); }
    / '\\' { throw new Error("Error tokenizing '" + input + "'."); }

Integer "integer"
    = '-'? [0-9]+ { return {type: 'IntegerLiteral', value: convertToInt64(text())}; }

Number "number"
    = '-'? [0-9]* '.' [0-9]+ ( ( 'e' / 'E' ) ( '+' / '-' )? [0-9]+ )? { return {type: 'NumberLiteral', value: parseFloat(text())}; }
    / '-'? [0-9]+ ( 'e' / 'E' ) ( '+' / '-' )? [0-9]+ { return {type: 'NumberLiteral', value: parseFloat(text())}; }

Boolean "boolean"
    = 'true' { return {type: 'BooleanLiteral', value: true}; }
    / 'false' { return {type: 'BooleanLiteral', value: false}; }

ReservedWord
    = ( 'true' / 'false' ) ![a-zA-Z_0-9.]

Identifier "identifier"
    = !ReservedWord [a-zA-Z_][a-zA-Z_0-9.]* { return {type: 'Identifier', name: text()}; }

_ "whitespace"
    = [ \t\r\n]*
