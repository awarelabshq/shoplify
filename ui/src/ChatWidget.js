import React, { useState } from 'react';
import { Button, Input } from 'antd';
import { MessageOutlined } from '@ant-design/icons';
import client from './api';
import './ChatWidget.css';

const ChatWidget = () => {
  const [isOpen, setIsOpen] = useState(false);
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState('');

  const toggleChat = () => setIsOpen(!isOpen);

  const sendMessage = async () => {
    if (!input.trim()) return;
    const userMessage = { sender: 'user', text: input };
    setMessages([...messages, userMessage]);
    setInput('');

    try {
      const response = await client.post('/frontend/chat', { message: input });
      const botMessage = { sender: 'bot', text: response.data.message };
      setMessages((prev) => [...prev, botMessage]);
    } catch (error) {
      console.error('Chat error:', error);
    }
  };

  return (
    <div className="chat-widget">
      <Button className="chat-toggle" type="primary" shape="circle" icon={<MessageOutlined />} onClick={toggleChat} />
      {isOpen && (
        <div className="chat-window">
          <div className="chat-header">
            <span>Shoplify AI</span>
            <Button type="text" onClick={toggleChat}>×</Button>
          </div>
          <div className="chat-messages">
            {messages.map((msg, index) => (
              <div key={index} className={`chat-bubble ${msg.sender}`} data-testid={`chat-bubble-${index}`}>
                {msg.text}
              </div>
            ))}
          </div>
          <div className="chat-input">
            <Input
              value={input}
              onChange={(e) => setInput(e.target.value)}
              onPressEnter={sendMessage}
              placeholder="Type your message..."
            />
            <Button type="primary" onClick={sendMessage}>Send</Button>
          </div>
        </div>
      )}
    </div>
  );
};

export default ChatWidget;
