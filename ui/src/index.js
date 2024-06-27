import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';
import { TestChimpSDK } from "testchimp-js";

window.onload = function () {
  TestChimpSDK.startRecording({
    enableRecording: true,
    projectId: "de40f23a-61db-40b1-91ee-2e42435c0fab",
    sessionRecordingApiKey: "3ac2f316-95a0-441b-9132-aba1a4e24bf6",
    endpoint: "https://ingress-staging.testchimp.io",
    samplingProbabilityOnError: 0.1,
    samplingProbability: 1.0,
    maxSessionDurationSecs: 500,
    eventWindowToSaveOnError: 200,
    tracedUriRegexListToTrack: ".*://shoplify-staging-fe.testchimp\.io.*$",
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
