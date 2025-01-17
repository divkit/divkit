import {
    describe,
    expect,
    test,
    vi
} from 'vitest';

import { applyTemplate } from '../../src/utils/applyTemplate';

describe('property', () => {
    test('test_property_not_templated', () => {
        const json = require('../../../../../test_data/template_test_data/property/test_property_not_templated.json');
        const logError = vi.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError.mock.calls).toMatchSnapshot();
    });

    test('test_property_not_templated_invalid', () => {
        const json = require('../../../../../test_data/template_test_data/property/test_property_not_templated_invalid.json');
        const logError = vi.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError.mock.calls).toMatchSnapshot();
    });

    test('test_property_template_unknown_type', () => {
        const json = require('../../../../../test_data/template_test_data/property/test_property_template_unknown_type.json');
        const logError = vi.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError).not.toBeCalled();
    });

    test('test_property_unknown_type', () => {
        const json = require('../../../../../test_data/template_test_data/property/test_property_unknown_type.json');
        const logError = vi.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError.mock.calls).toMatchSnapshot();
    });

    test('test_property_with_link', () => {
        const json = require('../../../../../test_data/template_test_data/property/test_property_with_link.json');
        const logError = vi.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError).not.toBeCalled();
    });

    test('test_property_with_link_invalid', () => {
        const json = require('../../../../../test_data/template_test_data/property/test_property_with_link_invalid.json');
        const logError = vi.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError).not.toBeCalled();
    });

    test('test_property_with_link_missing_data', () => {
        const json = require('../../../../../test_data/template_test_data/property/test_property_with_link_missing_data.json');
        const logError = vi.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError.mock.calls).toMatchSnapshot();
    });

    test('test_property_with_override', () => {
        const json = require('../../../../../test_data/template_test_data/property/test_property_with_override.json');
        const logError = vi.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError.mock.calls).toMatchSnapshot();
    });

    test('test_property_with_override_in_template', () => {
        const json = require('../../../../../test_data/template_test_data/property/test_property_with_override_in_template.json');
        const logError = vi.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError).not.toBeCalled();
    });

    test('test_property_without_link', () => {
        const json = require('../../../../../test_data/template_test_data/property/test_property_without_link.json');
        const logError = vi.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError).not.toBeCalled();
    });

    test('test_property_without_link_invalid', () => {
        const json = require('../../../../../test_data/template_test_data/property/test_property_without_link_invalid.json');
        const logError = vi.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError).not.toBeCalled();
    });

    test('test_property_without_link_missing_data', () => {
        const json = require('../../../../../test_data/template_test_data/property/test_property_without_link_missing_data.json');
        const logError = vi.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError).not.toBeCalled();
    });
});
