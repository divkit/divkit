import { applyTemplate } from '../../src/utils/applyTemplate';

describe('complex_property', () => {
    test('test_complex_property_not_templated', () => {
        const json = require('../../../../../test_data/template_test_data/complex_property/test_complex_property_not_templated.json');
        const logError = jest.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError.mock.calls).toMatchSnapshot();
    });

    test('test_complex_property_with_internal_link', () => {
        const json = require('../../../../../test_data/template_test_data/complex_property/test_complex_property_with_internal_link.json');
        const logError = jest.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError).not.toBeCalled();
    });

    test('test_complex_property_with_internal_link_missing_data', () => {
        const json = require('../../../../../test_data/template_test_data/complex_property/test_complex_property_with_internal_link_missing_data.json');
        const logError = jest.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError.mock.calls).toMatchSnapshot();
    });

    test('test_complex_property_with_internal_link_override_in_template', () => {
        const json = require('../../../../../test_data/template_test_data/complex_property/test_complex_property_with_internal_link_override_in_template.json');
        const logError = jest.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError).not.toBeCalled();
    });

    test('test_complex_property_with_link', () => {
        const json = require('../../../../../test_data/template_test_data/complex_property/test_complex_property_with_link.json');
        const logError = jest.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError).not.toBeCalled();
    });

    test('test_complex_property_with_link_missing_data', () => {
        const json = require('../../../../../test_data/template_test_data/complex_property/test_complex_property_with_link_missing_data.json');
        const logError = jest.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError.mock.calls).toMatchSnapshot();
    });

    test('test_complex_property_with_override_in_template', () => {
        const json = require('../../../../../test_data/template_test_data/complex_property/test_complex_property_with_override_in_template.json');
        const logError = jest.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError).not.toBeCalled();
    });

    test('test_complex_property_without_link', () => {
        const json = require('../../../../../test_data/template_test_data/complex_property/test_complex_property_without_link.json');
        const logError = jest.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError).not.toBeCalled();
    });

    test('test_complex_property_without_link_missing_data', () => {
        const json = require('../../../../../test_data/template_test_data/complex_property/test_complex_property_without_link_missing_data.json');
        const logError = jest.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError).not.toBeCalled();
    });

    test('test_invalid_complex_property_not_templated', () => {
        const json = require('../../../../../test_data/template_test_data/complex_property/test_invalid_complex_property_not_templated.json');
        const logError = jest.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError.mock.calls).toMatchSnapshot();
    });
});
