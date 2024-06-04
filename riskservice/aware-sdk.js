const api = require('@opentelemetry/api');
const protobuf = require('protobufjs');
const url = require('url');
const path = require('path');
const bodyParser = require('body-parser');
const multer = require('multer');
const fs = require('fs');
const yaml = require('js-yaml');
const jsonpath = require('jsonpath');

// Adjust the path to the proto file
const protoPath = path.join(__dirname, './payload.proto');
const root = protobuf.loadSync(protoPath);
const Payload = root.lookupType('Payload');
const HttpPayload = root.lookupType('HttpPayload');

// Create a multer instance for multipart form-data parsing
const upload = multer();

// Initialize body parsers
const jsonParser = bodyParser.json();
const urlencodedParser = bodyParser.urlencoded({ extended: true });

function getContentType(headers) {
  for (const [name, value] of Object.entries(headers)) {
    if (name.toLowerCase() === 'content-type') {
      return value.toLowerCase();
    }
  }
  // Default to plain text content type if not found
  return 'text/plain';
}

function parseConfig(configFilePath) {
  let config = {};
  if (configFilePath) {
    try {
      const configFile = fs.readFileSync(configFilePath, 'utf8');
      config = yaml.load(configFile);
    } catch (error) {
      console.error(`Failed to load config file: ${error.message}`);
    }
  }
  return config;
}

function createMappings(config) {
  const mappings = {
    ignoredUrls: new Set(),
    ignoredHeaders: new Set(),
    urlSpecificConfig: {},
  };

  if (config.global_config) {
    if (config.global_config.ignored_urls) {
      config.global_config.ignored_urls.forEach(url => mappings.ignoredUrls.add(url));
    }
    if (config.global_config.ignored_headers) {
      config.global_config.ignored_headers.forEach(header => mappings.ignoredHeaders.add(header));
    }
  }

  if (config.url_configs) {
    Object.keys(config.url_configs).forEach(urlPattern => {
      const urlConfig = config.url_configs[urlPattern];
      const requestConfig = urlConfig.request || {};
      const responseConfig = urlConfig.response || {};

      mappings.urlSpecificConfig[urlPattern] = {
        request: {
          ignoredHeaders: new Set(requestConfig.ignored_headers || []),
          ignoredFields: requestConfig.ignored_fields || [],
          extractToSpanAttributes: requestConfig.extract_to_span_attributes || [],
          extractHeadersToSpanAttributes: requestConfig.extract_headers_to_span_attributes || [],
          userIdField: requestConfig.user_id_field || null
        },
        response: {
          ignoredHeaders: new Set(responseConfig.ignored_headers || []),
          ignoredFields: responseConfig.ignored_fields || [],
          extractToSpanAttributes: responseConfig.extract_to_span_attributes || [],
          extractHeadersToSpanAttributes: responseConfig.extract_headers_to_span_attributes || [],
          userIdField: responseConfig.user_id_field || null
        }
      };
    });
  }

  return mappings;
}

function processJsonBody(requestBody, span, urlConfig) {
  urlConfig.request.ignoredFields.forEach(query => {
    const matches = jsonpath.query(requestBody, query);
    matches.forEach(match => {
      const matchPath = jsonpath.paths(requestBody, query)[0];
      if (matchPath) {
        const lastSegment = matchPath.pop();
        let current = requestBody;
        matchPath.forEach(segment => {
          current = current[segment];
        });
        delete current[lastSegment];
      }
    });
  });

  urlConfig.request.extractToSpanAttributes.forEach(query => {
    const matches = jsonpath.query(requestBody, query);
    if (matches.length > 0) {
      const firstField = jsonpath.paths(requestBody, query)[0].pop();
      span.setAttribute(`extracted_field_${firstField}`, matches.join(', '));
    }
  });

  if (urlConfig.request.userIdField) {
    const userIdMatches = jsonpath.query(requestBody, urlConfig.request.userIdField);
    if (userIdMatches.length > 0) {
      span.setAttribute('session.user_id', userIdMatches.join(', '));
    }
  }
}

function processOtherBodyTypes(body, span, urlConfig) {
  urlConfig.request.ignoredFields.forEach(field => {
    if (field === '$') {
      body = null;
    }
  });

  urlConfig.request.extractToSpanAttributes.forEach(field => {
    if (field === '$' && body) {
      span.setAttribute('extracted_field_body', body);
    }
  });

  if (urlConfig.request.userIdField && body) {
    span.setAttribute('session.user_id', body);
  }

  return body;
}

