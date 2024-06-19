import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';
import { TestChimpSDK } from "testchimp-js";

window.onload = function () {
  TestChimpSDK.startRecording({
    enableRecording: true,
    projectId: process.env.TESTCHIMP_PROJECT_ID,
    sessionRecordingApiKey: process.env.TESTCHIMP_SESSION_RECORD_API_KEY,
    endpoint: process.env.TESTCHIMP_INGRESS_ENDPOINT,
    samplingProbabilityOnError: 0.1,
    samplingProbability: 1.0,
    maxSessionDurationSecs: 500,
    eventWindowToSaveOnError: 200,
    tracedUriRegexListToTrack: ".*://shoplify-staging.awarelabs\.io.*$",
    untracedUriRegexListToTrack: "/^$/",
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
