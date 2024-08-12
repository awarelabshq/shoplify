import React, { useState, useEffect } from 'react';
import { Button, Input, Table, Modal, message } from 'antd';
import client from './api';
import './Cart.css';

const Cart = () => {
  const [cartItems, setCartItems] = useState([]);
  const [zipCode, setZipCode] = useState('');
  const [shippingCost, setShippingCost] = useState(null);
  const [totalCost, setTotalCost] = useState(0);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [cardNumber, setCardNumber] = useState('');
  const [expiry, setExpiry] = useState('');
  const [cvc, setCvc] = useState('');

  useEffect(() => {
    const fetchCartItems = async () => {
      const cart = JSON.parse(localStorage.getItem('cart')) || {};
      if (Object.keys(cart).length > 0) {
          const cartItems = Object.entries(cart).map(([productId, quantity]) => ({
              product_id: Number(productId),
              quantity: quantity
          }));

          // Prepare the request body in the required format
          const requestBody = {
              items: cartItems
          };

          // Send the request
        const response = await client.post('/frontend/list_cart', requestBody);
        const items = response.data.checkoutItems.map(checkoutItem => ({
            ...checkoutItem.item,        // Spread the `ProductItem` details
            quantity: cart[Number(checkoutItem.item.id)],  // Use product_id to get the quantity from the cart
            totalCost: checkoutItem.totalCost  // Add the total cost for this item
        }));
        console.log("Items: ",items);
        console.log("Cart: ",cart);
        setCartItems(items);
        setTotalCost(response.data.sumCost);
      }
    };
    fetchCartItems();
  }, []);

  const handleCalculateShipping = async () => {
    try {
      const response = await client.post('/frontend/get_shipping_cost', {
        zip_code: zipCode,
        total_cost: totalCost,
      });
      if (response.data.isSupported) {
        setShippingCost(response.data.shippingCost);
        setTotalCost(totalCost + response.data.shippingCost);
      } else {
        setShippingCost('Shipping not supported for this ZIP code.');
      }
    } catch (error) {
      message.error("An error occurred during processing. Try again later.")
      console.error('Error calculating shipping cost:', error);
    }
  };

  const handleCheckout = () => {
    setIsModalVisible(true);
  };

  const handlePay = () => {
    setIsModalVisible(false);
    message.success('Your items are on the way');
    localStorage.removeItem('cart')
  };

  const formatCardNumber = (value) => {
    const sanitizedValue = value.replace(/\D/g, ''); // Remove non-digits
    const formattedValue = sanitizedValue.replace(/(.{4})/g, '$1-').trim(); // Add hyphens every 4 digits
    return formattedValue.endsWith('-') ? formattedValue.slice(0, -1) : formattedValue; // Remove trailing hyphen
  };

  const columns = [
    {
      title: 'Image',
      dataIndex: 'imageUrl',
      key: 'imageUrl',
      render: (text) => <img src={text} alt="Product" style={{ width: '100px', height: '100px' }} />,
    },
    {
      title: 'Name',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: 'Quantity',
      dataIndex: 'quantity',
      key: 'quantity',
    },
    {
      title: 'Unit Price',
      dataIndex: 'price',
      key: 'price',
      render: (text) => `$${text}`,
    },
  ];

  return (
    <div className="cart-page">
      <h2>Cart</h2>
      <Table
        dataSource={cartItems}
        columns={columns}
        rowKey="id"
        pagination={false}
      />
      <div className="cart-summary">
        <div className="shipping-section">
          <Input
            value={zipCode}
            onChange={(e) => setZipCode(e.target.value)}
            placeholder="Enter zip code"
          />
          <Button onClick={handleCalculateShipping}>Calculate Shipping</Button>
        </div>
        {shippingCost && <p>Shipping Cost: {typeof shippingCost === 'string' ? shippingCost : `$${shippingCost}`}</p>}
        <h3>Total Cost: ${totalCost}</h3>
        <Button type="primary" onClick={handleCheckout}>Checkout</Button>
      </div>

      <Modal
        title="Checkout"
        visible={isModalVisible}
        onCancel={() => setIsModalVisible(false)}
        footer={[
          <Button key="back" onClick={() => setIsModalVisible(false)}>
            Cancel
          </Button>,
          <Button key="submit" type="primary" onClick={handlePay}>
            Pay
          </Button>,
        ]}
      >
        <Input
          value={cardNumber}
          onChange={(e) => setCardNumber(formatCardNumber(e.target.value))}
          placeholder="Credit Card Number"
          maxLength={19}
          style={{ marginBottom: '10px' }}
        />
        <div className="expiry-cvc">
          <Input
            value={expiry}
            onChange={(e) => setExpiry(e.target.value)}
            placeholder="Expiry Date (MM/YY)"
            style={{ marginBottom: '10px', marginRight: '10px' }}
          />
          <Input
            value={cvc}
            onChange={(e) => setCvc(e.target.value)}
            placeholder="CVC"
            style={{ marginBottom: '10px' }}
          />
        </div>
      </Modal>
    </div>
  );
};

export default Cart;