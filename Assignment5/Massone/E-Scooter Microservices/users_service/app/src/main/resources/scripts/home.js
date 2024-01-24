function checkLoginStatus() {
    const emailCookie = document.cookie.split('; ').find(row => row.startsWith('email'));
    if (emailCookie) {
        $('#loginButton').hide();
        $('#logoutButton').show();
    } else {
        $('#loginButton').show();
        $('#logoutButton').hide();
    }
}

function logout() {
    $.ajax({
        url: '/users/logout',
        type: 'POST',
        success: function(response) {
            $('body').html(response);
        },
        error: function(error) {
            console.error('Logout failed:', error);
        }
    });
}

function goToRegistration() {
    $.ajax({
        url: '/users/register',
        type: 'GET',
        success: function(response) {
            $('body').html(response);
        },
        error: function(error) {
            console.error('Error:', error);
        }
    });
}

function goToLogin() {
    window.location.href = "/users/login";
}
function goToRidesDashboard() {
    window.location.href = "/rides/home";
}

$(document).ready(function() {
    checkLoginStatus();
    window.goToLogin = goToLogin;
    window.goToRegistration = goToRegistration;
    window.logout = logout;
    window.goToRidesDashboard = goToRidesDashboard;
});