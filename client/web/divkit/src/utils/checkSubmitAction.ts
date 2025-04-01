import type { ActionSubmit, ActionSubmitHeader, ActionSubmitMethod } from '../../typings/common';
import type { MaybeMissing } from '../expressions/json';

function checkString(str: string | undefined): boolean {
    return Boolean(str && typeof str === 'string');
}

const allowedMethods = new Set([
    'get',
    'post',
    'put',
    'patch',
    'delete',
    'head',
    'options'
]);

function checkMethod(method: ActionSubmitMethod | undefined): boolean {
    return method === undefined || allowedMethods.has(method);
}

function checkHeaders(headers: MaybeMissing<ActionSubmitHeader>[] | undefined): boolean {
    return headers === undefined ||
        Array.isArray(headers) &&
            headers.every(header => checkString(header.name) && checkString(header.value));
}

export function checkSubmitAction(action: MaybeMissing<ActionSubmit>): action is ActionSubmit {
    return checkString(action.container_id) &&
        checkString(action.request?.url) &&
        checkMethod(action.request?.method) &&
        checkHeaders(action.request?.headers);
}