function awareSdk(configFilePath) {
  const config = parseConfig(configFilePath);
  const mappings = createMappings(config);

  return (req, res, next) => {
    const span = api.trace.getSpan(api.context.active());
    const parsedUrl = url.parse(req.url, true);
    const urlPath = parsedUrl.pathname;

    if (mappings.ignoredUrls.has(urlPath)) {
      return next();
    }

    const parseRequestBody = (callback) => {
      const contentType = getContentType(req.headers);

      if (contentType.includes('application/json')) {
        jsonParser(req, res, callback);
      } else if (contentType.includes('application/x-www-form-urlencoded')) {
        urlencodedParser(req, res, callback);
      } else if (contentType.includes('multipart/form-data')) {
        upload.any()(req, res, callback);
      } else {
        callback();
      }
    };

    parseRequestBody((err) => {
      if (span) {
        if (err) {
          console.error('Error parsing request body:', err);
        }

        let requestBody = req.body;
        let bodyType = 'text_body';

        if (req.headers['content-type'].includes('application/json')) {
          bodyType = 'json_body';
          requestBody = JSON.stringify(requestBody); // Ensure the request body is a JSON string
        } else if (req.headers['content-type'].includes('application/x-www-form-urlencoded')) {
          bodyType = 'http_form_urlencoded_body';
        } else if (req.headers['content-type'].includes('multipart/form-data')) {
          bodyType = 'http_form_data_body';
        }

        const httpPayload = {
          header_map: req.headers,
          query_param_map: parsedUrl.query,
          http_method: req.method,
          [bodyType]: requestBody
        };

        const urlConfig = mappings.urlSpecificConfig[urlPath];
        if (urlConfig) {
          urlConfig.request.ignoredHeaders.forEach(header => {
            delete httpPayload.header_map[header];
          });

          if (bodyType === 'json_body') {
            processJsonBody(requestBody, span, urlConfig);
          } else {
            requestBody = processOtherBodyTypes(requestBody, span, urlConfig);
          }

          urlConfig.request.extractHeadersToSpanAttributes.forEach(header => {
            const headerValue = req.headers[header];
            if (headerValue) {
              span.setAttribute(`extracted_header_${header}`, headerValue);
            }
          });
        }

        const payload = {
          span_id: span.spanContext().spanId,
          http_payload: httpPayload
        };

        const payloadString = JSON.stringify(payload);
        console.log("req : " + payloadString);
        span.setAttribute('aware.derived.request.payload', payloadString);
        span.setAttribute('aware.derived.url.path.self', parsedUrl.pathname);
      }

      const originalEnd = res.end;
      res.end = function (...args) {
        if (span) {
          let responseBody = args[0] instanceof Buffer ? args[0].toString() : args[0];
          const contentType = getContentType(res.getHeaders());
          let responseBodyPayload = {};

          if (contentType) {
            let responseBodyType;

            if (contentType.includes('application/json')) {
              responseBodyType = 'json_body';
            } else if (contentType.includes('text/plain')) {
              responseBodyType = 'text_body';
            } else if (contentType.includes('text/html')) {
              responseBodyType = 'html_body';
            } else if (contentType.includes('application/xml') || contentType.includes('text/xml')) {
              responseBodyType = 'xml_body';
            } else if (contentType.includes('application/x-www-form-urlencoded')) {
              responseBodyType = 'http_form_urlencoded_body';
            } else if (contentType.includes('multipart/form-data')) {
              responseBodyType = 'http_form_data_body';
            } else {
              responseBodyType = 'binary_data_body';
            }

            responseBodyPayload[responseBodyType] = responseBody;
          }

          const responsePayload = {
            body: responseBodyPayload,
            response_code: res.statusCode
          };

          const responsePayloadMessage = {
            span_id: span.spanContext().spanId,
            http_payload: responsePayload
          };

          const responsePayloadString = JSON.stringify(responsePayloadMessage);
          console.log("res content type " + contentType);
          console.log("res : " + responsePayloadString);
          span.setAttribute('aware.derived.response.payload', responsePayloadString);
        }
        originalEnd.apply(res, args);
      };

      next(); // Proceed to next middleware regardless of any errors during parsing
    });
  };
}

module.exports = awareSdk;
