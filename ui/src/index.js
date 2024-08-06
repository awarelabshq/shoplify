import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';
import { TestChimpSDK } from "testchimp-js";

const TC_PROJECT_ID=process.env.REACT_APP_TESTCHIMP_PROJECT_ID;
const TC_SESSION_RECORD_KEY=process.env.REACT_APP_TESTCHIMP_SESSION_RECORD_API_KEY;
const TESTCHIMP_TRACED_URI_REGEX=process.env.REACT_APP_TESTCHIMP_TRACED_URI_REGEX;
const TESTCHIMP_UNTRACED_URI_REGEX=process.env.REACT_APP_TESTCHIMP_UNTRACED_URI_REGEX
console.log("TC PROJECT: " + TC_PROJECT_ID);
window.onload = function () {
  TestChimpSDK.startRecording({
    enableRecording: true,
    projectId: TC_PROJECT_ID,
    sessionRecordingApiKey: TC_SESSION_RECORD_KEY,
    endpoint: "https://ingress-staging.testchimp.io",
    samplingProbabilityOnError: 0.1,
    samplingProbability: 1.0,
    maxSessionDurationSecs: 500,
    eventWindowToSaveOnError: 200,
    tracedUriRegexListToTrack: TESTCHIMP_TRACED_URI_REGEX,
    untracedUriRegexListToTrack: TESTCHIMP_UNTRACED_URI_REGEX,
    environment: "QA"
  });
};

const rootElement = document.getElementById('root');
const root = ReactDOM.createRoot(rootElement);

root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
