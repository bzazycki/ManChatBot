document.addEventListener('DOMContentLoaded', function() {
  const loginForm = document.querySelector('.admin-page form');
  
  loginForm.addEventListener('submit', function(event) {
    event.preventDefault();
    
    const username = document.querySelector('input[name="username"]').value;
    const password = document.querySelector('input[name="password"]').value;
    
    fetch('/validateUser', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ username, password })
    })
    .then(response => response.json())
    .then(data => {
      if (data.valid) {
        alert('Login successful');
        // Redirect to admin page or perform other actions
      } else {
        alert('Invalid username or password');
      }
    })
    .catch(error => {
      console.error('Error:', error);
    });
  });
});
