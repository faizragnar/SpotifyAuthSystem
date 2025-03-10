document.addEventListener("DOMContentLoaded", function () {
    const loginForm = document.getElementById("loginForm");
    const signupForm = document.getElementById("signupForm");
    const forgotPasswordModal = document.getElementById("forgotPasswordModal");
    const forgotPasswordLink = document.getElementById("forgotPasswordLink");
    const closeModal = document.querySelector(".close");
    const forgotPasswordForm = document.getElementById("forgotPasswordForm");

    if (loginForm) {
        loginForm.addEventListener("submit", async function (event) {
            event.preventDefault();
            const email = document.getElementById("loginEmail").value;
            const password = document.getElementById("loginPassword").value;

            try {
                const response = await fetch("http://localhost:8080/rig/auth/login", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({ email, password })
                });

                if (!response.ok) {
                    throw new Error("Invalid email or password");
                }

                const data = await response.json();
                sessionStorage.setItem("token", data.token); // Store JWT in sessionStorage
                alert("Login successful!");

                window.location.href = "spotifylog.html"; // ✅ Redirect to dashboard AFTER login
            } catch (error) {
                alert(error.message);
            }
        });
    }

    if (signupForm) {
        signupForm.addEventListener("submit", async function (event) {
            event.preventDefault();
            const firstName = document.getElementById("signupFirstName").value;
            const lastName = document.getElementById("signupLastName").value;
            const email = document.getElementById("signupEmail").value;
            const password = document.getElementById("signupPassword").value;

            try {
                const response = await fetch("http://localhost:8080/rig/auth/register", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({ firstName, lastName, email, password })
                });

                if (!response.ok) {
                    throw new Error("Signup failed. Email may already be registered.");
                }

                alert("Signup successful! Please log in.");
                window.location.href = "login.html"; // ✅ Redirect to login page AFTER signup
            } catch (error) {
                alert(error.message);
            }
        });
    }

    // 🔹 Only check authentication if not on login or signup pages
    if (!loginForm && !signupForm) {
        checkAuthentication();
    }
    if (forgotPasswordLink) {
        forgotPasswordLink.addEventListener("click", function () {
            forgotPasswordModal.style.display = "block";
        });
    }

    // 🔹 Close modal when 'X' is clicked
    if (closeModal) {
        closeModal.addEventListener("click", function () {
            forgotPasswordModal.style.display = "none";
        });
    }

    // 🔹 Close modal if user clicks outside
    window.addEventListener("click", function (event) {
        if (event.target === forgotPasswordModal) {
            forgotPasswordModal.style.display = "none";
        }
    });

    // 🔹 Forgot password form submission
    if (forgotPasswordForm) {
        forgotPasswordForm.addEventListener("submit", async function (event) {
            event.preventDefault();
            const email = document.getElementById("resetEmail").value;
            const currentPassword = document.getElementById("currentPassword").value;
            const newPassword = document.getElementById("newPassword").value;

            try {
                const response = await fetch(`http://localhost:8080/rig/auth/resetPassword/${email}`, {
                    method: "PUT",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ currentPassword, newPassword })
                });

                if (!response.ok) {
                    const data = await response.json();
                    throw new Error(data.message || "Failed to reset password.");
                }

                alert("Password reset successful! Please log in with your new password.");
                forgotPasswordModal.style.display = "none";
            } catch (error) {
                alert(error.message);
            }
        });
    }


    // 🔹 Display user info if logged in
    if (document.getElementById("username")) {
        displayUserInfo();
    }

    // 🔹 Logout functionality
    const logoutBtn = document.getElementById("logout");
    if (logoutBtn) {
        logoutBtn.addEventListener("click", function () {
            sessionStorage.removeItem("token"); // Clear JWT
            window.location.href = "spotify.html"; // Redirect to guest page
        });
    }
});

// 🔹 Function to Check Authentication and Redirect
function checkAuthentication() {
    const token = sessionStorage.getItem("token");

    if (token) {
        if (isTokenValid(token)) {
            if (window.location.pathname.includes("spotify.html")) {
                window.location.href = "spotifylog.html"; // ✅ Redirect only if on guest page
            }
        } else {
            sessionStorage.removeItem("token"); // Remove invalid token
            window.location.href = "spotify.html"; // Redirect to guest page
        }
    } else if (!window.location.pathname.includes("spotify.html") &&
        !window.location.pathname.includes("login.html") &&
        !window.location.pathname.includes("signup.html")) {
        window.location.href = "spotify.html"; // Redirect non-logged-in users (except login/signup)
    }
}

// 🔹 Function to Validate JWT Token Expiry
function isTokenValid(token) {
    try {
        const payload = JSON.parse(atob(token.split(".")[1]));
        const expiry = payload.exp * 1000; // Convert to milliseconds
        return expiry > Date.now(); // Check if token is expired
    } catch (error) {
        return false; // Invalid token
    }
}

// 🔹 Function to Display User Info
function displayUserInfo() {
    const token = sessionStorage.getItem("token");

    if (token) {
        try {
            const payload = JSON.parse(atob(token.split(".")[1]));

            console.log("Decoded JWT Payload:", payload); // Debugging Step

            // 🔹 Check different possible email fields
            const email = payload.email || payload.sub || payload.username || "Unknown User";

            const usernameElement = document.getElementById("username");
            if (usernameElement) {
                usernameElement.textContent = email;
            } else {
                console.error("⚠️ Username element not found in HTML!");
            }
        } catch (error) {
            console.error("❌ Error decoding token:", error);
        }
    } else {
        console.warn("⚠️ No token found in sessionStorage!");
    }
}
