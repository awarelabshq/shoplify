const api = require('@opentelemetry/api');
const protobuf = require('protobufjs');
const url = require('url');
const qs = require('qs');
const path = require('path');
const bodyParser = require('body-parser');
const multer = require('multer');

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
  for (const key in headers) {
    if (key.toLowerCase() === 'content-type') {
      return headers[key].toLowerCase();
    }
  }
  // Default to plain text content type if not found
  return 'text/plain';
}

function awareSdk(req, res, next) {
  const span = api.trace.getSpan(api.context.active());

  // Parse request body based on content-type
  const parseRequestBody = (callback) => {
    const contentType = getContentType(req.headers);

    if (contentType.includes('application/json')) {
      // No need for jsonParser if the body is already parsed by body-parser
      callback();
    } else if (contentType.includes('application/x-www-form-urlencoded')) {
      urlencodedParser(req, res, callback);
    } else if (contentType.includes('multipart/form-data')) {
      upload.any()(req, res, callback);
    } else {
      callback();
    }
  };

  // Process the request payload
  parseRequestBody((err) => {
    if (span) {
      if (err) {
        console.error('Error parsing request body:', err);
      }

     const contentType = getContentType(res.headers);
     const parsedUrl = url.parse(req.url, true);
      const queryParamMap = parsedUrl.query;
      const headers = req.headers;
      const method = req.method;
      let requestBody = req.body;
      let bodyType;

      if (contentType.includes('application/json')) {
        bodyType = 'json_body';
      } else if (contentType.includes('application/x-www-form-urlencoded')) {
        bodyType = 'http_form_urlencoded_body';
      } else if (contentType.includes('multipart/form-data')) {
        bodyType = 'http_form_data_body';
      } else {
        bodyType = 'text_body';
      }

      const httpPayload = HttpPayload.create({
        header_map: headers,
        query_param_map: queryParamMap,
        http_method: method,
        [bodyType]: requestBody // Populate body based on content type
      });

      const payload = Payload.create({
        span_id: span.spanContext().spanId,
        http_payload: httpPayload
      });

      const payloadString = Payload.encode(payload).finish().toString();
      span.setAttribute('aware.derived.request.payload', payloadString);
      span.setAttribute('aware.derived.url.path.self', parsedUrl.pathname);
    }

    const originalEnd = res.end;
    res.end = function (...args) {
      if (span) {
        let responseBody = args[0] instanceof Buffer ? args[0] : Buffer.from(args[0]);
        const contentType = getContentType(res.headers);
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

        const responsePayload = HttpPayload.create({
          body: responseBodyPayload,
          response_code: res.statusCode
        });

        const responsePayloadMessage = Payload.create({
          span_id: span.spanContext().spanId,
          http_payload: responsePayload
        });

        const responsePayloadString = Payload.encode(responsePayloadMessage).finish().toString();
        span.setAttribute('aware.derived.response.payload', responsePayloadString);
      }
      originalEnd.apply(res, args);
    };

    next(); // Proceed to next middleware regardless of any errors during parsing
  });
}

module.exports = awareSdk;

