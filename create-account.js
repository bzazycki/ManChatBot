// create-account.js

// Wait for DOM to be fully loaded
document.addEventListener('DOMContentLoaded', function() {
    // Get form and key container elements
    const createAccountForm = document.getElementById('createAccountForm');
    const productKeyContainer = document.getElementById('productKeyContainer');
    const productKeyBoxes = document.querySelectorAll('.product-key-box');
    const submitProductKeyButton = document.getElementById('submitProductKey');

    // API endpoints
    const API_BASE_URL = 'http://127.0.0.1:5000';
    const API_ENDPOINTS = {
        CHECK_KEY: `${API_BASE_URL}/checkRegKey`,
        REGISTER: `${API_BASE_URL}/register`
    };

    // Store form data for later use
    let formData = {};

    // Handle initial form submission
    createAccountForm.addEventListener('submit', handleFormSubmit);

    // Set up product key input handlers
    setupProductKeyInputs();

    // Handle product key submission
    submitProductKeyButton.addEventListener('click', handleProductKeySubmit);

    // Form submission handler
    async function handleFormSubmit(event) {
        event.preventDefault();

        // Get form values
        const username = document.getElementById('username').value;
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;
        const confirmPassword = document.getElementById('confirmPassword').value;

        // Validate passwords match
        if (password !== confirmPassword) {
            showError('Passwords do not match!');
            return;
        }

        // Store form data for later use
        formData = {
            username,
            email,
            password
        };

        // Show product key input
        productKeyContainer.classList.remove('hidden');
    }

    // Set up product key input behavior
    function setupProductKeyInputs() {
        productKeyBoxes.forEach((box, index) => {
            box.addEventListener('input', (event) => {
                const value = event.target.value;

                // Move to next box if value entered
                if (value.length === 1 && index < productKeyBoxes.length - 1) {
                    productKeyBoxes[index + 1].focus();
                }
            });

            box.addEventListener('keydown', (event) => {
                // Handle backspace
                if (event.key === 'Backspace' && index > 0 && box.value === '') {
                    productKeyBoxes[index - 1].focus();
                }
            });
        });
    }

    // Handle product key submission
    async function handleProductKeySubmit() {
        const productKey = Array.from(productKeyBoxes)
            .map(box => box.value)
            .join('');

        if (productKey.length !== 8) {
            showError('Please fill all 8 boxes with valid digits.');
            return;
        }

        try {
            // First validate the registration key
            const isKeyValid = await validateRegistrationKey(productKey);
            
            if (!isKeyValid) {
                showError('Invalid registration key. Please try again.');
                return;
            }

            // If key is valid, create the account
            const success = await createAccount(formData);
            
            if (success) {
                showSuccess('Account created successfully!');
                // Redirect to home page or login page
                setTimeout(() => {
                    window.location.href = 'index.html';
                }, 1500);
            }
        } catch (error) {
            showError(`Error: ${error.message}`);
        }
    }

    // Validate registration key with backend
    async function validateRegistrationKey(key) {
        try {
            const response = await fetch(API_ENDPOINTS.CHECK_KEY, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ reg_key: key })
            });

            const data = await response.json();
            return data.valid;
        } catch (error) {
            throw new Error('Failed to validate registration key');
        }
    }

    // Create account with backend
    async function createAccount(userData) {
        try {
            const response = await fetch(API_ENDPOINTS.REGISTER, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(userData)
            });

            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.error || 'Failed to create account');
            }

            return true;
        } catch (error) {
            throw new Error(error.message || 'Failed to create account');
        }
    }

    // Helper function to show errors
    function showError(message) {
        alert(message); // You can replace this with a better UI notification system
    }

    // Helper function to show success messages
    function showSuccess(message) {
        alert(message); // You can replace this with a better UI notification system
    }
});